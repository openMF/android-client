package com.mifos.mifosxdroid

import android.content.Intent
import android.os.Bundle
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.DashboardActivity

/**
 * Created by mayankjindal on 22/07/17.
 */
class SettingsActivity : MifosBaseActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        showBackButton()
        fragmentManager.beginTransaction()
                .replace(R.id.container, SettingsFragment())
                .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this, DashboardActivity::class.java)
        startActivity(i)
    }
}