package com.fizanyatik.sportsclub.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.Adapter.SupportAdapter;
import com.fizanyatik.sportsclub.Dialog.EditPlayerDialog;
import com.fizanyatik.sportsclub.List.PlayerList;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    ImageView aryan_back, role;
    String admin;
    CardView interests_cv, links_cv, stats_cv, support_cv, ratings_cv;
    FloatingActionButton edit_player;
    LinearProgressIndicator progress1, progress2, progress3, progress4;
    LinearProgressIndicator batting_rate_prg, bowling_rate_prg, fielding_rate_prg, wicket_keeping_prg;
    TextView match, runs, wicket, average, strike, economy, links_tv, support_count;
    TextView national_tv, ipl_tv, batting_tv, bowling_tv, type_tv, cricketer_tv;
    TextView batting_rate, bowling_rate, fielding_rate, wicket_keeping, speed_rate, stamina_rate;
    TextView name, birthdate, birthplace, nickname, batting, bowling, shirt, captain;
    String name_str, birthdate_str, birthplace_str, nickname_str, batting_str, bowling_str, shirt_str, captain_str, image_str;
    String links_str, interests_str, stats_str, role_str, parent, support_str, ratings_str;
    CircularProgressIndicator circle_progress, circle_progress1;
    CircularProgressIndicator speed_rate_prg, stamina_rate_prg;
    ImageView profile_pic;
    Button certificate_btn, public_btn, private_btn;
    SharedPreferences prefs;
    PlayerList supportList;
    RecyclerView support_rv;
    List<PlayerList> support_items;
    RecyclerView.Adapter supportAdapter;

    @SuppressLint("SetTextI18n")
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
        setContentView(R.layout.activity_player);

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

        progress1 = findViewById(R.id.progress1);
        progress2 = findViewById(R.id.progress2);
        progress3 = findViewById(R.id.progress3);
        progress4 = findViewById(R.id.progress4);
        support_count = findViewById(R.id.support_count);
        circle_progress = findViewById(R.id.circle_progress);
        circle_progress1 = findViewById(R.id.circle_progress1);
        links_tv = findViewById(R.id.links_text);

        batting_rate_prg = findViewById(R.id.batting_rate_prg);
        bowling_rate_prg = findViewById(R.id.bowling_rate_prg);
        fielding_rate_prg = findViewById(R.id.fielding_rate_prg);
        wicket_keeping_prg = findViewById(R.id.wicket_keeping_rate_prg);
        speed_rate_prg = findViewById(R.id.speed_prg);
        stamina_rate_prg = findViewById(R.id.stamina_prg);

        support_rv = findViewById(R.id.supporter_rv);
        support_rv.setNestedScrollingEnabled(false);

        certificate_btn = findViewById(R.id.certificate_btn);
        private_btn = findViewById(R.id.private_btn);
        public_btn = findViewById(R.id.public_btn);

        edit_player = findViewById(R.id.player_edit);
        support_items = new ArrayList<>();

        profile_pic = findViewById(R.id.profile_img);
        name = findViewById(R.id.name);
        birthdate = findViewById(R.id.birthdate);
        birthplace = findViewById(R.id.birthplace);
        nickname = findViewById(R.id.nickname);
        batting = findViewById(R.id.batting);
        bowling = findViewById(R.id.bowling);
        shirt = findViewById(R.id.shirt);
        captain = findViewById(R.id.captain);

        national_tv = findViewById(R.id.national_tv);
        ipl_tv = findViewById(R.id.ipl_tv);
        batting_tv = findViewById(R.id.batting_top);
        bowling_tv = findViewById(R.id.bowling_top);
        type_tv = findViewById(R.id.personality);
        cricketer_tv = findViewById(R.id.player_top);

        batting_rate = findViewById(R.id.batting_rate);
        bowling_rate = findViewById(R.id.bowling_rate);
        fielding_rate = findViewById(R.id.fielding_rate);
        wicket_keeping = findViewById(R.id.wicket_keeping_rate);
        speed_rate = findViewById(R.id.speed);
        stamina_rate = findViewById(R.id.stamina);

        interests_cv = findViewById(R.id.interests_cv);
        support_cv = findViewById(R.id.support_cv);
        links_cv = findViewById(R.id.links_cv);
        stats_cv = findViewById(R.id.statistics_cv);
        ratings_cv = findViewById(R.id.ratings_cv);

        links_str = getIntent().getStringExtra("links");
        stats_str = getIntent().getStringExtra("stats");
        ratings_str = getIntent().getStringExtra("ratings");
        interests_str = getIntent().getStringExtra("interests");
        support_str = getIntent().getStringExtra("support");
        role_str = getIntent().getStringExtra("role");

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            TextView role_tv = findViewById(R.id.role_tv);
            role_tv.setText(role_str);
        } else {
            role = findViewById(R.id.role_img);

            if (role_str.equals("Batter")){
                role.setImageResource(R.drawable.icons8_bat_32);
            } else if (role_str.equals("Bowler")){
                role.setImageResource(R.drawable.icons8_cricket_ball_64);
            }
        }
        name_str = getIntent().getStringExtra("name");
        name.setText(name_str);
        nickname_str = getIntent().getStringExtra("nickname");
        nickname.setText(nickname_str);
        birthplace_str = getIntent().getStringExtra("birthplace");
        birthplace.setText(birthplace_str);
        birthdate_str = getIntent().getStringExtra("birthdate");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss");
            birthdate_str = birthdate_str.replace("/", "-") + "T00:00:00";
            LocalDateTime dateTime = LocalDateTime.parse(birthdate_str, formatter);
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            birthdate.setText(dateTime.format(formatter1));
        } else {
            birthdate.setText(birthdate_str);
        }
        bowling_str = getIntent().getStringExtra("bowling");
        bowling.setText(bowling_str);
        batting_str = getIntent().getStringExtra("batting");
        batting.setText(batting_str + "ed Bat");
        shirt_str = getIntent().getStringExtra("shirt");
        shirt.setText(shirt_str);
        captain_str = getIntent().getStringExtra("captain");
        if (captain_str.equals("yes")){
            captain.setVisibility(View.VISIBLE);
        }
        image_str = getIntent().getStringExtra("image");
        if (!image_str.equals("default")){
            Glide.with(this).load(Uri.parse(image_str)).into(profile_pic);
        }
        parent = getIntent().getStringExtra("parent");
        FirebaseDatabase.getInstance().getReference("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                admin = snapshot.getValue().toString();
                if (admin.contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    edit_player.setVisibility(View.VISIBLE);
                    edit_player.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogFragment dialog = new EditPlayerDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString("ID", parent);
                            dialog.setArguments(bundle);
                            dialog.show(getSupportFragmentManager(), "edit");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        match = findViewById(R.id.match);
        runs = findViewById(R.id.runs);
        wicket = findViewById(R.id.wicket);
        average = findViewById(R.id.average);
        strike = findViewById(R.id.strike);
        economy = findViewById(R.id.economy);

        aryan_back = findViewById(R.id.aryan_back);
        aryan_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aryan_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        if (support_str.equals("no")){
            support_cv.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference("Profile").child(parent).child("support").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = 0;
                    support_items.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String parent = dataSnapshot.child("parent").getValue().toString();

                        supportList = new PlayerList(parent);
                        support_items.add(supportList);
                        count++;
                    }

                    if (count == 0){
                        support_cv.setVisibility(View.GONE);
                    } else if (count == 1){
                        support_count.setText(count + " Supporter");
                    } else {
                        support_count.setText(count + " Supporters");
                    }

                    supportAdapter = new SupportAdapter(support_items, PlayerActivity.this);
                    support_rv.setAdapter(supportAdapter);
                    supportAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (stats_str.equals("yes")){
            stats_cv.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference("Profile").child(parent).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String match_str = snapshot.child("stats_match").getValue().toString();
                    String runs_str = snapshot.child("stats_runs").getValue().toString();
                    String wicket_str = snapshot.child("stats_wicket").getValue().toString();
                    String average_str = snapshot.child("stats_average").getValue().toString();
                    String strike_str = snapshot.child("stats_strike").getValue().toString();
                    String economy_str = snapshot.child("stats_economy").getValue().toString();

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

        if (ratings_str.equals("yes")){
            ratings_cv.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference("Profile").child(parent).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String rate_batting = snapshot.child("rate_batting").getValue().toString();
                    String rate_bowling = snapshot.child("rate_bowling").getValue().toString();
                    String rate_wicket_keeping = snapshot.child("rate_wicket_keeping").getValue().toString();
                    String rate_fielding = snapshot.child("rate_fielding").getValue().toString();
                    String rate_speed = snapshot.child("rate_speed").getValue().toString();
                    String rate_stamina = snapshot.child("rate_stamina").getValue().toString();

                    batting_rate.setText(rate_batting);
                    bowling_rate.setText(rate_bowling);
                    wicket_keeping.setText(rate_wicket_keeping);
                    fielding_rate.setText(rate_fielding);
                    speed_rate.setText(rate_speed);
                    stamina_rate.setText(rate_stamina);

                    batting_rate_prg.setProgress(Math.round(Float.parseFloat(rate_batting)*20));
                    bowling_rate_prg.setProgress(Math.round(Float.parseFloat(rate_bowling)*20));
                    wicket_keeping_prg.setProgress(Math.round(Float.parseFloat(rate_wicket_keeping)*20));
                    fielding_rate_prg.setProgress(Math.round(Float.parseFloat(rate_fielding)*20));
                    speed_rate_prg.setProgress(Math.round(Float.parseFloat(rate_speed)*20));
                    stamina_rate_prg.setProgress(Math.round(Float.parseFloat(rate_stamina)*20));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (interests_str.equals("yes")){
            interests_cv.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference("Profile").child(parent).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String national = snapshot.child("int_national").getValue().toString();
                    national_tv.setText(national);
                    String ipl = snapshot.child("int_ipl").getValue().toString();
                    ipl_tv.setText(ipl);
                    String batting = snapshot.child("int_batting").getValue().toString();
                    batting_tv.setText(batting);
                    String bowling = snapshot.child("int_bowling").getValue().toString();
                    bowling_tv.setText(bowling);
                    String favorite = snapshot.child("int_favorite").getValue().toString();
                    cricketer_tv.setText(favorite);
                    String type = snapshot.child("int_type").getValue().toString();
                    type_tv.setText(type);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (links_str.equals("yes")){
            links_cv.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference("Profile").child(parent).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String certificate_link = snapshot.child("link_certificate_link").getValue().toString();
                    String public_profile_link = snapshot.child("link_public_profile_link").getValue().toString();
                    String private_message_link = snapshot.child("link_private_message_link").getValue().toString();
                    String public_profile = snapshot.child("link_public_profile").getValue().toString();
                    String private_message = snapshot.child("link_private_message").getValue().toString();

                    if (certificate_link.equals("default")){
                        certificate_btn.setVisibility(View.GONE);
                    } else {
                        certificate_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(certificate_link));
                                startActivity(intent);
                            }
                        });
                    }

                    public_btn.setText("Follow me on " + public_profile);
                    private_btn.setText("My " + private_message);
                    public_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(public_profile_link));
                            startActivity(intent);
                        }
                    });

                    private_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(private_message_link));
                            startActivity(intent);
                        }
                    });

                    links_tv.setText("Hi! I am " + name_str + ". You can message me personally on " + private_message + " or can check my public profile on " + public_profile + ".");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}