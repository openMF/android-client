/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
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
import com.mifos.core.ui.util.TextFieldsValidator
import com.mifos.feature.client.clientCollateral.ClientCollateralState.DialogState.ShowStatusDialog
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

internal class ClientCollateralViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: ClientDetailsRepository,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ClientCollateralState, ClientCollateralEvent, ClientCollateralAction>(
    initialState = run {
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
        when (val result = repo.getCollateralItems()) {
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
        mutableStateFlow.update {
            it.copy(
                isOverlayLoading = true,
            )
        }
        val collateralId = state.collaterals[state.currentSelectedIndex].id
        val result = repo.createCollateral(
            state.id,
            collateralId = collateralId,
            quantity = state.quantity,
        )
        when (result) {
            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ShowStatusDialog(ResultStatus.SUCCESS),
                        isOverlayLoading = false,
                    )
                }
            }
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ShowStatusDialog(
                            ResultStatus.FAILURE,
                            result.message,
                        ),
                        isOverlayLoading = false,
                    )
                }
            }
            else -> Unit
        }
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
                val quantityError = TextFieldsValidator.numberValidator(state.quantity)
                if (quantityError != null) {
                    mutableStateFlow.update {
                        it.copy(
                            quantityError = quantityError,
                        )
                    }
                } else {
                    viewModelScope.launch { saveCollateral() }
                }
            }

            is ClientCollateralAction.OnQuantityChange -> {
                mutableStateFlow.update { state ->
                    val currentCollateral = state.collaterals[state.currentSelectedIndex]

                    val total = currentCollateral.basePrice * action.quantity.toInt()
                    val totalCollateral = (total * currentCollateral.pctToBase) / 100

                    state.copy(
                        quantity = action.quantity,
                        quantityError = null,
                        total = if (action.quantity.isEmpty())0.0 else total,
                        totalCollateral = if (action.quantity.isEmpty())0.0 else totalCollateral,
                    )
                }
            }
        }
    }
}

data class ClientCollateralState(
    val id: Int = -1,
    val collaterals: List<CollateralItem> = emptyList(),
    val quantity: String = "",
    val quantityError: StringResource? = null,
    val total: Double = 0.0,
    val totalCollateral: Double = 0.0,
    val currentSelectedIndex: Int = 0,
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
    val isOverlayLoading: Boolean = false,
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
    data class OnQuantityChange(val quantity: String) : ClientCollateralAction
}
