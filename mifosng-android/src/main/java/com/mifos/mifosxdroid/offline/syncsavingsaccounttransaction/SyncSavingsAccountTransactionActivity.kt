package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction

import android.os.Bundle
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction.SyncSavingsAccountTransactionFragment.Companion.newInstance

/**
 * Created by Rajan Maurya on 20/08/16.
 */
class SyncSavingsAccountTransactionActivity : MifosBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        showBackButton()
        replaceFragment(newInstance(), false, R.id.container)
    }
}