package com.mifos.mifosxdroid.offline.synccenterpayloads

import android.os.Bundle
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.offline.synccenterpayloads.SyncCenterPayloadsFragment.Companion.newInstance

class SyncCenterPayloadActivity : MifosBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        showBackButton()
        replaceFragment(newInstance(), false, R.id.container)
    }
}