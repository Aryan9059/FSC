package com.fizanyatik.sportsclub.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.Activity.ScoringActivity;
import com.fizanyatik.sportsclub.List.LiveMatchList;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class LiveMatchAdapter extends RecyclerView.Adapter<LiveMatchAdapter.LiveMatchViewHolder> {

    private final Context context;
    private final List<LiveMatchList> liveMatches;

    public LiveMatchAdapter(Context context, List<LiveMatchList> liveMatches) {
        this.context = context;
        this.liveMatches = liveMatches;
    }

    @NonNull
    @Override
    public LiveMatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.live_match_item, parent, false);
        return new LiveMatchViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull LiveMatchViewHolder holder, @SuppressLint("RecyclerView") int position) {
        LiveMatchList item = liveMatches.get(position);

        // --- Match name / series ---
        holder.seriesTv.setText(item.getMatchName() + " • " + item.getSeriesName());

        // --- NSC row ---
        holder.team1Tv.setText("NSC");
        holder.team1Image.setImageDrawable(context.getResources().getDrawable(R.drawable.nsc_logo));
        holder.team1Score.setText(item.getNscScore());

        // --- SBR row ---
        holder.team2Tv.setText("SBR");
        holder.team2Image.setImageDrawable(context.getResources().getDrawable(R.drawable.sbr_logo));
        holder.team2Score.setText(item.getSbrScore());

        // --- NSC top performers ---
        holder.topTeam1Tv.setText(item.getTopNscBatterName());
        holder.topTeam1Score.setText(item.getTopNscBatterScore());
        holder.top2Team1Tv.setText(item.getTopNscBowlerName());
        holder.top2Team1Score.setText(item.getTopNscBowlerScore());

        // --- SBR top performers ---
        holder.topTeam2Tv.setText(item.getTopSbrBatterName());
        holder.topTeam2Score.setText(item.getTopSbrBatterScore());
        holder.top2Team2Tv.setText(item.getTopSbrBowlerName());
        holder.top2Team2Score.setText(item.getTopSbrBowlerScore());

        // --- LIVE status line ---
        String statusText = "";
        if (!item.isFirstInnings() && item.getTarget() > 0) {
            int runsNeeded = item.getTarget() - item.getRuns();
            statusText += item.getBattingTeam() + " need " + runsNeeded + " to win";
        } else if (item.isFirstInnings()) {
            statusText += "1st Innings";
        } else {
            statusText += "2nd Innings";
        }
        holder.statusTv.setText(statusText);

        // --- Load profile images for top performers from Firebase Profile node ---
        FirebaseDatabase.getInstance().getReference("Profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loadPerformerImage(snapshot, item.getTopNscBatterUid(), holder.topTeam1Iv);
                        loadPerformerImage(snapshot, item.getTopNscBowlerUid(), holder.top2Team1Iv);
                        loadPerformerImage(snapshot, item.getTopSbrBatterUid(), holder.topTeam2Iv);
                        loadPerformerImage(snapshot, item.getTopSbrBowlerUid(), holder.top2Team2Iv);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { /* no-op */ }
                });

        // --- Click → resume ScoringActivity ---
        holder.cardClick.setOnClickListener(v -> {
            Intent intent = new Intent(context, ScoringActivity.class);
            intent.putExtra("RESUME_KEY", item.getKey());
            context.startActivity(intent);
        });
    }

    /** Load a player's profile image into an ImageView using their UID. */
    private void loadPerformerImage(DataSnapshot profileSnap, String uid, CircleImageView iv) {
        try {
            if (uid == null || uid.isEmpty()) return;
            Object imgVal = profileSnap.child(uid).child("image").getValue();
            if (imgVal == null) return;
            String imgUrl = imgVal.toString();
            if (!imgUrl.equals("default")) {
                Glide.with(context).load(Uri.parse(imgUrl)).into(iv);
            }
        } catch (Exception ignored) {}
    }

    @Override
    public int getItemCount() {
        return liveMatches.size();
    }

    public static class LiveMatchViewHolder extends RecyclerView.ViewHolder {

        TextView seriesTv;
        CircleImageView team1Image, team2Image;
        TextView team1Tv, team1Score;
        TextView team2Tv, team2Score;
        CircleImageView topTeam1Iv, top2Team1Iv, topTeam2Iv, top2Team2Iv;
        TextView topTeam1Tv, topTeam1Score;
        TextView top2Team1Tv, top2Team1Score;
        TextView topTeam2Tv, topTeam2Score;
        TextView top2Team2Tv, top2Team2Score;
        TextView statusTv;
        MaterialCardView cardClick;

        public LiveMatchViewHolder(@NonNull View itemView) {
            super(itemView);

            seriesTv       = itemView.findViewById(R.id.live_series_tv);

            team1Image     = itemView.findViewById(R.id.live_team1_image);
            team1Tv        = itemView.findViewById(R.id.live_team1_tv);
            team1Score     = itemView.findViewById(R.id.live_team1_score);

            topTeam1Iv     = itemView.findViewById(R.id.live_top_team1_iv);
            topTeam1Tv     = itemView.findViewById(R.id.live_top_team1_tv);
            topTeam1Score  = itemView.findViewById(R.id.live_top_team1_score);

            top2Team1Iv    = itemView.findViewById(R.id.live_top2_team1_iv);
            top2Team1Tv    = itemView.findViewById(R.id.live_top2_team1_tv);
            top2Team1Score = itemView.findViewById(R.id.live_top2_team1_score);

            team2Image     = itemView.findViewById(R.id.live_team2_image);
            team2Tv        = itemView.findViewById(R.id.live_team2_tv);
            team2Score     = itemView.findViewById(R.id.live_team2_score);

            topTeam2Iv     = itemView.findViewById(R.id.live_top_team2_iv);
            topTeam2Tv     = itemView.findViewById(R.id.live_top_team2_tv);
            topTeam2Score  = itemView.findViewById(R.id.live_top_team2_score);

            top2Team2Iv    = itemView.findViewById(R.id.live_top2_team2_iv);
            top2Team2Tv    = itemView.findViewById(R.id.live_top2_team2_tv);
            top2Team2Score = itemView.findViewById(R.id.live_top2_team2_score);

            statusTv       = itemView.findViewById(R.id.live_status_tv);
            cardClick      = itemView.findViewById(R.id.live_card_click);
        }
    }
}
