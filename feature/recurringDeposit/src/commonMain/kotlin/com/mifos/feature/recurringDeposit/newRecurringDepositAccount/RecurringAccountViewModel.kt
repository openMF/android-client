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
import androidclient.feature.recurringdeposit.generated.resources.feature_error_network_not_available
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_account_created_successfully
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.RecurringAccountRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.payloads.ChargeItem
import com.mifos.core.model.objects.payloads.RecurringDepositAccountPayload
import com.mifos.core.model.objects.template.recurring.FieldOfficerOption
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.core.ui.util.TextFieldsValidator
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountState.ScreenState
import com.mifos.room.entities.templates.recurringDeposit.RecurringDepositAccountTemplate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

const val TOTAL_STEPS = 6

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

    private fun moveToNextStep() {
        if (state.currentStep < state.totalSteps) {
            mutableStateFlow.update {
                it.copy(
                    currentStep = state.currentStep + 1,
                    dialogState = null,
                )
            }
        } else {
            sendEvent(RecurringAccountEvent.Finish)
        }
    }

    private fun createRecurringDepositAccount() {
        val s = state
        val settings = s.recurringDepositAccountSettings

        val payload = RecurringDepositAccountPayload(
            charges = state.addedCharges,
            adjustAdvanceTowardsFuturePayments = settings.adjustAdvancePayments,
            allowWithdrawal = settings.allowWithdrawals,
            clientId = state.clientId,
            dateFormat = DateHelper.SHORT_MONTH,
            depositPeriod = settings.depositPeriod.period.toIntOrNull(),
            depositPeriodFrequencyId = (
                state.template.periodFrequencyTypeOptions?.getOrNull(
                    state.recurringDepositAccountSettings.depositPeriod.periodType,
                )?.id
                ),
            expectedFirstDepositOnDate = null,
            externalId = s.recurringDepositAccountDetail.externalId,
            fieldOfficerId = if (state.recurringDepositAccountDetail.fieldOfficerIndex != -1) {
                (
                    state.template.fieldOfficerOptions?.get(
                        state.recurringDepositAccountDetail.fieldOfficerIndex,
                    )?.id
                    )
            } else {
                null
            },
            interestCalculationDaysInYearType = if (state.recurringDepositAccountInterestChart.interestCalculationDaysInYearType != -1) {
                state.template.interestCalculationDaysInYearTypeOptions?.get(
                    state.recurringDepositAccountInterestChart.interestCalculationDaysInYearType,
                )?.id
            } else {
                null
            },
            interestCalculationType = if (state.recurringDepositAccountInterestChart.interestCalculationType != -1) {
                state.template.interestCalculationTypeOptions?.get(
                    state.recurringDepositAccountInterestChart.interestCalculationType,
                )?.id
            } else {
                null
            },
            interestCompoundingPeriodType = if (state.recurringDepositAccountInterestChart.interestCompoundingPeriodType != -1) {
                (
                    state.template.interestCompoundingPeriodTypeOptions?.get(
                        state.recurringDepositAccountInterestChart.interestCompoundingPeriodType,
                    )?.id
                    )
            } else {
                null
            },
            interestPostingPeriodType = if (state.recurringDepositAccountInterestChart.interestPostingPeriodType != -1) {
                state.template.interestPostingPeriodTypeOptions?.get(
                    state.recurringDepositAccountInterestChart.interestPostingPeriodType,
                )?.id
            } else {
                null
            },
            isCalendarInherited = settings.depositPeriod.depositFrequencySameAsGroupCenterMeeting,
            isMandatoryDeposit = settings.isMandatory,
            locale = Constants.LOCALE_EN,
            lockinPeriodFrequency = settings.lockInPeriod.frequency.toIntOrNull(),
            lockinPeriodFrequencyType = if (state.recurringDepositAccountSettings.lockInPeriod.frequencyTypeIndex != -1) {
                (
                    state.template.lockinPeriodFrequencyTypeOptions?.getOrNull(
                        state.recurringDepositAccountSettings.lockInPeriod.frequencyTypeIndex,
                    )?.id
                    )
            } else {
                null
            },
            mandatoryRecommendedDepositAmount = settings.recurringDepositDetails.depositAmount.toDoubleOrNull(),
            productId = state.template.productOptions?.get(state.recurringDepositAccountDetail.loanProductSelected)?.id,
            recurringFrequency = settings.recurringFrequency.toIntOrNull(),
            recurringFrequencyType = state.template.periodFrequencyTypeOptions
                ?.getOrNull(settings.recurringFrequencyTypeIndex)?.id,
            submittedOnDate = s.recurringDepositAccountDetail.submissionDate,
            preClosurePenalApplicable = settings.preMatureClosure.applyPenalInterest,
            preClosurePenalInterest = settings.preMatureClosure.penalInterest.toDoubleOrNull(),
            preClosurePenalInterestOnTypeId = if (state.recurringDepositAccountSettings.preMatureClosure.interestPeriodIndex != -1) {
                state.template.preClosurePenalInterestOnTypeOptions?.getOrNull(
                    state.recurringDepositAccountSettings.preMatureClosure.interestPeriodIndex,
                )?.id
            } else {
                null
            },
        )

        viewModelScope.launch {
            isOnline {
                recurringAccountRepo.createRecurringDepositAccount(payload).collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            if (dataState.exception is IllegalStateException) {
                                mutableStateFlow.update {
                                    it.copy(
                                        dialogState = RecurringAccountState.DialogState.SuccessResponseStatus(
                                            successStatus = false,
                                            msg = dataState.message,
                                        ),
                                        launchEffectKey = Random.nextInt(),
                                        isOverlayLoadingActive = false,
                                    )
                                }
                            } else {
                                mutableStateFlow.update {
                                    it.copy(
                                        screenState = RecurringAccountState.ScreenState.Error(
                                            dataState.message,
                                        ),
                                        isOverlayLoadingActive = false,
                                    )
                                }
                            }
                        }

                        is DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    isOverlayLoadingActive = true,
                                )
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    isOverlayLoadingActive = false,
                                    launchEffectKey = Random.nextInt(),
                                    dialogState = RecurringAccountState.DialogState.SuccessResponseStatus(
                                        successStatus = true,
                                        msg = getString(Res.string.feature_recurring_account_created_successfully),
                                    ),
                                )
                            }
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
                    productError = null,
                ),
            )
        }
        loadRecurringAccountTemplateWithProduct(
            state.clientId,
            state.template.productOptions?.get(state.recurringDepositAccountDetail.loanProductSelected)?.id
                ?: -1,
        )
    }

    private fun handleInterestCalculationDaysInYearType(action: RecurringAccountAction.RecurringAccountTermAction.OnInterestCalculationDaysInYearType) {
        mutableStateFlow.update {
            it.copy(
                recurringDepositAccountInterestChart = it.recurringDepositAccountInterestChart.copy(
                    interestCalculationDaysInYearType = action.interestCalculationTypeDaysInYear,
                ),

            )
        }
    }

    private fun handleInterestCalculationType(action: RecurringAccountAction.RecurringAccountTermAction.OnInterestCalculationType) {
        mutableStateFlow.update {
            it.copy(
                recurringDepositAccountInterestChart = it.recurringDepositAccountInterestChart.copy(
                    interestCalculationType = action.interestCalculationType,
                ),
            )
        }
    }

    private fun handleInterestCompoundingPeriodType(action: RecurringAccountAction.RecurringAccountTermAction.OnInterestCompoundingPeriodType) {
        mutableStateFlow.update {
            it.copy(
                recurringDepositAccountInterestChart = it.recurringDepositAccountInterestChart.copy(
                    interestCompoundingPeriodType = action.interestCompoundingPeriodType,
                ),
            )
        }
    }

    private fun handleInterestPostingPeriodType(action: RecurringAccountAction.RecurringAccountTermAction.OnInterestPostingPeriodType) {
        mutableStateFlow.update {
            it.copy(
                recurringDepositAccountInterestChart = it.recurringDepositAccountInterestChart.copy(
                    interestPostingPeriodType = action.interestPostingPeriodType,
                ),
            )
        }
    }

    private fun retry() {
        loadRecurringAccountTemplate()
    }

    private fun handleFieldOfficerChange(action: RecurringAccountAction.RecurringAccountDetailsAction.OnFieldOfficerChange) {
        mutableStateFlow.update {
            it.copy(
                recurringDepositAccountDetail = it.recurringDepositAccountDetail.copy(
                    fieldOfficerIndex = action.index,
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
        isOnline {
            recurringAccountRepo.getRecurringAccountTemplate(clientId = state.clientId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(
                                    template = dataState.data,
                                    screenState = ScreenState.Success,
                                )
                            }
                        }

                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = ScreenState.Error(dataState.message),
                                )
                            }
                        }

                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    screenState = ScreenState.Loading,
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun loadRecurringAccountTemplateWithProduct(
        clientId: Int,
        productId: Int,
    ) = viewModelScope.launch {
        isOnline {
            recurringAccountRepo.getRecurringAccountTemplate(clientId, productId).collect { state ->
                when (state) {
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = ScreenState.Success,
                                template = state.data,
                                isOverlayLoadingActive = false,
                            )
                        }
                    }

                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = ScreenState.Error(state.message),
                                isOverlayLoadingActive = false,
                            )
                        }
                    }

                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                isOverlayLoadingActive = true,
                            )
                        }
                    }
                }
            }
        }
    }

    suspend fun isOnline(
        content: suspend () -> Unit,
    ) {
        if (networkMonitor.isOnline.first()) {
            content()
        } else {
            mutableStateFlow.update {
                it.copy(
                    screenState = ScreenState.Error(getString(Res.string.feature_error_network_not_available)),
                )
            }
        }
    }

    override fun handleAction(action: RecurringAccountAction) {
        when (action) {
            RecurringAccountAction.Retry -> {
                retry()
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
                                    depositPeriodError = null,
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
                                    depositPeriodTypeError = null,
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
                                    recurringDepositDetails = state.recurringDepositAccountSettings.recurringDepositDetails.copy(
                                        depositAmount = action.depositAmount,
                                    ),
                                    depositAmountError = null,
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
                                        depositFrequencySameAsGroupCenterMeeting = !state.recurringDepositAccountSettings.depositPeriod.depositFrequencySameAsGroupCenterMeeting,
                                    ),
                                    recurringFrequencyTypeError = null,
                                    recurringFrequencyError = null,
                                    recurringFrequency = "",
                                    recurringFrequencyTypeIndex = -1,
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
                                        applyPenalInterest = !state.recurringDepositAccountSettings.preMatureClosure.applyPenalInterest,
                                    ),
                                ),
                            )
                        }
                    }

                    RecurringAccountAction.RecurringAccountSettingsAction.OnSettingNext -> {
                        val depositAmountError =
                            TextFieldsValidator.doubleNumberValidator(state.recurringDepositAccountSettings.recurringDepositDetails.depositAmount)
                        val depositPeriodTypeError = TextFieldsValidator.dropDownEmptyValidator(
                            state.recurringDepositAccountSettings.depositPeriod.periodType == -1,
                        )
                        val depositPeriodError =
                            TextFieldsValidator.numberValidator(state.recurringDepositAccountSettings.depositPeriod.period)

                        val recurringFrequencyError =
                            TextFieldsValidator.numberValidator(state.recurringDepositAccountSettings.recurringFrequency)
                        val recurringFrequencyTypeError =
                            TextFieldsValidator.dropDownEmptyValidator(state.recurringDepositAccountSettings.recurringFrequencyTypeIndex == -1)

                        val recurringErrorHandle =
                            (recurringFrequencyError != null || recurringFrequencyTypeError != null) && !state.recurringDepositAccountSettings.depositPeriod.depositFrequencySameAsGroupCenterMeeting

                        if (depositAmountError != null || depositPeriodTypeError != null || depositPeriodError != null || recurringErrorHandle) {
                            mutableStateFlow.update {
                                it.copy(
                                    recurringDepositAccountSettings = it.recurringDepositAccountSettings.copy(
                                        depositPeriodError = depositPeriodError,
                                        depositAmountError = depositAmountError,
                                        depositPeriodTypeError = depositPeriodTypeError,
                                        recurringFrequencyError = if (state.recurringDepositAccountSettings.depositPeriod.depositFrequencySameAsGroupCenterMeeting) null else recurringFrequencyError,
                                        recurringFrequencyTypeError = if (state.recurringDepositAccountSettings.depositPeriod.depositFrequencySameAsGroupCenterMeeting) null else recurringFrequencyTypeError,
                                    ),
                                )
                            }
                        } else {
                            moveToNextStep()
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.OnRecurringFrequencyChange -> {
                        mutableStateFlow.update {
                            it.copy(
                                recurringDepositAccountSettings = it.recurringDepositAccountSettings.copy(
                                    recurringFrequency = action.value,
                                    recurringFrequencyError = null,
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.OnRecurringFrequencyTypeIndexChange -> {
                        mutableStateFlow.update {
                            it.copy(
                                recurringDepositAccountSettings = it.recurringDepositAccountSettings.copy(
                                    recurringFrequencyTypeIndex = action.index,
                                    recurringFrequencyTypeError = null,
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

                    RecurringAccountAction.RecurringAccountDetailsAction.OnDetailNext -> {
                        val productError = TextFieldsValidator.dropDownEmptyValidator(
                            state.recurringDepositAccountDetail.loanProductSelected == -1,
                        )

                        if (productError != null) {
                            mutableStateFlow.update {
                                it.copy(
                                    recurringDepositAccountDetail = it.recurringDepositAccountDetail.copy(
                                        productError = productError,
                                    ),
                                )
                            }
                        } else {
                            moveToNextStep()
                        }
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
                val current = state.currentStep
                mutableStateFlow.update {
                    it.copy(
                        currentStep = current - 1,
                    )
                }
            }

            RecurringAccountAction.OnNextPress -> {
                moveToNextStep()
            }

            is RecurringAccountAction.RecurringAccountTermAction.OnInterestCalculationDaysInYearType -> {
                handleInterestCalculationDaysInYearType(action)
            }

            is RecurringAccountAction.RecurringAccountTermAction.OnInterestCalculationType -> {
                handleInterestCalculationType(action)
            }

            is RecurringAccountAction.RecurringAccountTermAction.OnInterestCompoundingPeriodType -> {
                handleInterestCompoundingPeriodType(action)
            }

            is RecurringAccountAction.RecurringAccountTermAction.OnInterestPostingPeriodType -> {
                handleInterestPostingPeriodType(action)
            }

            RecurringAccountAction.OnDismissDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }
            }

            RecurringAccountAction.OnShowRateChartDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = RecurringAccountState.DialogState.RateChartDialog,
                    )
                }
            }

            is RecurringAccountAction.DeleteChargeFromSelectedCharges -> {
                val newCharges = state.addedCharges.toMutableList().apply {
                    removeAt(action.index)
                }
                mutableStateFlow.update {
                    it.copy(addedCharges = newCharges)
                }
            }

            is RecurringAccountAction.EditCharge -> {
                val createdData = ChargeItem(
                    chargeId = state.template.chargeOptions?.get(action.index)?.id,
                    amount = state.chargeAmount.toDoubleOrNull(),
                )

                val currentAddedCharges = state.addedCharges.toMutableList()
                currentAddedCharges[action.index] = createdData
                mutableStateFlow.update {
                    it.copy(
                        addedCharges = currentAddedCharges,
                        chooseChargeIndex = null,
                        dialogState = RecurringAccountState.DialogState.ShowCharges,
                        chargeAmount = "",
                    )
                }
            }

            is RecurringAccountAction.EditChargeDialog -> {
                val selectedEditCharge = state.addedCharges[action.index]
                val chooseChargeIndex =
                    state.template.chargeOptions?.indexOfFirst { it.id == selectedEditCharge.chargeId }

                mutableStateFlow.update {
                    it.copy(
                        chargeAmount = selectedEditCharge.amount.toString(),
                        chooseChargeIndex = chooseChargeIndex,
                        dialogState = RecurringAccountState.DialogState.AddNewCharge(
                            true,
                            action.index,
                        ),
                    )
                }
            }

            is RecurringAccountAction.OnChargeAmountChange -> {
                mutableStateFlow.update {
                    it.copy(
                        chargeAmount = action.amount,
                    )
                }
            }

            is RecurringAccountAction.OnChooseChargeIndexChange -> {
                mutableStateFlow.update {
                    it.copy(
                        chooseChargeIndex = action.index,
                    )
                }
            }

            RecurringAccountAction.ShowAddChargeDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = RecurringAccountState.DialogState.AddNewCharge(false),
                    )
                }
            }

            RecurringAccountAction.ShowListOfChargesDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = RecurringAccountState.DialogState.ShowCharges,
                    )
                }
            }

            RecurringAccountAction.AddChargeToList -> {
                val createdData = ChargeItem(
                    chargeId = state.template.chargeOptions?.get(state.chooseChargeIndex!!)?.id,
                    amount = state.chargeAmount.toDoubleOrNull(),
                )

                mutableStateFlow.update {
                    it.copy(
                        addedCharges = it.addedCharges + createdData,
                        chooseChargeIndex = null,
                        dialogState = null,
                        chargeAmount = "",
                    )
                }
            }

            RecurringAccountAction.OnSubmitRecurringAccount -> {
                createRecurringDepositAccount()
            }
        }
    }
}

