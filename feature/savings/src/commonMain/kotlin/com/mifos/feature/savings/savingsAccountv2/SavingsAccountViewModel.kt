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

import androidclient.feature.savings.generated.resources.Res
import androidclient.feature.savings.generated.resources.feature_savings_error_not_connected_internet
import androidclient.feature.savings.generated.resources.feature_savings_new_savings_account_created_successfully
import androidclient.feature.savings.generated.resources.field_empty_msg
import androidclient.feature.savings.generated.resources.step_terms_decimal_places_error
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.domain.useCases.CreateSavingsAccountUseCase
import com.mifos.core.domain.useCases.GetClientTemplateUseCase
import com.mifos.core.domain.useCases.GetSavingsProductTemplateUseCase
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.payloads.SavingsPayload
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import com.mifos.room.entities.templates.clients.SavingProductOptionsEntity
import com.mifos.room.entities.templates.clients.StaffOptionsEntity
import com.mifos.room.entities.templates.savings.SavingProductsTemplate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class SavingsAccountViewModel(
    private val networkMonitor: NetworkMonitor,
    private val getClientTemplateUseCase: GetClientTemplateUseCase,
    private val getSavingsProductTemplateUseCase: GetSavingsProductTemplateUseCase,
    private val createSavingsAccountUseCase: CreateSavingsAccountUseCase,
    val savedStateHandle: SavedStateHandle,
) : BaseViewModel<SavingsAccountState, SavingsAccountEvent, SavingsAccountAction>(
    initialState = run {
        SavingsAccountState(clientId = savedStateHandle.toRoute<SavingsAccountRoute>().clientId)
    },
) {

    init {
        loadSavingsProductTemplate()
    }

    suspend fun isOnline(
        content: suspend () -> Unit,
    ) {
        if (networkMonitor.isOnline.first()) {
            content()
        } else {
            mutableStateFlow.update {
                it.copy(
                    screenState = SavingsAccountState.ScreenState.Error(getString(Res.string.feature_savings_error_not_connected_internet)),
                )
            }
        }
    }

    override fun handleAction(action: SavingsAccountAction) {
        when (action) {
            is SavingsAccountAction.NavigateBack -> sendEvent(SavingsAccountEvent.NavigateBack)
            is SavingsAccountAction.NextStep -> moveToNextStep()
            is SavingsAccountAction.PreviousStep -> moveToPreviousStep()
            is SavingsAccountAction.Finish -> {
                sendEvent(SavingsAccountEvent.Finish)
            }

            is SavingsAccountAction.OnStepChange -> handleStepChange(action)
            is SavingsAccountAction.Retry -> handleRetry()

            is SavingsAccountAction.OnSubmissionDatePick -> handleSubmissionDatePick(action)
            is SavingsAccountAction.OnSubmissionDateChange -> handleSubmissionDateChange(action)
            is SavingsAccountAction.OnDetailsSubmit -> handleOnDetailsSubmit()
            is SavingsAccountAction.OnExternalIdChange -> handleExternalIdChange(action)
            is SavingsAccountAction.OnProductNameChange -> handleOnProductNameChange(action)
            is SavingsAccountAction.Internal.OnReceivingClientTemplate -> handleClientTemplateResponse(
                action.clientTemplate,
            )

            is SavingsAccountAction.OnFieldOfficerChange -> handleFieldOfficerChange(action)
            is SavingsAccountAction.OnDecimalPlacesChange -> handleDecimalPlacesChange(action)
            is SavingsAccountAction.OnMinimumOpeningBalanceChange -> handleMinimumOpeningBalanceChange(
                action,
            )

            is SavingsAccountAction.OnFrequencyChange -> handleFrequencyChange(action)
            is SavingsAccountAction.OnMonthlyMinimumBalanceChange -> handleMonthlyMinimumBalanceChange(
                action,
            )

            is SavingsAccountAction.OnApplyWithdrawalFeeChange -> handleApplyWithdrawalChange(action)
            is SavingsAccountAction.OnMinimumBalanceChange -> handleMinimumBalanceChange(action)
            is SavingsAccountAction.OnOverDraftAllowedChange -> handleApplyOverdraftChange(action)
            is SavingsAccountAction.Internal.OnReceivingSavingsProductTemplate -> handleSavingsProductTemplate(
                action.savingsProductTemplate,
            )

            is SavingsAccountAction.OnCurrencyChange -> handleCurrencyChange(action)
            is SavingsAccountAction.OnDaysInYearChange -> handleDaysInYearChange(action)
            is SavingsAccountAction.OnFreqTypeChange -> handleFreqTypeChange(action)
            is SavingsAccountAction.OnInterestCalcChange -> handleInterestCalcChange(action)
            is SavingsAccountAction.OnInterestCompPeriodChange -> handleInterestCompPeriodChange(
                action,
            )

            is SavingsAccountAction.OnInterestPostingPeriodChange -> handleInterestPostingPeriodChange(
                action,
            )

            is SavingsAccountAction.ShowAddChargeDialog -> handleShowAddChargeDialog()
            is SavingsAccountAction.ShowCharges -> handleShowChargeDialog()
            is SavingsAccountAction.EditCharge -> handleEditCharge(action.index)
            is SavingsAccountAction.AddChargeToList -> handleAddChargeToList()
            is SavingsAccountAction.DismissDialog -> handleDismissDialog()
            is SavingsAccountAction.OnChargesAmountChange -> handleChargesAmountChange(action)
            is SavingsAccountAction.OnChargesDateChange -> handleChargesDateChange(action)
            is SavingsAccountAction.OnChargesDatePick -> handleChargesDatePick(action)
            is SavingsAccountAction.OnChooseChargeIndexChange -> handleChooseChargeIndexChange(
                action,
            )

            is SavingsAccountAction.DeleteChargeFromSelectedCharges -> handleDeleteCharge(action.index)
            is SavingsAccountAction.EditChargeDialog -> handleEditChargeDialog(action.index)
            is SavingsAccountAction.OnChargesAmountChangeError -> handleChargesAmountChangeError(
                action.error,
            )

            is SavingsAccountAction.SubmitSavingsApplication -> submitSavingsApplication()
            SavingsAccountAction.OnTermSubmit -> handleOnTermSubmit()
        }
    }

    private fun submitSavingsApplication() {
        val savingsPayload = SavingsPayload().apply {
            locale = "en"
            dateFormat = "dd-MM-yyyy"
            productId = state.savingProductOptions.getOrNull(state.savingsProductSelected)?.id
            clientId = state.clientId
            fieldOfficerId = state.fieldOfficerOptions.getOrNull(state.fieldOfficerIndex)?.id
            submittedOnDate = state.submissionDate
            externalId = state.externalId
            allowOverdraft = state.isCheckedOverdraftAllowed
            enforceMinRequiredBalance = state.isCheckedMinimumBalance
            minRequiredOpeningBalance = state.minimumOpeningBalance
            minRequiredBalance = state.monthlyMinimumBalance
            lockinPeriodFrequency = state.frequency.toIntOrNull()
            lockinPeriodFrequencyType =
                state.savingsProductTemplate?.lockinPeriodFrequencyTypeOptions?.getOrNull(state.freqTypeIndex)?.id.takeIf { state.frequency.toIntOrNull() != null }
            charges = state.addedCharges.map { charges ->
                ChargesPayload(
                    chargeId = charges.id,
                    amount = charges.amount.toString(),
                )
            }
            interestCompoundingPeriodType =
                state.savingsProductTemplate?.interestCompoundingPeriodTypeOptions?.getOrNull(state.interestCompPeriodIndex)?.id
            interestCalculationType =
                state.savingsProductTemplate?.interestCalculationTypeOptions?.getOrNull(state.interestCalcIndex)?.id
            interestCalculationDaysInYearType =
                state.savingsProductTemplate?.interestCalculationDaysInYearTypeOptions?.getOrNull(
                    state.daysInYearIndex,
                )?.id
            interestPostingPeriodType =
                state.savingsProductTemplate?.interestPostingPeriodTypeOptions?.getOrNull(state.interestPostingPeriodIndex)?.id
        }

        viewModelScope.launch {
            isOnline {
                createSavingsAccountUseCase(savingsPayload).collect { result ->
                    when (result) {
                        is DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    isOverLayLoadingActive = true,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    isOverLayLoadingActive = false,
                                    dialogState = SavingsAccountState.DialogState.SuccessResponseStatus(
                                        successStatus = true,
                                        msg = getString(Res.string.feature_savings_new_savings_account_created_successfully),
                                    ),
                                    launchEffectKey = Random.nextInt(),
                                )
                            }
                        }

                        is DataState.Error -> {
                            if (result.exception is IllegalStateException) {
                                mutableStateFlow.update {
                                    it.copy(
                                        dialogState = SavingsAccountState.DialogState.SuccessResponseStatus(
                                            successStatus = false,
                                            msg = result.message,
                                        ),
                                        launchEffectKey = Random.nextInt(),
                                        isOverLayLoadingActive = false,
                                    )
                                }
                            } else {
                                mutableStateFlow.update {
                                    it.copy(
                                        screenState = SavingsAccountState.ScreenState.Error(result.message),
                                        isOverLayLoadingActive = false,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleOnTermSubmit() {
        viewModelScope.launch {
            val decimalPlaces = state.decimalPlaces.toIntOrNull()
            val isCurrencyInvalid = state.currencyIndex == -1
            val isDecimalInvalid = decimalPlaces == null || decimalPlaces < 0 || decimalPlaces > 6

            if (isCurrencyInvalid || isDecimalInvalid) {
                mutableStateFlow.update {
                    it.copy(
                        currencyError = if (isCurrencyInvalid) getString(Res.string.field_empty_msg) else null,
                        decimalPlacesError = if (isDecimalInvalid) getString(Res.string.step_terms_decimal_places_error) else null,
                    )
                }
            } else {
                mutableStateFlow.update {
                    it.copy(
                        currencyError = null,
                        decimalPlacesError = null,
                    )
                }
                moveToNextStep()
            }
        }
    }

    private fun handleOnDetailsSubmit() {
        viewModelScope.launch {
            val msg = getString(Res.string.field_empty_msg)

            val isSavingProductInvalid = state.savingsProductSelected == -1

            if (isSavingProductInvalid) {
                mutableStateFlow.update {
                    it.copy(
                        savingProductError = msg,
                    )
                }
            } else {
                mutableStateFlow.update {
                    it.copy(
                        savingProductError = null,
                    )
                }
                moveToNextStep()
            }
        }
    }

    private fun handleChargesAmountChangeError(error: StringResource?) {
        mutableStateFlow.update {
            it.copy(
                chargeAmountError = error,
            )
        }
    }

    private fun handleEditChargeDialog(index: Int) {
        val selectedEditCharge = state.addedCharges[index]
        val chooseChargeIndex =
            state.savingsProductTemplate?.chargeOptions?.indexOfFirst { it.id == selectedEditCharge.id }
                ?: -1
        mutableStateFlow.update {
            it.copy(
                chargeAmount = selectedEditCharge.amount.toString(),
                chargeDate = selectedEditCharge.date,
                chooseChargeIndex = chooseChargeIndex,
                dialogState = SavingsAccountState.DialogState.AddNewCharge(true, index),
            )
        }
    }

    private fun handleDeleteCharge(index: Int) {
        val newCharges = state.addedCharges.toMutableList().apply {
            removeAt(index)
        }
        mutableStateFlow.update {
            it.copy(addedCharges = newCharges)
        }
    }

    private fun handleChargesDatePick(action: SavingsAccountAction.OnChargesDatePick) {
        mutableStateFlow.update {
            it.copy(showChargesDatePick = action.state)
        }
    }

    private fun handleChooseChargeIndexChange(action: SavingsAccountAction.OnChooseChargeIndexChange) {
        mutableStateFlow.update {
            it.copy(chooseChargeIndex = action.index)
        }
    }

    private fun handleChargesDateChange(action: SavingsAccountAction.OnChargesDateChange) {
        mutableStateFlow.update {
            it.copy(chargeDate = action.date)
        }
    }

    private fun handleChargesAmountChange(action: SavingsAccountAction.OnChargesAmountChange) {
        mutableStateFlow.update {
            it.copy(chargeAmount = action.amount)
        }
    }

    private fun handleDismissDialog() {
        mutableStateFlow.update { it.copy(dialogState = null) }
    }

    private fun handleAddChargeToList() {
        val selectedIndex = state.chooseChargeIndex
        val selectedCharge = state.savingsProductTemplate?.chargeOptions?.getOrNull(selectedIndex)
        val amount = state.chargeAmount.toDoubleOrNull() ?: selectedCharge?.amount ?: 0.0
        if (selectedCharge != null && state.chargeAmountError == null) {
            val newCharge = CreatedCharges(
                id = selectedCharge.id,
                name = selectedCharge.name,
                amount = amount,
                date = state.chargeDate,
                type = selectedCharge.chargeCalculationType?.value ?: "",
                collectedOn = selectedCharge.chargeTimeType?.value ?: "",
            )

            mutableStateFlow.update {
                it.copy(
                    addedCharges = it.addedCharges + newCharge,
                    chooseChargeIndex = -1,
                    dialogState = null,
                    chargeAmount = "",
                )
            }
        } else {
            mutableStateFlow.update {
                it.copy(
                    chooseChargeIndex = -1,
                    dialogState = null,
                    chargeAmount = "",
                )
            }
        }
    }

    private fun handleEditCharge(index: Int) {
        val selectedIndex = state.chooseChargeIndex
        val selectedCharge = state.savingsProductTemplate?.chargeOptions?.getOrNull(selectedIndex)
        val amount = state.chargeAmount.toDoubleOrNull() ?: selectedCharge?.amount ?: 0.0
        if (selectedCharge != null && state.chargeAmountError == null) {
            val newCharge = CreatedCharges(
                id = selectedCharge.id,
                name = selectedCharge.name,
                amount = amount,
                date = state.chargeDate,
                type = selectedCharge.chargeCalculationType?.value ?: "",
                collectedOn = selectedCharge.chargeTimeType?.value ?: "",
            )
            val currentAddedCharges = state.addedCharges.toMutableList()
            currentAddedCharges[index] = newCharge
            mutableStateFlow.update {
                it.copy(
                    addedCharges = currentAddedCharges,
                    chooseChargeIndex = -1,
                    dialogState = SavingsAccountState.DialogState.ShowCharges,
                    chargeAmount = "",
                )
            }
        }
    }

    private fun handleShowAddChargeDialog() {
        mutableStateFlow.update {
            it.copy(dialogState = SavingsAccountState.DialogState.AddNewCharge(false))
        }
    }

    private fun handleShowChargeDialog() {
        mutableStateFlow.update {
            it.copy(dialogState = SavingsAccountState.DialogState.ShowCharges)
        }
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
        mutableStateFlow.update {
            it.copy(
                currencyIndex = action.index,
                currencyError = null,
            )
        }
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
                    screenState = SavingsAccountState.ScreenState.Error(result.message),
                )
            }

            is DataState.Success -> mutableStateFlow.update {
                it.copy(
                    dialogState = null,
                    screenState = SavingsAccountState.ScreenState.Success,
                    savingsProductTemplate = result.data,
                    currencyIndex = result.data.currencyOptions?.indexOf(result.data.currency)
                        ?: -1,
                    decimalPlaces = result.data.currency?.decimalPlaces?.toInt().toString(),
                    interestPostingPeriodIndex = result.data.interestPostingPeriodTypeOptions?.indexOf(
                        result.data.interestPostingPeriodType,
                    ) ?: -1,
                    interestCalcIndex = result.data.interestCalculationTypeOptions?.indexOf(result.data.interestCalculationType)
                        ?: -1,
                    interestCompPeriodIndex = result.data.interestCompoundingPeriodTypeOptions?.indexOf(
                        result.data.interestCompoundingPeriodType,
                    ) ?: -1,
                    daysInYearIndex = result.data.interestCalculationDaysInYearTypeOptions?.indexOf(
                        result.data.interestCalculationDaysInYearType,
                    ) ?: -1,
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
        mutableStateFlow.update {
            it.copy(
                decimalPlaces = action.value,
                decimalPlacesError = null,
            )
        }
    }

    private fun handleFieldOfficerChange(action: SavingsAccountAction.OnFieldOfficerChange) {
        mutableStateFlow.update {
            it.copy(
                fieldOfficerIndex = action.index,
            )
        }
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
                    screenState = SavingsAccountState.ScreenState.Error(result.message),
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
        mutableStateFlow.update {
            it.copy(
                savingsProductSelected = action.index,
                savingProductError = null,
            )
        }
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

    private fun loadClientTemplate() = viewModelScope.launch {
        isOnline {
            getClientTemplateUseCase().collect { result ->
                sendAction(SavingsAccountAction.Internal.OnReceivingClientTemplate(result))
            }
        }
    }

    private fun loadSavingsProductTemplate() = viewModelScope.launch {
        isOnline {
            getSavingsProductTemplateUseCase().collect { result ->
                sendAction(SavingsAccountAction.Internal.OnReceivingSavingsProductTemplate(result))
            }
            loadClientTemplate()
        }
    }

    private fun handleRetry() {
        loadSavingsProductTemplate()
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
    val screenState: ScreenState = ScreenState.Loading,
    val submissionDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val showSubmissionDatePick: Boolean = false,
    val decimalPlaces: String = "",
    val decimalPlacesError: String? = null,
    val minimumOpeningBalance: String = "",
    val frequency: String = "",
    val monthlyMinimumBalance: String = "",
    val isCheckedApplyWithdrawalFee: Boolean = false,
    val isCheckedOverdraftAllowed: Boolean = false,
    val isCheckedMinimumBalance: Boolean = false,
    val savingsProductTemplate: SavingProductsTemplate? = null,
    val currencyIndex: Int = -1,
    val currencyError: String? = null,
    val interestCompPeriodIndex: Int = -1,
    val interestPostingPeriodIndex: Int = -1,
    val interestCalcIndex: Int = -1,
    val daysInYearIndex: Int = -1,
    val freqTypeIndex: Int = -1,

    val chooseChargeIndex: Int = -1,
    val addedCharges: List<CreatedCharges> = emptyList(),
    val chargeDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val showChargesDatePick: Boolean = false,
    val chargeAmount: String = "",
    val chargeAmountError: StringResource? = null,

    val launchEffectKey: Int? = null,

    val savingProductError: String? = null,
) {
    sealed interface DialogState {
        data object ShowCharges : DialogState
        data class AddNewCharge(val edit: Boolean, val index: Int = -1) : DialogState
        data class SuccessResponseStatus(val successStatus: Boolean, val msg: String = "") :
            DialogState
    }

    sealed interface ScreenState {
        data object Loading : ScreenState
        data object Success : ScreenState
        data class Error(val message: String) : ScreenState
    }
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
    data object SubmitSavingsApplication : SavingsAccountAction
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
    data object ShowAddChargeDialog : SavingsAccountAction
    data object ShowCharges : SavingsAccountAction
    data class EditCharge(val index: Int) : SavingsAccountAction
    data class OnChooseChargeIndexChange(val index: Int) : SavingsAccountAction
    data object DismissDialog : SavingsAccountAction
    data class OnChargesDatePick(val state: Boolean) : SavingsAccountAction
    data class OnChargesDateChange(val date: String) : SavingsAccountAction
    data class OnChargesAmountChange(val amount: String) : SavingsAccountAction
    data class OnChargesAmountChangeError(val error: StringResource?) : SavingsAccountAction
    data object AddChargeToList : SavingsAccountAction
    data class DeleteChargeFromSelectedCharges(val index: Int) : SavingsAccountAction
    data class EditChargeDialog(val index: Int) : SavingsAccountAction
    data object OnTermSubmit : SavingsAccountAction

    sealed interface Internal : SavingsAccountAction {
        data class OnReceivingClientTemplate(val clientTemplate: DataState<ClientsTemplateEntity>) :
            Internal

        data class OnReceivingSavingsProductTemplate(val savingsProductTemplate: DataState<SavingProductsTemplate>) :
            Internal
    }
}

data class CreatedCharges(
    val id: Int? = -1,
    val name: String?,
    val date: String,
    val type: String?,
    val amount: Double? = 0.0,
    val collectedOn: String = "",
)
