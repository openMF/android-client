/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientIdentifiersList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.DeleteIdentifierUseCase
import com.mifos.core.domain.useCases.GetDocumentsListUseCase
import com.mifos.core.domain.useCases.RemoveDocumentUseCase
import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.clientIdentifiersAddUpdate.Feature
import com.mifos.feature.client.clientIdentifiersList.ClientIdentifiersListEvent.AddNewClientIdentity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientIdentifiersListViewModel(
    private val repository: ClientIdentifiersRepository,
    private val deleteClientIdentifierUseCase: DeleteIdentifierUseCase,
    private val getDocumentListUseCase: GetDocumentsListUseCase,
    private val removeDocumentUseCase: RemoveDocumentUseCase,
    private val networkMonitor: NetworkMonitor,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ClientIdentifiersListState, ClientIdentifiersListEvent, ClientIdentifiersListAction>(
    initialState = ClientIdentifiersListState(),
) {
    private val route = savedStateHandle.toRoute<ClientIdentifiersListRoute>()

    override fun handleAction(action: ClientIdentifiersListAction) {
        when (action) {
            ClientIdentifiersListAction.AddNewClientIdentity -> {
                sendEvent(
                    AddNewClientIdentity(
                        id = route.clientId,
                        feature = Feature.ADD_IDENTIFIER,
                    ),
                )
            }

            is ClientIdentifiersListAction.ToggleShowMenu -> mutableStateFlow.update {
                it.copy(
                    currentExpandedItem = action.index,
                    expandClientIdentity = !it.expandClientIdentity,
                )
            }

            is ClientIdentifiersListAction.UploadAgain -> {
                sendEvent(
                    AddNewClientIdentity(
                        id = route.clientId,
                        feature = Feature.ADD_UPDATE_DOCUMENT,
                        uniqueKeyForHandleDocument = action.uniqueKeyForHandleDocument,
                    ),
                )
            }

            is ClientIdentifiersListAction.ViewDocument -> {
                sendEvent(
                    AddNewClientIdentity(
                        id = route.clientId,
                        feature = Feature.VIEW_DOCUMENT,
                        uniqueKeyForHandleDocument = action.uniqueKeyForHandleDocument,
                    ),
                )
            }

            is ClientIdentifiersListAction.DeleteDocument -> {
                deleteClientIdentity(route.clientId, action.identifier, action.uniqueKeyForHandleDocument)
            }

            ClientIdentifiersListAction.ToggleSearch -> {
                mutableStateFlow.update {
                    it.copy(isSearchBarActive = !it.isSearchBarActive)
                }
            }

            ClientIdentifiersListAction.CloseDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            ClientIdentifiersListAction.Refresh -> checkInternetAndFetchIdentities()
            ClientIdentifiersListAction.NavigateBack -> {
                sendEvent(ClientIdentifiersListEvent.NavigateBack)
            }
        }
    }

    init {
        checkInternetAndFetchIdentities()
    }

    private fun checkInternetAndFetchIdentities() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = ClientIdentifiersListState.DialogState.Loading)
            }
            checkNetworkConnection()
        }
    }

    private suspend fun checkNetworkConnection() {
        networkMonitor.isOnline.collect { status ->
            when (status) {
                true -> getClientListIdentities(route.clientId.toLong())
                false -> {
                    mutableStateFlow.update {
                        it.copy(dialogState = ClientIdentifiersListState.DialogState.NoInternet)
                    }
                }
            }
        }
    }

    private suspend fun deleteDocument(documentId: Int) {
        removeDocumentUseCase(
            Constants.ENTITY_TYPE_CLIENT_IDENTIFIERS,
            route.clientId,
            documentId,
        )
            .collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentifiersListState.DialogState.Error(
                                    dataState.message,
                                ),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentifiersListState.DialogState.Loading,
                                isOverlayLoading = true,
                            )
                        }
                    }

                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = null,
                                isOverlayLoading = false,
                            )
                        }
                    }
                }
            }
    }

    private suspend fun getDocumentId(documentKey: String?) {
        getDocumentListUseCase(Constants.ENTITY_TYPE_CLIENT_IDENTIFIERS, route.clientId)
            .collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentifiersListState.DialogState.Error(
                                    dataState.message,
                                ),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentifiersListState.DialogState.Loading,
                            )
                        }
                    }

                    is DataState.Success -> {
                        val deleteDocument =
                            dataState.data.firstOrNull { it.description == documentKey }?.id

                        if (deleteDocument != null) {
                            deleteDocument(deleteDocument)
                        }
                    }
                }
            }
    }

    private suspend fun getClientListIdentities(clientId: Long) {
        repository.getClientListIdentifiers(clientId).collect { dataState ->
            when (dataState) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientIdentifiersListState.DialogState.Error(
                                (dataState.message),
                            ),
                        )
                    }
                }

                DataState.Loading -> mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientIdentifiersListState.DialogState.Loading,
                    )
                }

                is DataState.Success -> {
                    val sortedList = dataState.data.sortedWith(
                        compareBy(
                            { identifier ->
                                val s = identifier.status?.lowercase() ?: ""
                                if (s.contains("active") && !s.contains("inactive")) 0 else 1
                            },
                            { identifier ->
                                identifier.description?.lowercase() ?: ""
                            },
                        ),
                    )

                    mutableStateFlow.update {
                        it.copy(
                            dialogState = null,
                            clientIdentitiesList = sortedList,
                        )
                    }
                }
            }
        }
    }

    private fun deleteClientIdentity(clientId: Int, identifierId: Int, documentKey: String?) {
        viewModelScope.launch {
            deleteClientIdentifierUseCase.invoke(clientId.toLong(), identifierId.toLong())
                .collect { state ->
                    when (state) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ClientIdentifiersListState.DialogState.Error(
                                        state.message,
                                    ),
                                )
                            }
                        }

                        DataState.Loading -> mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientIdentifiersListState.DialogState.Loading,
                                isOverlayLoading = true,
                            )
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ClientIdentifiersListState.DialogState.DeletedSuccessfully(
                                        identifierId,
                                    ),
                                )
                            }

                            // it call first take document id then delete document
                            getDocumentId(documentKey)

                            getClientListIdentities(route.clientId.toLong())
                        }
                    }
                }
        }
    }
}

