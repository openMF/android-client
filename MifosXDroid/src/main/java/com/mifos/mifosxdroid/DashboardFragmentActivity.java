package com.mifos.mifosxdroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by ishankhanna on 09/02/14.
 */


public class DashboardFragmentActivity extends ActionBarActivity {

    public static Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        context = DashboardFragmentActivity.this.getBaseContext();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_dashboard, new ClientListFragment())
                    .commit();
        }


    }

    public void replaceFragments(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.container_dashboard, fragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

}
