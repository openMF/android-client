package com.mifos.mifosxdroid.online.checkerinbox

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckerInboxPendingTasksActivity : MifosBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)

        showBackButton()
        setToolbarTitle(resources.getString(R.string.checker_inbox_and_pending_tasks))

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_nav_host_fragment) as NavHostFragment
        navHostFragment.navController.apply {
            popBackStack()
            navigate(R.id.checkerInboxTasksFragment)
        }
    }
}
