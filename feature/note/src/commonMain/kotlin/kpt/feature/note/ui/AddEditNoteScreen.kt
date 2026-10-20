/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.note.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kpt.core.designsystem.icon.MifosIcons

import kpt.feature.note.generated.resources.Res
import kpt.feature.note.generated.resources.feature_note_add_note
import kpt.feature.note.generated.resources.feature_note_button_add
import kpt.feature.note.generated.resources.feature_note_button_cancel
import kpt.feature.note.generated.resources.feature_note_button_confirm
import kpt.feature.note.generated.resources.feature_note_button_update
import kpt.feature.note.generated.resources.feature_note_cd_back
import kpt.feature.note.generated.resources.feature_note_dialog_warning
import kpt.feature.note.generated.resources.feature_note_dialog_warning_message
import kpt.feature.note.generated.resources.feature_note_edit_note_label
import kpt.feature.note.generated.resources.feature_note_error_offline
import kpt.feature.note.generated.resources.feature_note_unexpected_error
import kpt.feature.note.generated.resources.feature_note_update_note
import kpt.feature.note.generated.resources.feature_note_write_note_label

import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AddEditNoteScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditNoteViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                AddEditNoteEvent.NavigateBack -> onBackClick()
            }
        }
    }

    AddEditNoteContent(
        state = state,
        onAction = { viewModel.actionChannel.trySend(it) },
        modifier = modifier,
    )
}

/**
 * Stateless half — rendered in tests without Koin.
 *
 * The title / label / button text all derive from `editEnabled`. The original carried three
 * `StringResource` fields in its STATE and reassigned them in `init`, which put presentation in the
 * state machine and made the state un-testable without a Compose resource runtime.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddEditNoteContent(
    state: AddEditNoteState,
    onAction: (AddEditNoteAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize().testTag(TestTags.AddEditNote.SCREEN),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (state.editEnabled) {
                                Res.string.feature_note_update_note
                            } else {
                                Res.string.feature_note_add_note
                            },
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(
                        // Routed through the ViewModel, NOT straight to onBackClick: it is the
                        // ViewModel that knows whether there are unsaved changes worth asking about.
                        onClick = { onAction(AddEditNoteAction.BackClicked) },
                        modifier = Modifier.testTag(TestTags.AddEditNote.BACK),
                    ) {
                        Icon(
                            MifosIcons.ArrowBack,
                            contentDescription = stringResource(Res.string.feature_note_cd_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
        ) {
            OutlinedTextField(
                value = state.text,
                onValueChange = { onAction(AddEditNoteAction.TextChanged(it)) },
                label = {
                    Text(
                        stringResource(
                            if (state.editEnabled) {
                                Res.string.feature_note_edit_note_label
                            } else {
                                Res.string.feature_note_write_note_label
                            },
                        ),
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .testTag(TestTags.AddEditNote.FIELD),
            )

            // Shown INLINE, under the field, with the user's text still in place above it. A write
            // failure must never clear the one thing that exists nowhere else.
            state.writeError?.let { error ->
                Text(
                    text = stringResource(
                        when (error) {
                            NoteWriteError.Offline -> Res.string.feature_note_error_offline
                            NoteWriteError.Failed -> Res.string.feature_note_unexpected_error
                        },
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp).testTag(TestTags.AddEditNote.WRITE_ERROR),
                )
            }

            Button(
                onClick = { onAction(AddEditNoteAction.Submit) },
                // Disabled on a blank note and while in flight. The original allowed both: an
                // empty note round-tripped to the server to be rejected, and a double-tap sent two.
                enabled = state.canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .testTag(TestTags.AddEditNote.SUBMIT),
            ) {
                Text(
                    stringResource(
                        if (state.editEnabled) {
                            Res.string.feature_note_button_update
                        } else {
                            Res.string.feature_note_button_add
                        },
                    ),
                )
            }
        }
    }

    if (state.showDiscardConfirmation) {
        AlertDialog(
            onDismissRequest = { onAction(AddEditNoteAction.DismissDialog) },
            modifier = Modifier.testTag(TestTags.AddEditNote.DISCARD_DIALOG),
            title = { Text(stringResource(Res.string.feature_note_dialog_warning)) },
            text = { Text(stringResource(Res.string.feature_note_dialog_warning_message)) },
            confirmButton = {
                TextButton(onClick = { onAction(AddEditNoteAction.ConfirmDiscard) }) {
                    Text(stringResource(Res.string.feature_note_button_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(AddEditNoteAction.DismissDialog) }) {
                    Text(stringResource(Res.string.feature_note_button_cancel))
                }
            },
        )
    }
}
