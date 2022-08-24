package com.mifos.mifosxdroid

import android.content.Intent
import android.os.Bundle
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.DashboardActivity

/**
 * Created by mayankjindal on 22/07/17.
 */
class SettingsActivity : MifosBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        setToolbarTitle(getString(R.string.settings))
        showBackButton()
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SettingsFragment())
                .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this, HomeActivity::class.java)
        startActivity(i)
    }
}