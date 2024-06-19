package com.fizanyatik.sportsclub.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class DesignActivity extends AppCompatActivity {
    ConstraintLayout default_cl, yellow_cl, red_cl, pink_cl, purple_cl, blue_cl, teal_cl, green_cl;
    LinearLayout color1;
    TextView default_color_text;
    ImageView color2, color3;
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
        setContentView(R.layout.activity_design);

        color1 = findViewById(R.id.color1);
        color2 = findViewById(R.id.color2);
        color3 = findViewById(R.id.color3);
        default_color_text = findViewById(R.id.def_color_text);

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

        default_cl = findViewById(R.id.default_cl);
        yellow_cl = findViewById(R.id.yellow_cl);
        red_cl = findViewById(R.id.red_cl);
        pink_cl = findViewById(R.id.pink_cl);
        purple_cl = findViewById(R.id.purple_cl);
        blue_cl = findViewById(R.id.blue_cl);
        teal_cl = findViewById(R.id.teal_cl);
        green_cl = findViewById(R.id.green_cl);

        if (Build.VERSION.SDK_INT >= 31){
            default_cl.setVisibility(View.VISIBLE);
        }

        ImageView design_back = findViewById(R.id.design_back);
        design_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                design_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        default_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                default_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Main");
                editor.commit();
                Toast.makeText(DesignActivity.this, "Default theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DesignActivity.this, HomeActivity.class));
                finishAffinity();
            }
        });

        yellow_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yellow_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Yellow");
                editor.commit();
                Toast.makeText(DesignActivity.this, "Yellow theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DesignActivity.this, HomeActivity.class));
                finishAffinity();
            }
        });

        red_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                red_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Red");
                editor.commit();
                Toast.makeText(DesignActivity.this, "Red theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DesignActivity.this, HomeActivity.class));
                finishAffinity();
            }
        });

        pink_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pink_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Pink");
                editor.commit();
                Toast.makeText(DesignActivity.this, "Pink theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DesignActivity.this, HomeActivity.class));
                finishAffinity();
            }
        });

        purple_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purple_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Purple");
                editor.commit();
                Toast.makeText(DesignActivity.this, "Purple theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DesignActivity.this, HomeActivity.class));
                finishAffinity();
            }
        });

        blue_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blue_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Blue");
                editor.commit();
                Toast.makeText(DesignActivity.this, "Blue theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DesignActivity.this, HomeActivity.class));
                finishAffinity();
            }
        });

        teal_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teal_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Teal");
                editor.commit();
                Toast.makeText(DesignActivity.this, "Teal theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DesignActivity.this, HomeActivity.class));
                finishAffinity();
            }
        });

        green_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                green_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current", "Green");
                editor.commit();
                Toast.makeText(DesignActivity.this, "Green theme applied", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DesignActivity.this, HomeActivity.class));
                finishAffinity();
            }
        });

        switch (themeMode) {
            case "":
            case "Main":
                color1.setBackgroundColor(getResources().getColor(R.color.main1));
                color2.setBackgroundColor(getResources().getColor(R.color.main2));
                color3.setBackgroundColor(getResources().getColor(R.color.main3));
                default_color_text.setText("Default theme currently applied.");
                default_cl.setVisibility(View.GONE);
                break;
            case "Blue":
                color1.setBackgroundColor(getResources().getColor(R.color.blue1));
                color2.setBackgroundColor(getResources().getColor(R.color.blue2));
                color3.setBackgroundColor(getResources().getColor(R.color.blue3));
                default_color_text.setText("Blue theme currently applied.");
                blue_cl.setVisibility(View.GONE);
                break;
            case "Yellow":
                color1.setBackgroundColor(getResources().getColor(R.color.yellow1));
                color2.setBackgroundColor(getResources().getColor(R.color.yellow2));
                color3.setBackgroundColor(getResources().getColor(R.color.yellow3));
                default_color_text.setText("Yellow theme currently applied.");
                yellow_cl.setVisibility(View.GONE);
                break;
            case "Pink":
                color1.setBackgroundColor(getResources().getColor(R.color.pink1));
                color2.setBackgroundColor(getResources().getColor(R.color.pink2));
                color3.setBackgroundColor(getResources().getColor(R.color.pink3));
                default_color_text.setText("Pink theme currently applied.");
                pink_cl.setVisibility(View.GONE);
                break;
            case "Green":
                color1.setBackgroundColor(getResources().getColor(R.color.green1));
                color2.setBackgroundColor(getResources().getColor(R.color.green2));
                color3.setBackgroundColor(getResources().getColor(R.color.green3));
                default_color_text.setText("Green theme currently applied.");
                green_cl.setVisibility(View.GONE);
                break;
            case "Teal":
                color1.setBackgroundColor(getResources().getColor(R.color.teal1));
                color2.setBackgroundColor(getResources().getColor(R.color.teal2));
                color3.setBackgroundColor(getResources().getColor(R.color.teal3));
                default_color_text.setText("Teal theme currently applied.");
                teal_cl.setVisibility(View.GONE);
                break;
            case "Purple":
                color1.setBackgroundColor(getResources().getColor(R.color.purple1));
                color2.setBackgroundColor(getResources().getColor(R.color.purple2));
                color3.setBackgroundColor(getResources().getColor(R.color.purple3));
                default_color_text.setText("Purple theme currently applied.");
                purple_cl.setVisibility(View.GONE);
                break;
            case "Red":
                color1.setBackgroundColor(getResources().getColor(R.color.red1));
                color2.setBackgroundColor(getResources().getColor(R.color.red2));
                color3.setBackgroundColor(getResources().getColor(R.color.red3));
                default_color_text.setText("Red theme currently applied.");
                red_cl.setVisibility(View.GONE);
                break;
        }
    }
}