package com.fizanyatik.sportsclub.Dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
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

public class UpdatePlayerDialog extends DialogFragment {
    TextInputEditText first, last, nickname, birthplace, birthdate, shirt;
    ImageView add_player_back;
    Button add_player_upload;
    FirebaseAuth mauth;
    MaterialAutoCompleteTextView batting, bowling, role_player;
    DatabaseReference reference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.update_player_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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

        mauth = FirebaseAuth.getInstance();

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
                first.setText(snapshot.child("first").getValue().toString());
                last.setText(snapshot.child("last").getValue().toString());
                shirt.setText(snapshot.child("shirt").getValue().toString());
                batting.setText(snapshot.child("batting").getValue().toString());
                bowling.setText(snapshot.child("bowling").getValue().toString());
                nickname.setText(snapshot.child("nickname").getValue().toString());
                birthplace.setText(snapshot.child("birthplace").getValue().toString());
                birthdate.setText(snapshot.child("birthdate").getValue().toString());
                role_player.setText(snapshot.child("role").getValue().toString());

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
                reference = FirebaseDatabase.getInstance().getReference("Profile").child(mauth.getCurrentUser().getUid());

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
