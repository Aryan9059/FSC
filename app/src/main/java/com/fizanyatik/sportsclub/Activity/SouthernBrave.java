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

public class SouthernBrave extends AppCompatActivity {
    LinearProgressIndicator progress1, progress2, progress3;
    CircularProgressIndicator circle_progress;
    CircleImageView pranjal_pic, pushkar_pic, shikhar_pic;
    ConstraintLayout pranjal, pushkar, shikhar;
    TextView runs, match, wicket, wins;
    ImageView sbr_back;
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
        setContentView(R.layout.activity_southern_brave);

        progress1 = findViewById(R.id.progress1);
        progress2 = findViewById(R.id.progress2);
        progress3 = findViewById(R.id.progress3);
        circle_progress = findViewById(R.id.circle_progress);

        pranjal_pic = findViewById(R.id.pranjal_pic_sb);
        pushkar_pic = findViewById(R.id.pushkar_pic_sb);
        shikhar_pic = findViewById(R.id.shikhar_pic_sb);

        match = findViewById(R.id.match);
        runs = findViewById(R.id.runs);
        wicket = findViewById(R.id.wicket);
        wins = findViewById(R.id.wins);

        pranjal = findViewById(R.id.pranjal);
        pushkar = findViewById(R.id.pushkar);
        shikhar = findViewById(R.id.shikhar);

        pranjal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pranjal.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(SouthernBrave.this, PranjalSingh.class));
            }
        });

        pushkar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushkar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(SouthernBrave.this, PraveenPushkar.class));
            }
        });

        shikhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shikhar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(SouthernBrave.this, ShikharSinha.class));
            }
        });

        sbr_back = findViewById(R.id.sbr_back);
        sbr_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sbr_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        FirebaseDatabase.getInstance().getReference("Teams").child("SBR").addValueEventListener(new ValueEventListener() {
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

        FirebaseDatabase.getInstance().getReference("Players").child("pranjalsingh").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue().toString();
                Glide.with(SouthernBrave.this).load(Uri.parse(image)).into(pranjal_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Players").child("praveenpushkar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue().toString();
                Glide.with(SouthernBrave.this).load(Uri.parse(image)).into(pushkar_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Players").child("shikharsinha").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue().toString();
                Glide.with(SouthernBrave.this).load(Uri.parse(image)).into(shikhar_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}