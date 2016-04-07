/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.online.ClientListFragment;
import com.mifos.mifosxdroid.online.CreateNewClientFragment;

public class ClientListActivity extends MifosBaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_container);
        showBackButton();
        replaceFragment(new ClientListFragment(), false, R.id.container);
    }

}
