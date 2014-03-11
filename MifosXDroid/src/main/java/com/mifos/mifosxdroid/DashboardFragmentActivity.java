package com.mifos.mifosxdroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.mifos.mifosxdroid.adapters.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 09/02/14.
 */


public class DashboardFragmentActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static Context context;
    private ViewPager viewPager;
    private FragmentAdapter fragmentAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),getListOfAllFragments());
        viewPager = (ViewPager) findViewById(R.id.vp_dashboard);
        viewPager.setAdapter(fragmentAdapter);

        context = DashboardFragmentActivity.this.getBaseContext();
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        initTabListener();


        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                getSupportActionBar().setSelectedNavigationItem(position);

            }
        });

        viewPager.setOffscreenPageLimit(0);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container_dashboard, new ClientListFragment())
//                    .commit();
//        }


    }

    public void replaceFragments(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.vp_dashboard, fragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        viewPager.setCurrentItem(tab.getPosition(),true);

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public void initTabListener(){
        getSupportActionBar().addTab(getTab("Clients"), 0, true);
        getSupportActionBar().addTab(getTab("Centers"), 1, false);
    }

    public List<Fragment> getListOfAllFragments(){

        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new ClientListFragment());
        fragmentList.add(new CenterListFragment());
        return fragmentList;
    }

    public ActionBar.Tab getTab(String tabTitle){

        ActionBar.Tab tab = getSupportActionBar().newTab();
        tab.setText(tabTitle);
        tab.setTabListener(this);

        return tab;
    }
}
