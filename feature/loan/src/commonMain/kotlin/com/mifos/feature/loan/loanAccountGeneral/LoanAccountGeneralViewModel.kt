/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountGeneral

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_approved_amount
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_currency
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_disbursed_amount
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_disbursement_date
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_loan_officer
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_loan_purpose
import androidclient.feature.loan.generated.resources.feature_loan_general_detail_proposed_amount
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_row_fees
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_row_interest
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_row_penalties
import androidclient.feature.loan.generated.resources.feature_loan_general_summary_row_principal
import androidclient.feature.loan.generated.resources.feature_loan_general_value_not_available
import androidclient.feature.loan.generated.resources.feature_loan_general_value_unassigned
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_details_not_found
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_network_not_available
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.LoanAccountGeneralRepository
import com.mifos.core.data.repositoryImp.NetworkUnavailableException
import com.mifos.core.model.entity.loan.loanWithAssociations.LoanWithAssociations
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

internal class LoanAccountGeneralViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanAccountGeneralRepository,
) : BaseViewModel<LoanAccountGeneralState, LoanAccountGeneralEvent, LoanAccountGeneralAction>(
    initialState = LoanAccountGeneralState(),
) {
    private val loanId = savedStateHandle.toRoute<LoanAccountGeneralRoute>().loanId
    private var loadJob: Job? = null

    init {
        loadLoanById()
    }

    override fun handleAction(action: LoanAccountGeneralAction) {
        when (action) {
            LoanAccountGeneralAction.OnRetry -> loadLoanById()
        }
    }

    private fun loadLoanById() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.getLoanById(loanId).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = LoanAccountGeneralState.DialogState.Loading)
                        }
                    }

                    is DataState.Success -> {
                        val loan = dataState.data
                        if (loan != null) {
                            try {
                                fillGeneralState(loan)
                            } catch (e: Exception) {
                                mutableStateFlow.update {
                                    it.copy(dialogState = LoanAccountGeneralState.DialogState.Error(e.message.toString()))
                                }
                            }
                        } else {
                            mutableStateFlow.update {
                                it.copy(dialogState = LoanAccountGeneralState.DialogState.Error(getString(Res.string.feature_loan_profile_error_details_not_found)))
                            }
                        }
                    }

                    is DataState.Error -> {
                        val isNetworkError = dataState.exception is NetworkUnavailableException
                        mutableStateFlow.update {
                            it.copy(
                                networkConnection = !isNetworkError,
                                dialogState = LoanAccountGeneralState.DialogState.Error(
                                    if (isNetworkError) {
                                        getString(Res.string.feature_loan_profile_error_network_not_available)
                                    } else {
                                        dataState.message
                                    },
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun fillGeneralState(loan: LoanWithAssociations) {
        val currencyCode = loan.currency?.code
        val maxDigits = loan.currency?.decimalPlaces
        val summary = loan.summary
        fun formatAmount(value: Double?) = CurrencyFormatter.format(value, currencyCode, maxDigits)

        val expectedMaturityDate = loan.timeline?.expectedMaturityDate
        val maturityDate = if (!expectedMaturityDate.isNullOrEmpty()) {
            DateHelper.getDateAsString(expectedMaturityDate)
        } else {
            ""
        }

        val actualDisbursementDate = loan.timeline?.actualDisbursementDate
        val disbursementDate = if (!actualDisbursementDate.isNullOrEmpty()) {
            DateHelper.getDateAsString(actualDisbursementDate.filterNotNull())
        } else {
            ""
        }

        val currencyDisplay = if (!loan.currency?.name.isNullOrBlank() && !loan.currency?.code.isNullOrBlank()) {
            "${loan.currency?.name} ${loan.currency?.code}"
        } else {
            ""
        }

        val loanOfficer = loan.loanOfficerName?.takeIf { it.isNotBlank() }
            ?: getString(Res.string.feature_loan_general_value_unassigned)
        val loanPurpose = loan.loanPurposeName?.takeIf { it.isNotBlank() }
            ?: getString(Res.string.feature_loan_general_value_not_available)
        val externalId = loan.accountNo?.takeIf { it.isNotBlank() }
            ?: getString(Res.string.feature_loan_general_value_not_available)

        val proposedAmountValue = formatAmount(loan.proposedPrincipal)
        val approvedAmountValue = formatAmount(loan.approvedPrincipal)
        val disbursedAmountValue = formatAmount(loan.principal)

        mutableStateFlow.update {
            it.copy(
                dialogState = null,
                numberOfRepayments = loan.numberOfRepayments?.toString().orEmpty(),
                maturityDate = maturityDate,
                disbursementDate = disbursementDate,
                loanOfficer = loanOfficer,
                currency = currencyDisplay,
                loanPurpose = loanPurpose,
                externalId = externalId,
                proposedAmount = proposedAmountValue,
                approvedAmount = approvedAmountValue,
                disbursedAmount = disbursedAmountValue,
                details = listOf(
                    LoanAccountGeneralState.LoanDetailGroupState(
                        items = listOf(
                            LoanAccountGeneralState.LoanDetailItem.Text(
                                label = Res.string.feature_loan_general_detail_disbursement_date,
                                value = disbursementDate,
                            ),
                            LoanAccountGeneralState.LoanDetailItem.Text(
                                label = Res.string.feature_loan_general_detail_loan_purpose,
                                value = loanPurpose,
                            ),
                            LoanAccountGeneralState.LoanDetailItem.Text(
                                label = Res.string.feature_loan_general_detail_loan_officer,
                                value = loanOfficer,
                            ),
                            LoanAccountGeneralState.LoanDetailItem.Text(
                                label = Res.string.feature_loan_general_detail_currency,
                                value = currencyDisplay,
                            ),
                        ),
                    ),
                    LoanAccountGeneralState.LoanDetailGroupState(
                        items = listOf(
                            LoanAccountGeneralState.LoanDetailItem.Text(
                                label = Res.string.feature_loan_general_detail_proposed_amount,
                                value = proposedAmountValue,
                            ),
                            LoanAccountGeneralState.LoanDetailItem.Text(
                                label = Res.string.feature_loan_general_detail_approved_amount,
                                value = approvedAmountValue,
                            ),
                            LoanAccountGeneralState.LoanDetailItem.Text(
                                label = Res.string.feature_loan_general_detail_disbursed_amount,
                                value = disbursedAmountValue,
                            ),
                        ),
                    ),
                ),
                summaryRows = listOf(
                    LoanAccountGeneralState.SummaryRowState(
                        component = getString(Res.string.feature_loan_general_summary_row_principal),
                        original = formatAmount(summary?.principalDisbursed),
                        paid = formatAmount(summary?.principalPaid),
                        waived = formatAmount(summary?.principalWaived),
                        writtenOff = formatAmount(summary?.principalWrittenOff),
                        outstanding = formatAmount(summary?.principalOutstanding),
                        overDue = formatAmount(summary?.principalOverdue),
                    ),
                    LoanAccountGeneralState.SummaryRowState(
                        component = getString(Res.string.feature_loan_general_summary_row_interest),
                        original = formatAmount(summary?.interestCharged),
                        paid = formatAmount(summary?.interestPaid),
                        waived = formatAmount(summary?.interestWaived),
                        writtenOff = formatAmount(summary?.interestWrittenOff),
                        outstanding = formatAmount(summary?.interestOutstanding),
                        overDue = formatAmount(summary?.interestOverdue),
                    ),
                    LoanAccountGeneralState.SummaryRowState(
                        component = getString(Res.string.feature_loan_general_summary_row_fees),
                        original = formatAmount(summary?.feeChargesCharged),
                        paid = formatAmount(summary?.feeChargesPaid),
                        waived = formatAmount(summary?.feeChargesWaived),
                        writtenOff = formatAmount(summary?.feeChargesWrittenOff),
                        outstanding = formatAmount(summary?.feeChargesOutstanding),
                        overDue = formatAmount(summary?.feeChargesOverdue),
                    ),
                    LoanAccountGeneralState.SummaryRowState(
                        component = getString(Res.string.feature_loan_general_summary_row_penalties),
                        original = formatAmount(summary?.penaltyChargesCharged),
                        paid = formatAmount(summary?.penaltyChargesPaid),
                        waived = formatAmount(summary?.penaltyChargesWaived),
                        writtenOff = formatAmount(summary?.penaltyChargesWrittenOff),
                        outstanding = formatAmount(summary?.penaltyChargesOutstanding),
                        overDue = formatAmount(summary?.penaltyChargesOverdue),
                    ),
                ),
                totalOriginal = formatAmount(summary?.totalExpectedRepayment),
                totalPaid = formatAmount(summary?.totalRepayment),
                totalWaived = formatAmount(summary?.totalWaived),
                totalWrittenOff = formatAmount(summary?.totalWrittenOff),
                totalOutstanding = formatAmount(summary?.totalOutstanding),
                totalOverDue = formatAmount(summary?.totalOverdue),
            )
        }
    }
}

data class LoanAccountGeneralState(
    val dialogState: DialogState? = null,
    val networkConnection: Boolean = false,
    val numberOfRepayments: String = "",
    val maturityDate: String = "",
    val details: List<LoanDetailGroupState> = emptyList(),
    val summaryRows: List<SummaryRowState> = emptyList(),
    val totalOriginal: String = "",
    val totalPaid: String = "",
    val totalWaived: String = "",
    val totalWrittenOff: String = "",
    val totalOutstanding: String = "",
    val totalOverDue: String = "",
    val disbursementDate: String = "",
    val loanPurpose: String = "",
    val loanOfficer: String = "",
    val currency: String = "",
    val externalId: String = "",
    val proposedAmount: String = "",
    val approvedAmount: String = "",
    val disbursedAmount: String = "",
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
    }

    data class LoanDetailGroupState(
        val items: List<LoanDetailItem>,
    )

    sealed interface LoanDetailItem {
        data class Text(
            val label: StringResource,
            val value: String,
        ) : LoanDetailItem
    }

    data class SummaryRowState(
        val component: String,
        val original: String,
        val paid: String,
        val waived: String,
        val writtenOff: String,
        val outstanding: String,
        val overDue: String,
    )
}

sealed interface LoanAccountGeneralEvent

sealed interface LoanAccountGeneralAction {
    data object OnRetry : LoanAccountGeneralAction
}
