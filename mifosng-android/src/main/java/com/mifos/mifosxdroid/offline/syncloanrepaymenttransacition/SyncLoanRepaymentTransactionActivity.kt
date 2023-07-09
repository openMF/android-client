package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition

import android.os.Bundle
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition.SyncLoanRepaymentTransactionFragment.Companion.newInstance

/**
 * Created by Rajan Maurya on 30/07/16.
 */
class SyncLoanRepaymentTransactionActivity : MifosBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        showBackButton()
        replaceFragment(newInstance(), false, R.id.container)
    }
}