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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.data.LoansPayload
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.noncore.DataTable
import com.mifos.core.objects.noncore.DataTablePayload
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.DialogFragmentAddEntryToDatatableBinding
import com.mifos.mifosxdroid.formwidgets.*
import com.mifos.mifosxdroid.online.ClientActivity
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

    private lateinit var binding: DialogFragmentAddEntryToDatatableBinding

    private val LOG_TAG = javaClass.simpleName

    private lateinit var viewModel: DataTableListViewModel

    private var dataTables: List<DataTable>? = null
    private var dataTablePayloadElements: ArrayList<DataTablePayload>? = null
    private var clientLoansPayload: LoansPayload? = null
    private var groupLoanPayload: GroupLoanPayload? = null
    private lateinit var clientPayload: ClientPayload
    private var requestType = 0
    private var safeUIBlockingUtility: SafeUIBlockingUtility? = null
    private val listFormWidgets: MutableList<List<FormWidget>> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding = DialogFragmentAddEntryToDatatableBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DataTableListViewModel::class.java]
        requireActivity().title = requireActivity().resources.getString(
            R.string.associated_datatables
        )
        safeUIBlockingUtility = SafeUIBlockingUtility(
            requireContext(), getString(R.string.create_client_loading_message)
        )
        for (datatable in dataTables!!) {
            createForm(datatable)
        }
        addSaveButton()

        viewModel.dataTableListUiState.observe(viewLifecycleOwner) {
            when (it) {
                is DataTableListUiState.ShowClientCreatedSuccessfully -> {
                    showProgressbar(false)
                    showClientCreatedSuccessfully(it.client)
                }

                is DataTableListUiState.ShowMessage -> {
                    showProgressbar(false)
                    showMessage(it.message)
                }

                is DataTableListUiState.ShowMessageString -> {
                    showProgressbar(false)
                    showMessage(it.message)
                }

                is DataTableListUiState.ShowProgressBar -> showProgressbar(true)
                is DataTableListUiState.ShowWaitingForCheckerApproval -> {
                    showProgressbar(false)
                    showWaitingForCheckerApproval(it.message)
                }
            }
        }

        return binding.root
    }

    private fun createForm(table: DataTable) {
        val tableName = TextView(requireActivity().applicationContext)
        tableName.text = table.registeredTableName
        tableName.gravity = Gravity.CENTER_HORIZONTAL
        tableName.setTypeface(null, Typeface.BOLD)
        tableName.setTextColor(requireActivity().resources.getColor(R.color.black))
        tableName.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            requireActivity().resources.getDimension(R.dimen.datatable_name_heading)
        )
        binding.llDataTableEntryForm.addView(tableName)
        val formWidgets: MutableList<FormWidget> = ArrayList()
        for (columnHeader in table.columnHeaderData) {
            if (!columnHeader.columnPrimaryKey!!) {
                if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_STRING || columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_TEXT) {
                    val formEditText = FormEditText(
                        activity, columnHeader
                            .dataTableColumnName
                    )
                    formWidgets.add(formEditText)
                    binding.llDataTableEntryForm.addView(formEditText.view)
                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_INT) {
                    val formNumericEditText =
                        FormNumericEditText(activity, columnHeader.dataTableColumnName)
                    formNumericEditText.returnType = FormWidget.SCHEMA_KEY_INT
                    formWidgets.add(formNumericEditText)
                    binding.llDataTableEntryForm.addView(formNumericEditText.view)
                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_DECIMAL) {
                    val formNumericEditText =
                        FormNumericEditText(activity, columnHeader.dataTableColumnName)
                    formNumericEditText.returnType = FormWidget.SCHEMA_KEY_DECIMAL
                    formWidgets.add(formNumericEditText)
                    binding.llDataTableEntryForm.addView(formNumericEditText.view)
                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_CODELOOKUP || columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_CODEVALUE) {
                    if (columnHeader.columnValues.isNotEmpty()) {
                        val columnValueStrings: MutableList<String> = ArrayList()
                        val columnValueIds: MutableList<Int> = ArrayList()
                        for (columnValue in columnHeader.columnValues) {
                            columnValue.value?.let { columnValueStrings.add(it) }
                            columnValue.id?.let { columnValueIds.add(it) }
                        }
                        val formSpinner = FormSpinner(
                            activity, columnHeader
                                .dataTableColumnName, columnValueStrings, columnValueIds
                        )
                        formSpinner.returnType = FormWidget.SCHEMA_KEY_CODEVALUE
                        formWidgets.add(formSpinner)
                        binding.llDataTableEntryForm.addView(formSpinner.view)
                    }
                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_DATE) {
                    val formEditText = FormEditText(
                        activity, columnHeader
                            .dataTableColumnName
                    )
                    formEditText.setIsDateField(true, requireActivity().supportFragmentManager)
                    formWidgets.add(formEditText)
                    binding.llDataTableEntryForm.addView(formEditText.view)
                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_BOOL) {
                    val formToggleButton = FormToggleButton(
                        activity,
                        columnHeader.dataTableColumnName
                    )
                    formWidgets.add(formToggleButton)
                    binding.llDataTableEntryForm.addView(formToggleButton.view)
                }
            }
        }
        listFormWidgets.add(formWidgets)
    }

    private fun addSaveButton() {
        val bt_processForm = Button(activity)
        bt_processForm.layoutParams = FormWidget.defaultLayoutParams
        bt_processForm.text = getString(R.string.save)
        bt_processForm.setBackgroundColor(requireActivity().resources.getColor(R.color.blue_dark))
        binding.llDataTableEntryForm.addView(bt_processForm)
        bt_processForm.setOnClickListener {
            try {
                onSaveActionRequested()
            } catch (e: RequiredFieldException) {
                Log.d(LOG_TAG, e.message.toString())
            }
        }
    }

    @Throws(RequiredFieldException::class)
    fun onSaveActionRequested() {
        dataTablePayloadElements = ArrayList()
        for (i in dataTables!!.indices) {
            val dataTablePayload = DataTablePayload()
            dataTablePayload.registeredTableName = dataTables!![i].registeredTableName
            dataTablePayload.data = addDataTableInput(i)
            dataTablePayloadElements?.add(dataTablePayload)
        }
        when (requestType) {
            Constants.CLIENT_LOAN -> {
                clientLoansPayload?.dataTables = dataTablePayloadElements
                viewModel.createLoansAccount(clientLoansPayload)
            }

            Constants.GROUP_LOAN ->                 //Add Datatables in GroupLoan Payload and then add them here.
                viewModel.createGroupLoanAccount(groupLoanPayload)

            Constants.CREATE_CLIENT -> {
                clientPayload.datatables = dataTablePayloadElements
                viewModel.createClient(clientPayload)
            }
        }
    }

    private fun addDataTableInput(index: Int): HashMap<String, Any> {
        val formWidgets = listFormWidgets[index]
        val payload = HashMap<String, Any>()
        payload[Constants.DATE_FORMAT] = "dd-mm-YYYY"
        payload[Constants.LOCALE] = "en"
        for (formWidget in formWidgets) {
            when (formWidget.returnType) {
                FormWidget.SCHEMA_KEY_INT -> {
                    payload[formWidget.propertyName] = if (formWidget.value
                        == ""
                    ) "0" else formWidget.value.toInt()
                }

                FormWidget.SCHEMA_KEY_DECIMAL -> {
                    payload[formWidget.propertyName] =
                        if (formWidget.value == "") "0.0" else formWidget.value.toDouble()
                }

                FormWidget.SCHEMA_KEY_CODEVALUE -> {
                    val formSpinner = formWidget as FormSpinner
                    payload[formWidget.propertyName] =
                        formSpinner.getIdOfSelectedItem(formWidget.value)
                }

                else -> {
                    payload[formWidget.propertyName] = formWidget.value
                }
            }
        }
        return payload
    }

    private fun showMessage(messageId: Int) {
        Toaster.show(binding.root, getString(messageId))
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    private fun showMessage(message: String?) {
        Toaster.show(binding.root, message)
        requireActivity().supportFragmentManager.popBackStackImmediate()
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

    private fun showWaitingForCheckerApproval(message: Int) {
        requireActivity().supportFragmentManager.popBackStack()
        Toaster.show(binding.root, message, Toast.LENGTH_SHORT)
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            safeUIBlockingUtility?.safelyBlockUI()
        } else {
            safeUIBlockingUtility?.safelyUnBlockUI()
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
            when (type) {
                Constants.CLIENT_LOAN -> dataTableListFragment.clientLoansPayload =
                    payload as LoansPayload?

                Constants.GROUP_LOAN -> dataTableListFragment.groupLoanPayload =
                    payload as GroupLoanPayload?

                Constants.CREATE_CLIENT -> dataTableListFragment.clientPayload =
                    payload as ClientPayload
            }
            dataTableListFragment.arguments = args
            return dataTableListFragment
        }
    }
}