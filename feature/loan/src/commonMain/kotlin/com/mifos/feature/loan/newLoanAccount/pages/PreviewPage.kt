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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val notAvailable = "Not Available"
    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            "Details",
            style = MifosTypography.labelLarge,
        )
        DetailsCard(
            productName = state.productLoans[state.loanProductSelected].name.toString(),
            loanOfficer = if (state.loanOfficerIndex == -1) {
                notAvailable
            } else {
                state.loanTemplate?.loanOfficerOptions[state.loanOfficerIndex]?.displayName.toString()
            },
            externalId = state.externalId,
            submittedDate = state.submissionDate,
            expectedDisbursement = state.expectedDisbursementDate,
            loadPurpose = if (state.loanPurposeIndex == -1) {
                notAvailable
            } else state.loanTemplate?.loanPurposeOptions[state.loanPurposeIndex]?.name.toString(),
            isSavingsLinked = if (state.linkSavingsIndex == -1) {
                "NO"
            } else "YES",
        )

        Text(
            "Terms",
            style = MifosTypography.labelLarge,
        )
        TermsCard(
            //todo currency
            principal = state.principalAmount.toString(),
            loanTerm = if (state.termFrequencyIndex == -1) {
                notAvailable
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
                notAvailable
            } else {
                state.loanTemplate?.transactionProcessingStrategyOptions[state.repaymentStrategyIndex]?.name
                    ?: notAvailable
            },
            installmentAmount = "",
            ballonRepayment = state.balloonRepaymentAmount.toString(),
        )

        Text(
            "Moratorium",
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
            "Moratorium",
            style = MifosTypography.labelLarge,
        )

        MifosRowWithTextAndButton(
            onBtnClick = {},
            text = "Add Charges",
            btnText = "View",
            modifier = Modifier.fillMaxWidth(),
        )

        MifosTwoButtonRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            firstBtnText = "Back",
            secondBtnText = "Submit",
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
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LoanPreviewItemRow(
                title = "On principal payment",
                description = onPrincipalPayment,
            )

            LoanPreviewItemRow(
                title = "On interest payment",
                description = onInternestPayment,
            )

            LoanPreviewItemRow(
                title = "On Arrears Aging",
                description = onAreasAging,
            )

            LoanPreviewItemRow(
                title = "Enable installment level Delinquency",
                description = enableInstallmentLevelDelinquency,
            )

            LoanPreviewItemRow(
                title = "Recalculate Interest",
                description = recalculateInterest,
            )

            LoanPreviewItemRow(
                title = "Days in month",
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
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LoanPreviewItemRow(
                title = "Principal",
                description = principal,
            )

            LoanPreviewItemRow(
                title = "Loan Term",
                description = loanTerm,
            )

            LoanPreviewItemRow(
                title = "Number of Repayments",
                description = numberOfRepayments,
            )

            LoanPreviewItemRow(
                title = "First Repayment Date",
                description = firstRepaymentDate,
            )

            LoanPreviewItemRow(
                title = "Interest Charged From:",
                description = interestChargedForm,
            )

            LoanPreviewItemRow(
                title = "Repaid Every:",
                description = repaidEvery,
            )

            LoanPreviewItemRow(
                title = "Nominal Interest Rate:",
                description = nominalInterestRate,
            )

            LoanPreviewItemRow(
                title = "Is Equal Amortization:",
                description = isEqualAmortization,
            )

            LoanPreviewItemRow(
                title = "Amortization:",
                description = amortization,
            )
            LoanPreviewItemRow(
                title = "Interest Calculation Period:",
                description = interestCalculationPeriod,
            )

            LoanPreviewItemRow(
                title = "Calculate interest for exact days in partial :",
                description = calculateInterestForExactDaysInPartial,
            )
            LoanPreviewItemRow(
                title = "Arrears tolerance:",
                description = arrearsTolerance,
            )

            LoanPreviewItemRow(
                title = "Interest free period:",
                description = interestFreePeriod,
            )
            LoanPreviewItemRow(
                title = "Repayment strategy:",
                description = repaymentStrategy,
            )
            LoanPreviewItemRow(
                title = "Installment Amount:",
                description = installmentAmount,
            )
            LoanPreviewItemRow(
                title = "Balloon Repayment Amount",
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
            LoanPreviewItemRow(title = "Product Name", description = productName)
            LoanPreviewItemRow(title = "Loan Officer", description = loanOfficer)
            LoanPreviewItemRow(title = "External ID", description = externalId)
            LoanPreviewItemRow(
                title = "Submitted Date",
                description = submittedDate,
            )

            LoanPreviewItemRow(
                title = "Expected Disbursement",
                description = expectedDisbursement,
            )

            LoanPreviewItemRow(
                title = "Loan Purpose",
                description = loadPurpose,
            )

            LoanPreviewItemRow(
                title = "Is Savings Linked",
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
