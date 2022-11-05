package com.fizanyatik.sportsclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextView learn_more, forgot_password_btn;
    MaterialButton sign_in_btn;
    TextInputEditText email_edt, password_edt;
    private FirebaseAuth auth;
    SharedPreferences prefs;

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }

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
        setContentView(R.layout.activity_login);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.screen_background, typedValue,true);
        getWindow().setStatusBarColor(typedValue.data);

        email_edt = findViewById(R.id.emaillog);
        password_edt = findViewById(R.id.passwordlog);
        sign_in_btn = findViewById(R.id.sign_in_btn);
        forgot_password_btn = findViewById(R.id.forgot_password_btn);

        auth = FirebaseAuth.getInstance();

        learn_more = findViewById(R.id.learn_more_btn);
        learn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(LoginActivity.this);
                alertDialog.setTitle("Information");
                alertDialog.setMessage("The FSC App is meant to be used only by the players associated with them. The Sign in details were already provided to them through text. If you still want to explore the app, contact at fizanyatiksc@gmail.com");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false)
                        .create();
                alertDialog.show();
            }
        });

        forgot_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = email_edt.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(LoginActivity.this);
                    alertDialog.setTitle("Reset Password");
                    alertDialog.setMessage("You can't change your password until you are logged in already. If you have forgotten your password, tap on OK to contact us through email.");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:fizanyatiksc@gmail.com")));
                            dialog.dismiss();
                        }
                    }).setNegativeButton("Cancel", null).create().show();
                }
            }
        });

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_in_btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);

                final String email = email_edt.getText().toString();
                final String password = password_edt.getText().toString();

                if(email.isEmpty() && password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter correct credentials", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                } else{
                    SignIn();
                }
            }
        });
    }

    private void SignIn() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setView(R.layout.progress_material)
                .setCancelable(false).create();

        AlertDialog materialDialogs = dialogBuilder.create();
        materialDialogs.show();
        auth.signInWithEmailAndPassword(email_edt.getText().toString(), password_edt.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        materialDialogs.dismiss();

                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent (LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();

                        } else{
                            Log.e("ERROR",task.getException().toString());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage() + "", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}