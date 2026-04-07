/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanUndoApproval

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_submit
import androidclient.feature.loan.generated.resources.feature_loan_undo_approval_note
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosResizableOutlinedTextField
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun LoanUndoApprovalScreen(
    navController: NavController,
    viewModel: LoanUndoApprovalViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanUndoApprovalEvent.NavigationBack -> navigateBack()
        }
    }

    LoanUndoApprovalContent(
        navController = navController,
        state = state,
        onAction = viewModel::trySendAction,
    )

    LoanUndoApprovalDialog(
        state = state,
        onAction = viewModel::trySendAction,
    )
}

@Composable
fun LoanUndoApprovalDialog(
    state: LoanUndoApprovalState,
    onAction: (LoanUndoApprovalAction) -> Unit,
) {
    when (state.dialogState) {
        is LoanUndoApprovalState.DialogState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(color = KptTheme.colorScheme.background),
            ) {
                MifosSweetError(
                    message = state.dialogState.message,
                    isRetryEnabled = true,
                    onclick = { onAction.invoke(LoanUndoApprovalAction.OnRetry) },
                )
            }
        }
        LoanUndoApprovalState.DialogState.Loading -> {
            MifosProgressIndicatorOverlay()
        }
        else -> Unit
    }
}

@Composable
fun LoanUndoApprovalContent(
    navController: NavController,
    state: LoanUndoApprovalState,
    onAction: (LoanUndoApprovalAction) -> Unit,
) {
    Column {
        MifosBreadcrumbNavBar(navController)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = KptTheme.spacing.md)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            MifosResizableOutlinedTextField(
                value = state.note,
                onValueChange = {
                    onAction(LoanUndoApprovalAction.OnNoteChange(it))
                },
                placeholder = stringResource(Res.string.feature_loan_undo_approval_note),
            )

            MifosTwoButtonRow(
                firstBtnText = stringResource(Res.string.feature_loan_cancel),
                secondBtnText = stringResource(Res.string.feature_loan_submit),
                onFirstBtnClick = {
                    onAction(LoanUndoApprovalAction.OnNavigateBack)
                },
                onSecondBtnClick = {
                    onAction(LoanUndoApprovalAction.OnSubmit)
                },
                modifier = Modifier.padding(bottom = KptTheme.spacing.md),
            )
        }
    }
}
