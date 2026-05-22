/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.note.ui

import androidclient.feature.note.generated.resources.Res
import androidclient.feature.note.generated.resources.feature_note_button_back
import androidclient.feature.note.generated.resources.feature_note_button_confirm
import androidclient.feature.note.generated.resources.feature_note_dialog_warning
import androidclient.feature.note.generated.resources.feature_note_dialog_warning_message
import androidclient.feature.note.generated.resources.feature_note_failed_to_save
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import template.core.base.store.submit.SubmitState
import template.core.base.ui.submit.MutationScreenContent

@Composable
internal fun AddEditNoteScreen(
    onBackPressed: () -> Unit,
    onNavigateWithUpdatedList: (Int, String?) -> Unit,
    navController: NavController,
    viewModel: AddEditNoteViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            AddEditNoteEvent.NavigateBack -> onBackPressed()
            AddEditNoteEvent.NavigateBackWithUpdatedList -> onNavigateWithUpdatedList(
                state.resourceId,
                state.resourceType,
            )
        }
    }

    // Surface mutation failures as a snackbar.
    LaunchedEffect(submitState) {
        if (submitState is SubmitState.Failed) {
            snackbarHostState.showSnackbar(message = getString(Res.string.feature_note_failed_to_save))
            viewModel.onSubmitConsumed()
        }
    }

    AddEditNoteScreenContent(
        state = state,
        submitState = submitState,
        snackbarHostState = snackbarHostState,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        onSubmitted = {
            viewModel.onSubmitConsumed()
            onNavigateWithUpdatedList(state.resourceId, state.resourceType)
        },
        navController = navController,
    )

    if (state.showAccidentalBackDialog) {
        MifosAlertDialog(
            onDismissRequest = { viewModel.trySendAction(AddEditNoteAction.DismissAccidentalBackDialog) },
            confirmationText = stringResource(Res.string.feature_note_button_confirm),
            dialogTitle = stringResource(Res.string.feature_note_dialog_warning),
            dialogText = stringResource(Res.string.feature_note_dialog_warning_message),
            onConfirmation = {
                viewModel.trySendAction(AddEditNoteAction.DismissAccidentalBackDialog)
                viewModel.trySendAction(AddEditNoteAction.NavigateBack)
            },
            icon = null,
        )
    }
}

@Composable
internal fun AddEditNoteScreenContent(
    state: AddEditNoteState,
    submitState: SubmitState<Unit>,
    snackbarHostState: SnackbarHostState,
    onAction: (AddEditNoteAction) -> Unit,
    onSubmitted: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MifosBreadcrumbNavBar(navController)

            MutationScreenContent(
                screenState = state.loadState,
                submitState = submitState,
                onRetry = { onAction(AddEditNoteAction.OnRetryLoad) },
                onSubmitted = { onSubmitted() },
                modifier = Modifier.fillMaxSize(),
            ) { _, _ ->
                AddEditNoteForm(
                    state = state,
                    enabled = submitState !is SubmitState.Submitting,
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
private fun AddEditNoteForm(
    state: AddEditNoteState,
    enabled: Boolean,
    onAction: (AddEditNoteAction) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = KptTheme.spacing.md,
                vertical = KptTheme.spacing.sm,
            ),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        Text(
            text = stringResource(state.title),
            style = MifosTypography.labelLargeEmphasized,
            color = KptTheme.colorScheme.onSurface,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            MifosOutlinedTextField(
                value = state.textFieldNotesPayload,
                onValueChange = { onAction(AddEditNoteAction.TextFieldNotesPayload(it)) },
                maxLines = 18,
                singleLine = false,
                shape = KptTheme.shapes.large,
                label = stringResource(state.label),
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = DesignToken.spacing.half),
                textStyle = KptTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = KptTheme.colorScheme.secondaryContainer,
                ),
            )
        }

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_note_button_back),
            secondBtnText = stringResource(state.addUpdateButton),
            onFirstBtnClick = { onAction(AddEditNoteAction.RequestBack) },
            onSecondBtnClick = { onAction(AddEditNoteAction.Submit) },
            modifier = Modifier.padding(bottom = KptTheme.spacing.md),
        )
    }
}
