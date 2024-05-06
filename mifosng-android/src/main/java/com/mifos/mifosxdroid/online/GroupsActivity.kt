package com.mifos.mifosxdroid.online

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.mifos.core.common.utils.Constants
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by nellyk on 2/27/2016.
 */
@AndroidEntryPoint
class GroupsActivity : MifosBaseActivity() {

    private val args: GroupsActivityArgs by navArgs()
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolbar_container)
        showBackButton()

        val groupId = args.groupId
        val groupName = args.groupName
        setToolbarTitle(getString(R.string.group) + " - " + groupName)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_nav_host_fragment) as NavHostFragment
        val bundle = Bundle()
        bundle.putInt(Constants.GROUP_ID, groupId)
        navHostFragment.navController.apply {
            popBackStack()
            navigate(R.id.groupDetailsFragment, bundle)
        }
    }
}