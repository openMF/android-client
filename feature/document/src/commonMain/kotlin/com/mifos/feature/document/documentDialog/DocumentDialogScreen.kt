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
import androidclient.feature.document.generated.resources.allStringResources
import androidclient.feature.document.generated.resources.feature_document_browse
import androidclient.feature.document.generated.resources.feature_document_description
import androidclient.feature.document.generated.resources.feature_document_document_updated_successfully
import androidclient.feature.document.generated.resources.feature_document_message_field_required
import androidclient.feature.document.generated.resources.feature_document_message_file_required
import androidclient.feature.document.generated.resources.feature_document_name
import androidclient.feature.document.generated.resources.feature_document_permission_denied
import androidclient.feature.document.generated.resources.feature_document_permission_granted
import androidclient.feature.document.generated.resources.feature_document_remove_successful
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
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
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel



@Composable
internal fun DocumentDialogScreen(
    documentAction: String?,
    document: Document?,
    closeDialog: () -> Unit,
    entityType: String,
    entityId: Int,
    viewModel: DocumentDialogViewModel = koinViewModel(),
    closeScreen: () -> Unit,
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.documentDialogUiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
//    val requiredPermissions = if (Build.VERSION.SDK_INT >= 33) {
//        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
//    } else {
//        arrayOf(
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        )
//    }
    var fileName by rememberSaveable { mutableStateOf<String?>(document?.name) }
    var fileData by remember { mutableStateOf<ByteArray?>(null) }

    val pickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.ImageAndVideo,
        onResult = { files ->
            scope.launch {
                fileName=files?.name ?: "Nothing choosen"
                fileData=files?.readBytes()
            }
        }
    )


    DocumentDialogScreen(
        uiState = state,
        snackbarHostState=snackbarHostState,
        documentAction = documentAction,
        document = document,
        openFilePicker = {
            pickerLauncher.launch()
        },
        closeDialog = closeDialog,
        uploadDocument = { documentName, documentDescription ->

            if (documentAction == "Update Document") {
                viewModel.updateDocument(
                    entityType,
                    entityId,
                    document!!.id,
                    documentName,
                    documentDescription,
                    fileData!!,
                )
            } else if (documentAction == "Upload Document") {
                viewModel.createDocument(
                    entityType,
                    entityId,
                    documentName,
                    documentDescription,
                    fileData!!,
                )
            }
        },
        filename = fileName,
        closeScreen = closeScreen,
    )
}

@Composable
internal fun DocumentDialogScreen(
    uiState: DocumentDialogUiState,
    snackbarHostState: SnackbarHostState,
    documentAction: String?,
    document: Document?,
    openFilePicker: () -> Unit,
    closeDialog: () -> Unit?,
    uploadDocument: (String, String) -> Unit,
    filename: String?,
    closeScreen: () -> Unit,
) {
    DocumentDialogContent(
        document = document,
        documentAction = documentAction,
        setShowDialog = { closeDialog.invoke() },
        openFilePicker = openFilePicker,
        uploadDocument = uploadDocument,
        fileName = filename,
    )

    when (uiState) {
        is DocumentDialogUiState.Initial -> Unit

        is DocumentDialogUiState.ShowProgressbar -> {
            MifosCircularProgress()
        }

        is DocumentDialogUiState.ShowDocumentedCreatedSuccessfully -> {
            val message = stringResource(
                Res.string.feature_document_uploaded_successfully,
                listOf(filename)
            )
            LaunchedEffect(true) {
                snackbarHostState.showSnackbar(message)
            }
            closeDialog.invoke()
        }

        is DocumentDialogUiState.ShowDocumentUpdatedSuccessfully -> {
            val message = stringResource(
                Res.string.feature_document_document_updated_successfully,
                listOf(filename)
            )
            LaunchedEffect(true) {
                snackbarHostState.showSnackbar(message)
            }
            closeDialog.invoke()
        }

        is DocumentDialogUiState.ShowUploadError -> {
            LaunchedEffect(true) {
                snackbarHostState.showSnackbar(message = uiState.message)
            }
            closeScreen.invoke()
        }

        is DocumentDialogUiState.ShowError -> {
            LaunchedEffect(true) {
                snackbarHostState.showSnackbar(message = uiState.message)
            }
            closeScreen.invoke()
        }
    }
}

@Composable
private fun DocumentDialogContent(
    setShowDialog: (Boolean) -> Unit,
    documentAction: String?,
    document: Document?,
    openFilePicker: () -> Unit,
    uploadDocument: (String, String) -> Unit,
    fileName: String?,
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

    if (documentAction == stringResource(Res.string.feature_document_update_document)) {
        dialogTitle = stringResource(Res.string.feature_document_update_document)
        name = TextFieldValue(document?.name!!)
        description = TextFieldValue(document.description!!)
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
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(White),
            contentAlignment = Alignment.Center,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
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
                        contentDescription = "Cancel Icon",
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
                        .padding(start = 16.dp, end = 16.dp),
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

                Spacer(modifier = Modifier.height(20.dp))

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
        onClick=onClick,
        modifier=modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 20.dp)
    ){
        Text(text=text)
    }
}

//private class DocumentDialogPreviewProvider : PreviewParameterProvider<DocumentDialogUiState> {
//    override val values: Sequence<DocumentDialogUiState>
//        get() = sequenceOf(
//            DocumentDialogUiState.Initial,
//            DocumentDialogUiState.ShowProgressbar,
//            DocumentDialogUiState.ShowDocumentUpdatedSuccessfully(GenericResponse()),
//            DocumentDialogUiState.ShowDocumentedCreatedSuccessfully(GenericResponse()),
//            DocumentDialogUiState.ShowError("Error"),
//            DocumentDialogUiState.ShowUploadError("Upload Error"),
//        )
//}

//@Preview(showBackground = true)
//@Composable
//private fun DocumentDialogPreview(
//    @PreviewParameter(DocumentDialogPreviewProvider::class) state: DocumentDialogUiState,
//) {
//    DocumentDialogScreen(
//        uiState = state,
//        documentAction = "",
//        document = Document(),
//        openFilePicker = { },
//        closeDialog = { },
//        uploadDocument = { _, _ -> },
//        filename = "",
//        closeScreen = { },
//    )
//}
