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
import androidclient.feature.data_table.generated.resources.Res
import androidclient.feature.data_table.generated.resources.feature_data_table_generic_failure_message
import androidclient.feature.data_table.generated.resources.feature_data_table_loan_creation_success
import androidclient.feature.data_table.generated.resources.feature_data_table_something_went_wrong
import androidclient.feature.data_table.generated.resources.feature_data_table_waiting_for_checker_approval
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.DataTableListRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.payloads.GroupLoanPayload
import com.mifos.core.network.model.LoansPayload
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.noncore.DataTableEntity
import com.mifos.room.entities.noncore.DataTablePayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

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
    val dataTableListUiState: StateFlow<DataTableListUiState> = _dataTableListUiState.asStateFlow()

    private val _dataTableList: MutableStateFlow<List<DataTableEntity>?> = MutableStateFlow(null)
    val dataTableList: StateFlow<List<DataTableEntity>?> = _dataTableList.asStateFlow()

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
                data = addDataTableInput(widgets = formWidgetsList[i]),
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
        viewModelScope.launch {
            repository.createLoansAccount(loansPayload)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error ->
                            _dataTableListUiState.value =
                                DataTableListUiState.ShowMessage(Res.string.feature_data_table_generic_failure_message)

                        DataState.Loading ->
                            _dataTableListUiState.value =
                                DataTableListUiState.Loading

                        is DataState.Success -> {
                            _dataTableListUiState.value =
                                DataTableListUiState.ShowMessage(Res.string.feature_data_table_loan_creation_success)
                        }
                    }
                }
        }
    }

    private fun createGroupLoanAccount(loansPayload: GroupLoanPayload?) {
        viewModelScope.launch {
            repository.createGroupLoansAccount(loansPayload)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error ->
                            _dataTableListUiState.value =
                                DataTableListUiState.ShowMessage(Res.string.feature_data_table_generic_failure_message)

                        DataState.Loading ->
                            _dataTableListUiState.value =
                                DataTableListUiState.Loading

                        is DataState.Success -> {
                            _dataTableListUiState.value =
                                DataTableListUiState.ShowMessage(Res.string.feature_data_table_loan_creation_success)
                        }
                    }
                }
        }
    }

    private fun createClient(clientPayload: ClientPayloadEntity) {
        viewModelScope.launch {
            _dataTableListUiState.value = DataTableListUiState.Loading

            try {
                val clientId = repository.createClient(clientPayload)

                if (clientId != null) {
                    _dataTableListUiState.value =
                        DataTableListUiState.Success(client = clientPayload)
                } else {
                    _dataTableListUiState.value =
                        DataTableListUiState.Success(Res.string.feature_data_table_waiting_for_checker_approval)
                }
            } catch (e: Exception) {
                Logger.e("ExceptionCaught", e)
                _dataTableListUiState.value =
                    DataTableListUiState.ShowMessage(Res.string.feature_data_table_something_went_wrong)
            }
        }
    }

    fun addDataTableInput(widgets: List<Any>): Map<String, Any> {
        val payload = mutableMapOf<String, Any>()
        payload["dateFormat"] = "dd-mm-YYYY"
        payload["locale"] = "en"

        for (widget in widgets) {
            when (widget) {
                is FormWidgetModel -> {
                    payload[widget.propertyName] = when (widget.returnType) {
                        BaseFormWidget.SCHEMA_KEY_INT -> widget.value.toIntOrNull() ?: 0
                        BaseFormWidget.SCHEMA_KEY_DECIMAL -> widget.value.toDoubleOrNull() ?: 0.0
                        else -> widget.value
                    }
                }

                is SpinnerModel -> {
                    payload[widget.propertyName] = widget.getSelectedId()
                }
            }
        }

        return payload
    }
}
