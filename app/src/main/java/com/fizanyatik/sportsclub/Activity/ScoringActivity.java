package com.fizanyatik.sportsclub.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.fizanyatik.sportsclub.List.SimplePlayer;
import com.fizanyatik.sportsclub.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// A simple stats holder for each player
class PlayerStats {
    String uid;
    String name;
    int runsScored = 0, ballsFaced = 0, runsConceded = 0, ballsBowled = 0, wicketsTaken = 0;
    boolean isOut = false;
    public PlayerStats(String uid, String name) { this.uid = uid; this.name = name; }
    String getBattingScore() { return runsScored + (isOut ? "" : "*") + " (" + ballsFaced + ")"; }
    String getBowlingScore() { return wicketsTaken + "-" + runsConceded; }
}

public class ScoringActivity extends AppCompatActivity {

    // --- Setup Data ---
    String matchName, seriesName;
    int totalOvers, totalWickets, wicketsPerBowler;
    ArrayList<SimplePlayer> nscPlayers, sbrPlayers;

    // --- State Data ---
    boolean isFirstInnings = true;
    int currentRuns = 0, currentWickets = 0, currentBalls = 0, currentOvers = 0, target = -1;
    String onStrikeBatsmanId, offStrikeBatsmanId, currentBowlerId;

    //Batting/Bowling teams
    ArrayList<SimplePlayer> battingTeam, bowlingTeam;
    String battingTeamName, bowlingTeamName;

    // Stats maps
    HashMap<String, PlayerStats> playerStatsMap = new HashMap<>();

    // First Innings Score
    String firstInningsScoreStr = "", secondInningsScoreStr = "";

    // --- Views ---
    TextView teamNameTv, scoreTv, oversTv, targetTv, onStrikeBatterTv, offStrikeBatterTv, bowlerTv;
    Button btnRun0, btnRun1, btnRun2, btnRun3, btnRun4, btnRun6, btnWide, btnNoBall, btnWicket, btnEndInnings;

