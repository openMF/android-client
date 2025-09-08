/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientAddress

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_address_template
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_address
import androidclient.feature.client.generated.resources.feature_client_unable_to_create_address_for_client
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.repository.CreateNewClientRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.clients.ClientAddressEntity
import com.mifos.core.network.model.PostClientAddressRequest
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.client.AddressTemplate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal class ClientAddressViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: CreateNewClientRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientAddressState, ClientAddressEvent, ClientAddressAction>(
    initialState = ClientAddressState(),
) {
    val route = savedStateHandle.toRoute<ClientAddressRoute>()

    init {
        observeNetwork()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
                if (isConnected) {
                    loadClientAddress()
                    loadAddressTemplate()
                } else {
                    mutableStateFlow.update {
                        it.copy(
                            addressListScreenState = ClientAddressState.AddressListScreenState.NetworkError,
                        )
                    }
                }
            }
        }
    }

    fun loadClientAddress() {
        viewModelScope.launch {
            try {
                mutableStateFlow.update {
                    it.copy(
                        id = route.id,
                        addressListScreenState = ClientAddressState.AddressListScreenState.Loading,
                    )
                }
                val addressList = repository.getAddresses(clientId = route.id)
                mutableStateFlow.update {
                    it.copy(
                        address = addressList,
                        addressListScreenState = ClientAddressState.AddressListScreenState.ShowAddressList,
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientAddressState.DialogState.Error(
                            getString(Res.string.feature_client_failed_to_load_address),
                        ),
                    )
                }
            }
        }
    }

    fun loadAddressTemplate() {
        viewModelScope.launch {
            try {
                mutableStateFlow.update {
                    it.copy(
                        addressFormScreenState = ClientAddressState.AddressFormScreenState.Loading,
                    )
                }
                val template = repository.getAddressTemplate()
                mutableStateFlow.update {
                    it.copy(
                        addressTemplate = template,
                        addressFormScreenState = ClientAddressState.AddressFormScreenState.ShowAddressForm,
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ClientAddressState.DialogState.Error(
                            getString(Res.string.feature_client_failed_to_fetch_address_template),
                        ),
                    )
                }
            }
        }
    }

    fun createClientAddress(addressTypeId: Int, addressPayload: PostClientAddressRequest) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    addressFormScreenState = ClientAddressState.AddressFormScreenState.Loading,
                )
            }
            try {
                val response = repository.createClientAddress(
                    clientId = route.id,
                    addressTypeId = addressTypeId,
                    addressRequest = addressPayload,
                )
                if (response.resourceId != null) {
                    mutableStateFlow.update {
                        it.copy(
                            addressFormScreenState = ClientAddressState.AddressFormScreenState.ShowStatusDialog(
                                status = ResultStatus.SUCCESS,
                            ),
                        )
                    }
                } else {
                    mutableStateFlow.update {
                        it.copy(
                            addressFormScreenState = ClientAddressState.AddressFormScreenState.ShowStatusDialog(
                                status = ResultStatus.FAILURE,
                                msg = getString(Res.string.feature_client_unable_to_create_address_for_client),
                            ),
                        )
                    }
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        addressFormScreenState = ClientAddressState.AddressFormScreenState.ShowStatusDialog(
                            status = ResultStatus.FAILURE,
                        ),
                    )
                }
            }
        }
    }

    private fun handleRetry() {
        mutableStateFlow.update {
            it.copy(
                dialogState = null,
                addressTemplate = null,
            )
        }
        observeNetwork()
    }

    override fun handleAction(action: ClientAddressAction) {
        when (action) {
            is ClientAddressAction.NavigateBack -> sendEvent(ClientAddressEvent.NavigateBack)
            is ClientAddressAction.ShowAddressForm -> sendEvent(ClientAddressEvent.ShowAddressForm)
            is ClientAddressAction.OnNext -> sendEvent(ClientAddressEvent.NavigateNext)
            is ClientAddressAction.OnRetry -> handleRetry()
        }
    }
}

data class ClientAddressState(
    val id: Int = -1,
    val networkConnection: Boolean = false,
    val address: List<ClientAddressEntity> = emptyList(),
    val dialogState: DialogState? = null,
    val addressListScreenState: AddressListScreenState = AddressListScreenState.Loading,
    val addressFormScreenState: AddressFormScreenState = AddressFormScreenState.Loading,
    val addressTemplate: AddressTemplate? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
    }

    sealed interface AddressListScreenState {
        data object Loading : AddressListScreenState
        data object ShowAddressList : AddressListScreenState

        data object NetworkError : AddressListScreenState
    }
    sealed interface AddressFormScreenState {
        data object Loading : AddressFormScreenState
        data object ShowAddressForm : AddressFormScreenState
        data class ShowStatusDialog(val status: ResultStatus, val msg: String = "") : AddressFormScreenState
    }
}

sealed interface ClientAddressEvent {
    data object NavigateBack : ClientAddressEvent
    data object ShowAddressForm : ClientAddressEvent
    data object NavigateNext : ClientAddressEvent
}

sealed interface ClientAddressAction {
    data object NavigateBack : ClientAddressAction
    data object OnNext : ClientAddressAction
    data object ShowAddressForm : ClientAddressAction
    data object OnRetry : ClientAddressAction
}
