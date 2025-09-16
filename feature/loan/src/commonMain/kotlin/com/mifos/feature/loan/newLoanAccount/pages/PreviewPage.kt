/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.newLoanAccount.pages

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.back
import androidclient.feature.loan.generated.resources.expected_disbursement
import androidclient.feature.loan.generated.resources.external_id
import androidclient.feature.loan.generated.resources.feature_loan_charge_submit
import androidclient.feature.loan.generated.resources.first_repayment_date
import androidclient.feature.loan.generated.resources.interest_calculation_period
import androidclient.feature.loan.generated.resources.interest_charged_from
import androidclient.feature.loan.generated.resources.loan_new_loan_active_charges
import androidclient.feature.loan.generated.resources.loan_new_loan_amortization
import androidclient.feature.loan.generated.resources.loan_new_loan_arrears_tolerance
import androidclient.feature.loan.generated.resources.loan_new_loan_ballon_repayment_amount
import androidclient.feature.loan.generated.resources.loan_new_loan_calculate_interest_for_exact_days_in_pertial
import androidclient.feature.loan.generated.resources.loan_new_loan_charges
import androidclient.feature.loan.generated.resources.loan_new_loan_days_in_month
import androidclient.feature.loan.generated.resources.loan_new_loan_enable_installment_level
import androidclient.feature.loan.generated.resources.loan_new_loan_installment_amount
import androidclient.feature.loan.generated.resources.loan_new_loan_interest_free_period
import androidclient.feature.loan.generated.resources.loan_new_loan_is_equal_amortization
import androidclient.feature.loan.generated.resources.loan_new_loan_is_savings_linked
import androidclient.feature.loan.generated.resources.loan_new_loan_loan_officer
import androidclient.feature.loan.generated.resources.loan_new_loan_loan_purpose
import androidclient.feature.loan.generated.resources.loan_new_loan_loan_term
import androidclient.feature.loan.generated.resources.loan_new_loan_moratorium
import androidclient.feature.loan.generated.resources.loan_new_loan_nominal_interest_rate
import androidclient.feature.loan.generated.resources.loan_new_loan_on_arrears_aging
import androidclient.feature.loan.generated.resources.loan_new_loan_on_interest_payment
import androidclient.feature.loan.generated.resources.loan_new_loan_on_principal_payment
import androidclient.feature.loan.generated.resources.loan_new_loan_recalculate_interest
import androidclient.feature.loan.generated.resources.loan_new_loan_repaid_every
import androidclient.feature.loan.generated.resources.loan_new_loan_view
import androidclient.feature.loan.generated.resources.no
import androidclient.feature.loan.generated.resources.number_of_repayments
import androidclient.feature.loan.generated.resources.principal
import androidclient.feature.loan.generated.resources.product_name
import androidclient.feature.loan.generated.resources.repayment_strategy
import androidclient.feature.loan.generated.resources.submission_date
import androidclient.feature.loan.generated.resources.terms
import androidclient.feature.loan.generated.resources.yes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountAction
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountState
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreviewPage(
    state: NewLoanAccountState,
    onAction: (NewLoanAccountAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(Res.string.terms),
            style = MifosTypography.labelLarge,
        )
        DetailsCard(
            productName = state.productLoans[state.loanProductSelected].name.toString(),
            loanOfficer = if (state.loanOfficerIndex == -1) {
                ""
            } else {
                state.loanTemplate?.loanOfficerOptions[state.loanOfficerIndex]?.displayName.toString()
            },
            externalId = state.externalId,
            submittedDate = state.submissionDate,
            expectedDisbursement = state.expectedDisbursementDate,
            loadPurpose = if (state.loanPurposeIndex == -1) {
                ""
            } else state.loanTemplate?.loanPurposeOptions[state.loanPurposeIndex]?.name.toString(),
            isSavingsLinked = if (state.linkSavingsIndex == -1) {
                stringResource(Res.string.no)
            } else stringResource(Res.string.yes),
        )

        Text(
            text = stringResource(Res.string.terms),
            style = MifosTypography.labelLarge,
        )
        TermsCard(
            //todo currency
            principal = state.principalAmount.toString(),
            loanTerm = if (state.termFrequencyIndex == -1) {
                ""
            } else {
                state.loanTemplate?.termFrequencyTypeOptions[state.termFrequencyIndex]?.value ?: ""
            },
            numberOfRepayments = state.noOfRepayments.toString(),
            firstRepaymentDate = state.firstRepaymentDate,
            interestChargedForm = state.interestChargedFromDate,
            repaidEvery = "",
            nominalInterestRate = state.nominalInterestRate.toString(),
            isEqualAmortization = state.isCheckedEqualAmortization.toString(),
            amortization = if (state.nominalAmortizationIndex == -1) {
                ""
            } else {
                state.loanTemplate?.amortizationTypeOptions[state.nominalAmortizationIndex]?.value
                    ?: ""
            },
            interestCalculationPeriod = if (state.interestCalculationPeriodIndex == -1) {
                ""
            } else {
                state.loanTemplate?.interestCalculationPeriodTypeOptions[state.interestCalculationPeriodIndex]?.value
                    ?: ""
            },
            calculateInterestForExactDaysInPartial = "",
            arrearsTolerance = state.arrearsTolerance.toString(),
            interestFreePeriod = state.interestFreePeriod.toString(),
            repaymentStrategy = if (state.repaymentStrategyIndex == -1) {
                ""
            } else {
                state.loanTemplate?.transactionProcessingStrategyOptions[state.repaymentStrategyIndex]?.name
                    ?: ""
            },
            installmentAmount = "",
            ballonRepayment = state.balloonRepaymentAmount.toString(),
        )

        Text(
            text = stringResource(Res.string.loan_new_loan_moratorium),
            style = MifosTypography.labelLarge,
        )
        MoratoriumCard(
            onPrincipalPayment = state.moratoriumGraceOnPrincipalPayment.toString(),
            onInternestPayment = state.moratoriumGraceOnInterestPayment.toString(),
            onAreasAging = state.moratoriumOnArrearsAgeing.toString(),
            enableInstallmentLevelDelinquency = "",
            recalculateInterest = "",
            daysInMonth = "",
        )

        Text(
            text = stringResource(Res.string.loan_new_loan_charges),
            style = MifosTypography.labelLarge,
        )

        MifosRowWithTextAndButton(
            onBtnClick = {},
            text = state.addedCharges.size.toString() + " " + stringResource(Res.string.loan_new_loan_active_charges),
            btnText = stringResource(Res.string.loan_new_loan_view),
            modifier = Modifier.fillMaxWidth(),
        )

        MifosTwoButtonRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            firstBtnText = stringResource(Res.string.back),
            secondBtnText = stringResource(Res.string.feature_loan_charge_submit),
            onFirstBtnClick = {},
            onSecondBtnClick = {},
        )
    }
}

