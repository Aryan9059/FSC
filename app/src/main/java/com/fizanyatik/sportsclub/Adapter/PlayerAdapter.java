package com.fizanyatik.sportsclub.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.Activity.PlayerActivity;
import com.fizanyatik.sportsclub.List.PlayerList;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    List<PlayerList> listViews;
    Context context;

    public PlayerAdapter(List<PlayerList> listViews, Context context) {
        this.listViews = listViews;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PlayerList listItem = listViews.get(position);
        FirebaseDatabase.getInstance().getReference("Profile").child(listItem.getParent()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String batting = snapshot.child("batting").getValue().toString();
                String bowling = snapshot.child("bowling").getValue().toString();
                String birthplace = snapshot.child("birthplace").getValue().toString();
                String birthdate = snapshot.child("birthdate").getValue().toString();
                String interests = snapshot.child("interests").getValue().toString();
                String ratings = snapshot.child("ratings").getValue().toString();
                String links = snapshot.child("links").getValue().toString();
                String nickname = snapshot.child("nickname").getValue().toString();
                String shirt = snapshot.child("shirt").getValue().toString();
                String stats = snapshot.child("stats").getValue().toString();
                String role = snapshot.child("role").getValue().toString();
                String parent = snapshot.child("parent").getValue().toString();
                String name = snapshot.child("first").getValue().toString() + " " + snapshot.child("last").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                String captain = snapshot.child("captain").getValue().toString();
                if (captain.equals("yes")){
                    holder.is_captain.setVisibility(View.VISIBLE);
                }
                if (role.equals("Batter")){
                    holder.role_img.setImageResource(R.drawable.icons8_bat_32);
                } else if (role.equals("Bowler")){
                    holder.role_img.setImageResource(R.drawable.icons8_cricket_ball_64);
                }
                holder.name.setText(name);
                holder.shirt_no.setText(shirt);

                if (!image.equals("default")) {
                        Glide.with(context.getApplicationContext()).load(Uri.parse(image)).into(holder.image);
                    }

                holder.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.parent.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                        Intent intent = new Intent(context, PlayerActivity.class);
                        intent.putExtra("batting", batting);
                        intent.putExtra("bowling", bowling);
                        intent.putExtra("image", image);
                        intent.putExtra("shirt", shirt);
                        intent.putExtra("name", name);
                        intent.putExtra("stats", stats);
                        intent.putExtra("role", role);
                        intent.putExtra("parent", parent);
                        intent.putExtra("captain", captain);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("links", links);
                        intent.putExtra("interests", interests);
                        intent.putExtra("birthdate", birthdate);
                        intent.putExtra("birthplace", birthplace);
                        intent.putExtra("ratings", ratings);
                        intent.putExtra("support", "no");
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listViews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, is_captain, shirt_no;
        ImageView image, role_img;
        MaterialCardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            role_img = itemView.findViewById(R.id.role_img);
            shirt_no = itemView.findViewById(R.id.shirt_no);
            is_captain = itemView.findViewById(R.id.is_captain);
            name = itemView.findViewById(R.id.player_name);
            parent = itemView.findViewById(R.id.player_cl);
            image = itemView.findViewById(R.id.player_img);
        }
    }

}
