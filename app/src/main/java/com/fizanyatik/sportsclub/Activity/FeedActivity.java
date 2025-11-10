package com.fizanyatik.sportsclub.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.Dialog.AddFeedDialog;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FeedActivity extends AppCompatActivity {
    String image, body, date, title, type, parent, admin;
    ImageView imageView, back_btn, edit_btn;
    TextView title_tv, date_tv;
    DatabaseReference reference;
    TextView body_tv;
    MaterialToolbar toolbar_feed;
    SharedPreferences prefs;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("Themes", MODE_PRIVATE);
        String themeMode = prefs.getString("current", "");
        switch (themeMode){
            case "":
            case "Main":
                setTheme(R.style.Theme_Main);
                break;
            case "Blue":
                setTheme(R.style.Theme_Blue);
                break;
            case "Yellow":
                setTheme(R.style.Theme_Yellow);
                break;
            case "Pink":
                setTheme(R.style.Theme_Pink);
                break;
            case "Green":
                setTheme(R.style.Theme_Green);
                break;
            case "Teal":
                setTheme(R.style.Theme_Teal);
                break;
            case "Purple":
                setTheme(R.style.Theme_Purple);
                break;
            case "Red":
                setTheme(R.style.Theme_Red);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        TypedValue typedValue = new TypedValue();
        TypedValue typedValue2 = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.navigation, typedValue,true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            theme.resolveAttribute(R.attr.bar_background, typedValue,true);
            theme.resolveAttribute(R.attr.screen_background, typedValue2,true);
            getWindow().setStatusBarColor(typedValue.data);
            getWindow().setNavigationBarColor(typedValue2.data);
        }

        title_tv = findViewById(R.id.onefeedtitle);
        body_tv = findViewById(R.id.onefeedtext);
        date_tv = findViewById(R.id.onefeeddate);
        imageView = findViewById(R.id.iamgeonefeed);
        back_btn = findViewById(R.id.feed_back);
        edit_btn = findViewById(R.id.edit_feed);
        toolbar_feed = findViewById(R.id.toolbar_feed);

        image = getIntent().getStringExtra("image");
        body = getIntent().getStringExtra("body");
        date = getIntent().getStringExtra("date");
        title = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");
        parent = getIntent().getStringExtra("parent");

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back_btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Admin");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                admin = snapshot.getValue().toString();
                if(admin.contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    edit_btn.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                DialogFragment dialog = new AddFeedDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("feed_inside", true);
                bundle.putString("image", image);
                bundle.putString("body", body);
                bundle.putString("date", date);
                bundle.putString("title", title);
                bundle.putString("type", type);
                bundle.putString("parent", parent);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "feed");
            }
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            TextView type_feed = findViewById(R.id.text_type_feed);
            type_feed.setText(type);
        } else {
            toolbar_feed.setTitle(type);
        }

        title_tv.setText(title);
        body_tv.setText(Html.fromHtml(body));
        date_tv.setText("Dated: " + date);

        if (!image.equals("no")) {
            Glide.with(getApplicationContext()).load(image).into(imageView);
        }
    }
}