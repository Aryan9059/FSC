package com.fizanyatik.sportsclub;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_ui, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final FeedList listItem = listViews.get(position);
        holder.title.setText(listItem.getTitle());
        holder.body.setText(Html.fromHtml(listItem.getText()));
        holder.date.setText(listItem.getDate());
        Glide.with(context).load(listItem.getImage()).into(holder.image);

        holder.feedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FeedActivity.class);
                intent.putExtra("body", listItem.getText());
                intent.putExtra("date", listItem.getDate());
                intent.putExtra("image", listItem.getImage());
                intent.putExtra("title", listItem.getTitle());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listViews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, body, date;
        ImageView image;
        LinearLayout feedLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            feedLayout = itemView.findViewById(R.id.card_click_feed);
            title = itemView.findViewById(R.id.feed_title);
            body = itemView.findViewById(R.id.feed_des);
            date = itemView.findViewById(R.id.feed_time);
            image = itemView.findViewById(R.id.feed_image);
        }
    }
}
