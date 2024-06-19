package com.fizanyatik.sportsclub.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.Dialog.UpdateInterestsDialog;
import com.fizanyatik.sportsclub.Dialog.UpdateLinksDialog;
import com.fizanyatik.sportsclub.Dialog.PasswordDialog;
import com.fizanyatik.sportsclub.Dialog.UpdatePlayerDialog;
import com.fizanyatik.sportsclub.Dialog.UpdateSupporter;
import com.fizanyatik.sportsclub.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    ImageView profile_back, profile_picture;
    MaterialCardView change_pic;
    String email, username;
    TextView email_tv, name_tv;
    ConstraintLayout password_cl, profile_cl, stats_cl, sign_out_cl, interests_cl, links_cl;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    DatabaseReference reference;
    SharedPreferences prefs;

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
        setContentView(R.layout.activity_profile);

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

        change_pic = findViewById(R.id.pic_change);
        profile_picture = findViewById(R.id.profile_picture);
        change_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_picture.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                openImage();
            }
        });
        email_tv = findViewById(R.id.email_tv);
        name_tv = findViewById(R.id.name_tv);
        password_cl = findViewById(R.id.password_cl);
        profile_cl = findViewById(R.id.profile_cl);
        stats_cl = findViewById(R.id.stats_cl);
        sign_out_cl = findViewById(R.id.signout_cl);
        interests_cl = findViewById(R.id.interests_cl);
        links_cl = findViewById(R.id.links_cl);

        storageReference = FirebaseStorage.getInstance().getReference("Profile");

        password_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordDialog passwordDialog = new PasswordDialog();
                passwordDialog.show(getSupportFragmentManager(), "passwordDialog");
            }
        });

        interests_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new UpdateInterestsDialog();
                dialog.show(getSupportFragmentManager(), "interests");
            }
        });

        links_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new UpdateLinksDialog();
                dialog.show(getSupportFragmentManager(), "links");
            }
        });

        stats_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:fizanyatiksc@gmail.com?subject=Statistics%20On%20App&body=I request you to change my statistics in FSC App."));
                startActivity(emailIntent);
            }
        });

        sign_out_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_out_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });

        profile_back = findViewById(R.id.profile_back);
        profile_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Profile").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("type").getValue().toString().equals("player")){
                    profile_cl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogFragment dialog = new UpdatePlayerDialog();
                            dialog.show(getSupportFragmentManager(), "profile");
                        }
                    });
                } else {
                    interests_cl.setVisibility(View.GONE);
                    links_cl.setVisibility(View.GONE);
                    stats_cl.setVisibility(View.GONE);

                    profile_cl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogFragment dialog = new UpdateSupporter();
                            Bundle args = new Bundle();
                            args.putString("team", dataSnapshot.child("team").getValue().toString());
                            args.putString("player", dataSnapshot.child("player").getValue().toString());
                            dialog.setArguments(args);
                            dialog.show(getSupportFragmentManager(), "supporter");
                        }
                    });
                }
                name_tv.setText(dataSnapshot.child("first").getValue().toString() + " " + dataSnapshot.child("last").getValue().toString());
                if (dataSnapshot.child("image").getValue().toString().equals("default")){
                    profile_picture.setImageResource(R.drawable.default_pic);
                } else {
                    try {
                        Glide.with(ProfileActivity.this).load(dataSnapshot.child("image").getValue().toString()).into(profile_picture);
                    } catch (Exception exception){
                        Log.e("Error", exception.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        email_tv.setText(email);
        username = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){

        if (imageUri != null){
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
                        String mUri = downloadUri.toString();

                        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        username = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        reference = FirebaseDatabase.getInstance().getReference("Profile").child(username);                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image", mUri);
                        reference.updateChildren(map);
                    }

                    else {
                        Toast.makeText(ProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(ProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            uploadImage();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(ProfileActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            }
            else {
                uploadImage();
            }
        }
    }

}