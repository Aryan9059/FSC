package com.fizanyatik.sportsclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    ImageView profile_back;
    String email, username;
    TextInputEditText pass;
    CircleImageView profile_picture;
    LinearProgressIndicator progress1, progress2, progress3, progress4;
    TextView match, runs, wicket, average, strike, economy;
    CircularProgressIndicator circle_progress, circle_progress1;
    TextView email_tv, name_tv;
    ConstraintLayout password_cl, profile_cl, stats_cl, sign_out_cl;
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

        progress1 = findViewById(R.id.progress1);
        progress2 = findViewById(R.id.progress2);
        progress3 = findViewById(R.id.progress3);
        progress4 = findViewById(R.id.progress4);
        circle_progress = findViewById(R.id.circle_progress);
        circle_progress1 = findViewById(R.id.circle_progress1);
        match = findViewById(R.id.match);
        runs = findViewById(R.id.runs);
        wicket = findViewById(R.id.wicket);
        average = findViewById(R.id.average);
        strike = findViewById(R.id.strike);
        economy = findViewById(R.id.economy);

        profile_picture = findViewById(R.id.profile_picture);
        email_tv = findViewById(R.id.email_tv);
        name_tv = findViewById(R.id.name_tv);
        password_cl = findViewById(R.id.password_cl);
        profile_cl = findViewById(R.id.profile_cl);
        stats_cl = findViewById(R.id.stats_cl);
        pass = findViewById(R.id.password_edt);
        sign_out_cl = findViewById(R.id.signout_cl);

        storageReference = FirebaseStorage.getInstance().getReference("Profile");

        password_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordDialog passwordDialog = new PasswordDialog();
                passwordDialog.show(getSupportFragmentManager(), "passwordDialog");
            }
        });

        profile_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
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
                Toast.makeText(ProfileActivity.this, "Sign out successful", Toast.LENGTH_SHORT).show();
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

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        email_tv.setText(email);
        username = email.replace("@fsc.com", "");

        FirebaseDatabase.getInstance().getReference("Players").child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String stats_str = snapshot.child("stats").getValue().toString();

                if(stats_str.equals("yes")){
                    String match_str = snapshot.child("match").getValue().toString();
                    String runs_str = snapshot.child("runs").getValue().toString();
                    String wicket_str = snapshot.child("wicket").getValue().toString();
                    String average_str = snapshot.child("average").getValue().toString();
                    String strike_str = snapshot.child("strike").getValue().toString();
                    String economy_str = snapshot.child("economy").getValue().toString();

                    match.setText(match_str);
                    runs.setText(runs_str);
                    wicket.setText(wicket_str);
                    average.setText(average_str);
                    strike.setText(strike_str);
                    economy.setText(economy_str);

                    float average_fl = Float.parseFloat(average_str);
                    float strike_fl = Float.parseFloat(strike_str);
                    float economy_fl = Float.parseFloat(economy_str)*5;

                    progress1.setProgress(Integer.parseInt(match_str)*2);
                    progress2.setProgress(Integer.parseInt(runs_str)/10);
                    progress3.setProgress(Integer.parseInt(wicket_str)*2);
                    progress4.setProgress(Math.round(average_fl));
                    circle_progress.setProgress(Math.round(strike_fl)/2);
                    circle_progress1.setProgress(Math.round(economy_fl));

                } else{
                    CardView stats_cv = findViewById(R.id.stats_cv_profile);
                    stats_cv.setVisibility(View.GONE);
                }

                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                name_tv.setText(name);
                Glide.with(ProfileActivity.this).load(Uri.parse(image)).into(profile_picture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                        username = email.replace("@fsc.com", "");

                        reference = FirebaseDatabase.getInstance().getReference("Players").child(username);                        HashMap<String, Object> map = new HashMap<>();
                        map.put("image", ""+mUri);
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