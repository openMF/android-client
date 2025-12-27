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
import androidclient.feature.client.generated.resources.step_preview
import androidclient.feature.client.generated.resources.step_settings
import androidclient.feature.client.generated.resources.step_terms
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.newFixedDepositAccount.pages.AddNewChargeDialog
import com.mifos.feature.client.newFixedDepositAccount.pages.ChargesPage
import com.mifos.feature.client.newFixedDepositAccount.pages.DetailsPage
import com.mifos.feature.client.newFixedDepositAccount.pages.FixedDepositRateChart
import com.mifos.feature.client.newFixedDepositAccount.pages.InterestPage
import com.mifos.feature.client.newFixedDepositAccount.pages.PreviewPage
import com.mifos.feature.client.newFixedDepositAccount.pages.SettingPage
import com.mifos.feature.client.newFixedDepositAccount.pages.ShowChargesDialog
import com.mifos.feature.client.newFixedDepositAccount.pages.TermsPage
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CreateFixedDepositAccountScreen(
    onNavigateBack: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CreateFixedDepositAccountViewmodel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            NewFixedDepositAccountEvent.NavigateBack -> onNavigateBack()
        }
    }
    CreateFixedDepositAccountScaffold(
        navController = navController,
        state = state,
        onAction = { viewModel.trySendAction(it) },
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
    CreateFixedDepositAccountDialog(
        state = state,
        onAction = { viewModel.trySendAction(it) },
        snackbarHostState = snackbarHostState,
    )
}

@Composable
internal fun CreateFixedDepositAccountDialog(
    state: NewFixedDepositAccountState,
    onAction: (NewFixedDepositAccountAction) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    when (state.dialogState) {
        NewFixedDepositAccountState.DialogState.RateChartDialog -> {
            FixedDepositRateChart(
                state = state,
                onAction = onAction,
            )
        }

        null -> Unit
        is NewFixedDepositAccountState.DialogState.AddNewCharge -> {
            AddNewChargeDialog(
                state = state,
                isEdit = state.dialogState.edit,
                index = state.dialogState.index,
                onAction = onAction,
            )
        }

        NewFixedDepositAccountState.DialogState.ShowCharges -> {
            ShowChargesDialog(
                state = state,
                onAction = onAction,
            )
        }

        is NewFixedDepositAccountState.DialogState.SuccessResponseStatus -> {
            LaunchedEffect(state.launchEffectKey) {
                snackbarHostState.showSnackbar(
                    message = state.dialogState.msg,
                )

                if (state.dialogState.successStatus) {
                    delay(1000)
                    onAction(NewFixedDepositAccountAction.NavigateBack)
                }
            }
        }
    }
}

@Composable
private fun CreateFixedDepositAccountScaffold(
    navController: NavController,
    state: NewFixedDepositAccountState,
    onAction: (NewFixedDepositAccountAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
) {
    val steps = listOf(
        Step(stringResource(Res.string.step_details)) {
            DetailsPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(name = stringResource(Res.string.step_terms)) {
            TermsPage(
                state = state,
                onAction = onAction,
            )
        },

        Step(name = stringResource(Res.string.step_settings)) {
            SettingPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(name = stringResource(Res.string.step_interest)) {
            InterestPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(stringResource(Res.string.step_charges)) {
            ChargesPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(stringResource(Res.string.step_preview)) {
            PreviewPage(
                state = state,
                onAction = onAction,
            )
        },
    )

    MifosScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column {
            MifosBreadcrumbNavBar(navController)
            when (state.screenState) {
                is NewFixedDepositAccountState.ScreenState.Error -> {
                    MifosErrorComponent(
                        message = state.screenState.message,
                        isRetryEnabled = true,
                    ) {
                        onAction(NewFixedDepositAccountAction.Retry)
                    }
                }

                is NewFixedDepositAccountState.ScreenState.Loading -> {
                    MifosProgressIndicator()
                }

                is NewFixedDepositAccountState.ScreenState.Success -> {
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
        if (state.isOverlayLoadingActive) {
            MifosProgressIndicatorOverlay()
        }
    }
}
