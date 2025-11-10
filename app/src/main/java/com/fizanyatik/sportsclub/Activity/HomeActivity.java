package com.fizanyatik.sportsclub.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.fizanyatik.sportsclub.Adapter.ViewPagerAdapter;
import com.fizanyatik.sportsclub.Dialog.AddFeedDialog;
import com.fizanyatik.sportsclub.Dialog.AddMatchDialog;
import com.fizanyatik.sportsclub.Fragment.FeedFragment;
import com.fizanyatik.sportsclub.Fragment.HomeFragment;
import com.fizanyatik.sportsclub.Fragment.MatchFragment;
import com.fizanyatik.sportsclub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    ImageView settings_btn, scoring_btn;
    BottomNavigationView btm;
    NavigationRailView railView;
    FloatingActionButton fab_btn;
    MaterialToolbar toolbar;
    String email, username, admin;
    DatabaseReference reference;
    CollapsingToolbarLayout ctl;
    SharedPreferences prefs;
    ViewPager2 pagerMain;
    ArrayList<Fragment> arr = new ArrayList<>();

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
        TypedValue typedValue2 = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.navigation, typedValue,true);
        getWindow().setNavigationBarColor(typedValue.data);

        notification();

        settings_btn = findViewById(R.id.settings_btn);
        scoring_btn = findViewById(R.id.scoring_btn);
        fab_btn = findViewById(R.id.fab_add);
        toolbar = findViewById(R.id.toolbar_home);
        pagerMain = findViewById(R.id.fragment_container_new);
        arr.add(new HomeFragment());
        arr.add(new MatchFragment());
        arr.add(new FeedFragment());

        scoring_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    scoring_btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.ganapathy.best.cricket.scorer");
                    startActivity(intent);
                } catch (Exception e){
                    Toast.makeText(HomeActivity.this, "App isn't installed", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            btm = findViewById(R.id.bottom_nav);
            ctl = findViewById(R.id.ctl_home);

            btm.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {

                        case R.id.bottom_home:
                            pagerMain.setCurrentItem(0);
                            btm.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            break;

                        case R.id.bottom_matches:
                            pagerMain.setCurrentItem(1);
                            btm.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            break;

                        case R.id.bottom_feeds:
                            pagerMain.setCurrentItem(2);
                            btm.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            break;

                    }
                    return false;
                }
            });

        } else {
            railView = findViewById(R.id.bottom_rail);
            theme.resolveAttribute(R.attr.bar_background, typedValue,true);
            theme.resolveAttribute(R.attr.screen_background, typedValue2,true);
            getWindow().setStatusBarColor(typedValue.data);
            getWindow().setNavigationBarColor(typedValue2.data);

            railView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {

                        case R.id.bottom_home:
                            pagerMain.setCurrentItem(0);
                            railView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            break;

                        case R.id.bottom_matches:
                            pagerMain.setCurrentItem(1);
                            railView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            break;

                        case R.id.bottom_feeds:
                            pagerMain.setCurrentItem(2);
                            railView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            break;

                    }
                    return false;
                }
            });


        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, arr);
        pagerMain.setAdapter(viewPagerAdapter);

        pagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                switch (position){

                    case 0:
                        scoring_btn.setVisibility(View.GONE);
                        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
                            btm.getMenu().getItem(0).setChecked(true);
                            ctl.setTitle("Home");
                        } else {
                            railView.getMenu().getItem(0).setChecked(true);
                            toolbar.setTitle("Home");
                        }
                        fab_btn.setVisibility(View.GONE);

                        break;

                    case 1:
                        scoring_btn.setVisibility(View.VISIBLE);
                        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
                            btm.getMenu().getItem(1).setChecked(true);
                            ctl.setTitle("Matches");
                        } else {
                            railView.getMenu().getItem(1).setChecked(true);
                            toolbar.setTitle("Matches");
                        }
                        reference = FirebaseDatabase.getInstance().getReference("Admin");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                admin = snapshot.getValue().toString();
                                if(admin.contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                    fab_btn.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;

                    case 2:
                        scoring_btn.setVisibility(View.GONE);
                        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
                            btm.getMenu().getItem(2).setChecked(true);
                            ctl.setTitle("Feeds");
                        } else {
                            railView.getMenu().getItem(2).setChecked(true);
                            toolbar.setTitle("Feeds");
                        }
                        reference = FirebaseDatabase.getInstance().getReference("Admin");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                admin = snapshot.getValue().toString();
                                if(admin.contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                    fab_btn.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;

                }
            }
        });

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pagerMain.getCurrentItem() == 1){
                    DialogFragment dialog = new AddMatchDialog();
                    dialog.show(getSupportFragmentManager(), "match");
                } else if(pagerMain.getCurrentItem() == 2){
                    DialogFragment dialog = new AddFeedDialog();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("feed_inside", false);
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), "feed");
                }
            }
        });

        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings_btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        username = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String versionName;
        try{
            versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "2.0_Chokito";
        }

        String finalVersionName = versionName;
        FirebaseDatabase.getInstance().getReference("Profile").child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("version").getValue().toString().equals(finalVersionName)) {
                    reference = FirebaseDatabase.getInstance().getReference("Profile").child(username);                        HashMap<String, Object> map = new HashMap<>();
                    map.put("version", finalVersionName);
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
                if (!snapshot.child("version").getValue().toString().equals(finalVersionName)){
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

    private void notification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            int permission = ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.POST_NOTIFICATIONS);
            if (permission != PackageManager.PERMISSION_GRANTED){
                String[] NOTIFICATION_PERMISSION = {Manifest.permission.POST_NOTIFICATIONS};
                ActivityCompat.requestPermissions(HomeActivity.this, NOTIFICATION_PERMISSION, 0);
            }
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("NotificationTest", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String msg = task.getResult();
                        Log.d("NotificationTest", msg);
                    }
                });

    }

    @Override
    public void onBackPressed(){

        if (pagerMain.getCurrentItem() == 0) {
            super.onBackPressed();
            finish();
        } else {
            pagerMain.setCurrentItem(0);
        }

    }

}