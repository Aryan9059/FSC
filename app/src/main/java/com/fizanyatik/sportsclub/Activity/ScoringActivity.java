package com.fizanyatik.sportsclub.Activity;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    int runsConceded = 0, ballsBowled = 0, wicketsTaken = 0;
    // How many times this batter has been dismissed so far this innings
    int dismissalCount = 0;
    // True only when dismissalCount >= wicketsPerBatter (fully out)
    boolean isOut = false;

    public PlayerStats(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    /** e.g. "35* (20)" for not out, "35 (20)" for out */
    String getBattingScore() {
        return runsScored + (isOut ? "" : "*") + " (" + ballsFaced + ")";
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

    // ---- Views ----
    TextView teamNameTv, scoreTv, oversTv, targetTv,
            onStrikeBatterTv, offStrikeBatterTv, bowlerTv;
    Button btnRun0, btnRun1, btnRun2, btnRun3, btnRun4, btnRun6,
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
        teamNameTv       = findViewById(R.id.team_name_tv);
        scoreTv          = findViewById(R.id.score_tv);
        oversTv          = findViewById(R.id.overs_tv);
        targetTv         = findViewById(R.id.target_tv);
        onStrikeBatterTv = findViewById(R.id.on_strike_batter_tv);
        offStrikeBatterTv = findViewById(R.id.off_strike_batter_tv);
        bowlerTv         = findViewById(R.id.bowler_tv);
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
                new AlertDialog.Builder(this)
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
        new AlertDialog.Builder(this)
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
            targetTv.setVisibility(View.GONE);
        } else {
            // Second innings: batting and bowling teams are swapped
            ArrayList<SimplePlayer> tmp = battingTeam;
            battingTeam = bowlingTeam;
            bowlingTeam = tmp;
            String tmpName = battingTeamName;
            battingTeamName = bowlingTeamName;
            bowlingTeamName = tmpName;

            targetTv.setText("Target: " + target);
            targetTv.setVisibility(View.VISIBLE);
        }

        teamNameTv.setText("Batting: " + battingTeamName + "  |  Bowling: " + bowlingTeamName);
        setButtonsEnabled(false); // disable until players selected

        promptForOpeningPlayers();
    }

    // -----------------------------------------------------------------------
    // Core ball processing
    // -----------------------------------------------------------------------
    private boolean isInningsOver() {
        // Innings ends only when overs are exhausted.
        // Wicket-based endings are handled in promptForNextBatsman():
        // if no one is left to replace a dismissed batter, endInnings() is called there.
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

        if (bowler != null) bowler.runsConceded += runs;

        // Track whether this ball caused a "life lost" (but batter stays)
        boolean isLifeLost = false;

        if (!isExtra) {
            currentBalls++;
            if (bowler != null) bowler.ballsBowled++;
            if (batter != null) {
                batter.runsScored += runs;
                batter.ballsFaced++;
            }
            // Odd runs swap strike
            if (runs % 2 == 1) swapStrike();
        }

        if (isWicket) {
            if (bowler != null) bowler.wicketsTaken++;
            if (batter != null) {
                batter.dismissalCount++;

                // *** KEY CHANGE: increment team wickets on EVERY dismissal/life-lost ***
                currentWickets++;

                if (batter.dismissalCount >= wicketsPerBatter) {
                    // Batter has used all their lives — truly out
                    batter.isOut = true;
                    onStrikeBatsmanId = null;
                } else {
                    // Batter still has lives remaining — stays at the crease
                    isLifeLost = true;
                    int livesLeft = wicketsPerBatter - batter.dismissalCount;
                    Toast.makeText(this,
                            batter.name + " has " + livesLeft
                                    + " life" + (livesLeft == 1 ? "" : "s") + " remaining!",
                            Toast.LENGTH_SHORT).show();
                    // Do NOT set onStrikeBatsmanId = null; batter continues
                }
            }
        }

        // ---- Record this ball in our log ----
        BallEntry entry = new BallEntry();
        entry.innings     = isFirstInnings ? 1 : 2;
        entry.overNum     = currentOvers;
        entry.ballInOver  = currentBalls; // already incremented above for legal balls
        entry.batterName  = batter != null ? batter.name : "?";
        entry.bowlerName  = bowler != null ? bowler.name : "?";
        entry.runs        = runs;
        entry.isExtra     = isExtra;
        entry.isWicket    = isWicket && !isLifeLost;  // "true wicket" — batter fully out
        entry.isLifeLost  = isLifeLost;
        entry.teamScore   = currentRuns + "/" + currentWickets;
        ballLog.add(entry);
        persistBallLog();

        // End of over
        if (!isExtra && currentBalls == 6) {
            currentBalls = 0;
            currentOvers++;
            swapStrike(); // end-of-over swap (non-striker becomes striker)
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

        // After a true dismissal, try to bring in the next batter.
        // promptForNextBatsman() will call endInnings() itself if nobody is left.
        if (isWicket && onStrikeBatsmanId == null) {
            promptForNextBatsman();
        }

        // After completing an over (and innings not over), prompt for next bowler
        if (!isExtra && currentBalls == 0 && currentOvers > 0 && currentOvers < totalOvers) {
            promptForNextBowler();
        }
    }

    private void swapStrike() {
        // If there's no off-striker (last batter batting alone), don't swap
        if (offStrikeBatsmanId == null) return;
        String tmp = onStrikeBatsmanId;
        onStrikeBatsmanId = offStrikeBatsmanId;
        offStrikeBatsmanId = tmp;
    }

    // -----------------------------------------------------------------------
    // UI update
    // -----------------------------------------------------------------------
    private void updateUI() {
        scoreTv.setText(currentRuns + "/" + currentWickets);
        oversTv.setText("Overs: " + currentOvers + "." + currentBalls + " / " + totalOvers);

        if (onStrikeBatsmanId != null) {
            PlayerStats s = statsMap.get(onStrikeBatsmanId);
            onStrikeBatterTv.setText("⚡ " + (s != null ? s.name : "?") + "  " + (s != null ? s.getBattingScore() : ""));
        } else {
            onStrikeBatterTv.setText("⚡ (select next batter)");
        }

        if (offStrikeBatsmanId != null) {
            PlayerStats s = statsMap.get(offStrikeBatsmanId);
            offStrikeBatterTv.setText("   " + (s != null ? s.name : "?") + "  " + (s != null ? s.getBattingScore() : ""));
        } else {
            offStrikeBatterTv.setText("   -");
        }

        if (currentBowlerId != null) {
            PlayerStats s = statsMap.get(currentBowlerId);
            bowlerTv.setText("🏏 " + (s != null ? s.name : "?") + "  " + (s != null ? s.getBowlingFull() : ""));
        } else {
            bowlerTv.setText("🏏 (select bowler)");
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
        // Step 1: pick opener 1 (on strike)
        String[] names = getPlayerNames(battingTeam);
        new AlertDialog.Builder(this)
                .setTitle("Select Opening Batter (On Strike)")
                .setItems(names, (d, which) -> {
                    onStrikeBatsmanId = battingTeam.get(which).getUid();
                    updateUI();
                    // Step 2: pick opener 2 (off strike)
                    String[] remaining = getRemainingBatterNames(onStrikeBatsmanId);
                    List<SimplePlayer> remainingList = getRemainingBatters(onStrikeBatsmanId);
                    new AlertDialog.Builder(this)
                            .setTitle("Select Opening Batter (Off Strike)")
                            .setItems(remaining, (d2, which2) -> {
                                offStrikeBatsmanId = remainingList.get(which2).getUid();
                                updateUI();
                                // Step 3: pick bowler
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
            // No one left to replace the dismissed batter.
            // If there is still an off-striker, they bat alone (last batter).
            // If even the off-striker is gone, the innings is truly over.
            if (offStrikeBatsmanId == null) {
                endInnings();
            } else {
                // Last batter: the off-striker is now on their own.
                // onStrikeBatsmanId stays null → swap won't happen; they face every ball.
                // Swap so the remaining batter faces the next delivery.
                onStrikeBatsmanId = offStrikeBatsmanId;
                offStrikeBatsmanId = null;
                Toast.makeText(this, "Last batter — innings continues!", Toast.LENGTH_SHORT).show();
                updateUI();
                setButtonsEnabled(true);
            }
            return;
        }

        String[] names = new String[available.size()];
        for (int i = 0; i < available.size(); i++) names[i] = available.get(i).getName();

        new AlertDialog.Builder(this)
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
    // Next bowler dialog — all bowlers in bowling team are eligible
    // -----------------------------------------------------------------------
    private void promptForNextBowler() {
        String[] names = new String[bowlingTeam.size()];
        for (int i = 0; i < bowlingTeam.size(); i++) {
            PlayerStats s = statsMap.get(bowlingTeam.get(i).getUid());
            names[i] = bowlingTeam.get(i).getName()
                    + (s != null ? "  (" + s.getBowlingFull() + ")" : "");
        }

        new AlertDialog.Builder(this)
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

            new AlertDialog.Builder(this)
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

        new AlertDialog.Builder(this)
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
    // Top performer selection (4 steps: NSC top batter, NSC top bowler,
    //                                    SBR top batter, SBR top bowler)
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
                        // Generate PDF, upload, then save match
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
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setItems(names, (d, which) -> listener.onPicked(pool.get(which)))
                .setCancelable(false)
                .show();
    }

    // -----------------------------------------------------------------------
    // Ball-by-ball log — persist to SharedPreferences after every ball
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
    // PDF generation (Android PdfDocument — no extra library needed)
    // -----------------------------------------------------------------------
    private byte[] generateScorecardPdf(String result) {
        PdfDocument doc = new PdfDocument();

        // Page dimensions (A4-ish in points)
        int pageWidth  = 595;
        int pageHeight = 842;
        int margin     = 36;
        int lineH      = 18;  // line height
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

        // Separator
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint);
        yPos += 6;

        // ---- Batting scorecards ----
        // Team 1 (NSC)
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

        // Separator
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint);
        yPos += 8;

        // ---- Ball-by-ball log ----
        // If the remaining space is too small, start a new page
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

        // Column headers
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
                // Reprint headers
                for (int c = 0; c < cols.length; c++) {
                    canvas.drawText(cols[c], colX[c], yPos, headPaint);
                }
                yPos += lineH;
                canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint);
                yPos += 4;
            }

            // Alternate row background
            if (ballLog.indexOf(b) % 2 == 0) {
                Paint bgPaint = new Paint();
                bgPaint.setColor(Color.rgb(245, 245, 255));
                canvas.drawRect(margin, yPos - lineH + 4, pageWidth - margin, yPos + 4, bgPaint);
            }

            // Innings separator label
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
            // Truncate long names
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

    private int drawBattingCard(Canvas canvas, List<SimplePlayer> team, String teamName,
                                Paint headPaint, Paint bodyPaint, Paint linePaint,
                                int margin, int yPos, int pageWidth, int lineH) {
        canvas.drawText(teamName + " Batting", margin, yPos, headPaint);
        yPos += lineH;
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint);
        yPos += 4;
        canvas.drawText("Player", margin, yPos, headPaint);
        canvas.drawText("Runs (Balls)", margin + 200, yPos, headPaint);
        yPos += lineH;

        for (SimplePlayer p : team) {
            PlayerStats s = statsMap.get(p.getUid());
            if (s == null) continue;
            canvas.drawText(truncate(s.name, 28), margin, yPos, bodyPaint);
            canvas.drawText(s.getBattingScore(), margin + 200, yPos, bodyPaint);
            yPos += lineH;
        }
        return yPos;
    }

    private int drawBowlingCard(Canvas canvas, List<SimplePlayer> team, String label,
                                Paint headPaint, Paint bodyPaint, Paint linePaint,
                                int margin, int yPos, int pageWidth, int lineH) {
        canvas.drawText(label, margin, yPos, headPaint);
        yPos += lineH;
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, linePaint);
        yPos += 4;
        canvas.drawText("Bowler", margin, yPos, headPaint);
        canvas.drawText("W-Runs (Overs)", margin + 200, yPos, headPaint);
        yPos += lineH;

        for (SimplePlayer p : team) {
            PlayerStats s = statsMap.get(p.getUid());
            if (s == null || s.ballsBowled == 0) continue;
            canvas.drawText(truncate(s.name, 28), margin, yPos, bodyPaint);
            canvas.drawText(s.getBowlingFull(), margin + 200, yPos, bodyPaint);
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
                                // Fallback: save without a real scorecard URL
                                saveMatchData(result, "");
                            });
                        }
                    });
        }).start();
    }

    // -----------------------------------------------------------------------
    // Save match to Firebase (matches node) matching MatchFragment's schema
    // -----------------------------------------------------------------------
    private void saveMatchData(String result, String scorecardUrl) {
        // Determine which team batted 1st and 2nd for the schema
        // In MatchFragment/MatchList: team1 = first batting, team2 = second batting
        String t1Name, t2Name, t1Score, t2Score;
        // firstInningsScoreStr belongs to whoever batted first (recorded in isFirstInnings = true block)
        // At end of match: battingTeamName is the 2nd innings team
        t2Name  = battingTeamName;       // 2nd innings batting team
        t1Name  = bowlingTeamName;       // 1st innings batting team
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
        // Use the real Supabase URL (or empty string if upload failed)
        data.put("scorecard", scorecardUrl);

        // team1 = batting first
        data.put("team1_name", t1Name);
        data.put("team1_score", t1Score);
        // team2 = batting second
        data.put("team2_name", t2Name);
        data.put("team2_score", t2Score);

        // NSC top performers
        data.put("top_team1_image",  pickedNscBatter != null ? pickedNscBatter.getUid() : "");
        data.put("top_team1_name",   pickedNscBatter != null ? pickedNscBatter.getName() : "");
        data.put("top_team1_score",  nscBatterStats != null ? nscBatterStats.getBattingScore() : "0 (0)");
        data.put("top2_team1_image", pickedNscBowler != null ? pickedNscBowler.getUid() : "");
        data.put("top2_team1_name",  pickedNscBowler != null ? pickedNscBowler.getName() : "");
        data.put("top2_team1_score", nscBowlerStats != null ? nscBowlerStats.getBowlingFull() : "0-0");

        // SBR top performers
        data.put("top_team2_image",  pickedSbrBatter != null ? pickedSbrBatter.getUid() : "");
        data.put("top_team2_name",   pickedSbrBatter != null ? pickedSbrBatter.getName() : "");
        data.put("top_team2_score",  sbrBatterStats != null ? sbrBatterStats.getBattingScore() : "0 (0)");
        data.put("top2_team2_image", pickedSbrBowler != null ? pickedSbrBowler.getUid() : "");
        data.put("top2_team2_name",  pickedSbrBowler != null ? pickedSbrBowler.getName() : "");
        data.put("top2_team2_score", sbrBowlerStats != null ? sbrBowlerStats.getBowlingFull() : "0-0");

        newMatchRef.setValue(data)
                .addOnSuccessListener(aVoid -> {
                    // Remove the live-match node after the final save
                    if (liveMatchKey != null) {
                        liveMatchRef.child(liveMatchKey).removeValue();
                    }
                    // Clear local scorecard cache
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().remove(KEY_BALLS).apply();
                    Toast.makeText(ScoringActivity.this, "✅ Match saved successfully!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ScoringActivity.this,
                                "❌ Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // -----------------------------------------------------------------------
    // Live score push (called after every ball)
    // -----------------------------------------------------------------------
    private void saveLiveScore() {
        if (liveMatchKey == null) return;
        Map<String, Object> live = new HashMap<>();
        live.put("matchName", matchName);
        live.put("battingTeam", battingTeamName);
        live.put("bowlingTeam", bowlingTeamName);
        live.put("runs", currentRuns);
        live.put("wickets", currentWickets);
        live.put("overs", currentOvers + "." + currentBalls);
        live.put("target", target > 0 ? target : 0);
        live.put("isFirstInnings", isFirstInnings);
        liveMatchRef.child(liveMatchKey).setValue(live);
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