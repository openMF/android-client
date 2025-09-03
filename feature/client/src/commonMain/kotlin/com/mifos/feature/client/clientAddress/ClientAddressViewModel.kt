package com.mifos.feature.client.clientAddress

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_address_template
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_address
import androidclient.feature.client.generated.resources.feature_client_unable_to_create_address_for_client
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.model.objects.clients.ClientAddressEntity
import com.mifos.core.network.model.PostClientAddressRequest
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.client.AddressTemplate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class ClientAddressViewModel (
    savedStateHandle: SavedStateHandle,
    private val repository: CreateNewClientRepository,
) : BaseViewModel<ClientAddressState, ClientAddressEvent, ClientAddressAction>(
    initialState = ClientAddressState()
) {
    val route = savedStateHandle.toRoute<ClientAddressRoute>()

    suspend fun loadClientAddress() {
        try {
            mutableStateFlow.update {
                it.copy(
                    id = route.id,
                    dialogState = ClientAddressState.DialogState.Loading
                )
            }
            val addressList = repository.getAddresses(clientId = route.id)
            mutableStateFlow.update {
                it.copy(
                    address = addressList,
                    dialogState = null
                )
            }
        } catch (e: Exception) {
            mutableStateFlow.update {
                it.copy(
                    dialogState = ClientAddressState.DialogState.Error(
                        getString(Res.string.feature_client_failed_to_load_address)
                    )
                )
            }
        }
    }

    fun loadAddressTemplate() {
        viewModelScope.launch {
            try {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientAddressState.DialogState.Loading
                    )
                }
                val template = repository.getAddressTemplate()
                mutableStateFlow.update {
                    it.copy(
                        addressTemplate = template,
                        dialogState = null
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientAddressState.DialogState.Error(
                            getString(Res.string.feature_client_failed_to_fetch_address_template)
                        )
                    )
                }
            }
        }
    }

    fun createClientAddress(addressTypeId: Int, addressPayload: PostClientAddressRequest) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    dialogState = ClientAddressState.DialogState.Loading
                )
            }
            try {
                val response = repository.createClientAddress(
                    clientId = route.id,
                    addressTypeId = addressTypeId,
                    addressRequest = addressPayload,
                )
                if(response.resourceId != null) {
                    trySendAction(ClientAddressAction.ShowStatusDialog)
                } else {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ClientAddressState.DialogState.Error(
                                getString(Res.string.feature_client_unable_to_create_address_for_client)
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientAddressState.DialogState.Error(
                            getString(Res.string.feature_client_unable_to_create_address_for_client)
                        )
                    )
                }
            }
        }
    }

    override fun handleAction(action: ClientAddressAction) {
        when(action) {
            is ClientAddressAction.NavigateBack -> sendEvent(ClientAddressEvent.NavigateBack)
            is ClientAddressAction.ShowAddressForm -> sendEvent(ClientAddressEvent.ShowAddressForm)
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
                        dialogState = ClientAddressState.DialogState.ShowStatusDialog(
                            status = ResultStatus.SUCCESS
                        )
                    )
                }
            }

            ClientAddressAction.onRetry -> {
                loadAddressTemplate()
            }
        }
    }
}

data class ClientAddressState(
    val id: Int = -1,
    val address: List<ClientAddressEntity> = emptyList(),
    val dialogState: DialogState? = null,
    val addressTemplate: AddressTemplate? = null,
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
    data object onRetry: ClientAddressAction
}