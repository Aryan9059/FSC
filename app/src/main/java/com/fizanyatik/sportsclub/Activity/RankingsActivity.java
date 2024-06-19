package com.fizanyatik.sportsclub.Activity;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.fizanyatik.sportsclub.Adapter.RankAdapter;
import com.fizanyatik.sportsclub.List.RankList;
import com.fizanyatik.sportsclub.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class RankingsActivity extends AppCompatActivity {
    TextView team1_name, team1_points, team2_name, team2_points;
    ConstraintLayout ns_cl, sbr_cl;
    ImageView rankings_back;
    CircleImageView team1_image, team2_image;
    RecyclerView batter_rank_recyclerview, bowler_rank_recyclerview, all_rank_recyclerview;
    RecyclerView.Adapter batter_rankAdapter, bowler_rankAdapter, all_rankAdapter;
    private List<RankList> rank_items_bat, rank_items_bowl, rank_items_all;
    RankList rankList_bat, rankList_bowl, rankList_all;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("Themes", MODE_PRIVATE);
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
        setContentView(R.layout.activity_rankings);

        rank_items_bat = new ArrayList<>();
        rank_items_bowl = new ArrayList<>();
        rank_items_all = new ArrayList<>();

        rankings_back = findViewById(R.id.rankings_back);
        rankings_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rankings_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        TypedValue typedValue = new TypedValue();
        TypedValue typedValue2 = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.navigation, typedValue,true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            theme.resolveAttribute(R.attr.bar_background, typedValue,true);
            theme.resolveAttribute(R.attr.screen_background, typedValue2,true);
            getWindow().setStatusBarColor(typedValue.data);
            getWindow().setNavigationBarColor(typedValue2.data);
        }

        team1_image = findViewById(R.id.team1_image);
        team1_name = findViewById(R.id.rank_name);
        team1_points = findViewById(R.id.rank_points);
        team2_image = findViewById(R.id.team2_image);
        team2_name = findViewById(R.id.team2_name);
        team2_points = findViewById(R.id.team2_points);

        ns_cl = findViewById(R.id.ns_cl);
        ns_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankingsActivity.this, TeamActivity.class);
                intent.putExtra("team", "NSC");
                startActivity(intent);
            }
        });

        sbr_cl = findViewById(R.id.sb_cl);
        sbr_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankingsActivity.this, TeamActivity.class);
                intent.putExtra("team", "SBR");
                startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference("Ranking").child("Team").addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String team1_name_str = snapshot.child("1").child("name").getValue().toString();
                String team2_name_str = snapshot.child("2").child("name").getValue().toString();
                String team1_points_str = snapshot.child("1").child("points").getValue().toString();
                String team2_points_str = snapshot.child("2").child("points").getValue().toString();

                if (team1_name_str.equals("Northern Superchargers")){
                    team1_image.setImageDrawable(getResources().getDrawable(R.drawable.nsc_logo));
                } else {
                    team1_image.setImageDrawable(getResources().getDrawable(R.drawable.sbr_logo));
                }

                if (team2_name_str.equals("Northern Superchargers")){
                    team2_image.setImageDrawable(getResources().getDrawable(R.drawable.nsc_logo));
                } else {
                    team2_image.setImageDrawable(getResources().getDrawable(R.drawable.sbr_logo));
                }

                team1_name.setText(team1_name_str);
                team2_name.setText(team2_name_str);
                team1_points.setText("Win Percentage: " + team1_points_str);
                team2_points.setText("Win Percentage: " + team2_points_str);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        batter_rank_recyclerview = findViewById(R.id.batter_rv);
        batter_rank_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        batter_rank_recyclerview.setNestedScrollingEnabled(false);

        bowler_rank_recyclerview = findViewById(R.id.bowler_rv);
        bowler_rank_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        bowler_rank_recyclerview.setNestedScrollingEnabled(false);

        all_rank_recyclerview = findViewById(R.id.all_rv);
        all_rank_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        all_rank_recyclerview.setNestedScrollingEnabled(false);

        FirebaseDatabase.getInstance().getReference("Profile").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rank_items_bat.clear();
                rank_items_bowl.clear();
                rank_items_all.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(dataSnapshot.child("type").getValue().toString().equals("player")){
                        if (dataSnapshot.child("stats").getValue().toString().equals("yes")){

                            String name = dataSnapshot.child("first").getValue().toString() + " " + dataSnapshot.child("last").getValue().toString();
                            String image = dataSnapshot.child("image").getValue().toString();
                            String runs_str = dataSnapshot.child("stats_runs").getValue().toString();
                            String wicket_str = dataSnapshot.child("stats_wicket").getValue().toString();
                            String average_str = dataSnapshot.child("stats_average").getValue().toString();
                            String strike_str = dataSnapshot.child("stats_strike").getValue().toString();
                            String economy_str = dataSnapshot.child("stats_economy").getValue().toString();

                            float points = (Float.parseFloat(runs_str)/500)*40 + (Float.parseFloat(strike_str)/200)*30 + (Float.parseFloat(average_str)/100)*30;
                            float bowling_points = (Float.parseFloat(wicket_str)/50)*50 + (1-(Float.parseFloat(economy_str)/20))*50;
                            float all_points = points/2 + bowling_points/2;

                            DecimalFormat df = new DecimalFormat("##.##");

                            rankList_bat = new RankList(name, image, 1, Float.parseFloat(df.format(points)));
                            rank_items_bat.add(rankList_bat);

                            rankList_bowl = new RankList(name, image, 1, Float.parseFloat(df.format(bowling_points)));
                            rank_items_bowl.add(rankList_bowl);

                            rankList_all = new RankList(name, image, 1, Float.parseFloat(df.format(all_points)));
                            rank_items_all.add(rankList_all);
                        }
                    }
                }
                Collections.sort(rank_items_bat, new Comparator<RankList>() {
                    @Override
                    public int compare(RankList o1, RankList o2) {
                        return Float.compare(o1.getPoints(), o2.getPoints());
                    }
                });
                Collections.reverse(rank_items_bat);
                int i = 0;
                while (i < rank_items_bat.size()){
                    rank_items_bat.get(i).setRank(i+1);
                    i++;
                }
                batter_rankAdapter = new RankAdapter(rank_items_bat, getApplicationContext());
                batter_rank_recyclerview.setAdapter(batter_rankAdapter);
                batter_rankAdapter.notifyDataSetChanged();

                Collections.sort(rank_items_bowl, new Comparator<RankList>() {
                    @Override
                    public int compare(RankList o1, RankList o2) {
                        return Float.compare(o1.getPoints(), o2.getPoints());
                    }
                });
                Collections.reverse(rank_items_bowl);
                int i1 = 0;
                while (i1 < rank_items_bowl.size()){
                    rank_items_bowl.get(i1).setRank(i1+1);
                    i1++;
                }
                bowler_rankAdapter = new RankAdapter(rank_items_bowl, getApplicationContext());
                bowler_rank_recyclerview.setAdapter(bowler_rankAdapter);
                bowler_rankAdapter.notifyDataSetChanged();

                Collections.sort(rank_items_all, new Comparator<RankList>() {
                    @Override
                    public int compare(RankList o1, RankList o2) {
                        return Float.compare(o1.getPoints(), o2.getPoints());
                    }
                });
                Collections.reverse(rank_items_all);
                int i2 = 0;
                while (i2 < rank_items_all.size()){
                    rank_items_all.get(i2).setRank(i2+1);
                    i2++;
                }
                all_rankAdapter = new RankAdapter(rank_items_all, getApplicationContext());
                all_rank_recyclerview.setAdapter(all_rankAdapter);
                all_rankAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}