data class ClientIdentifiersListState(
    val isSearchBarActive: Boolean = false,
    val clientIdentitiesList: List<Identifier> = emptyList(),
    val currentExpandedItem: Int = -1,
    val expandClientIdentity: Boolean = false,
    val dialogState: DialogState? = null,
    val isOverlayLoading: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data object NoInternet : DialogState
        data class DeletedSuccessfully(val id: Int) : DialogState
    }
}

sealed interface ClientIdentifiersListEvent {
    data object NavigateBack : ClientIdentifiersListEvent
    data class AddNewClientIdentity(
        val id: Int,
        val feature: Feature,
        val uniqueKeyForHandleDocument: String? = null,
    ) :
        ClientIdentifiersListEvent
}

sealed interface ClientIdentifiersListAction {
    data object AddNewClientIdentity : ClientIdentifiersListAction
    data object ToggleSearch : ClientIdentifiersListAction
    data class ToggleShowMenu(val index: Int) : ClientIdentifiersListAction
    data class ViewDocument(val uniqueKeyForHandleDocument: String?) : ClientIdentifiersListAction
    data class DeleteDocument(val identifier: Int, val uniqueKeyForHandleDocument: String?) : ClientIdentifiersListAction
    data class UploadAgain(val uniqueKeyForHandleDocument: String?) : ClientIdentifiersListAction
    data object CloseDialog : ClientIdentifiersListAction
    data object NavigateBack : ClientIdentifiersListAction

    data object Refresh : ClientIdentifiersListAction
}
