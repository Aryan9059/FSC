package com.fizanyatik.sportsclub.Dialog;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.fizanyatik.sportsclub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class EditPlayerDialog extends DialogFragment {
    ImageView add_player_back;
    TextInputEditText certificate_link_tv, public_tv, public_link_tv, private_tv, private_link_tv;
    TextInputLayout certificate_layout;
    MaterialSwitch certificate_switch;
    MaterialSwitch interests_switch, stats_switch, links_switch, ratings_switch;
    TextInputEditText first, last, nickname, birthplace, birthdate, shirt;
    TextInputEditText national, ipl, batting_strength, bowling_strength, player, personality;
    TextInputEditText match_edt, runs_edt, wickets_edt, average_edt, strike_edt, economy_edt;
    MaterialAutoCompleteTextView batting, bowling, role_player;
    Slider batting_slider, bowling_slider, fielding_slider, wicket_keeping_slider, speed_slider, stamina_slider;
    LinearLayout interests_ll, stats_ll, links_ll, ratings_ll;
    Button add_player_upload;
    FirebaseAuth mauth;
    DatabaseReference reference;
    String player_id, first_str, last_str, shirt_str, batting_str, bowling_str, nickname_str, birthplace_str, birthdate_str, role_str;
    String interests_str, links_str, stats_str, ratings_str, runs, wicket, average, match, economy, strike;
    String national_str, ipl_str, batting_strength_str, bowling_strength_str, cricketer_str, personality_str;
    String rate_batting, rate_bowling, rate_fielding, rate_wicket_keeping, rate_speed, rate_statistics;
    String certificate_link_str, public_profile_str, private_message_str, public_profile_link_str, private_message_link_str;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_player_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        add_player_upload = view.findViewById(R.id.add_player_upload);
        add_player_back = view.findViewById(R.id.add_player_back);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.screen_background, typedValue,true);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().setNavigationBarColor(typedValue.data);

        first = view.findViewById(R.id.first_name);
        last = view.findViewById(R.id.last_name);
        nickname = view.findViewById(R.id.nickname);
        birthplace = view.findViewById(R.id.birthplace);
        birthdate = view.findViewById(R.id.birthday);
        batting = view.findViewById(R.id.batter);
        bowling = view.findViewById(R.id.bowling);
        add_player_upload = view.findViewById(R.id.add_player_upload);
        add_player_back = view.findViewById(R.id.add_player_back);
        role_player = view.findViewById(R.id.role);
        shirt = view.findViewById(R.id.shirt_no);

        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker dialogs = MaterialDatePicker.Builder.datePicker().setTitleText("Select Birthdate").build();
                dialogs.show(getActivity().getSupportFragmentManager(), "tag");
                dialogs.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                        calendar.setTimeInMillis(Long.parseLong(selection.toString()));
                        birthdate.setText(format.format(calendar.getTime()));
                    }
                });
            }
        });

        national = view.findViewById(R.id.national);
        ipl = view.findViewById(R.id.ipl);
        player = view.findViewById(R.id.cricketer);
        personality = view.findViewById(R.id.personality_edt);
        batting_strength = view.findViewById(R.id.batting_strength);
        bowling_strength = view.findViewById(R.id.bowling_strength);

        match_edt = view.findViewById(R.id.matches_edt);
        runs_edt = view.findViewById(R.id.runs_edt);
        wickets_edt = view.findViewById(R.id.wickets_edt);
        average_edt = view.findViewById(R.id.average_edt);
        strike_edt = view.findViewById(R.id.strike_edt);
        economy_edt = view.findViewById(R.id.economy_edt);

        batting_slider = view.findViewById(R.id.batting_slider);
        bowling_slider = view.findViewById(R.id.bowling_slider);
        fielding_slider = view.findViewById(R.id.fielding_slider);
        wicket_keeping_slider = view.findViewById(R.id.wicket_keeping_slider);
        speed_slider = view.findViewById(R.id.speed_slider);
        stamina_slider = view.findViewById(R.id.stamina_slider);

        certificate_link_tv = view.findViewById(R.id.certificate);
        certificate_layout = view.findViewById(R.id.certificate_parent);
        public_tv = view.findViewById(R.id.public_name);
        certificate_switch = view.findViewById(R.id.certificateSwitch);
        private_link_tv = view.findViewById(R.id.private_link);
        public_link_tv = view.findViewById(R.id.public_link);
        private_tv = view.findViewById(R.id.private_name);
        Bundle bundle = getArguments();
        player_id = bundle.getString("ID", "");

        interests_ll = view.findViewById(R.id.interests_ll);
        links_ll = view.findViewById(R.id.links_ll);
        stats_ll = view.findViewById(R.id.stats_ll);
        ratings_ll = view.findViewById(R.id.ratings_ll);

        interests_switch = view.findViewById(R.id.interests_switch);
        interests_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    interests_ll.setVisibility(View.VISIBLE);
                } else {
                    interests_ll.setVisibility(View.GONE);
                }
            }
        });

        ratings_switch = view.findViewById(R.id.ratings_switch);
        ratings_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ratings_ll.setVisibility(View.VISIBLE);
                } else {
                    ratings_ll.setVisibility(View.GONE);
                }
            }
        });

        links_switch = view.findViewById(R.id.links_switch);
        links_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    links_ll.setVisibility(View.VISIBLE);
                } else {
                    links_ll.setVisibility(View.GONE);
                }
            }
        });

        certificate_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    certificate_layout.setVisibility(View.VISIBLE);
                } else {
                    certificate_layout.setVisibility(View.GONE);
                }
            }
        });

        stats_switch = view.findViewById(R.id.stats_switch);
        stats_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    stats_ll.setVisibility(View.VISIBLE);
                } else {
                    stats_ll.setVisibility(View.GONE);
                }
            }
        });

        mauth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference("Profile").child(player_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                first_str = snapshot.child("first").getValue().toString();
                first.setText(first_str);
                last_str = snapshot.child("last").getValue().toString();
                last.setText(last_str);
                shirt_str = snapshot.child("shirt").getValue().toString();
                shirt.setText(shirt_str);
                batting_str = snapshot.child("batting").getValue().toString();
                batting.setText(batting_str);
                bowling_str = snapshot.child("bowling").getValue().toString();
                bowling.setText(bowling_str);
                nickname_str = snapshot.child("nickname").getValue().toString();
                nickname.setText(nickname_str);
                birthplace_str = snapshot.child("birthplace").getValue().toString();
                birthplace.setText(birthplace_str);
                birthdate_str = snapshot.child("birthdate").getValue().toString();
                birthdate.setText(birthdate_str);
                role_str = snapshot.child("role").getValue().toString();
                role_player.setText(role_str);

                ratings_str = snapshot.child("ratings").getValue().toString();
                if (ratings_str.equals("yes")){
                    ratings_switch.setChecked(true);
                    rate_batting = snapshot.child("rate_batting").getValue().toString();
                    batting_slider.setValue(Float.parseFloat(rate_batting));
                    rate_bowling = snapshot.child("rate_bowling").getValue().toString();
                    bowling_slider.setValue(Float.parseFloat(rate_bowling));
                    rate_fielding = snapshot.child("rate_fielding").getValue().toString();
                    fielding_slider.setValue(Float.parseFloat(rate_fielding));
                    rate_wicket_keeping = snapshot.child("rate_wicket_keeping").getValue().toString();
                    wicket_keeping_slider.setValue(Float.parseFloat(rate_wicket_keeping));
                    rate_speed = snapshot.child("rate_speed").getValue().toString();
                    speed_slider.setValue(Float.parseFloat(rate_speed));
                    rate_statistics = snapshot.child("rate_stamina").getValue().toString();
                    stamina_slider.setValue(Float.parseFloat(rate_statistics));
                }

                interests_str = snapshot.child("interests").getValue().toString();
                if (interests_str.equals("yes")){
                    interests_switch.setChecked(true);
                    national_str = snapshot.child("int_national").getValue().toString();
                    national.setText(national_str);
                    ipl_str = snapshot.child("int_ipl").getValue().toString();
                    ipl.setText(ipl_str);
                    batting_strength_str = snapshot.child("int_batting").getValue().toString();
                    batting_strength.setText(batting_strength_str);
                    bowling_strength_str = snapshot.child("int_bowling").getValue().toString();
                    bowling_strength.setText(bowling_strength_str);
                    cricketer_str = snapshot.child("int_favorite").getValue().toString();
                    player.setText(cricketer_str);
                    personality_str = snapshot.child("int_type").getValue().toString();
                    personality.setText(personality_str);
                }

                stats_str = snapshot.child("stats").getValue().toString();
                if (stats_str.equals("yes")){
                    stats_switch.setChecked(true);
                    runs = snapshot.child("stats_runs").getValue().toString();
                    runs_edt.setText(runs);
                    wicket = snapshot.child("stats_wicket").getValue().toString();
                    wickets_edt.setText(wicket);
                    average = snapshot.child("stats_average").getValue().toString();
                    average_edt.setText(average);
                    economy = snapshot.child("stats_economy").getValue().toString();
                    economy_edt.setText(economy);
                    strike = snapshot.child("stats_strike").getValue().toString();
                    strike_edt.setText(strike);
                    match = snapshot.child("stats_match").getValue().toString();
                    match_edt.setText(match);
                }

                links_str = snapshot.child("links").getValue().toString();
                if (links_str.equals("yes")){
                    links_switch.setChecked(true);
                    certificate_link_str = snapshot.child("link_certificate_link").getValue().toString();
                    if (!certificate_link_str.equals("default")){
                        certificate_switch.setChecked(true);
                        certificate_layout.setVisibility(View.VISIBLE);
                        certificate_link_tv.setText(certificate_link_str);
                    }
                    public_profile_link_str = snapshot.child("link_public_profile_link").getValue().toString();
                    public_link_tv.setText(public_profile_link_str);
                    private_message_link_str = snapshot.child("link_private_message_link").getValue().toString();
                    private_link_tv.setText(private_message_link_str);
                    public_profile_str = snapshot.child("link_public_profile").getValue().toString();
                    public_tv.setText(public_profile_str);
                    private_message_str = snapshot.child("link_private_message").getValue().toString();
                    private_tv.setText(private_message_str);
                }

                try {
                    String[] batter = getResources().getStringArray(R.array.batter_type);
                    ArrayAdapter arrayAdapterBat = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, batter);
                    batting.setAdapter(arrayAdapterBat);

                    String[] bowler = getResources().getStringArray(R.array.bowler_type);
                    ArrayAdapter arrayAdapterBowl = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, bowler);
                    bowling.setAdapter(arrayAdapterBowl);

                    String[] role = getResources().getStringArray(R.array.role);
                    ArrayAdapter arrayAdapterRole = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, role);
                    role_player.setAdapter(arrayAdapterRole);

                } catch (Exception e){
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add_player_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_player_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                dismiss();
            }
        });

        add_player_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_player_upload.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                if(!first.getText().toString().equals("") && !last.getText().toString().equals("") && !nickname.getText().toString().equals("") &&
                        !shirt.getText().toString().equals("") && !birthplace.getText().toString().equals("") &&
                        !birthdate.getText().toString().equals("")){
                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
                    dialogBuilder.setView(R.layout.progress_dialog)
                            .setCancelable(false).create();

                    AlertDialog materialDialogs = dialogBuilder.create();
                    materialDialogs.show();

                    add_player_upload.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    reference = FirebaseDatabase.getInstance().getReference("Profile").child(player_id);

                    final Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("first", first.getText().toString());
                    hashMap.put("last", last.getText().toString());
                    hashMap.put("birthdate", birthdate.getText().toString());
                    hashMap.put("birthplace", birthplace.getText().toString());
                    hashMap.put("shirt", shirt.getText().toString());
                    hashMap.put("nickname", nickname.getText().toString());
                    hashMap.put("role", role_player.getText().toString());
                    hashMap.put("batting", batting.getText().toString());
                    hashMap.put("bowling", bowling.getText().toString());

                    if (stats_switch.isChecked()){
                        hashMap.put("stats", "yes");
                        hashMap.put("stats_average", average_edt.getText().toString());
                        hashMap.put("stats_economy", economy_edt.getText().toString());
                        hashMap.put("stats_runs", runs_edt.getText().toString());
                        hashMap.put("stats_wicket", wickets_edt.getText().toString());
                        hashMap.put("stats_match", match_edt.getText().toString());
                        hashMap.put("stats_strike", strike_edt.getText().toString());
                    } else {
                        hashMap.put("stats", "no");
                    }

                    if (ratings_switch.isChecked()){
                        hashMap.put("ratings", "yes");
                        hashMap.put("rate_batting", batting_slider.getValue());
                        hashMap.put("rate_bowling", bowling_slider.getValue());
                        hashMap.put("rate_fielding", fielding_slider.getValue());
                        hashMap.put("rate_wicket_keeping", wicket_keeping_slider.getValue());
                        hashMap.put("rate_speed", speed_slider.getValue());
                        hashMap.put("rate_stamina", stamina_slider.getValue());
                    } else {
                        hashMap.put("ratings", "no");
                    }

                    if (interests_switch.isChecked()){
                        hashMap.put("interests", "yes");
                        hashMap.put("int_national", national.getText().toString());
                        hashMap.put("int_ipl", ipl.getText().toString());
                        hashMap.put("int_batting", batting_strength.getText().toString());
                        hashMap.put("int_bowling", bowling_strength.getText().toString());
                        hashMap.put("int_favorite", player.getText().toString());
                        hashMap.put("int_type", personality.getText().toString());
                    } else {
                        hashMap.put("interests", "no");
                    }

                    if (links_switch.isChecked()){
                        hashMap.put("links", "yes");
                        if (!certificate_switch.isChecked()){
                            hashMap.put("link_certificate_link", "default");
                        } else {
                            if (certificate_link_tv.getText().toString().equals("")){
                                hashMap.put("link_certificate_link", "default");
                            } else {
                                hashMap.put("link_certificate_link", certificate_link_tv.getText().toString());
                            }
                        }
                        hashMap.put("link_public_profile", public_tv.getText().toString());
                        hashMap.put("link_private_message", private_tv.getText().toString());
                        hashMap.put("link_public_profile_link", public_link_tv.getText().toString());
                        hashMap.put("link_private_message_link", private_link_tv.getText().toString());
                    } else {
                        hashMap.put("links", "no");
                    }

                    reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                                materialDialogs.dismiss();
                                dismiss();

                            } else {
                                Toast.makeText(getContext(), "Sign in unsuccessful. " + task.getException() + "", Toast.LENGTH_LONG).show();
                                materialDialogs.dismiss();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Enter details", Toast.LENGTH_SHORT).show();
                }}
        });

    }
}
