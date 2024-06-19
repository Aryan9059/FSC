package com.fizanyatik.sportsclub.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.Activity.RankingsActivity;
import com.fizanyatik.sportsclub.Activity.TeamActivity;
import com.fizanyatik.sportsclub.List.RankList;
import com.fizanyatik.sportsclub.R;

import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder>{
    List<RankList> listViews;
    Context context;

    public RankAdapter(List<RankList> listViews, Context context) {
        this.listViews = listViews;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RankList listItem = listViews.get(position);
        if (listItem.getRank() == 1){
            holder.rank_cl.setVisibility(View.VISIBLE);
            holder.rank.setTextColor(context.getResources().getColor(R.color.screen_background));
            holder.name.setTextColor(context.getResources().getColor(R.color.screen_background));
            holder.points.setTextColor(context.getResources().getColor(R.color.screen_background));
        } else if (listItem.getRank() == 2) {
            holder.parent_rank_cl.setPadding(0, 40, 0, 20);
        } else {
            holder.parent_rank_cl.setPadding(0, 24, 0, 20);
        }
        holder.name.setText(listItem.getName());
        holder.rank.setText(String.valueOf(listItem.getRank()));
        holder.points.setText(listItem.getPoints() + " Points");
        Glide.with(context).load(listItem.getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return listViews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, points, rank;
        ImageView image;
        ConstraintLayout rank_cl, parent_rank_cl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.rank_name);
            points = itemView.findViewById(R.id.rank_points);
            rank = itemView.findViewById(R.id.rank_number);
            rank_cl = itemView.findViewById(R.id.ranK_cl);
            image = itemView.findViewById(R.id.rank_pic);
            parent_rank_cl = itemView.findViewById(R.id.parent_rank_cl);
        }
    }

}
