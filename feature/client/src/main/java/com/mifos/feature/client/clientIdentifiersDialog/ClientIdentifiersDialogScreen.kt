package com.mifos.feature.client.clientIdentifiersDialog

import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.noncore.IdentifierPayload
import com.mifos.core.objects.noncore.IdentifierTemplate
import com.mifos.feature.client.R

@Composable
fun ClientIdentifiersDialogScreen(
    clientId: Int,
    viewModel: ClientIdentifiersDialogViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onIdentifierCreated: () -> Unit,
) {

    val state by viewModel.clientIdentifierDialogUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadClientIdentifierTemplate(clientId)
    }

    ClientIdentifiersDialogScreen(
        state = state,
        onDismiss = onDismiss,
        onIdentifierCreated = onIdentifierCreated,
        onRetry = {
            viewModel.loadClientIdentifierTemplate(clientId = clientId)
        },
        onCreate = {
            viewModel.createClientIdentifier(clientId, it)
        }
    )

}

@Composable
fun ClientIdentifiersDialogScreen(
    state: ClientIdentifierDialogUiState,
    onDismiss: () -> Unit,
    onIdentifierCreated: () -> Unit,
    onRetry: () -> Unit,
    onCreate: (IdentifierPayload) -> Unit
) {


    Dialog(onDismissRequest = { onDismiss() }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center
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
                            text = stringResource(id = R.string.feature_client_create_identifier_dialog),
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            color = BluePrimary
                        )
                        IconButton(onClick = { onDismiss() }) {
                            Icon(
                                imageVector = MifosIcons.close,
                                contentDescription = "",
                                tint = colorResource(android.R.color.darker_gray),
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(30.dp)
                            )
                        }
                    }
                    when (state) {
                        is ClientIdentifierDialogUiState.ClientIdentifierTemplate -> {
                            ClientIdentifiersContent(
                                clientIdentifierTemplate = state.identifierTemplate,
                                onCreate = onCreate
                            )
                        }

                        is ClientIdentifierDialogUiState.Error -> MifosSweetError(
                            message = stringResource(
                                id = state.message
                            )
                        ) {
                            onRetry()
                        }

                        is ClientIdentifierDialogUiState.IdentifierCreatedSuccessfully -> {
                            Toast.makeText(
                                LocalContext.current,
                                stringResource(id = R.string.feature_client_identifier_created_successfully),
                                Toast.LENGTH_SHORT
                            ).show()
                            onIdentifierCreated()
                        }

                        is ClientIdentifierDialogUiState.Loading -> MifosCircularProgress()
                    }
                }
            }
        }
    }
}

@Composable
fun ClientIdentifiersContent(
    clientIdentifierTemplate: IdentifierTemplate,
    onCreate: (IdentifierPayload) -> Unit
) {
    var documentType by rememberSaveable {
        mutableStateOf(
            clientIdentifierTemplate.allowedDocumentTypes?.get(
                0
            )?.name ?: ""
        )
    }
    var documentTypeId by rememberSaveable {
        mutableStateOf(
            clientIdentifierTemplate.allowedDocumentTypes?.get(
                0
            )?.id
        )
    }
    var uniqueId by rememberSaveable { mutableStateOf("") }
    var uniqueIdError by rememberSaveable { mutableStateOf(false) }
    var description by rememberSaveable { mutableStateOf("") }
    var descriptionError by rememberSaveable { mutableStateOf(false) }
    var isActive by rememberSaveable { mutableStateOf(false) }

    fun validateInput(): Boolean {
        var temp = true
        if (uniqueId.isEmpty()) {
            uniqueIdError = true
            temp = false
        }
        if (description.isEmpty()) {
            descriptionError = true
            temp = false
        }
        return temp
    }


    MifosTextFieldDropdown(
        value = documentType,
        onValueChanged = {
            documentType = it
        },
        onOptionSelected = { index, value ->
            documentType = value
            documentTypeId = clientIdentifierTemplate.allowedDocumentTypes?.get(index)?.id
        },
        label = R.string.feature_client_identifier_document_type,
        options = clientIdentifierTemplate.allowedDocumentTypes?.map { it.name.toString() }
            ?: emptyList(),
        readOnly = true
    )

    MifosOutlinedTextField(
        value = uniqueId,
        onValueChange = {
            uniqueId = it
            uniqueIdError = false
        },
        label = stringResource(id = R.string.feature_client_identifier_unique_id),
        error = if (uniqueIdError) R.string.feature_client_identifier_message_field_required else null,
        trailingIcon = {
            if (uniqueIdError) {
                Icon(
                    imageVector = MifosIcons.error,
                    contentDescription = null
                )
            }
        }
    )

    MifosOutlinedTextField(
        value = description,
        onValueChange = {
            description = it
            descriptionError = false
        },
        label = stringResource(id = R.string.feature_client_identifier_description),
        error = if (descriptionError) R.string.feature_client_identifier_message_field_required else null,
        trailingIcon = {
            if (descriptionError) {
                Icon(
                    imageVector = MifosIcons.error,
                    contentDescription = null
                )
            }
        }
    )

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isActive,
            onCheckedChange = {
                isActive = it
            }
        )
        Text(text = stringResource(id = R.string.feature_client_identifier_isActive))
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            if (validateInput()) {

                val payload = IdentifierPayload(
                    documentTypeId = documentTypeId,
                    documentKey = uniqueId,
                    status = if (isActive) "Active" else "InActive",
                    description = description
                )
                onCreate(payload)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonColors(
            containerColor = BluePrimary,
            contentColor = White,
            disabledContainerColor = BluePrimary,
            disabledContentColor = Gray
        )
    ) {
        Text(text = stringResource(id = R.string.feature_client_identifier_submit))
    }
}

class ClientIdentifiersDialogUiStatePreview :
    PreviewParameterProvider<ClientIdentifierDialogUiState> {

    override val values: Sequence<ClientIdentifierDialogUiState>
        get() = sequenceOf(
            ClientIdentifierDialogUiState.Loading,
            ClientIdentifierDialogUiState.Error(R.string.feature_client_failed_to_load_client_identifiers),
            ClientIdentifierDialogUiState.IdentifierCreatedSuccessfully
        )

}

@Preview(showBackground = true)
@Composable
private fun ClientIdentifiersDialogScreenPreview(
    @PreviewParameter(ClientIdentifiersDialogUiStatePreview::class) state: ClientIdentifierDialogUiState
) {
    ClientIdentifiersDialogScreen(
        state = state,
        onDismiss = {},
        onIdentifierCreated = {},
        onRetry = {},
        onCreate = {}
    )
}