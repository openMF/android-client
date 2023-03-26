package com.mifos.mifosxdroid

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.DashboardActivity
import com.mifos.utils.Constants

/**
 * Created by mayankjindal on 22/07/17.
 */
class SettingsActivity : MifosBaseActivity() {

    private var hasSettingsChanged = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        setToolbarTitle(getString(R.string.settings))
        showBackButton()
        fragmentManager.beginTransaction()
            .replace(R.id.container, SettingsFragment())
            .commit()
        if (intent.hasExtra(Constants.HAS_SETTING_CHANGED)) {
            hasSettingsChanged = intent.getBooleanExtra(
                Constants.HAS_SETTING_CHANGED,
                false
            )
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (hasSettingsChanged) {
            val i = Intent(this, HomeActivity::class.java)
            startActivity(i)
            ActivityCompat.finishAffinity(this)
        }
    }
}