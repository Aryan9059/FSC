package com.fizanyatik.sportsclub.Fragment;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.fizanyatik.sportsclub.Activity.NorthernSuperchargers;
import com.fizanyatik.sportsclub.Activity.SouthernBrave;
import com.fizanyatik.sportsclub.ProfileActivity;
import com.fizanyatik.sportsclub.R;
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
                startActivity(new Intent(getContext(), NorthernSuperchargers.class));
            }
        });

        sb_cl = root.findViewById(R.id.sb_cl);
        sb_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(getContext(), SouthernBrave.class));
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
                        Toast.makeText(getContext(), "Download has started", Toast.LENGTH_SHORT).show();
                        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/fizanto-fuzz.appspot.com/o/FSC%20Rulebook%203rd%20Edition.pdf?alt=media&token=5381a9ac-ad15-46e2-a7b9-a21f87021254");
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setTitle("FSC Rulebook");
                        request.setDescription("Downloading FSC Rulebook 3rd Edition.pdf");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(DIRECTORY_DOCUMENTS, "FSC_Rulebook_3rd_Edition.pdf");
                        downloadManager.enqueue(request);
            }
        });

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        username = email.replace("@fsc.com", "");

        FirebaseDatabase.getInstance().getReference("Players").child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue().toString();
                try {
                    Glide.with(getContext()).load(Uri.parse(image)).into(profile_picture);
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