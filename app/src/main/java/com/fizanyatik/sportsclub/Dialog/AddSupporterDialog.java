package com.fizanyatik.sportsclub.Dialog;

import static android.app.Activity.RESULT_OK;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import com.fizanyatik.sportsclub.Activity.HomeActivity;
import com.fizanyatik.sportsclub.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class AddSupporterDialog extends DialogFragment {
    TextInputEditText first, last, birthdate, email, password;
    ImageView add_player_back, profile;
    Button add_player_upload;
    FirebaseAuth auth;
    CardView change_pic;
    StorageReference storageReference;
    MaterialAutoCompleteTextView team, player;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri = null;
    private StorageTask uploadTask;
    DatabaseReference reference;
    ArrayList<String> players, players_parent;
    String mUri, selected_parent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_supporter_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        first = view.findViewById(R.id.first_name);
        last = view.findViewById(R.id.last_name);
        birthdate = view.findViewById(R.id.birthday);
        team = view.findViewById(R.id.team_choose);
        player = view.findViewById(R.id.player_choose);
        add_player_upload = view.findViewById(R.id.add_player_upload);
        add_player_back = view.findViewById(R.id.add_player_back);
        profile = view.findViewById(R.id.player_profile);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        change_pic = view.findViewById(R.id.pic_change);

        players = new ArrayList<>();
        players_parent = new ArrayList<>();

        selected_parent = "none";

        auth = FirebaseAuth.getInstance();

        String[] type = getResources().getStringArray(R.array.teams);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, type);
        team.setAdapter(arrayAdapter);

        reference = FirebaseDatabase.getInstance().getReference("Profile");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players_parent.clear();
                players.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String type = dataSnapshot.child("type").getValue().toString();
                    String parent = dataSnapshot.child("parent").getValue().toString();
                    String name = dataSnapshot.child("first").getValue().toString() + " " + dataSnapshot.child("last").getValue().toString();
                    if (type.equals("player")){
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
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                if (!selected_parent.equals("none") && !email.getText().toString().isEmpty() && !(password.getText().toString().length() < 8) && !first.getText().toString().isEmpty() && !last.getText().toString().isEmpty() && !birthdate.getText().toString().isEmpty()) {
                    CreateUserAccount();
                } else {
                    Toast.makeText(getContext(), "Enter details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        change_pic.getBackground().setAlpha(128);
        change_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
    }

            private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void CreateUserAccount() {

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        dialogBuilder.setView(R.layout.progress_dialog)
                .setCancelable(false).create();

        AlertDialog materialDialogs = dialogBuilder.create();
        materialDialogs.show();

        auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    reference = FirebaseDatabase.getInstance().getReference("Profile").child(auth.getCurrentUser().getUid());
                    String versionName;
                    try{
                        versionName = getContext().getPackageManager()
                                .getPackageInfo(getContext().getPackageName(), 0).versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        versionName = "2.0_Chokito";
                    }

                    String finalVersionName = versionName;

                    final HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("first", first.getText().toString());
                    hashMap.put("last", last.getText().toString());
                    hashMap.put("email", email.getText().toString());
                    hashMap.put("birthdate", birthdate.getText().toString());
                    if (imageUri == null){hashMap.put("image", "default");} else {hashMap.put("image", mUri);}
                    hashMap.put("team", team.getText().toString());
                    hashMap.put("parent", auth.getCurrentUser().getUid());
                    hashMap.put("type", "supporter");
                    hashMap.put("player", selected_parent);
                    hashMap.put("version", finalVersionName);

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                if (team.getText().toString().equals("Superchargers")){
                                    reference = FirebaseDatabase.getInstance().getReference("Teams").child("NSC").child("support").child(auth.getCurrentUser().getUid());
                                } else {
                                    reference = FirebaseDatabase.getInstance().getReference("Teams").child("SBR").child("support").child(auth.getCurrentUser().getUid());
                                }

                                final HashMap<String, String> hashMap2 = new HashMap<>();
                                hashMap2.put("parent", auth.getCurrentUser().getUid());
                                reference.setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        reference = FirebaseDatabase.getInstance().getReference("Profile").child(selected_parent).child("support").child(auth.getCurrentUser().getUid());
                                        final HashMap<String, String> hashMap3 = new HashMap<>();
                                        hashMap3.put("parent", auth.getCurrentUser().getUid());
                                        reference.setValue(hashMap3).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Intent home = new Intent(getActivity(), HomeActivity.class);
                                                Toast.makeText(getContext(), "Signed in successfully", Toast.LENGTH_SHORT).show();
                                                materialDialogs.dismiss();
                                                startActivity(home);
                                                getActivity().finish();
                                            }
                                        });
                                    }
                                });

                            } else {
                                Toast.makeText(getContext(), "Sign in unsuccessful. " + task.getException(), Toast.LENGTH_LONG).show();
                                materialDialogs.dismiss();
                            }
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    materialDialogs.dismiss();
                }
            }
        });
    }

    private void uploadImage(){

        if (imageUri != null){
            storageReference = FirebaseStorage.getInstance().getReference("Profile");
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        mUri = downloadUri.toString();
                    }

                    else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            profile.setImageURI(imageUri);
            uploadImage();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            }
            else {
                uploadImage();
            }
        }
    }
}
