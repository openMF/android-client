/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.recurringDeposit.newRecurringDepositAccount

import androidclient.feature.recurringdeposit.generated.resources.Res
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_create_recurring_deposit_account
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_charges
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_details
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_interest
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_settings
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_terms
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountAction.NavigateToStep
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.ChargesPage
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.DetailsPage
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.InterestPage
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.SettingPage
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.TermsPage
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun RecurringAccountScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecurringAccountViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            RecurringAccountEvent.NavigateBack -> onNavigateBack()
            RecurringAccountEvent.Finish -> onFinish()
        }
    }

    RecurringAccountScaffold(
        navController = navController,
        modifier = modifier,
        state = state,
        onAction = { viewModel.trySendAction(it) },
    )
}

@Composable
private fun RecurringAccountScaffold(
    navController: NavController,
    state: RecurringAccountState,
    modifier: Modifier = Modifier,
    onAction: (RecurringAccountAction) -> Unit,
) {
    val steps = listOf(
        Step(name = stringResource(Res.string.feature_recurring_deposit_step_details)) {
            DetailsPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(name = stringResource(Res.string.feature_recurring_deposit_step_terms)) {
            TermsPage(
                onNext = { onAction(RecurringAccountAction.OnNextPress) },
            )
        },
        Step(name = stringResource(Res.string.feature_recurring_deposit_step_settings)) {
            SettingPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(name = stringResource(Res.string.feature_recurring_deposit_step_interest)) {
            InterestPage(
                onNext = { onAction(RecurringAccountAction.OnNextPress) },
            )
        },
        Step(name = stringResource(Res.string.feature_recurring_deposit_step_charges)) {
            ChargesPage(
                onNext = { onAction(RecurringAccountAction.OnNextPress) },
            )
        },
    )

    MifosScaffold(
        title = stringResource(Res.string.feature_recurring_deposit_create_recurring_deposit_account),
        onBackPressed = { onAction(RecurringAccountAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
        ) {
            MifosBreadcrumbNavBar(navController)
            when (state.screenState) {
                is RecurringAccountState.ScreenState.Error -> {
                    MifosErrorComponent(
                        message = state.screenState.message,
                        isRetryEnabled = true,
                    ) {
                        onAction(RecurringAccountAction.Retry)
                    }
                }

                is RecurringAccountState.ScreenState.Loading -> {
                    MifosProgressIndicator()
                }

                is RecurringAccountState.ScreenState.Success -> {
                    MifosStepper(
                        steps = steps,
                        currentIndex = state.currentStep,
                        onStepChange = { newIndex ->
                            onAction(NavigateToStep(newIndex))
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }
            }
        }
    }
}
