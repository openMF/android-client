/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepayment

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.back
import androidclient.feature.loan.generated.resources.feature_loan_account
import androidclient.feature.loan.generated.resources.feature_loan_bank
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_cheque
import androidclient.feature.loan.generated.resources.feature_loan_due_date
import androidclient.feature.loan.generated.resources.feature_loan_no_penalties_found
import androidclient.feature.loan.generated.resources.feature_loan_no_penalties_message
import androidclient.feature.loan.generated.resources.feature_loan_note
import androidclient.feature.loan.generated.resources.feature_loan_outstanding_balance
import androidclient.feature.loan.generated.resources.feature_loan_penalties_short
import androidclient.feature.loan.generated.resources.feature_loan_principal
import androidclient.feature.loan.generated.resources.feature_loan_receipt
import androidclient.feature.loan.generated.resources.feature_loan_repayment_date
import androidclient.feature.loan.generated.resources.feature_loan_routing_code
import androidclient.feature.loan.generated.resources.feature_loan_select
import androidclient.feature.loan.generated.resources.feature_loan_select_all_penalties
import androidclient.feature.loan.generated.resources.feature_loan_show_payment_details
import androidclient.feature.loan.generated.resources.feature_loan_table_header_amount
import androidclient.feature.loan.generated.resources.feature_loan_table_header_fees
import androidclient.feature.loan.generated.resources.feature_loan_table_header_interest
import androidclient.feature.loan.generated.resources.feature_loan_waive_penalties
import androidclient.feature.loan.generated.resources.loan_new_loan_external_id
import androidclient.feature.loan.generated.resources.loan_officer
import androidclient.feature.loan.generated.resources.next
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosCheckBox
import com.mifos.core.ui.components.MifosDefaultListingComponentFromStringResources
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock

@Composable
internal fun LoanRepaymentScreen(
    navController: NavController,
    navigateBack: () -> Unit,
    viewmodel: LoanRepaymentViewModel = koinViewModel(),
) {
    val state by viewmodel.stateFlow.collectAsStateWithLifecycle()
    EventsEffect(viewmodel.eventFlow) { event ->
        when (event) {
            LoanRepaymentEvent.NavigationBack -> navigateBack()
        }
    }

    LoanRepaymentContent(
        navController = navController,
        state = state,
        onAction = { viewmodel.trySendAction(it) },
    )

    LoanRepaymentDialog(
        state = state,
        onAction = { viewmodel.trySendAction(it) },
    )
}

@Composable
fun LoanRepaymentDialog(
    state: LoanRepaymentState,
    onAction: (LoanRepaymentAction) -> Unit,
) {
    when (state.dialogState) {
        is LoanRepaymentState.DialogState.Error -> {
            MifosErrorComponent(
                message = state.dialogState.message,
                modifier = Modifier.background(KptTheme.colorScheme.background),
            )
        }

        is LoanRepaymentState.DialogState.Loading -> {
            if (state.dialogState.isOverlayLoading) {
                MifosProgressIndicatorOverlay()
            } else {
                MifosProgressIndicator()
            }
        }

        null -> Unit
    }
}

@Composable
fun LoanRepaymentContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    state: LoanRepaymentState,
    onAction: (LoanRepaymentAction) -> Unit,
) {
    val repaymentDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
    )
    if (state.showRepaymentDate) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(LoanRepaymentAction.UpdateRepaymentDate(false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(LoanRepaymentAction.UpdateRepaymentDate(false))
                        repaymentDatePickerState.selectedDateMillis?.let {
                            onAction(
                                LoanRepaymentAction.UpdateTransactionDate(
                                    DateHelper.getDateAsStringFromLong(it),
                                ),
                            )
                        }
                    },
                ) { Text(stringResource(Res.string.feature_loan_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(LoanRepaymentAction.UpdateRepaymentDate(false))
                    },
                ) { Text(stringResource(Res.string.feature_loan_cancel)) }
            },
        ) {
            DatePicker(state = repaymentDatePickerState)
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        MifosBreadcrumbNavBar(navController)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState())
                .padding(
                    horizontal = KptTheme.spacing.md,
                    vertical = KptTheme.spacing.sm,
                ),
        ) {
            state.clientName?.let {
                Text(
                    text = it,
                    style = MifosTypography.labelLargeEmphasized,
                )
            }
            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosDefaultListingComponentFromStringResources(
                data = mapOf(
                    Res.string.feature_loan_principal to state.loanRepaymentTemplate?.principalPortion.toString(),
                    Res.string.feature_loan_table_header_interest to state.loanRepaymentTemplate?.interestPortion.toString(),
                    Res.string.feature_loan_table_header_fees to state.loanRepaymentTemplate?.feeChargesPortion.toString(),
                    Res.string.feature_loan_penalties_short to state.penaltyAmount.toString(),
                ),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            )

            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosDatePickerTextField(
                value = state.transactionDate.orEmpty(),
                label = stringResource(Res.string.feature_loan_repayment_date),
                openDatePicker = {
                    onAction(LoanRepaymentAction.UpdateRepaymentDate(true))
                },
            )

            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.transactionAmount.orEmpty(),
                onValueChange = {
                    onAction(LoanRepaymentAction.UpdateTransactionAmount(it))
                },
                label = stringResource(Res.string.feature_loan_table_header_amount),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.externalId.orEmpty(),
                onValueChange = {
                    onAction(LoanRepaymentAction.UpdateExternalId(it))
                },
                label = stringResource(Res.string.loan_new_loan_external_id),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosTextFieldDropdown(
                value = if (state.paymentTypeIdIndex == null) {
                    ""
                } else {
                    state.loanRepaymentTemplate?.paymentTypeOptions?.get(state.paymentTypeIdIndex)?.name.toString()
                },
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(LoanRepaymentAction.UpdatePaymentTypeIdIndex(index))
                },
                options = state.loanRepaymentTemplate?.paymentTypeOptions?.map { it.name }
                    ?: emptyList(),
                label = stringResource(Res.string.loan_officer),
            )

            ShowPaymentDetails(
                state = state,
                onAction = onAction,
            )

            ShowPenalties(
                state = state,
                onAction = onAction,
            )

            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.note.orEmpty(),
                onValueChange = {
                    onAction(LoanRepaymentAction.UpdateNote(it))
                },
                config = MifosTextFieldConfig(
                    singleLine = false,
                ),
                label = stringResource(Res.string.feature_loan_note),
            )

            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosTwoButtonRow(
                firstBtnText = stringResource(Res.string.back),
                secondBtnText = stringResource(Res.string.next),
                onFirstBtnClick = {
                    onAction(LoanRepaymentAction.NavigateBack)
                },
                onSecondBtnClick = {
                    onAction(LoanRepaymentAction.SubmitRepayment)
                },
                modifier = Modifier.padding(
                    top = KptTheme.spacing.sm,
                    bottom = KptTheme.spacing.md,
                ),
            )
        }
    }
}

