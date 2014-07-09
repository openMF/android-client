package com.mifos.mifosxdroid.online;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import com.mifos.mifosxdroid.R;
import com.mifos.utils.FragmentConstants;

/**
 * Created by ishankhanna on 09/02/14.
 */


public class DashboardFragmentActivity extends ActionBarActivity {

    public final static String TAG = DashboardFragmentActivity.class.getSimpleName();
    public static Context context;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ClientSearchFragment clientSearchFragment = new ClientSearchFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_global_container, clientSearchFragment, FragmentConstants.FRAG_CLIENT_SEARCH);
        fragmentTransaction.commit();

    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());

        switch (item.getItemId()) {
            case R.id.item_centers:
                startActivity(new Intent(this, CentersActivity.class));
                break;
            case R.id.logout:
                startActivity(new Intent(DashboardFragmentActivity.this, LogoutActivity.class));
                break;
            default: //DO NOTHING
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
