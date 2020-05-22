package com.mifos.mifosxdroid.online.checkerinbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity

class CheckerInboxPendingTasksActivity : MifosBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)

        showBackButton()
        setToolbarTitle(resources.getString(R.string.checker_inbox_and_pending_tasks))

        val fragment = CheckerInboxTasksFragment()
        replaceFragment(fragment, false, R.id.container)
    }
}
