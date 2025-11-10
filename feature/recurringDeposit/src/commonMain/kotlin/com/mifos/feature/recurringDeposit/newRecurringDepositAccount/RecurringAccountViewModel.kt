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

import androidclient.feature.recurringdeposit.generated.resources.Res
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_deposit_amount_is_required
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_lock_in_period_frequency_is_required
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_no_internet_connection
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_recurring_frequency_is_required
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.RecurringAccountRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.payloads.RecurringDepositAccountPayload
import com.mifos.core.model.objects.template.recurring.FieldOfficerOption
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountState.ScreenState
import com.mifos.room.entities.templates.recurringDeposit.RecurringDepositAccountTemplate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

const val TOTAL_STEPS = 4

class RecurringAccountViewModel(
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val recurringAccountRepo: RecurringAccountRepository,
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

    private fun setLoadingState() {
        mutableStateFlow.update {
            it.copy(
                screenState = ScreenState.Loading,
            )
        }
    }
    private fun setSuccessState() {
        mutableStateFlow.update {
            it.copy(screenState = ScreenState.Success)
        }
    }

    private fun setErrorState(message: String) {
        mutableStateFlow.update {
            it.copy(
                screenState = ScreenState.Error(message),
            )
        }
    }

    private fun moveToNextStep() {
        if (state.currentStep < state.totalSteps) {
            mutableStateFlow.update {
                it.copy(
                    currentStep = state.currentStep + 1,
                )
            }
        } else {
            sendEvent(RecurringAccountEvent.Finish)
        }
    }

    private fun createRecurringDepositAccount() {
        viewModelScope.launch {
            val s = state
            val settings = s.recurringDepositAccountSettings

            val online = networkMonitor.isOnline.first()
            if (!online) {
                setErrorState(getString(Res.string.feature_recurring_deposit_no_internet_connection))
                return@launch
            }
            val lockInFreq = settings.lockInPeriod.frequency.toIntOrNull()
            val depositAmountInt = settings.recurringDepositDetails.depositAmount
                .filter(Char::isDigit)
                .toIntOrNull()
            val recurringFreq = settings.minimumDepositTerm.frequency.toIntOrNull()

            val payload = RecurringDepositAccountPayload(
                adjustAdvanceTowardsFuturePayments = settings.adjustAdvancePayments,
                allowWithdrawal = settings.allowWithdrawals,
                clientId = state.clientId,
                dateFormat = "dd MMMM yyyy",
                depositPeriod = settings.depositPeriod.period.toIntOrNull(),
                depositPeriodFrequencyId = settings.depositPeriod.periodType,
                expectedFirstDepositOnDate = null,
                externalId = s.recurringDepositAccountDetail.externalId,
                fieldOfficerId = s.recurringDepositAccountDetail.fieldOfficer?.id,
                interestCalculationDaysInYearType = null,
                interestCalculationType = null,
                interestCompoundingPeriodType = null,
                interestPostingPeriodType = null,
                isCalendarInherited = null,
                isMandatoryDeposit = settings.isMandatory,
                locale = "en",
                lockinPeriodFrequency = lockInFreq,
                lockinPeriodFrequencyType = settings.lockInPeriod.frequencyTypeIndex,
                mandatoryRecommendedDepositAmount = depositAmountInt,
                monthDayFormat = "dd MMMM",
                productId = s.recurringDepositAccountDetail.productId,
                recurringFrequency = recurringFreq,
                recurringFrequencyType = settings.minimumDepositTerm.frequencyTypeIndex,
                // date in dd MM yyyy format.
                submittedOnDate = s.recurringDepositAccountDetail.submittedOnDate,
            )

            if (state.isOnline) {
                recurringAccountRepo.createRecurringDepositAccount(payload).collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            if (depositAmountInt == null) {
                                setErrorState(getString(Res.string.feature_recurring_deposit_deposit_amount_is_required))
                            } else if (lockInFreq == null) {
                                setErrorState(getString(Res.string.feature_recurring_deposit_lock_in_period_frequency_is_required))
                            } else if (recurringFreq == null) {
                                setErrorState(getString(Res.string.feature_recurring_deposit_recurring_frequency_is_required))
                            } else {
                                setErrorState(dataState.message)
                            }
                        }

                        is DataState.Loading -> {
                            setLoadingState()
                        }

                        is DataState.Success -> {
                            setSuccessState()
                        }
                    }
                }
            }
        }
    }

    private fun handleProductNameChange(action: RecurringAccountAction.RecurringAccountDetailsAction.OnProductNameChange) {
        mutableStateFlow.update {
            it.copy(
                recurringDepositAccountDetail = it.recurringDepositAccountDetail.copy(
                    loanProductSelected = action.index,
                ),
            )
        }
        loadRecurringAccountTemplateWithProduct(
            state.clientId,
            state.template.productOptions?.get(state.recurringDepositAccountDetail.loanProductSelected)?.id ?: -1,
        )
    }

    private fun resetForRetry() {
        mutableStateFlow.update {
            it.copy(
                isOnline = false,
                clientId = -1,
                currentStep = 0,
                screenState = ScreenState.Loading,
                recurringDepositAccountDetail = RecurringAccountDetailsState(),
                template = RecurringDepositAccountTemplate(),
                recurringDepositAccountSettings = RecurringAccountSettingsState(),
                currencyIndex = -1,
                currencyError = null,
            )
        }
        loadRecurringAccountTemplate()
    }

    private fun handleFieldOfficerChange(action: RecurringAccountAction.RecurringAccountDetailsAction.OnFieldOfficerChange) {
        mutableStateFlow.update {
            it.copy(
                recurringDepositAccountDetail = it.recurringDepositAccountDetail.copy(
                    fieldOfficerIndex = action.index,
                    fieldOfficerError = null,
                ),
            )
        }
    }

    private fun handleSubmissionDatePick(action: RecurringAccountAction.RecurringAccountDetailsAction.OnSubmissionDatePick) {
        mutableStateFlow.update {
            it.copy(
                recurringDepositAccountDetail = it.recurringDepositAccountDetail.copy(
                    showSubmissionDatePick = action.state,
                ),
            )
        }
    }

    private fun handleSubmissionDateChange(action: RecurringAccountAction.RecurringAccountDetailsAction.OnSubmissionDateChange) {
        mutableStateFlow.update {
            it.copy(
                recurringDepositAccountDetail = it.recurringDepositAccountDetail.copy(
                    submissionDate = action.date,
                ),
            )
        }
    }

    private fun handleExternalIdChange(action: RecurringAccountAction.RecurringAccountDetailsAction.OnExternalIdChange) {
        mutableStateFlow.update {
            it.copy(
                recurringDepositAccountDetail = it.recurringDepositAccountDetail.copy(
                    externalId = action.value,
                ),
            )
        }
    }

    private fun loadRecurringAccountTemplate() = viewModelScope.launch {
        recurringAccountRepo.getRecurringAccountTemplate(clientId = state.clientId).collect { state ->
            when (state) {
                is DataState.Success -> {
                    setSuccessState()
                    mutableStateFlow.update {
                        it.copy(
                            template = state.data,
                            isOnline = true,
                        )
                    }
                }

                is DataState.Error -> {
                    setErrorState(state.message)
                }

                DataState.Loading -> {
                    setLoadingState()
                }
            }
        }
    }

    private fun loadRecurringAccountTemplateWithProduct(
        clientId: Int,
        productId: Int,
    ) = viewModelScope.launch {
        recurringAccountRepo.getRecurringAccountTemplate(clientId, productId).collect { state ->
            when (state) {
                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.Success,
                            template = state.data,
                            recurringDepositAccountDetail = it.recurringDepositAccountDetail.copy(
                                isMiniLoaderActive = false,
                            ),
                        )
                    }
                }

                is DataState.Error -> {
                    setErrorState(state.message)
                    mutableStateFlow.update {
                        it.copy(
                            recurringDepositAccountDetail = it.recurringDepositAccountDetail.copy(
                                isMiniLoaderActive = false,
                            ),
                        )
                    }
                }

                DataState.Loading -> {
                    setLoadingState()
                }
            }
        }
    }

    override fun handleAction(action: RecurringAccountAction) {
        when (action) {
            RecurringAccountAction.Retry -> {
                resetForRetry()
            }

            is RecurringAccountAction.RecurringAccountSettingsAction -> {
                when (action) {
                    is RecurringAccountAction.RecurringAccountSettingsAction.SetDepositPeriod -> {
                        mutableStateFlow.update { state ->
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    depositPeriod = state.recurringDepositAccountSettings.depositPeriod.copy(
                                        period = action.period,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetDepositPeriodType -> {
                        mutableStateFlow.update { state ->
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    depositPeriod = state.recurringDepositAccountSettings.depositPeriod.copy(
                                        periodType = state.template.periodFrequencyTypeOptions?.get(
                                            action.periodType,
                                        )?.id ?: -1,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetLockInPeriod -> {
                        mutableStateFlow.update { state ->
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    lockInPeriod = state.recurringDepositAccountSettings.lockInPeriod.copy(
                                        frequency = action.frequency,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetLockInPeriodType -> {
                        mutableStateFlow.update { state ->
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    lockInPeriod = state.recurringDepositAccountSettings.lockInPeriod.copy(
                                        frequencyTypeIndex = state.template.lockinPeriodFrequencyTypeOptions?.get(
                                            action.frequencyTypeIndex,
                                        )?.id ?: -1,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreq -> {
                        mutableStateFlow.update { state ->
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    minimumDepositTerm = state.recurringDepositAccountSettings.minimumDepositTerm.copy(
                                        frequency = action.frequency,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreqType -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    minimumDepositTerm = state.recurringDepositAccountSettings.minimumDepositTerm.copy(
                                        frequencyTypeIndex = state.template.periodFrequencyTypeOptions?.get(
                                            action.frequencyTypeIndex,
                                        )?.id ?: -1,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreqAfterInMultiOf -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    minimumDepositTerm = state.recurringDepositAccountSettings.minimumDepositTerm.copy(
                                        frequencyAfterInMultiplesOf = action.frequencyAfterInMultiplesOf,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreqTypeAfterInMultiOf -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    minimumDepositTerm = state.recurringDepositAccountSettings.minimumDepositTerm.copy(
                                        frequencyTypeIndexAfterInMultiplesOf = state.template.periodFrequencyTypeOptions?.get(
                                            action.frequencyTypeIndexAfterInMultiplesOf,
                                        )?.id ?: -1,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetMaxDepositTermFreq -> {
                        mutableStateFlow.update { state ->
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    maxDepositTerm = state.recurringDepositAccountSettings.maxDepositTerm.copy(
                                        frequency = action.frequency,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetMaxDepositTermFreqType -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    maxDepositTerm = state.recurringDepositAccountSettings.maxDepositTerm.copy(
                                        frequencyTypeIndex = state.template.periodFrequencyTypeOptions?.get(
                                            action.frequencyTypeIndex,
                                        )?.id ?: -1,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetPreMatureClosureInterestPeriodIndex -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    preMatureClosure = state.recurringDepositAccountSettings.preMatureClosure.copy(
                                        interestPeriodIndex = state.template.preClosurePenalInterestOnTypeOptions?.get(
                                            action.interestPeriodIndex,
                                        )?.id ?: -1,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetPreMatureClosureMinimumBalanceForInterestCalculation -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    preMatureClosure = state.recurringDepositAccountSettings.preMatureClosure.copy(
                                        minimumBalanceForInterestCalculation = action.minimumBalanceForInterestCalculation,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetPreMatureClosurePenalInterest -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    preMatureClosure = state.recurringDepositAccountSettings.preMatureClosure.copy(
                                        penalInterest = action.penalInterest,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetRecurringDepositAmount -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    recurringDepositDetails = state.recurringDepositAccountSettings
                                        .recurringDepositDetails.copy(
                                            depositAmount = action.depositAmount,
                                        ),
                                ),
                            )
                        }
                    }

                    RecurringAccountAction.RecurringAccountSettingsAction.ToggleAdvancePaymentsTowardsFutureInstallments -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    adjustAdvancePayments = !state.recurringDepositAccountSettings.adjustAdvancePayments,
                                ),
                            )
                        }
                    }

                    RecurringAccountAction.RecurringAccountSettingsAction.ToggleAllowWithdrawals -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    allowWithdrawals = !state.recurringDepositAccountSettings.allowWithdrawals,
                                ),
                            )
                        }
                    }

                    RecurringAccountAction.RecurringAccountSettingsAction.ToggleDepositFrequencySameAsGroupCenterMeeting -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    depositPeriod = state.recurringDepositAccountSettings.depositPeriod.copy(
                                        depositFrequencySameAsGroupCenterMeeting = !state.recurringDepositAccountSettings
                                            .depositPeriod.depositFrequencySameAsGroupCenterMeeting,
                                    ),
                                ),
                            )
                        }
                    }

                    RecurringAccountAction.RecurringAccountSettingsAction.ToggleMandatoryDeposit -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    isMandatory = !state.recurringDepositAccountSettings.isMandatory,
                                ),
                            )
                        }
                    }

                    RecurringAccountAction.RecurringAccountSettingsAction.TogglePreMatureClosureApplyPenalInterest -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    preMatureClosure = state.recurringDepositAccountSettings.preMatureClosure.copy(
                                        applyPenalInterest = !state.recurringDepositAccountSettings
                                            .preMatureClosure.applyPenalInterest,
                                    ),
                                ),
                            )
                        }
                    }
                }
            }

            is RecurringAccountAction.RecurringAccountDetailsAction -> {
                when (action) {
                    is RecurringAccountAction.RecurringAccountDetailsAction.OnProductNameChange -> {
                        handleProductNameChange(action)
                    }
                    is RecurringAccountAction.RecurringAccountDetailsAction.OnSubmissionDateChange -> {
                        handleSubmissionDateChange(action)
                    }
                    is RecurringAccountAction.RecurringAccountDetailsAction.OnSubmissionDatePick -> {
                        handleSubmissionDatePick(action)
                    }
                    is RecurringAccountAction.RecurringAccountDetailsAction.OnFieldOfficerChange -> {
                        handleFieldOfficerChange(action)
                    }
                    is RecurringAccountAction.RecurringAccountDetailsAction.OnExternalIdChange -> {
                        handleExternalIdChange(action)
                    }
                }
            }

            RecurringAccountAction.NavigateBack -> {
                sendEvent(RecurringAccountEvent.NavigateBack)
            }

            is RecurringAccountAction.NavigateToStep -> {
                val newIndex = action.index
                if (newIndex in 0..state.totalSteps) {
                    mutableStateFlow.update {
                        it.copy(currentStep = newIndex)
                    }
                }
            }

            RecurringAccountAction.OnBackPress -> {
                mutableStateFlow.update {
                    it.copy(currentStep = 3)
                }
            }

            RecurringAccountAction.OnNextPress -> {
                moveToNextStep()
            }
        }
    }
}