data class RecurringAccountState(
    val clientId: Int = -1,
    val currentStep: Int = 0,
    val totalSteps: Int = TOTAL_STEPS,
    val screenState: ScreenState = ScreenState.Loading,
    val recurringDepositAccountDetail: RecurringAccountDetailsState = RecurringAccountDetailsState(),
    val template: RecurringDepositAccountTemplate = RecurringDepositAccountTemplate(),
    val recurringDepositAccountSettings: RecurringAccountSettingsState = RecurringAccountSettingsState(),
    val recurringDepositAccountInterestChart: RecurringAccountInterestChartState = RecurringAccountInterestChartState(),
    val currencyIndex: Int = -1,
    val currencyError: String? = null,
    val isOverlayLoadingActive: Boolean = false,
    val dialogState: DialogState? = null,
    val addedCharges: List<ChargeItem> = emptyList(),
    val chargeAmount: String = "",
    val chooseChargeIndex: Int? = null,
    val launchEffectKey: Int? = null,
) {
    sealed interface ScreenState {
        data class Error(val message: String) : ScreenState
        data object Loading : ScreenState
        data object Success : ScreenState
    }

    sealed interface DialogState {
        data object RateChartDialog : DialogState
        data class AddNewCharge(val edit: Boolean, val index: Int = -1) : DialogState
        data object ShowCharges : DialogState
        data class SuccessResponseStatus(val successStatus: Boolean, val msg: String = "") :
            DialogState
    }

    val isRateChartEmpty = !template.accountChart?.chartSlabs.isNullOrEmpty()
}

