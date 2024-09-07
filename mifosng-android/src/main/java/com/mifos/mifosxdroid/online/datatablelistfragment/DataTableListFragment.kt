/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.datatablelistfragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.mifos.core.common.utils.Constants
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.noncore.DataTable
import com.mifos.feature.data_table.dataTableList.DataTableListScreen
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.online.ClientActivity
import com.mifos.utils.MifosResponseHandler
import com.mifos.utils.PrefManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * A generic fragment to show the DataTables at the runtime.
 *
 * It receives the list of DataTables, the corresponding LoanPayload (client/center/group)
 * and an identifier int that states the type of entity generating the request for datatables.
 *
 * It differs from the other DatatableDialogFragments in the sense that -
 * 1. it does NOT query for the datatables i.e. it does not fetch the datatable from the endpoint.
 * 2. it shows all the datatables (from datatable array) unlike in the other fragments which show
 * a single datatable.
 */
@AndroidEntryPoint
class DataTableListFragment : Fragment() {

    private var dataTables: List<DataTable>? = null
    private var payload: Any? = null
    private var requestType = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DataTableListScreen(
                    onBackPressed = { requireActivity().supportFragmentManager.popBackStack() },
                    clientCreated = { it, it2 -> showClientCreatedSuccessfully(it) }
                )
            }
        }
    }

    private fun showClientCreatedSuccessfully(client: Client) {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.popBackStack()
        Toast.makeText(
            activity, getString(R.string.client) +
                    MifosResponseHandler.response, Toast.LENGTH_SHORT
        ).show()
        if (PrefManager.userStatus == Constants.USER_ONLINE) {
            val clientActivityIntent = Intent(activity, ClientActivity::class.java)
            clientActivityIntent.putExtra(Constants.CLIENT_ID, client.clientId)
            startActivity(clientActivityIntent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            dataTables: List<DataTable>?,
            payload: Any?, type: Int
        ): DataTableListFragment {
            val dataTableListFragment = DataTableListFragment()
            val args = Bundle()
            dataTableListFragment.dataTables = dataTables
            dataTableListFragment.requestType = type
            dataTableListFragment.payload = payload
            dataTableListFragment.arguments = args
            return dataTableListFragment
        }
    }
}