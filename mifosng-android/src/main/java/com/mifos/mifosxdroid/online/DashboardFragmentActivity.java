/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class DashboardFragmentActivity extends MifosBaseActivity {

    public final static String TAG = DashboardFragmentActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        replaceFragment(new ClientSearchFragment(), false, R.id.container);

        // setup navigation drawer
        setupNavigationBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mItem_create_new_client:
                openCreateClient();
                break;
            case R.id.mItem_create_new_center:
                openCreateCenter();
                break;
            case R.id.mItem_create_new_group:
                openCreateGroup();
                break;
            case R.id.logout:
                logout();
                break;
            default: //DO NOTHING
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openCreateClient() {
        replaceFragment(CreateNewClientFragment.newInstance(), true, R.id.container);
    }

    public void openCreateCenter() {
        replaceFragment(CreateNewCenterFragment.newInstance(), true, R.id.container);

    }

    public void openCreateGroup() {
        replaceFragment(CreateNewGroupFragment.newInstance(), true, R.id.container);
    }


}


