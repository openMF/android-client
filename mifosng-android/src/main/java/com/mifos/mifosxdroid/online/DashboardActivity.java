/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.os.Bundle;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.databinding.ActivityDashboardBinding;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncCenter;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncClient;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncGroup;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncLoanRepayment;
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncSavingsAccount;
import com.mifos.mifosxdroid.online.search.SearchFragment;


/**
 * Created by shashankpriyadarshi on 19/06/20.
 */
public class DashboardActivity extends MifosBaseActivity {
    private ActivityDashboardBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //runJobs();
        replaceFragment(new SearchFragment(), false, R.id.container);
    }

    private void runJobs() {
        OfflineSyncCenter.schedulePeriodic();
        OfflineSyncGroup.schedulePeriodic();
        OfflineSyncClient.schedulePeriodic();
        OfflineSyncSavingsAccount.schedulePeriodic();
        OfflineSyncLoanRepayment.schedulePeriodic();
    }
}
