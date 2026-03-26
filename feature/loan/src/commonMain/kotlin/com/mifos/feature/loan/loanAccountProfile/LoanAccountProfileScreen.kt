/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountProfile

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_profile_account
import androidclient.feature.loan.generated.resources.feature_loan_profile_action_repayment
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_details_not_found
import androidclient.feature.loan.generated.resources.feature_loan_profile_label_arrears
import androidclient.feature.loan.generated.resources.feature_loan_profile_label_balance
import androidclient.feature.loan.generated.resources.feature_loan_profile_label_client_name_placeholder
import androidclient.feature.loan.generated.resources.feature_loan_profile_label_overpaid_by
import androidclient.feature.loan.generated.resources.feature_loan_profile_section_account_overview
import androidclient.feature.loan.generated.resources.feature_loan_profile_section_actions_details
import androidclient.feature.loan.generated.resources.feature_loan_profile_status_active
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosCard
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosRowCard
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.util.TextUtil
import com.mifos.feature.loan.loanAccountProfile.components.LoanAccountProfileActionItem
import com.mifos.feature.loan.loanAccountProfile.components.loanProfileActionItems
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.accounts.loans.LoansAccountSummaryEntity
import com.mifos.room.entities.accounts.savings.SavingAccountCurrencyEntity
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun LoanAccountProfileScreen(
    onNavigateBack: () -> Unit,
    approveLoan: (Int, LoanWithAssociationsEntity) -> Unit,
    onRepaymentClick: (LoanWithAssociationsEntity) -> Unit,
    navigateToRepaymentSchedule: (Int) -> Unit,
    navigateToTransactions: (Int) -> Unit,
    navigateToCharges: (Int) -> Unit,
    navigateToDocuments: (Int) -> Unit,
    navigateToReschedules: (Int) -> Unit,
    navigateToNotes: (Int) -> Unit,
    navigateToTransferScreen: (loanId: Int, accountNumber: String, clientId: Int, currencyCode: String, officeId: Int) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: LoanAccountProfileViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanAccountEvent.NavigateBack -> onNavigateBack.invoke()
            is LoanAccountEvent.NavigateToAction -> {
                val account = state.loanAccount ?: return@EventsEffect

                when (event.action) {
                    LoanProfileAction.Approve -> approveLoan(account.id, account)
                    LoanProfileAction.Repayment -> onRepaymentClick(account)
                    LoanProfileAction.Transfer -> {
                        val account = state.loanAccount ?: return@EventsEffect
                        navigateToTransferScreen(
                            account.id,
                            account.accountNo,
                            account.clientId,
                            account.currency.code ?: "N/A",
                            account.clientOfficeId,
                        )
                    }
                }
            }
            is LoanAccountEvent.NavigateToDetail -> {
                val loanId = state.loanAccount?.id ?: -1

                when (event.detailItem) {
                    LoanAccountProfileActionItem.RepaymentSchedule -> navigateToRepaymentSchedule(loanId)
                    LoanAccountProfileActionItem.Transactions -> navigateToTransactions(loanId)
                    LoanAccountProfileActionItem.Charges -> navigateToCharges(loanId)
                    LoanAccountProfileActionItem.Documents -> navigateToDocuments(loanId)
                    LoanAccountProfileActionItem.Reschedules -> navigateToReschedules(loanId)
                    LoanAccountProfileActionItem.Notes -> navigateToNotes(loanId)
                    else -> { }
                }
            }
            LoanAccountEvent.NavigateToAccountDetails -> {}
        }
    }

    if (state.dialogState == null) {
        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            MifosBreadcrumbNavBar(navController)

            state.loanAccount?.let { loanAccount ->
                LoanAccountContent(
                    state = state,
                    onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
                )
            }
        }
    }

    LoanAccountDialogs(
        state = state,
        onRetry = remember(viewModel) { { viewModel.trySendAction(LoanAccountAction.OnRetry) } },
    )
}

@Composable
private fun LoanAccountContent(
    state: LoanAccountState,
    modifier: Modifier = Modifier,
    onAction: (LoanAccountAction) -> Unit,
) {
    val loanAccount = state.loanAccount ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = KptTheme.spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.feature_loan_profile_account),
            style = MifosTypography.labelLargeEmphasized,
        )

        Spacer(Modifier.height(DesignToken.padding.medium))

        LoanAccountTopCard(
            loanAccount = loanAccount,
            statusUi = state.statusUiModel,
            onClick = { onAction(LoanAccountAction.OnAccountClick) },
        )

        Spacer(Modifier.height(KptTheme.spacing.md))

        MifosButton(
            onClick = { onAction(LoanAccountAction.OnNextActionClick) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(state.nextActionButtonRes),
                style = MifosTypography.labelMediumEmphasized,
                color = AppColors.customWhite,
            )
        }

        Spacer(Modifier.height(KptTheme.spacing.lg))

        Text(
            text = stringResource(Res.string.feature_loan_profile_section_actions_details),
            style = MifosTypography.labelMediumEmphasized,
        )

        Spacer(Modifier.height(DesignToken.padding.medium))

        loanProfileActionItems.forEach { item ->
            MifosRowCard(
                title = stringResource(item.title),
                imageVector = item.icon,
                leftValues = listOf(
                    TextUtil(
                        text = stringResource(item.subTitle),
                        style = MifosTypography.bodySmall,
                        color = KptTheme.colorScheme.secondary,
                    ),
                ),
                rightValues = emptyList(),
                modifier = Modifier
                    .clickable { onAction(LoanAccountAction.OnDetailItemClick(item)) }
                    .padding(vertical = DesignToken.padding.medium),
            )
        }

        Spacer(Modifier.height(KptTheme.spacing.md))
    }
}

