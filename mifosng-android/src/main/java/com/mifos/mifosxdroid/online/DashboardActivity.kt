/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online

import android.os.Bundle
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncCenter
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncClient
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncGroup
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncLoanRepayment
import com.mifos.mifosxdroid.offlinejobs.OfflineSyncSavingsAccount
import com.mifos.mifosxdroid.online.search.SearchFragment

/**
 * Created by shashankpriyadarshi on 19/06/20.
 */
class DashboardActivity : MifosBaseActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        //runJobs();
        replaceFragment(SearchFragment(), false, R.id.container)
    }

    private fun runJobs() {
        OfflineSyncCenter.schedulePeriodic()
        OfflineSyncGroup.schedulePeriodic()
        OfflineSyncClient.schedulePeriodic()
        OfflineSyncSavingsAccount.schedulePeriodic()
        OfflineSyncLoanRepayment.schedulePeriodic()
    }
}