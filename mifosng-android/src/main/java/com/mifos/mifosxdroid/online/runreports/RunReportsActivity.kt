package com.mifos.mifosxdroid.online.runreports

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Tarun on 02-08-17.
 */
@AndroidEntryPoint
class RunReportsActivity : MifosBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_nav_host_fragment) as NavHostFragment
        navHostFragment.navController.apply {
            popBackStack()
            navigate(R.id.reportCategoryFragment)
        }
    }
}