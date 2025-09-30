/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientCharges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client_charges
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientChargeRepository
import com.mifos.core.domain.useCases.CreateChargesUseCase
import com.mifos.core.domain.useCases.GetAllChargesV2UseCase
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.clientChargeDialog.ChargeDialogUiState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientChargesViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ClientChargeRepository,
    private val getChargeTemplateUseCase: GetAllChargesV2UseCase,
    private val createChargesUseCase: CreateChargesUseCase,
) : BaseViewModel<ClientChargesState, ClientChargesEvent, ClientChargesAction>(
    initialState = ClientChargesState(
        clientId = savedStateHandle.toRoute<ClientChargesRoute>().clientId
    )
) {

    init {
        loadCharges()
    }

    override fun handleAction(action: ClientChargesAction) {
        when (action) {
            ClientChargesAction.NavigateBack -> sendEvent(ClientChargesEvent.NavigateBack)
            ClientChargesAction.Refresh -> refreshChargesList()
            ClientChargesAction.LoadChargeTemplate -> loadChargeTemplate()
            ClientChargesAction.OnRetry -> loadCharges()
            is ClientChargesAction.CreateCharge -> createCharge(action.payload)
            ClientChargesAction.CloseDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        showChargeDialog = false,
                        chargeDialogUiState = ChargeDialogUiState.Loading
                    )
                }
            }
        }
    }

    private fun loadCharges() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(isLoading = true, error = null) }

            try {
                val response = repository.getClientCharges(mutableStateFlow.value.clientId)
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        chargesFlow = response,
                        error = null
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load charges",
                        chargesFlow = null
                    )
                }
            }
        }
    }

    private fun refreshChargesList() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(isRefreshing = true) }
            loadCharges()
            mutableStateFlow.update { it.copy(isRefreshing = false) }
        }
    }

    private fun loadChargeTemplate() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    showChargeDialog = true,
                    chargeDialogUiState = ChargeDialogUiState.Loading
                )
            }

            getChargeTemplateUseCase(mutableStateFlow.value.clientId).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                chargeDialogUiState = ChargeDialogUiState.Error(
                                    message = Res.string.feature_client_failed_to_load_client_charges
                                )
                            )
                        }
                    }

                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(chargeDialogUiState = ChargeDialogUiState.Loading)
                        }
                    }

                    is DataState.Success -> {
                        val template = result.data
                        val firstOption = template.chargeOptions.firstOrNull()
                        mutableStateFlow.update {
                            it.copy(
                                chargeDialogUiState = ChargeDialogUiState.AllChargesV2(
                                    chargeTemplate = template,
                                    selectedChargeName = firstOption?.name.orEmpty(),
                                    selectedChargeId = firstOption?.id ?: -1,
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createCharge(payload: ChargesPayload) {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(chargeDialogUiState = ChargeDialogUiState.Loading)
            }

            createChargesUseCase(mutableStateFlow.value.clientId, payload).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                chargeDialogUiState = ChargeDialogUiState.Error(
                                    message = Res.string.feature_client_failed_to_load_client_charges
                                )
                            )
                        }
                    }

                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(chargeDialogUiState = ChargeDialogUiState.Loading)
                        }
                    }

                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                chargeDialogUiState = ChargeDialogUiState.ChargesCreatedSuccessfully,
                                showSuccessSnackbar = true
                            )
                        }
                        // Refresh the charges list after successful creation
                        loadCharges()
                    }
                }
            }
        }
    }
}

data class ClientChargesState(
    val clientId: Int,
    val chargesFlow: Any? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val showChargeDialog: Boolean = false,
    val chargeDialogUiState: ChargeDialogUiState = ChargeDialogUiState.Loading,
    val showSuccessSnackbar: Boolean = false
)

sealed interface ClientChargesEvent {
    data object NavigateBack : ClientChargesEvent
}

sealed interface ClientChargesAction {
    data object NavigateBack : ClientChargesAction
    data object Refresh : ClientChargesAction
    data object LoadChargeTemplate : ClientChargesAction
    data object OnRetry : ClientChargesAction
    data class CreateCharge(val payload: ChargesPayload) : ClientChargesAction
    data object CloseDialog : ClientChargesAction
}

