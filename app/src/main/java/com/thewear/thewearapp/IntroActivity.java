package com.thewear.thewearapp;

import android.os.Bundle;
import android.view.View;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class IntroActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private IntroFragement fragment1 = new IntroFragement();
    private IntroFragementTwo fragment2 = new IntroFragementTwo();
    private DotsIndicator dotsIndicator;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);

        dotsIndicator=(DotsIndicator)findViewById(R.id.dots_indicator);
        viewPager=(ViewPager)findViewById(R.id.vp_intro);
        viewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener) this);

        viewPager.setCurrentItem(0);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0: return fragment1;
                    case 1: return  fragment2;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        FadeOutTransformation fadeOutTransformation = new FadeOutTransformation();
        viewPager.setPageTransformer(true, fadeOutTransformation);
        dotsIndicator.setViewPager(viewPager);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //hide status bar and below softkey
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
