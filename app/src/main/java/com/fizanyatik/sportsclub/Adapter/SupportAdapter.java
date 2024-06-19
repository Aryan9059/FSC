package com.fizanyatik.sportsclub.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.List.PlayerList;
import com.fizanyatik.sportsclub.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class SupportAdapter extends RecyclerView.Adapter<SupportAdapter.ViewHolder> {

    List<PlayerList> listViews;
    Context context;

    public SupportAdapter(List<PlayerList> listViews, Context context) {
        this.listViews = listViews;
        this.context = context;
    }

    @NonNull
    @Override
    public SupportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.supporter_item, parent, false);
        return new SupportAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SupportAdapter.ViewHolder holder, int position) {
        final PlayerList listItem = listViews.get(position);
        FirebaseDatabase.getInstance().getReference("Profile").child(listItem.getParent()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("first").getValue().toString() + " " + snapshot.child("last").getValue().toString();
                String image = snapshot.child("image").getValue().toString();

                if (context.getClass().getSimpleName().equals("TeamActivity")){
                    FirebaseDatabase.getInstance().getReference("Profile").child(snapshot.child("player").getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            holder.player.setText("Favorite Player: " + snapshot1.child("first").getValue().toString() + " " + snapshot1.child("last").getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    holder.player.setText("Favorite Team: " + snapshot.child("team").getValue().toString());
                }

                holder.name.setText(name);

                if (!image.equals("default")) {
                    Glide.with(context.getApplicationContext()).load(Uri.parse(image)).into(holder.image);
                }

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

        TextView name, player;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.support_name);
            player = itemView.findViewById(R.id.support_player);
            image = itemView.findViewById(R.id.support_image);
        }
    }
}
