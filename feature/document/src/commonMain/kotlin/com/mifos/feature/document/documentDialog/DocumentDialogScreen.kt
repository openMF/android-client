/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.document.documentDialog

import androidclient.feature.document.generated.resources.Res
import androidclient.feature.document.generated.resources.feature_document_browse
import androidclient.feature.document.generated.resources.feature_document_description
import androidclient.feature.document.generated.resources.feature_document_document_updated_successfully
import androidclient.feature.document.generated.resources.feature_document_message_field_required
import androidclient.feature.document.generated.resources.feature_document_message_file_required
import androidclient.feature.document.generated.resources.feature_document_name
import androidclient.feature.document.generated.resources.feature_document_selected_file
import androidclient.feature.document.generated.resources.feature_document_update_document
import androidclient.feature.document.generated.resources.feature_document_upload
import androidclient.feature.document.generated.resources.feature_document_upload_document
import androidclient.feature.document.generated.resources.feature_document_uploaded_successfully
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.objects.noncoreobjects.Document
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DocumentDialogScreen(
    documentAction: StringResource?,
    document: Document?,
    closeDialog: () -> Unit,
    snackbarHostState: SnackbarHostState,
    entityType: String,
    entityId: Int,
    viewModel: DocumentDialogViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    closeScreen: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.resetDialogUiState()
    }

    val state by viewModel.documentDialogUiState.collectAsStateWithLifecycle()

    var fileName by rememberSaveable { mutableStateOf(document?.name) }
    var fileChosen by rememberSaveable { mutableStateOf<PlatformFile?>(null) }

    DocumentDialogScreen(
        uiState = state,
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        documentAction = documentAction,
        snackbarHostState = snackbarHostState,
        document = document,
        openFilePicker = {
            viewModel.openFilePicker { file ->
                file?.let {
                    fileChosen = it
                    fileName = it.name
                }
            }
        },
        closeDialog = closeDialog,
        uploadDocument = { documentName, documentDescription ->
            fileChosen?.let { file ->
                if (documentAction == Res.string.feature_document_update_document) {
                    document?.let {
                        viewModel.updateDocument(
                            entityType,
                            entityId,
                            document.id,
                            documentName,
                            documentDescription,
                            file,
                        )
                    }
                } else if (documentAction == Res.string.feature_document_upload_document) {
                    viewModel.createDocument(
                        entityType,
                        entityId,
                        documentName,
                        documentDescription,
                        file,
                    )
                }
            }
        },
        filename = fileName,
        closeScreen = closeScreen,
    )
}

@Composable
internal fun DocumentDialogScreen(
    uiState: DocumentDialogUiState,
    documentAction: StringResource?,
    snackbarHostState: SnackbarHostState,
    document: Document?,
    openFilePicker: () -> Unit,
    closeDialog: () -> Unit?,
    uploadDocument: (String, String) -> Unit,
    filename: String?,
    modifier: Modifier = Modifier,
    closeScreen: () -> Unit,
) {
    when (uiState) {
        is DocumentDialogUiState.Initial -> {
            DocumentDialogContent(
                document = document,
                documentAction = documentAction,
                setShowDialog = { closeDialog.invoke() },
                openFilePicker = openFilePicker,
                uploadDocument = uploadDocument,
                fileName = filename,
                modifier = modifier,
            )
        }

        is DocumentDialogUiState.ShowProgressbar -> {
            Box(modifier = modifier.fillMaxSize()) {
                MifosCircularProgress()
            }
        }

        is DocumentDialogUiState.ShowDocumentedCreatedSuccessfully -> {
            LaunchedEffect(uiState) {
                snackbarHostState.showSnackbar(getString(Res.string.feature_document_uploaded_successfully))
                closeDialog.invoke()
            }
        }

        is DocumentDialogUiState.ShowDocumentUpdatedSuccessfully -> {
            LaunchedEffect(uiState) {
                snackbarHostState.showSnackbar(getString(Res.string.feature_document_document_updated_successfully))
                closeDialog.invoke()
            }
        }

        is DocumentDialogUiState.ShowUploadError -> {
            LaunchedEffect(uiState) {
                snackbarHostState.showSnackbar(uiState.message)
                closeDialog.invoke()
            }
        }

        is DocumentDialogUiState.ShowError -> {
            LaunchedEffect(uiState) {
                snackbarHostState.showSnackbar(uiState.message)
                closeDialog.invoke()
            }
        }
    }
}

