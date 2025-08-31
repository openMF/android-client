package com.mifos.feature.client.clientIdentitiesList

import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientIdentifiersRepository
import com.mifos.core.data.repository.ClientListRepository
import com.mifos.core.domain.useCases.CreateClientIdentifierUseCase
import com.mifos.core.domain.useCases.DeleteIdentifierUseCase
import com.mifos.core.model.objects.noncoreobjects.Identifier
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.clientIdentitiesList.ClientIdentitiesListEvent.AddNewClientIdentity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientIdentitiesListViewModel(
    private val repository: ClientIdentifiersRepository,
    private val deleteClientIdentifierUseCase: DeleteIdentifierUseCase,
    private val createClientIdentifierUseCase: CreateClientIdentifierUseCase,
) : BaseViewModel<ClientIdentitiesListState, ClientIdentitiesListEvent, ClientIdentitiesListAction>(
    initialState = ClientIdentitiesListState(),
) {
    override fun handleAction(action: ClientIdentitiesListAction) {
        when (action) {
            ClientIdentitiesListAction.AddNewClientIdentity -> sendEvent(
                AddNewClientIdentity(
                    state.id,
                ),
            )

            ClientIdentitiesListAction.ExpandClientIdentity -> mutableStateFlow.update {
                it.copy(expandClientIdentity = it.expandClientIdentity)
            }

            ClientIdentitiesListAction.UploadAgain -> {

            }

            ClientIdentitiesListAction.ViewDocument -> sendEvent(ClientIdentitiesListEvent.ViewDocument)

            is ClientIdentitiesListAction.DeleteDocument -> {
                deleteClientIdentity(state.id, action.identifier)
            }
        }
    }

    init {
        getClientIdentities(state.id)
    }

    private fun getClientIdentities(clientId: Int) {
        viewModelScope.launch {
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

                    is DataState.Success -> mutableStateFlow.update {
                        it.copy(dialogState = ClientIdentitiesListState.DialogState.DeletedSuccessfully)
                    }
                }
            }
        }
    }
}

data class ClientIdentitiesListState(
    val id: Int = -1,
    val clientIdentitiesList: List<Identifier> = emptyList(),
    val currentExpandedItem: Int? = null,
    val expandClientIdentity: Boolean = false,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data object DeletedSuccessfully : DialogState
    }
}

sealed interface ClientIdentitiesListEvent {
    data object ViewDocument : ClientIdentitiesListEvent
    data object DeleteDocument : ClientIdentitiesListEvent
    data class AddNewClientIdentity(val id: Int) : ClientIdentitiesListEvent
}

sealed interface ClientIdentitiesListAction {
    data object AddNewClientIdentity : ClientIdentitiesListAction
    data object ExpandClientIdentity : ClientIdentitiesListAction
    data object ViewDocument : ClientIdentitiesListAction
    data class DeleteDocument(val identifier: Int) : ClientIdentitiesListAction
    data object UploadAgain : ClientIdentitiesListAction
}