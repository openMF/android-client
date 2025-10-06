/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountv2

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.GetClientTemplateUseCase
import com.mifos.core.domain.useCases.GetSavingsProductTemplateUseCase
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.TextFieldsValidator
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import com.mifos.room.entities.templates.clients.SavingProductOptionsEntity
import com.mifos.room.entities.templates.clients.StaffOptionsEntity
import com.mifos.room.entities.templates.savings.SavingProductsTemplate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class SavingsAccountViewModel(
    private val networkMonitor: NetworkMonitor,
    private val getClientTemplateUseCase: GetClientTemplateUseCase,
    private val getSavingsProductTemplateUseCase: GetSavingsProductTemplateUseCase,
    val savedStateHandle: SavedStateHandle,
) :
    BaseViewModel<SavingsAccountState, SavingsAccountEvent, SavingsAccountAction>(
        initialState = run {
            SavingsAccountState(clientId = savedStateHandle.toRoute<SavingsAccountRoute>().clientId)
        },
    ) {

    init {
        loadClientTemplate()
        loadSavingsProductTemplate()
    }

    override fun handleAction(action: SavingsAccountAction) {
        when (action) {
            is SavingsAccountAction.NavigateBack -> sendEvent(SavingsAccountEvent.NavigateBack)
            is SavingsAccountAction.NextStep -> moveToNextStep()
            is SavingsAccountAction.PreviousStep -> moveToPreviousStep()
            is SavingsAccountAction.Finish -> sendEvent(SavingsAccountEvent.Finish)
            is SavingsAccountAction.OnStepChange -> handleStepChange(action)
            is SavingsAccountAction.OnSubmissionDatePick -> handleSubmissionDatePick(action)
            is SavingsAccountAction.Retry -> handleRetry()
            is SavingsAccountAction.OnSubmissionDateChange -> handleSubmissionDateChange(action)
            is SavingsAccountAction.OnDetailsSubmit -> handleOnDetailsSubmit()
            is SavingsAccountAction.OnExternalIdChange -> handleExternalIdChange(action)
            is SavingsAccountAction.OnProductNameChange -> handleOnProductNameChange(action)
            is SavingsAccountAction.Internal.OnReceivingClientTemplate -> handleClientTemplateResponse(action.clientTemplate)
            is SavingsAccountAction.OnFieldOfficerChange -> handleFieldOfficerChange(action)
            is SavingsAccountAction.OnDecimalPlacesChange -> handleDecimalPlacesChange(action)
            is SavingsAccountAction.OnMinimumOpeningBalanceChange -> handleMinimumOpeningBalanceChange(action)
            is SavingsAccountAction.OnFrequencyChange -> handleFrequencyChange(action)
            is SavingsAccountAction.OnMonthlyMinimumBalanceChange -> handleMonthlyMinimumBalanceChange(action)
            is SavingsAccountAction.OnApplyWithdrawalFeeChange -> handleApplyWithdrawalChange(action)
            is SavingsAccountAction.OnMinimumBalanceChange -> handleMinimumBalanceChange(action)
            is SavingsAccountAction.OnOverDraftAllowedChange -> handleApplyOverdraftChange(action)
            is SavingsAccountAction.Internal.OnReceivingSavingsProductTemplate -> handleSavingsProductTemplate(action.savingsProductTemplate)
            is SavingsAccountAction.OnCurrencyChange -> handleCurrencyChange(action)
            is SavingsAccountAction.OnDaysInYearChange -> handleDaysInYearChange(action)
            is SavingsAccountAction.OnFreqTypeChange -> handleFreqTypeChange(action)
            is SavingsAccountAction.OnInterestCalcChange -> handleInterestCalcChange(action)
            is SavingsAccountAction.OnInterestCompPeriodChange -> handleInterestCompPeriodChange(action)
            is SavingsAccountAction.OnInterestPostingPeriodChange -> handleInterestPostingPeriodChange(action)
            is SavingsAccountAction.SetCurrencyError -> handleCurrencyError(action)
            is SavingsAccountAction.SetDecimalPlacesError -> handleDecimalPlacesError(action)
            is SavingsAccountAction.SetInterestCompPeriodError -> handleInterestCompPeriodError(action)
            is SavingsAccountAction.SetInterestPostingPeriodError -> handleInterestPostingPeriodError(action)
            is SavingsAccountAction.SetInterestCalcError -> handleInterestCalcError(action)
            is SavingsAccountAction.SetDaysInYearError -> handleDaysInYearError(action)
            is SavingsAccountAction.OnMinimumOpeningBalanceError -> handleMinimumOpeningBalanceError(action)
            is SavingsAccountAction.SetFrequencyError -> handleFrequencyError(action)
            is SavingsAccountAction.SetFreqTypeError -> handleFreqTypeError(action)
            is SavingsAccountAction.OnMonthlyMinimumBalanceError -> handleMonthlyMinimumBalanceError(action)
        }
    }

    private fun handleCurrencyError(action: SavingsAccountAction.SetCurrencyError) {
        mutableStateFlow.update { it.copy(currencyError = action.message) }
    }

    private fun handleDecimalPlacesError(action: SavingsAccountAction.SetDecimalPlacesError) {
        mutableStateFlow.update { it.copy(decimalPlacesError = action.message) }
    }

    private fun handleInterestCompPeriodError(action: SavingsAccountAction.SetInterestCompPeriodError) {
        mutableStateFlow.update { it.copy(interestCompPeriodError = action.message) }
    }

    private fun handleInterestPostingPeriodError(action: SavingsAccountAction.SetInterestPostingPeriodError) {
        mutableStateFlow.update { it.copy(interestPostingPeriodError = action.message) }
    }

    private fun handleInterestCalcError(action: SavingsAccountAction.SetInterestCalcError) {
        mutableStateFlow.update { it.copy(interestCalcError = action.message) }
    }

    private fun handleDaysInYearError(action: SavingsAccountAction.SetDaysInYearError) {
        mutableStateFlow.update { it.copy(daysInYearError = action.message) }
    }

    private fun handleMinimumOpeningBalanceError(action: SavingsAccountAction.OnMinimumOpeningBalanceError) {
        mutableStateFlow.update { it.copy(minimumOpeningBalanceError = action.message) }
    }

    private fun handleFrequencyError(action: SavingsAccountAction.SetFrequencyError) {
        mutableStateFlow.update { it.copy(frequencyError = action.message) }
    }

    private fun handleFreqTypeError(action: SavingsAccountAction.SetFreqTypeError) {
        mutableStateFlow.update { it.copy(freqTypeError = action.message) }
    }

    private fun handleMonthlyMinimumBalanceError(action: SavingsAccountAction.OnMonthlyMinimumBalanceError) {
        mutableStateFlow.update { it.copy(monthlyMinimumBalanceError = action.message) }
    }

    private fun handleInterestCalcChange(action: SavingsAccountAction.OnInterestCalcChange) {
        mutableStateFlow.update { it.copy(interestCalcIndex = action.index) }
    }

    private fun handleInterestCompPeriodChange(action: SavingsAccountAction.OnInterestCompPeriodChange) {
        mutableStateFlow.update { it.copy(interestCompPeriodIndex = action.index) }
    }

    private fun handleInterestPostingPeriodChange(action: SavingsAccountAction.OnInterestPostingPeriodChange) {
        mutableStateFlow.update { it.copy(interestPostingPeriodIndex = action.index) }
    }

    private fun handleFreqTypeChange(action: SavingsAccountAction.OnFreqTypeChange) {
        mutableStateFlow.update { it.copy(freqTypeIndex = action.index) }
    }

    private fun handleDaysInYearChange(action: SavingsAccountAction.OnDaysInYearChange) {
        mutableStateFlow.update { it.copy(daysInYearIndex = action.index) }
    }

    private fun handleCurrencyChange(action: SavingsAccountAction.OnCurrencyChange) {
        mutableStateFlow.update { it.copy(currencyIndex = action.index) }
    }

    private fun handleSavingsProductTemplate(result: DataState<SavingProductsTemplate>) {
        when (result) {
            is DataState.Loading -> mutableStateFlow.update {
                it.copy(
                    screenState = SavingsAccountState.ScreenState.Loading,
                )
            }
            is DataState.Error -> mutableStateFlow.update {
                it.copy(
                    dialogState = SavingsAccountState.DialogState.Error(result.message),
                )
            }
            is DataState.Success -> mutableStateFlow.update {
                it.copy(
                    dialogState = null,
                    screenState = SavingsAccountState.ScreenState.Success,
                    savingsProductTemplate = result.data,
                    currencyIndex = result.data.currencyOptions?.indexOf(result.data.currency) ?: -1,
                    decimalPlaces = result.data.currency?.decimalPlaces?.toInt().toString(),
                    interestPostingPeriodIndex = result.data.interestPostingPeriodTypeOptions?.indexOf(result.data.interestPostingPeriodType) ?: -1,
                    interestCalcIndex = result.data.interestCalculationTypeOptions?.indexOf(result.data.interestCalculationType) ?: -1,
                    interestCompPeriodIndex = result.data.interestCompoundingPeriodTypeOptions?.indexOf(result.data.interestCompoundingPeriodType) ?: -1,
                    daysInYearIndex = result.data.interestCalculationDaysInYearTypeOptions?.indexOf(result.data.interestCalculationDaysInYearType) ?: -1,
                )
            }
        }
    }

    private fun handleApplyWithdrawalChange(action: SavingsAccountAction.OnApplyWithdrawalFeeChange) {
        mutableStateFlow.update { it.copy(isCheckedApplyWithdrawalFee = action.boolean) }
    }

    private fun handleMinimumBalanceChange(action: SavingsAccountAction.OnMinimumBalanceChange) {
        mutableStateFlow.update { it.copy(isCheckedMinimumBalance = action.boolean) }
    }

    private fun handleApplyOverdraftChange(action: SavingsAccountAction.OnOverDraftAllowedChange) {
        mutableStateFlow.update { it.copy(isCheckedOverdraftAllowed = action.boolean) }
    }

    private fun handleFrequencyChange(action: SavingsAccountAction.OnFrequencyChange) {
        mutableStateFlow.update { it.copy(frequency = action.value) }
    }

    private fun handleMonthlyMinimumBalanceChange(action: SavingsAccountAction.OnMonthlyMinimumBalanceChange) {
        mutableStateFlow.update { it.copy(monthlyMinimumBalance = action.value) }
    }

    private fun handleMinimumOpeningBalanceChange(action: SavingsAccountAction.OnMinimumOpeningBalanceChange) {
        mutableStateFlow.update { it.copy(minimumOpeningBalance = action.value) }
    }

    private fun handleDecimalPlacesChange(action: SavingsAccountAction.OnDecimalPlacesChange) {
        mutableStateFlow.update { it.copy(decimalPlaces = action.value) }
    }

    private fun handleFieldOfficerChange(action: SavingsAccountAction.OnFieldOfficerChange) {
        mutableStateFlow.update { it.copy(fieldOfficerIndex = action.index) }
    }

    private fun handleClientTemplateResponse(result: DataState<ClientsTemplateEntity>) {
        when (result) {
            is DataState.Loading -> mutableStateFlow.update {
                it.copy(
                    screenState = SavingsAccountState.ScreenState.Loading,
                )
            }

            is DataState.Error -> mutableStateFlow.update {
                it.copy(
                    dialogState = SavingsAccountState.DialogState.Error(result.message),
                )
            }

            is DataState.Success -> mutableStateFlow.update {
                it.copy(
                    dialogState = null,
                    screenState = SavingsAccountState.ScreenState.Success,
                    savingProductOptions = result.data.savingProductOptions ?: emptyList(),
                    fieldOfficerOptions = result.data.staffOptions ?: emptyList(),
                )
            }
        }
    }

    private fun handleOnProductNameChange(action: SavingsAccountAction.OnProductNameChange) {
        mutableStateFlow.update { it.copy(savingsProductSelected = action.index) }
    }

    private fun handleExternalIdChange(action: SavingsAccountAction.OnExternalIdChange) {
        mutableStateFlow.update { it.copy(externalId = action.value) }
    }

    private fun handleStepChange(action: SavingsAccountAction.OnStepChange) {
        mutableStateFlow.update { it.copy(currentStep = action.newIndex) }
    }

    private fun handleSubmissionDatePick(action: SavingsAccountAction.OnSubmissionDatePick) {
        mutableStateFlow.update { it.copy(showSubmissionDatePick = action.state) }
    }

    private fun handleSubmissionDateChange(action: SavingsAccountAction.OnSubmissionDateChange) {
        mutableStateFlow.update { it.copy(submissionDate = action.date) }
    }

    private fun handleOnDetailsSubmit() {
        mutableStateFlow.update {
            it.copy(
                externalIdError = null,
            )
        }
        val externalIdError = TextFieldsValidator.optionalStringValidator(state.externalId)
        if (externalIdError != null) {
            mutableStateFlow.update { it.copy(externalIdError = externalIdError) }
            return
        } else {
            moveToNextStep()
        }
    }

    private fun loadClientTemplate() = viewModelScope.launch {
        val online = networkMonitor.isOnline.first()
        if (online) {
            getClientTemplateUseCase().collect { result ->
                sendAction(SavingsAccountAction.Internal.OnReceivingClientTemplate(result))
            }
        } else {
            mutableStateFlow.update {
                it.copy(
                    screenState = SavingsAccountState.ScreenState.NetworkError,
                )
            }
        }
    }

    private fun loadSavingsProductTemplate() = viewModelScope.launch {
        val online = networkMonitor.isOnline.first()
        if (online) {
            getSavingsProductTemplateUseCase().collect { result ->
                sendAction(SavingsAccountAction.Internal.OnReceivingSavingsProductTemplate(result))
            }
        } else {
            mutableStateFlow.update {
                it.copy(
                    screenState = SavingsAccountState.ScreenState.NetworkError,
                )
            }
        }
    }

    private fun handleRetry() {
        mutableStateFlow.update {
            it.copy(
                dialogState = null,
            )
        }
        loadClientTemplate()
    }

    private fun moveToPreviousStep() {
        val current = state.currentStep
        mutableStateFlow.update {
            it.copy(
                currentStep = current - 1,
            )
        }
    }

    private fun moveToNextStep() {
        val current = state.currentStep
        if (current < state.totalSteps) {
            mutableStateFlow.update {
                it.copy(
                    currentStep = current + 1,
                )
            }
        } else {
            sendEvent(SavingsAccountEvent.Finish)
        }
    }
}

