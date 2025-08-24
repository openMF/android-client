package com.mifos.feature.client.clientTransfer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ClientTransferViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: ClientDetailsRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientTransferState, ClientTransferEvent, ClientTransferAction>(
    initialState = ClientTransferState(),
) {
    private val route = savedStateHandle.toRoute<ClientTransferRoute>()

    init {
        getTransferOptionsAndObserveNetwork(route.id)
    }

    private fun getTransferOptionsAndObserveNetwork(clientId: Int) {
        observeNetwork()
        loadTransferOptions(clientId)
    }

    private fun loadTransferOptions(clientId: Int) {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(dialogState = ClientTransferState.DialogState.Loading) }
            try {
//                val options = repo.getClientTransferOptions(clientId)
//                mutableStateFlow.update { it.copy(transferOptions = options, dialogState = null) }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(dialogState = ClientTransferState.DialogState.Error(e.message ?: "Unknown error"))
                }
            }
        }
    }

    private suspend fun transferClient() {
        mutableStateFlow.update { it.copy(dialogState = ClientTransferState.DialogState.Loading) }

//        val result = repo.transferClient(
//            clientId = route.id,
//            officeId = state.transferOptions[state.currentSelectedIndex].id,
//            note = state.note,
//        )
//
//        when (result) {
//            is DataState.Success -> {
//                mutableStateFlow.update {
//                    it.copy(dialogState = ClientTransferState.DialogState.ShowStatusDialog(ResultStatus.SUCCESS))
//                }
//            }
//            is DataState.Error -> {
//                mutableStateFlow.update {
//                    it.copy(dialogState = ClientTransferState.DialogState.ShowStatusDialog(ResultStatus.FAILURE, result.message))
//                }
//            }
//        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
            }
        }
    }

    override fun handleAction(action: ClientTransferAction) {
        when (action) {
            ClientTransferAction.NavigateBack -> sendEvent(ClientTransferEvent.NavigateBack)
            ClientTransferAction.OnRetry -> getTransferOptionsAndObserveNetwork(route.id)
            ClientTransferAction.OnNext -> sendEvent(ClientTransferEvent.NavigateNext)
            is ClientTransferAction.OptionChanged -> {
                mutableStateFlow.update { it.copy(currentSelectedIndex = action.index) }
            }
            is ClientTransferAction.NoteChanged -> {
                mutableStateFlow.update { it.copy(note = action.note) }
            }
            ClientTransferAction.OnSubmit -> {
                viewModelScope.launch { transferClient() }
            }
        }
    }
}

data class ClientTransferState(
    val transferOptions: List<String> = emptyList(),
    val currentSelectedIndex: Int = 0,
    val note: String = "",
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data class ShowStatusDialog(val status: ResultStatus, val msg: String = "") : DialogState
    }
}

sealed interface ClientTransferEvent {
    data object NavigateBack : ClientTransferEvent
    data object NavigateNext : ClientTransferEvent
}

sealed interface ClientTransferAction {
    data object NavigateBack : ClientTransferAction
    data object OnRetry : ClientTransferAction
    data object OnNext : ClientTransferAction
    data class OptionChanged(val index: Int) : ClientTransferAction
    data class NoteChanged(val note: String) : ClientTransferAction
    data object OnSubmit : ClientTransferAction
}
