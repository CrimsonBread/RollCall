package com.example.keynes.rollcall;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.keynes.rollcall.adapter.CourseFragmentPagerAdapter;

public class CourseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager_course);
        // Create an adapter that knows which fragment should be shown on each page
        CourseFragmentPagerAdapter adapter = new CourseFragmentPagerAdapter(this ,getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_course);
        tabLayout.setupWithViewPager(viewPager);
    }
}