data class SavingsAccountState
@OptIn(ExperimentalTime::class)
constructor(
    val clientId: Int,
    val fieldOfficerIndex: Int = -1,
    val fieldOfficerOptions: List<StaffOptionsEntity> = emptyList(),
    val isOverLayLoadingActive: Boolean = false,
    val savingsProductSelected: Int = -1,
    val savingProductOptions: List<SavingProductOptionsEntity> = emptyList(),
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    val dialogState: DialogState? = null,
    val externalId: String = "",
    val externalIdError: StringResource? = null,
    val screenState: ScreenState = ScreenState.Loading,
    val submissionDate: String = DateHelper.getDateAsStringFromLong(Clock.System.now().toEpochMilliseconds()),
    val showSubmissionDatePick: Boolean = false,
    val decimalPlaces: String = "",
    val decimalPlacesError: String? = null,
    val minimumOpeningBalance: String = "",
    val minimumOpeningBalanceError: String? = null,
    val frequency: String = "",
    val frequencyError: String? = null,
    val monthlyMinimumBalance: String = "",
    val monthlyMinimumBalanceError: String? = null,
    val isCheckedApplyWithdrawalFee: Boolean = false,
    val isCheckedOverdraftAllowed: Boolean = false,
    val isCheckedMinimumBalance: Boolean = false,
    val savingsProductTemplate: SavingProductsTemplate? = null,
    val currencyIndex: Int = -1,
    val currencyError: String? = null,
    val interestCompPeriodIndex: Int = -1,
    val interestCompPeriodError: String? = null,
    val interestPostingPeriodIndex: Int = -1,
    val interestPostingPeriodError: String? = null,
    val interestCalcIndex: Int = -1,
    val interestCalcError: String? = null,
    val daysInYearIndex: Int = -1,
    val daysInYearError: String? = null,
    val freqTypeIndex: Int = -1,
    val freqTypeError: String? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
    }

    sealed interface ScreenState {
        data object Loading : ScreenState
        data object Success : ScreenState
        data object NetworkError : ScreenState
    }

    val isDetailsNextEnabled = submissionDate.isNotEmpty() &&
        savingsProductSelected != -1 &&
        fieldOfficerIndex != -1

    val isTermsNextEnabled = isDetailsNextEnabled &&
        currencyIndex != -1 &&
        interestCalcIndex != -1 &&
        interestPostingPeriodIndex != -1 &&
        interestCompPeriodIndex != -1
}

