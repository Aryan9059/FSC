package com.fizanyatik.sportsclub.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fizanyatik.sportsclub.Activity.RankingsActivity;
import com.fizanyatik.sportsclub.Adapter.LiveMatchAdapter;
import com.fizanyatik.sportsclub.Adapter.MatchAdapter;
import com.fizanyatik.sportsclub.List.LiveMatchList;
import com.fizanyatik.sportsclub.List.MatchList;
import com.fizanyatik.sportsclub.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchFragment extends Fragment {

    // Completed matches
    CardView rankings_cv;
    EditText search_match;
    RecyclerView recyclerView;
    private List<MatchList> matchLists;
    DatabaseReference matchReference;
    RecyclerView.Adapter matchAdapter;

    // Live matches
    RecyclerView liveRecyclerView;
    LinearLayout liveSection;
    TextView completedHeaderTv;
    private List<LiveMatchList> liveMatchLists;
    DatabaseReference liveMatchReference;
    RecyclerView.Adapter liveMatchAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_match, container, false);

        rankings_cv       = root.findViewById(R.id.rankings_cv);
        search_match      = root.findViewById(R.id.search_match);
        liveSection       = root.findViewById(R.id.live_section);
        completedHeaderTv = root.findViewById(R.id.completed_header_tv);

        List<MatchList> matchItems = new ArrayList<>();

        rankings_cv.setOnClickListener(v -> {
            rankings_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            startActivity(new Intent(getContext(), RankingsActivity.class));
        });

        // ----------------------------------------------------------------
        // Completed matches RecyclerView
        // ----------------------------------------------------------------
        recyclerView = root.findViewById(R.id.match_rv);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        matchLists = new ArrayList<>();
        matchReference = FirebaseDatabase.getInstance().getReference("Match");

        matchReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matchLists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        String match_details       = dataSnapshot.child("details").getValue().toString();
                        String match_result        = dataSnapshot.child("result").getValue().toString();
                        String match_scorecard     = dataSnapshot.child("scorecard").getValue().toString();
                        String team1_name          = dataSnapshot.child("team1_name").getValue().toString();
                        String team1_score         = dataSnapshot.child("team1_score").getValue().toString();
                        String top_team1_name      = dataSnapshot.child("top_team1_name").getValue().toString();
                        String top_team1_image     = dataSnapshot.child("top_team1_image").getValue().toString();
                        String top_team1_score     = dataSnapshot.child("top_team1_score").getValue().toString();
                        String top2_team1_name     = dataSnapshot.child("top2_team1_name").getValue().toString();
                        String top2_team1_image    = dataSnapshot.child("top2_team1_image").getValue().toString();
                        String top2_team1_score    = dataSnapshot.child("top2_team1_score").getValue().toString();
                        String team2_name          = dataSnapshot.child("team2_name").getValue().toString();
                        String team2_score         = dataSnapshot.child("team2_score").getValue().toString();
                        String top_team2_name      = dataSnapshot.child("top_team2_name").getValue().toString();
                        String top_team2_image     = dataSnapshot.child("top_team2_image").getValue().toString();
                        String top_team2_score     = dataSnapshot.child("top_team2_score").getValue().toString();
                        String top2_team2_name     = dataSnapshot.child("top2_team2_name").getValue().toString();
                        String top2_team2_image    = dataSnapshot.child("top2_team2_image").getValue().toString();
                        String top2_team2_score    = dataSnapshot.child("top2_team2_score").getValue().toString();
                        String parent              = dataSnapshot.child("parent").getValue().toString();

                        MatchList matchList = new MatchList(
                                match_details, match_result, match_scorecard,
                                team1_name, team1_score,
                                top_team1_name, top_team1_image, top_team1_score,
                                top2_team1_name, top2_team1_image, top2_team1_score,
                                team2_name, team2_score,
                                top_team2_name, top_team2_image, top_team2_score,
                                top2_team2_name, top2_team2_image, top2_team2_score,
                                parent);
                        matchLists.add(matchList);
                    } catch (Exception ignored) {}
                }

                Collections.reverse(matchLists);
                matchAdapter = new MatchAdapter(getContext(), matchLists);
                recyclerView.setAdapter(matchAdapter);
                matchAdapter.notifyDataSetChanged();

                if (!matchLists.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    completedHeaderTv.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    completedHeaderTv.setVisibility(View.GONE);
                }

                search_match.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override public void afterTextChanged(Editable s) {}

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (search_match.getText().toString().isEmpty()) {
                            matchItems.clear();
                            matchAdapter = new MatchAdapter(getContext(), matchLists);
                            recyclerView.setAdapter(matchAdapter);
                            matchAdapter.notifyDataSetChanged();
                        } else {
                            matchItems.clear();
                            for (MatchList m : matchLists) {
                                if (m.getMatch_details().toLowerCase()
                                        .contains(search_match.getText().toString().toLowerCase())) {
                                    matchItems.add(m);
                                }
                            }
                            matchAdapter = new MatchAdapter(getContext(), matchItems);
                            recyclerView.setAdapter(matchAdapter);
                            matchAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // ----------------------------------------------------------------
        // Live matches RecyclerView
        // ----------------------------------------------------------------
        liveRecyclerView = root.findViewById(R.id.live_match_rv);
        liveRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        liveRecyclerView.setNestedScrollingEnabled(false);

        liveMatchLists = new ArrayList<>();
        liveMatchReference = FirebaseDatabase.getInstance().getReference("LiveMatch");

        liveMatchReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                liveMatchLists.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        String key         = ds.getKey();
                        String matchName   = ds.child("matchName").getValue(String.class);
                        String seriesName  = ds.child("seriesName").getValue(String.class);
                        String battingTeam = ds.child("battingTeam").getValue(String.class);
                        String bowlingTeam = ds.child("bowlingTeam").getValue(String.class);
                        String overs       = ds.child("overs").getValue(String.class);

                        // 1st innings info
                        String firstInningsScore = ds.child("firstInningsScoreStr").getValue(String.class);
                        String firstInningsTeam  = ds.child("bowlingTeamName").getValue(String.class);

                        // Per-team score strings (pre-computed by ScoringActivity)
                        String nscScore = ds.child("nsc_score").getValue(String.class);
                        String sbrScore = ds.child("sbr_score").getValue(String.class);

                        // Top performers
                        String topNscBatterUid   = ds.child("top_nsc_batter_uid").getValue(String.class);
                        String topNscBatterName  = ds.child("top_nsc_batter_name").getValue(String.class);
                        String topNscBatterScore = ds.child("top_nsc_batter_score").getValue(String.class);
                        String topNscBowlerUid   = ds.child("top_nsc_bowler_uid").getValue(String.class);
                        String topNscBowlerName  = ds.child("top_nsc_bowler_name").getValue(String.class);
                        String topNscBowlerScore = ds.child("top_nsc_bowler_score").getValue(String.class);
                        String topSbrBatterUid   = ds.child("top_sbr_batter_uid").getValue(String.class);
                        String topSbrBatterName  = ds.child("top_sbr_batter_name").getValue(String.class);
                        String topSbrBatterScore = ds.child("top_sbr_batter_score").getValue(String.class);
                        String topSbrBowlerUid   = ds.child("top_sbr_bowler_uid").getValue(String.class);
                        String topSbrBowlerName  = ds.child("top_sbr_bowler_name").getValue(String.class);
                        String topSbrBowlerScore = ds.child("top_sbr_bowler_score").getValue(String.class);

                        int runs = 0, wickets = 0, target = 0;
                        boolean firstInnings = true;

                        Object runsObj = ds.child("runs").getValue();
                        if (runsObj != null) runs = Integer.parseInt(runsObj.toString());

                        Object wicketsObj = ds.child("wickets").getValue();
                        if (wicketsObj != null) wickets = Integer.parseInt(wicketsObj.toString());

                        Object targetObj = ds.child("target").getValue();
                        if (targetObj != null) target = Integer.parseInt(targetObj.toString());

                        Object firstObj = ds.child("isFirstInnings").getValue();
                        if (firstObj != null) firstInnings = Boolean.parseBoolean(firstObj.toString());

                        if (matchName != null && battingTeam != null) {
                            liveMatchLists.add(new LiveMatchList(
                                    key, matchName,
                                    or(seriesName),
                                    battingTeam,
                                    or(bowlingTeam),
                                    runs, wickets,
                                    overs != null ? overs : "0.0",
                                    target, firstInnings,
                                    or(nscScore),     or(sbrScore),
                                    or(firstInningsScore), or(firstInningsTeam),
                                    or(topNscBatterUid),  or(topNscBatterName),  or(topNscBatterScore),
                                    or(topNscBowlerUid),  or(topNscBowlerName),  or(topNscBowlerScore),
                                    or(topSbrBatterUid),  or(topSbrBatterName),  or(topSbrBatterScore),
                                    or(topSbrBowlerUid),  or(topSbrBowlerName),  or(topSbrBowlerScore)));
                        }
                    } catch (Exception ignored) {}
                }

                if (!liveMatchLists.isEmpty()) {
                    liveSection.setVisibility(View.VISIBLE);
                } else {
                    liveSection.setVisibility(View.GONE);
                }

                liveMatchAdapter = new LiveMatchAdapter(getContext(), liveMatchLists);
                liveRecyclerView.setAdapter(liveMatchAdapter);
                liveMatchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return root;
    }

    /** Null-safe helper: returns empty string for null values. */
    private static String or(String v) { return v != null ? v : ""; }
}