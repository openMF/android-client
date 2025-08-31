/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientIdentitiesList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.DeleteIdentifierUseCase
import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.clientIdentitiesList.ClientIdentitiesListEvent.AddNewClientIdentity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientIdentitiesListViewModel(
    private val repository: ClientIdentifiersRepository,
    private val deleteClientIdentifierUseCase: DeleteIdentifierUseCase,
    private val networkMonitor: NetworkMonitor,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ClientIdentitiesListState, ClientIdentitiesListEvent, ClientIdentitiesListAction>(
    initialState = ClientIdentitiesListState(),
) {
    private val route = savedStateHandle.toRoute<ClientIdentitiesListRoute>()

    override fun handleAction(action: ClientIdentitiesListAction) {
        when (action) {
            ClientIdentitiesListAction.AddNewClientIdentity -> sendEvent(
                AddNewClientIdentity(
                    route.clientId,
                ),
            )

            is ClientIdentitiesListAction.ToggleShowMenu -> mutableStateFlow.update {
                it.copy(
                    currentExpandedItem = action.index,
                    expandClientIdentity = !it.expandClientIdentity,
                )
            }

            ClientIdentitiesListAction.UploadAgain -> {
            }

            ClientIdentitiesListAction.ViewDocument -> sendEvent(ClientIdentitiesListEvent.ViewDocument)

            is ClientIdentitiesListAction.DeleteDocument -> {
                deleteClientIdentity(route.clientId, action.identifier)
            }

            ClientIdentitiesListAction.ToggleSearch -> {
                mutableStateFlow.update {
                    it.copy(isSearchBarActive = !it.isSearchBarActive)
                }
            }

            ClientIdentitiesListAction.CloseDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            ClientIdentitiesListAction.Refresh -> checkInternetAndFetchIdentities()
        }
    }

    init {
        checkInternetAndFetchIdentities()
    }

    private fun checkInternetAndFetchIdentities() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = ClientIdentitiesListState.DialogState.Loading)
            }
            checkNetworkConnection()
        }
    }

    private suspend fun checkNetworkConnection() {
        networkMonitor.isOnline.collect { status ->
            when (status) {
                true -> getClientIdentities(route.clientId)
                false -> {
                    mutableStateFlow.update {
                        it.copy(dialogState = ClientIdentitiesListState.DialogState.NoInternet)
                    }
                }
            }
        }
    }

    private suspend fun getClientIdentities(clientId: Int) {
        repository.getClientIdentifiers(clientId).collect { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientIdentitiesListState.DialogState.Error(
                                dataState.message ?: "An unknown error occured",
                            ),
                        )
                    }
                }

                DataState.Loading -> mutableStateFlow.update {
                    it.copy(dialogState = ClientIdentitiesListState.DialogState.Loading)
                }

                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = null,
                            clientIdentitiesList = dataState.data,
                        )
                    }
                }
            }
        }
    }

    private fun deleteClientIdentity(clientId: Int, identifierId: Int) {
        viewModelScope.launch {
            deleteClientIdentifierUseCase.invoke(clientId, identifierId).collect { state ->
                when (state) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentitiesListState.DialogState.Error(
                                    state.message ?: "An unknown error occured",
                                ),
                            )
                        }
                    }

                    DataState.Loading -> mutableStateFlow.update {
                        it.copy(dialogState = ClientIdentitiesListState.DialogState.Loading)
                    }

                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentitiesListState.DialogState.DeletedSuccessfully(
                                    identifierId,
                                ),
                            )
                        }
                        getClientIdentities(route.clientId)
                    }
                }
            }
        }
    }
}

data class ClientIdentitiesListState(
    val isSearchBarActive: Boolean = false,
    val clientIdentitiesList: List<Identifier> = emptyList(),
    val currentExpandedItem: Int = -1,
    val expandClientIdentity: Boolean = false,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data object NoInternet : DialogState
        data class DeletedSuccessfully(val id: Int) : DialogState
    }
}

sealed interface ClientIdentitiesListEvent {
    data object ViewDocument : ClientIdentitiesListEvent
    data class AddNewClientIdentity(val id: Int) : ClientIdentitiesListEvent
}

sealed interface ClientIdentitiesListAction {
    data object AddNewClientIdentity : ClientIdentitiesListAction
    data object ToggleSearch : ClientIdentitiesListAction
    data class ToggleShowMenu(val index: Int) : ClientIdentitiesListAction
    data object ViewDocument : ClientIdentitiesListAction
    data class DeleteDocument(val identifier: Int) : ClientIdentitiesListAction
    data object UploadAgain : ClientIdentitiesListAction
    data object CloseDialog : ClientIdentitiesListAction
    data object Refresh : ClientIdentitiesListAction
}
