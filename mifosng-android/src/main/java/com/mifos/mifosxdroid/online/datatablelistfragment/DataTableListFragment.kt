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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.exceptions.RequiredFieldException
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.formwidgets.*
import com.mifos.mifosxdroid.online.ClientActivity
import com.mifos.mifosxdroid.online.datatablelistfragment.DataTableListFragment
import com.mifos.objects.client.Client
import com.mifos.objects.client.ClientPayload
import com.mifos.objects.noncore.DataTable
import com.mifos.objects.noncore.DataTablePayload
import com.mifos.services.data.GroupLoanPayload
import com.mifos.services.data.LoansPayload
import com.mifos.utils.Constants
import com.mifos.utils.MifosResponseHandler
import com.mifos.utils.PrefManager
import com.mifos.utils.SafeUIBlockingUtility
import java.util.*
import javax.inject.Inject

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
class DataTableListFragment : Fragment(), DataTableListMvpView {
    private val LOG_TAG = javaClass.simpleName

    @JvmField
    @BindView(R.id.ll_data_table_entry_form)
    var linearLayout: LinearLayout? = null

    @JvmField
    @Inject
    var mDataTableListPresenter: DataTableListPresenter? = null
    private var dataTables: List<DataTable>? = null
    private var dataTablePayloadElements: ArrayList<DataTablePayload>? = null
    private var clientLoansPayload: LoansPayload? = null
    private var groupLoanPayload: GroupLoanPayload? = null
    private var clientPayload: ClientPayload? = null
    private var requestType = 0
    private lateinit var rootView: View
    private var safeUIBlockingUtility: SafeUIBlockingUtility? = null
    private val listFormWidgets: MutableList<List<FormWidget>> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        rootView = inflater.inflate(R.layout.dialog_fragment_add_entry_to_datatable, container,
                false)
        ButterKnife.bind(this, rootView)
        mDataTableListPresenter!!.attachView(this)
        requireActivity().title = requireActivity().resources.getString(
                R.string.associated_datatables)
        safeUIBlockingUtility = SafeUIBlockingUtility(this@DataTableListFragment
                .activity, getString(R.string.create_client_loading_message))
        for (datatable in dataTables!!) {
            createForm(datatable)
        }
        addSaveButton()
        return rootView
    }

    fun createForm(table: DataTable) {
        val tableName = TextView(requireActivity().applicationContext)
        tableName.text = table.registeredTableName
        tableName.gravity = Gravity.CENTER_HORIZONTAL
        tableName.setTypeface(null, Typeface.BOLD)
        tableName.setTextColor(requireActivity().resources.getColor(R.color.black))
        tableName.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                requireActivity().resources.getDimension(R.dimen.datatable_name_heading))
        linearLayout!!.addView(tableName)
        val formWidgets: MutableList<FormWidget> = ArrayList()
        for (columnHeader in table.columnHeaderData) {
            if (!columnHeader.columnPrimaryKey) {
                if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_STRING || columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_TEXT) {
                    val formEditText = FormEditText(activity, columnHeader
                            .columnName)
                    formWidgets.add(formEditText)
                    linearLayout!!.addView(formEditText.view)
                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_INT) {
                    val formNumericEditText = FormNumericEditText(activity, columnHeader.columnName)
                    formNumericEditText.returnType = FormWidget.SCHEMA_KEY_INT
                    formWidgets.add(formNumericEditText)
                    linearLayout!!.addView(formNumericEditText.view)
                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_DECIMAL) {
                    val formNumericEditText = FormNumericEditText(activity, columnHeader.columnName)
                    formNumericEditText.returnType = FormWidget.SCHEMA_KEY_DECIMAL
                    formWidgets.add(formNumericEditText)
                    linearLayout!!.addView(formNumericEditText.view)
                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_CODELOOKUP || columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_CODEVALUE) {
                    if (columnHeader.columnValues.size > 0) {
                        val columnValueStrings: MutableList<String> = ArrayList()
                        val columnValueIds: MutableList<Int> = ArrayList()
                        for (columnValue in columnHeader.columnValues) {
                            columnValueStrings.add(columnValue.value)
                            columnValueIds.add(columnValue.id)
                        }
                        val formSpinner = FormSpinner(activity, columnHeader
                                .columnName, columnValueStrings, columnValueIds)
                        formSpinner.returnType = FormWidget.SCHEMA_KEY_CODEVALUE
                        formWidgets.add(formSpinner)
                        linearLayout!!.addView(formSpinner.view)
                    }
                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_DATE) {
                    val formEditText = FormEditText(activity, columnHeader
                            .columnName)
                    formEditText.setIsDateField(true, requireActivity().supportFragmentManager)
                    formWidgets.add(formEditText)
                    linearLayout!!.addView(formEditText.view)
                } else if (columnHeader.columnDisplayType == FormWidget.SCHEMA_KEY_BOOL) {
                    val formToggleButton = FormToggleButton(activity,
                            columnHeader.columnName)
                    formWidgets.add(formToggleButton)
                    linearLayout!!.addView(formToggleButton.view)
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
        linearLayout!!.addView(bt_processForm)
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
            dataTablePayloadElements!!.add(dataTablePayload)
        }
        when (requestType) {
            Constants.CLIENT_LOAN -> {
                clientLoansPayload!!.dataTables = dataTablePayloadElements
                mDataTableListPresenter!!.createLoansAccount(clientLoansPayload)
            }
            Constants.GROUP_LOAN ->                 //Add Datatables in GroupLoan Payload and then add them here.
                mDataTableListPresenter!!.createGroupLoanAccount(groupLoanPayload)
            Constants.CREATE_CLIENT -> {
                clientPayload!!.datatables = dataTablePayloadElements
                mDataTableListPresenter!!.createClient(clientPayload)
            }
        }
    }

    fun addDataTableInput(index: Int): HashMap<String, Any> {
        val formWidgets = listFormWidgets[index]
        val payload = HashMap<String, Any>()
        payload[Constants.DATE_FORMAT] = "dd-mm-YYYY"
        payload[Constants.LOCALE] = "en"
        for (formWidget in formWidgets) {
            if (formWidget.returnType == FormWidget.SCHEMA_KEY_INT) {
                payload[formWidget.propertyName] = if (formWidget.value
                        == "") "0" else formWidget.value.toInt()
            } else if (formWidget.returnType == FormWidget.SCHEMA_KEY_DECIMAL) {
                payload[formWidget.propertyName] = if (formWidget.value == "") "0.0" else formWidget.value.toDouble()
            } else if (formWidget.returnType == FormWidget.SCHEMA_KEY_CODEVALUE) {
                val formSpinner = formWidget as FormSpinner
                payload[formWidget.getPropertyName()] = formSpinner.getIdOfSelectedItem(formWidget.getValue())
            } else {
                payload[formWidget.propertyName] = formWidget.value
            }
        }
        return payload
    }

    override fun showMessage(messageId: Int) {
        Toaster.show(rootView, getString(messageId))
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    override fun showMessage(message: String?) {
        Toaster.show(rootView, message)
        requireActivity().supportFragmentManager.popBackStackImmediate()
    }

    override fun showClientCreatedSuccessfully(client: Client) {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.popBackStack()
        Toast.makeText(activity, getString(R.string.client) +
                MifosResponseHandler.getResponse(), Toast.LENGTH_SHORT).show()
        if (PrefManager.getUserStatus() == Constants.USER_ONLINE) {
            val clientActivityIntent = Intent(activity, ClientActivity::class.java)
            clientActivityIntent.putExtra(Constants.CLIENT_ID, client.clientId)
            startActivity(clientActivityIntent)
        }
    }

    override fun showWaitingForCheckerApproval(message: Int) {
        requireActivity().supportFragmentManager.popBackStack()
        Toaster.show(rootView, message, Toast.LENGTH_SHORT)
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            safeUIBlockingUtility!!.safelyBlockUI()
        } else {
            safeUIBlockingUtility!!.safelyUnBlockUI()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDataTableListPresenter!!.detachView()
    }

    companion object {
        @JvmStatic
        fun newInstance(dataTables: List<DataTable>?,
                        payload: Any?, type: Int): DataTableListFragment {
            val dataTableListFragment = DataTableListFragment()
            val args = Bundle()
            dataTableListFragment.dataTables = dataTables
            dataTableListFragment.requestType = type
            when (type) {
                Constants.CLIENT_LOAN -> dataTableListFragment.clientLoansPayload = payload as LoansPayload?
                Constants.GROUP_LOAN -> dataTableListFragment.groupLoanPayload = payload as GroupLoanPayload?
                Constants.CREATE_CLIENT -> dataTableListFragment.clientPayload = payload as ClientPayload?
            }
            dataTableListFragment.arguments = args
            return dataTableListFragment
        }
    }
}