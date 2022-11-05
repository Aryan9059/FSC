package com.fizanyatik.sportsclub.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrateekSinha extends AppCompatActivity {
    ImageView aryan_back;
    MaterialCardView national_team, ipl_team, player, certificate_card;
    LinearProgressIndicator progress1, progress2, progress3, progress4;
    TextView match, runs, wicket, average, strike, economy;
    ImageView profile_pic;
    CircularProgressIndicator circle_progress, circle_progress1;
    Button social1_btn, social2_btn;
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
        setContentView(R.layout.activity_prateek_sinha);

        progress1 = findViewById(R.id.progress1);
        progress2 = findViewById(R.id.progress2);
        progress3 = findViewById(R.id.progress3);
        progress4 = findViewById(R.id.progress4);
        circle_progress = findViewById(R.id.circle_progress);
        circle_progress1 = findViewById(R.id.circle_progress1);
        profile_pic = findViewById(R.id.prateek_pic);

        national_team = findViewById(R.id.national_team_cv);
        ipl_team = findViewById(R.id.ipl_team_cv);
        player = findViewById(R.id.player_cv);

        social1_btn = findViewById(R.id.social1_btn);
        social2_btn = findViewById(R.id.social2_btn);

        social1_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/PSOP_19"));
                startActivity(intent);
            }
        });

        social2_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/prateeksinha_19/"));
                startActivity(intent);
            }
        });

        national_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bcci.tv/"));
                startActivity(intent);
            }
        });

        ipl_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.chennaisuperkings.com/"));
                startActivity(intent);
            }
        });

        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cricbuzz.com/profiles/265/ms-dhoni"));
                startActivity(intent);
            }
        });

        match = findViewById(R.id.match);
        runs = findViewById(R.id.runs);
        wicket = findViewById(R.id.wicket);
        average = findViewById(R.id.average);
        strike = findViewById(R.id.strike);
        economy = findViewById(R.id.economy);

        certificate_card = findViewById(R.id.certificate_card);
        certificate_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/drive/folders/18EpyCAR_tSAtEQkJ1ocCLOvqfsKYe310"));
                startActivity(intent);
            }
        });

        aryan_back = findViewById(R.id.aryan_back);
        aryan_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aryan_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        FirebaseDatabase.getInstance().getReference("Players").child("prateeksinha").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String match_str = snapshot.child("match").getValue().toString();
                String runs_str = snapshot.child("runs").getValue().toString();
                String wicket_str = snapshot.child("wicket").getValue().toString();
                String average_str = snapshot.child("average").getValue().toString();
                String strike_str = snapshot.child("strike").getValue().toString();
                String economy_str = snapshot.child("economy").getValue().toString();
                String image = snapshot.child("image").getValue().toString();

                Glide.with(PrateekSinha.this).load(Uri.parse(image)).into(profile_pic);

                match.setText(match_str);
                runs.setText(runs_str);
                wicket.setText(wicket_str);
                average.setText(average_str);
                strike.setText(strike_str);
                economy.setText(economy_str);

                float average_fl = Float.parseFloat(average_str);
                float strike_fl = Float.parseFloat(strike_str);
                float economy_fl = Float.parseFloat(economy_str)*5;

                progress1.setProgress(Integer.parseInt(match_str)*2);
                progress2.setProgress(Integer.parseInt(runs_str)/10);
                progress3.setProgress(Integer.parseInt(wicket_str)*2);
                progress4.setProgress(Math.round(average_fl));
                circle_progress.setProgress(Math.round(strike_fl)/2);
                circle_progress1.setProgress(Math.round(economy_fl));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}