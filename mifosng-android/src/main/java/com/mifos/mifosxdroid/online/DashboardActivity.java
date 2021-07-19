/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncCenter;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncClient;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncGroup;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncLoanRepayment;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncSavingsAccount;
import com.mifos.mifosxdroid.online.search.SearchFragment;
import com.mifos.mifosxdroid.receivers.NetworkChangeReceiver;
import com.mifos.utils.Constants;
import com.mifos.utils.PrefManager;


import butterknife.ButterKnife;

/**
 * Created by shashankpriyadarshi on 19/06/20.
 */
public class DashboardActivity extends MifosBaseActivity {

    NetworkChangeReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);
        //runJobs();
        replaceFragment(new SearchFragment(), false, R.id.container);

        receiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int state = intent.getExtras().getInt(getString(R.string.network_state));
        if (state != PrefManager.getUserStatus()) {
            String info = "Auto switch to " +
                    (state == Constants.USER_ONLINE ? "Online" : "offline") +
                    " mode.";
            Toast.makeText(this, info, Toast.LENGTH_LONG).show();
        }
    }

    private void runJobs() {
        OfflineSyncCenter.schedulePeriodic();
        OfflineSyncGroup.schedulePeriodic();
        OfflineSyncClient.schedulePeriodic();
        OfflineSyncSavingsAccount.schedulePeriodic();
        OfflineSyncLoanRepayment.schedulePeriodic();
    }
}
