package com.fizanyatik.sportsclub.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class NorthernSuperchargers extends AppCompatActivity {
    LinearProgressIndicator progress1, progress2, progress3;
    CircularProgressIndicator circle_progress;
    CircleImageView aryan_pic, aditi_pic, prateek_pic;
    ConstraintLayout aryan, prateek, aditi;
    TextView runs, match, wicket, wins;
    ImageView nsc_back;
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
        setContentView(R.layout.activity_northern_superchargers);

        progress1 = findViewById(R.id.progress1);
        progress2 = findViewById(R.id.progress2);
        progress3 = findViewById(R.id.progress3);
        circle_progress = findViewById(R.id.circle_progress);

        aryan_pic = findViewById(R.id.aryan_pic_ns);
        aditi_pic = findViewById(R.id.aditi_pic_ns);
        prateek_pic = findViewById(R.id.prateek_pic_ns);

        match = findViewById(R.id.match);
        runs = findViewById(R.id.runs);
        wicket = findViewById(R.id.wicket);
        wins = findViewById(R.id.wins);

        aryan = findViewById(R.id.aryan);
        prateek = findViewById(R.id.prateek);
        aditi = findViewById(R.id.aditi);

        aryan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aryan.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(NorthernSuperchargers.this, AryanSrivastava.class));
            }
        });

        aditi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aditi.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(NorthernSuperchargers.this, AditiSrivastava.class));
            }
        });

        prateek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prateek.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(NorthernSuperchargers.this, PrateekSinha.class));
            }
        });

        nsc_back = findViewById(R.id.nsc_back);
        nsc_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nsc_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        FirebaseDatabase.getInstance().getReference("Teams").child("NSC").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String match_str = snapshot.child("match").getValue().toString();
                String runs_str = snapshot.child("runs").getValue().toString();
                String wicket_str = snapshot.child("wicket").getValue().toString();
                String wins_str = snapshot.child("wins").getValue().toString();

                match.setText(match_str);
                runs.setText(runs_str);
                wicket.setText(wicket_str);
                wins.setText(wins_str + "%");

                progress1.setProgress(Integer.parseInt(match_str)*2);
                progress2.setProgress(Integer.parseInt(runs_str)/10);
                progress3.setProgress(Integer.parseInt(wicket_str));
                circle_progress.setProgress(Integer.parseInt(wins_str));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Players").child("aditisrivastava").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue().toString();
                Glide.with(NorthernSuperchargers.this).load(Uri.parse(image)).into(aditi_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Players").child("aryansrivastava").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue().toString();
                Glide.with(NorthernSuperchargers.this).load(Uri.parse(image)).into(aryan_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Players").child("prateeksinha").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue().toString();
                Glide.with(NorthernSuperchargers.this).load(Uri.parse(image)).into(prateek_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}