data class RecurringAccountState(
    val isOnline: Boolean = false,
    val clientId: Int = -1,
    val currentStep: Int = 0,
    val totalSteps: Int = TOTAL_STEPS,
    val screenState: ScreenState = ScreenState.Loading,
    val recurringDepositAccountDetail: RecurringAccountDetailsState = RecurringAccountDetailsState(),
    val template: RecurringDepositAccountTemplate = RecurringDepositAccountTemplate(),
    val recurringDepositAccountSettings: RecurringAccountSettingsState = RecurringAccountSettingsState(),
    val currencyIndex: Int = -1,
    val currencyError: String? = null,
) {
    sealed interface ScreenState {
        data class Error(val message: String) : ScreenState
        data object Loading : ScreenState
        data object Success : ScreenState
    }
}

data class RecurringAccountDetailsState(
    val productId: Int = -1,
    val submittedOnDate: String = "",
    val fieldOfficer: FieldOfficerOption? = null,
    val showSubmissionDatePick: Boolean = false,
    val loanProductSelected: Int = -1,
    val submissionDate: String = "",
    val fieldOfficerIndex: Int = -1,
    val fieldOfficerError: String? = null,
    val externalId: String = "",
    val isMiniLoaderActive: Boolean = false,
    val fieldOfficerOptions: List<FieldOfficerOption>? = null,
) {
    val isDetailButtonEnabled = fieldOfficerIndex != -1 && submissionDate.isNotEmpty()
}

