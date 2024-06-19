package com.fizanyatik.sportsclub.Dialog;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class UpdateLinksDialog extends DialogFragment {

    TextInputEditText certificate_link_tv, public_tv, public_link_tv, private_tv, private_link_tv;
    TextInputLayout certificate_layout;
    MaterialSwitch certificate_switch;
    ImageView add_player_back;
    Button add_player_upload;
    String links_str, certificate_link_str, public_profile_str, private_message_str, public_profile_link_str, private_message_link_str;
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
        return inflater.inflate(R.layout.update_links_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        certificate_link_tv = view.findViewById(R.id.certificate);
        certificate_layout = view.findViewById(R.id.certificate_parent);
        public_tv = view.findViewById(R.id.public_name);
        certificate_switch = view.findViewById(R.id.certificateSwitch);
        private_link_tv = view.findViewById(R.id.private_link);
        public_link_tv = view.findViewById(R.id.public_link);
        private_tv = view.findViewById(R.id.private_name);
        add_player_upload = view.findViewById(R.id.add_player_upload);
        add_player_back = view.findViewById(R.id.add_player_back);

        mauth = FirebaseAuth.getInstance();
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

        reference = FirebaseDatabase.getInstance().getReference("Profile").child(mauth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                links_str = snapshot.child("links").getValue().toString();
                if (links_str.equals("yes")){
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
                if(!public_tv.getText().toString().equals("") && !public_link_tv.getText().toString().equals("") && !private_link_tv.getText().toString().equals("") &&
                        !private_tv.getText().toString().equals("")){
                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
                    dialogBuilder.setView(R.layout.progress_dialog)
                            .setCancelable(false).create();

                    AlertDialog materialDialogs = dialogBuilder.create();
                    materialDialogs.show();

                    add_player_upload.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    reference = FirebaseDatabase.getInstance().getReference("Profile").child(mauth.getCurrentUser().getUid());

                    final Map<String, Object> hashMap = new HashMap<>();
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
