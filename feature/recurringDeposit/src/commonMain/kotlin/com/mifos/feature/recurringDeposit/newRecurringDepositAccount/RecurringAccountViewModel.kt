/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.recurringDeposit.newRecurringDepositAccount

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.RecurringAccountRepository
import com.mifos.core.model.objects.template.recurring.FieldOfficerOption
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.templates.recurringDeposit.RecurringDepositAccountTemplate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class RecurringAccountViewModel(
    private val recurringAccountRepo: RecurringAccountRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<
    RecurringAccountState,
    RecurringAccountEvent,
    RecurringAccountAction,
    >(
    run {
        RecurringAccountState(
            clientId = savedStateHandle.toRoute<RecurringAccountRoute>().clientId,
        )
    },
) {
    init {
        loadRecurringAccountTemplate()
    }

    override fun handleAction(action: RecurringAccountAction) {
        when (action) {
            RecurringAccountAction.NextStep -> {
                mutableStateFlow.update { state ->
                    val maxIndex = 4
                    state.copy(currentStep = (state.currentStep + 1).coerceAtMost(maxIndex))
                }
            }

            is RecurringAccountAction.OnStepChange -> {
                mutableStateFlow.update { it.copy(currentStep = action.index) }
            }

            RecurringAccountAction.NavigateBack -> {
                sendEvent(RecurringAccountEvent.NavigateBack)
            }

            RecurringAccountAction.Finish -> {
                sendEvent(RecurringAccountEvent.Finish)
            }

            is RecurringAccountAction.OnProductNameChange -> handleProductNameChange(action)

            is RecurringAccountAction.OnSubmissionDateChange -> handleSubmissionDateChange(action)

            is RecurringAccountAction.OnSubmissionDatePick -> handleSubmissionDatePick(action)

            is RecurringAccountAction.OnFieldOfficerChange -> handleFieldOfficerChange(action)

            is RecurringAccountAction.OnExternalIdChange -> handleExternalIdChange(action)

            RecurringAccountAction.Retry -> resetForRetry()
        }
    }

    private fun handleProductNameChange(action: RecurringAccountAction.OnProductNameChange) {
        mutableStateFlow.update {
            it.copy(
                loanProductSelected = action.index,
            )
        }
        loadRecurringAccountTemplateWithProduct(state.clientId, state.template?.productOptions?.get(state.loanProductSelected)?.id ?: -1)
    }

    private fun resetForRetry() {
        mutableStateFlow.update {
            it.copy(
                currentStep = 0,
                template = null,
                state = RecurringAccountState.State.Loading,
                fieldOfficerOptions = emptyList(),
            )
        }
        loadRecurringAccountTemplate()
    }

    private fun handleFieldOfficerChange(action: RecurringAccountAction.OnFieldOfficerChange) {
        mutableStateFlow.update {
            it.copy(
                fieldOfficerIndex = action.index,
                fieldOfficerError = null,
            )
        }
    }

    private fun handleSubmissionDatePick(action: RecurringAccountAction.OnSubmissionDatePick) {
        mutableStateFlow.update { it.copy(showSubmissionDatePick = action.state) }
    }

    private fun handleSubmissionDateChange(action: RecurringAccountAction.OnSubmissionDateChange) {
        mutableStateFlow.update { it.copy(submissionDate = action.date) }
    }

    private fun handleExternalIdChange(action: RecurringAccountAction.OnExternalIdChange) {
        mutableStateFlow.update { it.copy(externalId = action.value) }
    }

    private fun loadRecurringAccountTemplate() = viewModelScope.launch {
        recurringAccountRepo.getRecuttingAccountRepository().collect { state ->
            when (state) {
                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            state = RecurringAccountState.State.Success,
                            template = state.data,
                        )
                    }
                }
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            state = RecurringAccountState.State.Error(state.message),
                        )
                    }
                }

                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(
                            state = RecurringAccountState.State.Loading,
                        )
                    }
                }
            }
        }
    }

    private fun loadRecurringAccountTemplateWithProduct(
        clientId: Int,
        productId: Int,
    ) = viewModelScope.launch {
        recurringAccountRepo.getRecuttingAccountRepositoryBtProduct(clientId, productId).collect { state ->
            when (state) {
                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            state = RecurringAccountState.State.Success,
                            template = state.data,
                            fieldOfficerOptions = state.data.fieldOfficerOptions,
                            isMiniLoaderActive = false,
                        )
                    }
                }
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            state = RecurringAccountState.State.Error(state.message),
                            isMiniLoaderActive = false,
                        )
                    }
                }

                DataState.Loading -> {
                    mutableStateFlow.update {
                        it.copy(
                            isMiniLoaderActive = true,
                        )
                    }
                }
            }
        }
    }
}

