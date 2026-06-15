package com.fizanyatik.sportsclub.Activity;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.fizanyatik.sportsclub.List.SimplePlayer;
import com.fizanyatik.sportsclub.R;
import com.fizanyatik.sportsclub.SupabaseStorageHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// ---------------------------------------------------------------------------
// Stats holder for each player
// ---------------------------------------------------------------------------
class PlayerStats {
    String uid;
    String name;
    int runsScored = 0, ballsFaced = 0;
    int fours = 0, sixes = 0;
    int runsConceded = 0, ballsBowled = 0, wicketsTaken = 0;
    int maidenOvers = 0;
    int currentOverRuns = 0; // track runs in current over for maiden detection
    // How many times this batter has been dismissed so far this innings
    int dismissalCount = 0;
    // True only when dismissalCount >= wicketsPerBatter (fully out)
    boolean isOut = false;

    public PlayerStats(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    /** e.g. "35* (20)  4s:2  6s:1" for not out */
    String getBattingScore() {
        return runsScored + (isOut ? "" : "*") + " (" + ballsFaced + ")";
    }

    String getBattingFull() {
        return getBattingScore() + "  4s:" + fours + "  6s:" + sixes
                + "  SR:" + getStrikeRate();
    }

    String getStrikeRate() {
        if (ballsFaced == 0) return "0.00";
        return String.format(Locale.getDefault(), "%.2f", (runsScored * 100.0f) / ballsFaced);
    }

    /** e.g. "2-18" */
    String getBowlingScore() {
        return wicketsTaken + "-" + runsConceded;
    }

    String getBowlingFull() {
        int overs = ballsBowled / 6;
        int balls = ballsBowled % 6;
        return wicketsTaken + "-" + runsConceded + " (" + overs + "." + balls + ")";
    }

    String getBowlingDetailed() {
        int overs = ballsBowled / 6;
        int balls = ballsBowled % 6;
        String econ = getEconomy();
        return overs + "." + balls + " ov  " + maidenOvers + "M  "
                + runsConceded + "R  " + wicketsTaken + "W  Econ:" + econ;
    }

    String getEconomy() {
        float totalOvers = ballsBowled / 6.0f;
        if (totalOvers == 0) return "0.00";
        return String.format(Locale.getDefault(), "%.2f", runsConceded / totalOvers);
    }
}

// ---------------------------------------------------------------------------
// BallEntry — one delivery record stored in SharedPreferences (as JSON)
// ---------------------------------------------------------------------------
class BallEntry {
    int innings;      // 1 or 2
    int overNum;      // 0-indexed over
    int ballInOver;   // 1..6
    String batterName;
    String bowlerName;
    int runs;
    boolean isExtra;
    boolean isWicket;
    boolean isLifeLost;  // batter lost a life but stayed
    String teamScore;    // e.g. "45/3"

    JSONObject toJson() {
        try {
            JSONObject o = new JSONObject();
            o.put("innings", innings);
            o.put("over", overNum);
            o.put("ball", ballInOver);
            o.put("batter", batterName);
            o.put("bowler", bowlerName);
            o.put("runs", runs);
            o.put("extra", isExtra);
            o.put("wicket", isWicket);
            o.put("lifeLost", isLifeLost);
            o.put("score", teamScore);
            return o;
        } catch (Exception e) {
            return new JSONObject();
        }
    }
}

// ---------------------------------------------------------------------------
// ScoringActivity
// ---------------------------------------------------------------------------
public class ScoringActivity extends AppCompatActivity {

    // SharedPreferences key for ball-by-ball data
    private static final String PREFS_NAME  = "fsc_scorecard";
    private static final String KEY_BALLS   = "ball_by_ball";

    // ---- Setup Data ----
    String matchName, seriesName;
    int totalOvers, totalWickets, wicketsPerBatter;
    ArrayList<SimplePlayer> nscPlayers, sbrPlayers;

    // ---- State ----
    boolean isFirstInnings = true;
    int currentRuns = 0, currentWickets = 0, currentBalls = 0, currentOvers = 0;
    int target = -1;
    String onStrikeBatsmanId = null, offStrikeBatsmanId = null, currentBowlerId = null;

    // Which team bats / bowls this innings
    ArrayList<SimplePlayer> battingTeam, bowlingTeam;
    String battingTeamName, bowlingTeamName;

    // Player stats (filled for all players in the match)
    HashMap<String, PlayerStats> statsMap = new HashMap<>();

    // Saved innings strings for final save
    String firstInningsScoreStr = "", secondInningsScoreStr = "";

    // Whether innings end has been triggered (guard against double-trigger)
    boolean inningsEndTriggered = false;

    // Ball-by-ball list accumulated during the match
    List<BallEntry> ballLog = new ArrayList<>();

    // Current-over ball labels e.g. ["0","1","4","W","Wd","6"]
    List<String> thisOverBalls = new ArrayList<>();

    // ---- Views ----
    TextView teamNameTv, scoreTv, oversTv, targetTv;
    CardView targetCv;

    // On-strike batter table views
    ImageView onStrikeAvatarIv;
    TextView  onStrikeNameTv, onStrikeRunsTv, onStrikeBallsTv, onStrikeFoursTv, onStrikeSixesTv, onStrikeSrTv;

    // Off-strike batter table views
    ImageView offStrikeAvatarIv;
    TextView  offStrikeNameTv, offStrikeRunsTv, offStrikeBallsTv, offStrikeFoursTv, offStrikeSixesTv, offStrikeSrTv;

    // Bowler table views
    ImageView bowlerAvatarIv;
    TextView  bowlerNameTv, bowlerOversTv, bowlerMaidensTv, bowlerRunsTv, bowlerWicketsTv, bowlerEcoTv;

    // This Over chip container
    android.widget.LinearLayout thisOverContainer;

    androidx.cardview.widget.CardView btnRun0, btnRun1, btnRun2, btnRun3, btnRun4, btnRun6,
            btnWide, btnNoBall, btnWicket, btnChangeStrike, btnEndInnings;

    // ---- Firebase ----
    DatabaseReference matchRef;
    DatabaseReference liveMatchRef; // for live scoring updates
    String liveMatchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.content.SharedPreferences prefs = getSharedPreferences("Themes", MODE_PRIVATE);
        String themeMode = prefs.getString("current", "");
        switch (themeMode){
            case "":
            case "Main":
                setTheme(R.style.Theme_Main);
                break;
            case "Blue":
                setTheme(R.style.Theme_Blue);
                break;
            case "Yellow":
                setTheme(R.style.Theme_Yellow);
                break;
            case "Pink":
                setTheme(R.style.Theme_Pink);
                break;
            case "Green":
                setTheme(R.style.Theme_Green);
                break;
            case "Teal":
                setTheme(R.style.Theme_Teal);
                break;
            case "Purple":
                setTheme(R.style.Theme_Purple);
                break;
            case "Red":
                setTheme(R.style.Theme_Red);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring);

