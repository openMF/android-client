/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.charges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.charges_client_update_not_support_error_msg
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Constants.LOCALE_EN
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.ChargeRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.CreateChargesUseCase
import com.mifos.core.domain.useCases.GetChargesTemplateUseCase
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.template.client.ChargeTemplate
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.TextFieldsValidator
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ChargesViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ChargeRepository,
    private val getChargeTemplateUseCase: GetChargesTemplateUseCase,
    private val createChargesUseCase: CreateChargesUseCase,
    private val networkMonitor: NetworkMonitor,
) : BaseViewModel<ChargesState, ChargesEvent, ChargesAction>(
    initialState = ChargesState(),
) {

    val route = savedStateHandle.toRoute<ChargesRoute>()

    init {
        observeNetwork()
    }

    @OptIn(ExperimentalTime::class)
    override fun handleAction(action: ChargesAction) {
        when (action) {
            ChargesAction.NavigateBack -> sendEvent(ChargesEvent.NavigateBack)
            ChargesAction.OnRetry -> observeNetwork()
            is ChargesAction.CreateCharge -> {
                val amountValidation = TextFieldsValidator.doubleNumberValidator(state.amount ?: "")

                val chargeOptionError =
                    TextFieldsValidator.stringValidator(if (state.chargeOptionIndex == null) "" else state.chargeOptionIndex.toString())

                if (amountValidation != null || chargeOptionError != null) {
                    mutableStateFlow.update {
                        it.copy(
                            amountValidationError = amountValidation,
                            chargeOptionError = chargeOptionError,
                        )
                    }
                } else {
                    val payload = ChargesPayload(
                        chargeId = state.chargeOptionId,
                        amount = state.amount,
                        dueDate = state.dueDate,
                        locale = LOCALE_EN,
                        dateFormat = DateHelper.SHORT_MONTH,
                    )

                    viewModelScope.launch {
                        if (state.isUpdate) {
                            updateCharge(payload, state.chargeId)
                        } else {
                            createCharge(payload)
                        }
                    }
                }
            }

            ChargesAction.CloseChargeAddFields -> {
                if (state.isUpdate) {
                    mutableStateFlow.update {
                        it.copy(
                            chargeOptionIndex = null,
                            amount = null,
                            isUpdate = false,
                            dueDate = DateHelper.getDateAsStringFromLong(
                                Clock.System.now().toEpochMilliseconds(),
                            ),
                        )
                    }
                } else {
                    mutableStateFlow.update {
                        it.copy(
                            chargeOptionIndex = null,
                            showChargeAddFields = false,
                            amount = null,
                        )
                    }
                }
            }

            is ChargesAction.OnChargeOptionChange -> {
                mutableStateFlow.update {
                    it.copy(
                        chargeOptionIndex = action.index,
                        chargeOptionId = state.chargeTemplate?.chargeOptions[action.index]?.id,
                        chargeOptionError = null,
                    )
                }
            }

            is ChargesAction.OnDueDateChange -> {
                mutableStateFlow.update { it.copy(dueDate = action.dueDate) }
            }

            is ChargesAction.OnAmountChange -> {
                mutableStateFlow.update {
                    it.copy(
                        amount = action.amount,
                        amountValidationError = null,
                    )
                }
            }

            is ChargesAction.OnShowAddCharge -> {
                mutableStateFlow.update {
                    it.copy(
                        showChargeAddFields = true,
                    )
                }
            }

            is ChargesAction.OnCollectedOnDatePick -> {
                mutableStateFlow.update {
                    it.copy(
                        showCollectedOnDatePicker = action.collectedOnPick,
                    )
                }
            }

            is ChargesAction.OnDueDatePick -> {
                mutableStateFlow.update {
                    it.copy(
                        showDueDatePicker = action.dueDatePick,
                    )
                }
            }

            ChargesAction.DismissDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                        isUpdate = false,
                    )
                }
            }

            is ChargesAction.DeleteCharge -> {
                deleteCharge(action.chargeId)
            }

            is ChargesAction.FetchEditChargeData -> {
                getCharge(action.chargeId)
            }

            ChargesAction.OnShowChargeBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ChargesState.DialogState.ShowChargeBottomSheet,
                    )
                }
            }
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                if (isConnected) {
                    loadChargeTemplate()
                    if (route.resourceType == Constants.ENTITY_TYPE_CLIENTS) {
                        clientLoadCharges()
                    } else {
                        loadOtherAccountCharges()
                    }
                } else {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ChargesState.DialogState.Error("Network Error"),
                        )
                    }
                }
            }
        }
    }

    /**
     * it fetch client charge list
     */
    private suspend fun clientLoadCharges(showBottomSheet: Boolean = false) {
        repository.getListOfClientCharges(route.resourceType, route.resourceId)
            .collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ChargesState.DialogState.Error(dataState.message),
                            )
                        }
                    }

                    DataState.Loading -> {
                        if (!showBottomSheet) {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ChargesState.DialogState.Loading,
                                )
                            }
                        }
                    }

                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                chargesList = dataState.data.pageItems,
                                totalCharges = dataState.data.totalFilteredRecords,
                                dialogState = if (showBottomSheet && dataState.data.pageItems.isNotEmpty()) ChargesState.DialogState.ShowChargeBottomSheet else null,
                            )
                        }
                    }
                }
            }
    }

    /**
     * it fetch other account charge list like saving, load and etc.
     */
    private suspend fun loadOtherAccountCharges(showBottomSheet: Boolean = false) {
        repository.getListOfOtherAccountCharge(route.resourceType, route.resourceId)
            .collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ChargesState.DialogState.Error(dataState.message),
                            )
                        }
                    }

                    DataState.Loading -> {
                        if (!showBottomSheet) {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ChargesState.DialogState.Loading,
                                )
                            }
                        }
                    }

                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                chargesList = dataState.data,
                                dialogState = if (showBottomSheet && dataState.data.isNotEmpty()) ChargesState.DialogState.ShowChargeBottomSheet else null,
                                totalCharges = dataState.data.size,
                            )
                        }
                    }
                }
            }
    }

    private fun getCharge(chargeId: Int) {
        viewModelScope.launch {
            repository.getCharge(route.resourceType, route.resourceId, chargeId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ChargesState.DialogState.Error(dataState.message),
                                    isOverlayLoading = false,
                                )
                            }
                        }

                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ChargesState.DialogState.Loading,
                                    isOverlayLoading = true,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                val index =
                                    state.chargeTemplate?.chargeOptions?.indexOfFirst { item -> item.name == dataState.data.name }
                                it.copy(
                                    amount = dataState.data.amount.toString(),
                                    dueDate = DateHelper.getDateAsString(
                                        dataState.data.dueDate ?: emptyList(),
                                    ),
                                    chargeOptionIndex = index,
                                    chargeOptionId = index?.let { item ->
                                        state.chargeTemplate?.chargeOptions?.get(
                                            item,
                                        )?.id
                                    },
                                    dialogState = null,
                                    showChargeAddFields = true,
                                    isOverlayLoading = false,
                                    isUpdate = true,
                                    chargeId = chargeId,
                                )
                            }
                        }
                    }
                }
        }
    }

    private suspend fun loadChargeTemplate() {
        getChargeTemplateUseCase(route.resourceType, route.resourceId).collect { result ->
            when (result) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ChargesState.DialogState.Error(
                                message = result.message,
                            ),
                        )
                    }
                }

                is DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ChargesState.DialogState.Loading,
                        )
                    }
                }

                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            chargeTemplate = result.data,
                        )
                    }
                }
            }
        }
    }

    private suspend fun createCharge(payload: ChargesPayload) {
        createChargesUseCase(route.resourceType, route.resourceId, payload).collect { result ->
            when (result) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ChargesState.DialogState.ShowStatusDialog(
                                ResultStatus.FAILURE,
                                message = result.message,
                            ),
                            isOverlayLoading = false,
                        )
                    }
                }

                is DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ChargesState.DialogState.Loading,
                            isOverlayLoading = true,
                        )
                    }
                }

                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = ChargesState.DialogState.ShowStatusDialog(
                                ResultStatus.SUCCESS,
                            ),
                            isOverlayLoading = false,
                        )
                    }
                }
            }
        }
    }

    private suspend fun updateCharge(payload: ChargesPayload, chargeId: Int?) {
        /**
         * It it check when Client is Update then it through error
         * because client update API not have currently.
         */
        if (route.resourceType == Constants.ENTITY_TYPE_CLIENTS) {
            mutableStateFlow.update {
                it.copy(
                    dialogState = ChargesState.DialogState.ShowStatusDialog(
                        ResultStatus.FAILURE,
                        message = getString(Res.string.charges_client_update_not_support_error_msg),
                    ),
                )
            }
        } else {
            chargeId?.let { chargeId ->
                repository.updateCharge(
                    resourceType = route.resourceType,
                    resourceId = route.resourceId,
                    payload = payload,
                    chargeId = chargeId,
                ).collect { result ->
                    when (result) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ChargesState.DialogState.ShowStatusDialog(
                                        ResultStatus.FAILURE,
                                        message = result.message,
                                    ),
                                    isOverlayLoading = false,
                                )
                            }
                        }

                        is DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ChargesState.DialogState.Loading,
                                    isOverlayLoading = true,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = ChargesState.DialogState.ShowStatusDialog(
                                        ResultStatus.SUCCESS,
                                    ),
                                    isOverlayLoading = false,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun deleteCharge(chargeId: Int) {
        viewModelScope.launch {
            repository.deleteCharge(
                resourceType = route.resourceType,
                resourceId = route.resourceId,
                chargeId = chargeId,
            ).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ChargesState.DialogState.Error(
                                    message = result.message,
                                ),
                                isOverlayLoading = false,
                            )
                        }
                    }

                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                isOverlayLoading = true,
                            )
                        }
                    }

                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = ChargesState.DialogState.ShowChargeBottomSheet,
                                isOverlayLoading = false,
                            )
                        }

                        if (route.resourceType == Constants.ENTITY_TYPE_CLIENTS) {
                            clientLoadCharges(true)
                        } else {
                            loadOtherAccountCharges(true)
                        }
                    }
                }
            }
        }
    }
}

