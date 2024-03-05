/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.mifos.core.common.utils.Constants
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CentersActivity : MifosBaseActivity() {

    private val args: CentersActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        showBackButton()
        val centerId = args.centerId
        val bundle = Bundle()
        bundle.putInt(Constants.CENTER_ID, centerId)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_nav_host_fragment) as NavHostFragment
        navHostFragment.navController.apply {
            popBackStack()
            navigate(R.id.centerDetailsFragment, bundle)
        }
    }
}