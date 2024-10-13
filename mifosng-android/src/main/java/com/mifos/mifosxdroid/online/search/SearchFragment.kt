/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import com.mifos.core.objects.SearchedEntity
import com.mifos.core.objects.navigation.ClientArgs
import com.mifos.core.ui.components.FabType
import com.mifos.feature.search.SearchScreenRoute
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.activity.home.HomeActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as HomeActivity).supportActionBar?.title = getString(R.string.dashboard)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SearchScreenRoute(
                    onFabClick = { fabType ->
                        onFabClick(fabType)
                    },
                    onSearchOptionClick = {
                        onSearchOptionClick(it)
                    }
                )
            }
        }
    }

    private fun onFabClick(fabType: FabType) {
        when (fabType) {
            FabType.CLIENT -> {
                findNavController().navigate(R.id.action_navigation_dashboard_to_createNewClientFragment)
            }
            FabType.CENTER -> {
                findNavController().navigate(R.id.action_navigation_dashboard_to_createNewCenterFragment)
            }
            FabType.GROUP -> {
            }
        }
    }

    private fun onSearchOptionClick(searchedEntity: SearchedEntity) {
        when (searchedEntity.entityType) {
            Constants.SEARCH_ENTITY_LOAN -> {
                val action = SearchFragmentDirections.actionNavigationDashboardToClientActivity(
                    ClientArgs(clientId = searchedEntity.entityId)
                )
                findNavController().navigate(action)
            }

            Constants.SEARCH_ENTITY_CLIENT -> {
                val action = SearchFragmentDirections.actionNavigationDashboardToClientActivity(
                    ClientArgs(clientId = searchedEntity.entityId)
                )
                findNavController().navigate(action)
            }

            Constants.SEARCH_ENTITY_GROUP -> {
                
            }

            Constants.SEARCH_ENTITY_SAVING -> {
                val action = SearchFragmentDirections.actionNavigationDashboardToClientActivity(
                    ClientArgs(savingsAccountNumber = searchedEntity.entityId)
                )
                findNavController().navigate(action)
            }

            Constants.SEARCH_ENTITY_CENTER -> {
                val action =
                    SearchFragmentDirections.actionNavigationDashboardToCentersActivity(
                        searchedEntity.entityId
                    )
                findNavController().navigate(action)
            }
        }
    }
}