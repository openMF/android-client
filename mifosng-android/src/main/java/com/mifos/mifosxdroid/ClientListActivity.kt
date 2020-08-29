/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid

import android.os.Bundle
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.clientlist.ClientListFragment

class ClientListActivity : MifosBaseActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        showBackButton()
        replaceFragment(ClientListFragment(), false, R.id.container)
    }
}