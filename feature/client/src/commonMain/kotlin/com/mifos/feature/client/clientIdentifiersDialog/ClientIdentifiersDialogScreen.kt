/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientIdentifiersDialog

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_create_identifier_dialog
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client_identifiers
import androidclient.feature.client.generated.resources.feature_client_identifier_created_successfully
import androidclient.feature.client.generated.resources.feature_client_identifier_description
import androidclient.feature.client.generated.resources.feature_client_identifier_document_type
import androidclient.feature.client.generated.resources.feature_client_identifier_isActive
import androidclient.feature.client.generated.resources.feature_client_identifier_message_field_required
import androidclient.feature.client.generated.resources.feature_client_identifier_submit
import androidclient.feature.client.generated.resources.feature_client_identifier_unique_id
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.objects.noncoreobjects.IdentifierPayload
import com.mifos.core.model.objects.noncoreobjects.IdentifierTemplate
import com.mifos.core.ui.util.DevicePreview
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientIdentifiersDialogScreen(
    clientId: Int,
    onDismiss: () -> Unit,
    onIdentifierCreated: () -> Unit,
    viewModel: ClientIdentifiersDialogViewModel = koinViewModel(),
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
        },
    )
}

@Composable
internal fun ClientIdentifiersDialogScreen(
    state: ClientIdentifierDialogUiState,
    onDismiss: () -> Unit,
    onIdentifierCreated: () -> Unit,
    onRetry: () -> Unit,
    onCreate: (IdentifierPayload) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Box(
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
                            text = stringResource(Res.string.feature_client_create_identifier_dialog),
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        )
                        IconButton(onClick = { onDismiss() }) {
                            Icon(
                                imageVector = MifosIcons.Close,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .width(30.dp)
                                    .height(30.dp),
                            )
                        }
                    }
                    when (state) {
                        is ClientIdentifierDialogUiState.ClientIdentifierTemplate -> {
                            ClientIdentifiersContent(
                                clientIdentifierTemplate = state.identifierTemplate,
                                onCreate = onCreate,
                            )
                        }

                        is ClientIdentifierDialogUiState.Error -> MifosSweetError(
                            message = stringResource(state.message),
                        ) {
                            onRetry()
                        }

                        is ClientIdentifierDialogUiState.IdentifierCreatedSuccessfully -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = getString(
                                        Res.string.feature_client_identifier_created_successfully,
                                    ),
                                )
                            }
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
private fun ClientIdentifiersContent(
    clientIdentifierTemplate: IdentifierTemplate,
    onCreate: (IdentifierPayload) -> Unit,
) {
    var documentType by rememberSaveable {
        mutableStateOf(
            clientIdentifierTemplate.allowedDocumentTypes?.get(
                0,
            )?.name ?: "",
        )
    }
    var documentTypeId by rememberSaveable {
        mutableStateOf(
            clientIdentifierTemplate.allowedDocumentTypes?.get(
                0,
            )?.id,
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
    Column {
        MifosTextFieldDropdown(
            value = documentType,
            onValueChanged = {
                documentType = it
            },
            onOptionSelected = { index, value ->
                documentType = value
                documentTypeId = clientIdentifierTemplate.allowedDocumentTypes?.get(index)?.id
            },
            label = stringResource(Res.string.feature_client_identifier_document_type),
            options = clientIdentifierTemplate.allowedDocumentTypes?.map { it.name.toString() }
                ?: emptyList(),
            readOnly = true,
        )

        MifosOutlinedTextField(
            value = uniqueId,
            onValueChange = {
                uniqueId = it
                uniqueIdError = false
            },
            label = stringResource(Res.string.feature_client_identifier_unique_id),
            error = if (uniqueIdError) stringResource(Res.string.feature_client_identifier_message_field_required) else null,
            trailingIcon = {
                if (uniqueIdError) {
                    Icon(
                        imageVector = MifosIcons.Error,
                        contentDescription = null,
                    )
                }
            },
        )

        MifosOutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                descriptionError = false
            },
            label = stringResource(Res.string.feature_client_identifier_description),
            error = if (descriptionError) stringResource(Res.string.feature_client_identifier_message_field_required) else null,
            trailingIcon = {
                if (descriptionError) {
                    Icon(
                        imageVector = MifosIcons.Error,
                        contentDescription = null,
                    )
                }
            },
        )

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isActive,
                onCheckedChange = {
                    isActive = it
                },
            )
            Text(text = stringResource(Res.string.feature_client_identifier_isActive))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (validateInput()) {
                    val payload = IdentifierPayload(
                        documentTypeId = documentTypeId,
                        documentKey = uniqueId,
                        status = if (isActive) "Active" else "InActive",
                        description = description,
                    )
                    onCreate(payload)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        ) {
            Text(text = stringResource(Res.string.feature_client_identifier_submit))
        }
    }
}

private class ClientIdentifiersDialogUiStatePreview :
    PreviewParameterProvider<ClientIdentifierDialogUiState> {

    override val values: Sequence<ClientIdentifierDialogUiState>
        get() = sequenceOf(
            ClientIdentifierDialogUiState.Loading,
            ClientIdentifierDialogUiState.Error(Res.string.feature_client_failed_to_load_client_identifiers),
            ClientIdentifierDialogUiState.IdentifierCreatedSuccessfully,
        )
}

@DevicePreview
@Composable
private fun ClientIdentifiersDialogScreenPreview(
    @PreviewParameter(ClientIdentifiersDialogUiStatePreview::class) state: ClientIdentifierDialogUiState,
) {
    ClientIdentifiersDialogScreen(
        state = state,
        onDismiss = {},
        onIdentifierCreated = {},
        onRetry = {},
        onCreate = {},
    )
}
