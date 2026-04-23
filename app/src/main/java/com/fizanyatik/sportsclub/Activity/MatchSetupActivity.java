package com.fizanyatik.sportsclub.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

    TextInputEditText matchNameEdt, seriesNameEdt, totalOversEdt, totalWicketsEdt, wicketsPerBowlerEdt;
    RecyclerView nscPlayersRv, sbrPlayersRv;
    Button startMatchBtn;

    DatabaseReference profileRef;
    PlayerSelectionAdapter nscAdapter, sbrAdapter;
    List<SimplePlayer> nscPlayerList = new ArrayList<>();
    List<SimplePlayer> sbrPlayerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_setup);

        // Find Views
        matchNameEdt = findViewById(R.id.match_name_edt);
        seriesNameEdt = findViewById(R.id.series_name_edt);
        totalOversEdt = findViewById(R.id.total_overs_edt);
        totalWicketsEdt = findViewById(R.id.total_wickets_edt);
        wicketsPerBowlerEdt = findViewById(R.id.wickets_per_bowler_edt);
        nscPlayersRv = findViewById(R.id.nsc_players_rv);
        sbrPlayersRv = findViewById(R.id.sbr_players_rv);
        startMatchBtn = findViewById(R.id.start_match_btn);

        // Setup Adapters
        nscAdapter = new PlayerSelectionAdapter(nscPlayerList, this);
        sbrAdapter = new PlayerSelectionAdapter(sbrPlayerList, this);
        nscPlayersRv.setAdapter(nscAdapter);
        sbrPlayersRv.setAdapter(sbrAdapter);

        // Load players from Firebase
        profileRef = FirebaseDatabase.getInstance().getReference("Profile");
        loadPlayers();

        startMatchBtn.setOnClickListener(v -> validateAndStart());
    }

    private void loadPlayers() {
        profileRef.equalTo("dHjYjLIUXAXEEjTo0jDViZQFZyd2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nscPlayerList.clear();
                sbrPlayerList.clear();
                for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                    String uid = playerSnapshot.getKey();
                    String name = playerSnapshot.child("first").getValue(String.class) + " " + playerSnapshot.child("last").getValue(String.class);
                    String team = playerSnapshot.child("team").getValue(String.class);
                    String image = playerSnapshot.child("image").getValue(String.class);

                    SimplePlayer player = new SimplePlayer(uid, name, team, image);

                    if ("NSC".equals(team)) {
                        nscPlayerList.add(player);
                    } else if ("SBR".equals(team)) {
                        sbrPlayerList.add(player);
                    }
                }
                nscAdapter.notifyDataSetChanged();
                sbrAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MatchSetupActivity.this, "Failed to load players", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateAndStart() {
        String matchName = matchNameEdt.getText().toString().trim();
        String seriesName = seriesNameEdt.getText().toString().trim();
        String oversStr = totalOversEdt.getText().toString().trim();
        String wicketsStr = totalWicketsEdt.getText().toString().trim();
        String wicketsPerBowlerStr = wicketsPerBowlerEdt.getText().toString().trim();

        if (TextUtils.isEmpty(matchName) || TextUtils.isEmpty(seriesName) || TextUtils.isEmpty(oversStr) || TextUtils.isEmpty(wicketsStr) || TextUtils.isEmpty(wicketsPerBowlerStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<SimplePlayer> nscPlaying = nscAdapter.getSelectedPlayers();
        ArrayList<SimplePlayer> sbrPlaying = sbrAdapter.getSelectedPlayers();

        if (nscPlaying.isEmpty() || sbrPlaying.isEmpty()) {
            Toast.makeText(this, "Please select players for both teams", Toast.LENGTH_SHORT).show();
            return;
        }

        // All checks passed, start Scoring Activity
        Intent intent = new Intent(MatchSetupActivity.this, ScoringActivity.class);
        intent.putExtra("MATCH_NAME", matchName);
        intent.putExtra("SERIES_NAME", seriesName);
        intent.putExtra("TOTAL_OVERS", Integer.parseInt(oversStr));
        intent.putExtra("TOTAL_WICKETS", Integer.parseInt(wicketsStr));
        intent.putExtra("WICKETS_PER_BOWLER", Integer.parseInt(wicketsPerBowlerStr));
        intent.putExtra("NSC_PLAYERS", nscPlaying);
        intent.putExtra("SBR_PLAYERS", sbrPlaying);

        startActivity(intent);
        finish();
    }
}