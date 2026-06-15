package com.fizanyatik.sportsclub.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fizanyatik.sportsclub.Adapter.PlayerSelectionAdapter;
import com.fizanyatik.sportsclub.List.SimplePlayer;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MatchSetupActivity extends AppCompatActivity {

    TextInputEditText matchNameEdt, seriesNameEdt, totalOversEdt, totalWicketsEdt, wicketsPerBatterEdt;
    RecyclerView nscPlayersRv, sbrPlayersRv;
    Button startMatchBtn;
    ImageView back_btn;

    DatabaseReference profileRef;
    PlayerSelectionAdapter nscAdapter, sbrAdapter;
    List<SimplePlayer> nscPlayerList = new ArrayList<>();
    List<SimplePlayer> sbrPlayerList = new ArrayList<>();

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
        setContentView(R.layout.activity_match_setup);


        // Find Views
        matchNameEdt = findViewById(R.id.match_name_edt);
        seriesNameEdt = findViewById(R.id.series_name_edt);
        totalOversEdt = findViewById(R.id.total_overs_edt);
        totalWicketsEdt = findViewById(R.id.total_wickets_edt);
        wicketsPerBatterEdt = findViewById(R.id.wickets_per_bowler_edt);
        nscPlayersRv = findViewById(R.id.nsc_players_rv);
        sbrPlayersRv = findViewById(R.id.sbr_players_rv);
        startMatchBtn = findViewById(R.id.start_match_btn);
        back_btn = findViewById(R.id.match_setup_back);

        // Setup Adapters
        nscAdapter = new PlayerSelectionAdapter(nscPlayerList, this);
        sbrAdapter = new PlayerSelectionAdapter(sbrPlayerList, this);
        nscPlayersRv.setLayoutManager(new LinearLayoutManager(this));
        sbrPlayersRv.setLayoutManager(new LinearLayoutManager(this));
        nscPlayersRv.setAdapter(nscAdapter);
        sbrPlayersRv.setAdapter(sbrAdapter);

        // Load players from Firebase — load ALL players and split by team
        profileRef = FirebaseDatabase.getInstance().getReference("Profile");
        loadPlayers();

        startMatchBtn.setOnClickListener(v -> validateAndStart());
        back_btn.setOnClickListener(v -> finish());
    }

    // Counts how many profile fetches are still pending (for both teams combined)
    private int pendingFetches = 0;

    private void loadPlayers() {
        startMatchBtn.setEnabled(false);
        nscPlayerList.clear();
        sbrPlayerList.clear();

        // Fetch NSC players then SBR players from Teams node
        loadTeamPlayers("NSC");
        loadTeamPlayers("SBR");
    }

    private void loadTeamPlayers(String team) {
        DatabaseReference teamsRef = FirebaseDatabase.getInstance()
                .getReference("Teams").child(team).child("players");

        teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || !snapshot.hasChildren()) {
                    // Nothing to load for this team — check if we're done
                    checkAllLoaded();
                    return;
                }

                for (DataSnapshot entry : snapshot.getChildren()) {
                    // Each child has a "parent" field = player UID
                    String uid = entry.child("parent").getValue(String.class);
                    if (uid == null || uid.isEmpty()) continue;

                    pendingFetches++;
                    // Fetch profile details for this UID
                    profileRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot profileSnap) {
                            try {
                                String firstName = profileSnap.child("first").getValue(String.class);
                                String lastName  = profileSnap.child("last").getValue(String.class);
                                String image     = profileSnap.child("image").getValue(String.class);

                                if (firstName != null) {
                                    String name = firstName + (lastName != null ? " " + lastName : "");
                                    SimplePlayer player = new SimplePlayer(uid, name, team, image);

                                    if ("NSC".equals(team)) {
                                        nscPlayerList.add(player);
                                    } else {
                                        sbrPlayerList.add(player);
                                    }
                                }
                            } catch (Exception ignored) {}

                            pendingFetches--;
                            checkAllLoaded();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            pendingFetches--;
                            checkAllLoaded();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MatchSetupActivity.this,
                        "Failed to load " + team + " players", Toast.LENGTH_SHORT).show();
                checkAllLoaded();
            }
        });
    }

    private void checkAllLoaded() {
        if (pendingFetches == 0) {
            nscAdapter.notifyDataSetChanged();
            sbrAdapter.notifyDataSetChanged();
            startMatchBtn.setEnabled(true);

            if (nscPlayerList.isEmpty() && sbrPlayerList.isEmpty()) {
                Toast.makeText(this, "No players found. Check Teams node in Firebase.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void validateAndStart() {
        String matchName = matchNameEdt.getText() != null ? matchNameEdt.getText().toString().trim() : "";
        String seriesName = seriesNameEdt.getText() != null ? seriesNameEdt.getText().toString().trim() : "";
        String oversStr = totalOversEdt.getText() != null ? totalOversEdt.getText().toString().trim() : "";
        String wicketsStr = totalWicketsEdt.getText() != null ? totalWicketsEdt.getText().toString().trim() : "";
        String wicketsPerBatterStr = wicketsPerBatterEdt.getText() != null ? wicketsPerBatterEdt.getText().toString().trim() : "";

        if (TextUtils.isEmpty(matchName) || TextUtils.isEmpty(seriesName)
                || TextUtils.isEmpty(oversStr) || TextUtils.isEmpty(wicketsStr)
                || TextUtils.isEmpty(wicketsPerBatterStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int overs, wickets, wicketsPerBatter;
        try {
            overs = Integer.parseInt(oversStr);
            wickets = Integer.parseInt(wicketsStr);
            wicketsPerBatter = Integer.parseInt(wicketsPerBatterStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        if (overs <= 0 || wickets <= 0 || wicketsPerBatter <= 0) {
            Toast.makeText(this, "Values must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<SimplePlayer> nscPlaying = nscAdapter.getSelectedPlayers();
        ArrayList<SimplePlayer> sbrPlaying = sbrAdapter.getSelectedPlayers();

        if (nscPlaying.isEmpty()) {
            Toast.makeText(this, "Please select at least one NSC player", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sbrPlaying.isEmpty()) {
            Toast.makeText(this, "Please select at least one SBR player", Toast.LENGTH_SHORT).show();
            return;
        }
        if (nscPlaying.size() < 2) {
            Toast.makeText(this, "Select at least 2 NSC players (for opening pair)", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sbrPlaying.size() < 2) {
            Toast.makeText(this, "Select at least 2 SBR players (for opening pair)", Toast.LENGTH_SHORT).show();
            return;
        }

        // All checks passed, start Scoring Activity
        Intent intent = new Intent(MatchSetupActivity.this, ScoringActivity.class);
        intent.putExtra("MATCH_NAME", matchName);
        intent.putExtra("SERIES_NAME", seriesName);
        intent.putExtra("TOTAL_OVERS", overs);
        intent.putExtra("TOTAL_WICKETS", wickets);
        intent.putExtra("WICKETS_PER_BATTER", wicketsPerBatter);
        intent.putExtra("NSC_PLAYERS", nscPlaying);
        intent.putExtra("SBR_PLAYERS", sbrPlaying);

        startActivity(intent);
        finish();
    }
}