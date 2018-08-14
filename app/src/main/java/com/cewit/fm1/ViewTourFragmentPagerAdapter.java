package com.cewit.fm1;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Taeyu Im on 18. 8. 11.
 * qvo@cs.stonybrook.edu
 */

public class ViewTourFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private int numOfTabs;

    public ViewTourFragmentPagerAdapter(Context context, FragmentManager fm, int numOfTabs) {
        super(fm);
        mContext = context;
        this.numOfTabs = numOfTabs;
    }

    //This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TourDiagramFragment();
        } else {
            return new TourSummaryFragment();
        }
    }

    //This determines the number of tabs
    @Override
    public int getCount() {
        return numOfTabs;
    }

    //This determines the title for each tab
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.tour_diagram_tab);
        } else {
            return mContext.getString(R.string.tour_summary_tab);
        }
    }
}
