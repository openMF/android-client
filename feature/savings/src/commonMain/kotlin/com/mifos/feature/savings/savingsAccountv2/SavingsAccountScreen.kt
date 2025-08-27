/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountv2

import androidclient.feature.savings.generated.resources.Res
import androidclient.feature.savings.generated.resources.feature_savings_create_savings_account
import androidclient.feature.savings.generated.resources.step_charges
import androidclient.feature.savings.generated.resources.step_details
import androidclient.feature.savings.generated.resources.step_preview
import androidclient.feature.savings.generated.resources.step_terms
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.savings.savingsAccountv2.pages.ChargesPage
import com.mifos.feature.savings.savingsAccountv2.pages.DetailsPage
import com.mifos.feature.savings.savingsAccountv2.pages.PreviewPage
import com.mifos.feature.savings.savingsAccountv2.pages.TermsPage
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SavingsAccountScreen(
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavingsAccountViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SavingsAccountEvent.NavigateBack -> onNavigateBack()
            SavingsAccountEvent.Finish -> onFinish()
        }
    }

    SavingsAccountScaffold(
        modifier = modifier,
        state = state,
        onAction = { viewModel.trySendAction(it) },
    )
}

@Composable
private fun SavingsAccountScaffold(
    state: SavingsAccountState,
    modifier: Modifier = Modifier,
    onAction: (SavingsAccountAction) -> Unit,
) {
    val steps = listOf(
        Step(stringResource(Res.string.step_details)) {
            DetailsPage {
                onAction(SavingsAccountAction.NextStep)
            }
        },
        Step(stringResource(Res.string.step_terms)) {
            TermsPage {
                onAction(SavingsAccountAction.NextStep)
            }
        },
        Step(stringResource(Res.string.step_charges)) {
            ChargesPage {
                onAction(SavingsAccountAction.NextStep)
            }
        },
        Step(stringResource(Res.string.step_preview)) {
            PreviewPage {
                onAction(SavingsAccountAction.NextStep)
            }
        },
    )

    MifosScaffold(
        title = stringResource(Res.string.feature_savings_create_savings_account),
        onBackPressed = { onAction(SavingsAccountAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        if (state.dialogState == null) {
            MifosStepper(
                steps = steps,
                currentIndex = state.currentStep,
                onStepChange = { newIndex ->
                    onAction(SavingsAccountAction.OnStepChange(newIndex))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
            )
        }
    }
}
