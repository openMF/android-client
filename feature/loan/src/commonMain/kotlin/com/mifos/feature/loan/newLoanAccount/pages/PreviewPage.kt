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
import androidclient.feature.loan.generated.resources.step_details
import androidclient.feature.loan.generated.resources.submission_date
import androidclient.feature.loan.generated.resources.terms
import androidclient.feature.loan.generated.resources.yes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosDefaultListingComponentFromStringResources
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountAction
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountState
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme

@Composable
fun PreviewPage(
    state: NewLoanAccountState,
    modifier: Modifier = Modifier,
    onAction: (NewLoanAccountAction) -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(bottom = KptTheme.spacing.md),
    ) {
        Column(
            modifier = modifier.weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.largeIncreased),
        ) {
            Text(
                text = stringResource(Res.string.step_details),
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
                } else {
                    state.loanTemplate?.loanPurposeOptions[state.loanPurposeIndex]?.name.toString()
                },
                isSavingsLinked = if (state.linkSavingsIndex == -1) {
                    stringResource(Res.string.no)
                } else {
                    stringResource(Res.string.yes)
                },
            )

            Text(
                text = stringResource(Res.string.terms),
                style = MifosTypography.labelLarge,
            )
            TermsCard(
                principal = CurrencyFormatter.format(
                    balance = state.principalAmount.toDouble(),
                    currencyCode = state.loanTemplate?.currency?.code,
                    maximumFractionDigits = 2,
                ),
                loanTerm = if (state.termFrequencyIndex == -1) {
                    ""
                } else {
                    state.loanTemplate?.termFrequencyTypeOptions[state.termFrequencyIndex]?.value
                        ?: ""
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
                // todo
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
                // todo
                enableInstallmentLevelDelinquency = "",
                recalculateInterest = if (state.loanTemplate?.isInterestRecalculationEnabled
                    ?: false
                ) {
                    stringResource(Res.string.yes)
                } else {
                    stringResource(Res.string.no)
                },
                // todo
                daysInMonth = "",
            )

            Text(
                text = stringResource(Res.string.loan_new_loan_charges),
                style = MifosTypography.labelLarge,
            )

            MifosRowWithTextAndButton(
                onBtnClick = { onAction(NewLoanAccountAction.ShowCharges) },
                text = state.addedCharges.size.toString() + " " + stringResource(Res.string.loan_new_loan_active_charges),
                btnText = stringResource(Res.string.loan_new_loan_view),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.back),
            secondBtnText = stringResource(Res.string.feature_loan_charge_submit),
            onFirstBtnClick = { onAction(NewLoanAccountAction.PreviousStep) },
            onSecondBtnClick = { onAction(NewLoanAccountAction.SubmitLoanApplication) },
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
    MifosDefaultListingComponentFromStringResources(
        data = mapOf(
            Res.string.loan_new_loan_on_principal_payment to onPrincipalPayment,
            Res.string.loan_new_loan_on_interest_payment to onInternestPayment,
            Res.string.loan_new_loan_on_arrears_aging to onAreasAging,
            Res.string.loan_new_loan_enable_installment_level to enableInstallmentLevelDelinquency,
            Res.string.loan_new_loan_recalculate_interest to recalculateInterest,
            Res.string.loan_new_loan_days_in_month to daysInMonth,
        ),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
    )
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
    MifosDefaultListingComponentFromStringResources(
        data = mapOf(
            Res.string.principal to principal,
            Res.string.loan_new_loan_loan_term to loanTerm,
            Res.string.number_of_repayments to numberOfRepayments,
            Res.string.first_repayment_date to firstRepaymentDate,
            Res.string.interest_charged_from to interestChargedForm,
            Res.string.loan_new_loan_repaid_every to repaidEvery,
            Res.string.loan_new_loan_nominal_interest_rate to nominalInterestRate,
            Res.string.loan_new_loan_is_equal_amortization to isEqualAmortization,
            Res.string.loan_new_loan_amortization to amortization,
            Res.string.interest_calculation_period to interestCalculationPeriod,
            Res.string.loan_new_loan_calculate_interest_for_exact_days_in_pertial to calculateInterestForExactDaysInPartial,
            Res.string.loan_new_loan_arrears_tolerance to arrearsTolerance,
            Res.string.loan_new_loan_interest_free_period to interestFreePeriod,
            Res.string.repayment_strategy to repaymentStrategy,
            Res.string.loan_new_loan_installment_amount to installmentAmount,
            Res.string.loan_new_loan_ballon_repayment_amount to ballonRepayment,
        ),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
    )
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
    MifosDefaultListingComponentFromStringResources(
        data = mapOf(
            Res.string.product_name to productName,
            Res.string.loan_new_loan_loan_officer to loanOfficer,
            Res.string.external_id to externalId,
            Res.string.submission_date to submittedDate,
            Res.string.expected_disbursement to expectedDisbursement,
            Res.string.loan_new_loan_loan_purpose to loadPurpose,
            Res.string.loan_new_loan_is_savings_linked to isSavingsLinked,
        ),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
    )
}
