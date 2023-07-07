package com.mifos.mifosxdroid.offline.syncgrouppayloads

import android.os.Bundle
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity

/**
 * Created by Rajan Maurya on 21/07/16.
 */
class SyncGroupPayloadsActivity : MifosBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        showBackButton()
        replaceFragment(SyncGroupPayloadsFragment.newInstance(), false, R.id.container)
    }
}