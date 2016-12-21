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
import com.mifos.mifosxdroid.online.centerlist.CenterListFragment;
import com.mifos.mifosxdroid.online.clientlist.ClientListFragment;
import com.mifos.mifosxdroid.online.collectionsheet.CollectionSheetFragment;
import com.mifos.mifosxdroid.online.grouplist.GroupListFragment;
import com.mifos.mifosxdroid.online.savingsaccount.SavingsAccountFragment;
import com.mifos.objects.client.Client;
import com.mifos.utils.Constants;

import java.util.List;

public class CentersActivity extends MifosBaseActivity implements CenterListFragment
        .OnFragmentInteractionListener, GroupListFragment.OnFragmentInteractionListener {

    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        showBackButton();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int centerId = bundle.getInt(Constants.CENTER_ID);
            replaceFragment(GroupListFragment.newInstance(centerId), true, R.id.container);
        } else {
            replaceFragment(new CenterListFragment(), false, R.id.container);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.centers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadGroupsOfCenter(int centerId) {
        replaceFragment(GroupListFragment.newInstance(centerId), true, R.id.container);
    }

    @Override
    public void loadCollectionSheetForCenter(int centerId, String collectionDate, int
            calenderInstanceId) {
        replaceFragment(CollectionSheetFragment.newInstance(centerId, collectionDate,
                calenderInstanceId), true, R.id.container);
    }

    @Override
    public void loadClientsOfGroup(List<Client> clientList) {
        replaceFragment(ClientListFragment.newInstance(clientList, true), true, R.id.container);
    }

    @Override
    public void addCenterSavingAccount(int centerId) {
        replaceFragment(SavingsAccountFragment.newInstance(centerId, true), true, R.id.container);
    }
}
