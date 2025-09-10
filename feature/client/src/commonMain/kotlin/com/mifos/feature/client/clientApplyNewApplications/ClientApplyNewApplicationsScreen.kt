/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientApplyNewApplications

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.account_balance_wallet
import androidclient.feature.client.generated.resources.calendar_month
import androidclient.feature.client.generated.resources.client_apply_new_applications_apply_fixed_account
import androidclient.feature.client.generated.resources.client_apply_new_applications_apply_loan_account
import androidclient.feature.client.generated.resources.client_apply_new_applications_apply_recurring_account
import androidclient.feature.client.generated.resources.client_apply_new_applications_apply_savings_account
import androidclient.feature.client.generated.resources.client_apply_new_applications_fixed_account
import androidclient.feature.client.generated.resources.client_apply_new_applications_loan_account
import androidclient.feature.client.generated.resources.client_apply_new_applications_recurring_account
import androidclient.feature.client.generated.resources.client_apply_new_applications_savings_account
import androidclient.feature.client.generated.resources.client_apply_new_applications_share_account
import androidclient.feature.client.generated.resources.client_apply_new_applications_title
import androidclient.feature.client.generated.resources.savings
import androidclient.feature.client.generated.resources.shield
import androidclient.feature.client.generated.resources.stacked_bar_chart
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosRowCard
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.util.TextUtil
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientApplyNewApplicationsScreen(
    onNavigateBack: () -> Unit,
    onNavigateApplyLoanAccount: (Int) -> Unit,
    onNavigateApplySavingsAccount: (Int) -> Unit,
    onNavigateApplyShareAccount: () -> Unit,
    onNavigateApplyRecurringAccount: () -> Unit,
    onNavigateApplyFixedAccount: () -> Unit,
    navController: NavController,
    viewModel: ClientApplyNewApplicationsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientApplyNewApplicationsEvent.NavigateBack -> onNavigateBack()
            is ClientApplyNewApplicationsEvent.OnActionClick -> {
                when (event.action) {
                    ClientApplyNewApplicationsItem.NewFixedAccount -> onNavigateApplyFixedAccount()
                    ClientApplyNewApplicationsItem.NewLoanAccount -> onNavigateApplyLoanAccount(state.clientId)
                    ClientApplyNewApplicationsItem.NewRecurringAccount -> onNavigateApplyRecurringAccount()
                    ClientApplyNewApplicationsItem.NewSavingsAccount -> onNavigateApplySavingsAccount(state.clientId)
                    ClientApplyNewApplicationsItem.NewShareAccount -> onNavigateApplyShareAccount()
                }
            }
        }
    }

    ClientApplyNewApplicationsContent(
        navController = navController,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
private fun ClientApplyNewApplicationsContent(
    navController: NavController,
    onAction: (ClientApplyNewApplicationsAction) -> Unit,
) {
    MifosScaffold(
        title = "",
        onBackPressed = {
            onAction(ClientApplyNewApplicationsAction.NavigateBack)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            MifosBreadcrumbNavBar(navController)
            Column(
                modifier = Modifier.padding(horizontal = DesignToken.padding.large),
            ) {
                Text(
                    text = stringResource(Res.string.client_apply_new_applications_title),
                    style = MifosTypography.labelLargeEmphasized,
                )

                Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

                clientApplyNewApplicationsItems.forEach {
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
                        rightValues = emptyList(),
                        modifier = Modifier
                            .clickable {
                                onAction(ClientApplyNewApplicationsAction.OnActionClick(it))
                            }
                            .padding(vertical = DesignToken.padding.medium),
                    )
                }
            }
        }
    }
}

sealed class ClientApplyNewApplicationsItem(
    val title: StringResource,
    val subTitle: StringResource,
    val icon: DrawableResource,
) {
    data object NewLoanAccount : ClientApplyNewApplicationsItem(
        title = Res.string.client_apply_new_applications_loan_account,
        subTitle = Res.string.client_apply_new_applications_apply_loan_account,
        icon = Res.drawable.account_balance_wallet,
    )

    data object NewSavingsAccount : ClientApplyNewApplicationsItem(
        title = Res.string.client_apply_new_applications_savings_account,
        subTitle = Res.string.client_apply_new_applications_apply_savings_account,
        icon = Res.drawable.savings,
    )

    data object NewShareAccount : ClientApplyNewApplicationsItem(
        title = Res.string.client_apply_new_applications_share_account,
        subTitle = Res.string.client_apply_new_applications_apply_loan_account,
        icon = Res.drawable.stacked_bar_chart,
    )

    data object NewRecurringAccount : ClientApplyNewApplicationsItem(
        title = Res.string.client_apply_new_applications_recurring_account,
        subTitle = Res.string.client_apply_new_applications_apply_recurring_account,
        icon = Res.drawable.calendar_month,
    )

    data object NewFixedAccount : ClientApplyNewApplicationsItem(
        title = Res.string.client_apply_new_applications_fixed_account,
        subTitle = Res.string.client_apply_new_applications_apply_fixed_account,
        icon = Res.drawable.shield,
    )
}

internal val clientApplyNewApplicationsItems: ImmutableList<ClientApplyNewApplicationsItem> =
    persistentListOf(
        ClientApplyNewApplicationsItem.NewLoanAccount,
        ClientApplyNewApplicationsItem.NewSavingsAccount,
        ClientApplyNewApplicationsItem.NewShareAccount,
        ClientApplyNewApplicationsItem.NewRecurringAccount,
        ClientApplyNewApplicationsItem.NewFixedAccount,
    )
