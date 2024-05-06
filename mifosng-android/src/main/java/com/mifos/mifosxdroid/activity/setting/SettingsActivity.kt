package com.mifos.mifosxdroid.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.activity.home.HomeActivity
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.utils.Constants

/**
 * Created by mayankjindal on 22/07/17.
 */
class SettingsActivity : MifosBaseActivity() {

    private var hasLanguageSettingsChanged = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        setToolbarTitle(getString(R.string.settings))
        showBackButton()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_nav_host_fragment) as NavHostFragment
        navHostFragment.navController.apply {
            popBackStack()
            navigate(R.id.settingsFragment)
        }

        if (intent.hasExtra(Constants.HAS_SETTING_CHANGED)) {
            hasLanguageSettingsChanged = intent.getBooleanExtra(
                Constants.HAS_SETTING_CHANGED,
                false
            )
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (hasLanguageSettingsChanged) {
            val i = Intent(this, HomeActivity::class.java)
            startActivity(i)
            ActivityCompat.finishAffinity(this)
        }
    }
}