/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccountDetails

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_details
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosDefaultListingComponentFromStringResources
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun LoanAccountDetailsScreenRoute(
    onNavigateBack: () -> Unit,
    navController: NavController,
    viewModel: LoanAccountDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanAccountDetailsEvent.NavigateBack -> onNavigateBack()
        }
    }

    LoanAccountDetailsScreen(
        state = state,
        navController = navController,
        onRetry = remember(viewModel) { { viewModel.trySendAction(LoanAccountDetailsAction.OnRetry) } },
    )
}

@Composable
internal fun LoanAccountDetailsScreen(
    state: LoanAccountDetailsState,
    navController: NavController,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KptTheme.colorScheme.background),
    ) {
        MifosBreadcrumbNavBar(navController)

        if (state.dialogState == null && state.details.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                LoanAccountDetailsContent(state = state)
            }
        } else {
            LoanAccountDialogs(
                state = state,
                onRetry = onRetry,
            )
        }
    }
}

@Composable
private fun LoanAccountDialogs(
    state: LoanAccountDetailsState,
    onRetry: () -> Unit,
) {
    when (state.dialogState) {
        is LoanAccountDetailsState.DialogState.Loading -> MifosProgressIndicator()
        is LoanAccountDetailsState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = stringResource(state.dialogState.message),
                isRetryEnabled = true,
                onRetry = onRetry,
            )
        }
        null -> Unit
    }
}

@Composable
private fun LoanAccountDetailsContent(
    state: LoanAccountDetailsState,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = KptTheme.spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.feature_loan_details),
            style = MifosTypography.labelLargeEmphasized,
        )

        Spacer(Modifier.height(DesignToken.padding.medium))

        state.details.forEach { mapData ->
            MifosDefaultListingComponentFromStringResources(data = mapData)
            Spacer(Modifier.height(KptTheme.spacing.md))
        }

        Spacer(Modifier.height(KptTheme.spacing.md))
    }
}
