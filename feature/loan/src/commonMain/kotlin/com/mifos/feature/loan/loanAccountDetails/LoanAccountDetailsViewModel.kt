/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountDetails

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_allow_partial_calculation
import androidclient.feature.loan.generated.resources.feature_loan_amortization
import androidclient.feature.loan.generated.resources.feature_loan_approved_on
import androidclient.feature.loan.generated.resources.feature_loan_available_disbursement_amount
import androidclient.feature.loan.generated.resources.feature_loan_charge_off_behavior
import androidclient.feature.loan.generated.resources.feature_loan_days_in_year
import androidclient.feature.loan.generated.resources.feature_loan_disbursed_on
import androidclient.feature.loan.generated.resources.feature_loan_enable_buy_down_fee
import androidclient.feature.loan.generated.resources.feature_loan_enable_down_payments
import androidclient.feature.loan.generated.resources.feature_loan_enable_income_capitalization
import androidclient.feature.loan.generated.resources.feature_loan_equal_amortization
import androidclient.feature.loan.generated.resources.feature_loan_free_period
import androidclient.feature.loan.generated.resources.feature_loan_fund_source
import androidclient.feature.loan.generated.resources.feature_loan_hyphen
import androidclient.feature.loan.generated.resources.feature_loan_installment_level_delinquency
import androidclient.feature.loan.generated.resources.feature_loan_interest_on_disbursement
import androidclient.feature.loan.generated.resources.feature_loan_interest_rate
import androidclient.feature.loan.generated.resources.feature_loan_interest_rate_per_annum_format
import androidclient.feature.loan.generated.resources.feature_loan_interest_rate_per_period_format
import androidclient.feature.loan.generated.resources.feature_loan_interest_type
import androidclient.feature.loan.generated.resources.feature_loan_matures_on
import androidclient.feature.loan.generated.resources.feature_loan_not_available
import androidclient.feature.loan.generated.resources.feature_loan_not_available_abbr
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_details_not_found
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_network_not_available
import androidclient.feature.loan.generated.resources.feature_loan_profile_failed_to_load_loan
import androidclient.feature.loan.generated.resources.feature_loan_repayment_strategy
import androidclient.feature.loan.generated.resources.feature_loan_repayments_format
import androidclient.feature.loan.generated.resources.feature_loan_submitted_on
import androidclient.feature.loan.generated.resources.feature_loan_unassigned
import androidclient.feature.loan.generated.resources.feature_loan_zero_amount
import androidclient.feature.loan.generated.resources.grace_on_interest_payment
import androidclient.feature.loan.generated.resources.grace_on_principal_payment
import androidclient.feature.loan.generated.resources.interest_calculation_period
import androidclient.feature.loan.generated.resources.loan_new_loan_days_in_month
import androidclient.feature.loan.generated.resources.no
import androidclient.feature.loan.generated.resources.on_arrears_ageing
import androidclient.feature.loan.generated.resources.recalculate_interest
import androidclient.feature.loan.generated.resources.repayments
import androidclient.feature.loan.generated.resources.yes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.model.objects.account.loan.LoanWithAssociations
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

