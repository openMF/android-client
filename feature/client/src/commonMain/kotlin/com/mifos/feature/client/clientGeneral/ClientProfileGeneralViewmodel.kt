package com.mifos.feature.client.clientGeneral

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.GetClientDetailsUseCase
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


internal class ClientProfileGeneralViewmodel(
    savedStateHandle: SavedStateHandle,
    private val getClientDetailsUseCase: GetClientDetailsUseCase,
    private val networkMonitor: NetworkMonitor,
): BaseViewModel<ClientProfileGeneralState, ClientProfileGeneralEvent, ClientProfileGeneralAction>(
    initialState = ClientProfileGeneralState()
){
    private val route = savedStateHandle.toRoute<ClientProfileGeneralRoute>()

    init {
        getClientAndObserveNetwork()
    }

    override fun handleAction(action: ClientProfileGeneralAction) {
        when (action) {
            ClientProfileGeneralAction.NavigateBack -> sendEvent(ClientProfileGeneralEvent.NavigateBack)
            is ClientProfileGeneralAction.OnActionClick ->
            sendEvent(ClientProfileGeneralEvent.OnActionClick(action.action))
            ClientProfileGeneralAction.OnRetry -> null
        }
    }

    private fun getClientAndObserveNetwork() {
        observeNetwork()
        loadClientDetails(route.id)
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

    /**
     * Fetches both client details and profile image.
     *
     * @param clientId ID of the client whose details need to be fetched.
     */
    private fun loadClientDetails(clientId: Int) {
        // Fetch client details
        viewModelScope.launch {
            getClientDetailsUseCase(clientId).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                client = result.data.client,
                                dialogState = null,
                            )
                        }
                    }

                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientProfileGeneralState.DialogState.Error(result.message),
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientProfileGeneralState.DialogState.Loading,
                            )
                        }
                    }
                }
            }
        }
    }


}

data class ClientProfileGeneralState(
    val client: ClientEntity? = null,
    val dialogState: ClientProfileGeneralState.DialogState? = null,
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
