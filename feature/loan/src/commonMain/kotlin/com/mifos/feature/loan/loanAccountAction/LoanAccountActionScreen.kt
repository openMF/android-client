/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountAction

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_action_empty
import androidclient.feature.loan.generated.resources.feature_loan_header_actions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosErrorContent
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosRowCard
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.util.TextUtil
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun LoanAccountActionScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onActionSelected: (LoanAccountActionItem, Int) -> Unit,
    viewModel: LoanAccountActionsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanAccountActionsEvent.NavigateBack -> onNavigateBack.invoke()
            is LoanAccountActionsEvent.NavigateToAction -> {
                onActionSelected(event.action, event.loanId)
            }
        }
    }

    LoanAccountActionContent(
        state = state,
        onAction = viewModel::trySendAction,
        navController = navController,
    )
}

@Composable
private fun LoanAccountActionContent(
    state: LoanAccountActionsState,
    onAction: (LoanAccountActionsAction) -> Unit,
    navController: NavController,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MifosBreadcrumbNavBar(navController)
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            when (state.viewState) {
                is LoanAccountActionsState.ViewState.Empty -> {
                    MifosErrorContent(
                        message = stringResource(Res.string.feature_loan_action_empty),
                    )
                }

                is LoanAccountActionsState.ViewState.Loading -> MifosProgressIndicator()

                is LoanAccountActionsState.ViewState.Error -> {
                    MifosErrorComponent(
                        isNetworkConnected = state.networkConnection,
                        message = stringResource(state.viewState.message),
                        isRetryEnabled = true,
                        onRetry = { onAction(LoanAccountActionsAction.OnRetry) },
                    )
                }

                is LoanAccountActionsState.ViewState.Content -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = KptTheme.spacing.md),
                    ) {
                        item {
                            Text(
                                text = stringResource(Res.string.feature_loan_header_actions),
                                style = MifosTypography.labelMediumEmphasized,
                            )

                            Spacer(Modifier.height(DesignToken.padding.medium))
                        }

                        items(
                            items = state.viewState.actions,
                            key = {
                                it.id
                            },
                        ) { item ->
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
                                    .clickable {
                                        onAction(
                                            LoanAccountActionsAction.OnLoanAccountActionItemClick(
                                                item,
                                            ),
                                        )
                                    }
                                    .padding(vertical = DesignToken.padding.medium),
                            )
                        }

                        item {
                            Spacer(Modifier.height(KptTheme.spacing.md))
                        }
                    }
                }
            }
        }
    }
}