@Composable
private fun ShowPaymentDetails(
    state: LoanRepaymentState,
    onAction: (LoanRepaymentAction) -> Unit,
) {
    MifosCheckBox(
        checked = state.isShowPaymentDetail,
        onCheckChanged = {
            onAction(LoanRepaymentAction.UpdateIsShowPaymentDetails(it))
        },
        text = stringResource(Res.string.feature_loan_show_payment_details),
    )

    AnimatedVisibility(
        visible = state.isShowPaymentDetail,
    ) {
        Spacer(Modifier.height(KptTheme.spacing.md))

        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            MifosOutlinedTextField(
                value = state.accountNumber.orEmpty(),
                onValueChange = {
                    onAction(LoanRepaymentAction.UpdateAccountNumber(it))
                },
                label = stringResource(Res.string.feature_loan_account),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.checkNumber.orEmpty(),
                onValueChange = {
                    onAction(LoanRepaymentAction.UpdateCheckNumber(it))
                },
                label = stringResource(Res.string.feature_loan_cheque),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.routingCode.orEmpty(),
                onValueChange = {
                    onAction(LoanRepaymentAction.UpdateRoutingCode(it))
                },
                label = stringResource(Res.string.feature_loan_routing_code),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.receiptNumber.orEmpty(),
                onValueChange = {
                    onAction(LoanRepaymentAction.UpdateReceiptNumber(it))
                },
                label = stringResource(Res.string.feature_loan_receipt),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosOutlinedTextField(
                value = state.bankNumber.orEmpty(),
                onValueChange = {
                    onAction(LoanRepaymentAction.UpdateBankNumber(it))
                },
                label = stringResource(Res.string.feature_loan_bank),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
        }
    }
}

@Composable
private fun ShowPenalties(
    state: LoanRepaymentState,
    onAction: (LoanRepaymentAction) -> Unit,
) {
    MifosCheckBox(
        checked = state.isShowPenalties,
        onCheckChanged = {
            onAction(
                LoanRepaymentAction.UpdateIsShowPenalties(it),
            )
        },
        text = stringResource(
            Res.string.feature_loan_waive_penalties,
        ),
    )

    AnimatedVisibility(
        visible = state.isShowPenalties,
    ) {
        Column {
            if (state.loanCharges.isEmpty()) {
                MifosEmptyCard(
                    title = stringResource(Res.string.feature_loan_no_penalties_found),
                    msg = stringResource(Res.string.feature_loan_no_penalties_message),
                )
            } else {
                MifosCheckBox(
                    checked = state.selectAllPenalties,
                    onCheckChanged = {
                        onAction(
                            LoanRepaymentAction.ToggleAllPenalty(it),
                        )
                    },
                    text = stringResource(Res.string.feature_loan_select_all_penalties),
                )

                state.loanCharges.forEach { penalty ->
                    val isSelected =
                        penalty.id in state.selectedPenaltyIds ||
                            state.selectAllPenalties

                    Column {
                        MifosCheckBox(
                            checked = isSelected,
                            onCheckChanged = {
                                onAction(
                                    LoanRepaymentAction.TogglePenalty(
                                        penalty.id,
                                    ),
                                )
                            },
                            text = penalty.name ?: "",
                        )

                        AnimatedVisibility(
                            visible = isSelected,
                        ) {
                            penalty.dueDate?.let {
                                MifosDefaultListingComponentFromStringResources(
                                    data = mapOf(
                                        Res.string.feature_loan_due_date to DateHelper.getDateAsString(
                                            it,
                                        ),
                                        Res.string.feature_loan_outstanding_balance to penalty.amountOutstanding.toString(),
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
