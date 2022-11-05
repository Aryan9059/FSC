package com.fizanyatik.sportsclub;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Design extends AppCompatActivity {
    SharedPreferences prefs;
    MaterialCardView main_cv, yellow_cv, blue_cv, red_cv, green_cv, purple_cv, teal_cv, pink_cv;
    ImageView main_tick, yellow_tick, blue_tick, red_tick, green_tick, purple_tick, teal_tick, pink_tick;
    String username, email;

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
        setContentView(R.layout.activity_design);

        main_cv = findViewById(R.id.main_cv);
        yellow_cv = findViewById(R.id.yellow_cv);
        blue_cv = findViewById(R.id.blue_cv);
        red_cv = findViewById(R.id.red_cv);
        green_cv = findViewById(R.id.green_cv);
        purple_cv = findViewById(R.id.purple_cv);
        teal_cv = findViewById(R.id.teal_cv);
        pink_cv = findViewById(R.id.pink_cv);

        main_tick = findViewById(R.id.main_tick);
        yellow_tick = findViewById(R.id.yellow_tick);
        blue_tick = findViewById(R.id.blue_tick);
        red_tick = findViewById(R.id.red_tick);
        green_tick = findViewById(R.id.green_tick);
        purple_tick = findViewById(R.id.purple_tick);
        teal_tick = findViewById(R.id.teal_tick);
        pink_tick = findViewById(R.id.pink_tick);

        CircleImageView profile_picture = findViewById(R.id.profile_picture_design);

        ImageView design_back = findViewById(R.id.design_back);
        design_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                design_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        TextView version_tv = findViewById(R.id.version_tv_design);
        version_tv.setText("Currently using v" + BuildConfig.VERSION_NAME);

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        username = email.replace("@fsc.com", "");
        FirebaseDatabase.getInstance().getReference("Players").child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue().toString();
                try {
                    Glide.with(Design.this).load(Uri.parse(image)).into(profile_picture);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        main_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Main");
                editor.commit();
                Toast.makeText(Design.this, "Default theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Design.this, HomeActivity.class));
                finishAffinity();
            }
        });

        blue_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blue_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Blue");
                editor.commit();
                Toast.makeText(Design.this, "Blue theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Design.this, HomeActivity.class));
                finishAffinity();
            }
        });

        yellow_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yellow_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Yellow");
                editor.commit();
                Toast.makeText(Design.this, "Yellow theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Design.this, HomeActivity.class));
                finishAffinity();
            }
        });

        pink_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pink_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Pink");
                editor.commit();
                Toast.makeText(Design.this, "Pink theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Design.this, HomeActivity.class));
                finishAffinity();
            }
        });

        green_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Green");
                editor.commit();
                Toast.makeText(Design.this, "Green theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Design.this, HomeActivity.class));
                finishAffinity();
            }
        });

        teal_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teal_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Teal");
                editor.commit();
                Toast.makeText(Design.this, "Teal theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Design.this, HomeActivity.class));
                finishAffinity();
            }
        });

        purple_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purple_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Purple");
                editor.commit();
                Toast.makeText(Design.this, "Purple theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Design.this, HomeActivity.class));
                finishAffinity();
            }
        });

        red_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                red_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Red");
                editor.commit();
                Toast.makeText(Design.this, "Red theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Design.this, HomeActivity.class));
                finishAffinity();
            }
        });

        switch (themeMode) {
            case "":
            case "Main":
                main_cv.setClickable(false);
                main_tick.setVisibility(View.VISIBLE);
                break;
            case "Blue":
                blue_cv.setClickable(false);
                blue_tick.setVisibility(View.VISIBLE);
                break;
            case "Yellow":
                yellow_cv.setClickable(false);
                yellow_tick.setVisibility(View.VISIBLE);
                break;
            case "Pink":
                pink_cv.setClickable(false);
                pink_tick.setVisibility(View.VISIBLE);
                break;
            case "Green":
                green_cv.setClickable(false);
                green_tick.setVisibility(View.VISIBLE);
                break;
            case "Teal":
                teal_cv.setClickable(false);
                teal_tick.setVisibility(View.VISIBLE);
                break;
            case "Purple":
                purple_cv.setClickable(false);
                purple_tick.setVisibility(View.VISIBLE);
                break;
            case "Red":
                red_cv.setClickable(false);
                red_tick.setVisibility(View.VISIBLE);
                break;
        }

    }
}