data class RecurringAccountSettingsState(
    val canDoNext: Boolean = false,
    val isMandatory: Boolean = false,
    val adjustAdvancePayments: Boolean = false,
    val allowWithdrawals: Boolean = false,
    val lockInPeriod: LockInPeriod = LockInPeriod(),
    val recurringDepositDetails: RecurringDepositDetails = RecurringDepositDetails(),
    val depositPeriod: DepositPeriod = DepositPeriod(),
    val minimumDepositTerm: MinimumDepositTerm = MinimumDepositTerm(),
    val maxDepositTerm: MaxDepositTerm = MaxDepositTerm(),
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

    data class MaxDepositTerm(
        val frequency: String = "",
        val frequencyTypeIndex: Int = -1,
        val frequencyTypeError: String? = null,
    )

    data class PreMatureClosure(
        val applyPenalInterest: Boolean = false,
        val penalInterest: String = "",
        val interestPeriodIndex: Int = -1,
        val interestPeriodIndexError: String? = null,
        val minimumBalanceForInterestCalculation: String = "",
    )

    val isSettingsNextEnabled = preMatureClosure.penalInterest.isNotBlank() &&
        preMatureClosure.minimumBalanceForInterestCalculation.isNotBlank() &&
        recurringDepositDetails.depositAmount.isNotBlank() &&
        depositPeriod.period.isNotBlank() &&
        lockInPeriod.frequency.isNotBlank() &&
        minimumDepositTerm.frequency.isNotBlank() &&
        minimumDepositTerm.frequencyAfterInMultiplesOf.isNotBlank() &&
        maxDepositTerm.frequency.isNotBlank()
}

