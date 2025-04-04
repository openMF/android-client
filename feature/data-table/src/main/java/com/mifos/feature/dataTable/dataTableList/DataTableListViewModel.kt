/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTableList

import FormSpinnerDTO
import FormWidgetDTO
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.DataTableListRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.payloads.GroupLoanPayload
import com.mifos.core.network.model.LoansPayload
import com.mifos.feature.data_table.R
import com.mifos.room.entities.accounts.loans.Loan
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.noncore.DataTableEntity
import com.mifos.room.entities.noncore.DataTablePayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class DataTableListViewModel(
    private val repository: DataTableListRepository,
    private val prefManager: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val userStatus: StateFlow<Boolean> = prefManager.userInfo
        .map { it.userStatus }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    private val json = Json {
        serializersModule = SerializersModule {
            polymorphic(FormWidgetDTO::class) {
                subclass(FormSpinnerDTO::class, FormSpinnerDTO.serializer())
            }

            polymorphic(Any::class) {
                subclass(LoansPayload::class, LoansPayload.serializer())
                subclass(GroupLoanPayload::class, GroupLoanPayload.serializer())
                subclass(ClientPayloadEntity::class, ClientPayloadEntity.serializer())
            }
        }
    }

    private val args =
        savedStateHandle.getStateFlow(key = Constants.DATA_TABLE_LIST_NAV_DATA, initialValue = "")
    val arg: DataTableListNavArgs = json.decodeFromString<DataTableListNavArgs>(args.value)

    private val _dataTableListUiState: MutableStateFlow<DataTableListUiState> =
        MutableStateFlow(DataTableListUiState.Loading)
    val dataTableListUiState: StateFlow<DataTableListUiState> = _dataTableListUiState

    private val _dataTableList: MutableStateFlow<List<DataTableEntity>?> = MutableStateFlow(null)
    val dataTableList: StateFlow<List<DataTableEntity>?> = _dataTableList

    private var requestType: Int = 0
    private var dataTablePayloadElements: ArrayList<DataTablePayload>? = null
    private var clientLoanPayload: LoansPayload? = null
    private var groupLoanPayload: GroupLoanPayload? = null
    private var clientPayload: ClientPayloadEntity? = null
    private var formWidgetsList: MutableList<List<FormWidgetDTO>> = ArrayList()

    fun initArgs(
        dataTables: List<DataTableEntity>,
        requestType: Int,
        formWidgetsList: MutableList<List<FormWidgetDTO>>,
        payload: Any?,
    ) {
        _dataTableList.value = dataTables
        this.requestType = requestType
        this.formWidgetsList = formWidgetsList
        when (requestType) {
            Constants.CLIENT_LOAN -> clientLoanPayload = payload as LoansPayload?
            Constants.GROUP_LOAN -> groupLoanPayload = payload as GroupLoanPayload?
            Constants.CREATE_CLIENT -> clientPayload = payload as ClientPayloadEntity?
        }
    }

    fun processDataTable() {
        val dataTables = dataTableList.value ?: listOf()
        for (i in dataTables.indices) {
            val dataTablePayload = DataTablePayload(
                registeredTableName = dataTables[i].registeredTableName,
                data = addDataTableInput(formWidgets = formWidgetsList[i]),
            )

            dataTablePayloadElements?.add(dataTablePayload)
        }
        when (requestType) {
            Constants.CLIENT_LOAN -> {
                clientLoanPayload?.dataTables = dataTablePayloadElements
                createLoanAccount(clientLoanPayload)
            }

            Constants.CREATE_CLIENT -> {
                clientPayload = clientPayload?.copy(datatables = dataTablePayloadElements)
                clientPayload?.let { createClient(it) }
            }

            Constants.GROUP_LOAN -> {
                createGroupLoanAccount(groupLoanPayload)
            }
        }
    }

    private fun createLoanAccount(loansPayload: LoansPayload?) {
        _dataTableListUiState.value = DataTableListUiState.Loading
        repository.createLoansAccount(loansPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Loan>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessage(R.string.feature_data_table_generic_failure_message)
                }

                override fun onNext(loans: Loan) {
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
            .subscribe(object : Subscriber<Loan?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessage(R.string.feature_data_table_generic_failure_message)
                }

                override fun onNext(loans: Loan?) {
                    _dataTableListUiState.value =
                        DataTableListUiState.ShowMessage(R.string.feature_data_table_loan_creation_success)
                }
            })
    }

    private fun createClient(clientPayload: ClientPayloadEntity) {
        viewModelScope.launch {
            _dataTableListUiState.value = DataTableListUiState.Loading

            try {
                val clientId = repository.createClient(clientPayload)

                if (clientId != null) {
                    // todo uncomment and resolve
//                    _dataTableListUiState.value = DataTableListUiState.Success(client = clientPayload)
                } else {
                    _dataTableListUiState.value =
                        DataTableListUiState.Success(messageResId = R.string.feature_data_table_waiting_for_checker_approval)
                }
            } catch (e: Exception) {
                _dataTableListUiState.value =
                    DataTableListUiState.ShowMessage(message = e.message.toString())
            }
        }
    }

    private fun addDataTableInput(formWidgets: List<FormWidgetDTO>): HashMap<String, Any> {
        val payload = HashMap<String, Any>()
        payload[Constants.DATE_FORMAT] = "dd-mm-YYYY"
        payload[Constants.LOCALE] = "en"
        for (formWidget in formWidgets) {
            when (formWidget.returnType) {
                FormWidget.SCHEMA_KEY_INT -> {
                    payload[formWidget.propertyName] = (
                        if (formWidget.value
                            == ""
                        ) {
                            "0"
                        } else {
                            formWidget.value?.toInt()
                        }
                        )!!
                }

                FormWidget.SCHEMA_KEY_DECIMAL -> {
                    payload[formWidget.propertyName] =
                        (if (formWidget.value == "") "0.0" else formWidget.value?.toDouble())!!
                }

                FormWidget.SCHEMA_KEY_CODEVALUE -> {
                    val formSpinner = formWidget as FormSpinner
                    payload[formWidget.propertyName] =
                        formSpinner.getIdOfSelectedItem(formWidget.value)
                }

                else -> {
                    payload[formWidget.propertyName] = formWidget.returnType
                }
            }
        }
        return payload
    }
}