@Composable
private fun LoanAccountTopCard(
    loanAccount: LoanWithAssociationsEntity,
    statusUi: LoanStatusUiModel?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currencyCode = loanAccount.currency?.code
    val decimalPlaces = loanAccount.currency?.decimalPlaces

    val balance = CurrencyFormatter.format(loanAccount.summary.totalOutstanding, currencyCode, decimalPlaces)
    val arrears = CurrencyFormatter.format(loanAccount.summary.totalOverdue, currencyCode, decimalPlaces)
    val overpaid = CurrencyFormatter.format(loanAccount.totalOverpaid, currencyCode, decimalPlaces)

    MifosCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardColors(
            containerColor = KptTheme.colorScheme.primary,
            contentColor = AppColors.customWhite,
            disabledContainerColor = KptTheme.colorScheme.primary,
            disabledContentColor = AppColors.customWhite,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.lg),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${loanAccount.loanProductName?.uppercase().orEmpty()} ${loanAccount.accountNo}".trim(),
                        style = MifosTypography.titleMediumEmphasized,
                        color = AppColors.customWhite,
                    )

                    Spacer(Modifier.height(KptTheme.spacing.xs))

                    Text(
                        text = loanAccount.clientName ?: stringResource(Res.string.feature_loan_profile_label_client_name_placeholder),
                        style = MifosTypography.bodyMedium,
                        color = AppColors.customWhite.copy(alpha = 0.8f),
                    )
                }

                Icon(
                    imageVector = MifosIcons.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(DesignToken.sizes.iconSmall),
                    tint = KptTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                )
            }

            Spacer(Modifier.height(DesignToken.padding.medium))

            statusUi?.let { ui ->
                Box(
                    modifier = Modifier
                        .clip(KptTheme.shapes.large)
                        .background(ui.color)
                        .padding(horizontal = DesignToken.padding.medium, vertical = KptTheme.spacing.xs),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(ui.labelRes).uppercase(),
                        color = AppColors.customWhite,
                        style = MifosTypography.labelSmallEmphasized,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(KptTheme.spacing.md))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(KptTheme.shapes.medium)
                    .background(AppColors.customWhite.copy(alpha = 0.15f))
                    .padding(KptTheme.spacing.md),
            ) {
                Text(
                    text = stringResource(Res.string.feature_loan_profile_section_account_overview),
                    style = MifosTypography.labelSmallEmphasized,
                    color = AppColors.customWhite.copy(alpha = 0.8f),
                )

                Spacer(Modifier.height(DesignToken.padding.medium))

                OverviewRow(stringResource(Res.string.feature_loan_profile_label_balance), balance)
                OverviewRow(stringResource(Res.string.feature_loan_profile_label_arrears), arrears)

                OverviewRow(
                    label = stringResource(Res.string.feature_loan_profile_label_overpaid_by),
                    value = overpaid,
                    valueColor = AppColors.loanActiveStatus,
                )
            }
        }
    }
}

@Composable
private fun OverviewRow(
    label: String,
    value: String,
    valueColor: Color = AppColors.customWhite,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DesignToken.padding.extraExtraSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MifosTypography.bodySmall,
            color = AppColors.customWhite.copy(alpha = 0.9f),
        )
        Text(
            text = value,
            style = MifosTypography.labelMediumEmphasized,
            color = valueColor,
        )
    }
}

@Composable
private fun LoanAccountDialogs(
    state: LoanAccountState,
    onRetry: () -> Unit,
) {
    when (state.dialogState) {
        is LoanAccountState.DialogState.Loading -> MifosProgressIndicator()
        is LoanAccountState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = stringResource(state.dialogState.message),
                isRetryEnabled = true,
                onRetry = onRetry,
            )
        }
        null -> Unit
    }
}

private class LoanAccountPreviewProvider : PreviewParameterProvider<LoanAccountState> {
    override val values: Sequence<LoanAccountState>
        get() = sequenceOf(
            LoanAccountState(
                loanAccount = LoanWithAssociationsEntity(
                    id = 1,
                    accountNo = "000000018",
                    clientName = "MARIA",
                    loanProductName = "PERSONAL",
                    totalOverpaid = 0.0,
                    currency = SavingAccountCurrencyEntity(
                        code = "USD",
                        decimalPlaces = 2,
                    ),
                    summary = LoansAccountSummaryEntity(
                        totalOutstanding = 1500.00,
                        totalOverdue = 0.00,
                    ),
                    status = LoanStatusEntity(
                        active = true,
                        pendingApproval = false,
                        overpaid = false,
                        value = "Active",
                    ),
                ),
                statusUiModel = LoanStatusUiModel(
                    labelRes = Res.string.feature_loan_profile_status_active,
                    color = AppColors.loanActiveStatus,
                ),
                nextActionButtonRes = Res.string.feature_loan_profile_action_repayment,
                dialogState = null,
            ),
            LoanAccountState(
                dialogState = LoanAccountState.DialogState.Loading,
            ),
            LoanAccountState(
                dialogState = LoanAccountState.DialogState.Error(Res.string.feature_loan_profile_error_details_not_found),
            ),
        )
}

@Composable
@Preview(showBackground = true)
private fun LoanAccountProfileScreenPreview(
    @PreviewParameter(LoanAccountPreviewProvider::class) state: LoanAccountState,
) {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.dialogState == null && state.loanAccount != null) {
                LoanAccountContent(
                    state = state,
                    onAction = {},
                )
            } else {
                LoanAccountDialogs(
                    state = state,
                    onRetry = {},
                )
            }
        }
    }
}