data class ChargesState
@OptIn(ExperimentalTime::class)
constructor(
    val chargesList: List<ChargesEntity> = emptyList(),
    val isOverlayLoading: Boolean = false,
    val showChargeAddFields: Boolean = false,
    val dialogState: DialogState? = null,
    val showDueDatePicker: Boolean = false,
    val showCollectedOnDatePicker: Boolean = false,
    val chargeTemplate: ChargeTemplate? = null,

    val amount: String? = null,
    val isUpdate: Boolean = false,
    val chargeId: Int? = null,

    val dueDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),

    val chargeOptionId: Int? = null,
    val totalCharges: Int = 0,
    val chargeOptionIndex: Int? = null,
    val chargeOptionError: StringResource? = null,
    val amountValidationError: StringResource? = null,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
        data object ShowChargeBottomSheet : DialogState
        data class ShowStatusDialog(val status: ResultStatus, val message: String = "") :
            DialogState
    }
}

sealed interface ChargesEvent {
    data object NavigateBack : ChargesEvent
}

sealed interface ChargesAction {
    data object NavigateBack : ChargesAction
    data object OnRetry : ChargesAction
    data object DismissDialog : ChargesAction
    data object CreateCharge : ChargesAction
    data object CloseChargeAddFields : ChargesAction
    data class OnChargeOptionChange(val index: Int) : ChargesAction
    data class OnDueDateChange(val dueDate: String) : ChargesAction
    data class OnCollectedOnDatePick(val collectedOnPick: Boolean) : ChargesAction
    data class OnDueDatePick(val dueDatePick: Boolean) : ChargesAction
    data object OnShowAddCharge : ChargesAction
    data object OnShowChargeBottomSheet : ChargesAction
    data class OnAmountChange(val amount: String) : ChargesAction
    data class DeleteCharge(val chargeId: Int) : ChargesAction
    data class FetchEditChargeData(val chargeId: Int) : ChargesAction
}
