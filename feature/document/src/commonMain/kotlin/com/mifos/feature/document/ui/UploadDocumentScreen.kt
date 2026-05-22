/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.document.ui

import androidclient.feature.document.generated.resources.Res
import androidclient.feature.document.generated.resources.feature_document_browse
import androidclient.feature.document.generated.resources.feature_document_cd_close_dialog
import androidclient.feature.document.generated.resources.feature_document_description
import androidclient.feature.document.generated.resources.feature_document_failed_to_update_document
import androidclient.feature.document.generated.resources.feature_document_failed_to_upload_document
import androidclient.feature.document.generated.resources.feature_document_message_field_required
import androidclient.feature.document.generated.resources.feature_document_message_file_required
import androidclient.feature.document.generated.resources.feature_document_name
import androidclient.feature.document.generated.resources.feature_document_selected_file
import androidclient.feature.document.generated.resources.feature_document_supported_formats
import androidclient.feature.document.generated.resources.feature_document_update_document
import androidclient.feature.document.generated.resources.feature_document_upload
import androidclient.feature.document.generated.resources.feature_document_upload_document
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.util.DevicePreview
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme
import template.core.base.store.screen.DataFreshness
import template.core.base.store.screen.ScreenState
import template.core.base.store.submit.SubmitState
import template.core.base.ui.submit.MutationScreenContent

/**
 * Upload / update document dialog. Hosted by [DocumentListScreen]; routed by
 * [com.mifos.feature.document.navigation.DocumentNavigation]'s dialog destination
 * for use cases that want to launch it standalone.
 *
 * Wraps the form body in `MutationScreenContent` to get the canonical progress
 * overlay + result handler (same pattern as `feature/note/AddEditNoteScreen`).
 *
 * The composable is exported under two names:
 *  - [UploadDocumentDialog] — invoked from [DocumentListScreen] (legacy spelling
 *    preserved for binary-compat with existing callers).
 *  - [UploadDocumentScreen]  — the Wave-9-standard `*Screen` spelling exposed for
 *    completeness so navigation graphs / previews can use either symbol.
 *
 * Both delegate to the internal [UploadDocumentDialogContent] which holds the
 * actual UI; this keeps the symbol surface stable while the migration completes.
 */
@Composable
fun UploadDocumentDialog(
    viewModel: UploadDocumentViewModel,
    onDismiss: () -> Unit,
    onSubmitted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            UploadDocumentEvent.DismissDialog -> onDismiss()
            UploadDocumentEvent.MutationSucceeded -> onSubmitted()
        }
    }

    // Surface mutation failures as a snackbar inside the dialog. The kind drives
    // the message so the user knows whether the upload or update failed.
    LaunchedEffect(submitState, state.kind) {
        if (submitState is SubmitState.Failed) {
            val failureRes: StringResource = when (state.kind) {
                DocumentDialogKind.Upload -> Res.string.feature_document_failed_to_upload_document
                DocumentDialogKind.Update -> Res.string.feature_document_failed_to_update_document
            }
            snackbarHostState.showSnackbar(getString(failureRes))
            viewModel.onSubmitConsumed()
        }
    }

    Dialog(
        onDismissRequest = { viewModel.trySendAction(UploadDocumentAction.DismissDialog) },
        properties = DialogProperties(
            dismissOnBackPress = submitState !is SubmitState.Submitting,
            dismissOnClickOutside = submitState !is SubmitState.Submitting,
        ),
    ) {
        Card(
            modifier = modifier
                .widthIn(min = DesignToken.sizes.imageDp48 * 6)
                .fillMaxWidth(),
            shape = DesignToken.shapes.largeIncreased,
        ) {
            UploadDocumentDialogContent(
                state = state,
                submitState = submitState,
                snackbarHostState = snackbarHostState,
                onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
                onSubmitted = onSubmitted,
            )
        }
    }
}

/**
 * Wave-9-standard `*Screen` spelling alias for [UploadDocumentDialog]. Provided
 * so navigation graphs / previews can use the symbol expected by the migration
 * checklist; the underlying UI is the same dialog.
 */
@Composable
fun UploadDocumentScreen(
    viewModel: UploadDocumentViewModel,
    onDismiss: () -> Unit,
    onSubmitted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    UploadDocumentDialog(
        viewModel = viewModel,
        onDismiss = onDismiss,
        onSubmitted = onSubmitted,
        modifier = modifier,
    )
}