@Composable
private fun MoratoriumCard(
    onPrincipalPayment: String,
    onInternestPayment: String,
    onAreasAging: String,
    enableInstallmentLevelDelinquency: String,
    recalculateInterest: String,
    daysInMonth: String,
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_on_principal_payment),
                description = onPrincipalPayment,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_on_interest_payment),
                description = onInternestPayment,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_on_arrears_aging),
                description = onAreasAging,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_enable_installment_level),
                description = enableInstallmentLevelDelinquency,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_recalculate_interest),
                description = recalculateInterest,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_days_in_month),
                description = daysInMonth,
            )
        }
    }
}

@Composable
private fun TermsCard(
    principal: String,
    loanTerm: String,
    numberOfRepayments: String,
    firstRepaymentDate: String,
    interestChargedForm: String,
    repaidEvery: String,
    nominalInterestRate: String,
    isEqualAmortization: String,
    amortization: String,
    interestCalculationPeriod: String,
    calculateInterestForExactDaysInPartial: String,
    arrearsTolerance: String,
    interestFreePeriod: String,
    repaymentStrategy: String,
    installmentAmount: String,
    ballonRepayment: String,
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LoanPreviewItemRow(
                title = stringResource(Res.string.principal),
                description = principal,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_loan_term),
                description = loanTerm,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.number_of_repayments),
                description = numberOfRepayments,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.first_repayment_date),
                description = firstRepaymentDate,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.interest_charged_from),
                description = interestChargedForm,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_repaid_every),
                description = repaidEvery,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_nominal_interest_rate),
                description = nominalInterestRate,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_is_equal_amortization),
                description = isEqualAmortization,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_amortization),
                description = amortization,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.interest_calculation_period),
                description = interestCalculationPeriod,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_calculate_interest_for_exact_days_in_pertial),
                description = calculateInterestForExactDaysInPartial,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_arrears_tolerance),
                description = arrearsTolerance,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_interest_free_period),
                description = interestFreePeriod,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.repayment_strategy),
                description = repaymentStrategy,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_installment_amount),
                description = installmentAmount,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_ballon_repayment_amount),
                description = ballonRepayment,
            )
        }
    }
}

@Composable
private fun DetailsCard(
    productName: String,
    loanOfficer: String,
    externalId: String,
    submittedDate: String,
    expectedDisbursement: String,
    loadPurpose: String,
    isSavingsLinked: String,
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LoanPreviewItemRow(
                title = stringResource(Res.string.product_name),
                description = productName,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_loan_officer),
                description = loanOfficer,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.external_id),
                description = externalId,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.submission_date),
                description = submittedDate,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.expected_disbursement),
                description = expectedDisbursement,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_loan_purpose),
                description = loadPurpose,
            )
            LoanPreviewItemRow(
                title = stringResource(Res.string.loan_new_loan_is_savings_linked),
                description = isSavingsLinked,
            )
        }
    }
}

@Composable
private fun LoanPreviewItemRow(
    title: String,
    description: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "$title:",
            style = MifosTypography.labelMediumEmphasized,
            modifier = Modifier.weight(5f),
            maxLines = 1,
        )

        Text(
            text = description,
            modifier = Modifier.weight(5f),
            maxLines = 1,
            textAlign = TextAlign.End,
            style = MifosTypography.labelMedium,
        )
    }
}
