/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.updateServer

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.model.ServerConfig
import com.mifos.core.ui.util.DevicePreviews
import com.mifos.feature.settings.R

@Composable
fun UpdateServerConfigScreenRoute(
    onBackClick: () -> Unit,
    onSuccessful: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateServerConfigViewModel = hiltViewModel(),
) {
    val protocolError by viewModel.protocolError.collectAsStateWithLifecycle()
    val apiPathError by viewModel.apiPathError.collectAsStateWithLifecycle()
    val endPointError by viewModel.endPointError.collectAsStateWithLifecycle()
    val portError by viewModel.portError.collectAsStateWithLifecycle()
    val tenantError by viewModel.tenantError.collectAsStateWithLifecycle()

    val result by viewModel.result.collectAsStateWithLifecycle(false)

    LaunchedEffect(result) {
        if (result) {
            onSuccessful()
        }
    }

    UpdateServerConfigScreenContent(
        modifier = modifier,
        serverConfig = viewModel.state.value,
        protocolError = protocolError,
        apiPathError = apiPathError,
        endPointError = endPointError,
        portError = portError,
        tenantError = tenantError,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick,
    )
}

@VisibleForTesting
@Composable
internal fun UpdateServerConfigScreenContent(
    serverConfig: ServerConfig,
    onEvent: (UpdateServerConfigEvent) -> Unit,
    modifier: Modifier = Modifier,
    protocolError: Int? = null,
    apiPathError: Int? = null,
    endPointError: Int? = null,
    portError: Int? = null,
    tenantError: Int? = null,
    onBackClick: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val hasAnyError = listOf(
        protocolError,
        apiPathError,
        endPointError,
        portError,
        tenantError,
    ).any { it != null }

    MifosScaffold(
        modifier = modifier,
        title = stringResource(R.string.feature_settings_title),
        icon = MifosIcons.arrowBack,
        onBackPressed = onBackClick,
        snackbarHostState = null,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            state = lazyListState,
        ) {
            item {
                MifosOutlinedTextField(
                    value = serverConfig.protocol,
                    label = stringResource(R.string.feature_settings_label_protocol),
                    leadingIcon = Icons.Default.AddLink,
                    isError = protocolError != null,
                    errorText = protocolError?.let { stringResource(it) },
                    placeholder = stringResource(R.string.feature_settings_protocol_placeholder),
                    keyboardType = KeyboardType.Uri,
                    showClearIcon = serverConfig.protocol.isNotEmpty(),
                    onClickClearIcon = {
                        onEvent(UpdateServerConfigEvent.UpdateProtocol(""))
                    },
                    onValueChange = {
                        onEvent(UpdateServerConfigEvent.UpdateProtocol(it))
                    },
                )
            }

            item {
                MifosOutlinedTextField(
                    value = serverConfig.endPoint,
                    label = stringResource(R.string.feature_settings_label_endpoint),
                    leadingIcon = Icons.Default.Link,
                    isError = endPointError != null,
                    errorText = endPointError?.let { stringResource(it) },
                    placeholder = stringResource(R.string.feature_settings_endpoint_placeholder),
                    showClearIcon = serverConfig.endPoint.isNotEmpty(),
                    onClickClearIcon = {
                        onEvent(UpdateServerConfigEvent.UpdateEndPoint(""))
                    },
                    onValueChange = {
                        onEvent(UpdateServerConfigEvent.UpdateEndPoint(it))
                    },
                )
            }

            item {
                MifosOutlinedTextField(
                    value = serverConfig.apiPath,
                    label = stringResource(R.string.feature_settings_label_api_path),
                    leadingIcon = Icons.Default.Link,
                    isError = apiPathError != null,
                    errorText = apiPathError?.let { stringResource(it) },
                    placeholder = stringResource(R.string.feature_settings_api_path_placeholder),
                    showClearIcon = serverConfig.endPoint.isNotEmpty(),
                    onClickClearIcon = {
                        onEvent(UpdateServerConfigEvent.UpdateEndPoint(""))
                    },
                    onValueChange = {
                        onEvent(UpdateServerConfigEvent.UpdateApiPath(it))
                    },
                )
            }

            item {
                MifosOutlinedTextField(
                    value = serverConfig.port,
                    label = stringResource(R.string.feature_settings_label_port),
                    leadingIcon = Icons.Default.Link,
                    isError = portError != null,
                    errorText = portError?.let { stringResource(it) },
                    placeholder = stringResource(R.string.feature_settings_port_placeholder),
                    keyboardType = KeyboardType.Number,
                    showClearIcon = serverConfig.port.isNotEmpty(),
                    onClickClearIcon = {
                        onEvent(UpdateServerConfigEvent.UpdatePort(""))
                    },
                    onValueChange = {
                        onEvent(UpdateServerConfigEvent.UpdatePort(it))
                    },
                )
            }

            item {
                MifosOutlinedTextField(
                    value = serverConfig.tenant,
                    label = stringResource(R.string.feature_settings_label_tenant),
                    leadingIcon = Icons.Default.Link,
                    isError = tenantError != null,
                    errorText = tenantError?.let { stringResource(it) },
                    placeholder = stringResource(R.string.feature_settings_tenant_placeholder),
                    showClearIcon = serverConfig.tenant.isNotEmpty(),
                    onClickClearIcon = {
                        onEvent(UpdateServerConfigEvent.UpdateTenant(""))
                    },
                    onValueChange = {
                        onEvent(UpdateServerConfigEvent.UpdateTenant(it))
                    },
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "infoIcon",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp),
                    )

                    Text(
                        text = stringResource(R.string.feature_settings_note_text),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                Spacer(Modifier.height(8.dp))
            }

            item {
                ElevatedButton(
                    onClick = {
                        onEvent(UpdateServerConfigEvent.UpdateServerConfig)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !hasAnyError,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = BluePrimary,
                        contentColor = Color.White,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "updateConfig",
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.feature_settings_update_config_btn_text).uppercase())
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun UpdateServerConfigScreenEmptyData() {
    MaterialTheme {
        UpdateServerConfigScreenContent(
            serverConfig = ServerConfig(
                protocol = "",
                endPoint = "",
                apiPath = "",
                port = "",
                tenant = "",
            ),
            onEvent = {},
            onBackClick = {},
        )
    }
}