data class RecurringAccountDetailsState
@OptIn(ExperimentalTime::class)
constructor(
    val showSubmissionDatePick: Boolean = false,
    val loanProductSelected: Int = -1,
    val submissionDate: String = DateHelper.getDateAsStringFromLong(
        Clock.System.now().toEpochMilliseconds(),
    ),
    val fieldOfficerIndex: Int = -1,
    val externalId: String = "",
    val fieldOfficerOptions: List<FieldOfficerOption>? = null,

    val productError: StringResource? = null,
)

data class RecurringAccountInterestChartState(
    val interestCalculationDaysInYearType: Int = -1,
    val interestCalculationType: Int = -1,
    val interestCompoundingPeriodType: Int = -1,
    val interestPostingPeriodType: Int = -1,
)

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

    val depositAmountError: StringResource? = null,
    val depositPeriodTypeError: StringResource? = null,
    val depositPeriodError: StringResource? = null,

    val recurringFrequency: String = "",
    val recurringFrequencyTypeIndex: Int = -1,

    val recurringFrequencyError: StringResource? = null,
    val recurringFrequencyTypeError: StringResource? = null,
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
}

sealed class RecurringAccountAction {
    data class NavigateToStep(val index: Int) : RecurringAccountAction()
    object NavigateBack : RecurringAccountAction()
    object OnBackPress : RecurringAccountAction()
    object OnNextPress : RecurringAccountAction()
    data object Retry : RecurringAccountAction()
    object OnShowRateChartDialog : RecurringAccountAction()
    object OnDismissDialog : RecurringAccountAction()
    object ShowAddChargeDialog : RecurringAccountAction()
    object ShowListOfChargesDialog : RecurringAccountAction()
    object OnSubmitRecurringAccount : RecurringAccountAction()
    data class EditCharge(val index: Int) : RecurringAccountAction()
    data class OnChooseChargeIndexChange(val index: Int) : RecurringAccountAction()
    data class OnChargeAmountChange(val amount: String) : RecurringAccountAction()
    data class DeleteChargeFromSelectedCharges(val index: Int) : RecurringAccountAction()
    data class EditChargeDialog(val index: Int) : RecurringAccountAction()
    data object AddChargeToList : RecurringAccountAction()

