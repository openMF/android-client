/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.note.addEditNotes

import androidclient.feature.note.generated.resources.Res
import androidclient.feature.note.generated.resources.feature_note_button_back
import androidclient.feature.note.generated.resources.feature_note_button_confirm
import androidclient.feature.note.generated.resources.feature_note_dialog_warning
import androidclient.feature.note.generated.resources.feature_note_dialog_warning_message
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AddEditNoteScreen(
    onBackPressed: () -> Unit,
    onNavigateWithUpdatedList: (Int, String?) -> Unit,
    navController: NavController,
    viewModel: AddEditNoteViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            AddEditNoteEvent.NavigateBack -> onBackPressed()
            AddEditNoteEvent.NavigateBackWithUpdateList -> onNavigateWithUpdatedList(
                state.resourceId,
                state.resourceType,
            )
        }
    }

    AddEditNoteScreenScaffold(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        navController = navController,
    )

    AddEditNoteScreenDialog(
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        state = state,
    )
}

@Composable
fun AddEditNoteScreenDialog(
    onAction: (AddEditNoteAction) -> Unit,
    state: AddEditNoteState,
) {
    when (state.dialogState) {
        is AddEditNoteState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = stringResource(state.dialogState.message),
                isRetryEnabled = true,
                onRetry = {
                    onAction(AddEditNoteAction.OnRetry)
                },
            )
        }

        AddEditNoteState.DialogState.Loading -> {
            MifosProgressIndicatorOverlay()
        }

        AddEditNoteState.DialogState.MisTouchBack -> {
            MifosAlertDialog(
                onDismissRequest = {
                    onAction(AddEditNoteAction.DismissDialog)
                },
                confirmationText = stringResource(Res.string.feature_note_button_confirm),
                dialogTitle = stringResource(Res.string.feature_note_dialog_warning),
                dialogText = stringResource(Res.string.feature_note_dialog_warning_message),
                onConfirmation = {
                    onAction(AddEditNoteAction.NavigateBack)
                },
                icon = null,
            )
        }

        null -> Unit
    }
}

@Composable
internal fun AddEditNoteScreenScaffold(
    onAction: (AddEditNoteAction) -> Unit,
    state: AddEditNoteState,
    navController: NavController,
) {
    MifosScaffold(
        title = "",
        onBackPressed = { onAction(AddEditNoteAction.MisTouchBackDialog) },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (
                state.dialogState !is AddEditNoteState.DialogState.Error
            ) {
                MifosBreadcrumbNavBar(navController)

                AddEditNote(
                    state = state,
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
private fun AddEditNote(
    state: AddEditNoteState,
    onAction: (AddEditNoteAction) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = DesignToken.spacing.large,
                vertical = DesignToken.spacing.small,
            ),
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.large),
    ) {
        Text(
            text = stringResource(state.title),
            style = MifosTypography.labelLargeEmphasized,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Column(
            modifier = Modifier
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.large),
        ) {
            MifosOutlinedTextField(
                value = state.textFieldNotesPayload.note ?: "",
                onValueChange = {
                    onAction(
                        AddEditNoteAction.TextFieldNotesPayload(
                            state.textFieldNotesPayload.copy(
                                note = it,
                            ),
                        ),
                    )
                },
                maxLines = 18,
                singleLine = false,
                shape = DesignToken.shapes.large,
                label = stringResource(state.label),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 550.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Start),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.extraSmall),
            ) {
                MifosOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = {
                        Text(
                            text = stringResource(Res.string.feature_note_button_back),
                        )
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    onClick = {
                        onAction(AddEditNoteAction.MisTouchBackDialog)
                    },
                )

                MifosOutlinedButton(
                    modifier = Modifier.weight(1f),
                    text = {
                        Text(text = stringResource(state.addUpdateButton))
                    },
                    onClick = {
                        if (state.editEnabled) {
                            onAction(AddEditNoteAction.EditNote(state.textFieldNotesPayload))
                        } else {
                            onAction(AddEditNoteAction.AddNote(state.textFieldNotesPayload))
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    enabled = !state.textFieldNotesPayload.note.isNullOrEmpty(),
                )
            }
        }
    }
}
