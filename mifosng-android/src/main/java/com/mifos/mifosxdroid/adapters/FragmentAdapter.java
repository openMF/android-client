package com.mifos.mifosxdroid.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by ishankhanna on 11/03/14.
 */
public class FragmentAdapter extends FragmentPagerAdapter {


    List<Fragment> fragmentList;

    public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {

        return this.fragmentList.get(position);
    }


    @Override
    public int getCount() {
        return this.fragmentList.size();
    }
}
