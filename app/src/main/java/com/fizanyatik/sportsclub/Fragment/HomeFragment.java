package com.fizanyatik.sportsclub.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.Activity.LoginActivity;
import com.fizanyatik.sportsclub.Activity.TeamActivity;
import com.fizanyatik.sportsclub.Activity.ProfileActivity;
import com.fizanyatik.sportsclub.Activity.ViewPDF;
import com.fizanyatik.sportsclub.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    ConstraintLayout ns_cl, sb_cl;
    CircleImageView profile_picture;
    CardView profile_cv;
    MaterialCardView fizanto_ground;
    ImageView log_out_btn;
    TextView land_logout;
    String email, username;
    Button download_rules_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        ns_cl = root.findViewById(R.id.ns_cl);
        ns_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ns_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                Intent intent = new Intent(getContext(), TeamActivity.class);
                intent.putExtra("team", "NSC");
                intent.putExtra("caption", "We always get the job done.");
                getActivity().startActivity(intent);
            }
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            land_logout = root.findViewById(R.id.land_logout);
            land_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sb_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(v.getContext());
                    alertDialog1.setTitle("Logout");
                    alertDialog1.setMessage("Do you want to logout from your account?");
                    alertDialog1.setPositiveButton("Logout", new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getContext(), LoginActivity.class));
                                    getActivity().finish();
                                }
                            }).setCancelable(true)
                            .setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    alertDialog1.show();
                }
            });

        } else {
            log_out_btn = root.findViewById(R.id.log_out_btn);
            log_out_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sb_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(v.getContext());
                    alertDialog1.setTitle("Logout");
                    alertDialog1.setMessage("Do you want to logout from your account?");
                    alertDialog1.setPositiveButton("Logout", new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getContext(), LoginActivity.class));
                                    getActivity().finish();
                                }
                            }).setCancelable(true)
                            .setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    alertDialog1.show();
                }
            });
        }

        sb_cl = root.findViewById(R.id.sb_cl);
        sb_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                Intent intent = new Intent(getContext(), TeamActivity.class);
                intent.putExtra("team", "SBR");
                intent.putExtra("caption", "Time for you to see adventures.");
                getActivity().startActivity(intent);
            }
        });

        MaterialButton share = root.findViewById(R.id.share_our_app_btn);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://github.com/Aryan9059/FSC/releases/download/1.5_Balisto/app-debug.apk");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Our App");
                startActivity(Intent.createChooser(shareIntent, "Share..."));
            }
        });

        MaterialButton source = root.findViewById(R.id.github_btn);
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Aryan9059/FSC/"));
                startActivity(intent);
            }
        });

        profile_picture = root.findViewById(R.id.profile_picture);

        profile_cv = root.findViewById(R.id.profile_cv);
        profile_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(getContext(), ProfileActivity.class));
            }
        });

        download_rules_btn = root.findViewById(R.id.download_rules_btn);
        download_rules_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), ViewPDF.class));
            }
        });

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        username = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fizanto_ground = root.findViewById(R.id.fizanto_ground);
        fizanto_ground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fizanto_ground.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.setContentView(R.layout.fizanto_ground);
                dialog.show();
            }
        });

        FirebaseDatabase.getInstance().getReference("Profile").child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue().toString();
                try {
                    if (!image.equals("default")){Glide.with(getContext()).load(Uri.parse(image)).into(profile_picture);}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }
}