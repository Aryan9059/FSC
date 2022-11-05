package com.fizanyatik.sportsclub;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordDialog extends AppCompatDialogFragment {
    private TextInputEditText pass, old_pass;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.password_dialog, null);

        pass = view.findViewById(R.id.password_edt);
        old_pass = view.findViewById(R.id.old_password_edt);

        builder.setView(view)
                .setTitle("Change Password")
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(pass.getText().length() < 8){
                            Toast.makeText(getContext(), "Password should be at least of 8 digits", Toast.LENGTH_LONG).show();
                        } else if (old_pass.getText().toString().equals("")){
                            Toast.makeText(getContext(), "Enter old password", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), old_pass.getText().toString());
                            user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    user.updatePassword(pass.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //Toast.makeText(getActivity(), "Updated successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Toast.makeText(getActivity(), "Error encountered", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Toast.makeText(getContext(), "You may have entered wrong current password", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}
