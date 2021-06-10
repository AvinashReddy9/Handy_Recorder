package com.example.viewrecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.viewrecorder.Adapter.ViewPageAdapter;
import com.example.viewrecorder.Fragments.PlayListFragment;
import com.example.viewrecorder.Fragments.RecorderFragment;
import com.example.viewrecorder.Fragments.SpeakerFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private PlayListFragment mPlayListFragment;
    private RecorderFragment mRecorderFragment;
    private SpeakerFragment mSpeakerFragment;
    private TabLayout mTabLayout;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_main);
        mRecorderFragment = new RecorderFragment();
        mPlayListFragment =new PlayListFragment();
        mHandler = new Handler();
     //   mSpeakerFragment = new SpeakerFragment();


        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mTabLayout = (TabLayout)findViewById(R.id.tabs);
        addTabs(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
        setupTabIcons();
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    Log.e("INFO", "First Tab");
                 //   mPlayListFragment.cleanUp();
                } else if (tab.getPosition() == 1) {
                    Log.e("INFO", "Second Tab");
                  //  mPlayListFragment.init();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset,
//                                       int positionOffsetPixels) {
//                //Log.e("INFO","PAGE SCROLLED");
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                //Log.e("INFO","PAGE SELECTED");
//                //Log.e("INFO", "POSITION " + position);
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                //Log.e("INFO","PAGE onPageScrollStateChanged");
//            }
//        });

    }


    private void addTabs(ViewPager viewPager) {
        final ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());
        adapter.addFrag(mRecorderFragment,"RECORDER");
        adapter.addFrag(mPlayListFragment,"PLAYLIST");
        mViewPager.setAdapter(adapter);

    }

    private void setupTabIcons() {
        mTabLayout.getTabAt(0).setIcon(R.drawable.recorder_microphone);
//        mTabLayout.getTabAt(1).setIcon(R.drawable.speaker);
        mTabLayout.getTabAt(1).setIcon(R.drawable.recorder_playlist);
        // tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

}