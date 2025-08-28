package com.mifos.feature.client.clientGeneral

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.clientProfile.ClientProfileState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


internal class ClientProfileGeneralViewmodel(
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
): BaseViewModel<ClientProfileGeneralState, ClientProfileGeneralEvent, ClientProfileGeneralAction>(
    initialState = ClientProfileGeneralState()
){
    override fun handleAction(action: ClientProfileGeneralAction) {
        when (action) {
            ClientProfileGeneralAction.NavigateBack -> sendEvent(ClientProfileGeneralEvent.NavigateBack)
            is ClientProfileGeneralAction.OnActionClick ->
            sendEvent(ClientProfileGeneralEvent.OnActionClick(action.action))
            ClientProfileGeneralAction.OnRetry -> null
        }
    }


    /**
     * Observes the network connectivity status and updates state accordingly.
     */
    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update {
                    it.copy(networkConnection = isConnected)
                }
            }
        }
    }

}

data class ClientProfileGeneralState(
    val dialogState: ClientProfileState.DialogState? = null,
    val networkConnection: Boolean = false,
) {

    sealed interface DialogState {
        data class Error(val message: String) : ClientProfileGeneralState.DialogState
        data object Loading : ClientProfileGeneralState.DialogState
    }

}


sealed interface ClientProfileGeneralEvent {
    /** Navigates back to the previous screen */
    data object NavigateBack : ClientProfileGeneralEvent

    /** Triggered when an action item is clicked */
    data class OnActionClick(val action: ClientProfileGeneralActionItem) : ClientProfileGeneralEvent
}

sealed interface ClientProfileGeneralAction {
    /** Navigate back from the screen */
    data object NavigateBack : ClientProfileGeneralAction

    /** User clicks on an action item */
    data class OnActionClick(val action: ClientProfileGeneralActionItem) : ClientProfileGeneralAction

    /** User clicks on Retry */
    data object OnRetry : ClientProfileGeneralAction
}
