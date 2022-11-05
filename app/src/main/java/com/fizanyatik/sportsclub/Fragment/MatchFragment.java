package com.fizanyatik.sportsclub.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fizanyatik.sportsclub.Activity.RankingsActivity;
import com.fizanyatik.sportsclub.MatchAdapter;
import com.fizanyatik.sportsclub.MatchList;
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
    CardView rankings_cv;
    RecyclerView recyclerView;
    private List<MatchList> matchLists;
    DatabaseReference matchReference;
    RecyclerView.Adapter matchAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_match, container, false);

        rankings_cv = root.findViewById(R.id.rankings_cv);
        rankings_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rankings_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(getContext(), RankingsActivity.class));
            }
        });

        recyclerView = root.findViewById(R.id.match_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        matchLists = new ArrayList<>();

        matchReference = FirebaseDatabase.getInstance().getReference("Matches");

        matchReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matchLists.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String match_details = dataSnapshot.child("details").getValue().toString();
                    String match_result = dataSnapshot.child("result").getValue().toString();
                    String match_scorecard = dataSnapshot.child("scorecard").getValue().toString();
                    String team1_name = dataSnapshot.child("team1_name").getValue().toString();
                    String team1_score = dataSnapshot.child("team1_score").getValue().toString();
                    String top_team1_name = dataSnapshot.child("top_team1_name").getValue().toString();
                    String top_team1_image = dataSnapshot.child("top_team1_image").getValue().toString();
                    String top_team1_score = dataSnapshot.child("top_team1_score").getValue().toString();
                    String top2_team1_name = dataSnapshot.child("top2_team1_name").getValue().toString();
                    String top2_team1_image = dataSnapshot.child("top2_team1_image").getValue().toString();
                    String top2_team1_score = dataSnapshot.child("top2_team1_score").getValue().toString();
                    String team2_name = dataSnapshot.child("team2_name").getValue().toString();
                    String team2_score = dataSnapshot.child("team2_score").getValue().toString();
                    String top_team2_name = dataSnapshot.child("top_team2_name").getValue().toString();
                    String top_team2_image = dataSnapshot.child("top_team2_image").getValue().toString();
                    String top_team2_score = dataSnapshot.child("top_team2_score").getValue().toString();
                    String top2_team2_name = dataSnapshot.child("top2_team2_name").getValue().toString();
                    String top2_team2_image = dataSnapshot.child("top2_team2_image").getValue().toString();
                    String top2_team2_score = dataSnapshot.child("top2_team2_score").getValue().toString();

                    MatchList matchList = new MatchList(match_details, match_result, match_scorecard, team1_name, team1_score, top_team1_name, top_team1_image,
                            top_team1_score, top2_team1_name, top2_team1_image, top2_team1_score, team2_name, team2_score, top_team2_name, top_team2_image,
                            top_team2_score, top2_team2_name, top2_team2_image, top2_team2_score);
                    matchLists.add(matchList);
                }
                Collections.reverse(matchLists);
                matchAdapter = new MatchAdapter(getContext(), matchLists);
                recyclerView.setAdapter(matchAdapter);
                matchAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }
}