/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mifos.mifosxdroid.OfflineCenterInputActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.utils.FragmentConstants;

/**
 * Created by ishankhanna on 09/02/14.
 */


public class DashboardFragmentActivity extends ActionBarActivity implements CreateNewClientFragment.OnFragmentInteractionListener{

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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.client_search, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());

        switch (item.getItemId()) {
            case R.id.item_centers:
                startActivity(new Intent(this, CentersActivity.class));
                break;
            case R.id.mItem_list :
                loadClientList();
                break;
            //case R.id.item_collection_sheet :
                //startActivity(new Intent(DashboardFragmentActivity.this, GenerateCollectionSheet.class));
            //    break;
            case R.id.item_offline_centers:
                startActivity(new Intent(this, OfflineCenterInputActivity.class));
                break;
            case R.id.logout:
                startActivity(new Intent(DashboardFragmentActivity.this, LogoutActivity.class));
                break;
            case R.id.item_create_new_client:
                openCreateClient();

            default: //DO NOTHING
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadClientList() {

        ClientListFragment clientListFragment = new ClientListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_SEARCH);
        fragmentTransaction.replace(R.id.dashboard_global_container, clientListFragment);
        fragmentTransaction.commit();

    }

    public void openCreateClient(){
        CreateNewClientFragment createNewClientFragment = new CreateNewClientFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CREATE_NEW_CLIENT);
        //fragmentTransaction.add(createNewClientFragment, FragmentConstants.FRAG_CREATE_NEW_CLIENT);
        fragmentTransaction.replace(R.id.dashboard_global_container, createNewClientFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}


