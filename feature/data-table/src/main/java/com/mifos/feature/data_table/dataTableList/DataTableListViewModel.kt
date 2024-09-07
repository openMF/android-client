package com.mifos.feature.data_table.dataTableList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.data.LoansPayload
import com.mifos.core.data.repository.DataTableListRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.noncore.DataTable
import com.mifos.core.objects.noncore.DataTablePayload
import com.mifos.feature.data_table.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class DataTableListViewModel @Inject constructor(
    private val repository: DataTableListRepository,
    private val prefManager: PrefManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args =
        savedStateHandle.getStateFlow(key = Constants.DATA_TABLE_LIST_NAV_DATA, initialValue = "")
    val arg: DataTableListNavArgs = Gson().fromJson(args.value, DataTableListNavArgs::class.java)

    private val _dataTableListUiState: MutableStateFlow<DataTableListUiState> =
        MutableStateFlow(DataTableListUiState.Loading)
    val dataTableListUiState: StateFlow<DataTableListUiState> = _dataTableListUiState

    private val _dataTableList: MutableStateFlow<List<DataTable>?> = MutableStateFlow(null)
    val dataTableList: StateFlow<List<DataTable>?> = _dataTableList

    private var requestType: Int = 0
    private var dataTablePayloadElements: ArrayList<DataTablePayload>? = null
    private var clientLoansPayload: LoansPayload? = null
    private var groupLoanPayload: GroupLoanPayload? = null
    private var clientPayload: ClientPayload? = null
    private var formWidgetsList: MutableList<List<FormWidget>> = ArrayList()

    fun getUserStatus() : Boolean {
        return prefManager.userStatus
    }

    fun initArgs(
        dataTables: List<DataTable>,
        requestType: Int,
        formWidgetsList: MutableList<List<FormWidget>>,
        payload: Any?
    ) {
        _dataTableList.value = dataTables
        this.requestType = requestType
        this.formWidgetsList = formWidgetsList
        when (requestType) {
            Constants.CLIENT_LOAN -> clientLoansPayload = payload as LoansPayload?
            Constants.GROUP_LOAN -> groupLoanPayload = payload as GroupLoanPayload?
            Constants.CREATE_CLIENT -> clientPayload = payload as ClientPayload?
        }
    }

    fun processDataTable() {
        val dataTables = dataTableList.value ?: listOf()
        for (i in dataTables.indices) {
            val dataTablePayload = DataTablePayload()
            dataTablePayload.registeredTableName = dataTables[i].registeredTableName
            dataTablePayload.data = addDataTableInput(formWidgets = formWidgetsList[i])
            dataTablePayloadElements?.add(dataTablePayload)
        }
        when (requestType) {
            Constants.CLIENT_LOAN -> {
                clientLoansPayload?.dataTables = dataTablePayloadElements
                createLoansAccount(clientLoansPayload)
            }

            Constants.CREATE_CLIENT -> {
                clientPayload?.datatables = dataTablePayloadElements
                clientPayload?.let { createClient(it) }
            }

            Constants.GROUP_LOAN -> {
                createGroupLoanAccount(groupLoanPayload)
            }
        }
    }

    private fun createLoansAccount(loansPayload: LoansPayload?) {
        _dataTableListUiState.value = DataTableListUiState.Loading
        repository.createLoansAccount(loansPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Loans>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessage(R.string.feature_data_table_generic_failure_message)
                }

                override fun onNext(loans: Loans) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessage(R.string.feature_data_table_loan_creation_success)
                }
            })
    }

    private fun createGroupLoanAccount(loansPayload: GroupLoanPayload?) {
        _dataTableListUiState.value = DataTableListUiState.Loading
        repository.createGroupLoansAccount(loansPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Loans?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessage(R.string.feature_data_table_generic_failure_message)
                }

                override fun onNext(loans: Loans?) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessage(R.string.feature_data_table_loan_creation_success)
                }
            })

    }

    private fun createClient(clientPayload: ClientPayload) {
        _dataTableListUiState.value = DataTableListUiState.Loading
        repository.createClient(clientPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Client>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessage(message = e.message.toString())
                }

                override fun onNext(client: Client) {
                    if (client.clientId != null) {
                        _dataTableListUiState.value = DataTableListUiState.Success(client = client)
                    } else {
                        _dataTableListUiState.value =
                            DataTableListUiState.Success(messageResId = R.string.feature_data_table_waiting_for_checker_approval)
                    }
                }
            })
    }

    private fun addDataTableInput(formWidgets: List<FormWidget>): HashMap<String, Any> {
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
}