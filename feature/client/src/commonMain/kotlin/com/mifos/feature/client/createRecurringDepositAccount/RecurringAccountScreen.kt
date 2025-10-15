/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createRecurringDepositAccount

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.createRecurringDepositAccount.pages.ChargesPage
import com.mifos.feature.client.createRecurringDepositAccount.pages.DetailsPage
import com.mifos.feature.client.createRecurringDepositAccount.pages.InterestPage
import com.mifos.feature.client.createRecurringDepositAccount.pages.SettingPage
import com.mifos.feature.client.createRecurringDepositAccount.pages.TermsPage

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
    val steps = remember {
        listOf(
            Step(name = "Details") {
                DetailsPage(
                    onNext = { onAction(RecurringAccountAction.NextStep) },
                )
            },
            Step(name = "Terms") {
                TermsPage(
                    onNext = { onAction(RecurringAccountAction.NextStep) },
                )
            },
            Step(name = "Settings") {
                SettingPage(
                    onNext = { onAction(RecurringAccountAction.NextStep) },
                )
            },
            Step(name = "Interest") {
                InterestPage(
                    onNext = { onAction(RecurringAccountAction.NextStep) },
                )
            },
            Step(name = "Charges") {
                ChargesPage(
                    onNext = { onAction(RecurringAccountAction.NextStep) },
                )
            },
        )
    }

    MifosScaffold(
        title = "Create Recurring Deposit Account",
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
