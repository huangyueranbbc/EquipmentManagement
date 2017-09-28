package com.hyr.equipment.management.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hyr.equipment.management.activity.MainActivity;
import com.hyr.equipment.management.screens.HorizontalPagerFragment;

/**
 * Created by GIGAMOLE on 8/18/16.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private final static int COUNT = 3;

    private final static int HORIZONTAL = 0;
    private final static int TWO_WAY = 1;

    private MainActivity mainActivity = null;

    public MainPagerAdapter(final FragmentManager fm, MainActivity mainActivity) {
        super(fm);
        this.mainActivity=mainActivity;
    }

    @Override
    public Fragment getItem(final int position) {
        return new HorizontalPagerFragment(mainActivity);
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