sealed class RecurringAccountAction {
    data class NavigateToStep(val index: Int) : RecurringAccountAction()
    object NavigateBack : RecurringAccountAction()
    object OnBackPress : RecurringAccountAction()
    object OnNextPress : RecurringAccountAction()
    data object Retry : RecurringAccountAction()

    sealed class RecurringAccountDetailsAction : RecurringAccountAction() {
        data class OnProductNameChange(val index: Int) : RecurringAccountDetailsAction()
        data class OnSubmissionDateChange(val date: String) : RecurringAccountDetailsAction()
        data class OnSubmissionDatePick(val state: Boolean) : RecurringAccountDetailsAction()
        data class OnFieldOfficerChange(val index: Int) : RecurringAccountDetailsAction()
        data class OnExternalIdChange(val value: String) : RecurringAccountDetailsAction()
    }

    sealed class RecurringAccountSettingsAction : RecurringAccountAction() {
        object ToggleMandatoryDeposit : RecurringAccountSettingsAction()
        object ToggleAdvancePaymentsTowardsFutureInstallments : RecurringAccountSettingsAction()
        object ToggleAllowWithdrawals : RecurringAccountSettingsAction()
        data class SetLockInPeriod(val frequency: String) : RecurringAccountSettingsAction()
        data class SetLockInPeriodType(val frequencyTypeIndex: Int) : RecurringAccountSettingsAction()
        data class SetRecurringDepositAmount(val depositAmount: String) : RecurringAccountSettingsAction()
        data class SetDepositPeriod(val period: String) : RecurringAccountSettingsAction()
        data class SetDepositPeriodType(val periodType: Int) : RecurringAccountSettingsAction()
        data object ToggleDepositFrequencySameAsGroupCenterMeeting : RecurringAccountSettingsAction()