data class RecurringAccountState(
    val clientId: Int,
    val currentStep: Int = 0,
    val currencyIndex: Int = -1,
    val currencyError: String? = null,
    val loanProductSelected: Int = -1,
    val template: RecurringDepositAccountTemplate? = null,
    val fieldOfficerOptions: List<FieldOfficerOption>? = null,
    val recurringDepositAccountSettings: RecurringAccountSettingsState = RecurringAccountSettingsState(),
    val state: State = State.Loading,
    val showSubmissionDatePick: Boolean = false,
    val submissionDate: String = "",
    val fieldOfficerIndex: Int = -1,
    val fieldOfficerError: StringResource? = null,
    val externalId: String = "",
    val isMiniLoaderActive: Boolean = false,
) {
    sealed interface State {
        data object Success : State
        data class Error(val message: String) : State
        object Loading : State
    }

    val isDetailButtonEnabled = fieldOfficerIndex != -1 && submissionDate.isNotEmpty()
}

data class RecurringAccountSettingsState(
    val isMandatory: Boolean = false,
    val adjustAdvancePayments: Boolean = false,
    val allowWithdrawals: Boolean = false,
    val lockInPeriod: LockInPeriod = LockInPeriod(),
    val recurringDepositDetails: RecurringDepositDetails = RecurringDepositDetails(),
    val depositPeriod: DepositPeriod = DepositPeriod(),
    val minimumDepositTerm: MinimumDepositTerm = MinimumDepositTerm(),
    val preMatureClosure: PreMatureClosure = PreMatureClosure(),
) {
    data class LockInPeriod(
        val frequency: String = "",
        val frequencyTypeIndex: Int = -1,
        val freqTypeError: String? = null,
    )

    data class RecurringDepositDetails(
        val depositAmount: String = "",
    )

    data class DepositPeriod(
        val period: String = "",
        val periodType: Int = -1,
        val periodTypeError: String? = null,
        val depositFrequencySameAsGroupCenterMeeting: Boolean = false,
    )

    data class MinimumDepositTerm(
        val frequency: String = "",
        val frequencyTypeIndex: Int = -1,
        val freqTypeError: String? = null,
        val frequencyAfterInMultiplesOf: String = "",
        val frequencyTypeIndexAfterInMultiplesOf: Int = -1,
        val freqTypeAfterInMultiplesOfError: String? = null,
    )

    data class PreMatureClosure(
        val applyPenalInterest: Boolean = false,
        val penalInterest: String = "",
        val interestPeriodIndex: Int = -1,
        val interestPeriodIndexError: String? = null,
        val minimumBalanceForInterestCalculation: String = "",
    )
}

sealed interface RecurringAccountAction {
    data object Retry : RecurringAccountAction
    data object NextStep : RecurringAccountAction
    data class OnStepChange(val index: Int) : RecurringAccountAction
    data object NavigateBack : RecurringAccountAction
    data object Finish : RecurringAccountAction
    data class OnProductNameChange(val index: Int) : RecurringAccountAction
    data class OnSubmissionDateChange(val date: String) : RecurringAccountAction
    data class OnSubmissionDatePick(val state: Boolean) : RecurringAccountAction
    data class OnFieldOfficerChange(val index: Int) : RecurringAccountAction
    data class OnExternalIdChange(val value: String) : RecurringAccountAction
}

sealed class RecurringAccountEvent {
    object NavigateBack : RecurringAccountEvent()
    object Finish : RecurringAccountEvent()
}
