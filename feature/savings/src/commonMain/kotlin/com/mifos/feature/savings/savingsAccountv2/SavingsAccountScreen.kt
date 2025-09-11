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
import androidclient.feature.savings.generated.resources.feature_savings_error_not_connected_internet
import androidclient.feature.savings.generated.resources.step_charges
import androidclient.feature.savings.generated.resources.step_details
import androidclient.feature.savings.generated.resources.step_preview
import androidclient.feature.savings.generated.resources.step_terms
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.savings.savingsAccountv2.pages.ChargesPage
import com.mifos.feature.savings.savingsAccountv2.pages.DetailsPage
import com.mifos.feature.savings.savingsAccountv2.pages.PreviewPage
import com.mifos.feature.savings.savingsAccountv2.pages.TermsPage
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SavingsAccountScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavingsAccountViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SavingsAccountEvent.NavigateBack -> onNavigateBack()
            SavingsAccountEvent.Finish -> onFinish()
        }
    }

    NewSavingsAccountDialog(
        state = state,
        onAction = { viewModel.trySendAction(it) },
    )

    SavingsAccountScaffold(
        modifier = modifier,
        state = state,
        onAction = { viewModel.trySendAction(it) },
        navController = navController,
    )
}

@Composable
private fun SavingsAccountScaffold(
    navController: NavController,
    state: SavingsAccountState,
    modifier: Modifier = Modifier,
    onAction: (SavingsAccountAction) -> Unit,
) {
    val steps = listOf(
        Step(stringResource(Res.string.step_details)) {
            DetailsPage(
                state = state,
                onAction = onAction,
            )
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
        when (state.screenState) {
            is SavingsAccountState.ScreenState.Loading -> MifosProgressIndicator()
            is SavingsAccountState.ScreenState.Success -> {
                Column(
                    Modifier.fillMaxSize().padding(paddingValues),
                ) {
                    MifosBreadcrumbNavBar(
                        navController,
                    )
                    MifosStepper(
                        steps = steps,
                        currentIndex = state.currentStep,
                        onStepChange = { newIndex ->
                            onAction(SavingsAccountAction.OnStepChange(newIndex))
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }
            }
            is SavingsAccountState.ScreenState.NetworkError -> {
                MifosSweetError(
                    message = stringResource(Res.string.feature_savings_error_not_connected_internet),
                    onclick = { onAction(SavingsAccountAction.Retry) },
                )
            }
        }
        if (state.isOverLayLoadingActive) {
            MifosProgressIndicatorOverlay()
        }
    }
}

@Composable
private fun NewSavingsAccountDialog(
    state: SavingsAccountState,
    onAction: (SavingsAccountAction) -> Unit,
) {
    when (state.dialogState) {
        is SavingsAccountState.DialogState.Error -> {
            MifosSweetError(
                message = state.dialogState.message,
                onclick = { onAction(SavingsAccountAction.Retry) },
            )
        }
        null -> Unit
    }
}
