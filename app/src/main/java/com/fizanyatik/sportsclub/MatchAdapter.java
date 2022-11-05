package com.fizanyatik.sportsclub;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.Activity.RankingsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder>{

    private Context context;
    private List<MatchList> matchLists;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    public MatchAdapter(Context context, List<MatchList> matchLists) {
        this.context = context;
        this.matchLists = matchLists;
    }

    @NonNull
    @Override
    public MatchAdapter.MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.match_ui, parent, false);
        return new MatchAdapter.MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.team1_tv.setText(matchLists.get(position).getTeam1_name());
        if (matchLists.get(position).getTeam1_name().equals("NSC")){
            holder.team1_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ns_full));
        } else {
            holder.team1_image.setImageDrawable(context.getResources().getDrawable(R.drawable.sb_full));
        }
        holder.team1_score.setText(matchLists.get(position).getTeam1_score());
        holder.top_team1_tv.setText(matchLists.get(position).getTop_team1_name());
        holder.top_team1_score.setText(matchLists.get(position).getTop_team1_score());
        holder.top2_team1_tv.setText(matchLists.get(position).getTop2_team1_name());
        holder.top2_team1_score.setText(matchLists.get(position).getTop2_team1_score());
        holder.team2_tv.setText(matchLists.get(position).getTeam2_name());
        if (matchLists.get(position).getTeam2_name().equals("NSC")){
            holder.team2_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ns_full));
        } else {
            holder.team2_image.setImageDrawable(context.getResources().getDrawable(R.drawable.sb_full));
        }
        holder.team2_score.setText(matchLists.get(position).getTeam2_score());
        holder.top_team2_tv.setText(matchLists.get(position).getTop_team2_name());
        holder.top_team2_score.setText(matchLists.get(position).getTop_team2_score());
        holder.top2_team2_tv.setText(matchLists.get(position).getTop2_team2_name());
        holder.top2_team2_score.setText(matchLists.get(position).getTop2_team2_score());
        holder.match_details.setText(matchLists.get(position).getMatch_details());
        holder.match_result.setText(matchLists.get(position).getMatch_result());

        holder.card_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Download has started", Toast.LENGTH_SHORT).show();
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(matchLists.get(position).getMatch_scorecard());
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle("Match Scorecard");
                request.setDescription("Downloading " + matchLists.get(position).getTeam1_name() + " vs " + matchLists.get(position).getTeam2_name() + " Scorecard");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(DIRECTORY_DOCUMENTS, matchLists.get(position).getMatch_details() + ".pdf");
                downloadManager.enqueue(request);
            }
        });

        FirebaseDatabase.getInstance().getReference("Players").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotImg) {
                Uri image1 = Uri.parse(snapshotImg.child(matchLists.get(position).getTop_team1_image()).child("image").getValue().toString());
                Uri image2 = Uri.parse(snapshotImg.child(matchLists.get(position).getTop2_team1_image()).child("image").getValue().toString());
                Uri image3 = Uri.parse(snapshotImg.child(matchLists.get(position).getTop_team2_image()).child("image").getValue().toString());
                Uri image4 = Uri.parse(snapshotImg.child(matchLists.get(position).getTop2_team2_image()).child("image").getValue().toString());

                Glide.with(context).load(image1).into(holder.top_team1_iv);
                Glide.with(context).load(image2).into(holder.top2_team1_iv);
                Glide.with(context).load(image3).into(holder.top_team2_iv);
                Glide.with(context).load(image4).into(holder.top2_team2_iv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return matchLists.size();
    }

    public class MatchViewHolder extends RecyclerView.ViewHolder{

        private TextView team1_tv, team1_score, top_team1_tv, top_team1_score, top2_team1_tv, top2_team1_score,
                team2_tv, team2_score, top_team2_tv, top_team2_score, top2_team2_tv, top2_team2_score,
                match_details, match_result;
        private ImageView team1_image, team2_image;
        private CircleImageView top_team1_iv, top_team2_iv, top2_team1_iv, top2_team2_iv;
        private MaterialCardView card_click;

        public MatchViewHolder(View itemView) {
            super(itemView);

            card_click = itemView.findViewById(R.id.card_click);

            team1_tv = itemView.findViewById(R.id.team1_tv);
            team1_score = itemView.findViewById(R.id.team1_score);
            top_team1_tv = itemView.findViewById(R.id.top_team1_tv);
            top_team1_score = itemView.findViewById(R.id.top_team1_score);
            top2_team1_tv = itemView.findViewById(R.id.top2_team1_tv);
            top2_team1_score = itemView.findViewById(R.id.top2_team1_score);

            team1_image = itemView.findViewById(R.id.team1_image);
            top_team1_iv = itemView.findViewById(R.id.top_team1_iv);
            top2_team1_iv = itemView.findViewById(R.id.top2_team1_iv);

            team2_tv = itemView.findViewById(R.id.team2_tv);
            team2_score = itemView.findViewById(R.id.team2_score);
            top_team2_tv = itemView.findViewById(R.id.top_team2_tv);
            top_team2_score = itemView.findViewById(R.id.top_team2_score);
            top2_team2_tv = itemView.findViewById(R.id.top2_team2_tv);
            top2_team2_score = itemView.findViewById(R.id.top2_team2_score);

            team2_image = itemView.findViewById(R.id.team2_image);
            top_team2_iv = itemView.findViewById(R.id.top_team2_iv);
            top2_team2_iv = itemView.findViewById(R.id.top2_team2_iv);

            match_details = itemView.findViewById(R.id.series_tv);
            match_result = itemView.findViewById(R.id.result_tv);

        }
    }
}
