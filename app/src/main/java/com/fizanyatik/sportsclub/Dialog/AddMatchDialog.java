package com.fizanyatik.sportsclub.Dialog;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.fizanyatik.sportsclub.List.MatchPlayerList;
import com.fizanyatik.sportsclub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    ImageView match_back;
    String batter1_str, batter2_str, batter1_score_final_str, batter2_score_final_str, batter1_parent_str, batter2_parent_str;
    String bowler1_str, bowler2_str, bowler1_score_final_str, bowler2_score_final_str, bowler1_parent_str, bowler2_parent_str;
    Button add_match_btn;
    MatchPlayerList matchPlayerList;
    List<MatchPlayerList> matchPlayerItems;
    ArrayList<String> players, players2, players3, players4;
    DatabaseReference reference;
    MaterialSwitch out_batter1, out_batter2;
    MaterialAutoCompleteTextView team1, team2, batter1, batter2, bowler1, bowler2;
    TextInputEditText score_team1, score_team2, overs_team1, overs_team2, score_batter1, score_batter2, balls_batter1,
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
        players = new ArrayList<>();
        players2 = new ArrayList<>();
        players3 = new ArrayList<>();
        players4 = new ArrayList<>();
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
        matchPlayerItems = new ArrayList<>();

        String[] type = getResources().getStringArray(R.array.team_match);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, type);
        team1.setAdapter(arrayAdapter);
        team2.setAdapter(arrayAdapter);

        match_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                match_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                dismiss();
            }
        });

        team1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (team1.getText().toString().equals("NSC")){
                    reference = FirebaseDatabase.getInstance().getReference("Teams").child("NSC").child("players");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            players.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String parent = dataSnapshot.child("parent").getValue().toString();

                                FirebaseDatabase.getInstance().getReference("Profile").child(parent).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        matchPlayerList = new MatchPlayerList(snapshot.child("first").getValue().toString(), snapshot.child("last").getValue().toString(), snapshot.child("image").getValue().toString(), snapshot.child("parent").getValue().toString());
                                        matchPlayerItems.add(matchPlayerList);
                                        String name = snapshot.child("first").getValue().toString() + " " + snapshot.child("last").getValue().toString();
                                        players.add(name);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            ArrayAdapter arrayAdapter4 = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, players);
                            batter1.setAdapter(arrayAdapter4);
                            FirebaseDatabase.getInstance().getReference("Teams").child("SBR").child("players").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    players2.clear();
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        String parent = dataSnapshot.child("parent").getValue().toString();

                                        FirebaseDatabase.getInstance().getReference("Profile").child(parent).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                matchPlayerList = new MatchPlayerList(snapshot.child("first").getValue().toString(), snapshot.child("last").getValue().toString(), snapshot.child("image").getValue().toString(), snapshot.child("parent").getValue().toString());
                                                matchPlayerItems.add(matchPlayerList);
                                                String name = snapshot.child("first").getValue().toString() + " " + snapshot.child("last").getValue().toString();
                                                players2.add(name);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    ArrayAdapter arrayAdapter1 = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, players2);
                                    bowler1.setAdapter(arrayAdapter1);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    reference = FirebaseDatabase.getInstance().getReference("Teams").child("SBR").child("players");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            players3.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String parent = dataSnapshot.child("parent").getValue().toString();

                                FirebaseDatabase.getInstance().getReference("Profile").child(parent).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String name = snapshot.child("first").getValue().toString() + " " + snapshot.child("last").getValue().toString();
                                        players3.add(name);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            ArrayAdapter arrayAdapter3 = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, players3);
                            batter1.setAdapter(arrayAdapter3);
                            FirebaseDatabase.getInstance().getReference("Teams").child("NSC").child("players").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    players4.clear();
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        String parent = dataSnapshot.child("parent").getValue().toString();

                                        FirebaseDatabase.getInstance().getReference("Profile").child(parent).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String name = snapshot.child("first").getValue().toString() + " " + snapshot.child("last").getValue().toString();
                                                players4.add(name);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    ArrayAdapter arrayAdapter1 = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, players4);
                                    bowler1.setAdapter(arrayAdapter1);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        team2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (team2.getText().toString().equals("NSC")){
                    reference = FirebaseDatabase.getInstance().getReference("Teams").child("NSC").child("players");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            players.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String parent = dataSnapshot.child("parent").getValue().toString();

                                FirebaseDatabase.getInstance().getReference("Profile").child(parent).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String name = snapshot.child("first").getValue().toString() + " " + snapshot.child("last").getValue().toString();
                                        players.add(name);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            ArrayAdapter arrayAdapter1 = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, players);
                            batter2.setAdapter(arrayAdapter1);
                            FirebaseDatabase.getInstance().getReference("Teams").child("SBR").child("players").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    players2.clear();
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        String parent = dataSnapshot.child("parent").getValue().toString();

                                        FirebaseDatabase.getInstance().getReference("Profile").child(parent).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String name = snapshot.child("first").getValue().toString() + " " + snapshot.child("last").getValue().toString();
                                                players2.add(name);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    ArrayAdapter arrayAdapter1 = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, players2);
                                    bowler2.setAdapter(arrayAdapter1);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    reference = FirebaseDatabase.getInstance().getReference("Teams").child("SBR").child("players");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            players3.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String parent = dataSnapshot.child("parent").getValue().toString();

                                FirebaseDatabase.getInstance().getReference("Profile").child(parent).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        String name = snapshot.child("first").getValue().toString() + " " + snapshot.child("last").getValue().toString();
                                        players3.add(name);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            ArrayAdapter arrayAdapter2 = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, players3);
                            batter2.setAdapter(arrayAdapter2);
                            FirebaseDatabase.getInstance().getReference("Teams").child("NSC").child("players").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    players4.clear();
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        String parent = dataSnapshot.child("parent").getValue().toString();

                                        FirebaseDatabase.getInstance().getReference("Profile").child(parent).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String name = snapshot.child("first").getValue().toString() + " " + snapshot.child("last").getValue().toString();
                                                players4.add(name);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    ArrayAdapter arrayAdapter1 = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, players4);
                                    bowler2.setAdapter(arrayAdapter1);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        add_match_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!score_team1.getText().toString().equals("") && !score_team2.getText().toString().equals("") && !overs_team1.getText().toString().equals("") && !overs_team2.getText().toString().equals("") && !score_batter1.getText().toString().equals("") && !score_batter2.getText().toString().equals("") &&
                        !balls_batter1.getText().toString().equals("") && !balls_batter2.getText().toString().equals("") && !wickets_bowler1.getText().toString().equals("") && !wickets_bowler2.getText().toString().equals("") &&
                        !runs_bowler1.getText().toString().equals("") && !runs_bowler2.getText().toString().equals("") && !overs_bowler1.getText().toString().equals("") &&
                        !overs_bowler2.getText().toString().equals("") && !match_details.getText().toString().equals("") && !match_result.getText().toString().equals("")){

                    final String parent = -new Date().getTime() + "";
                    reference = FirebaseDatabase.getInstance().getReference("Match").child(parent);
                        final HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("details", match_details.getText().toString());
                        hashMap.put("result", match_result.getText().toString());
                        hashMap.put("scorecard", "none");
                        hashMap.put("team1_name", team1.getText().toString());
                        hashMap.put("team2_name", team2.getText().toString());
                        hashMap.put("parent", parent);
                        hashMap.put("team1_score", score_team1.getText().toString() + " (" + overs_team1.getText().toString() + ")");
                        hashMap.put("team2_score", score_team2.getText().toString() + " (" + overs_team2.getText().toString() + ")");

                        for(int i = 0; i < matchPlayerItems.size(); i++){
                            if (matchPlayerItems.get(i).getFirst().equals(batter1.getText().toString())){
                                batter1_str = matchPlayerItems.get(i).getFirst() + " " + matchPlayerItems.get(i).getLast();
                                batter1_score_final_str = score_batter1.getText().toString() + "* (" + balls_batter1.getText().toString() + ")";
                                batter1_parent_str = matchPlayerItems.get(i).getParent();
                            }
                        }
                        hashMap.put("top_team1_name", batter1_str);
                        hashMap.put("top_team1_image", batter1_parent_str);
                        hashMap.put("top_team1_score", batter1_score_final_str);


                        for(int i = 0; i < matchPlayerItems.size(); i++){
                            if (matchPlayerItems.get(i).getFirst().equals(bowler1.getText().toString())){
                                bowler1_str = matchPlayerItems.get(i).getFirst() + " " + matchPlayerItems.get(i).getLast();
                                bowler1_score_final_str = wickets_bowler1.getText().toString() + "-" + runs_bowler1.getText().toString() + " (" + overs_bowler1.getText().toString() + ")";
                                bowler1_parent_str = matchPlayerItems.get(i).getParent();
                            }
                        }

                    hashMap.put("top2_team1_name", bowler1_str);
                    hashMap.put("top2_team1_image", bowler1_parent_str);
                    hashMap.put("top2_team1_score", bowler1_score_final_str);

                    for(int i = 0; i < matchPlayerItems.size(); i++){
                        if (matchPlayerItems.get(i).getFirst().equals(batter2.getText().toString())){
                            batter2_str = matchPlayerItems.get(i).getFirst() + " " + matchPlayerItems.get(i).getLast();
                            batter2_score_final_str = score_batter2.getText().toString() + "* (" + balls_batter2.getText().toString() + ")";
                            batter2_parent_str = matchPlayerItems.get(i).getParent();
                        }
                    }

                            hashMap.put("top_team2_name", batter2_str);
                            hashMap.put("top_team2_image", batter2_parent_str);
                            hashMap.put("top_team2_score", batter2_score_final_str);

                        for(int i = 0; i < matchPlayerItems.size(); i++){
                            if (matchPlayerItems.get(i).getFirst().equals(bowler2.getText().toString())){
                                bowler2_str = matchPlayerItems.get(i).getFirst() + " " + matchPlayerItems.get(i).getLast();
                                bowler2_score_final_str = wickets_bowler2.getText().toString() + "-" + runs_bowler2.getText().toString() + " (" + overs_bowler2.getText().toString() + ")";
                                bowler2_parent_str = matchPlayerItems.get(i).getParent();
                            }
                        }

                            hashMap.put("top2_team2_name", bowler2_str);
                            hashMap.put("top2_team2_image", bowler2_parent_str);
                            hashMap.put("top2_team2_score", bowler2_score_final_str);

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(getContext(), "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        });
                } else Toast.makeText(getContext(), "Enter details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
