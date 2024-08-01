/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.groupslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.findNavController
import com.mifos.core.common.utils.Constants
import com.mifos.feature.groups.group_list.GroupsListRoute
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.activity.home.HomeActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog.SyncGroupsDialogFragment
import com.mifos.utils.FragmentConstants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by nellyk on 2/27/2016.
 *
 *
 * This class loading and showing groups, Here is two way to load the Groups. First one to load
 * Groups from Rest API
 *
 *
 * >demo.openmf.org/fineract-provider/api/v1/groups?paged=true&offset=offset_value&limit
 * =limit_value>
 *
 *
 * Offset : From Where index, Groups will be fetch.
 * limit : Total number of client, need to fetch
 *
 *
 * and showing in the GroupList.
 *
 *
 * and Second one is showing Groups provided by Parent(Fragment or Activity).
 * Parent(Fragment or Activity) load the GroupList and send the
 * Groups to GroupsListFragment newInstance(List<Group> groupList,
 * boolean isParentFragment) {...}
 * and unregister the ScrollListener and SwipeLayout.
</Group> */
@AndroidEntryPoint
class GroupsListFragment : MifosBaseFragment() {

    private var isParentFragment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            isParentFragment = requireArguments()
                .getBoolean(Constants.IS_A_PARENT_FRAGMENT)
        }

        if (!isParentFragment) (activity as HomeActivity).supportActionBar?.title =
            getString(R.string.feature_groups_groups)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                GroupsListRoute(
                    onAddGroupClick = {
                        findNavController().navigate(R.id.action_navigation_group_list_to_createNewGroupFragment)
                    },
                    onGroupClick = { group ->
                        group.id?.let { id ->
                            group.name?.let { name ->
                                val action =
                                    GroupsListFragmentDirections.actionNavigationGroupListToGroupsActivity(id, name)
                                findNavController().navigate(action)
                            }
                        }
                    },
                    onSyncClick = { selectedGroups ->
                        val syncGroupsDialogFragment =
                            SyncGroupsDialogFragment.newInstance(ArrayList(selectedGroups))
                        val fragmentTransaction = activity
                            ?.supportFragmentManager?.beginTransaction()
                        fragmentTransaction?.addToBackStack(FragmentConstants.FRAG_GROUP_SYNC)
                        syncGroupsDialogFragment.isCancelable = false
                        fragmentTransaction?.let {
                            syncGroupsDialogFragment.show(
                                fragmentTransaction,
                                resources.getString(R.string.sync_groups)
                            )
                        }
                    }
                )
            }
        }
    }
}