package com.fizanyatik.sportsclub.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.fizanyatik.sportsclub.Adapter.PlayerAdapter;
import com.fizanyatik.sportsclub.Adapter.SupportAdapter;
import com.fizanyatik.sportsclub.List.PlayerList;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;
import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends AppCompatActivity {
    LinearProgressIndicator progress1, progress2, progress3;
    CircularProgressIndicator circle_progress;
    TextView runs, match, wicket, wins, support_count;
    ImageView team_back, team_img;
    SharedPreferences prefs;
    String team, caption;
    PlayerList playerList;
    CardView support_cv;
    DatabaseReference reference;
    RecyclerView player_rv;
    List<PlayerList> player_items;
    RecyclerView.Adapter playerAdapter;
    PlayerList supportList;
    RecyclerView support_rv;
    List<PlayerList> support_items;
    RecyclerView.Adapter supportAdapter;

    @SuppressLint("UseCompatLoadingForDrawables")
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
        setContentView(R.layout.activity_team);

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

        player_rv = findViewById(R.id.player_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        player_rv.setLayoutManager(gridLayoutManager);
        player_rv.setNestedScrollingEnabled(false);
        player_items = new ArrayList<>();
        team_img = findViewById(R.id.team_img);

        support_rv = findViewById(R.id.supporter_rv);
        support_count = findViewById(R.id.support_count);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);
        support_rv.setLayoutManager(flowLayoutManager);
        support_rv.setNestedScrollingEnabled(false);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            player_rv.setLayoutManager(new LinearLayoutManager(this));
            player_rv.setNestedScrollingEnabled(false);
        }

        support_cv = findViewById(R.id.support_cv);
        support_items = new ArrayList<>();

        progress1 = findViewById(R.id.progress1);
        progress2 = findViewById(R.id.progress2);
        progress3 = findViewById(R.id.progress3);
        circle_progress = findViewById(R.id.circle_progress);

        team = getIntent().getStringExtra("team");
        caption = getIntent().getStringExtra("caption");

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            TextView caption_tv = findViewById(R.id.caption_team_tv);
            TextView team_tv = findViewById(R.id.team_name_tv);

            caption_tv.setText(caption);

            if (team.equals("NSC")){
                team_tv.setText("Northern Superchargers");
                team_img.setImageDrawable(getResources().getDrawable(R.drawable.nsc_logo));
            } else {
                team_tv.setText("Southern Brave");
                team_img.setImageDrawable(getResources().getDrawable(R.drawable.sbr_logo));
            }
        } else{
            if (team.equals("NSC")){
                team_img.setImageDrawable(getResources().getDrawable(R.drawable.nsc_full));
            } else {
                team_img.setImageDrawable(getResources().getDrawable(R.drawable.sbr_full));
            }
        }

        match = findViewById(R.id.match);
        runs = findViewById(R.id.runs);
        wicket = findViewById(R.id.wicket);
        wins = findViewById(R.id.wins);

        team_back = findViewById(R.id.team_back);
        team_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                team_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Teams").child(team);

        reference.child("support").addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
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
                    support_cv.setVisibility(View.VISIBLE);
                    support_count.setText(count + " Supporter");
                } else {
                    support_cv.setVisibility(View.VISIBLE);
                    support_count.setText(count + " Supporters");
                }

                supportAdapter = new SupportAdapter(support_items, TeamActivity.this);
                support_rv.setAdapter(supportAdapter);
                supportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("players").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                player_items.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String parent = dataSnapshot.child("parent").getValue().toString();

                    playerList = new PlayerList(parent);
                    player_items.add(playerList);
                }

                playerAdapter = new PlayerAdapter(player_items, TeamActivity.this);
                player_rv.setAdapter(playerAdapter);
                playerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
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
    }
}