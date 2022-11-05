package com.fizanyatik.sportsclub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("Themes", MODE_PRIVATE);
        String themeMode = prefs.getString("current", "");
        switch (themeMode){
            case "":
            case "Main":
                setTheme(R.style.Theme_Splash_Main);
                break;
            case "Blue":
                setTheme(R.style.Theme_Splash_Blue);
                break;
            case "Yellow":
                setTheme(R.style.Theme_Splash_Yellow);
                break;
            case "Pink":
                setTheme(R.style.Theme_Splash_Pink);
                break;
            case "Green":
                setTheme(R.style.Theme_Splash_Green);
                break;
            case "Teal":
                setTheme(R.style.Theme_Splash_Teal);
                break;
            case "Purple":
                setTheme(R.style.Theme_Splash_Purple);
                break;
            case "Red":
                setTheme(R.style.Theme_Splash_Red);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();

        int SPLASH_DISPLAY_LENGTH = 1000;
        if (firebaseAuth != null) {
            new Handler().postDelayed(() -> {
                Intent mainIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(mainIntent);
                MainActivity.this.finish();

            }, SPLASH_DISPLAY_LENGTH);
        } else {
            new Handler().postDelayed(() -> {
                Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                MainActivity.this.finish();
            }, SPLASH_DISPLAY_LENGTH);
        }

    }
}