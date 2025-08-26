package com.mifos.feature.client.clientCollateral

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.network.model.CollateralItem
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ClientCollateralViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: ClientDetailsRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientCollateralState, ClientCollateralEvent, ClientCollateralAction>(
    initialState = run{
        ClientCollateralState(savedStateHandle.toRoute<ClientCollateralRoute>().clientId)
    },
) {


    init {
        getCollateralsAndObserveNetwork()
    }

    private fun getCollateralsAndObserveNetwork() {
        viewModelScope.launch {
            observeNetwork()
            loadCollaterals()
        }
    }

    private suspend fun loadCollaterals() {
        mutableStateFlow.update { it.copy(dialogState = ClientCollateralState.DialogState.Loading) }
        val result=repo.getCollateralItems()
        when(result){
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientCollateralState.DialogState.Error(
                            result.message,
                        ),
                    )
                }
            }
            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        collaterals = result.data,
                        dialogState = null,
                    )
                }
            }
            else -> Unit
        }
    }

    private suspend fun saveCollateral() {
        mutableStateFlow.update { it.copy(dialogState = ClientCollateralState.DialogState.Loading) }
//        val collateralId = state.collaterals[state.currentSelectedIndex].id
//        val result = repo.saveClientCollateral(route.clientId, collateralId)
//        when (result) {
//            is DataState.Success -> {
//                mutableStateFlow.update {
//                    it.copy(dialogState = ClientCollateralState.DialogState.ShowStatusDialog(ResultStatus.SUCCESS))
//                }
//            }
//            is DataState.Error -> {
//                mutableStateFlow.update {
//                    it.copy(
//                        dialogState = ClientCollateralState.DialogState.ShowStatusDialog(
//                            ResultStatus.FAILURE,
//                            result.message,
//                        ),
//                    )
//                }
//            }
//            else -> Unit
//        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
            }
        }
    }

    override fun handleAction(action: ClientCollateralAction) {
        when (action) {
            ClientCollateralAction.NavigateBack -> sendEvent(ClientCollateralEvent.NavigateBack)
            ClientCollateralAction.OnRetry -> getCollateralsAndObserveNetwork()
            ClientCollateralAction.OnNext -> sendEvent(ClientCollateralEvent.NavigateNext)
            is ClientCollateralAction.OptionChanged -> {
                mutableStateFlow.update { it.copy(currentSelectedIndex = action.index) }
            }
            ClientCollateralAction.OnSave -> {
                viewModelScope.launch { saveCollateral() }
            }
        }
    }
}

data class ClientCollateralState(
    val id:Int=-1,
    val collaterals: List<CollateralItem> = emptyList(),
    val currentSelectedIndex: Int = 0,
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
        data class ShowStatusDialog(val status: ResultStatus, val msg: String = "") : DialogState
    }
}

sealed interface ClientCollateralEvent {
    data object NavigateBack : ClientCollateralEvent
    data object NavigateNext : ClientCollateralEvent
}

sealed interface ClientCollateralAction {
    data object NavigateBack : ClientCollateralAction
    data object OnRetry : ClientCollateralAction
    data object OnNext : ClientCollateralAction
    data class OptionChanged(val index: Int) : ClientCollateralAction
    data object OnSave : ClientCollateralAction
}
