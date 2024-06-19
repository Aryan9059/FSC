package com.fizanyatik.sportsclub.Activity;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.fizanyatik.sportsclub.R;
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

public class ViewPDF extends AppCompatActivity {
    SharedPreferences prefs;
    ImageView download;

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
        setContentView(R.layout.activity_view_pdf);

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

        ImageView terms_back = findViewById(R.id.pdf_back);
        terms_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terms_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        download = findViewById(R.id.pdf_download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                Toast.makeText(ViewPDF.this, "Download has started", Toast.LENGTH_SHORT).show();
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/fizanto-fuzz.appspot.com/o/FSC%20Rulebook%203rd%20Edition.pdf?alt=media&token=5381a9ac-ad15-46e2-a7b9-a21f87021254");
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle("FSC Rulebook");
                request.setDescription("Downloading FSC Rulebook 3rd Edition.pdf");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(DIRECTORY_DOCUMENTS, "FSC_Rulebook_3rd_Edition.pdf");
                downloadManager.enqueue(request);
            }
        });

        HtmlTextView bat_tv = findViewById(R.id.rules_html_tv);
        bat_tv.setHtml("<p><span style=\"font-size: 16px;\"><strong>Clause 1.1: Front of Stumps (FOS)</strong></span></p>\n" +
                "<p>In the absence of Ball Tracking technology and the Decision Review System (DRS) in cricket, an alternative method known as Front of Stumps (FOS) has been introduced. The salient points of this alternative are as follows:</p>\n" +
                "<ul>\n" +
                "    <li>If a batter gets beaten or deliberately blocks the ball using their body in front of the specified stumps on three occasions within a single over, the batter shall be deemed out.</li>\n" +
                "    <li>The FOS rule is reset at the start of each new over, and the count of infringements restarts from zero.</li>\n" +
                "    <li>FOS ceases to be in effect upon the completion of an over, and its application does not carry forward to subsequent overs.</li>\n" +
                "    <li>This rule is designed to operate independently of complex technologies and instead relies on the players appealing for FOS.</li>\n" +
                "    <li>The decision to invoke FOS can only be overturned if a majority of players disagree with the appeal.</li>\n" +
                "    <li>It is recognized that certain adjustments may be necessary based on the players involved and the prevailing playing conditions.</li>\n" +
                "</ul>\n" +
                "<p>It should be noted that while the FOS alternative aims to address the absence of technological aids, its implementation warrants careful consideration to ensure fair and consistent decision-making in line with the spirit of the game.</p><p><span style=\"font-size: 16px;\"><strong>Clause 1.2: Prohibited Strokes</strong></span></p>\n" +
                "<p>In the interest of playing within a confined space encompassed by vehicles, delicate materials, and valuable objects, certain shots have been deemed prohibited. The following restrictions are to be observed:</p>\n" +
                "<ul>\n" +
                "    <li>Straight lofted shots, where the ball is struck directly upwards in a forceful manner, are strictly forbidden.</li>\n" +
                "    <li>It is strongly advised to refrain from hitting the ball forcefully towards any vehicles present in the vicinity.</li>\n" +
                "    <li>Given the presence of glass windows in close proximity, players are urged to exercise prudence and discretion in their shot selection to avoid damaging the windows.</li>\n" +
                "    <li>The playing area is characterized by two distinct gutters: one from which the ball can be retrieved, and another from which it cannot be recovered. Players should be mindful of these gutters and the associated consequences when playing their shots.</li>\n" +
                "    <li>It is imperative to recognize that due to the nature of the playing environment, all players must collectively agree upon the imposition of penalties, which may include deducting five runs or other suitable sanctions, as deemed appropriate.</li>\n" +
                "</ul>\n" +
                "<p>It is essential to adhere to these regulations in order to ensure the safety of individuals, minimize property damage, and maintain a harmonious playing environment.</p><p><span style=\"font-size: 16px;\"><strong>Clause 1.3: Miscellaneous Rules</strong></span></p>\n" +
                "<p>In addition to the aforementioned regulations applicable to this particular category of play, there exist supplementary rules that, although they may already be inherently understood, can occasionally generate conflicts among players. The salient points to be observed are as follows:</p>\n" +
                "<ul>\n" +
                "    <li>In the event that the ball makes contact with the edge of the bat and subsequently strikes the stumps, the batter shall be declared OUT.</li>\n" +
                "    <li>Run-outs can only be executed when the fielding team dislodges the stumps in closer proximity to the runner.</li>\n" +
                "    <li>To effect a Run-out on the Non-Striker, the fielding team must strike the area surrounding the steep line, whereas to effect a Run-out on the Striker, they must target the designated stumps.</li>\n" +
                "    <li>Should the ball venture into the gutter, it is the responsibility of the batter who played the shot to retrieve the ball from the gutter and meticulously cleanse it.</li>\n" +
                "</ul>\n" +
                "<p>It is crucial for all participants to uphold these additional regulations to foster a harmonious and fair playing environment. While some of these rules may appear implicit, they have been explicitly stated here to prevent any disputes and ensure consistent adherence.</p>", new HtmlHttpImageGetter(bat_tv));

        HtmlTextView bowl_tv = findViewById(R.id.bowl_rules);
        bowl_tv.setHtml("<p>This sub-category encompasses the specific rules pertaining to the bowling unit. It is essential to take note of the following regulations:</p>\n" +
                "<ul>\n" +
                "    <li>Our stumps encompass each and every component of the red plastic chair positioned behind the batter, and they should be treated as the designated target.</li>\n" +
                "    <li>It is strongly advised against commencing the bowling run-up from the boundary; an alternative starting point is recommended.</li>\n" +
                "    <li>The Bowling Tram-line refers to the steep line etched into the ground, and it serves as the boundary for the permissible trajectory of the delivery.</li>\n" +
                "    <li>The act of bowling with excessive speed, particularly with a hard red ball, is strictly prohibited to ensure the safety of all participants.</li>\n" +
                "    <li>Overthrows, wherein the ball is inadvertently thrown beyond the intended target, are not permitted within this category.</li>\n" +
                "    <li>In the event that the batter accepts their own dismissal or acknowledges their wicket being taken, they shall be ruled out accordingly.</li>\n" +
                "    <li>If the ball strikes a building, subsequently rebounds, and a player claims a catch, the batter shall be deemed NOT OUT. However, no runs will be awarded in such instances.</li>\n" +
                "    <li>The concepts of wides and no-balls are applicable within this category, carrying a penalty of +1 run.</li>\n" +
                "</ul>\n" +
                "<p>It is imperative to adhere to these rules specific to the bowling unit in order to maintain a fair and safe playing environment. The regulations mentioned above serve to ensure consistency and promote a smooth gameplay experience for all participants.</p>", new HtmlHttpImageGetter(bowl_tv));

        HtmlTextView field_tv = findViewById(R.id.field_rules);
        field_tv.setHtml("<p>This sub-category provides specific details regarding the runs allotted in various circumstances. The following information outlines the corresponding run values:</p>\n" +
                "<ol>\n" +
                "    <li>Back of Keeper: 1 run.</li>\n" +
                "    <li>Till Underground margin: 2 runs.</li>\n" +
                "    <li>Beyond Underground margin: 4 runs (on bounce), 6 runs (direct).</li>\n" +
                "    <li>Parking: 4 runs (on bounce), 6 runs (direct).</li>\n" +
                "    <li>MMS Building: 4 runs (on bounce), 6 runs (direct).</li>\n" +
                "    <li>Apartment:<ul>\n" +
                "      <li>1st Floor: 2 runs.</li>\n" +
                "    <li>2nd Floor: 4 runs.</li>\n" +
                "    <li>3rd Floor: 6 runs.</li>\n" +
                "      </ul></li>\n" +
                "    <li>Straight Boundary: 4 runs (on bounce), 6 runs (direct).</li>\n" +
                "    <li>Tom House: 2 runs (below pillar), 4 runs (above pillar).</li>\n" +
                "    <li>Goldie&apos;s Apartment: 4 runs (on bounce), 6 runs (direct).</li>\n" +
                "    <li>.  Prateek&rsquo;s House:<ul>\n" +
                "      <li>Front wall: 2 runs.</li>\n" +
                "    <li>Back wall: 4 runs (on bounce), 6 runs (direct).</li>\n" +
                "      </ul></li>\n" +
                "</ol>\n" +
                "<p>These run allocations are intended to reflect the specific locations or objects present within the playing area. Adhering to these guidelines will help determine the appropriate number of runs based on where the ball reaches or strikes during gameplay.</p>", new HtmlHttpImageGetter(bowl_tv));

        HtmlTextView social_tv = findViewById(R.id.social_rules);
        social_tv.setHtml("<p>To ensure smooth and timely progress of ongoing matches, it is advised to refrain from engaging in leisurely conversations that may cause unnecessary delays. Instead, players are encouraged to converse during breaks, while playing, or at the conclusion of the match.</p>\n" +
                "<p>During the course of gameplay, it is strictly prohibited to use abusive language or employ informal words. Players are expected to maintain a respectful and sportsmanlike demeanor throughout the match.</p>\n" +
                "<p>In terms of interaction among players, it is permissible to cheer and support teammates using their respective nicknames. Additionally, players have the right to appeal for catches behind the wicket and for Front of Stumps (FOS) decisions.</p>\n" +
                "<p>It is important to understand that there exist numerous rules and responsibilities that depend on individual behavior and common sense. Playing the game with full commitment and giving one&apos;s best effort, while also adhering to proper social and moral conduct, is of utmost importance.</p>\n" +
                "<p>By following these guidelines, players can contribute to a positive and enjoyable cricketing experience for everyone involved.</p>", new HtmlHttpImageGetter(bowl_tv));

    }
}