sealed interface SavingsAccountEvent {
    data object NavigateBack : SavingsAccountEvent
    data object Finish : SavingsAccountEvent
}

sealed interface SavingsAccountAction {
    data object NavigateBack : SavingsAccountAction
    data object NextStep : SavingsAccountAction
    data object PreviousStep : SavingsAccountAction
    data object Finish : SavingsAccountAction
    data class OnStepChange(val newIndex: Int) : SavingsAccountAction
    data class OnSubmissionDateChange(val date: String) : SavingsAccountAction
    data class OnSubmissionDatePick(val state: Boolean) : SavingsAccountAction
    data object OnDetailsSubmit : SavingsAccountAction
    data class OnProductNameChange(val index: Int) : SavingsAccountAction
    data class OnFieldOfficerChange(val index: Int) : SavingsAccountAction
    data class OnExternalIdChange(val value: String) : SavingsAccountAction
    data class OnDecimalPlacesChange(val value: String) : SavingsAccountAction
    data class OnMinimumOpeningBalanceChange(val value: String) : SavingsAccountAction
    data class OnFrequencyChange(val value: String) : SavingsAccountAction
    data class OnMonthlyMinimumBalanceChange(val value: String) : SavingsAccountAction
    data class OnApplyWithdrawalFeeChange(val boolean: Boolean) : SavingsAccountAction
    data class OnOverDraftAllowedChange(val boolean: Boolean) : SavingsAccountAction
    data class OnMinimumBalanceChange(val boolean: Boolean) : SavingsAccountAction
    data class OnCurrencyChange(val index: Int) : SavingsAccountAction
    data class OnInterestCompPeriodChange(val index: Int) : SavingsAccountAction
    data class OnInterestPostingPeriodChange(val index: Int) : SavingsAccountAction
    data class OnInterestCalcChange(val index: Int) : SavingsAccountAction
    data class OnDaysInYearChange(val index: Int) : SavingsAccountAction
    data class OnFreqTypeChange(val index: Int) : SavingsAccountAction
    data object Retry : SavingsAccountAction

    data class SetCurrencyError(val message: String?) : SavingsAccountAction
    data class SetDecimalPlacesError(val message: String?) : SavingsAccountAction
    data class SetInterestCompPeriodError(val message: String?) : SavingsAccountAction
    data class SetInterestPostingPeriodError(val message: String?) : SavingsAccountAction
    data class SetInterestCalcError(val message: String?) : SavingsAccountAction
    data class SetDaysInYearError(val message: String?) : SavingsAccountAction
    data class OnMinimumOpeningBalanceError(val message: String?) : SavingsAccountAction
    data class SetFrequencyError(val message: String?) : SavingsAccountAction
    data class SetFreqTypeError(val message: String?) : SavingsAccountAction
    data class OnMonthlyMinimumBalanceError(val message: String?) : SavingsAccountAction
    sealed interface Internal : SavingsAccountAction {
        data class OnReceivingClientTemplate(val clientTemplate: DataState<ClientsTemplateEntity>) : Internal
        data class OnReceivingSavingsProductTemplate(val savingsProductTemplate: DataState<SavingProductsTemplate>) : Internal
    }
}
