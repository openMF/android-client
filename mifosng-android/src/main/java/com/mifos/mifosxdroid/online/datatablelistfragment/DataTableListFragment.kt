/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.datatablelistfragment

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.data.LoansPayload
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.noncore.ColumnHeader
import com.mifos.core.objects.noncore.DataTable
import com.mifos.core.objects.noncore.DataTablePayload
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.DialogFragmentAddEntryToDatatableBinding
import com.mifos.mifosxdroid.formwidgets.*
import com.mifos.mifosxdroid.online.ClientActivity
import com.mifos.mifosxdroid.online.datatable.DataTableScreen
import com.mifos.utils.MifosResponseHandler
import com.mifos.utils.PrefManager
import com.mifos.utils.SafeUIBlockingUtility
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

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

    private val viewModel: DataTableListViewModel by viewModels()

    private var dataTables: List<DataTable>? = null
    private var payload: Any? = null
    private var requestType = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.initArgs(
            dataTables = dataTables ?: listOf(),
            requestType = requestType,
            payload = payload,
            formWidgetsList = createFormWidgetList(),
        )

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DataTableListScreen(
                    onBackPressed = { requireActivity().supportFragmentManager.popBackStack() },
                    clientCreated = { showClientCreatedSuccessfully(it) }
                )
            }
        }
    }

    private fun createFormWidgetList(): MutableList<List<FormWidget>> {
        return dataTables?.map { createForm(it) }?.toMutableList() ?: mutableListOf()
    }

    private fun createForm(table: DataTable): List<FormWidget> {
        return table.columnHeaderData
            .filterNot { it.columnPrimaryKey == true }
            .map { createFormWidget(it) }
    }

    private fun createFormWidget(columnHeader: ColumnHeader): FormWidget {
        return when (columnHeader.columnDisplayType) {
            FormWidget.SCHEMA_KEY_STRING, FormWidget.SCHEMA_KEY_TEXT -> FormEditText(activity, columnHeader.dataTableColumnName)
            FormWidget.SCHEMA_KEY_INT -> FormNumericEditText(activity, columnHeader.dataTableColumnName).apply { returnType = FormWidget.SCHEMA_KEY_INT }
            FormWidget.SCHEMA_KEY_DECIMAL -> FormNumericEditText(activity, columnHeader.dataTableColumnName).apply { returnType = FormWidget.SCHEMA_KEY_DECIMAL }
            FormWidget.SCHEMA_KEY_CODELOOKUP, FormWidget.SCHEMA_KEY_CODEVALUE -> createFormSpinner(columnHeader)
            FormWidget.SCHEMA_KEY_DATE -> FormEditText(activity, columnHeader.dataTableColumnName).apply { setIsDateField(true, requireActivity().supportFragmentManager) }
            FormWidget.SCHEMA_KEY_BOOL -> FormToggleButton(activity, columnHeader.dataTableColumnName)
            else -> FormEditText(activity, columnHeader.dataTableColumnName)
        }
    }

    private fun createFormSpinner(columnHeader: ColumnHeader): FormSpinner {
        val columnValueStrings = columnHeader.columnValues.mapNotNull { it.value }
        val columnValueIds = columnHeader.columnValues.mapNotNull { it.id }
        return FormSpinner(activity, columnHeader.dataTableColumnName, columnValueStrings, columnValueIds).apply {
            returnType = FormWidget.SCHEMA_KEY_CODEVALUE
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