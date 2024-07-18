package com.mifos.feature.document.document_dialog

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.White
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.noncore.Document
import com.mifos.feature.document.R
import java.io.File
import com.mifos.core.common.utils.FileUtils


@Composable
fun DocumentDialogScreen(
    viewModel: DocumentDialogViewModel = hiltViewModel(),
    documentAction: String?,
    document: Document?,
    closeDialog: () -> Unit,
    entityType: String,
    entityId: Int,
    closeScreen: () -> Unit
) {

    val context = LocalContext.current
    val state by viewModel.documentDialogUiState.collectAsStateWithLifecycle()
    val requiredPermissions = if (Build.VERSION.SDK_INT >= 33) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
    var fileName by rememberSaveable { mutableStateOf<String?>(document?.name) }
    var fileChosen by rememberSaveable { mutableStateOf<File?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let { uri ->
            val filePath = FileUtils.getPathReal(context, uri)
            filePath?.let { path ->
                fileChosen = File(path)
                fileName = fileChosen!!.name
            }
        }
    }
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionsMap: Map<String, Boolean> ->
        permissionsMap.forEach { (permission, isGranted) ->
            if (isGranted) {
                Toast.makeText(context, context.getString(R.string.feature_document_permission_granted), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, context.getString(R.string.feature_document_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    DocumentDialogScreen(
        uiState = state,
        documentAction = documentAction,
        document = document,
        openFilePicker = {
            if ( checkPermission(context) ) {
                launcher.launch(
                    PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                )
            } else {
                permissionsLauncher.launch(requiredPermissions)
            }
        },
        closeDialog = closeDialog,
        uploadDocument = { documentName, documentDescription ->

            if (documentAction == context.getString(R.string.feature_document_update_document)) {
                viewModel.updateDocument(
                    entityType, entityId, document!!.id, documentName, documentDescription, fileChosen!!
                )
            } else if (documentAction == context.getString(R.string.feature_document_upload_document)) {
                viewModel.createDocument(
                    entityType, entityId, documentName, documentDescription, fileChosen!!
                )
            }
        },
        filename = fileName,
        closeScreen = closeScreen
    )
}

fun checkPermission( context: Context ) : Boolean {
    if ( Build.VERSION.SDK_INT >= 33 )
    {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PermissionChecker.PERMISSION_GRANTED)
            return true;
    }else {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED )
            return true;
    }

    return false;
}

@Composable
fun DocumentDialogScreen(
    uiState: DocumentDialogUiState,
    documentAction: String?,
    document: Document?,
    openFilePicker: () -> Unit,
    closeDialog: () -> Unit?,
    uploadDocument: (String, String) -> Unit,
    filename: String?,
    closeScreen: () -> Unit
) {
    val context = LocalContext.current

    DocumentDialogContent(
        document = document,
        documentAction = documentAction,
        setShowDialog = { closeDialog.invoke() },
        openFilePicker = openFilePicker,
        uploadDocument = uploadDocument,
        fileName = filename
    )

    when (uiState) {

        is DocumentDialogUiState.Initial -> Unit

        is DocumentDialogUiState.ShowProgressbar -> {
            MifosCircularProgress()
        }

        is DocumentDialogUiState.ShowDocumentedCreatedSuccessfully -> {
            LaunchedEffect(true) {
                Toast.makeText(
                    context,
                    String.format(context.getString(R.string.feature_document_uploaded_successfully), filename),
                    Toast.LENGTH_SHORT
                ).show()
            }
            closeDialog.invoke()
        }

        is DocumentDialogUiState.ShowDocumentUpdatedSuccessfully -> {
            LaunchedEffect(true) {
                Toast.makeText(
                    context,
                    String.format(context.getString(R.string.feature_document_document_updated_successfully), filename),
                    Toast.LENGTH_SHORT
                ).show()
            }
            closeDialog.invoke()
        }

        is DocumentDialogUiState.ShowUploadError -> {
            LaunchedEffect(true){
                Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
            }
            closeScreen.invoke()
        }

        is DocumentDialogUiState.ShowError -> {
            LaunchedEffect(true) {
                Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
            }
            closeScreen.invoke()
        }
    }
}

@Composable
fun DocumentDialogContent(
    setShowDialog: (Boolean) -> Unit,
    documentAction: String?,
    document: Document?,
    openFilePicker: () -> Unit,
    uploadDocument: (String, String) -> Unit,
    fileName: String?
) {

    var dialogTitle = stringResource(id = R.string.feature_document_upload_document)
    var name by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var description by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var nameError by rememberSaveable { mutableStateOf(false) }
    var descriptionError by rememberSaveable { mutableStateOf(false) }
    var fileError by rememberSaveable { mutableStateOf(false) }

    if( documentAction == stringResource(id = R.string.feature_document_update_document))
    {
        dialogTitle = stringResource(id = R.string.feature_document_update_document)
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
        }else {
            fileError = false
        }

        return temp
    }

    Dialog(onDismissRequest = { setShowDialog(false) }) {

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dialogTitle,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = BluePrimary
                    )
                    Icon(
                        imageVector = MifosIcons.cancel,
                        contentDescription = "",
                        tint = colorResource(android.R.color.darker_gray),
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                            .clickable { setShowDialog(false) }
                    )
                }

                MifosOutlinedTextField(
                    value = name,
                    onValueChange = { value ->
                        name = value
                        nameError = false
                    },
                    label = R.string.feature_document_name,
                    error = if(nameError) R.string.feature_document_message_field_required else null,
                    trailingIcon = {
                        if (nameError) {
                            Icon(imageVector = MifosIcons.error, contentDescription = null)
                        }
                    }
                )


                MifosOutlinedTextField(
                    value = description,
                    onValueChange = { value ->
                        description = value
                        descriptionError = false
                    },
                    label = R.string.feature_document_description,
                    error = if(descriptionError) R.string.feature_document_message_field_required else null,
                    trailingIcon = {
                        if (descriptionError) {
                            Icon(imageVector = MifosIcons.error, contentDescription = null)
                        }
                    }
                )


                androidx.compose.material3.OutlinedTextField(
                    value = if(fileName != null) TextFieldValue(fileName) else TextFieldValue(""),
                    onValueChange = {
                        fileError = false
                    },
                    label = { Text(stringResource(id = R.string.feature_document_selected_file)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    trailingIcon = {
                        if (descriptionError) {
                            Icon(imageVector = MifosIcons.error, contentDescription = null)
                        }
                    },
                    enabled = false,
                    maxLines = 1,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
                    ),
                    textStyle = LocalDensity.current.run {
                        TextStyle(fontSize = 18.sp)
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    isError = fileError,
                    supportingText = {
                        if (fileError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.feature_document_message_file_required),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                DialogButton(
                    text = stringResource(id = R.string.feature_document_browse),
                    onClick = openFilePicker
                )

                Spacer(modifier = Modifier.height(20.dp))

                DialogButton(
                    text = stringResource(id = R.string.feature_document_upload),
                    onClick = {
                        if (validateInput()) {
                            uploadDocument.invoke(name.text, description.text)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DialogButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(20.dp, 0.dp, 20.dp, 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BluePrimary,
            contentColor = White,
            disabledContainerColor = BluePrimary,
            disabledContentColor = White
        )
    ) {
        Text(text = text)
    }
}

class DocumentDialogPreviewProvider : PreviewParameterProvider<DocumentDialogUiState> {
    override val values: Sequence<DocumentDialogUiState>
        get() = sequenceOf(
            DocumentDialogUiState.Initial,
            DocumentDialogUiState.ShowProgressbar,
            DocumentDialogUiState.ShowDocumentUpdatedSuccessfully(GenericResponse()),
            DocumentDialogUiState.ShowDocumentedCreatedSuccessfully(GenericResponse()),
            DocumentDialogUiState.ShowError("Error"),
            DocumentDialogUiState.ShowUploadError("Upload Error")
        )
}

@Preview(showBackground = true)
@Composable
private fun DocumentDialogPreview(
    @PreviewParameter(DocumentDialogPreviewProvider::class) state: DocumentDialogUiState
) {
    DocumentDialogScreen(
        uiState = state,
        documentAction = "",
        document = Document(),
        openFilePicker = {  },
        closeDialog = {  },
        uploadDocument = { _, _,->  },
        filename = "",
        closeScreen = { }
    )
}