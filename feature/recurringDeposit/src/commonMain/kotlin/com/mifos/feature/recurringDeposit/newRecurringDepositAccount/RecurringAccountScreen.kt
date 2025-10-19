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
import androidclient.feature.recurringdeposit.generated.resources.create_recurring_deposit_account
import androidclient.feature.recurringdeposit.generated.resources.step_charges
import androidclient.feature.recurringdeposit.generated.resources.step_details
import androidclient.feature.recurringdeposit.generated.resources.step_interest
import androidclient.feature.recurringdeposit.generated.resources.step_settings
import androidclient.feature.recurringdeposit.generated.resources.step_terms
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.ChargesPage
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.DetailsPage
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.InterestPage
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.SettingPage
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.TermsPage
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RecurringAccountScreen(
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecurringAccountViewModel = viewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            RecurringAccountEvent.NavigateBack -> onNavigateBack()
            RecurringAccountEvent.Finish -> onFinish()
        }
    }

    RecurringAccountScaffold(
        modifier = modifier,
        state = state,
        onAction = { viewModel.trySendAction(it) },
    )
}

@Composable
private fun RecurringAccountScaffold(
    state: RecurringAccountState,
    modifier: Modifier = Modifier,
    onAction: (RecurringAccountAction) -> Unit,
) {
    val steps = listOf(
        Step(name = stringResource(Res.string.step_details)) {
            DetailsPage(
                onNext = { onAction(RecurringAccountAction.NextStep) },
            )
        },
        Step(name = stringResource(Res.string.step_terms)) {
            TermsPage(
                onNext = { onAction(RecurringAccountAction.NextStep) },
            )
        },
        Step(name = stringResource(Res.string.step_settings)) {
            SettingPage(
                onNext = { onAction(RecurringAccountAction.NextStep) },
            )
        },
        Step(name = stringResource(Res.string.step_interest)) {
            InterestPage(
                onNext = { onAction(RecurringAccountAction.NextStep) },
            )
        },
        Step(name = stringResource(Res.string.step_charges)) {
            ChargesPage(
                onNext = { onAction(RecurringAccountAction.NextStep) },
            )
        },
    )

    MifosScaffold(
        title = stringResource(Res.string.create_recurring_deposit_account),
        onBackPressed = { onAction(RecurringAccountAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        if (state.dialogState == null) {
            MifosStepper(
                steps = steps,
                currentIndex = state.currentStep,
                onStepChange = { newIndex ->
                    onAction(RecurringAccountAction.OnStepChange(newIndex))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
            )
        }
    }
}