@Composable
private fun DocumentDialogContent(
    setShowDialog: (Boolean) -> Unit,
    documentAction: StringResource?,
    document: Document?,
    openFilePicker: () -> Unit,
    uploadDocument: (String, String) -> Unit,
    fileName: String?,
    modifier: Modifier = Modifier,
) {
    var dialogTitle = stringResource(Res.string.feature_document_upload_document)
    var name by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var description by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var nameError by rememberSaveable { mutableStateOf(false) }
    var descriptionError by rememberSaveable { mutableStateOf(false) }
    var fileError by rememberSaveable { mutableStateOf(false) }

    if (documentAction == Res.string.feature_document_update_document) {
        dialogTitle = stringResource(Res.string.feature_document_update_document)
        name = TextFieldValue(document?.name ?: "")
        description = TextFieldValue(document?.description ?: "")
    }

    fun validateInput(): Boolean {
        var temp = true
        if (name.text.isEmpty()) {
            nameError = true
            temp = false
        }
        if (description.text.isEmpty()) {
            descriptionError = true
            temp = false
        }
        if (fileName.isNullOrEmpty()) {
            fileError = true
            temp = false
        } else {
            fileError = false
        }

        return temp
    }

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = dialogTitle,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Icon(
                        imageVector = MifosIcons.Cancel,
                        contentDescription = "",
                        tint = Color.Gray,
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                            .clickable { setShowDialog(false) },
                    )
                }

                MifosOutlinedTextField(
                    value = name,
                    onValueChanged = { value ->
                        name = value
                        nameError = false
                    },
                    label = stringResource(Res.string.feature_document_name),
                    error = if (nameError) stringResource(Res.string.feature_document_message_field_required) else null,
                    trailingIcon = {
                        if (nameError) {
                            Icon(imageVector = MifosIcons.Error, contentDescription = null)
                        }
                    },
                )

                MifosOutlinedTextField(
                    value = description,
                    onValueChanged = { value ->
                        description = value
                        descriptionError = false
                    },
                    label = stringResource(Res.string.feature_document_description),
                    error = if (descriptionError) stringResource(Res.string.feature_document_message_field_required) else null,
                    trailingIcon = {
                        if (descriptionError) {
                            Icon(imageVector = MifosIcons.Error, contentDescription = null)
                        }
                    },
                )

                OutlinedTextField(
                    value = if (fileName != null) TextFieldValue(fileName) else TextFieldValue(""),
                    onValueChange = {
                        fileError = false
                    },
                    label = { Text(stringResource(Res.string.feature_document_selected_file)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    trailingIcon = {
                        if (descriptionError) {
                            Icon(imageVector = MifosIcons.Error, contentDescription = null)
                        }
                    },
                    enabled = false,
                    maxLines = 1,
                    textStyle = LocalDensity.current.run {
                        TextStyle(fontSize = 18.sp)
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    isError = fileError,
                    supportingText = {
                        if (fileError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(Res.string.feature_document_message_file_required),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Supported formats: xls,xlsx,pdf,doc,docx,png,jpeg,jpg.")

                Spacer(modifier = Modifier.height(12.dp))

                DialogButton(
                    text = stringResource(Res.string.feature_document_browse),
                    onClick = openFilePicker,
                )

                Spacer(modifier = Modifier.height(20.dp))

                DialogButton(
                    text = stringResource(Res.string.feature_document_upload),
                    onClick = {
                        if (validateInput()) {
                            uploadDocument.invoke(name.text, description.text)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun DialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Text(text = text)
    }
}

private class DocumentDialogPreviewProvider : PreviewParameterProvider<DocumentDialogUiState> {
    override val values: Sequence<DocumentDialogUiState>
        get() = sequenceOf(
            DocumentDialogUiState.Initial,
            DocumentDialogUiState.ShowProgressbar,
            DocumentDialogUiState.ShowDocumentUpdatedSuccessfully,
            DocumentDialogUiState.ShowDocumentedCreatedSuccessfully,
            DocumentDialogUiState.ShowError("Error"),
            DocumentDialogUiState.ShowUploadError("Upload Error"),
        )
}

@Preview
@Composable
private fun DocumentDialogPreview(
    @PreviewParameter(DocumentDialogPreviewProvider::class) state: DocumentDialogUiState,
) {
    DocumentDialogScreen(
        uiState = state,
        documentAction = Res.string.feature_document_document_updated_successfully,
        snackbarHostState = remember { SnackbarHostState() },
        document = Document(),
        openFilePicker = { },
        closeDialog = { },
        uploadDocument = { _, _ -> },
        filename = "",
        closeScreen = { },
    )
}
