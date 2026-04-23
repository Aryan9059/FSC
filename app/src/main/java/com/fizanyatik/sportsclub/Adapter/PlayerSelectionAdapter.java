package com.fizanyatik.sportsclub.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fizanyatik.sportsclub.List.SimplePlayer;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.checkbox.MaterialCheckBox;
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
        holder.checkBox.setText(player.getName());
        holder.checkBox.setChecked(player.isSelected());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            player.setSelected(isChecked);
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
        MaterialCheckBox checkBox;
        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.player_checkbox);
        }
    }
}