    // --- Firebase ---
    DatabaseReference matchRef;
    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring);

        // Get data from Intent
        if (getIntent().getExtras() == null) {
            Toast.makeText(this, "Error: No match data.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        matchName = getIntent().getStringExtra("MATCH_NAME");
        seriesName = getIntent().getStringExtra("SERIES_NAME");
        totalOvers = getIntent().getIntExtra("TOTAL_OVERS", 0);
        totalWickets = getIntent().getIntExtra("TOTAL_WICKETS", 0);
        wicketsPerBowler = getIntent().getIntExtra("WICKETS_PER_BOWLER", 0);
        nscPlayers = (ArrayList<SimplePlayer>) getIntent().getSerializableExtra("NSC_PLAYERS");
        sbrPlayers = (ArrayList<SimplePlayer>) getIntent().getSerializableExtra("SBR_PLAYERS");

        // Init Firebase
        matchRef = FirebaseDatabase.getInstance().getReference("Match");
        storageRef = FirebaseStorage.getInstance().getReference("Match"); // For PDF

        initViews();
        initListeners();

        // Populate stats map
        for(SimplePlayer p : nscPlayers) playerStatsMap.put(p.getUid(), new PlayerStats(p.getUid(), p.getName()));
        for(SimplePlayer p : sbrPlayers) playerStatsMap.put(p.getUid(), new PlayerStats(p.getUid(), p.getName()));

        // Start first innings (Assuming NSC bats first - TODO: Add a toss)
        startInnings(true);
    }

    private void initViews() {
        teamNameTv = findViewById(R.id.team_name_tv);
        scoreTv = findViewById(R.id.score_tv);
        oversTv = findViewById(R.id.overs_tv);
        targetTv = findViewById(R.id.target_tv);
        onStrikeBatterTv = findViewById(R.id.on_strike_batter_tv);
        offStrikeBatterTv = findViewById(R.id.off_strike_batter_tv);
        bowlerTv = findViewById(R.id.bowler_tv);
        btnRun0 = findViewById(R.id.btn_run_0);
        btnRun1 = findViewById(R.id.btn_run_1);
        btnRun2 = findViewById(R.id.btn_run_2);
        btnRun3 = findViewById(R.id.btn_run_3);
        btnRun4 = findViewById(R.id.btn_run_4);
        btnRun6 = findViewById(R.id.btn_run_6);
        btnWide = findViewById(R.id.btn_wide);
        btnNoBall = findViewById(R.id.btn_no_ball);
        btnWicket = findViewById(R.id.btn_wicket);
        btnEndInnings = findViewById(R.id.btn_end_innings);
    }

    private void initListeners() {
        btnRun0.setOnClickListener(v -> addBall(0, false, false));
        btnRun1.setOnClickListener(v -> addBall(1, false, false));
        btnRun2.setOnClickListener(v -> addBall(2, false, false));
        btnRun3.setOnClickListener(v -> addBall(3, false, false));
        btnRun4.setOnClickListener(v -> addBall(4, false, false));
        btnRun6.setOnClickListener(v -> addBall(6, false, false));

        btnWide.setOnClickListener(v -> addBall(1, true, false)); // 1 run, isExtra
        btnNoBall.setOnClickListener(v -> addBall(1, true, false)); // 1 run, isExtra (simplification)

        btnWicket.setOnClickListener(v -> {
            addBall(0, false, true); // 0 runs, isWicket
            if (currentWickets < totalWickets) {
                promptForNextBatsman();
            }
        });

        btnEndInnings.setOnClickListener(v -> endInnings());
    }

    private void startInnings(boolean isFirst) {
        isFirstInnings = isFirst;
        currentRuns = 0;
        currentWickets = 0;
        currentBalls = 0;
        currentOvers = 0;

        if (isFirstInnings) {
            battingTeam = nscPlayers;
            bowlingTeam = sbrPlayers;
            battingTeamName = "NSC";
            bowlingTeamName = "SBR";
            targetTv.setVisibility(View.GONE);
        } else {
            battingTeam = sbrPlayers;
            bowlingTeam = nscPlayers;
            battingTeamName = "SBR";
            bowlingTeamName = "NSC";
            target = target; // Target already set
            targetTv.setText("Target: " + target);
            targetTv.setVisibility(View.VISIBLE);
        }
        teamNameTv.setText("Batting Team: " + battingTeamName);
        promptForOpeningPlayers();
    }

    private void addBall(int runs, boolean isExtra, boolean isWicket) {
        if (currentOvers == totalOvers || currentWickets == totalWickets) {
            Toast.makeText(this, "Innings is over.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentRuns += runs;

        // Get stats objects
        PlayerStats batter = playerStatsMap.get(onStrikeBatsmanId);
        PlayerStats bowler = playerStatsMap.get(currentBowlerId);

        // Update bowler stats
        bowler.runsConceded += runs;

        if (!isExtra) {
            currentBalls++;
            bowler.ballsBowled++;
            batter.runsScored += runs;
            batter.ballsFaced++;

            if (runs == 1 || runs == 3) {
                swapStrike();
            }
        }

        if (isWicket) {
            currentWickets++;
            bowler.wicketsTaken++;
            batter.isOut = true;
            onStrikeBatsmanId = null; // Next batter will be selected
        }

        // Check for end of over
        if (currentBalls == 6) {
            currentBalls = 0;
            currentOvers++;
            swapStrike();
            if (currentOvers < totalOvers) {
                promptForNextBowler();
            }
        }

        updateUI();

        // Check for innings end
        if (currentOvers == totalOvers || currentWickets == totalWickets) {
            endInnings();
        }

        // Check for chase complete
        if (!isFirstInnings && currentRuns >= target) {
            endInnings();
        }
    }

    private void swapStrike() {
        String temp = onStrikeBatsmanId;
        onStrikeBatsmanId = offStrikeBatsmanId;
        offStrikeBatsmanId = temp;
    }

    private void updateUI() {
        scoreTv.setText(currentRuns + "/" + currentWickets);
        oversTv.setText("Overs: " + currentOvers + "." + currentBalls + " (" + totalOvers + ")");

        if (onStrikeBatsmanId != null) {
            onStrikeBatterTv.setText("On Strike: " + playerStatsMap.get(onStrikeBatsmanId).getBattingScore());
        } else {
            onStrikeBatterTv.setText("On Strike: -");
        }

        if (offStrikeBatsmanId != null) {
            offStrikeBatterTv.setText("Off Strike: " + playerStatsMap.get(offStrikeBatsmanId).getBattingScore());
        } else {
            offStrikeBatterTv.setText("Off Strike: -");
        }

        if (currentBowlerId != null) {
            bowlerTv.setText("Bowler: " + playerStatsMap.get(currentBowlerId).name + " (" + playerStatsMap.get(currentBowlerId).getBowlingScore() + ")");
        } else {
            bowlerTv.setText("Bowler: -");
        }
    }

    private void endInnings() {
        if (isFirstInnings) {
            target = currentRuns + 1;
            firstInningsScoreStr = currentRuns + "/" + currentWickets + " (" + currentOvers + "." + currentBalls + ")";
            new AlertDialog.Builder(this)
                    .setTitle("Innings Over")
                    .setMessage("Target for " + bowlingTeamName + " is " + target)
                    .setPositiveButton("Start 2nd Innings", (dialog, which) -> startInnings(false))
                    .setCancelable(false)
                    .show();
        } else {
            // Match is over
            secondInningsScoreStr = currentRuns + "/" + currentWickets + " (" + currentOvers + "." + currentBalls + ")";
            promptForTopPerformersAndSave();
        }
    }

    // --- DIALOGS for player selection ---

    private void promptForOpeningPlayers() {
        // Show dialog to select two batsmen and one bowler
        // This is complex, so I'll just pick the first players for simplicity
        onStrikeBatsmanId = battingTeam.get(0).getUid();
        offStrikeBatsmanId = battingTeam.get(1).getUid();
        promptForNextBowler(); // This will select the first bowler
    }

    private void promptForNextBatsman() {
        // Show dialog with remaining (not-out) batsmen from `battingTeam`
        // For simplicity, just picking the next player in the list
        for (SimplePlayer p : battingTeam) {
            if (!playerStatsMap.get(p.getUid()).isOut && !p.getUid().equals(offStrikeBatsmanId)) {
                onStrikeBatsmanId = p.getUid();
                updateUI();
                return;
            }
        }
    }

    private void promptForNextBowler() {
        // Show dialog with bowlers from `bowlingTeam` who haven't exceeded `wicketsPerBowler`
        // For simplicity, just picking the first player
        currentBowlerId = bowlingTeam.get(0).getUid();
        updateUI();
    }

    // --- FINAL STEP: Save to Firebase ---

    private void promptForTopPerformersAndSave() {
        // This is where you would show a dialog to select top performers
        // For now, we will auto-select them and save

        // TODO: Create a proper dialog to select top performers
        // For this example, I'll just pick the first player from each list
        // and you MUST replace this with a real selection dialog.

        SimplePlayer nscTopBatter = nscPlayers.get(0);
        SimplePlayer nscTopBowler = nscPlayers.get(0);
        SimplePlayer sbrTopBatter = sbrPlayers.get(0);
        SimplePlayer sbrTopBowler = sbrPlayers.get(0);

        // After selection, call saveMatchData
        saveMatchData(nscTopBatter, nscTopBowler, sbrTopBatter, sbrTopBowler);
    }

    private void saveMatchData(SimplePlayer nscTopBatter, SimplePlayer nscTopBowler, SimplePlayer sbrTopBatter, SimplePlayer sbrTopBowler) {

        String result = calculateResult(); // Get "NSC won by..."

        // TODO: PDF Generation
        // 1. Use iText7 or another library to generate a PDF scorecard
        // 2. Upload it to Firebase Storage (e.g., to storageRef.child(System.currentTimeMillis() + ".pdf"))
        // 3. Get the downloadUrl

        String fakePdfUrl = "https://firebasestorage.googleapis.com/v0/b/fsc-app-dcb8c.appspot.com/o/Match%2FScorecard.pdf?alt=media&token=84ee60b0-8509-4285-be91-ff5a950b8b01"; // Placeholder

        DatabaseReference newMatchRef = matchRef.push();
        String parentKey = newMatchRef.getKey();

        Map<String, Object> matchData = new HashMap<>();
        matchData.put("parent", parentKey);
        matchData.put("details", matchName + " • " + seriesName);
        matchData.put("result", result);
        matchData.put("scorecard", fakePdfUrl); // Use the real URL here

        // Assuming NSC (Team 2 in my logic) batted second
        matchData.put("team1_name", "SBR"); // Batting first
        matchData.put("team1_score", firstInningsScoreStr);
        matchData.put("team2_name", "NSC"); // Batting second
        matchData.put("team2_score", secondInningsScoreStr);

        // Get stats from our map
        PlayerStats sbrBatterStats = playerStatsMap.get(sbrTopBatter.getUid());
        PlayerStats sbrBowlerStats = playerStatsMap.get(sbrTopBowler.getUid());
        PlayerStats nscBatterStats = playerStatsMap.get(nscTopBatter.getUid());
        PlayerStats nscBowlerStats = playerStatsMap.get(nscTopBowler.getUid());

        // SBR (Team 1)
        matchData.put("top_team1_image", sbrTopBatter.getUid());
        matchData.put("top_team1_name", sbrBatterStats.name);
        matchData.put("top_team1_score", sbrBatterStats.getBattingScore());
        matchData.put("top2_team1_image", sbrTopBowler.getUid());
        matchData.put("top2_team1_name", sbrBowlerStats.name);
        matchData.put("top2_team1_score", sbrBowlerStats.getBowlingScore() + " (" + (sbrBowlerStats.ballsBowled / 6) + "." + (sbrBowlerStats.ballsBowled % 6) + ")");

        // NSC (Team 2)
        matchData.put("top_team2_image", nscTopBatter.getUid());
        matchData.put("top_team2_name", nscBatterStats.name);
        matchData.put("top_team2_score", nscBatterStats.getBattingScore());
        matchData.put("top2_team2_image", nscTopBowler.getUid());
        matchData.put("top2_team2_name", nscBowlerStats.name);
        matchData.put("top2_team2_score", nscBowlerStats.getBowlingScore() + " (" + (nscBowlerStats.ballsBowled / 6) + "." + (nscBowlerStats.ballsBowled % 6) + ")");

        newMatchRef.setValue(matchData).addOnSuccessListener(aVoid -> {
            Toast.makeText(ScoringActivity.this, "Match Saved!", Toast.LENGTH_LONG).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(ScoringActivity.this, "Error saving match: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private String calculateResult() {
        if (isFirstInnings) return "Match abandoned"; // Should not happen

        int runsDiff = Math.abs(currentRuns - (target - 1));
        if (currentRuns >= target) {
            // Batting second team (NSC) won
            return battingTeamName + " won by " + (totalWickets - currentWickets) + " wickets";
        } else if (currentRuns < target - 1) {
            // Batting first team (SBR) won
            return bowlingTeamName + " won by " + runsDiff + " runs";
        } else {
            return "Match Tied";
        }
    }
}