    sealed class RecurringAccountDetailsAction : RecurringAccountAction() {
        data class OnProductNameChange(val index: Int) : RecurringAccountDetailsAction()
        data class OnSubmissionDateChange(val date: String) : RecurringAccountDetailsAction()
        data class OnSubmissionDatePick(val state: Boolean) : RecurringAccountDetailsAction()
        data class OnFieldOfficerChange(val index: Int) : RecurringAccountDetailsAction()
        data class OnExternalIdChange(val value: String) : RecurringAccountDetailsAction()
        object OnDetailNext : RecurringAccountDetailsAction()
    }

    sealed class RecurringAccountSettingsAction : RecurringAccountAction() {
        object ToggleMandatoryDeposit : RecurringAccountSettingsAction()
        object ToggleAdvancePaymentsTowardsFutureInstallments : RecurringAccountSettingsAction()
        object ToggleAllowWithdrawals : RecurringAccountSettingsAction()
        data class SetLockInPeriod(val frequency: String) : RecurringAccountSettingsAction()
        data class SetLockInPeriodType(val frequencyTypeIndex: Int) :
            RecurringAccountSettingsAction()

        data class SetRecurringDepositAmount(val depositAmount: String) :
            RecurringAccountSettingsAction()

        data class SetDepositPeriod(val period: String) : RecurringAccountSettingsAction()
        data class SetDepositPeriodType(val periodType: Int) : RecurringAccountSettingsAction()
        data object ToggleDepositFrequencySameAsGroupCenterMeeting :
            RecurringAccountSettingsAction()

