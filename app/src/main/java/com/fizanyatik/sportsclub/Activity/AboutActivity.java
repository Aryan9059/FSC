package com.fizanyatik.sportsclub.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fizanyatik.sportsclub.R;

public class AboutActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_about);

        ImageView terms_back = findViewById(R.id.about_back);

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
        terms_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terms_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        TextView privacy_tv = findViewById(R.id.about_tv);
        privacy_tv.setText(Html.fromHtml("<p><span style=\"text-align: inherit;\">Welcome to Fizanyatik Sports Club, a vibrant and dynamic sports community located in the city of Bhagalpur, in the state of Bihar, India. Our journey has been one of transformation, growth, and a deep passion for cricket, the game of gentlemen. With the launch of our own website, we have taken a significant step forward, marking the beginning of an exciting era for our dedicated supporters and enthusiasts.</span></p>\n" +
                "<p><span style=\"text-align: inherit;\"><strong>Our History</strong></span></p>\n" +
                "<p><span style=\"text-align: inherit;\">Initially known as Golkothi Cricket Club, we recognized the importance of embracing all sports and subsequently renamed ourselves as Golkothi Sports Club. Under the leadership of Aryan Srivastava, HyperNought took over our club, and we became HyperSports. Unfortunately, in May 2020, the company faced setbacks and closed its doors, citing a lack of support, development, and resources. Nevertheless, we emerged from the experience with a renewed sense of purpose and rebranded ourselves as Fizanyatik Sports Club. We realized the immense potential within ourselves and decided to establish our independent identity. It was a pivotal moment when we broke away from the past and embraced the freedom to thrive on our own feet.</span></p>\n" +
                "<p><span style=\"text-align: inherit;\"><strong>Our Goals</strong></span></p>\n" +
                "<p><span style=\"text-align: inherit;\">Our primary objective is to foster a strong and united sports community, devoted to the love and admiration of cricket. Located in Bhagalpur, a city known for its rich cultural heritage and sporting enthusiasm, we are determined to make our mark on the cricketing landscape. Our journey is fueled by an unwavering desire to play the sport professionally, continuously challenging ourselves, and pushing the boundaries of what we can achieve.</span></p>\n" +
                "<p><span style=\"text-align: inherit;\">At Fizanyatik Sports Club, we have set lofty goals for ourselves. We seek to revolutionize the way cricket is played, advocating for a free and fair approach, adhering to a comprehensive set of rules that govern our conduct on and off the field. We believe that true success lies not only in victory but also in promoting the spirit of good sportsmanship, camaraderie, and integrity.</span></p>\n" +
                "<p><span style=\"text-align: inherit;\">With each passing day, we are making significant strides towards reaching milestones and etching our names in the annals of cricket history. Our dedication to the sport, coupled with the unwavering support of our passionate fans, fuels our progress and drives us forward.</span></p>\n" +
                "<p><strong>Final Words</strong></p>\n" +
                "<ul>\n" +
                "    <p><span style=\"text-align: inherit;\">We invite you to join us on this thrilling journey as we continue to grow, evolve, and make our mark in the world of cricket. Together, we can create a lasting legacy, inspire future generations, and write a new chapter in the glorious story of Fizanyatik Sports Club.</span></p>\n" +
                "</ul>"));
    }
}