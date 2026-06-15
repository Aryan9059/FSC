package com.fizanyatik.sportsclub.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.fizanyatik.sportsclub.List.SimplePlayer;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PlayerSelectionAdapter extends RecyclerView.Adapter<PlayerSelectionAdapter.PlayerViewHolder> {

    List<SimplePlayer> playerList;
    Context context;

    public PlayerSelectionAdapter(List<SimplePlayer> playerList, Context context) {
        this.playerList = playerList;
        this.context = context;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_player_select, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        SimplePlayer player = playerList.get(position);

        // Name
        holder.playerName.setText(player.getName());

        // Default stats text while loading
        holder.playerStats.setText("—");

        // Fetch stats from Firebase: Profile/{uid}/stats_match, stats_runs, stats_wicket
        FirebaseDatabase.getInstance()
                .getReference("Profile")
                .child(player.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Object matchObj  = snapshot.child("stats_match").getValue();
                        Object runsObj   = snapshot.child("stats_runs").getValue();
                        Object wicketObj = snapshot.child("stats_wicket").getValue();

                        String matches = matchObj  != null ? matchObj.toString()  : "0";
                        String runs    = runsObj   != null ? runsObj.toString()   : "0";
                        String wickets = wicketObj != null ? wicketObj.toString() : "0";

                        holder.playerStats.setText(matches + " Matches · " + runs + " Runs · " + wickets + " Wickets");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        holder.playerStats.setText("—");
                    }
                });

        // Load player photo using Glide (circular crop)
        if (player.getImageUrl() != null && !player.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(player.getImageUrl())
                    .transform(new CircleCrop())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(holder.playerAvatar);
        } else {
            holder.playerAvatar.setImageResource(R.drawable.ic_baseline_person_24);
        }

        // Sync checkbox + background without triggering listener
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(player.isSelected());
        holder.itemView.setActivated(player.isSelected());

        // Toggle on whole-row click
        holder.itemView.setOnClickListener(v -> {
            boolean nowSelected = !player.isSelected();
            player.setSelected(nowSelected);
            holder.checkBox.setChecked(nowSelected);
            holder.itemView.setActivated(nowSelected);
        });
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    // Helper method to get selected players
    public ArrayList<SimplePlayer> getSelectedPlayers() {
        ArrayList<SimplePlayer> selected = new ArrayList<>();
        for (SimplePlayer player : playerList) {
            if (player.isSelected()) {
                selected.add(player);
            }
        }
        return selected;
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        ImageView playerAvatar;
        TextView playerName;
        TextView playerStats;
        MaterialCheckBox checkBox;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerAvatar = itemView.findViewById(R.id.player_avatar);
            playerName   = itemView.findViewById(R.id.player_name);
            playerStats  = itemView.findViewById(R.id.player_stats);
            checkBox     = itemView.findViewById(R.id.player_checkbox);
        }
    }
}