        data class SetMinDepositTermFreq(val frequency: String) : RecurringAccountSettingsAction()
        data class SetMinDepositTermFreqType(val frequencyTypeIndex: Int) : RecurringAccountSettingsAction()
        data class SetMinDepositTermFreqAfterInMultiOf(val frequencyAfterInMultiplesOf: String) : RecurringAccountSettingsAction()
        data class SetMinDepositTermFreqTypeAfterInMultiOf(val frequencyTypeIndexAfterInMultiplesOf: Int) : RecurringAccountSettingsAction()

        data class SetMaxDepositTermFreq(val frequency: String) : RecurringAccountSettingsAction()
        data class SetMaxDepositTermFreqType(val frequencyTypeIndex: Int) : RecurringAccountSettingsAction()
        data object TogglePreMatureClosureApplyPenalInterest : RecurringAccountSettingsAction()
        data class SetPreMatureClosurePenalInterest(val penalInterest: String) : RecurringAccountSettingsAction()
        data class SetPreMatureClosureInterestPeriodIndex(val interestPeriodIndex: Int) : RecurringAccountSettingsAction()
        data class SetPreMatureClosureMinimumBalanceForInterestCalculation(val minimumBalanceForInterestCalculation: String) : RecurringAccountSettingsAction()
    }
}

sealed class RecurringAccountEvent {
    object NavigateBack : RecurringAccountEvent()
    object Finish : RecurringAccountEvent()
}
