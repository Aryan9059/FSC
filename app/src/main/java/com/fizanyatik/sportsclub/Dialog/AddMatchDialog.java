package com.fizanyatik.sportsclub.Dialog;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.fizanyatik.sportsclub.List.MatchPlayerList;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddMatchDialog extends DialogFragment {
    private ImageView match_back;
    private Button add_match_btn;
    private List<MatchPlayerList> matchPlayerItems;
    private ArrayList<String> players, players2, players3, players4;
    private MaterialSwitch out_batter1, out_batter2;
    private MaterialAutoCompleteTextView team1, team2, batter1, batter2, bowler1, bowler2;
    private TextInputEditText score_team1, score_team2, overs_team1, overs_team2, score_batter1, score_batter2, balls_batter1,
            balls_batter2, wickets_bowler1, wickets_bowler2, runs_bowler1, runs_bowler2, overs_bowler1, overs_bowler2,
            match_details, match_result;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_match_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        setupTeamAdapters();
        setupListeners();
    }

    private void initViews(View view) {
        players = new ArrayList<>();
        players2 = new ArrayList<>();
        players3 = new ArrayList<>();
        players4 = new ArrayList<>();
        matchPlayerItems = new ArrayList<>();

        match_details = view.findViewById(R.id.match_details);
        match_result = view.findViewById(R.id.match_result);
        match_back = view.findViewById(R.id.add_match_back);
        add_match_btn = view.findViewById(R.id.add_match_upload);
        out_batter1 = view.findViewById(R.id.out_switch_batter1);
        out_batter2 = view.findViewById(R.id.out_switch_batter2);
        team1 = view.findViewById(R.id.team1);
        team2 = view.findViewById(R.id.team2);
        batter1 = view.findViewById(R.id.batter1);
        batter2 = view.findViewById(R.id.batter2);
        bowler1 = view.findViewById(R.id.bowler1);
        bowler2 = view.findViewById(R.id.bowler2);
        score_team1 = view.findViewById(R.id.score_team1);
        score_team2 = view.findViewById(R.id.score_team2);
        overs_team1 = view.findViewById(R.id.overs_team1);
        overs_team2 = view.findViewById(R.id.overs_team2);
        score_batter1 = view.findViewById(R.id.score_batter1);
        score_batter2 = view.findViewById(R.id.score_batter2);
        balls_batter1 = view.findViewById(R.id.balls_batter1);
        balls_batter2 = view.findViewById(R.id.balls_batter2);
        wickets_bowler1 = view.findViewById(R.id.wicket_bowler1);
        wickets_bowler2 = view.findViewById(R.id.wicket_bowler2);
        runs_bowler1 = view.findViewById(R.id.runs_bowler1);
        runs_bowler2 = view.findViewById(R.id.runs_bowler2);
        overs_bowler1 = view.findViewById(R.id.overs_bowler1);
        overs_bowler2 = view.findViewById(R.id.overs_bowler2);
    }

    private void setupTeamAdapters() {
        String[] type = getResources().getStringArray(R.array.team_match);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.drop_down_feed_item, type);
        team1.setAdapter(arrayAdapter);
        team2.setAdapter(arrayAdapter);
    }

    private void setupListeners() {
        match_back.setOnClickListener(v -> {
            match_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            dismiss();
        });

        team1.setOnItemClickListener((parent, view, position, id) -> handleTeamSelection(team1, batter1, bowler1, players, players2, players3, players4, true));
        team2.setOnItemClickListener((parent, view, position, id) -> handleTeamSelection(team2, batter2, bowler2, players, players2, players3, players4, false));

        add_match_btn.setOnClickListener(v -> uploadMatch());
    }

    private void handleTeamSelection(MaterialAutoCompleteTextView teamView, MaterialAutoCompleteTextView batterView,
                                    MaterialAutoCompleteTextView bowlerView, ArrayList<String> p1, ArrayList<String> p2,
                                    ArrayList<String> p3, ArrayList<String> p4, boolean isTeam1) {
        String selectedTeam = teamView.getText().toString();
        String teamA = "NSC";
        String teamB = "SBR";
        if (selectedTeam.equals(teamA)) {
            fetchPlayers(teamA, p1, batterView, isTeam1);
            fetchPlayers(teamB, p2, bowlerView, isTeam1);
        } else {
            fetchPlayers(teamB, p3, batterView, isTeam1);
            fetchPlayers(teamA, p4, bowlerView, isTeam1);
        }
    }

    private void fetchPlayers(String team, ArrayList<String> playerList, MaterialAutoCompleteTextView playerView, boolean addToMatchPlayerItems) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Teams").child(team).child("players");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                playerList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String parent = String.valueOf(dataSnapshot.child("parent").getValue());
                    FirebaseDatabase.getInstance().getReference("Profile").child(parent)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot profileSnapshot) {
                                    String first = String.valueOf(profileSnapshot.child("first").getValue());
                                    String last = String.valueOf(profileSnapshot.child("last").getValue());
                                    String image = String.valueOf(profileSnapshot.child("image").getValue());
                                    String parentId = String.valueOf(profileSnapshot.child("parent").getValue());
                                    String name = first + " " + last;
                                    playerList.add(name);
                                    if (addToMatchPlayerItems) {
                                        matchPlayerItems.add(new MatchPlayerList(first, last, image, parentId));
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.drop_down_feed_item, playerList);
                                    playerView.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void uploadMatch() {
        if (isInputValid()) {
            String parent = "-" + new Date().getTime();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Match").child(parent);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("details", match_details.getText().toString());
            hashMap.put("result", match_result.getText().toString());
            hashMap.put("scorecard", "none");
            hashMap.put("team1_name", team1.getText().toString());
            hashMap.put("team2_name", team2.getText().toString());
            hashMap.put("parent", parent);
            hashMap.put("team1_score", score_team1.getText().toString() + " (" + overs_team1.getText().toString() + ")");
            hashMap.put("team2_score", score_team2.getText().toString() + " (" + overs_team2.getText().toString() + ")");

            putPlayerStats(hashMap, batter1, "top_team1_name", "top_team1_image", "top_team1_score", score_batter1, balls_batter1, out_batter1.isChecked());
            putPlayerStats(hashMap, bowler1, "top2_team1_name", "top2_team1_image", "top2_team1_score", wickets_bowler1, runs_bowler1, overs_bowler1);
            putPlayerStats(hashMap, batter2, "top_team2_name", "top_team2_image", "top_team2_score", score_batter2, balls_batter2, out_batter2.isChecked());
            putPlayerStats(hashMap, bowler2, "top2_team2_name", "top2_team2_image", "top2_team2_score", wickets_bowler2, runs_bowler2, overs_bowler2);

            reference.setValue(hashMap).addOnCompleteListener(task -> {
                Toast.makeText(getContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        } else {
            Toast.makeText(getContext(), "Enter details", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isInputValid() {
        return !score_team1.getText().toString().isEmpty() &&
                !score_team2.getText().toString().isEmpty() &&
                !overs_team1.getText().toString().isEmpty() &&
                !overs_team2.getText().toString().isEmpty() &&
                !score_batter1.getText().toString().isEmpty() &&
                !score_batter2.getText().toString().isEmpty() &&
                !balls_batter1.getText().toString().isEmpty() &&
                !balls_batter2.getText().toString().isEmpty() &&
                !wickets_bowler1.getText().toString().isEmpty() &&
                !wickets_bowler2.getText().toString().isEmpty() &&
                !runs_bowler1.getText().toString().isEmpty() &&
                !runs_bowler2.getText().toString().isEmpty() &&
                !overs_bowler1.getText().toString().isEmpty() &&
                !overs_bowler2.getText().toString().isEmpty() &&
                !match_details.getText().toString().isEmpty() &&
                !match_result.getText().toString().isEmpty();
    }

    private void putPlayerStats(HashMap<String, String> hashMap, MaterialAutoCompleteTextView playerView,
                               String nameKey, String imageKey, String scoreKey,
                               TextInputEditText stat1, TextInputEditText stat2, boolean isBatter) {
        for (MatchPlayerList player : matchPlayerItems) {
            String fullName = player.getFirst() + " " + player.getLast();
            if (fullName.equals(playerView.getText().toString())) {
                hashMap.put(nameKey, fullName);
                hashMap.put(imageKey, player.getParent());
                if (isBatter) {
                    hashMap.put(scoreKey, stat1.getText().toString() + "* (" + stat2.getText().toString() + ")");
                } else {
                    hashMap.put(scoreKey, stat1.getText().toString() + " (" + stat2.getText().toString() + ")");
                }
                break;
            }
        }
    }

    private void putPlayerStats(HashMap<String, String> hashMap, MaterialAutoCompleteTextView playerView,
                               String nameKey, String imageKey, String scoreKey,
                               TextInputEditText wickets, TextInputEditText runs, TextInputEditText overs) {
        for (MatchPlayerList player : matchPlayerItems) {
            String fullName = player.getFirst() + " " + player.getLast();
            if (fullName.equals(playerView.getText().toString())) {
                hashMap.put(nameKey, fullName);
                hashMap.put(imageKey, player.getParent());
                hashMap.put(scoreKey, wickets.getText().toString() + "-" + runs.getText().toString() + " (" + overs.getText().toString() + ")");
                break;
            }
        }
    }
}