///*
// * Copyright 2024 Mifos Initiative
// *
// * This Source Code Form is subject to the terms of the Mozilla Public
// * License, v. 2.0. If a copy of the MPL was not distributed with this
// * file, You can obtain one at https://mozilla.org/MPL/2.0/.
// *
// * See https://github.com/openMF/android-client/blob/master/LICENSE.md
// */
//package com.mifos.feature.client.clientCharges
//
//import androidclient.feature.client.generated.resources.Res
//import androidclient.feature.client.generated.resources.feature_client_failed_to_create_charge
//import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client_charges
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.mifos.core.common.utils.Constants
//import com.mifos.core.common.utils.DataState
//import com.mifos.core.data.repository.ClientChargeRepository
//import com.mifos.core.domain.useCases.CreateChargesUseCase
//import com.mifos.core.domain.useCases.GetAllChargesV2UseCase
//import com.mifos.core.model.objects.payloads.ChargesPayload
//import com.mifos.feature.client.clientChargeDialog.ChargeDialogUiState
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class ClientChargesViewModel(
//    private val repository: ClientChargeRepository,
//    private val getChargeTemplateUseCase: GetAllChargesV2UseCase,
//    private val createChargesUseCase: CreateChargesUseCase,
//    savedStateHandle: SavedStateHandle,
//) : ViewModel() {
//
//    val clientId = savedStateHandle.getStateFlow(Constants.CLIENT_ID, 0)
//
//    private val _clientChargesUiState =
//        MutableStateFlow<ClientChargeUiState>(ClientChargeUiState.Loading)
//    val clientChargesUiState = _clientChargesUiState.asStateFlow()
//
//    private val _isRefreshing = MutableStateFlow(false)
//    val isRefreshing = _isRefreshing.asStateFlow()
//
//    private val _chargeDialogUiState =
//        MutableStateFlow<ChargeDialogUiState>(ChargeDialogUiState.Loading)
//    val chargeDialogUiState = _chargeDialogUiState.asStateFlow()
//
//    init {
//        loadCharges()
//    }
//
//    fun refreshChargesList() {
//        _isRefreshing.value = true
//        loadCharges()
//        _isRefreshing.value = false
//    }
//
//    fun loadCharges() = viewModelScope.launch {
//        val response = repository.getClientCharges(clientId.value)
//        _clientChargesUiState.value = ClientChargeUiState.ChargesList(response)
//    }
//
//    fun loadChargeTemplate() = viewModelScope.launch {
//        getChargeTemplateUseCase(clientId.value).collect { result ->
//            when (result) {
//                is DataState.Error ->
//                    _chargeDialogUiState.value =
//                        ChargeDialogUiState.Error(Res.string.feature_client_failed_to_load_client_charges)
//
//                is DataState.Loading ->
//                    _chargeDialogUiState.value = ChargeDialogUiState.Loading
//
//                is DataState.Success -> {
//                    val template = result.data
//                    val firstOption = template.chargeOptions.firstOrNull()
//                    _chargeDialogUiState.value = ChargeDialogUiState.AllChargesV2(
//                        chargeTemplate = template,
//                        selectedChargeName = firstOption?.name.orEmpty(),
//                        selectedChargeId = firstOption?.id ?: -1,
//                    )
//                }
//            }
//        }
//    }
//
//    fun createCharge(payload: ChargesPayload) = viewModelScope.launch {
//        createChargesUseCase(clientId.value, payload).collect { result ->
//            when (result) {
//                is DataState.Error ->
//                    _chargeDialogUiState.value =
//                        ChargeDialogUiState.Error(Res.string.feature_client_failed_to_create_charge)
//
//                is DataState.Loading ->
//                    _chargeDialogUiState.value = ChargeDialogUiState.Loading
//
//                is DataState.Success ->
//                    _chargeDialogUiState.value = ChargeDialogUiState.ChargesCreatedSuccessfully
//            }
//        }
//    }
//}
