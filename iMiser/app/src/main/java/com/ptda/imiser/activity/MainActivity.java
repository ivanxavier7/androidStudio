package com.ptda.imiser.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.ptda.imiser.R;
import com.ptda.imiser.adapter.SliderAdapter;
import com.ptda.imiser.config.FireBaseConfig;

public class MainActivity extends AppCompatActivity {
    private ViewPager sliderViewPager;
    private LinearLayout dotLayout;
    private TextView[] dots;
    private SliderAdapter sliderAdapter;
    private Button preButton;
    private Button nextButton;
    private int currentPage;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sliderViewPager = (ViewPager) findViewById(R.id.sliderViewPager);
        dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
        preButton = (Button) findViewById(R.id.preBtn);
        nextButton = (Button) findViewById(R.id.nextBtn);

        sliderAdapter = new SliderAdapter(this);
        sliderViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        sliderViewPager.addOnPageChangeListener(viewListener);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nextButton.getText() == "Finalizar") {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity( intent );
                }
                sliderViewPager.setCurrentItem(currentPage + 1);
            }
        });

        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderViewPager.setCurrentItem(currentPage - 1);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyLogin();
    }

    public void addDotsIndicator(int position) {
        dots = new TextView[3];
        dotLayout.removeAllViews();
        for(int i=0; i<dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            dotLayout.setGravity(Gravity.CENTER);
            dotLayout.addView(dots[i]);
        }

        if(dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorSnackBar));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPage = position;
            if(position == 0) {
                nextButton.setEnabled(true);
                preButton.setEnabled(false);
                preButton.setVisibility(View.INVISIBLE);
                nextButton.setText("Seguinte");
                preButton.setText("");
            } else if (position == dots.length - 1) {
                nextButton.setEnabled(true);
                preButton.setEnabled(true);
                preButton.setVisibility(View.VISIBLE);
                nextButton.setText("Finalizar");
                preButton.setText("Anterior");
            } else {
                nextButton.setEnabled(true);
                preButton.setEnabled(true);
                preButton.setVisibility(View.VISIBLE);
                nextButton.setText("Seguinte");
                preButton.setText("Anterior");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void verifyLogin() {
        auth = FireBaseConfig.getFireBaseAuth();
        //auth.signOut(); // Logout
        if(auth.getCurrentUser() != null) {
            startApp();
        }
    }

    public void startApp() {
        startActivity(new Intent(this, MainAppActivity.class));
    }
}