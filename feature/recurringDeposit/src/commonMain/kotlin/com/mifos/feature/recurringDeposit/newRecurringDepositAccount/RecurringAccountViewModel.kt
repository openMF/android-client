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
import androidclient.feature.recurringdeposit.generated.resources.no_internet_connection
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

class RecurringAccountViewModel(
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val recurringAccountRepository: RecurringAccountRepository,
) : BaseViewModel<
    RecurringAccountState,
    RecurringAccountEvent,
    RecurringAccountAction,
    >(RecurringAccountState()) {

    init {
        observeNetwork()
    }
    private val clientId = savedStateHandle.toRoute<RecurringAccountRoute>().clientId

    private fun setLoadingState() {
        mutableStateFlow.update {
            it.copy(
                screenState = ScreenState.Loading,
            )
        }
    }
    private fun setErrorState(message: String) {
        mutableStateFlow.update {
            it.copy(
                screenState = ScreenState.Error(message),
            )
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            val isConnected = networkMonitor.isOnline.first()
            mutableStateFlow.update {
                it.copy(isOnline = isConnected)
            }
            if (isConnected) {
//                loadTemplate()
                // TODO
                // Used only for testing purpose
                // Must be removed from here after the implementation of detail screen is finished.
                loadTemplateByProduct()
            } else {
                setErrorState(
                    getString(Res.string.no_internet_connection),
                )
            }
        }
    }
    private fun handleRetry() {
        mutableStateFlow.update {
            it.copy(
                screenState = null,
                recurringDepositAccountTemplate = RecurringDepositAccountTemplate(),
            )
        }
        observeNetwork()
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

    private fun loadTemplate() {
        viewModelScope.launch {
            recurringAccountRepository.getRecurringAccountTemplate().collect { templateState ->
                when (templateState) {
                    is DataState.Error -> {
                        setErrorState(message = templateState.message)
                    }
                    is DataState.Loading -> {
                        setLoadingState()
                    }
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = null,
                                recurringDepositAccountTemplate = templateState.data,
                            )
                        }
                    }
                }
            }
        }
    }
    private fun loadTemplateByProduct() {
        viewModelScope.launch {
            recurringAccountRepository.getRecurringAccountTemplateByProduct(
                clientId = clientId,
                productId = state.recurringDepositAccountDetail.productId,
            ).collect { templateState ->
                when (templateState) {
                    is DataState.Error -> {
                        setErrorState(message = templateState.message)
                    }
                    is DataState.Loading -> {
                        setLoadingState()
                    }
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = null,
                                recurringDepositAccountTemplate = templateState.data,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun formattedAmount(amount: String): String {
        val revStr = amount.reversed()
        var formattedAmount = ""
        revStr.forEachIndexed { index, ch ->
            formattedAmount += ch
            if ((index + 1) % 3 == 0) {
                formattedAmount += ","
            }
        }
        return "$ ${formattedAmount.reversed()}"
    }

    private fun createRecurringDepositAccount() {
        viewModelScope.launch {
            val s = state
            val settings = s.recurringDepositAccountSettings
            val payload = RecurringDepositAccountPayload(
                adjustAdvanceTowardsFuturePayments = settings.adjustAdvancePayments,
                allowWithdrawal = settings.allowWithdrawals,
                clientId = clientId,
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
                lockinPeriodFrequency = settings.lockInPeriod.frequency.toInt(),
                lockinPeriodFrequencyType = settings.lockInPeriod.frequencyTypeIndex,
                mandatoryRecommendedDepositAmount = settings.recurringDepositDetails.depositAmount.toInt(),
                monthDayFormat = "dd MMMM",
                productId = s.recurringDepositAccountDetail.productId,
                recurringFrequency = settings.minDepositTerm.frequency.toIntOrNull(),
                recurringFrequencyType = settings.minDepositTerm.frequencyTypeIndex,
                // date in dd MM yyyy format.
                submittedOnDate = s.recurringDepositAccountDetail.submittedOnDate,
            )
            observeNetwork()

            if (state.isOnline) {
                recurringAccountRepository.createRecurringDepositAccount(payload).collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            setErrorState(dataState.message)
                        }
                        is DataState.Loading -> {
                            setLoadingState()
                        }
                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(screenState = null)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun handleAction(action: RecurringAccountAction) {
        when (action) {
            RecurringAccountAction.NextStep -> {
                moveToNextStep()
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

            RecurringAccountAction.Retry -> {
                handleRetry()
            }
            is RecurringAccountAction.RecurringAccountSettingsAction -> {
                when (action) {
                    RecurringAccountAction.RecurringAccountSettingsAction.OnBackPress -> {
                        sendEvent(RecurringAccountEvent.NavigateBack)
                    }
                    RecurringAccountAction.RecurringAccountSettingsAction.OnNextPress -> {
                        moveToNextStep()
                    }
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
                                        periodType = action.periodType,
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
                                        frequencyTypeIndex = action.frequencyTypeIndex,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreq -> {
                        mutableStateFlow.update { state ->
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    minDepositTerm = state.recurringDepositAccountSettings.minDepositTerm.copy(
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
                                    minDepositTerm = state.recurringDepositAccountSettings.minDepositTerm.copy(
                                        frequencyTypeIndex = action.frequencyTypeIndex,
                                    ),
                                ),
                            )
                        }
                    }

                    is RecurringAccountAction.RecurringAccountSettingsAction.SetMinDepositTermFreqAfterInMultiOf -> {
                        mutableStateFlow.update {
                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    minDepositTerm = state.recurringDepositAccountSettings.minDepositTerm.copy(
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
                                    minDepositTerm = state.recurringDepositAccountSettings.minDepositTerm.copy(
                                        frequencyTypeIndexAfterInMultiplesOf = action.frequencyTypeIndexAfterInMultiplesOf,
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
                                        frequencyTypeIndex = action.frequencyTypeIndex,
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
                                        interestPeriodIndex = action.interestPeriodIndex,
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
                                        minimumBalanceForInterestCalculation = formattedAmount(action.minimumBalanceForInterestCalculation),
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
                            var formattedAmount = ""
                            val strLen = action.depositAmount.length
                            val rev = action.depositAmount.reversed()
                            action.depositAmount.forEachIndexed { index, ch ->
                                formattedAmount += ch
                            }

                            state.copy(
                                recurringDepositAccountSettings = state.recurringDepositAccountSettings.copy(
                                    recurringDepositDetails = state.recurringDepositAccountSettings
                                        .recurringDepositDetails.copy(
                                            depositAmount = formattedAmount(action.depositAmount),
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

            is RecurringAccountAction.RecurringAccountDetailsAction.SetProductId -> {
                mutableStateFlow.update {
                    it.copy(
                        recurringDepositAccountDetail = it.recurringDepositAccountDetail.copy(
                            productId = action.productId,
                        ),
                    )
                }
                loadTemplateByProduct()
            }
        }
    }
}

data class RecurringAccountState(
    val isOnline: Boolean = false,
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    val screenState: ScreenState? = null,
    val recurringDepositAccountDetail: RecurringAccountDetailsState = RecurringAccountDetailsState(),
    val recurringDepositAccountTemplate: RecurringDepositAccountTemplate = RecurringDepositAccountTemplate(),
    val recurringDepositAccountSettings: RecurringAccountSettingsState = RecurringAccountSettingsState(),
) {
    sealed interface ScreenState {
        data class Error(val message: String) : ScreenState
        data object Loading : ScreenState
    }
}

data class RecurringAccountDetailsState(
    // TODO
    // productId is set 6 only for testing.
    // It should be set -1 when the implementation of detail screen is finished.
    val productId: Int = 6,
    val externalId: String = "",
    val submittedOnDate: String = "",
    val fieldOfficer: FieldOfficerOption? = null,
)

data class RecurringAccountSettingsState(
    val canDoNext: Boolean = false,
    val isMandatory: Boolean = false,
    val adjustAdvancePayments: Boolean = false,
    val allowWithdrawals: Boolean = false,
    val lockInPeriod: LockInPeriod = LockInPeriod(),
    val recurringDepositDetails: RecurringDepositDetails = RecurringDepositDetails(),
    val depositPeriod: DepositPeriod = DepositPeriod(),
    val minDepositTerm: MinDepositTerm = MinDepositTerm(),
    val maxDepositTerm: MaxDepositTerm = MaxDepositTerm(),
    val preMatureClosure: PreMatureClosure = PreMatureClosure(),
) {
    data class LockInPeriod(
        val frequency: String = "",
        val frequencyTypeIndex: Int = 0,
    )

    data class RecurringDepositDetails(
        val depositAmount: String = "$",
    )

    data class DepositPeriod(
        val period: String = "",
        val periodType: Int = 0,
        val depositFrequencySameAsGroupCenterMeeting: Boolean = false,
    )

    data class MinDepositTerm(
        val frequency: String = "",
        val frequencyTypeIndex: Int = 0,
        val frequencyTypeError: String? = null,
        val frequencyAfterInMultiplesOf: String = "",
        val frequencyTypeIndexAfterInMultiplesOf: Int = 0,
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
        val minimumBalanceForInterestCalculation: String = "",
    )
}

sealed class RecurringAccountAction {
    object NextStep : RecurringAccountAction()
    data class OnStepChange(val index: Int) : RecurringAccountAction()
    object NavigateBack : RecurringAccountAction()
    object Finish : RecurringAccountAction()
    data object Retry : RecurringAccountAction()
    sealed class RecurringAccountDetailsAction : RecurringAccountAction() {
        data class SetProductId(val productId: Int) : RecurringAccountDetailsAction()
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

        data object OnBackPress : RecurringAccountSettingsAction()
        data object OnNextPress : RecurringAccountSettingsAction()
    }
}

sealed class RecurringAccountEvent {
    object NavigateBack : RecurringAccountEvent()
    object Finish : RecurringAccountEvent()
}
