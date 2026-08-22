/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientIdentifiersList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.DeleteIdentifierUseCase
import com.mifos.core.domain.useCases.GetDocumentsListUseCase
import com.mifos.core.domain.useCases.RemoveDocumentUseCase
import com.mifos.core.model.objects.noncoreobjects.Identifier
import kpt.core.base.ui.viewmodel.BaseViewModel
import com.mifos.feature.client.clientIdentifiersAddUpdate.Feature
import com.mifos.feature.client.clientIdentifiersList.ClientIdentifiersListEvent.AddNewClientIdentity
import kotlinx.coroutines.flow.catch
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

            is ClientIdentifiersListAction.ShowDeleteConfirmation -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientIdentifiersListState.DialogState.DeleteConfirmation(
                            id = action.identifier,
                            uniqueKey = action.uniqueKeyForHandleDocument,
                        ),
                    )
                }
            }

            ClientIdentifiersListAction.ToggleSearch -> {
                sendEvent(ClientIdentifiersListEvent.NavigateToSearch)
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
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientIdentifiersListState.DialogState.Loading,
                isOverlayLoading = true,
            )
        }
        removeDocumentUseCase(
            Constants.ENTITY_TYPE_CLIENT_IDENTIFIERS,
            route.clientId,
            documentId,
        )
            .catch { error ->
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientIdentifiersListState.DialogState.Error(
                            error.message ?: "",
                        ),
                    )
                }
            }
            .collect { _ ->
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                        isOverlayLoading = false,
                    )
                }
            }
    }

    private suspend fun getDocumentId(documentKey: String?) {
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientIdentifiersListState.DialogState.Loading,
            )
        }
        getDocumentListUseCase(Constants.ENTITY_TYPE_CLIENT_IDENTIFIERS, route.clientId)
            .catch { error ->
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientIdentifiersListState.DialogState.Error(
                            error.message ?: "",
                        ),
                    )
                }
            }
            .collect { documents ->
                val deleteDocument =
                    documents.firstOrNull { it.description == documentKey }?.id

                if (deleteDocument != null) {
                    deleteDocument(deleteDocument)
                }
            }
    }

    private suspend fun getClientListIdentities(clientId: Long) {
        mutableStateFlow.update {
            it.copy(
                dialogState = ClientIdentifiersListState.DialogState.Loading,
            )
        }
        repository.getClientListIdentifiers(clientId)
            .catch { error ->
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientIdentifiersListState.DialogState.Error(
                            (error.message ?: ""),
                        ),
                    )
                }
            }
            .collect { identifiers ->
                val sortedList = identifiers.sortedWith(
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

    private fun deleteClientIdentity(clientId: Int, identifierId: Int, documentKey: String?) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    dialogState = ClientIdentifiersListState.DialogState.Loading,
                    isOverlayLoading = true,
                )
            }
            deleteClientIdentifierUseCase.invoke(clientId.toLong(), identifierId.toLong())
                .catch { error ->
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientIdentifiersListState.DialogState.Error(
                                error.message ?: "",
                            ),
                        )
                    }
                }
                .collect { _ ->
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
        data class DeleteConfirmation(val id: Int, val uniqueKey: String?) : DialogState
    }
}

sealed interface ClientIdentifiersListEvent {
    data object NavigateBack : ClientIdentifiersListEvent
    data object NavigateToSearch : ClientIdentifiersListEvent
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
    data class ShowDeleteConfirmation(val identifier: Int, val uniqueKeyForHandleDocument: String?) : ClientIdentifiersListAction
    data class UploadAgain(val uniqueKeyForHandleDocument: String?) : ClientIdentifiersListAction
    data object CloseDialog : ClientIdentifiersListAction
    data object NavigateBack : ClientIdentifiersListAction
    data object Refresh : ClientIdentifiersListAction
}
