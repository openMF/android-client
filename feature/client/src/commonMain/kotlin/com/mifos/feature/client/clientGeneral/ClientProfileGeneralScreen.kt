/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientGeneral

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_performance_history_active_loans_count_label
import androidclient.feature.client.generated.resources.client_performance_history_active_savings_label
import androidclient.feature.client.generated.resources.client_performance_history_last_loan_amount_label
import androidclient.feature.client.generated.resources.client_performance_history_loan_cycle_count_label
import androidclient.feature.client.generated.resources.client_performance_history_total_savings_label
import androidclient.feature.client.generated.resources.client_profile_general_header_actions
import androidclient.feature.client.generated.resources.client_profile_general_header_linked_accounts
import androidclient.feature.client.generated.resources.client_profile_general_header_performance_history
import androidclient.feature.client.generated.resources.client_savings_not_avilable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosRowCard
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.util.TextUtil
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientProfileGeneralScreen(
    onNavigateBack: () -> Unit,
    upcomingCharges: (Int) -> Unit,
    loanAccounts: (Int) -> Unit,
    savingAccounts: (Int) -> Unit,
    fixedDepositAccounts: (Int) -> Unit,
    recurringDepositAccounts: (Int) -> Unit,
    sharesAccounts: (Int) -> Unit,
    collateralData: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientProfileGeneralViewmodel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientProfileGeneralEvent.NavigateBack -> {
                onNavigateBack.invoke()
            }

            is ClientProfileGeneralEvent.OnActionClick -> {
                when (event.action) {
                    ClientProfileGeneralActionItem.CollateralData -> collateralData(
                        state.client?.id ?: -1,
                    )

                    ClientProfileGeneralActionItem.FixedDepositAccounts -> fixedDepositAccounts(
                        state.client?.id ?: -1,
                    )

                    ClientProfileGeneralActionItem.LoanAccount -> loanAccounts(
                        state.client?.id ?: -1,
                    )

                    ClientProfileGeneralActionItem.RecurringDepositAccounts -> recurringDepositAccounts(
                        state.client?.id ?: -1,
                    )

                    ClientProfileGeneralActionItem.SavingAccounts -> savingAccounts(
                        state.client?.id ?: -1,
                    )

                    ClientProfileGeneralActionItem.SharesAccounts -> sharesAccounts(
                        state.client?.id ?: -1,
                    )

                    ClientProfileGeneralActionItem.UpcomingCharges -> upcomingCharges(
                        state.client?.id ?: -1,
                    )
                }
            }
        }
    }

    ClientProfileGeneralScaffold(
        state = state,
        modifier = modifier,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    ClientProfileGeneralDialogs(
        state = state,
        onRetry = remember(viewModel) {
            {
                viewModel.trySendAction(ClientProfileGeneralAction.OnRetry)
            }
        },
    )
}

@Composable
private fun ClientProfileGeneralDialogs(
    state: ClientProfileGeneralState,
    onRetry: () -> Unit,
) {
    when (state.dialogState) {
        is ClientProfileGeneralState.DialogState.Loading -> MifosProgressIndicator()

        is ClientProfileGeneralState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = {
                    onRetry()
                },
            )
        }
        null -> Unit
    }
}

@Composable
internal fun ClientProfileGeneralScaffold(
    state: ClientProfileGeneralState,
    onAction: (ClientProfileGeneralAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        title = stringResource(Res.string.client_profile_general_header_linked_accounts),
        onBackPressed = { onAction(ClientProfileGeneralAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        if (state.dialogState == null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
                    .verticalScroll(rememberScrollState()).padding(
                        vertical = DesignToken.padding.extraLarge,
                        horizontal = DesignToken.padding.large,
                    ),
            ) {
                Text(
                    stringResource(Res.string.client_profile_general_header_performance_history),
                    style = MaterialTheme.typography.labelLarge,
                )

                Spacer(Modifier.height(DesignToken.spacing.medium))

                PerformanceHistoryCard(
                    state = state,
                )

                Spacer(Modifier.height(DesignToken.spacing.largeIncreased))

                Text(
                    stringResource(Res.string.client_profile_general_header_actions),
                    style = MaterialTheme.typography.labelLarge,
                )

                Spacer(Modifier.height(DesignToken.spacing.small))

                clientProfileGeneralActions.forEach {
                    MifosRowCard(
                        title = stringResource(it.title),
                        imageVector = it.icon,
                        leftValues = listOf(
                            TextUtil(
                                text = stringResource(it.subTitle),
                                style = MifosTypography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                            ),
                        ),
                        rightValues = listOf(
                            TextUtil(
                                // TODO: The count values for each action has to derived from api.
                                // Placeholder values.
//                                text = "12",
                                text = "",
                                style = MifosTypography.bodySmall,
                                color = AppColors.customEnable,
                            ),
                        ),
                        modifier = Modifier.padding(vertical = DesignToken.padding.medium)
                            .clickable {
                                onAction(
                                    ClientProfileGeneralAction.OnActionClick(it),
                                )
                            },
                    )
                }
            }
        }
    }
}

@Composable
fun PerformanceHistoryCard(state: ClientProfileGeneralState) {
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().clip(
            RoundedCornerShape(12),
        ).background(MaterialTheme.colorScheme.primary)
            .padding(DesignToken.padding.largeIncreasedExtra),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                DesignToken.spacing.small,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val performanceHistory = state.performanceHistory
            PerformanceHistoryRows(
                stringResource(Res.string.client_performance_history_loan_cycle_count_label),
                performanceHistory.loanCyclesCount?.toString()
                    ?: stringResource(Res.string.client_savings_not_avilable),

            )

            PerformanceHistoryRows(
                stringResource(Res.string.client_performance_history_active_loans_count_label),
                performanceHistory.activeLoans?.toString()
                    ?: stringResource(Res.string.client_savings_not_avilable),
            )

            PerformanceHistoryRows(
                stringResource(Res.string.client_performance_history_last_loan_amount_label),

                performanceHistory.lastLoanAmount?.let {
                    state.currency + " " + it.toString()
                }
                    ?: stringResource(Res.string.client_savings_not_avilable),
            )

            PerformanceHistoryRows(
                stringResource(Res.string.client_performance_history_active_savings_label),
                performanceHistory.activeSavingsCount?.toString()
                    ?: stringResource(Res.string.client_savings_not_avilable),
            )

            PerformanceHistoryRows(
                stringResource(Res.string.client_performance_history_total_savings_label),
                performanceHistory.totalSaving?.let {
                    state.currency + " " + it.toString()
                }
                    ?: stringResource(Res.string.client_savings_not_avilable),
            )
        }
    }
}

@Composable
fun PerformanceHistoryRows(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        color = AppColors.customWhite,
        fontStyle = MaterialTheme.typography.labelMedium.fontStyle,
    ),
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = textStyle)
        Text(value, style = textStyle)
    }
}
