package com.fizanyatik.sportsclub.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RankingsActivity extends AppCompatActivity {
    TextView team1_name, team1_points, team2_name, team2_points;
    ImageView team1_image, team2_image, rankings_back;
    TextView batter1_name, batter1_points, batter2_name, batter2_points, batter3_name, batter3_points, batter4_name, batter4_points, batter5_name, batter5_points, batter6_name, batter6_points;
    ImageView batter1_image, batter2_image, batter3_image, batter4_image, batter5_image, batter6_image;
    TextView bowler1_name, bowler1_points, bowler2_name, bowler2_points, bowler3_name, bowler3_points, bowler4_name, bowler4_points, bowler5_name, bowler5_points, bowler6_name, bowler6_points;
    ImageView bowler1_image, bowler2_image, bowler3_image, bowler4_image, bowler5_image, bowler6_image;
    TextView all1_name, all1_points, all2_name, all2_points, all3_name, all3_points, all4_name, all4_points, all5_name, all5_points, all6_name, all6_points;
    ImageView all1_image, all2_image, all3_image, all4_image, all5_image, all6_image;
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

        rankings_back = findViewById(R.id.rankings_back);
        rankings_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rankings_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        team1_image = findViewById(R.id.team1_image);
        team1_name = findViewById(R.id.team1_name);
        team1_points = findViewById(R.id.team1_points);
        team2_image = findViewById(R.id.team2_image);
        team2_name = findViewById(R.id.team2_name);
        team2_points = findViewById(R.id.team2_points);

        FirebaseDatabase.getInstance().getReference("Ranking").child("Team").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String team1_uri = snapshot.child("1").child("pic").getValue().toString();
                String team2_uri = snapshot.child("2").child("pic").getValue().toString();
                String team1_name_str = snapshot.child("1").child("name").getValue().toString();
                String team2_name_str = snapshot.child("2").child("name").getValue().toString();
                String team1_points_str = snapshot.child("1").child("points").getValue().toString();
                String team2_points_str = snapshot.child("2").child("points").getValue().toString();

                Glide.with(RankingsActivity.this).load(Uri.parse(team1_uri)).into(team1_image);
                Glide.with(RankingsActivity.this).load(Uri.parse(team2_uri)).into(team2_image);
                team1_name.setText(team1_name_str);
                team2_name.setText(team2_name_str);
                team1_points.setText("Win Percentage: " + team1_points_str);
                team2_points.setText("Win Percentage: " + team2_points_str);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        batter1_image = findViewById(R.id.batter1_image);
        batter2_image = findViewById(R.id.batter2_image);
        batter3_image = findViewById(R.id.batter3_image);
        batter4_image = findViewById(R.id.batter4_image);
        batter5_image = findViewById(R.id.batter5_image);
        batter6_image = findViewById(R.id.batter6_image);
        batter1_name = findViewById(R.id.batter1_name);
        batter2_name = findViewById(R.id.batter2_name);
        batter3_name = findViewById(R.id.batter3_name);
        batter4_name = findViewById(R.id.batter4_name);
        batter5_name = findViewById(R.id.batter5_name);
        batter6_name = findViewById(R.id.batter6_name);
        batter1_points = findViewById(R.id.batter1_points);
        batter2_points = findViewById(R.id.batter2_points);
        batter3_points = findViewById(R.id.batter3_points);
        batter4_points = findViewById(R.id.batter4_points);
        batter5_points = findViewById(R.id.batter5_points);
        batter6_points = findViewById(R.id.batter6_points);

        FirebaseDatabase.getInstance().getReference("Ranking").child("Batter").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                FirebaseDatabase.getInstance().getReference("Players").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshotImg) {
                        Uri image1 = Uri.parse(snapshotImg.child(snapshot.child("1").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image2 = Uri.parse(snapshotImg.child(snapshot.child("2").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image3 = Uri.parse(snapshotImg.child(snapshot.child("3").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image4 = Uri.parse(snapshotImg.child(snapshot.child("4").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image5 = Uri.parse(snapshotImg.child(snapshot.child("5").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image6 = Uri.parse(snapshotImg.child(snapshot.child("6").child("image").getValue().toString()).child("image").getValue().toString());

                        Glide.with(RankingsActivity.this).load(image1).into(batter1_image);
                        Glide.with(RankingsActivity.this).load(image2).into(batter2_image);
                        Glide.with(RankingsActivity.this).load(image3).into(batter3_image);
                        Glide.with(RankingsActivity.this).load(image4).into(batter4_image);
                        Glide.with(RankingsActivity.this).load(image5).into(batter5_image);
                        Glide.with(RankingsActivity.this).load(image6).into(batter6_image);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                batter1_name.setText(snapshot.child("1").child("name").getValue().toString());
                batter2_name.setText(snapshot.child("2").child("name").getValue().toString());
                batter3_name.setText(snapshot.child("3").child("name").getValue().toString());
                batter4_name.setText(snapshot.child("4").child("name").getValue().toString());
                batter5_name.setText(snapshot.child("5").child("name").getValue().toString());
                batter6_name.setText(snapshot.child("6").child("name").getValue().toString());

                batter1_points.setText(snapshot.child("1").child("points").getValue().toString());
                batter2_points.setText(snapshot.child("2").child("points").getValue().toString());
                batter3_points.setText(snapshot.child("3").child("points").getValue().toString());
                batter4_points.setText(snapshot.child("4").child("points").getValue().toString());
                batter5_points.setText(snapshot.child("5").child("points").getValue().toString());
                batter6_points.setText(snapshot.child("6").child("points").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bowler1_image = findViewById(R.id.bowler1_image);
        bowler2_image = findViewById(R.id.bowler2_image);
        bowler3_image = findViewById(R.id.bowler3_image);
        bowler4_image = findViewById(R.id.bowler4_image);
        bowler5_image = findViewById(R.id.bowler5_image);
        bowler6_image = findViewById(R.id.bowler6_image);
        bowler1_name = findViewById(R.id.bowler1_name);
        bowler2_name = findViewById(R.id.bowler2_name);
        bowler3_name = findViewById(R.id.bowler3_name);
        bowler4_name = findViewById(R.id.bowler4_name);
        bowler5_name = findViewById(R.id.bowler5_name);
        bowler6_name = findViewById(R.id.bowler6_name);
        bowler1_points = findViewById(R.id.bowler1_points);
        bowler2_points = findViewById(R.id.bowler2_points);
        bowler3_points = findViewById(R.id.bowler3_points);
        bowler4_points = findViewById(R.id.bowler4_points);
        bowler5_points = findViewById(R.id.bowler5_points);
        bowler6_points = findViewById(R.id.bowler6_points);

        FirebaseDatabase.getInstance().getReference("Ranking").child("Bowler").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                FirebaseDatabase.getInstance().getReference("Players").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshotImg) {
                        Uri image1 = Uri.parse(snapshotImg.child(snapshot.child("1").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image2 = Uri.parse(snapshotImg.child(snapshot.child("2").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image3 = Uri.parse(snapshotImg.child(snapshot.child("3").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image4 = Uri.parse(snapshotImg.child(snapshot.child("4").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image5 = Uri.parse(snapshotImg.child(snapshot.child("5").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image6 = Uri.parse(snapshotImg.child(snapshot.child("6").child("image").getValue().toString()).child("image").getValue().toString());

                        Glide.with(RankingsActivity.this).load(image1).into(bowler1_image);
                        Glide.with(RankingsActivity.this).load(image2).into(bowler2_image);
                        Glide.with(RankingsActivity.this).load(image3).into(bowler3_image);
                        Glide.with(RankingsActivity.this).load(image4).into(bowler4_image);
                        Glide.with(RankingsActivity.this).load(image5).into(bowler5_image);
                        Glide.with(RankingsActivity.this).load(image6).into(bowler6_image);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                bowler1_name.setText(snapshot.child("1").child("name").getValue().toString());
                bowler2_name.setText(snapshot.child("2").child("name").getValue().toString());
                bowler3_name.setText(snapshot.child("3").child("name").getValue().toString());
                bowler4_name.setText(snapshot.child("4").child("name").getValue().toString());
                bowler5_name.setText(snapshot.child("5").child("name").getValue().toString());
                bowler6_name.setText(snapshot.child("6").child("name").getValue().toString());

                bowler1_points.setText(snapshot.child("1").child("points").getValue().toString());
                bowler2_points.setText(snapshot.child("2").child("points").getValue().toString());
                bowler3_points.setText(snapshot.child("3").child("points").getValue().toString());
                bowler4_points.setText(snapshot.child("4").child("points").getValue().toString());
                bowler5_points.setText(snapshot.child("5").child("points").getValue().toString());
                bowler6_points.setText(snapshot.child("6").child("points").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        all1_image = findViewById(R.id.all1_image);
        all2_image = findViewById(R.id.all2_image);
        all3_image = findViewById(R.id.all3_image);
        all4_image = findViewById(R.id.all4_image);
        all5_image = findViewById(R.id.all5_image);
        all6_image = findViewById(R.id.all6_image);
        all1_name = findViewById(R.id.all1_name);
        all2_name = findViewById(R.id.all2_name);
        all3_name = findViewById(R.id.all3_name);
        all4_name = findViewById(R.id.all4_name);
        all5_name = findViewById(R.id.all5_name);
        all6_name = findViewById(R.id.all6_name);
        all1_points = findViewById(R.id.all1_points);
        all2_points = findViewById(R.id.all2_points);
        all3_points = findViewById(R.id.all3_points);
        all4_points = findViewById(R.id.all4_points);
        all5_points = findViewById(R.id.all5_points);
        all6_points = findViewById(R.id.all6_points);

        FirebaseDatabase.getInstance().getReference("Ranking").child("All").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                FirebaseDatabase.getInstance().getReference("Players").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshotImg) {
                        Uri image1 = Uri.parse(snapshotImg.child(snapshot.child("1").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image2 = Uri.parse(snapshotImg.child(snapshot.child("2").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image3 = Uri.parse(snapshotImg.child(snapshot.child("3").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image4 = Uri.parse(snapshotImg.child(snapshot.child("4").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image5 = Uri.parse(snapshotImg.child(snapshot.child("5").child("image").getValue().toString()).child("image").getValue().toString());
                        Uri image6 = Uri.parse(snapshotImg.child(snapshot.child("6").child("image").getValue().toString()).child("image").getValue().toString());

                        Glide.with(RankingsActivity.this).load(image1).into(all1_image);
                        Glide.with(RankingsActivity.this).load(image2).into(all2_image);
                        Glide.with(RankingsActivity.this).load(image3).into(all3_image);
                        Glide.with(RankingsActivity.this).load(image4).into(all4_image);
                        Glide.with(RankingsActivity.this).load(image5).into(all5_image);
                        Glide.with(RankingsActivity.this).load(image6).into(all6_image);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                all1_name.setText(snapshot.child("1").child("name").getValue().toString());
                all2_name.setText(snapshot.child("2").child("name").getValue().toString());
                all3_name.setText(snapshot.child("3").child("name").getValue().toString());
                all4_name.setText(snapshot.child("4").child("name").getValue().toString());
                all5_name.setText(snapshot.child("5").child("name").getValue().toString());
                all6_name.setText(snapshot.child("6").child("name").getValue().toString());

                all1_points.setText(snapshot.child("1").child("points").getValue().toString());
                all2_points.setText(snapshot.child("2").child("points").getValue().toString());
                all3_points.setText(snapshot.child("3").child("points").getValue().toString());
                all4_points.setText(snapshot.child("4").child("points").getValue().toString());
                all5_points.setText(snapshot.child("5").child("points").getValue().toString());
                all6_points.setText(snapshot.child("6").child("points").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}