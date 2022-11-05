package com.fizanyatik.sportsclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.Fragment.FeedFragment;
import com.fizanyatik.sportsclub.Fragment.HomeFragment;
import com.fizanyatik.sportsclub.Fragment.MatchFragment;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    ImageView settings_btn;
    BottomNavigationView btm;
    String email, username;
    DatabaseReference reference;
    CollapsingToolbarLayout ctl;
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
        setContentView(R.layout.activity_home);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.navigation, typedValue,true);
        getWindow().setNavigationBarColor(typedValue.data);

        settings_btn = findViewById(R.id.settings_btn);
        ctl = findViewById(R.id.ctl_home);
        btm = findViewById(R.id.bottom_nav);
        btm.setOnItemSelectedListener(this);

        loadFragment(new HomeFragment());

        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings_btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        username = email.replace("@fsc.com", "");

        FirebaseDatabase.getInstance().getReference("Players").child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String stats_str = snapshot.child("stats").getValue().toString();
                if (!snapshot.child("version").getValue().toString().equals(BuildConfig.VERSION_NAME)){
                    reference = FirebaseDatabase.getInstance().getReference("Players").child(username);                        HashMap<String, Object> map = new HashMap<>();
                    map.put("version", BuildConfig.VERSION_NAME);
                    reference.updateChildren(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseDatabase.getInstance().getReference("Update").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("version").getValue().toString().equals(BuildConfig.VERSION_NAME)){
                    MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(HomeActivity.this);
                    alertDialog1.setTitle("Update");
                    alertDialog1.setMessage("A new version of FSC App is available to download.");
                    alertDialog1.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(snapshot.child("website").getValue().toString())));
                                    dialog.dismiss();
                                }
                            }).setCancelable(true)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    alertDialog1.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        switch (item.getItemId()){

            case R.id.bottom_home:
                fragment = new HomeFragment();
                ctl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                ctl.setTitle("Home");
                break;

            case R.id.bottom_matches:
                fragment = new MatchFragment();
                ctl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                ctl.setTitle("Matches");
                break;

            case R.id.bottom_feeds:
                fragment = new FeedFragment();
                ctl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                ctl.setTitle("Feeds");
                break;

        }
        return loadFragment(fragment);
    }


    public boolean loadFragment(Fragment fragment){

        if (fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }

        return true;
    }

    @Override
    public void onBackPressed(){

        if (btm.getSelectedItemId() == R.id.bottom_home) {
            super.onBackPressed();
            finish();
        } else {
            btm.setSelectedItemId(R.id.bottom_home);
            ctl.setTitle("Home");
        }

    }

}