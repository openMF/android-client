/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createShareAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.share_account_charges
import androidclient.feature.client.generated.resources.share_account_details
import androidclient.feature.client.generated.resources.share_account_preview
import androidclient.feature.client.generated.resources.share_account_terms
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.createShareAccount.pages.ChargesPage
import com.mifos.feature.client.createShareAccount.pages.DetailsPage
import com.mifos.feature.client.createShareAccount.pages.PreviewPage
import com.mifos.feature.client.createShareAccount.pages.TermsPage
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CreateShareAccountScreen(
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateShareAccountViewModel = koinViewModel(),
    navController: NavController,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ShareAccountEvent.NavigateBack -> onNavigateBack()
            ShareAccountEvent.Finish -> onFinish()
        }
    }

    CreateShareAccountContent(
        modifier = modifier,
        state = state,
        onAction = { viewModel.trySendAction(it) },
        navController = navController,
    )
}

@Composable
private fun CreateShareAccountContent(
    state: ShareAccountState,
    modifier: Modifier = Modifier,
    onAction: (ShareAccountAction) -> Unit,
    navController: NavController,
) {
    val steps = listOf(
        Step(name = stringResource(Res.string.share_account_details)) {
            DetailsPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(name = stringResource(Res.string.share_account_terms)) {
            TermsPage(
                onNext = { onAction(ShareAccountAction.NextStep) },
            )
        },
        Step(name = stringResource(Res.string.share_account_charges)) {
            ChargesPage(
                onNext = { onAction(ShareAccountAction.NextStep) },
            )
        },
        Step(name = stringResource(Res.string.share_account_preview)) {
            PreviewPage(
                onNext = { onAction(ShareAccountAction.Finish) },
            )
        },
    )

    MifosScaffold(
        modifier = modifier,
    ) { paddingValues ->
        when (state.screenState) {
            is ShareAccountState.ScreenState.Loading -> MifosProgressIndicator()
            is ShareAccountState.ScreenState.Success -> {
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
                            onAction(ShareAccountAction.OnStepChange(newIndex))
                        },
                        modifier = Modifier
                            .fillMaxWidth().align(Alignment.CenterHorizontally),
                    )
                }
            }

            is ShareAccountState.ScreenState.Error -> {
                MifosSweetError(
                    message = state.screenState.message,
                    onclick = { onAction(ShareAccountAction.Retry) },
                )
            }
        }
    }
}
