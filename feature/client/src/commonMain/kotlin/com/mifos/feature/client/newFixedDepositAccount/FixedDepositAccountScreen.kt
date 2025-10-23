/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.newFixedDepositAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.step_charges
import androidclient.feature.client.generated.resources.step_details
import androidclient.feature.client.generated.resources.step_interest
import androidclient.feature.client.generated.resources.step_settings
import androidclient.feature.client.generated.resources.step_terms
import androidclient.feature.client.generated.resources.title_new_fixed_deposit_account
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.newFixedDepositAccount.pages.ChargesPage
import com.mifos.feature.client.newFixedDepositAccount.pages.DetailsPage
import com.mifos.feature.client.newFixedDepositAccount.pages.InterestPage
import com.mifos.feature.client.newFixedDepositAccount.pages.SettingPage
import com.mifos.feature.client.newFixedDepositAccount.pages.TermsPage
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FixedDepositAccountScreen(
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewFixedDepositAccountViewmodel = viewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            NewFixedDepositAccountEvent.NavigateBack -> onNavigateBack()
            NewFixedDepositAccountEvent.Finish -> onFinish()
        }
    }
    FixedDepositAccountScaffold(
        state = state,
        onAction = { viewModel.trySendAction(it) },
        modifier = modifier,
    )
}

@Composable
private fun FixedDepositAccountScaffold(
    state: NewFixedDepositAccountState,
    onAction: (NewFixedDepositAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val steps =
        listOf(
            Step(stringResource(Res.string.step_details)) {
                DetailsPage(
                    onNext = { onAction(NewFixedDepositAccountAction.NextStep) },
                )
            },
            Step(name = stringResource(Res.string.step_terms)) {
                TermsPage(
                    onNext = { onAction(NewFixedDepositAccountAction.NextStep) },
                )
            },

            Step(name = stringResource(Res.string.step_settings)) {
                SettingPage(
                    onNext = { onAction(NewFixedDepositAccountAction.NextStep) },
                )
            },
            Step(name = stringResource(Res.string.step_interest)) {
                InterestPage(
                    onNext = { onAction(NewFixedDepositAccountAction.NextStep) },
                )
            },
            Step(stringResource(Res.string.step_charges)) {
                ChargesPage(
                    onNext = { onAction(NewFixedDepositAccountAction.NextStep) },
                )
            },
        )

    MifosScaffold(
        title = stringResource(Res.string.title_new_fixed_deposit_account),
        onBackPressed = { onAction(NewFixedDepositAccountAction.NavigateBack) },
        modifier = modifier,

    ) { paddingValues ->
        if (state.dialogState == null) {
            MifosStepper(
                steps = steps,
                currentIndex = state.currentStep,
                onStepChange = { newIndex ->
                    onAction(NewFixedDepositAccountAction.OnStepChange(newIndex))
                },
                modifier = Modifier
                    .fillMaxWidth(),

            )
        }
    }
}
