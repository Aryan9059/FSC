package com.fizanyatik.sportsclub.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.os.Handler;
import com.fizanyatik.sportsclub.R;
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
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);

        FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseAuth != null) {
                Intent mainIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(mainIntent);
                MainActivity.this.finish();
        } else {
                Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                MainActivity.this.finish();
        }

    }
}