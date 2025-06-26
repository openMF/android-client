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

import androidclient.feature.settings.generated.resources.Res
import androidclient.feature.settings.generated.resources.feature_settings_api_path_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_endpoint_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_label_api_path
import androidclient.feature.settings.generated.resources.feature_settings_label_endpoint
import androidclient.feature.settings.generated.resources.feature_settings_label_port
import androidclient.feature.settings.generated.resources.feature_settings_label_protocol
import androidclient.feature.settings.generated.resources.feature_settings_label_tenant
import androidclient.feature.settings.generated.resources.feature_settings_note_text
import androidclient.feature.settings.generated.resources.feature_settings_port_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_protocol_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_restart
import androidclient.feature.settings.generated.resources.feature_settings_tenant_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_title
import androidclient.feature.settings.generated.resources.feature_settings_update_config_btn_text
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.ServerConfig
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.util.DevicePreview
import com.mifos.core.ui.util.ShareUtils
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UpdateServerConfigScreenRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateServerConfigViewModel = koinViewModel(),
) {
    val protocolError by viewModel.protocolError.collectAsStateWithLifecycle()
    val apiPathError by viewModel.apiPathError.collectAsStateWithLifecycle()
    val endPointError by viewModel.endPointError.collectAsStateWithLifecycle()
    val portError by viewModel.portError.collectAsStateWithLifecycle()
    val tenantError by viewModel.tenantError.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val result by viewModel.result.collectAsStateWithLifecycle(false)
    var showCountdown by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(result) {
        if (result) {
            showCountdown = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        UpdateServerConfigScreenContent(
            modifier = modifier,
            serverConfig = viewModel.state.value!!,
            protocolError = protocolError,
            apiPathError = apiPathError,
            endPointError = endPointError,
            portError = portError,
            tenantError = tenantError,
            onEvent = viewModel::onEvent,
            onBackClick = onBackClick,
            snackbarHostState = snackbarHostState,
        )

        if (showCountdown) {
            SimpleCountdownSnackbar(
                message = Res.string.feature_settings_restart,
                durationSeconds = 5,
                onDismiss = {
                    showCountdown = false
                    ShareUtils.restartApplication()
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@VisibleForTesting
@Composable
internal fun UpdateServerConfigScreenContent(
    serverConfig: ServerConfig,
    onEvent: (UpdateServerConfigEvent) -> Unit,
    modifier: Modifier = Modifier,
    protocolError: String? = null,
    apiPathError: String? = null,
    endPointError: String? = null,
    portError: String? = null,
    tenantError: String? = null,
    onBackClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
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
        title = stringResource(Res.string.feature_settings_title),
        onBackPressed = onBackClick,
        snackbarHostState = snackbarHostState,
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
                    label = stringResource(Res.string.feature_settings_label_protocol),
                    leadingIcon = MifosIcons.AddLink,
                    isError = protocolError != null,
                    errorText = protocolError,
                    placeholder = stringResource(Res.string.feature_settings_protocol_placeholder),
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
                    label = stringResource(Res.string.feature_settings_label_endpoint),
                    leadingIcon = MifosIcons.Link,
                    isError = endPointError != null,
                    errorText = endPointError,
                    placeholder = stringResource(Res.string.feature_settings_endpoint_placeholder),
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
                    label = stringResource(Res.string.feature_settings_label_api_path),
                    leadingIcon = MifosIcons.Link,
                    isError = apiPathError != null,
                    errorText = apiPathError,
                    placeholder = stringResource(Res.string.feature_settings_api_path_placeholder),
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
                    label = stringResource(Res.string.feature_settings_label_port),
                    leadingIcon = MifosIcons.Link,
                    isError = portError != null,
                    errorText = portError,
                    placeholder = stringResource(Res.string.feature_settings_port_placeholder),
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
                    label = stringResource(Res.string.feature_settings_label_tenant),
                    leadingIcon = MifosIcons.Link,
                    isError = tenantError != null,
                    errorText = tenantError,
                    placeholder = stringResource(Res.string.feature_settings_tenant_placeholder),
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
                        imageVector = MifosIcons.Info,
                        contentDescription = "infoIcon",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp),
                    )

                    Text(
                        text = stringResource(Res.string.feature_settings_note_text),
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
                ) {
                    Icon(
                        imageVector = MifosIcons.Save,
                        contentDescription = "updateConfig",
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(Res.string.feature_settings_update_config_btn_text).uppercase())
                }
            }
        }
    }
}

@Composable
private fun SimpleCountdownSnackbar(
    message: StringResource,
    durationSeconds: Int = 5,
    dismissText: String? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onAboutToEnd: () -> Unit = {},
) {
    var countdown by rememberSaveable { mutableStateOf(durationSeconds) }

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000L)
            countdown--

            if (countdown == 1) {
                onAboutToEnd()
            }
        } else {
            onDismiss()
        }
    }

    Snackbar(
        modifier = modifier.padding(16.dp),
        action = {
            if (dismissText != null) {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            }
        },
    ) {
        Text(
            text = stringResource(message, countdown),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@DevicePreview
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
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
