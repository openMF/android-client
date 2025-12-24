/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientsList

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.filter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.Page
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.repository.ClientListRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.imageToByteArray
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ClientListViewModel(
    private val repository: ClientListRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val clientDetailsRepo: ClientDetailsRepository,
) : BaseViewModel<ClientListState, ClientListEvent, ClientListAction>(
    initialState = ClientListState(
        dialogState = ClientListState.DialogState.Loading,
        clients = emptyList(),
        isOnline = false,
        clientsFlow = null,
        unfilteredClients = emptyList(),
    ),
) {

    init {
        // load initial data
        loadClients()
    }

    override fun handleAction(action: ClientListAction) {
        when (action) {
            is ClientListAction.RefreshClients -> refreshClients()
            is ClientListAction.OnDismissDialog -> dismissDialog()
            is ClientListAction.OnClientClick -> sendEvent(ClientListEvent.OnClientClick(action.clientId))
            is ClientListAction.Internal.ReceiveClientResult -> handleClientResult(action.result)
            is ClientListAction.Internal.ReceiveClientResultFromDb -> handleClientResultFromDb(action.result)
            is ClientListAction.FetchImage -> fetchClientImage(action.clientId)
            ClientListAction.ActivateSearch -> {
                updateState {
                    it.copy(
                        isSearchActive = true,
                    )
                }
            }
            ClientListAction.DismissSearch -> {
                updateState {
                    it.copy(
                        isSearchActive = false,
                    )
                }
            }
            ClientListAction.NavigateToCreateClient -> sendEvent(ClientListEvent.NavigateToCreateClient)
            is ClientListAction.OnQueryChange -> {
                updateState {
                    it.copy(
                        searchQuery = action.query,
                    )
                }
            }

            ClientListAction.ToggleFilterVisibility -> toggleFilterVisibility()
            is ClientListAction.HandleSortClick -> handleSortClick(action.sort)
            is ClientListAction.OnUpdateOffice -> onUpdateOffice(action.offices)
            is ClientListAction.HandleFilterClick -> handleFilterClick(action.filter, action.filterType)
            ClientListAction.ClearFilters -> clearFilters()
        }
    }

    private fun updateState(update: (ClientListState) -> ClientListState) {
        mutableStateFlow.update(update)
    }

    private fun dismissDialog() {
        updateState { it.copy(dialogState = null) }
    }

    private fun refreshClients() = loadClients()

    private fun loadClients() {
        viewModelScope.launch {
            val userStatus = userPreferencesRepository.userInfo.first().userStatus
            if (userStatus) {
                processClientsFromDb()
            } else {
                processClientsFromApi()
            }
        }
    }

    private fun processClientsFromApi() {
        viewModelScope.launch {
            updateState { it.copy(dialogState = ClientListState.DialogState.Loading) }
            runCatching {
                repository.getAllClients()
            }.onSuccess { result ->
                sendAction(ClientListAction.Internal.ReceiveClientResult(result))
            }.onFailure { throwable ->
                updateState {
                    it.copy(
                        dialogState = ClientListState.DialogState.Error(
                            throwable.message ?: "An error occurred while loading clients",
                        ),
                    )
                }
            }
        }
    }

    private fun processClientsFromDb() {
        viewModelScope.launch {
            repository.allDatabaseClients().collect { result ->
                sendAction(ClientListAction.Internal.ReceiveClientResultFromDb(result))
            }
        }
    }

    private fun handleClientResultFromDb(result: DataState<Page<ClientEntity>>) {
        when (result) {
            is DataState.Loading -> updateState {
                it.copy(dialogState = ClientListState.DialogState.Loading)
            }

            is DataState.Error -> updateState {
                it.copy(
                    dialogState = ClientListState.DialogState.Error(
                        result.exception.message ?: Res.string.feature_client_failed_to_load_client.toString(),
                    ),
                )
            }

            is DataState.Success -> updateState {
                val data = result.data.pageItems
                if (data.isEmpty()) {
                    it.copy(isEmpty = true, dialogState = null)
                } else {
                    it.copy(clients = data, dialogState = null, unfilteredClients = data)
                }
            }
        }
    }

    private fun handleClientResult(result: Flow<PagingData<ClientEntity>>) {
        updateState {
            state.copy(
                clientsFlow = result,
                dialogState = null,
                unfilteredClientsFlow = result,
            )
        }
    }

    private fun fetchClientImage(clientId: Int) {
        viewModelScope.launch {
            clientDetailsRepo.getImage(clientId).collect { result ->
                when (result) {
                    is DataState.Error -> {}
                    DataState.Loading -> {}
                    is DataState.Success -> {
                        val imageBytes = imageToByteArray(result.data)
                        updateState { state ->
                            state.copy(
                                clientImages = state.clientImages + (clientId to imageBytes),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleSortClick(sort: SortTypes?) {
        updateState {
            val sortedList = when (sort) {
                SortTypes.NAME -> it.clients.sortedBy { it.displayName?.lowercase() }
                SortTypes.ACCOUNT_NUMBER -> it.clients.sortedBy { it.accountNo }
                SortTypes.EXTERNAL_ID -> it.clients.sortedBy { it.externalId }
                else -> it.clients
            }

            it.copy(
                sort = sort,
                clients = sortedList,
            )
        }
    }

    private fun toggleFilterVisibility() {
        updateState {
            it.copy(
                isFilterVisible = !it.isFilterVisible,
            )
        }
    }

    private fun onUpdateOffice(offices: List<String?>) {
        updateState {
            it.copy(
                officeNames = (offices + it.officeNames).distinct().sortedBy { it },
            )
        }
    }

    private fun handleFilterClick(filter: String, filterType: FilterType) {
        updateState {
            val newSelectedStatus = if (filterType == FilterType.STATUS) {
                if (filter in it.selectedStatus) {
                    it.selectedStatus - filter
                } else {
                    it.selectedStatus + filter
                }
            } else {
                it.selectedStatus
            }
            val newSelectedOffices = if (filterType == FilterType.OFFICE) {
                if (filter in it.selectedOffices) {
                    it.selectedOffices - filter
                } else {
                    it.selectedOffices + filter
                }
            } else {
                it.selectedOffices
            }

            fun keep(client: ClientEntity): Boolean {
                val statusMatch = newSelectedStatus.isEmpty() || client.status?.value in newSelectedStatus
                val officeMatch = newSelectedOffices.isEmpty() || (client.officeName ?: "Null") in newSelectedOffices

                return statusMatch && officeMatch
            }
            val filteredList = it.unfilteredClients.filter { client ->
                keep(client)
            }

            val filteredFlow = it.unfilteredClientsFlow?.map { clients ->
                clients.filter { client ->
                    keep(client)
                }
            }
            it.copy(
                selectedStatus = newSelectedStatus,
                selectedOffices = newSelectedOffices,
                clients = filteredList,
                clientsFlow = filteredFlow,
            )
        }
    }

    private fun clearFilters() {
        updateState {
            it.copy(
                clients = it.unfilteredClients,
                clientsFlow = it.unfilteredClientsFlow,
                sort = null,
                selectedStatus = emptyList(),
                selectedOffices = emptyList(),
            )
        }
    }
}

/**
 * Represents the UI state of the Client List screen.
 */
data class ClientListState(
    val clients: List<ClientEntity>,
    val unfilteredClients: List<ClientEntity>,
    val clientsFlow: Flow<PagingData<ClientEntity>>?,
    val unfilteredClientsFlow: Flow<PagingData<ClientEntity>>? = null,
    val isOnline: Boolean,
    val isEmpty: Boolean = false,
    val isSearchActive: Boolean = false,
    val dialogState: DialogState? = null,
    val searchQuery: String = "",
    val clientImages: Map<Int, ByteArray?> = emptyMap(),
    val sort: SortTypes? = null,
    val selectedStatus: List<String> = emptyList(),
    val isFilterVisible: Boolean = false,
    val officeNames: List<String?> = emptyList(),
    val selectedOffices: List<String> = emptyList(),
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
}

enum class SortTypes(val value: String) {
    NAME("Name"),
    ACCOUNT_NUMBER("Account Number"),
    EXTERNAL_ID("External ID"),
}

enum class FilterType(val value: String) {
    STATUS("Status"),
    OFFICE("Office"),
}

/**
 * UI events for the Client List screen.
 */
sealed interface ClientListEvent {
    data class OnClientClick(val clientId: Int) : ClientListEvent
    data object NavigateToCreateClient : ClientListEvent
}

/**
 * Actions dispatched to the Client List ViewModel.
 */
sealed interface ClientListAction {
    data object RefreshClients : ClientListAction
    data object OnDismissDialog : ClientListAction
    data class OnClientClick(val clientId: Int) : ClientListAction
    data class FetchImage(val clientId: Int) : ClientListAction
    data object ActivateSearch : ClientListAction
    data object DismissSearch : ClientListAction
    data object NavigateToCreateClient : ClientListAction
    data class OnQueryChange(val query: String) : ClientListAction
    data object ToggleFilterVisibility : ClientListAction
    data class HandleFilterClick(val filter: String, val filterType: FilterType) : ClientListAction
    data class HandleSortClick(val sort: SortTypes) : ClientListAction
    data class OnUpdateOffice(val offices: List<String?>) : ClientListAction
    data object ClearFilters : ClientListAction

    sealed class Internal : ClientListAction {
        data class ReceiveClientResult(val result: Flow<PagingData<ClientEntity>>) : Internal()
        data class ReceiveClientResultFromDb(val result: DataState<Page<ClientEntity>>) : Internal()
    }
}
