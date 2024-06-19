package com.fizanyatik.sportsclub.Dialog;

import static android.app.Activity.RESULT_OK;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import com.fizanyatik.sportsclub.BuildConfig;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddPlayerDialog extends DialogFragment{
    TextInputEditText first, last, nickname, birthplace, birthdate, shirt, email, password;
    ImageView add_player_back, profile;
    Button add_player_upload;
    FirebaseAuth mauth;
    CardView change_pic;
    StorageReference storageReference;
    MaterialAutoCompleteTextView team, batting, bowling, role_player;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri = null;
    private StorageTask uploadTask;
    DatabaseReference reference;
    String mUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_player_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        first = view.findViewById(R.id.first_name);
        last = view.findViewById(R.id.last_name);
        nickname = view.findViewById(R.id.nickname);
        birthplace = view.findViewById(R.id.birthplace);
        birthdate = view.findViewById(R.id.birthday);
        team = view.findViewById(R.id.team_choose);
        batting = view.findViewById(R.id.batter);
        bowling = view.findViewById(R.id.bowling);
        add_player_upload = view.findViewById(R.id.add_player_upload);
        add_player_back = view.findViewById(R.id.add_player_back);
        profile = view.findViewById(R.id.player_profile);
        role_player = view.findViewById(R.id.role);
        shirt = view.findViewById(R.id.shirt_no);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        change_pic = view.findViewById(R.id.pic_change);

        mauth = FirebaseAuth.getInstance();

        String[] type = getResources().getStringArray(R.array.teams);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, type);
        team.setAdapter(arrayAdapter);

        String[] batter = getResources().getStringArray(R.array.batter_type);
        ArrayAdapter arrayAdapterBat = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, batter);
        batting.setAdapter(arrayAdapterBat);

        String[] bowler = getResources().getStringArray(R.array.bowler_type);
        ArrayAdapter arrayAdapterBowl = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, bowler);
        bowling.setAdapter(arrayAdapterBowl);

        String[] role = getResources().getStringArray(R.array.role);
        ArrayAdapter arrayAdapterRole = new ArrayAdapter(getContext(), R.layout.drop_down_feed_item, role);
        role_player.setAdapter(arrayAdapterRole);

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
                if(!email.getText().toString().equals("") && !(password.getText().toString().length() < 8) && !first.getText().toString().equals("") && !last.getText().toString().equals("") && !nickname.getText().toString().equals("") &&
                        !shirt.getText().toString().equals("") && !birthplace.getText().toString().equals("") &&
                        !birthdate.getText().toString().equals("")){

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

        mauth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    reference = FirebaseDatabase.getInstance().getReference("Profile").child(mauth.getCurrentUser().getUid());

                    final HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("first", first.getText().toString());
                    hashMap.put("last", last.getText().toString());
                    hashMap.put("email", email.getText().toString());
                    hashMap.put("birthdate", birthdate.getText().toString());
                    hashMap.put("birthplace", birthplace.getText().toString());
                    hashMap.put("shirt", shirt.getText().toString());
                    hashMap.put("nickname", nickname.getText().toString());
                    if (imageUri == null){hashMap.put("image", "default");} else {hashMap.put("image", mUri);}
                    hashMap.put("team", team.getText().toString());
                    hashMap.put("parent", mauth.getCurrentUser().getUid());
                    hashMap.put("role", role_player.getText().toString());
                    hashMap.put("batting", batting.getText().toString());
                    hashMap.put("bowling", bowling.getText().toString());
                    hashMap.put("stats", "no");
                    hashMap.put("links", "no");
                    hashMap.put("type", "player");
                    hashMap.put("interests", "no");
                    hashMap.put("ratings", "no");
                    hashMap.put("captain", "no");
                    hashMap.put("version", BuildConfig.VERSION_NAME);

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                if (team.getText().toString().equals("Superchargers")){
                                    reference = FirebaseDatabase.getInstance().getReference("Teams").child("NSC").child("players").child(mauth.getCurrentUser().getUid());
                                } else {
                                    reference = FirebaseDatabase.getInstance().getReference("Teams").child("SBR").child("players").child(mauth.getCurrentUser().getUid());
                                }

                                final HashMap<String, String> hashMap2 = new HashMap<>();
                                hashMap2.put("parent", mauth.getCurrentUser().getUid());
                                reference.setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent home = new Intent(getActivity(), HomeActivity.class);
                                        Toast.makeText(getContext(), "Signed in successfully", Toast.LENGTH_SHORT).show();
                                        materialDialogs.dismiss();
                                        startActivity(home);
                                        getActivity().finish();
                                    }
                                });

                            } else {
                                Toast.makeText(getContext(), "Sign in unsuccessful. " + task.getException() + "", Toast.LENGTH_LONG).show();
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
