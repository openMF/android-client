package com.mifos.mifosxdroid.online.createnewgroup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.response.SaveResponse
import com.mifos.feature.groups.create_new_group.CreateNewGroupScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.online.GroupsActivity
import com.mifos.utils.PrefManager
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.InvocationTargetException

@AndroidEntryPoint
class CreateNewGroupFragment : MifosBaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CreateNewGroupScreen(
                    onGroupCreated = { group ->
                        onGroupCreated(group)
                    }
                )
            }
        }
    }

    private fun onGroupCreated(group: SaveResponse?) {
        requireActivity().supportFragmentManager.popBackStack()
        if (PrefManager.userStatus == Constants.USER_ONLINE) {
            val groupActivityIntent = Intent(activity, GroupsActivity::class.java)
            groupActivityIntent.putExtra(
                Constants.GROUP_ID,
                group?.groupId
            )
            /**
             * On group creation [InvocationTargetException] exception is thrown And app crashes
             * Original XML design fragment had this bug. Not sure if it's a bug or intentional.
             * I am leaving it as it is.
             */
            startActivity(groupActivityIntent)
        }
    }
}