internal class LoanAccountDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val loanRepository: LoanAccountSummaryRepository,
) : BaseViewModel<LoanAccountDetailsState, LoanAccountDetailsEvent, LoanAccountDetailsAction>(
    initialState = LoanAccountDetailsState(),
) {
    private val routeData = savedStateHandle.toRoute<LoanAccountDetailsRoute>()
    private var loadJob: Job? = null

    init {
        observeNetworkAndLoad()
    }

    private fun observeNetworkAndLoad() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkConnection = isConnected) }
                if (isConnected) {
                    if (mutableStateFlow.value.details.isEmpty()) {
                        loadLoanAccountDetails(routeData.loanId)
                    }
                } else if (mutableStateFlow.value.details.isEmpty()) {
                    mutableStateFlow.update {
                        it.copy(dialogState = LoanAccountDetailsState.DialogState.Error(Res.string.feature_loan_profile_error_network_not_available))
                    }
                }
            }
        }
    }

    private fun loadLoanAccountDetails(loanId: Int) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            loanRepository.getLoanById(loanId).collect { result ->
                when (result) {
                    is DataState.Success -> {
                        val loan = result.data
                        if (loan == null) {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = LoanAccountDetailsState.DialogState.Error(Res.string.feature_loan_profile_error_details_not_found),
                                )
                            }
                            return@collect
                        }
                        processLoanData(loan)
                    }
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = LoanAccountDetailsState.DialogState.Error(Res.string.feature_loan_profile_failed_to_load_loan))
                        }
                    }
                    DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = LoanAccountDetailsState.DialogState.Loading)
                        }
                    }
                }
            }
        }
    }

    override fun handleAction(action: LoanAccountDetailsAction) {
        when (action) {
            LoanAccountDetailsAction.NavigateBack -> sendEvent(LoanAccountDetailsEvent.NavigateBack)
            LoanAccountDetailsAction.OnRetry -> if (stateFlow.value.networkConnection) {
                loadLoanAccountDetails(routeData.loanId)
            } else {
                mutableStateFlow.update {
                    it.copy(dialogState = LoanAccountDetailsState.DialogState.Error(Res.string.feature_loan_profile_error_details_not_found))
                }
            }
        }
    }

    private suspend fun processLoanData(loan: LoanWithAssociations) {
        val availableDisbursement = loan.availableDisbursementAmount

        val naStr = getString(Res.string.feature_loan_not_available_abbr)
        val yesStr = getString(Res.string.yes)
        val noStr = getString(Res.string.no)
        val hyphenStr = getString(Res.string.feature_loan_hyphen)
        val unassignedStr = getString(Res.string.feature_loan_unassigned)
        val notAvailableDateStr = getString(Res.string.feature_loan_not_available)

        val repaymentsFormat = if (
            loan.numberOfRepayments?.let { it > 0 } == true &&
            loan.repaymentEvery?.let { it > 0 } == true &&
            !loan.repaymentFrequencyType.isNullOrBlank()
        ) {
            getString(
                Res.string.feature_loan_repayments_format,
                loan.numberOfRepayments!!,
                loan.repaymentEvery!!,
                loan.repaymentFrequencyType!!,
            ).trim()
        } else {
            naStr
        }

        val interestRateFormat = getString(Res.string.feature_loan_interest_rate_per_annum_format, loan.annualInterestRate.toString())
        val interestRateSubtitle = getString(Res.string.feature_loan_interest_rate_per_period_format, loan.interestRatePerPeriod.toString())

        val repaymentStructureMap = mapOf(
            Res.string.feature_loan_repayment_strategy to (
                loan.transactionProcessingStrategyName?.ifBlank { naStr }
                    ?: naStr
                ),
            Res.string.repayments to repaymentsFormat,
            Res.string.feature_loan_amortization to loan.amortizationType.orEmpty().ifBlank { naStr },
            Res.string.feature_loan_equal_amortization to if (loan.isEqualAmortization == true) yesStr else noStr,
        )

        val interestConfigurationMap = mapOf(
            Res.string.feature_loan_interest_rate to "$interestRateFormat $interestRateSubtitle".trim(),
            Res.string.feature_loan_interest_type to loan.interestType.orEmpty().ifBlank { naStr },
            Res.string.feature_loan_free_period to hyphenStr,
            Res.string.interest_calculation_period to loan.interestCalculationPeriodType.orEmpty().ifBlank { naStr },
            Res.string.feature_loan_allow_partial_calculation to if (loan.allowPartialPeriodInterestCalculation == true) yesStr else noStr,
            Res.string.feature_loan_interest_on_disbursement to if (loan.interestRecognitionOnDisbursementDate == true) yesStr else noStr,
        )

        val gracePeriodsMap = mapOf(
            Res.string.grace_on_principal_payment to notAvailableDateStr,
            Res.string.grace_on_interest_payment to notAvailableDateStr,
            Res.string.on_arrears_ageing to notAvailableDateStr,
        )

        val settingsMap = mapOf(
            Res.string.feature_loan_enable_down_payments to if (loan.enableDownPayment == true) yesStr else noStr,
            Res.string.feature_loan_charge_off_behavior to loan.chargeOffBehaviour.orEmpty().ifBlank { naStr },
            Res.string.feature_loan_enable_income_capitalization to if (loan.enableIncomeCapitalization == true) yesStr else noStr,
            Res.string.feature_loan_enable_buy_down_fee to if (loan.enableBuyDownFee == true) yesStr else noStr,
            Res.string.feature_loan_installment_level_delinquency to if (loan.enableInstallmentLevelDelinquency == true) yesStr else noStr,
        )

        val timelineMap = mapOf(
            Res.string.feature_loan_submitted_on to formatDate(loan.timeline?.submittedOnDate).ifBlank { notAvailableDateStr },
            Res.string.feature_loan_approved_on to formatDate(loan.timeline?.approvedOnDate).ifBlank { notAvailableDateStr },
            Res.string.feature_loan_disbursed_on to formatDate(loan.timeline?.actualDisbursementDate).ifBlank { notAvailableDateStr },
            Res.string.feature_loan_matures_on to formatDate(loan.timeline?.expectedMaturityDate).ifBlank { notAvailableDateStr },
        )

        val additionalInfoMap = mapOf(
            Res.string.feature_loan_fund_source to (loan.fundName?.ifBlank { unassignedStr } ?: unassignedStr),
            Res.string.recalculate_interest to if (loan.isInterestRecalculationEnabled == true) yesStr else noStr,
            Res.string.feature_loan_days_in_year to loan.daysInYearType.orEmpty().ifBlank { naStr },
            Res.string.loan_new_loan_days_in_month to loan.daysInMonthType.orEmpty().ifBlank { naStr },
            Res.string.feature_loan_available_disbursement_amount to formatCurrency(
                amount = availableDisbursement,
                currencyCode = loan.currency?.code,
                decimalPlaces = loan.currency?.decimalPlaces,
            ),
        )

        val mappedDetails = listOf(
            repaymentStructureMap,
            interestConfigurationMap,
            gracePeriodsMap,
            settingsMap,
            timelineMap,
            additionalInfoMap,
        )

        mutableStateFlow.update {
            it.copy(
                dialogState = null,
                details = mappedDetails,
            )
        }
    }

    private suspend fun formatCurrency(amount: Double?, currencyCode: String?, decimalPlaces: Int?): String {
        if (amount == null) return getString(Res.string.feature_loan_zero_amount)
        if (currencyCode.isNullOrBlank()) return amount.toString()

        return CurrencyFormatter.format(
            balance = amount,
            currencyCode = currencyCode,
            maximumFractionDigits = decimalPlaces,
        )
    }

    private fun formatDate(date: List<Int?>?): String {
        val year = date?.getOrNull(0)
        val month = date?.getOrNull(1)
        val day = date?.getOrNull(2)

        return if (year != null && day != null && month != null && month in 1..12) {
            DateHelper.getDateAsString(listOf(year, month, day))
        } else {
            ""
        }
    }
}

data class LoanAccountDetailsState(
    val dialogState: DialogState? = DialogState.Loading,
    val networkConnection: Boolean = false,
    val details: List<Map<StringResource, String>> = emptyList(),
) {
    sealed interface DialogState {
        data class Error(val message: StringResource) : DialogState
        data object Loading : DialogState
    }
}

sealed interface LoanAccountDetailsEvent {
    data object NavigateBack : LoanAccountDetailsEvent
}

sealed interface LoanAccountDetailsAction {
    data object NavigateBack : LoanAccountDetailsAction
    data object OnRetry : LoanAccountDetailsAction
}
