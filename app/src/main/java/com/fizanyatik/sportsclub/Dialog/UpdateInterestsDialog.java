package com.fizanyatik.sportsclub.Dialog;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class UpdateInterestsDialog extends DialogFragment {
    TextInputEditText national, ipl, batting, bowling, player, personality;
    ImageView add_player_back;
    Button add_player_upload;
    String interests_str, national_str, ipl_str, batting_strength_str, bowling_strength_str, cricketer_str, personality_str;
    FirebaseAuth mauth;
    DatabaseReference reference;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.update_interests_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        national = view.findViewById(R.id.national);
        ipl = view.findViewById(R.id.ipl);
        player = view.findViewById(R.id.cricketer);
        personality = view.findViewById(R.id.personality_edt);
        batting = view.findViewById(R.id.batting_strength);
        add_player_upload = view.findViewById(R.id.add_player_upload);
        add_player_back = view.findViewById(R.id.add_player_back);
        bowling = view.findViewById(R.id.bowling_strength);

        mauth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference("Profile").child(mauth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                interests_str = snapshot.child("interests").getValue().toString();
                if (interests_str.equals("yes")){
                    national_str = snapshot.child("int_national").getValue().toString();
                    national.setText(national_str);
                    ipl_str = snapshot.child("int_ipl").getValue().toString();
                    ipl.setText(ipl_str);
                    batting_strength_str = snapshot.child("int_batting").getValue().toString();
                    batting.setText(batting_strength_str);
                    bowling_strength_str = snapshot.child("int_bowling").getValue().toString();
                    bowling.setText(bowling_strength_str);
                    cricketer_str = snapshot.child("int_favorite").getValue().toString();
                    player.setText(cricketer_str);
                    personality_str = snapshot.child("int_type").getValue().toString();
                    personality.setText(personality_str);
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
                if(!national.getText().toString().equals("") && !ipl.getText().toString().equals("") && !personality.getText().toString().equals("") &&
                        !player.getText().toString().equals("") && !batting.getText().toString().equals("") &&
                        !bowling.getText().toString().equals("")){
                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
                    dialogBuilder.setView(R.layout.progress_dialog)
                            .setCancelable(false).create();

                    AlertDialog materialDialogs = dialogBuilder.create();
                    materialDialogs.show();

                    add_player_upload.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    reference = FirebaseDatabase.getInstance().getReference("Profile").child(mauth.getCurrentUser().getUid());

                    final Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("interests", "yes");
                    hashMap.put("int_national", national.getText().toString());
                    hashMap.put("int_ipl", ipl.getText().toString());
                    hashMap.put("int_batting", batting.getText().toString());
                    hashMap.put("int_bowling", bowling.getText().toString());
                    hashMap.put("int_favorite", player.getText().toString());
                    hashMap.put("int_type", personality.getText().toString());

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
