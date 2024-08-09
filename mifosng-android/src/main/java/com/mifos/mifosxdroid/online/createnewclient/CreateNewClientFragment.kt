/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.createnewclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.findNavController
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.noncore.DataTable
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListFragment
import com.mifos.utils.FragmentConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateNewClientFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {

                CreateNewClientScreen(
                    navigateBack = { findNavController().popBackStack() },
                    hasDatatables = { dataTables, payload ->
                        hasDatatables(dataTables, payload)
                    }
                )
            }
        }
    }

    private fun hasDatatables(dataTables : List<DataTable>, clientPayload: ClientPayload ){
        val fragment = DataTableListFragment.newInstance(
            dataTables,
            clientPayload, Constants.CREATE_CLIENT
        )
        val fragmentTransaction = requireActivity().supportFragmentManager
            .beginTransaction()
        requireActivity().supportFragmentManager.popBackStackImmediate()
        fragmentTransaction.addToBackStack(FragmentConstants.DATA_TABLE_LIST)
        fragmentTransaction.replace(R.id.container, fragment).commit()
    }

}