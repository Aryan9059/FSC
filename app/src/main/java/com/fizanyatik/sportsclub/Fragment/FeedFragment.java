package com.fizanyatik.sportsclub.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.fizanyatik.sportsclub.Adapter.FeedAdapter;
import com.fizanyatik.sportsclub.List.FeedList;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import xyz.peridy.shimmerlayout.ShimmerLayout;

public class FeedFragment extends Fragment {
    ShimmerLayout shimmer2;
    FeedList feedTextList;
    EditText search_feed;
    RecyclerView feed_text_recyclerview;
    private List<FeedList> feeditems, feeditems_1;
    MaterialCardView website_pill, youtube_pill,fizanto_pill;
    RecyclerView.Adapter feed_textAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_feed, container, false);

        shimmer2 = root.findViewById(R.id.feed_shimmer);
        feed_text_recyclerview = root.findViewById(R.id.feed_rv);
        search_feed = root.findViewById(R.id.search_feed);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            feed_text_recyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            feed_text_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        feeditems = new ArrayList<>();

        website_pill = root.findViewById(R.id.website_pill);
        youtube_pill = root.findViewById(R.id.youtube_pill);
        fizanto_pill = root.findViewById(R.id.fizanto_pill);

        feeditems_1 = new ArrayList<>();

        website_pill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/fizanyatiksc?igshid=YmMyMTA2M2Y"));
                startActivity(intent);
            }
        });

        youtube_pill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/channel/UCljG6xDx_rvrC4ZCWp38gcA"));
                startActivity(intent);
            }
        });

        fizanto_pill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/c/FizantoFuzz"));
                startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference("Feeds").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feeditems.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String body = dataSnapshot.child("body").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String topic = dataSnapshot.child("topic").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    String type = dataSnapshot.child("type").getValue().toString();
                    String parent = dataSnapshot.child("parent").getValue().toString();

                    feedTextList = new FeedList(body, date, topic, image, type, parent);
                    feeditems.add(feedTextList);
                }

                Collections.reverse(feeditems);
                feed_textAdapter = new FeedAdapter(feeditems, getContext());
                feed_text_recyclerview.setAdapter(feed_textAdapter);
                shimmer2.setVisibility(View.GONE);
                feed_text_recyclerview.setVisibility(View.VISIBLE);
                feed_textAdapter.notifyDataSetChanged();

                search_feed.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (search_feed.getText().toString().equals("")){
                            feeditems_1.clear();
                            feed_textAdapter = new FeedAdapter(feeditems, getContext());
                            feed_text_recyclerview.setAdapter(feed_textAdapter);
                            feed_textAdapter.notifyDataSetChanged();
                        } else {
                            feeditems_1.clear();
                            int i = 0;
                            while(i < feeditems.size()){
                                if(feeditems.get(i).getTitle().toLowerCase().contains(search_feed.getText().toString().toLowerCase())) {
                                    feeditems_1.add(feeditems.get(i));
                                }
                                feed_textAdapter = new FeedAdapter(feeditems_1, getContext());
                                feed_text_recyclerview.setAdapter(feed_textAdapter);
                                feed_textAdapter.notifyDataSetChanged();
                                i++;
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return root;
    }
}