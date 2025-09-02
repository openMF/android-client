package com.mifos.feature.client.clientAddress

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.mifos.core.model.objects.clients.Address
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.clientCollateral.ClientCollateralState
import kotlinx.coroutines.flow.update

internal class ClientAddressViewModel (
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ClientAddressState, ClientAddressEvent, ClientAddressAction>(
    initialState = ClientAddressState()
) {
    val route = savedStateHandle.toRoute<ClientAddressRoute>()

//    init {
//        mutableStateFlow.update {
//            it.copy(
//                dialogState = ClientAddressState.DialogState.Loading
//            )
//        }
//    }

    override fun handleAction(action: ClientAddressAction) {
        when(action) {
            is ClientAddressAction.NavigateBack -> sendEvent(ClientAddressEvent.NavigateBack)
            is ClientAddressAction.ShowAddressForm -> {sendEvent(ClientAddressEvent.ShowAddressForm)}
            is ClientAddressAction.OnNext -> sendEvent(ClientAddressEvent.NavigateNext)
            is ClientAddressAction.ShowAddress -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null
                    )
                }
            }

            is ClientAddressAction.ShowStatusDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientAddressState.DialogState.ShowStatusDialog(status = ResultStatus.SUCCESS)
                    )
                }
            }
        }
    }
}

data class ClientAddressState(
    val id: Int = -1,
    val address: List<Address> = emptyList(),
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data class ShowStatusDialog(val status: ResultStatus, val msg: String = "") : DialogState
    }
}

sealed interface ClientAddressEvent {
    data object NavigateBack: ClientAddressEvent
    data object ShowAddressForm: ClientAddressEvent
    data object NavigateNext: ClientAddressEvent
}

sealed interface ClientAddressAction {
    data object NavigateBack: ClientAddressAction
    data object OnNext: ClientAddressAction
    data object ShowStatusDialog: ClientAddressAction
    data object ShowAddressForm: ClientAddressAction
    data object ShowAddress: ClientAddressAction
}