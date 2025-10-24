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
import com.mifos.core.common.utils.Constants.LOCALE_EN
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientChargeRepository
import com.mifos.core.domain.useCases.CreateChargesUseCase
import com.mifos.core.domain.useCases.GetAllChargesV2UseCase
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.template.client.ChargeTemplate
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

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
            ClientChargesAction.CloseShowChargesDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        showCharges = false
                    )
                }
            }
            ClientChargesAction.CloseAddChargesDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        showAddCharges = false,
                        chargeId = -1,
                        amount = null,
                        chargeTitle = null,
                        chargeNameTouched = false,
                        amountTouched = false,
                        chargeTitleTouched = false,
                        dueDate = null,
                        collectedOn = null,

                    )
                }
            }
            ClientChargesAction.ShowCharges ->{
                mutableStateFlow.update {
                    it.copy(
                        showCharges = true,
                    )
                }
            }
            is ClientChargesAction.AddCharge ->{
                mutableStateFlow.update {
                    it.copy(
                        showAddCharges = true,
                        showCharges = false
                    )
                }
                loadChargeTemplate()
            }

            is ClientChargesAction.OnChargeNameChange -> {
                mutableStateFlow.update {
                    it.copy(
                        chargeName = action.chargeName
                    )
                }
            }
            is ClientChargesAction.OnDueDateChange -> {
                mutableStateFlow.update { it.copy(dueDate = action.dueDate) }
            }
            is ClientChargesAction.OnAmountChange -> {
                mutableStateFlow.update { it.copy(amount = action.amount) }
            }
            is ClientChargesAction.OnChargeIdChange -> {
                mutableStateFlow.update { it.copy(chargeId = action.chargeId) }
            }
            is ClientChargesAction.OnChargeTitleChange -> {
                mutableStateFlow.update { it.copy(chargeTitle = action.chargeTitle) }
            }
            is ClientChargesAction.OnCollectedOnDateChange -> {
                mutableStateFlow.update { it.copy(collectedOn = action.collectedOn) }
            }
            is ClientChargesAction.OnShowAddCharge -> {
                mutableStateFlow.update {
                    it.copy(
                        showAddCharges = true,
                        showCharges = false
                    )
                }
            }

            ClientChargesAction.OnAmountTouched -> {
                mutableStateFlow.update {
                    it.copy(
                        amountTouched = true
                    )
                }
            }
            ClientChargesAction.OnChargeNameTouched -> {
                mutableStateFlow.update {
                    it.copy(
                        chargeNameTouched = true
                    )
                }
            }
            ClientChargesAction.OnChargeTitleTouched -> {
                mutableStateFlow.update {
                    it.copy(
                        chargeTitleTouched = true
                    )
                }
            }
            is ClientChargesAction.OnCollectedOnDatePick -> {
                mutableStateFlow.update { it.copy(
                    showCollectedOnDatePicker = action.collectedOnPick
                ) }
            }
            is ClientChargesAction.OnDueDatePick -> {
                mutableStateFlow.update { it.copy(
                    showDueDatePicker = action.dueDatePick
                ) }
            }
        }
    }

    private fun loadCharges() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(isLoading = true, error = null) }

            try {
                val response = mutableStateFlow.value.clientId?.let { repository.getClientCharges(it) }
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
                    chargeDialogState = ChargeDialogState.Loading
                )
            }

            mutableStateFlow.value.clientId?.let { getChargeTemplateUseCase(it) }?.collect { result ->
                when (result) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                chargeDialogState = ChargeDialogState.Error(
                                    message = Res.string.feature_client_failed_to_load_client_charges
                                )
                            )
                        }
                    }

                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(chargeDialogState = ChargeDialogState.Loading)
                        }
                    }

                    is DataState.Success -> {
                        val template = result.data
                        val firstOption = template.chargeOptions.firstOrNull()
                        mutableStateFlow.update {
                            it.copy(
                                chargeDialogState = ChargeDialogState.AllChargesV2(
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
                it.copy(dialogState = ClientChargesState.DialogState.Loading)
            }

            mutableStateFlow.value.clientId?.let { createChargesUseCase(it, payload) }
                ?.collect { result ->
                when (result) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientChargesState.DialogState.ShowStatusDialog(
                                    ResultStatus.FAILURE,
                                    message = result.message
                                ),
                            )
                        }
                    }

                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = ClientChargesState.DialogState.Loading)
                        }
                    }

                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ClientChargesState.DialogState.ShowStatusDialog(
                                    ResultStatus.SUCCESS,
                                )
                            )
                        }
                        // Refresh the charges list after successful creation
                        loadCharges()
                    }
                }
            }
        }
    }

    sealed class ChargeDialogState {
        data object Loading : ChargeDialogState()
        data class Error(val message: StringResource) : ChargeDialogState()
        data class AllChargesV2(
            val chargeTemplate: ChargeTemplate,
            val selectedChargeName: String,
            val selectedChargeId: Int,
        ) : ChargeDialogState()
//        data object Success : ChargeDialogState()
    }
}

data class ClientChargesState(
    val clientId: Int?=null,
    val chargesFlow: Any? = null,
    val addedCharges: List<ChargesPayload> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val chargeDialogState: ClientChargesViewModel.ChargeDialogState = ClientChargesViewModel.ChargeDialogState.Loading,
    val showSuccessSnackbar: Boolean = false,
    val showAddCharges: Boolean = false,
    val showCharges: Boolean = false,
    val dialogState: DialogState?=null,

    val showDueDatePicker : Boolean = false,
    val showCollectedOnDatePicker : Boolean = false,
    val chargeTitle: String? = null,
    val chargeTitleTouched: Boolean = false,

    val amount: String?=null,
    val amountTouched: Boolean = false,

    val chargeName: String? = "",
    val chargeNameTouched: Boolean = false,

    val dueDate: Long? = null,
    val collectedOn: Long? = null,

    val chargeId: Int? = -1,

    val locale: String = LOCALE_EN
){
    sealed interface DialogState {
        data object Loading : DialogState
        data object Error : DialogState
        data class ShowStatusDialog(val status: ResultStatus, val message: String = "") : DialogState
    }
}

sealed interface ClientChargesEvent {
    data object NavigateBack : ClientChargesEvent
}

sealed interface ClientChargesAction {
    data object NavigateBack : ClientChargesAction
    data object Refresh : ClientChargesAction
    data object LoadChargeTemplate : ClientChargesAction
    data object OnRetry : ClientChargesAction
    data class CreateCharge(val payload: ChargesPayload) : ClientChargesAction
    data object CloseShowChargesDialog : ClientChargesAction
    data object CloseAddChargesDialog : ClientChargesAction
    data object ShowCharges : ClientChargesAction
    data object AddCharge : ClientChargesAction
    data class OnChargeTitleChange(val chargeTitle:String) : ClientChargesAction
    data class OnChargeNameChange(val chargeName:String) : ClientChargesAction
    data class OnCollectedOnDateChange(val collectedOn:Long) : ClientChargesAction
    data class OnDueDateChange(val dueDate: Long?) : ClientChargesAction
    data class OnCollectedOnDatePick(val collectedOnPick:Boolean) : ClientChargesAction
    data class OnDueDatePick(val dueDatePick: Boolean) : ClientChargesAction
    data object OnShowAddCharge : ClientChargesAction
    data class OnChargeIdChange(val chargeId: Int) : ClientChargesAction
    data class OnAmountChange(val amount:String) : ClientChargesAction
    data object OnAmountTouched : ClientChargesAction
    data object OnChargeNameTouched : ClientChargesAction
    data object OnChargeTitleTouched : ClientChargesAction
}