        data class SetMinDepositTermFreq(val frequency: String) : RecurringAccountSettingsAction()
        data class SetMinDepositTermFreqType(val frequencyTypeIndex: Int) :
            RecurringAccountSettingsAction()

        data class SetMinDepositTermFreqAfterInMultiOf(val frequencyAfterInMultiplesOf: String) :
            RecurringAccountSettingsAction()

        data class SetMinDepositTermFreqTypeAfterInMultiOf(val frequencyTypeIndexAfterInMultiplesOf: Int) :
            RecurringAccountSettingsAction()

        data class SetMaxDepositTermFreq(val frequency: String) : RecurringAccountSettingsAction()
        data class SetMaxDepositTermFreqType(val frequencyTypeIndex: Int) :
            RecurringAccountSettingsAction()

        data object TogglePreMatureClosureApplyPenalInterest : RecurringAccountSettingsAction()
        data class SetPreMatureClosurePenalInterest(val penalInterest: String) :
            RecurringAccountSettingsAction()

        data class SetPreMatureClosureInterestPeriodIndex(val interestPeriodIndex: Int) :
            RecurringAccountSettingsAction()

        data class SetPreMatureClosureMinimumBalanceForInterestCalculation(val minimumBalanceForInterestCalculation: String) :
            RecurringAccountSettingsAction()

        object OnSettingNext : RecurringAccountSettingsAction()
        data class OnRecurringFrequencyChange(val value: String) : RecurringAccountSettingsAction()
        data class OnRecurringFrequencyTypeIndexChange(val index: Int) :
            RecurringAccountSettingsAction()
    }

    sealed class RecurringAccountTermAction : RecurringAccountAction() {
        data class OnInterestCalculationDaysInYearType(val interestCalculationTypeDaysInYear: Int) :
            RecurringAccountTermAction()

        data class OnInterestCompoundingPeriodType(val interestCompoundingPeriodType: Int) :
            RecurringAccountTermAction()

        data class OnInterestCalculationType(val interestCalculationType: Int) :
            RecurringAccountTermAction()

        data class OnInterestPostingPeriodType(val interestPostingPeriodType: Int) :
            RecurringAccountTermAction()
    }
}

sealed class RecurringAccountEvent {
    object NavigateBack : RecurringAccountEvent()
    object Finish : RecurringAccountEvent()
}
