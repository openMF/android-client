package com.mifos.feature.client.clientUpcomingCharges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_error_not_connected_internet
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.map
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.Page
import com.mifos.core.data.repository.ClientChargeRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.template.loan.Charges
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.clientCharges.ClientChargeUiState
import com.mifos.feature.client.clientUpdateDefaultAccount.UpdateDefaultAccountRoute
import com.mifos.feature.client.clientsList.ClientListAction
import com.mifos.feature.client.clientsList.ClientListState
import com.mifos.room.entities.client.ChargesEntity
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class ClientUpcomingChargesViewmodel(
    private val networkMonitor: NetworkMonitor,
    savedStateHandle: SavedStateHandle,
    private val repository: ClientChargeRepository,
) : BaseViewModel<ClientUpcomingChargesState, ClientUpcomingChargesEvent, ClientUpcomingChargesAction>(
    initialState = ClientUpcomingChargesState(),
) {
    private val route = savedStateHandle.toRoute<ClientUpcomingChargesRoute>()

    override fun handleAction(action: ClientUpcomingChargesAction) {
        when (action) {
            is ClientUpcomingChargesAction.CardClicked -> mutableStateFlow.update {
                it.copy(
                    expandedItemIndex = action.index,
                    isExpanded = !it.isExpanded,
                )
            }

            ClientUpcomingChargesAction.PayOutstandingAmound -> {
                sendEvent(ClientUpcomingChargesEvent.PayOutstandingAmound)
            }

            ClientUpcomingChargesAction.ToggleFilter -> {
                mutableStateFlow.update {
                    it.copy(isFilterOpen = !it.isFilterOpen)
                }
            }

            ClientUpcomingChargesAction.ToggleSearch -> {
                mutableStateFlow.update {
                    it.copy(isSearchBarOpen = !it.isSearchBarOpen)
                }
            }

            ClientUpcomingChargesAction.OnRefresh -> checkNetworkAndGetCharges()

            ClientUpcomingChargesAction.DismissDialog -> {
                mutableStateFlow.update { it.copy(dialogState = null) }
            }
        }
    }

    init {
        checkNetworkAndGetCharges()
    }

    fun checkNetworkAndGetCharges() {
        viewModelScope.launch {
            val isOnline = networkMonitor.isOnline.first()
            when (isOnline) {
                true -> {
                    mutableStateFlow.update { it.copy(dialogState = null) }
                    getClientCharges()
                }

                false -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientUpcomingChargesState.DialogState.Error(
                                getString(Res.string.feature_client_error_not_connected_internet)
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun getClientCharges() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = ClientUpcomingChargesState.DialogState.Loading)
            }

            runCatching {
                repository.getClientCharges(route.clientId)
            }.onSuccess { result ->
                mutableStateFlow.update {
                    it.copy(
                        chargesFlow = result,
                        dialogState = null,
                    )
                }
            }.onFailure { e ->
                mutableStateFlow.update {
                    it.copy(dialogState = ClientUpcomingChargesState.DialogState.Error("An error occured while fetch upcoming client charges, would you liek to retry?"))
                }
            }
        }
    }


}

data class ClientUpcomingChargesState(
    val isFilterOpen: Boolean = false,
    val chargesFlow: Flow<PagingData<ChargesEntity>>? = null,
    val isExpanded: Boolean = false,
    val expandedItemIndex: Int = -1,
    val isSearchBarOpen: Boolean = false,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
}

sealed interface ClientUpcomingChargesEvent {
    data object PayOutstandingAmound : ClientUpcomingChargesEvent
}

sealed interface ClientUpcomingChargesAction {
    data object PayOutstandingAmound : ClientUpcomingChargesAction
    data object DismissDialog : ClientUpcomingChargesAction
    data object ToggleFilter : ClientUpcomingChargesAction
    data object ToggleSearch : ClientUpcomingChargesAction
    data object OnRefresh : ClientUpcomingChargesAction
    data class CardClicked(val index: Int) : ClientUpcomingChargesAction
}