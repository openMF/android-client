/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTableList

import androidclient.feature.data_table.generated.resources.Res
import androidclient.feature.data_table.generated.resources.feature_data_table_generic_failure_message
import androidclient.feature.data_table.generated.resources.feature_data_table_loan_creation_success
import androidclient.feature.data_table.generated.resources.feature_data_table_something_went_wrong
import androidclient.feature.data_table.generated.resources.feature_data_table_waiting_for_checker_approval
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.DataTableListRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.payloads.GroupLoanPayload
import com.mifos.core.network.model.LoansPayload
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.noncore.ColumnHeader
import com.mifos.room.entities.noncore.DataTableEntity
import com.mifos.room.entities.noncore.DataTablePayload
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.StringResource

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class DataTableListViewModel(
    private val repository: DataTableListRepository,
    private val prefManager: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<DataTableListState, DataTableListEvent, DataTableListAction>(
    initialState = DataTableListState(),
) {

    private val json = Json {
        serializersModule = SerializersModule {
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

    private val requestType: Int = arg.requestType
    private var dataTablePayloadElements: ArrayList<DataTablePayload> = ArrayList()
    private var clientLoanPayload: LoansPayload? = null
    private var groupLoanPayload: GroupLoanPayload? = null
    private var clientPayload: ClientPayloadEntity? = null

    init {
        when (requestType) {
            Constants.CLIENT_LOAN -> clientLoanPayload = arg.payload as LoansPayload?
            Constants.GROUP_LOAN -> groupLoanPayload = arg.payload as GroupLoanPayload?
            Constants.CREATE_CLIENT -> clientPayload = arg.payload as ClientPayloadEntity?
        }
        mutableStateFlow.update {
            it.copy(
                dataTableList = arg.dataTableList,
                formValues = arg.dataTableList.indices.associateWith { emptyMap() },
            )
        }
        viewModelScope.launch {
            prefManager.userInfo
                .map { it.userStatus }
                .collect { status ->
                    mutableStateFlow.update { it.copy(userStatus = status) }
                }
        }
    }

    override fun handleAction(action: DataTableListAction) {
        when (action) {
            is DataTableListAction.OnFieldChanged -> updateFieldValue(
                action.tableIndex,
                action.columnName,
                action.value,
            )

            DataTableListAction.OnSaveClicked -> processDataTable()
        }
    }

    private fun updateFieldValue(tableIndex: Int, columnName: String, value: Any) {
        mutableStateFlow.update { current ->
            val tableMap = current.formValues[tableIndex].orEmpty().toMutableMap()
            tableMap[columnName] = value
            current.copy(formValues = current.formValues + (tableIndex to tableMap))
        }
    }

    private fun processDataTable() {
        val dataTables = state.dataTableList
        dataTablePayloadElements.clear()

        dataTables.indices.forEach { index ->
            val rawValues = state.formValues[index].orEmpty()
            val data = buildPayloadMap(dataTables[index].columnHeaderData, rawValues)
            dataTablePayloadElements.add(
                DataTablePayload(
                    registeredTableName = dataTables[index].registeredTableName,
                    data = data,
                ),
            )
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

    private fun buildPayloadMap(
        headers: List<ColumnHeader>,
        rawValues: Map<String, Any>,
    ): Map<String, Any> {
        val payload = mutableMapOf<String, Any>(
            "dateFormat" to ApiDateFormatter.DATE_FORMAT,
            "locale" to ApiDateFormatter.LOCALE,
        )
        headers
            .filter { it.columnPrimaryKey == false }
            .forEach { header ->
                val name = header.dataTableColumnName ?: return@forEach
                val raw = rawValues[name] ?: return@forEach
                val coerced = coerce(raw, header.columnDisplayType) ?: return@forEach
                payload[name] = coerced
            }
        return payload
    }

    private fun coerce(value: Any, displayType: String?): Any? {
        if (value !is String) return value
        if (value.isBlank()) return null
        return when (displayType) {
            DataTableColumnType.INTEGER -> value.toIntOrNull()
            DataTableColumnType.DECIMAL, DataTableColumnType.FLOAT -> value.toDoubleOrNull()
            else -> value
        }
    }

    private fun createLoanAccount(loansPayload: LoansPayload?) {
        viewModelScope.launch {
            repository.createLoansAccount(loansPayload)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(screenState = DataTableListState.ScreenState.Content)
                            }
                            sendEvent(
                                DataTableListEvent.ShowMessage(
                                    Res.string.feature_data_table_generic_failure_message,
                                ),
                            )
                        }

                        DataState.Loading ->
                            mutableStateFlow.update {
                                it.copy(screenState = DataTableListState.ScreenState.Loading)
                            }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(screenState = DataTableListState.ScreenState.Content)
                            }
                            sendEvent(
                                DataTableListEvent.ShowMessage(
                                    Res.string.feature_data_table_loan_creation_success,
                                ),
                            )
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
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(screenState = DataTableListState.ScreenState.Content)
                            }
                            sendEvent(
                                DataTableListEvent.ShowMessage(
                                    Res.string.feature_data_table_generic_failure_message,
                                ),
                            )
                        }

                        DataState.Loading ->
                            mutableStateFlow.update {
                                it.copy(screenState = DataTableListState.ScreenState.Loading)
                            }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(screenState = DataTableListState.ScreenState.Content)
                            }
                            sendEvent(
                                DataTableListEvent.ShowMessage(
                                    Res.string.feature_data_table_loan_creation_success,
                                ),
                            )
                        }
                    }
                }
        }
    }

    private fun createClient(clientPayload: ClientPayloadEntity) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(screenState = DataTableListState.ScreenState.Loading)
            }

            try {
                val clientId = repository.createClient(clientPayload)
                mutableStateFlow.update {
                    it.copy(screenState = DataTableListState.ScreenState.Content)
                }

                if (clientId != null) {
                    sendEvent(DataTableListEvent.ClientCreated(clientPayload))
                } else {
                    sendEvent(
                        DataTableListEvent.ShowMessage(
                            Res.string.feature_data_table_waiting_for_checker_approval,
                        ),
                    )
                    sendEvent(DataTableListEvent.NavigateBack)
                }
            } catch (e: Exception) {
                Logger.e("ExceptionCaught", e)
                mutableStateFlow.update {
                    it.copy(screenState = DataTableListState.ScreenState.Content)
                }
                sendEvent(
                    DataTableListEvent.ShowMessage(
                        Res.string.feature_data_table_something_went_wrong,
                    ),
                )
            }
        }
    }
}

data class DataTableListState(
    val dataTableList: List<DataTableEntity> = emptyList(),
    val formValues: Map<Int, Map<String, Any>> = emptyMap(),
    val userStatus: Boolean = false,
    val screenState: ScreenState = ScreenState.Content,
) {
    sealed interface ScreenState {
        data object Loading : ScreenState
        data object Content : ScreenState
    }
}

sealed interface DataTableListEvent {
    data object NavigateBack : DataTableListEvent
    data class ClientCreated(val client: ClientPayloadEntity) : DataTableListEvent
    data class ShowMessage(val message: StringResource) : DataTableListEvent
}

sealed interface DataTableListAction {
    data object OnSaveClicked : DataTableListAction
    data class OnFieldChanged(
        val tableIndex: Int,
        val columnName: String,
        val value: Any,
    ) : DataTableListAction
}
