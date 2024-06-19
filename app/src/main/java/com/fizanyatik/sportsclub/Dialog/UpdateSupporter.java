package com.fizanyatik.sportsclub.Dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.fizanyatik.sportsclub.BuildConfig;
import com.fizanyatik.sportsclub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class UpdateSupporter extends DialogFragment {
    TextInputEditText first, last, birthdate;
    ImageView add_player_back;
    Button add_player_upload;
    String former_team, former_player;
    String image_str, team_str, player_str, selected_parent, type;
    FirebaseAuth mauth;
    ArrayList<String> players, players_parent;
    MaterialAutoCompleteTextView team, player;
    DatabaseReference reference, reference1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.update_supporter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        first = view.findViewById(R.id.first_name);
        last = view.findViewById(R.id.last_name);
        birthdate = view.findViewById(R.id.birthday);
        add_player_upload = view.findViewById(R.id.add_player_upload);
        add_player_back = view.findViewById(R.id.add_player_back);
        team = view.findViewById(R.id.team_choose);
        player = view.findViewById(R.id.player_choose);

        former_team = getArguments().getString("team");
        former_player = getArguments().getString("player");

        mauth = FirebaseAuth.getInstance();
        players = new ArrayList<>();
        players_parent = new ArrayList<>();
        selected_parent = "none";

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

        reference = FirebaseDatabase.getInstance().getReference("Profile").child(mauth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                type = snapshot.child("type").getValue().toString();
                String first_str = snapshot.child("first").getValue().toString();
                image_str = snapshot.child("image").getValue().toString();
                first.setText(first_str);
                String last_str = snapshot.child("last").getValue().toString();
                last.setText(last_str);
                String birthdate_str = snapshot.child("birthdate").getValue().toString();
                birthdate.setText(birthdate_str);
                team_str = snapshot.child("team").getValue().toString();
                team.setText(team_str);

                try {
                    String[] selected_team = getActivity().getResources().getStringArray(R.array.teams);
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, selected_team);
                    team.setAdapter(arrayAdapter);
                } catch (Exception e){
                    //Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }

                player_str = snapshot.child("player").getValue().toString();
                selected_parent = player_str;
                reference = FirebaseDatabase.getInstance().getReference("Profile").child(player_str);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        String name = snapshot2.child("first").getValue().toString() + " " + snapshot2.child("last").getValue().toString();
                        player.setText(name);

                        reference1 = FirebaseDatabase.getInstance().getReference("Profile");
                        reference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                players_parent.clear();
                                players.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String typer = dataSnapshot.child("type").getValue().toString();
                                    String parent = dataSnapshot.child("parent").getValue().toString();
                                    String name = dataSnapshot.child("first").getValue().toString() + " " + dataSnapshot.child("last").getValue().toString();
                                    if (typer.equals("player")){
                                        players.add(name);
                                        players_parent.add(parent);
                                    }
                                }

                                player.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        selected_parent = players_parent.get(position);
                                    }
                                });

                                try {
                                    ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, players);
                                    player.setAdapter(arrayAdapter);
                                } catch (Exception e){
                                    //Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                if(!first.getText().toString().equals("") && !last.getText().toString().equals("") && !team.getText().toString().equals("") && !selected_parent.equals("none") && !birthdate.getText().toString().equals("")){
                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
                    dialogBuilder.setView(R.layout.progress_dialog)
                            .setCancelable(false).create();

                    AlertDialog materialDialogs = dialogBuilder.create();
                    materialDialogs.show();

                    add_player_upload.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    reference = FirebaseDatabase.getInstance().getReference("Profile").child(mauth.getCurrentUser().getUid());

                    final HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("first", first.getText().toString());
                    hashMap.put("last", last.getText().toString());
                    hashMap.put("birthdate", birthdate.getText().toString());
                    hashMap.put("image", image_str);
                    hashMap.put("parent", mauth.getCurrentUser().getUid());
                    hashMap.put("team", team.getText().toString());
                    hashMap.put("version", BuildConfig.VERSION_NAME);
                    hashMap.put("email", mauth.getCurrentUser().getEmail());
                    hashMap.put("player", selected_parent);
                    hashMap.put("type", type);

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                if (!team.getText().toString().equals(former_team)){
                                    if(former_team.equals("Superchargers")){
                                        FirebaseDatabase.getInstance().getReference("Teams").child("NSC").child("support").child(mauth.getCurrentUser().getUid()).removeValue();

                                        reference = FirebaseDatabase.getInstance().getReference("Teams").child("SBR").child("support").child(mauth.getCurrentUser().getUid());
                                        final HashMap<String, String> hashMap2 = new HashMap<>();
                                        hashMap2.put("parent", mauth.getCurrentUser().getUid());
                                        reference.setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!selected_parent.equals(former_player)){
                                                    FirebaseDatabase.getInstance().getReference("Profile").child(former_player).child("support").child(mauth.getCurrentUser().getUid()).removeValue();

                                                    reference = FirebaseDatabase.getInstance().getReference("Profile").child(selected_parent).child("support").child(mauth.getCurrentUser().getUid());
                                                    final HashMap<String, String> hashMap3 = new HashMap<>();
                                                    hashMap3.put("parent", mauth.getCurrentUser().getUid());
                                                    reference.setValue(hashMap3).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    } else {
                                        FirebaseDatabase.getInstance().getReference("Teams").child("SBR").child("support").child(mauth.getCurrentUser().getUid()).removeValue();

                                        reference = FirebaseDatabase.getInstance().getReference("Teams").child("NSC").child("support").child(mauth.getCurrentUser().getUid());
                                        final HashMap<String, String> hashMap2 = new HashMap<>();
                                        hashMap2.put("parent", mauth.getCurrentUser().getUid());
                                        reference.setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!selected_parent.equals(former_player)){
                                                    FirebaseDatabase.getInstance().getReference("Profile").child(former_player).child("support").child(mauth.getCurrentUser().getUid()).removeValue();

                                                    reference = FirebaseDatabase.getInstance().getReference("Profile").child(selected_parent).child("support").child(mauth.getCurrentUser().getUid());
                                                    final HashMap<String, String> hashMap3 = new HashMap<>();
                                                    hashMap3.put("parent", mauth.getCurrentUser().getUid());
                                                    reference.setValue(hashMap3).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                } else if (!selected_parent.equals(former_player)){
                                    FirebaseDatabase.getInstance().getReference("Profile").child(former_player).child("support").child(mauth.getCurrentUser().getUid()).removeValue();

                                    reference = FirebaseDatabase.getInstance().getReference("Profile").child(selected_parent).child("support").child(mauth.getCurrentUser().getUid());
                                    final HashMap<String, String> hashMap3 = new HashMap<>();
                                    hashMap3.put("parent", mauth.getCurrentUser().getUid());
                                    reference.setValue(hashMap3).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
                                }

                                Toast.makeText(getContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
                                materialDialogs.dismiss();
                                dismiss();

                            } else {
                                Toast.makeText(getContext(), "Sign in unsuccessful. " + task.getException(), Toast.LENGTH_LONG).show();
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
