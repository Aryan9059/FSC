package com.fizanyatik.sportsclub.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.Activity.FeedActivity;
import com.fizanyatik.sportsclub.List.FeedList;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>{
        List<FeedList> listViews;
        Context context;

    public FeedAdapter(List<FeedList> listViews, Context context) {
        this.listViews = listViews;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final FeedList listItem = listViews.get(position);
        holder.title.setText(listItem.getTitle());
        holder.body.setText(Html.fromHtml(listItem.getText()));
        holder.date.setText(listItem.getDate());
        holder.type_tv.setText(listItem.getType());

        if (listItem.getType().equals("News")){
            holder.type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.bottom_matches_filled));
        } else if (listItem.getType().equals("Updates")){
            holder.type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_info_24));
        } else {
            holder.type_image.setImageDrawable(context.getResources().getDrawable(R.drawable.icons8_bat_ball_66));
        }

        Glide.with(context).load(listItem.getImage()).into(holder.image);

        holder.feedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedActivity.class);
                intent.putExtra("body", listItem.getText());
                intent.putExtra("date", listItem.getDate());
                intent.putExtra("image", listItem.getImage());
                intent.putExtra("title", listItem.getTitle());
                intent.putExtra("type", listItem.getType());
                intent.putExtra("parent", listItem.getParent());
                context.startActivity(intent);
            }
        });
        FirebaseDatabase.getInstance().getReference("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    holder.feedLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            holder.feedLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(v.getContext());
                            alertDialog1.setTitle("Delete");
                            alertDialog1.setMessage("Do you want to delete this feed from the app?");
                            alertDialog1.setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            FirebaseDatabase.getInstance().getReference("Feeds").child(listItem.getParent()).removeValue();
                                            dialog.dismiss();
                                            Toast.makeText(context, "Feed deleted successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).setCancelable(true)
                                    .setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create();
                            alertDialog1.show();
                            return false;
                        }
                    });
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

        TextView title, date, type_tv;
        TextView body;
        ImageView image, type_image;
        MaterialCardView feedLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            feedLayout = itemView.findViewById(R.id.card_click_feed);
            title = itemView.findViewById(R.id.feed_title);
            body = itemView.findViewById(R.id.feed_des);
            date = itemView.findViewById(R.id.feed_time);
            image = itemView.findViewById(R.id.feed_image);
            type_image = itemView.findViewById(R.id.type_image);
            type_tv = itemView.findViewById(R.id.type_tv);
        }
    }
}