@Composable
internal fun UploadDocumentDialogContent(
    state: UploadDocumentState,
    submitState: SubmitState<Unit>,
    snackbarHostState: SnackbarHostState,
    onAction: (UploadDocumentAction) -> Unit,
    onSubmitted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        UploadDocumentDialogHeader(
            kind = state.kind,
            enabled = submitState !is SubmitState.Submitting,
            onCloseClick = { onAction(UploadDocumentAction.DismissDialog) },
        )

        MutationScreenContent(
            screenState = state.loadState,
            submitState = submitState,
            onRetry = { /* loadState is seeded synchronously — no retry needed */ },
            onSubmitted = { onSubmitted() },
            modifier = Modifier.fillMaxWidth(),
        ) { _, _ ->
            UploadDocumentForm(
                state = state,
                enabled = submitState !is SubmitState.Submitting,
                onAction = onAction,
            )
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
private fun UploadDocumentDialogHeader(
    kind: DocumentDialogKind,
    enabled: Boolean,
    onCloseClick: () -> Unit,
) {
    val titleRes: StringResource = when (kind) {
        DocumentDialogKind.Upload -> Res.string.feature_document_upload_document
        DocumentDialogKind.Update -> Res.string.feature_document_update_document
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = KptTheme.spacing.md,
                end = KptTheme.spacing.xs,
                top = KptTheme.spacing.sm,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(titleRes),
            style = KptTheme.typography.titleMedium,
            color = KptTheme.colorScheme.onSurface,
        )
        IconButton(
            onClick = onCloseClick,
            enabled = enabled,
        ) {
            Icon(
                imageVector = MifosIcons.Close,
                contentDescription = stringResource(Res.string.feature_document_cd_close_dialog),
            )
        }
    }
}

@Composable
private fun UploadDocumentForm(
    state: UploadDocumentState,
    enabled: Boolean,
    onAction: (UploadDocumentAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fieldRequired = stringResource(Res.string.feature_document_message_field_required)
    val fileRequired = stringResource(Res.string.feature_document_message_file_required)
    val supportedFormats = stringResource(Res.string.feature_document_supported_formats)
    val selectedFileLabel = stringResource(Res.string.feature_document_selected_file)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = KptTheme.spacing.md,
                vertical = KptTheme.spacing.sm,
            ),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        MifosOutlinedTextField(
            value = state.name,
            onValueChange = { onAction(UploadDocumentAction.NameChanged(it)) },
            label = stringResource(Res.string.feature_document_name),
            isError = state.nameError,
            errorText = if (state.nameError) fieldRequired else null,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
        )

        MifosOutlinedTextField(
            value = state.description,
            onValueChange = { onAction(UploadDocumentAction.DescriptionChanged(it)) },
            label = stringResource(Res.string.feature_document_description),
            isError = state.descriptionError,
            errorText = if (state.descriptionError) fieldRequired else null,
            enabled = enabled,
            singleLine = false,
            maxLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )

        // File picker row: shows the picked file name (or the empty-file hint
        // when validation fired) next to a BROWSE button.
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            val pickedDisplay = state.pickedFileName
            val helperText = pickedDisplay ?: selectedFileLabel
            val helperColor = when {
                state.fileError -> KptTheme.colorScheme.error
                pickedDisplay == null -> KptTheme.colorScheme.onSurfaceVariant
                else -> KptTheme.colorScheme.onSurface
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = helperText,
                    style = KptTheme.typography.bodyMedium,
                    color = helperColor,
                    maxLines = 1,
                )
                if (state.fileError) {
                    Text(
                        text = fileRequired,
                        style = KptTheme.typography.labelSmall,
                        color = KptTheme.colorScheme.error,
                    )
                }
            }
            MifosButton(
                onClick = { onAction(UploadDocumentAction.OpenFilePicker) },
                enabled = enabled,
            ) {
                Text(
                    text = stringResource(Res.string.feature_document_browse),
                    style = KptTheme.typography.labelLarge,
                )
            }
        }

        Text(
            text = supportedFormats,
            style = KptTheme.typography.bodySmall,
            color = KptTheme.colorScheme.onSurfaceVariant,
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            MifosButton(
                onClick = { onAction(UploadDocumentAction.Submit) },
                enabled = enabled,
            ) {
                Text(
                    text = stringResource(Res.string.feature_document_upload),
                    style = KptTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// region Previews

@DevicePreview
@Composable
private fun PreviewUploadDocumentDialogContent_Empty() {
    UploadDocumentDialogContent(
        state = UploadDocumentState(
            entityId = 1,
            entityType = "clients",
            kind = DocumentDialogKind.Upload,
            loadState = ScreenState.Content(data = Unit, freshness = DataFreshness.FRESH),
        ),
        submitState = SubmitState.Idle,
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
        onSubmitted = {},
    )
}

@DevicePreview
@Composable
private fun PreviewUploadDocumentDialogContent_Errors() {
    UploadDocumentDialogContent(
        state = UploadDocumentState(
            entityId = 1,
            entityType = "clients",
            kind = DocumentDialogKind.Upload,
            name = "",
            description = "",
            pickedFile = null,
            nameError = true,
            descriptionError = true,
            fileError = true,
            loadState = ScreenState.Content(data = Unit, freshness = DataFreshness.FRESH),
        ),
        submitState = SubmitState.Idle,
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
        onSubmitted = {},
    )
}

@DevicePreview
@Composable
private fun PreviewUploadDocumentDialogContent_UpdateFilled() {
    UploadDocumentDialogContent(
        state = UploadDocumentState(
            entityId = 1,
            entityType = "clients",
            kind = DocumentDialogKind.Update,
            documentId = 42,
            name = "Loan agreement",
            description = "Signed contract",
            pickedFileName = "loan-agreement.pdf",
            loadState = ScreenState.Content(data = Unit, freshness = DataFreshness.FRESH),
        ),
        submitState = SubmitState.Idle,
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
        onSubmitted = {},
    )
}

@DevicePreview
@Composable
private fun PreviewUploadDocumentDialogContent_Submitting() {
    UploadDocumentDialogContent(
        state = UploadDocumentState(
            entityId = 1,
            entityType = "clients",
            kind = DocumentDialogKind.Upload,
            name = "Loan agreement",
            description = "Signed contract",
            pickedFileName = "loan-agreement.pdf",
            loadState = ScreenState.Content(data = Unit, freshness = DataFreshness.FRESH),
        ),
        submitState = SubmitState.Submitting,
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
        onSubmitted = {},
    )
}

@DevicePreview
@Composable
private fun PreviewUploadDocumentDialogContent_Submitted() {
    UploadDocumentDialogContent(
        state = UploadDocumentState(
            entityId = 1,
            entityType = "clients",
            kind = DocumentDialogKind.Upload,
            name = "Loan agreement",
            description = "Signed contract",
            pickedFileName = "loan-agreement.pdf",
            loadState = ScreenState.Content(data = Unit, freshness = DataFreshness.FRESH),
        ),
        submitState = SubmitState.Submitted(result = Unit),
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
        onSubmitted = {},
    )
}

// endregion
