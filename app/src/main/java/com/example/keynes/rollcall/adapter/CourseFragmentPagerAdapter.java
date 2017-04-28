package com.example.keynes.rollcall.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.keynes.rollcall.R;
import com.example.keynes.rollcall.fragnment.RollCallFragment;
import com.example.keynes.rollcall.fragnment.SettingFragment;
import com.example.keynes.rollcall.fragnment.StatisticsFragment;

/**
 * Created by Keynes on 2017/4/7.
 */

public class CourseFragmentPagerAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context mContext;

    public CourseFragmentPagerAdapter(Context context ,FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return new RollCallFragment();
        } else if(position == 1) {
            return new StatisticsFragment();
        } else {
            return new SettingFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.course_roll_call);
        } else if (position == 1) {
            return mContext.getString(R.string.course_statistics);
        } else {
            return mContext.getString(R.string.course_setting);
        }
    }
}