        // ---- Read Intent extras ----
        // Check if we are RESUMING an existing live match
        String resumeKey = getIntent().getStringExtra("RESUME_KEY");
        if (resumeKey != null && !resumeKey.isEmpty()) {
            // We are resuming — set up Firebase refs then load state from LiveMatch node
            matchRef     = FirebaseDatabase.getInstance().getReference("Match");
            liveMatchRef = FirebaseDatabase.getInstance().getReference("LiveMatch");
            liveMatchKey = resumeKey;

            // Bind views first (needed before state is applied)
            initViews();
            initListeners();
            setButtonsEnabled(false);

            Toast.makeText(this, "Resuming match…", Toast.LENGTH_SHORT).show();

            liveMatchRef.child(liveMatchKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(ScoringActivity.this,
                                "Live match data not found.", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                    try {
                        // ---- Restore match config ----
                        matchName        = getStr(snapshot, "matchName");
                        seriesName       = getStr(snapshot, "seriesName");
                        totalOvers       = getInt(snapshot, "totalOvers", 10);
                        totalWickets     = getInt(snapshot, "totalWickets", 5);
                        wicketsPerBatter = getInt(snapshot, "wicketsPerBatter", 1);

                        // ---- Restore player lists from JSON ----
                        nscPlayers = deserializePlayers(getStr(snapshot, "nscPlayersJson"));
                        sbrPlayers = deserializePlayers(getStr(snapshot, "sbrPlayersJson"));

                        if (nscPlayers == null || nscPlayers.isEmpty()
                                || sbrPlayers == null || sbrPlayers.isEmpty()) {
                            Toast.makeText(ScoringActivity.this,
                                    "Player data corrupted.", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                        // ---- Restore innings state ----
                        isFirstInnings       = getBool(snapshot, "isFirstInnings", true);
                        battingTeamName      = getStr(snapshot, "battingTeamName");
                        bowlingTeamName      = getStr(snapshot, "bowlingTeamName");
                        currentRuns          = getInt(snapshot, "runs", 0);
                        currentWickets       = getInt(snapshot, "wickets", 0);
                        currentOvers         = getInt(snapshot, "currentOvers", 0);
                        currentBalls         = getInt(snapshot, "currentBalls", 0);
                        target               = getInt(snapshot, "target", -1);
                        inningsEndTriggered  = getBool(snapshot, "inningsEndTriggered", false);
                        firstInningsScoreStr = getStr(snapshot, "firstInningsScoreStr");
                        onStrikeBatsmanId    = getStr(snapshot, "onStrikeBatsmanId");
                        offStrikeBatsmanId   = getStr(snapshot, "offStrikeBatsmanId");
                        currentBowlerId      = getStr(snapshot, "currentBowlerId");

                        // Resolve batting / bowling team lists
                        battingTeam  = battingTeamName.equals("NSC") ? nscPlayers : sbrPlayers;
                        bowlingTeam  = battingTeamName.equals("NSC") ? sbrPlayers : nscPlayers;

                        // ---- Restore per-player stats ----
                        for (SimplePlayer p : nscPlayers)
                            statsMap.put(p.getUid(), new PlayerStats(p.getUid(), p.getName()));
                        for (SimplePlayer p : sbrPlayers)
                            statsMap.put(p.getUid(), new PlayerStats(p.getUid(), p.getName()));

                        String statsJson = getStr(snapshot, "statsJson");
                        if (!statsJson.isEmpty()) {
                            try {
                                JSONObject statsObj = new JSONObject(statsJson);
                                for (Map.Entry<String, PlayerStats> e : statsMap.entrySet()) {
                                    if (statsObj.has(e.getKey())) {
                                        JSONObject ps = statsObj.getJSONObject(e.getKey());
                                        PlayerStats st = e.getValue();
                                        st.runsScored      = ps.optInt("runsScored", 0);
                                        st.ballsFaced      = ps.optInt("ballsFaced", 0);
                                        st.fours           = ps.optInt("fours", 0);
                                        st.sixes           = ps.optInt("sixes", 0);
                                        st.runsConceded    = ps.optInt("runsConceded", 0);
                                        st.ballsBowled     = ps.optInt("ballsBowled", 0);
                                        st.wicketsTaken    = ps.optInt("wicketsTaken", 0);
                                        st.maidenOvers     = ps.optInt("maidenOvers", 0);
                                        st.currentOverRuns = ps.optInt("currentOverRuns", 0);
                                        st.dismissalCount  = ps.optInt("dismissalCount", 0);
                                        st.isOut           = ps.optBoolean("isOut", false);
                                    }
                                }
                            } catch (Exception ignored) {}
                        }

                        // ---- Restore ball log ----
                        String ballLogJson = getStr(snapshot, "ballLogJson");
                        if (!ballLogJson.isEmpty()) {
                            try {
                                JSONArray arr = new JSONArray(ballLogJson);
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject o = arr.getJSONObject(i);
                                    BallEntry be = new BallEntry();
                                    be.innings    = o.optInt("innings", 1);
                                    be.overNum    = o.optInt("over", 0);
                                    be.ballInOver = o.optInt("ball", 0);
                                    be.batterName = o.optString("batter", "");
                                    be.bowlerName = o.optString("bowler", "");
                                    be.runs       = o.optInt("runs", 0);
                                    be.isExtra    = o.optBoolean("extra", false);
                                    be.isWicket   = o.optBoolean("wicket", false);
                                    be.isLifeLost = o.optBoolean("lifeLost", false);
                                    be.teamScore  = o.optString("score", "");
                                    ballLog.add(be);
                                }
                            } catch (Exception ignored) {}
                        }

                        // ---- Restore this-over ball chips ----
                        String thisOverJson = getStr(snapshot, "thisOverJson");
                        if (!thisOverJson.isEmpty()) {
                            try {
                                JSONArray arr = new JSONArray(thisOverJson);
                                for (int i = 0; i < arr.length(); i++) thisOverBalls.add(arr.getString(i));
                            } catch (Exception ignored) {}
                        }

                        // ---- Set toolbar title ----
                        Toolbar toolbar = findViewById(R.id.scoring_toolbar);
                        if (toolbar != null) {
                            setSupportActionBar(toolbar);
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                getSupportActionBar().setTitle(matchName);
                            }
                        }

                        // ---- Show target banner if second innings ----
                        if (!isFirstInnings && target > 0) {
                            targetCv.setVisibility(View.VISIBLE);
                        } else {
                            targetCv.setVisibility(View.GONE);
                        }

                        teamNameTv.setText("Batting: " + battingTeamName + "  |  Bowling: " + bowlingTeamName);
                        updateUI();

                        // Re-enable scoring buttons only if we have active players
                        if (onStrikeBatsmanId != null && currentBowlerId != null) {
                            setButtonsEnabled(true);
                        } else {
                            // Something is missing — ask user to re-pick
                            promptForOpeningPlayers();
                        }

                        Toast.makeText(ScoringActivity.this, "Match resumed!", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Toast.makeText(ScoringActivity.this,
                                "Failed to restore match: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ScoringActivity.this,
                            "Error loading match: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            });
            return; // Don't run the normal new-match path
        }

        // ---- Normal new-match path ----
        if (getIntent().getExtras() == null) {
            Toast.makeText(this, "Error: No match data.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        matchName          = getIntent().getStringExtra("MATCH_NAME");
        seriesName         = getIntent().getStringExtra("SERIES_NAME");
        totalOvers         = getIntent().getIntExtra("TOTAL_OVERS", 10);
        totalWickets       = getIntent().getIntExtra("TOTAL_WICKETS", 5);
        wicketsPerBatter   = getIntent().getIntExtra("WICKETS_PER_BATTER", 1);
        nscPlayers         = (ArrayList<SimplePlayer>) getIntent().getSerializableExtra("NSC_PLAYERS");
        sbrPlayers         = (ArrayList<SimplePlayer>) getIntent().getSerializableExtra("SBR_PLAYERS");

        if (nscPlayers == null || sbrPlayers == null || nscPlayers.isEmpty() || sbrPlayers.isEmpty()) {
            Toast.makeText(this, "Player data is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ---- Clear any old scorecard data from a previous match ----
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().remove(KEY_BALLS).apply();

        // ---- Firebase refs ----
        matchRef = FirebaseDatabase.getInstance().getReference("Match");
        // Create a live-scoring node under "LiveMatch"
        liveMatchRef = FirebaseDatabase.getInstance().getReference("LiveMatch");
        liveMatchKey = liveMatchRef.push().getKey();

        // ---- Toolbar ----
        Toolbar toolbar = findViewById(R.id.scoring_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setTitle(matchName);
            }
        }

        // ---- Bind views ----
        initViews();
        initListeners();

        // ---- Build stats map for every player in this match ----
        for (SimplePlayer p : nscPlayers) statsMap.put(p.getUid(), new PlayerStats(p.getUid(), p.getName()));
        for (SimplePlayer p : sbrPlayers)  statsMap.put(p.getUid(), new PlayerStats(p.getUid(), p.getName()));

        // ---- Ask toss first ----
        promptToss();
    }


    // -----------------------------------------------------------------------
    // View binding
    // -----------------------------------------------------------------------
    private void initViews() {
        teamNameTv        = findViewById(R.id.team_name_tv);
        scoreTv           = findViewById(R.id.score_tv);
        oversTv           = findViewById(R.id.overs_tv);
        targetTv          = findViewById(R.id.target_tv);
        targetCv          = findViewById(R.id.target_cv);

        // On-strike batter table
        onStrikeAvatarIv = findViewById(R.id.on_strike_avatar);
        onStrikeNameTv   = findViewById(R.id.on_strike_name_tv);
        onStrikeRunsTv   = findViewById(R.id.on_strike_runs_tv);
        onStrikeBallsTv  = findViewById(R.id.on_strike_balls_tv);
        onStrikeFoursTv  = findViewById(R.id.on_strike_fours_tv);
        onStrikeSixesTv  = findViewById(R.id.on_strike_sixes_tv);
        onStrikeSrTv     = findViewById(R.id.on_strike_sr_tv);

        // Off-strike batter table
        offStrikeAvatarIv = findViewById(R.id.off_strike_avatar);
        offStrikeNameTv   = findViewById(R.id.off_strike_name_tv);
        offStrikeRunsTv   = findViewById(R.id.off_strike_runs_tv);
        offStrikeBallsTv  = findViewById(R.id.off_strike_balls_tv);
        offStrikeFoursTv  = findViewById(R.id.off_strike_fours_tv);
        offStrikeSixesTv  = findViewById(R.id.off_strike_sixes_tv);
        offStrikeSrTv     = findViewById(R.id.off_strike_sr_tv);

        // Bowler table
        bowlerAvatarIv  = findViewById(R.id.bowler_avatar);
        bowlerNameTv    = findViewById(R.id.bowler_name_tv);
        bowlerOversTv   = findViewById(R.id.bowler_overs_tv);
        bowlerMaidensTv = findViewById(R.id.bowler_maidens_tv);
        bowlerRunsTv    = findViewById(R.id.bowler_runs_tv);
        bowlerWicketsTv = findViewById(R.id.bowler_wickets_tv);
        bowlerEcoTv     = findViewById(R.id.bowler_eco_tv);

        thisOverContainer = findViewById(R.id.this_over_balls_container);

        btnRun0          = findViewById(R.id.btn_run_0);
        btnRun1          = findViewById(R.id.btn_run_1);
        btnRun2          = findViewById(R.id.btn_run_2);
        btnRun3          = findViewById(R.id.btn_run_3);
        btnRun4          = findViewById(R.id.btn_run_4);
        btnRun6          = findViewById(R.id.btn_run_6);
        btnWide          = findViewById(R.id.btn_wide);
        btnNoBall        = findViewById(R.id.btn_no_ball);
        btnWicket        = findViewById(R.id.btn_wicket);
        btnChangeStrike  = findViewById(R.id.btn_change_strike);
        btnEndInnings    = findViewById(R.id.btn_end_innings);
    }

    // -----------------------------------------------------------------------
    // Click listeners
    // -----------------------------------------------------------------------
    private void initListeners() {
        btnRun0.setOnClickListener(v -> addBall(0, false, false));
        btnRun1.setOnClickListener(v -> addBall(1, false, false));
        btnRun2.setOnClickListener(v -> addBall(2, false, false));
        btnRun3.setOnClickListener(v -> addBall(3, false, false));
        btnRun4.setOnClickListener(v -> addBall(4, false, false));
        btnRun6.setOnClickListener(v -> addBall(6, false, false));

        // Wide: 1 extra run, ball NOT counted, no boundary for batter
        btnWide.setOnClickListener(v -> addBall(1, true, false));

        // No-ball: 1 extra run, ball NOT counted, batter still faces
        btnNoBall.setOnClickListener(v -> addBall(1, true, false));

        btnWicket.setOnClickListener(v -> {
            if (isInningsOver()) return;
            addBall(0, false, true);
        });

        btnChangeStrike.setOnClickListener(v -> {
            swapStrike();
            updateUI();
            Toast.makeText(this, "Strike changed", Toast.LENGTH_SHORT).show();
        });

        btnEndInnings.setOnClickListener(v -> {
            if (!inningsEndTriggered) {
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                        .setTitle("End Innings?")
                        .setMessage("Are you sure you want to end the current innings?")
                        .setPositiveButton("Yes, End", (d, w) -> endInnings())
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    // -----------------------------------------------------------------------
    // Toss dialog
    // -----------------------------------------------------------------------
    private void promptToss() {
        String[] options = {"NSC bats first", "SBR bats first"};
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Toss — Who bats first?")
                .setItems(options, (dialog, which) -> {
                    boolean nscBatsFirst = (which == 0);
                    startInnings(true, nscBatsFirst);
                })
                .setCancelable(false)
                .show();
    }

    // -----------------------------------------------------------------------
    // Start an innings
    // -----------------------------------------------------------------------
    private void startInnings(boolean isFirst, boolean nscBatsFirst) {
        isFirstInnings = isFirst;
        inningsEndTriggered = false;
        currentRuns = 0;
        currentWickets = 0;
        currentBalls = 0;
        currentOvers = 0;
        onStrikeBatsmanId = null;
        offStrikeBatsmanId = null;
        currentBowlerId = null;
        thisOverBalls.clear();

        if (isFirstInnings) {
            if (nscBatsFirst) {
                battingTeam = nscPlayers;
                bowlingTeam = sbrPlayers;
                battingTeamName = "NSC";
                bowlingTeamName = "SBR";
            } else {
                battingTeam = sbrPlayers;
                bowlingTeam = nscPlayers;
                battingTeamName = "SBR";
                bowlingTeamName = "NSC";
            }
            targetCv.setVisibility(View.GONE);
        } else {
            // Second innings: batting and bowling teams are swapped
            ArrayList<SimplePlayer> tmp = battingTeam;
            battingTeam = bowlingTeam;
            bowlingTeam = tmp;
            String tmpName = battingTeamName;
            battingTeamName = bowlingTeamName;
            bowlingTeamName = tmpName;

            targetTv.setText(battingTeamName + " need " + (target-currentRuns)  + " in " + (totalOvers*6-currentOvers*6-currentBalls) + " balls.");
            targetCv.setVisibility(View.VISIBLE);
        }

        teamNameTv.setText("Batting: " + battingTeamName + "  |  Bowling: " + bowlingTeamName);
        setButtonsEnabled(false); // disable until players selected

        promptForOpeningPlayers();
    }

    // -----------------------------------------------------------------------
    // Core ball processing
    // -----------------------------------------------------------------------
    private boolean isInningsOver() {
        return currentOvers >= totalOvers;
    }

    private void addBall(int runs, boolean isExtra, boolean isWicket) {
        if (isInningsOver()) {
            Toast.makeText(this, "Innings is already over.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (onStrikeBatsmanId == null || currentBowlerId == null) {
            Toast.makeText(this, "Please select batsmen and bowler first.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentRuns += runs;

        PlayerStats batter = statsMap.get(onStrikeBatsmanId);
        PlayerStats bowler = statsMap.get(currentBowlerId);

        if (bowler != null) {
            bowler.runsConceded += runs;
            bowler.currentOverRuns += runs;
        }

        // Track whether this ball caused a "life lost" (but batter stays)
        boolean isLifeLost = false;

        if (!isExtra) {
            currentBalls++;
            if (bowler != null) bowler.ballsBowled++;
            if (batter != null) {
                batter.runsScored += runs;
                batter.ballsFaced++;
                if (runs == 4) batter.fours++;
                if (runs == 6) batter.sixes++;
            }
            // Odd runs swap strike
            if (runs % 2 == 1) swapStrike();
        }

        if (isWicket) {
            if (bowler != null) bowler.wicketsTaken++;
            if (batter != null) {
                batter.dismissalCount++;

                currentWickets++;

                if (batter.dismissalCount >= wicketsPerBatter) {
                    batter.isOut = true;
                    onStrikeBatsmanId = null;
                } else {
                    isLifeLost = true;
                    int livesLeft = wicketsPerBatter - batter.dismissalCount;
                    Toast.makeText(this,
                            batter.name + " has " + livesLeft
                                    + " wickets" + (livesLeft == 1 ? "" : "s") + " remaining",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        // ---- Record this ball in our log ----
        BallEntry entry = new BallEntry();
        entry.innings     = isFirstInnings ? 1 : 2;
        entry.overNum     = currentOvers;
        entry.ballInOver  = currentBalls;
        entry.batterName  = batter != null ? batter.name : "?";
        entry.bowlerName  = bowler != null ? bowler.name : "?";
        entry.runs        = runs;
        entry.isExtra     = isExtra;
        entry.isWicket    = isWicket && !isLifeLost;
        entry.isLifeLost  = isLifeLost;
        entry.teamScore   = currentRuns + "/" + currentWickets;
        ballLog.add(entry);
        persistBallLog();

        // ---- Append ball label to This Over ----
        String chipLabel;
        if (isWicket && !isLifeLost) chipLabel = "W";
        else if (isLifeLost)         chipLabel = "W" + runs;
        else if (isExtra)            chipLabel = (runs > 0 ? runs + "" : "") + (entry.isExtra ? "Wd" : "Nb");
        else if (runs == 4)          chipLabel = "4";
        else if (runs == 6)          chipLabel = "6";
        else                         chipLabel = String.valueOf(runs);
        thisOverBalls.add(chipLabel);

        // End of over
        if (!isExtra && currentBalls == 6) {
            // Check for maiden over
            if (bowler != null && bowler.currentOverRuns == 0) {
                bowler.maidenOvers++;
            }
            if (bowler != null) bowler.currentOverRuns = 0;

            currentBalls = 0;
            currentOvers++;
            swapStrike();
            thisOverBalls.clear();
        }

        updateUI();
        saveLiveScore();

        // Chase complete?
        if (!isFirstInnings && target > 0 && currentRuns >= target) {
            endInnings();
            return;
        }

        // Check innings end
        if (isInningsOver()) {
            endInnings();
            return;
        }

        if (isWicket && onStrikeBatsmanId == null) {
            promptForNextBatsman();
        }

        if (!isExtra && currentBalls == 0 && currentOvers > 0 && currentOvers < totalOvers) {
            promptForNextBowler();
        }
    }

    private void swapStrike() {
        if (offStrikeBatsmanId == null) return;
        String tmp = onStrikeBatsmanId;
        onStrikeBatsmanId = offStrikeBatsmanId;
        offStrikeBatsmanId = tmp;
    }

    // -----------------------------------------------------------------------
    // Helper: find a SimplePlayer by UID across both teams
    // -----------------------------------------------------------------------
    private SimplePlayer findPlayerByUid(String uid) {
        if (uid == null) return null;
        for (SimplePlayer p : nscPlayers) if (p.getUid().equals(uid)) return p;
        for (SimplePlayer p : sbrPlayers)  if (p.getUid().equals(uid)) return p;
        return null;
    }

    // -----------------------------------------------------------------------
    // Helper: load a player photo into an ImageView using Glide
    // -----------------------------------------------------------------------
    private void loadAvatar(ImageView iv, SimplePlayer player) {
        if (player != null && player.getImageUrl() != null
                && !player.getImageUrl().isEmpty()
                && !player.getImageUrl().equals("default")) {
            Glide.with(this)
                    .load(Uri.parse(player.getImageUrl()))
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(iv);
        } else {
            iv.setImageResource(R.drawable.ic_baseline_person_24);
        }
    }

    // -----------------------------------------------------------------------
    // UI update
    // -----------------------------------------------------------------------
    private void updateUI() {
        scoreTv.setText(currentRuns + "-" + currentWickets);
        oversTv.setText("(" + currentOvers + "." + currentBalls + "/" + totalOvers + ")");
        if (!isFirstInnings) {
            int runsNeeded = target - currentRuns;
            int ballsLeft = totalOvers * 6 - (currentOvers * 6 + currentBalls);
            if (runsNeeded <= 0) {
                targetTv.setText(battingTeamName + " won the match!");
            } else {
                targetTv.setText(battingTeamName + " need " + runsNeeded + " in " + ballsLeft + " balls.");
            }
        }
        updateThisOver();

        // --- On-strike batter ---
        if (onStrikeBatsmanId != null) {
            PlayerStats s = statsMap.get(onStrikeBatsmanId);
            SimplePlayer p = findPlayerByUid(onStrikeBatsmanId);
            loadAvatar(onStrikeAvatarIv, p);
            onStrikeNameTv.setText((s != null ? s.name : "?") + " *");
            onStrikeRunsTv.setText(s != null ? String.valueOf(s.runsScored) : "0");
            onStrikeBallsTv.setText(s != null ? String.valueOf(s.ballsFaced) : "0");
            onStrikeFoursTv.setText(s != null ? String.valueOf(s.fours) : "0");
            onStrikeSixesTv.setText(s != null ? String.valueOf(s.sixes) : "0");
            onStrikeSrTv.setText(s != null ? s.getStrikeRate() : "0.00");
        } else {
            onStrikeAvatarIv.setImageResource(R.drawable.ic_baseline_person_24);
            onStrikeNameTv.setText("Select Batter");
            onStrikeRunsTv.setText("-"); onStrikeBallsTv.setText("-");
            onStrikeFoursTv.setText("-"); onStrikeSixesTv.setText("-"); onStrikeSrTv.setText("-");
        }

        // --- Off-strike batter ---
        if (offStrikeBatsmanId != null) {
            PlayerStats s = statsMap.get(offStrikeBatsmanId);
            SimplePlayer p = findPlayerByUid(offStrikeBatsmanId);
            loadAvatar(offStrikeAvatarIv, p);
            offStrikeNameTv.setText(s != null ? s.name : "?");
            offStrikeRunsTv.setText(s != null ? String.valueOf(s.runsScored) : "0");
            offStrikeBallsTv.setText(s != null ? String.valueOf(s.ballsFaced) : "0");
            offStrikeFoursTv.setText(s != null ? String.valueOf(s.fours) : "0");
            offStrikeSixesTv.setText(s != null ? String.valueOf(s.sixes) : "0");
            offStrikeSrTv.setText(s != null ? s.getStrikeRate() : "0.00");
        } else {
            offStrikeAvatarIv.setImageResource(R.drawable.ic_baseline_person_24);
            offStrikeNameTv.setText("—");
            offStrikeRunsTv.setText("-"); offStrikeBallsTv.setText("-");
            offStrikeFoursTv.setText("-"); offStrikeSixesTv.setText("-"); offStrikeSrTv.setText("-");
        }

        // --- Bowler ---
        if (currentBowlerId != null) {
            PlayerStats s = statsMap.get(currentBowlerId);
            SimplePlayer p = findPlayerByUid(currentBowlerId);
            loadAvatar(bowlerAvatarIv, p);
            int ov = s != null ? s.ballsBowled / 6 : 0;
            int bl = s != null ? s.ballsBowled % 6 : 0;
            bowlerNameTv.setText((s != null ? s.name : "?") + " *");
            bowlerOversTv.setText(ov + "." + bl);
            bowlerMaidensTv.setText(s != null ? String.valueOf(s.maidenOvers) : "0");
            bowlerRunsTv.setText(s != null ? String.valueOf(s.runsConceded) : "0");
            bowlerWicketsTv.setText(s != null ? String.valueOf(s.wicketsTaken) : "0");
            bowlerEcoTv.setText(s != null ? s.getEconomy() : "0.00");
        } else {
            bowlerAvatarIv.setImageResource(R.drawable.ic_baseline_person_24);
            bowlerNameTv.setText("select bowler");
            bowlerOversTv.setText("-"); bowlerMaidensTv.setText("-");
            bowlerRunsTv.setText("-"); bowlerWicketsTv.setText("-"); bowlerEcoTv.setText("-");
        }
    }

    // -----------------------------------------------------------------------
    // This Over chip renderer
    // -----------------------------------------------------------------------
    private void updateThisOver() {
        if (thisOverContainer == null) return;
        thisOverContainer.removeAllViews();

        int chipSize  = (int) (28 * getResources().getDisplayMetrics().density);
        int chipGap   = (int) (8  * getResources().getDisplayMetrics().density);
        int textSizePx= (int) (10 * getResources().getDisplayMetrics().density);

        for (String label : thisOverBalls) {
            android.widget.TextView chip = new android.widget.TextView(this);

            android.widget.LinearLayout.LayoutParams lp =
                    new android.widget.LinearLayout.LayoutParams(chipSize, chipSize);
            lp.setMarginEnd(chipGap);
            chip.setLayoutParams(lp);

            chip.setText(label);
            chip.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, textSizePx);
            chip.setGravity(android.view.Gravity.CENTER);
            chip.setTextColor(android.graphics.Color.WHITE);
            chip.setTypeface(null, android.graphics.Typeface.BOLD);

            // Pick background colour by ball type
            int bgColor;
            switch (label) {
                case "4":               bgColor = android.graphics.Color.parseColor("#2E7D32"); break; // dark green
                case "6":               bgColor = android.graphics.Color.parseColor("#006064"); break; // teal
                case "W":               bgColor = android.graphics.Color.parseColor("#B71C1C"); break; // red
                case "0":               bgColor = android.graphics.Color.parseColor("#616161"); break; // grey
                default:
                    if (label.contains("Wd") || label.contains("Nb"))
                        bgColor = android.graphics.Color.parseColor("#E65100");  // amber
                    else if (label.startsWith("W"))
                        bgColor = android.graphics.Color.parseColor("#B71C1C");  // red (life lost)
                    else
                        bgColor = android.graphics.Color.parseColor("#37474F");  // blue-grey for 1,2,3
                    break;
            }

            // Draw as a circle
            android.graphics.drawable.GradientDrawable circle = new android.graphics.drawable.GradientDrawable();
            circle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            circle.setColor(bgColor);
            chip.setBackground(circle);

            thisOverContainer.addView(chip);
        }

        // Show placeholder dots for remaining balls in the over
        int ballsLeft = 6 - thisOverBalls.size();
        for (int i = 0; i < ballsLeft; i++) {
            android.widget.TextView dot = new android.widget.TextView(this);
            android.widget.LinearLayout.LayoutParams lp =
                    new android.widget.LinearLayout.LayoutParams(chipSize, chipSize);
            lp.setMarginEnd(chipGap);
            dot.setLayoutParams(lp);
            dot.setText("·");
            dot.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, textSizePx * 2);
            dot.setGravity(android.view.Gravity.CENTER);
            dot.setTextColor(android.graphics.Color.parseColor("#9E9E9E"));

            android.graphics.drawable.GradientDrawable ring = new android.graphics.drawable.GradientDrawable();
            ring.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            ring.setColor(android.graphics.Color.TRANSPARENT);
            ring.setStroke((int)(1.5f * getResources().getDisplayMetrics().density),
                    android.graphics.Color.parseColor("#9E9E9E"));
            dot.setBackground(ring);
            thisOverContainer.addView(dot);
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        btnRun0.setEnabled(enabled);
        btnRun1.setEnabled(enabled);
        btnRun2.setEnabled(enabled);
        btnRun3.setEnabled(enabled);
        btnRun4.setEnabled(enabled);
        btnRun6.setEnabled(enabled);
        btnWide.setEnabled(enabled);
        btnNoBall.setEnabled(enabled);
        btnWicket.setEnabled(enabled);
        btnChangeStrike.setEnabled(enabled);
    }

    // -----------------------------------------------------------------------
    // Opening players dialog
    // -----------------------------------------------------------------------
    private void promptForOpeningPlayers() {
        String[] names = getPlayerNames(battingTeam);
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Select Opening Batter (On Strike)")
                .setItems(names, (d, which) -> {
                    onStrikeBatsmanId = battingTeam.get(which).getUid();
                    updateUI();
                    String[] remaining = getRemainingBatterNames(onStrikeBatsmanId);
                    List<SimplePlayer> remainingList = getRemainingBatters(onStrikeBatsmanId);
                    new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                            .setTitle("Select Opening Batter (Off Strike)")
                            .setItems(remaining, (d2, which2) -> {
                                offStrikeBatsmanId = remainingList.get(which2).getUid();
                                updateUI();
                                promptForNextBowler();
                            })
                            .setCancelable(false)
                            .show();
                })
                .setCancelable(false)
                .show();
    }

    // -----------------------------------------------------------------------
    // Next batsman dialog
    // -----------------------------------------------------------------------
    private void promptForNextBatsman() {
        List<SimplePlayer> available = new ArrayList<>();
        for (SimplePlayer p : battingTeam) {
            PlayerStats s = statsMap.get(p.getUid());
            boolean notOut = (s != null && !s.isOut);
            boolean notOnField = !p.getUid().equals(offStrikeBatsmanId);
            if (notOut && notOnField) available.add(p);
        }

        if (available.isEmpty()) {
            if (offStrikeBatsmanId == null) {
                endInnings();
            } else {
                onStrikeBatsmanId = offStrikeBatsmanId;
                offStrikeBatsmanId = null;
                updateUI();
                setButtonsEnabled(true);
            }
            return;
        }

        String[] names = new String[available.size()];
        for (int i = 0; i < available.size(); i++) names[i] = available.get(i).getName();

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Select Next Batter")
                .setItems(names, (d, which) -> {
                    onStrikeBatsmanId = available.get(which).getUid();
                    updateUI();
                    setButtonsEnabled(true);
                })
                .setCancelable(false)
                .show();
    }

    // -----------------------------------------------------------------------
    // Next bowler dialog
    // -----------------------------------------------------------------------
    private void promptForNextBowler() {
        String[] names = new String[bowlingTeam.size()];
        for (int i = 0; i < bowlingTeam.size(); i++) {
            PlayerStats s = statsMap.get(bowlingTeam.get(i).getUid());
            names[i] = bowlingTeam.get(i).getName()
                    + (s != null ? "  (" + s.getBowlingFull() + ")" : "");
        }

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Select Bowler")
                .setItems(names, (d, which) -> {
                    currentBowlerId = bowlingTeam.get(which).getUid();
                    updateUI();
                    setButtonsEnabled(true);
                })
                .setCancelable(false)
                .show();
    }

    // -----------------------------------------------------------------------
    // End of innings
    // -----------------------------------------------------------------------
    private void endInnings() {
        if (inningsEndTriggered) return;
        inningsEndTriggered = true;
        setButtonsEnabled(false);

        if (isFirstInnings) {
            target = currentRuns + 1;
            firstInningsScoreStr = currentRuns + "/" + currentWickets
                    + " (" + currentOvers + "." + currentBalls + ")";

            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                    .setTitle("1st Innings Over")
                    .setMessage(battingTeamName + ": " + firstInningsScoreStr
                            + "\n\nTarget for " + bowlingTeamName + ": " + target)
                    .setPositiveButton("Start 2nd Innings", (d, w) -> startInnings(false, false))
                    .setCancelable(false)
                    .show();
        } else {
            secondInningsScoreStr = currentRuns + "/" + currentWickets
                    + " (" + currentOvers + "." + currentBalls + ")";
            showMatchResult();
        }
    }

    // -----------------------------------------------------------------------
    // Show result & pick top performers
    // -----------------------------------------------------------------------
    private void showMatchResult() {
        String result = calculateResult();

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Match Over!")
                .setMessage(result
                        + "\n\n1st Innings: " + (isFirstInnings ? battingTeamName : bowlingTeamName) + " — " + firstInningsScoreStr
                        + "\n2nd Innings: " + battingTeamName + " — " + secondInningsScoreStr)
                .setPositiveButton("Select Top Performers & Save", (d, w) -> pickTopPerformers(result))
                .setCancelable(false)
                .show();
    }

    private String calculateResult() {
        if (currentRuns >= target) {
            int wicketsLeft = totalWickets - currentWickets;
            return battingTeamName + " won by " + wicketsLeft + " wicket" + (wicketsLeft == 1 ? "" : "s");
        } else if (currentRuns < target - 1) {
            int margin = (target - 1) - currentRuns;
            return bowlingTeamName + " won by " + margin + " run" + (margin == 1 ? "" : "s");
        } else {
            return "Match Tied";
        }
    }

    // -----------------------------------------------------------------------
    // Top performer selection
    // -----------------------------------------------------------------------
    private SimplePlayer pickedNscBatter, pickedNscBowler, pickedSbrBatter, pickedSbrBowler;

    private void pickTopPerformers(String result) {
        pickPlayer("NSC Top Batter", nscPlayers, chosen -> {
            pickedNscBatter = chosen;
            pickPlayer("NSC Top Bowler", nscPlayers, chosen2 -> {
                pickedNscBowler = chosen2;
                pickPlayer("SBR Top Batter", sbrPlayers, chosen3 -> {
                    pickedSbrBatter = chosen3;
                    pickPlayer("SBR Top Bowler", sbrPlayers, chosen4 -> {
                        pickedSbrBowler = chosen4;
                        generateAndUploadScorecard(result);
                    });
                });
            });
        });
    }

    interface PlayerPickListener {
        void onPicked(SimplePlayer player);
    }

    private void pickPlayer(String title, List<SimplePlayer> pool, PlayerPickListener listener) {
        String[] names = new String[pool.size()];
        for (int i = 0; i < pool.size(); i++) {
            SimplePlayer p = pool.get(i);
            PlayerStats s = statsMap.get(p.getUid());
            String detail = "";
            if (s != null) detail = "  " + s.getBattingScore() + "  |  " + s.getBowlingFull();
            names[i] = p.getName() + detail;
        }
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setItems(names, (d, which) -> listener.onPicked(pool.get(which)))
                .setCancelable(false)
                .show();
    }

    // -----------------------------------------------------------------------
    // Ball-by-ball log
    // -----------------------------------------------------------------------
    private void persistBallLog() {
        try {
            JSONArray arr = new JSONArray();
            for (BallEntry e : ballLog) arr.put(e.toJson());
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(KEY_BALLS, arr.toString())
                    .apply();
        } catch (Exception ignored) {}
    }

    // -----------------------------------------------------------------------
    // PDF generation
    // -----------------------------------------------------------------------
    private byte[] generateScorecardPdf(String result) {
        PdfDocument doc = new PdfDocument();

        int pageWidth  = 595;
        int pageHeight = 842;
        int margin     = 36;
        int lineH      = 18;
        int yPos       = margin + 10;

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = doc.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint titlePaint = new Paint();
        titlePaint.setColor(Color.rgb(30, 30, 30));
        titlePaint.setTextSize(18f);
        titlePaint.setFakeBoldText(true);

        Paint headPaint = new Paint();
        headPaint.setColor(Color.rgb(50, 50, 200));
        headPaint.setTextSize(12f);
        headPaint.setFakeBoldText(true);

        Paint bodyPaint = new Paint();
        bodyPaint.setColor(Color.rgb(30, 30, 30));
        bodyPaint.setTextSize(10f);

        Paint subPaint = new Paint();
        subPaint.setColor(Color.rgb(100, 100, 100));
        subPaint.setTextSize(10f);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.rgb(200, 200, 200));
        linePaint.setStrokeWidth(0.5f);

        // ---- Title ----
        canvas.drawText(matchName + " • " + seriesName, margin, yPos, titlePaint);
        yPos += lineH + 4;

        String dateStr = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                .format(new Date());
        canvas.drawText("Date: " + dateStr, margin, yPos, subPaint);
        yPos += lineH;
        canvas.drawText("Result: " + result, margin, yPos, subPaint);
        yPos += lineH;
        canvas.drawText("1st Innings: " + firstInningsScoreStr
                + "   2nd Innings: " + secondInningsScoreStr, margin, yPos, subPaint);
        yPos += lineH + 4;

        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint);
        yPos += 6;

        // ---- Batting scorecards ----
        yPos = drawBattingCard(canvas, nscPlayers, "NSC", headPaint, bodyPaint,
                linePaint, margin, yPos, pageWidth, lineH);
        yPos += 8;
        yPos = drawBattingCard(canvas, sbrPlayers, "SBR", headPaint, bodyPaint,
                linePaint, margin, yPos, pageWidth, lineH);
        yPos += 8;

        // ---- Bowling scorecards ----
        yPos = drawBowlingCard(canvas, sbrPlayers, "SBR Bowling", headPaint, bodyPaint,
                linePaint, margin, yPos, pageWidth, lineH);
        yPos += 8;
        yPos = drawBowlingCard(canvas, nscPlayers, "NSC Bowling", headPaint, bodyPaint,
                linePaint, margin, yPos, pageWidth, lineH);
        yPos += 12;

        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint);
        yPos += 8;

        // ---- Ball-by-ball log ----
        if (yPos > pageHeight - 100) {
            doc.finishPage(page);
            PdfDocument.PageInfo pi2 =
                    new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create();
            page = doc.startPage(pi2);
            canvas = page.getCanvas();
            yPos = margin + 10;
        }

        canvas.drawText("Ball-by-Ball Scorecard", margin, yPos, headPaint);
        yPos += lineH;
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint);
        yPos += 6;

        String[] cols = {"Inn", "Over", "Batter", "Bowler", "Runs", "Extra", "W", "Score"};
        int[] colX    = {margin, margin+28, margin+56, margin+170, margin+285,
                         margin+310, margin+345, margin+375};
        for (int c = 0; c < cols.length; c++) {
            canvas.drawText(cols[c], colX[c], yPos, headPaint);
        }
        yPos += lineH;
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint);
        yPos += 4;

        int inningsNum = 0;
        for (BallEntry b : ballLog) {
            if (yPos > pageHeight - margin - lineH) {
                doc.finishPage(page);
                PdfDocument.PageInfo pi =
                        new PdfDocument.PageInfo.Builder(pageWidth, pageHeight,
                                doc.getPages().size() + 1).create();
                page = doc.startPage(pi);
                canvas = page.getCanvas();
                yPos = margin + 10;
                for (int c = 0; c < cols.length; c++) {
                    canvas.drawText(cols[c], colX[c], yPos, headPaint);
                }
                yPos += lineH;
                canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint);
                yPos += 4;
            }

            if (ballLog.indexOf(b) % 2 == 0) {
                Paint bgPaint = new Paint();
                bgPaint.setColor(Color.rgb(245, 245, 255));
                canvas.drawRect(margin, yPos - lineH + 4, pageWidth - margin, yPos + 4, bgPaint);
            }

            if (b.innings != inningsNum) {
                inningsNum = b.innings;
                Paint innPaint = new Paint();
                innPaint.setColor(Color.rgb(30, 100, 30));
                innPaint.setTextSize(10f);
                innPaint.setFakeBoldText(true);
                canvas.drawText("--- Innings " + inningsNum + " ---", margin, yPos, innPaint);
                yPos += lineH;
            }

            canvas.drawText(String.valueOf(b.innings),            colX[0], yPos, bodyPaint);
            canvas.drawText(b.overNum + "." + b.ballInOver,       colX[1], yPos, bodyPaint);
            canvas.drawText(truncate(b.batterName, 14),           colX[2], yPos, bodyPaint);
            canvas.drawText(truncate(b.bowlerName, 14),           colX[3], yPos, bodyPaint);
            canvas.drawText(String.valueOf(b.runs),               colX[4], yPos, bodyPaint);
            canvas.drawText(b.isExtra  ? "W" : "-",              colX[5], yPos, bodyPaint);
            canvas.drawText(b.isWicket ? "W" : (b.isLifeLost ? "L" : "-"), colX[6], yPos, bodyPaint);
            canvas.drawText(b.teamScore,                          colX[7], yPos, bodyPaint);
            yPos += lineH;
        }

        doc.finishPage(page);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            doc.writeTo(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        doc.close();
        return out.toByteArray();
    }

    // Batting card: Player | Runs(Balls) | 4s | 6s | SR
    private int drawBattingCard(Canvas canvas, List<SimplePlayer> team, String teamName,
                                Paint headPaint, Paint bodyPaint, Paint linePaint,
                                int margin, int yPos, int pageWidth, int lineH) {
        canvas.drawText(teamName + " Batting", margin, yPos, headPaint);
        yPos += lineH;
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint);
        yPos += 4;

        // Column headers
        canvas.drawText("Player",      margin,       yPos, headPaint);
        canvas.drawText("R(B)",        margin + 180, yPos, headPaint);
        canvas.drawText("4s",          margin + 280, yPos, headPaint);
        canvas.drawText("6s",          margin + 310, yPos, headPaint);
        canvas.drawText("SR",          margin + 345, yPos, headPaint);
        yPos += lineH;

        for (SimplePlayer p : team) {
            PlayerStats s = statsMap.get(p.getUid());
            if (s == null) continue;
            canvas.drawText(truncate(s.name, 25),           margin,       yPos, bodyPaint);
            canvas.drawText(s.getBattingScore(),             margin + 180, yPos, bodyPaint);
            canvas.drawText(String.valueOf(s.fours),         margin + 280, yPos, bodyPaint);
            canvas.drawText(String.valueOf(s.sixes),         margin + 310, yPos, bodyPaint);
            canvas.drawText(s.getStrikeRate(),               margin + 345, yPos, bodyPaint);
            yPos += lineH;
        }
        return yPos;
    }

    // Bowling card: Bowler | Overs | Maidens | Runs | Wickets | Economy
    private int drawBowlingCard(Canvas canvas, List<SimplePlayer> team, String label,
                                Paint headPaint, Paint bodyPaint, Paint linePaint,
                                int margin, int yPos, int pageWidth, int lineH) {
        canvas.drawText(label, margin, yPos, headPaint);
        yPos += lineH;
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint);
        yPos += 4;

        // Column headers
        canvas.drawText("Bowler",  margin,       yPos, headPaint);
        canvas.drawText("Ov",      margin + 170, yPos, headPaint);
        canvas.drawText("M",       margin + 210, yPos, headPaint);
        canvas.drawText("R",       margin + 240, yPos, headPaint);
        canvas.drawText("W",       margin + 270, yPos, headPaint);
        canvas.drawText("Econ",    margin + 300, yPos, headPaint);
        yPos += lineH;

        for (SimplePlayer p : team) {
            PlayerStats s = statsMap.get(p.getUid());
            if (s == null || s.ballsBowled == 0) continue;
            int overs = s.ballsBowled / 6;
            int balls = s.ballsBowled % 6;
            canvas.drawText(truncate(s.name, 22),         margin,       yPos, bodyPaint);
            canvas.drawText(overs + "." + balls,          margin + 170, yPos, bodyPaint);
            canvas.drawText(String.valueOf(s.maidenOvers), margin + 210, yPos, bodyPaint);
            canvas.drawText(String.valueOf(s.runsConceded), margin + 240, yPos, bodyPaint);
            canvas.drawText(String.valueOf(s.wicketsTaken), margin + 270, yPos, bodyPaint);
            canvas.drawText(s.getEconomy(),               margin + 300, yPos, bodyPaint);
            yPos += lineH;
        }
        return yPos;
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() > maxLen ? s.substring(0, maxLen - 1) + "…" : s;
    }

    // -----------------------------------------------------------------------
    // Generate PDF → upload to Supabase → save match to Firebase
    // -----------------------------------------------------------------------
    private void generateAndUploadScorecard(String result) {
        Toast.makeText(this, "Generating scorecard PDF…", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            byte[] pdfBytes = generateScorecardPdf(result);
            String fileName = "scorecard_" + matchName.replaceAll("[^a-zA-Z0-9]", "_")
                    + "_" + System.currentTimeMillis() + ".pdf";

            SupabaseStorageHelper.uploadBytes(
                    "feeds", fileName, pdfBytes, "application/pdf",
                    new SupabaseStorageHelper.UploadCallback() {
                        @Override
                        public void onSuccess(String publicUrl) {
                            runOnUiThread(() -> {
                                Toast.makeText(ScoringActivity.this,
                                        "Scorecard uploaded ✅", Toast.LENGTH_SHORT).show();
                                saveMatchData(result, publicUrl);
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            runOnUiThread(() -> {
                                Toast.makeText(ScoringActivity.this,
                                        "PDF upload failed: " + error + "\nSaving without PDF…",
                                        Toast.LENGTH_LONG).show();
                                saveMatchData(result, "");
                            });
                        }
                    });
        }).start();
    }

    // -----------------------------------------------------------------------
    // Save match to Firebase
    // -----------------------------------------------------------------------
    private void saveMatchData(String result, String scorecardUrl) {
        String t1Name, t2Name, t1Score, t2Score;
        t2Name  = battingTeamName;
        t1Name  = bowlingTeamName;
        t2Score = secondInningsScoreStr;
        t1Score = firstInningsScoreStr;

        PlayerStats nscBatterStats = (pickedNscBatter != null) ? statsMap.get(pickedNscBatter.getUid()) : null;
        PlayerStats nscBowlerStats = (pickedNscBowler != null) ? statsMap.get(pickedNscBowler.getUid()) : null;
        PlayerStats sbrBatterStats = (pickedSbrBatter != null) ? statsMap.get(pickedSbrBatter.getUid()) : null;
        PlayerStats sbrBowlerStats = (pickedSbrBowler != null) ? statsMap.get(pickedSbrBowler.getUid()) : null;

        DatabaseReference newMatchRef = matchRef.push();
        String parentKey = newMatchRef.getKey();

        Map<String, Object> data = new HashMap<>();
        data.put("parent", parentKey);
        data.put("details", matchName + " • " + seriesName);
        data.put("result", result);
        data.put("scorecard", scorecardUrl);

        data.put("team1_name", t1Name);
        data.put("team1_score", t1Score);
        data.put("team2_name", t2Name);
        data.put("team2_score", t2Score);

        data.put("top_team1_image",  pickedNscBatter != null ? pickedNscBatter.getUid() : "");
        data.put("top_team1_name",   pickedNscBatter != null ? pickedNscBatter.getName() : "");
        data.put("top_team1_score",  nscBatterStats != null ? nscBatterStats.getBattingScore() : "0 (0)");
        data.put("top2_team1_image", pickedNscBowler != null ? pickedNscBowler.getUid() : "");
        data.put("top2_team1_name",  pickedNscBowler != null ? pickedNscBowler.getName() : "");
        data.put("top2_team1_score", nscBowlerStats != null ? nscBowlerStats.getBowlingFull() : "0-0");

        data.put("top_team2_image",  pickedSbrBatter != null ? pickedSbrBatter.getUid() : "");
        data.put("top_team2_name",   pickedSbrBatter != null ? pickedSbrBatter.getName() : "");
        data.put("top_team2_score",  sbrBatterStats != null ? sbrBatterStats.getBattingScore() : "0 (0)");
        data.put("top2_team2_image", pickedSbrBowler != null ? pickedSbrBowler.getUid() : "");
        data.put("top2_team2_name",  pickedSbrBowler != null ? pickedSbrBowler.getName() : "");
        data.put("top2_team2_score", sbrBowlerStats != null ? sbrBowlerStats.getBowlingFull() : "0-0");

        newMatchRef.setValue(data)
                .addOnSuccessListener(aVoid -> {
                    if (liveMatchKey != null) {
                        liveMatchRef.child(liveMatchKey).removeValue();
                    }
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().remove(KEY_BALLS).apply();
                    Toast.makeText(ScoringActivity.this, "Match saved successfully!", Toast.LENGTH_LONG).show();
                    updatePlayerStats();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ScoringActivity.this,
                                "Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // -----------------------------------------------------------------------
    // Update cumulative player stats in Firebase Profile nodes
    // -----------------------------------------------------------------------
    /**
     * For every player who appeared in this match (batted or bowled),
     * read their existing stats from Firebase, add this match's numbers,
     * and write the updated values back.
     *
     * Stats fields (mirroring PlayerActivity):
     *   stats_match   – total matches played
     *   stats_runs    – career runs scored
     *   stats_wicket  – career wickets taken
     *   stats_average – batting average  (career_runs / career_dismissals)
     *   stats_strike  – career strike rate (career_runs*100 / career_balls_faced)
     *   stats_economy – career economy    (career_runs_conceded / career_overs_bowled)
     */
    private void updatePlayerStats() {
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("Profile");

        for (Map.Entry<String, PlayerStats> entry : statsMap.entrySet()) {
            String uid = entry.getKey();
            PlayerStats ps = entry.getValue();

            // Only process players who actually participated (batted or bowled)
            boolean batted = ps.ballsFaced > 0 || ps.runsScored > 0;
            boolean bowled = ps.ballsBowled > 0;
            if (!batted && !bowled) continue;

            profileRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // ---- Read existing values (default to 0 if not yet set) ----
                    int prevMatches    = safeInt(snapshot, "stats_match");
                    int prevRuns       = safeInt(snapshot, "stats_runs");
                    int prevWickets    = safeInt(snapshot, "stats_wicket");
                    // We store helper fields to correctly recalculate averages.
                    // Career balls faced / dismissals / balls bowled / runs conceded.
                    int prevBallsFaced    = safeInt(snapshot, "stats_balls_faced");
                    int prevDismissals    = safeInt(snapshot, "stats_dismissals");
                    int prevBallsBowled   = safeInt(snapshot, "stats_balls_bowled");
                    int prevRunsConceded  = safeInt(snapshot, "stats_runs_conceded");

                    // ---- Add this match's contribution ----
                    int newMatches       = prevMatches + 1;
                    int newRuns          = prevRuns          + ps.runsScored;
                    int newWickets       = prevWickets       + ps.wicketsTaken;
                    int newBallsFaced    = prevBallsFaced    + ps.ballsFaced;
                    int newDismissals    = prevDismissals    + ps.dismissalCount;
                    int newBallsBowled   = prevBallsBowled   + ps.ballsBowled;
                    int newRunsConceded  = prevRunsConceded  + ps.runsConceded;

                    // ---- Recompute derived stats ----
                    // Batting average: runs per dismissal (show 0.00 if never dismissed)
                    String newAverage = newDismissals > 0
                            ? String.format(Locale.getDefault(), "%.2f",
                                    (float) newRuns / newDismissals)
                            : String.format(Locale.getDefault(), "%.2f", (float) newRuns);

                    // Strike rate: (runs / balls) * 100
                    String newStrike = newBallsFaced > 0
                            ? String.format(Locale.getDefault(), "%.2f",
                                    (newRuns * 100.0f) / newBallsFaced)
                            : "0.00";

                    // Economy: runs conceded per over (6 balls)
                    String newEconomy = newBallsBowled > 0
                            ? String.format(Locale.getDefault(), "%.2f",
                                    newRunsConceded / (newBallsBowled / 6.0f))
                            : "0.00";

                    // ---- Write back to Firebase ----
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("stats_match",         String.valueOf(newMatches));
                    updates.put("stats_runs",          String.valueOf(newRuns));
                    updates.put("stats_wicket",        String.valueOf(newWickets));
                    updates.put("stats_average",       newAverage);
                    updates.put("stats_strike",        newStrike);
                    updates.put("stats_economy",       newEconomy);
                    // Persist helper accumulator fields so future matches can update correctly
                    updates.put("stats_balls_faced",   String.valueOf(newBallsFaced));
                    updates.put("stats_dismissals",    String.valueOf(newDismissals));
                    updates.put("stats_balls_bowled",  String.valueOf(newBallsBowled));
                    updates.put("stats_runs_conceded", String.valueOf(newRunsConceded));

                    profileRef.child(uid).updateChildren(updates)
                            .addOnFailureListener(e ->
                                    android.util.Log.e("FSC_Stats",
                                            "Failed to update stats for " + uid + ": " + e.getMessage()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    android.util.Log.e("FSC_Stats", "Stats read cancelled for " + uid
                            + ": " + error.getMessage());
                }
            });
        }
    }

    /** Safely parse an integer stat field from a Firebase DataSnapshot (returns 0 if absent/invalid). */
    private int safeInt(DataSnapshot snapshot, String key) {
        try {
            Object val = snapshot.child(key).getValue();
            if (val == null) return 0;
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // -----------------------------------------------------------------------
    // Live score push
    // -----------------------------------------------------------------------
    private void saveLiveScore() {
        if (liveMatchKey == null) return;
        Map<String, Object> live = new HashMap<>();

        // ---- Live display fields (for MatchFragment card) ----
        live.put("matchName",    matchName);
        live.put("seriesName",   seriesName != null ? seriesName : "");
        live.put("battingTeam",  battingTeamName);
        live.put("bowlingTeam",  bowlingTeamName);
        live.put("runs",         currentRuns);
        live.put("wickets",      currentWickets);
        live.put("overs",        currentOvers + "." + currentBalls);
        live.put("target",       target > 0 ? target : 0);
        live.put("isFirstInnings", isFirstInnings);

        // ---- Full resume state ----
        live.put("totalOvers",          totalOvers);
        live.put("totalWickets",         totalWickets);
        live.put("wicketsPerBatter",     wicketsPerBatter);
        live.put("currentOvers",         currentOvers);
        live.put("currentBalls",         currentBalls);
        live.put("battingTeamName",      battingTeamName);
        live.put("bowlingTeamName",      bowlingTeamName);
        live.put("inningsEndTriggered",  inningsEndTriggered);
        live.put("firstInningsScoreStr", firstInningsScoreStr != null ? firstInningsScoreStr : "");
        live.put("onStrikeBatsmanId",    onStrikeBatsmanId  != null ? onStrikeBatsmanId  : "");
        live.put("offStrikeBatsmanId",   offStrikeBatsmanId != null ? offStrikeBatsmanId : "");
        live.put("currentBowlerId",      currentBowlerId    != null ? currentBowlerId    : "");

        // Serialize NSC + SBR player lists
        live.put("nscPlayersJson", serializePlayers(nscPlayers));
        live.put("sbrPlayersJson", serializePlayers(sbrPlayers));

        // Serialize per-player stats
        try {
            JSONObject statsObj = new JSONObject();
            for (Map.Entry<String, PlayerStats> e : statsMap.entrySet()) {
                PlayerStats st = e.getValue();
                JSONObject ps = new JSONObject();
                ps.put("runsScored",      st.runsScored);
                ps.put("ballsFaced",      st.ballsFaced);
                ps.put("fours",           st.fours);
                ps.put("sixes",           st.sixes);
                ps.put("runsConceded",    st.runsConceded);
                ps.put("ballsBowled",     st.ballsBowled);
                ps.put("wicketsTaken",    st.wicketsTaken);
                ps.put("maidenOvers",     st.maidenOvers);
                ps.put("currentOverRuns", st.currentOverRuns);
                ps.put("dismissalCount",  st.dismissalCount);
                ps.put("isOut",           st.isOut);
                statsObj.put(e.getKey(), ps);
            }
            live.put("statsJson", statsObj.toString());
        } catch (Exception ignored) {}

        // Serialize ball log
        try {
            JSONArray arr = new JSONArray();
            for (BallEntry b : ballLog) arr.put(b.toJson());
            live.put("ballLogJson", arr.toString());
        } catch (Exception ignored) {}

        // Serialize this-over chips
        try {
            JSONArray arr = new JSONArray();
            for (String s : thisOverBalls) arr.put(s);
            live.put("thisOverJson", arr.toString());
        } catch (Exception ignored) {}

        // ---- Per-team current score strings ----
        // current batting score e.g. "45/2 (3.4)"
        String currentScoreStr = currentRuns + "/" + currentWickets
                + " (" + currentOvers + "." + currentBalls + ")";
        String nscScore, sbrScore;
        if (isFirstInnings) {
            if ("NSC".equals(battingTeamName)) { nscScore = currentScoreStr; sbrScore = "-"; }
            else                               { sbrScore = currentScoreStr; nscScore = "-"; }
        } else {
            String first = firstInningsScoreStr != null ? firstInningsScoreStr : "-";
            if ("NSC".equals(battingTeamName)) { nscScore = currentScoreStr; sbrScore = first; }
            else                               { sbrScore = currentScoreStr; nscScore = first; }
        }
        live.put("nsc_score", nscScore);
        live.put("sbr_score", sbrScore);

        // ---- Top NSC batter (most runs) ----
        SimplePlayer topNscBatter = null; PlayerStats topNscBatterStats = null;
        for (SimplePlayer p : nscPlayers) {
            PlayerStats st = statsMap.get(p.getUid());
            if (st == null) continue;
            if (topNscBatterStats == null || st.runsScored > topNscBatterStats.runsScored) {
                topNscBatter = p; topNscBatterStats = st;
            }
        }
        // ---- Top NSC bowler (most wickets, fewest runs on tie) ----
        SimplePlayer topNscBowler = null; PlayerStats topNscBowlerStats = null;
        for (SimplePlayer p : nscPlayers) {
            PlayerStats st = statsMap.get(p.getUid());
            if (st == null || st.ballsBowled == 0) continue;
            if (topNscBowlerStats == null
                    || st.wicketsTaken > topNscBowlerStats.wicketsTaken
                    || (st.wicketsTaken == topNscBowlerStats.wicketsTaken
                        && st.runsConceded < topNscBowlerStats.runsConceded)) {
                topNscBowler = p; topNscBowlerStats = st;
            }
        }
        // ---- Top SBR batter ----
        SimplePlayer topSbrBatter = null; PlayerStats topSbrBatterStats = null;
        for (SimplePlayer p : sbrPlayers) {
            PlayerStats st = statsMap.get(p.getUid());
            if (st == null) continue;
            if (topSbrBatterStats == null || st.runsScored > topSbrBatterStats.runsScored) {
                topSbrBatter = p; topSbrBatterStats = st;
            }
        }
        // ---- Top SBR bowler ----
        SimplePlayer topSbrBowler = null; PlayerStats topSbrBowlerStats = null;
        for (SimplePlayer p : sbrPlayers) {
            PlayerStats st = statsMap.get(p.getUid());
            if (st == null || st.ballsBowled == 0) continue;
            if (topSbrBowlerStats == null
                    || st.wicketsTaken > topSbrBowlerStats.wicketsTaken
                    || (st.wicketsTaken == topSbrBowlerStats.wicketsTaken
                        && st.runsConceded < topSbrBowlerStats.runsConceded)) {
                topSbrBowler = p; topSbrBowlerStats = st;
            }
        }

        live.put("top_nsc_batter_uid",   topNscBatter != null ? topNscBatter.getUid()  : "");
        live.put("top_nsc_batter_name",  topNscBatter != null ? topNscBatter.getName() : "");
        live.put("top_nsc_batter_score", topNscBatterStats != null ? topNscBatterStats.getBattingScore() : "-");
        live.put("top_nsc_bowler_uid",   topNscBowler != null ? topNscBowler.getUid()  : "");
        live.put("top_nsc_bowler_name",  topNscBowler != null ? topNscBowler.getName() : "");
        live.put("top_nsc_bowler_score", topNscBowlerStats != null ? topNscBowlerStats.getBowlingScore() : "-");

        live.put("top_sbr_batter_uid",   topSbrBatter != null ? topSbrBatter.getUid()  : "");
        live.put("top_sbr_batter_name",  topSbrBatter != null ? topSbrBatter.getName() : "");
        live.put("top_sbr_batter_score", topSbrBatterStats != null ? topSbrBatterStats.getBattingScore() : "-");
        live.put("top_sbr_bowler_uid",   topSbrBowler != null ? topSbrBowler.getUid()  : "");
        live.put("top_sbr_bowler_name",  topSbrBowler != null ? topSbrBowler.getName() : "");
        live.put("top_sbr_bowler_score", topSbrBowlerStats != null ? topSbrBowlerStats.getBowlingScore() : "-");

        liveMatchRef.child(liveMatchKey).setValue(live);
    }

    // -----------------------------------------------------------------------
    // Player list serialization helpers
    // -----------------------------------------------------------------------
    private String serializePlayers(List<SimplePlayer> players) {
        try {
            JSONArray arr = new JSONArray();
            for (SimplePlayer p : players) {
                JSONObject o = new JSONObject();
                o.put("uid",      p.getUid());
                o.put("name",     p.getName());
                o.put("team",     p.getTeam());
                o.put("imageUrl", p.getImageUrl() != null ? p.getImageUrl() : "default");
                arr.put(o);
            }
            return arr.toString();
        } catch (Exception e) { return "[]"; }
    }

    private ArrayList<SimplePlayer> deserializePlayers(String json) {
        ArrayList<SimplePlayer> list = new ArrayList<>();
        if (json == null || json.isEmpty()) return list;
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new SimplePlayer(
                        o.optString("uid", ""),
                        o.optString("name", ""),
                        o.optString("team", ""),
                        o.optString("imageUrl", "default")));
            }
        } catch (Exception ignored) {}
        return list;
    }

    // -----------------------------------------------------------------------
    // Snapshot helper utilities (safe reads)
    // -----------------------------------------------------------------------
    private String getStr(DataSnapshot snap, String key) {
        Object v = snap.child(key).getValue();
        return v != null ? v.toString() : "";
    }
    private int getInt(DataSnapshot snap, String key, int def) {
        try { Object v = snap.child(key).getValue();
              return v != null ? Integer.parseInt(v.toString()) : def;
        } catch (Exception e) { return def; }
    }
    private boolean getBool(DataSnapshot snap, String key, boolean def) {
        try { Object v = snap.child(key).getValue();
              return v != null ? Boolean.parseBoolean(v.toString()) : def;
        } catch (Exception e) { return def; }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------
    private String[] getPlayerNames(List<SimplePlayer> team) {
        String[] arr = new String[team.size()];
        for (int i = 0; i < team.size(); i++) arr[i] = team.get(i).getName();
        return arr;
    }

    private List<SimplePlayer> getRemainingBatters(String excludeUid) {
        List<SimplePlayer> list = new ArrayList<>();
        for (SimplePlayer p : battingTeam) {
            if (!p.getUid().equals(excludeUid)) list.add(p);
        }
        return list;
    }

    private String[] getRemainingBatterNames(String excludeUid) {
        List<SimplePlayer> list = getRemainingBatters(excludeUid);
        String[] arr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i).getName();
        return arr;
    }
}