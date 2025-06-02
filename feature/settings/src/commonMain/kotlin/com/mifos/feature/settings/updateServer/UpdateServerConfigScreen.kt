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
import androidclient.feature.settings.generated.resources.feature_settings_close_bottomsheet
import androidclient.feature.settings.generated.resources.feature_settings_endpoint_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_label_api_path
import androidclient.feature.settings.generated.resources.feature_settings_label_endpoint
import androidclient.feature.settings.generated.resources.feature_settings_label_port
import androidclient.feature.settings.generated.resources.feature_settings_label_protocol
import androidclient.feature.settings.generated.resources.feature_settings_label_tenant
import androidclient.feature.settings.generated.resources.feature_settings_note_text
import androidclient.feature.settings.generated.resources.feature_settings_port_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_protocol_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_tenant_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_title
import androidclient.feature.settings.generated.resources.feature_settings_update_config_btn_text
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.ServerConfig
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.util.DevicePreview
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UpdateServerConfigScreenRoute(
    onCloseClick: () -> Unit,
    onSuccessful: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateServerConfigViewModel = koinViewModel(),
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
        serverConfig = viewModel.state.value!!,
        protocolError = protocolError,
        apiPathError = apiPathError,
        endPointError = endPointError,
        portError = portError,
        tenantError = tenantError,
        onEvent = viewModel::onEvent,
        onCloseClick = onCloseClick,
    )
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
    onCloseClick: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val hasAnyError = listOf(
        protocolError,
        apiPathError,
        endPointError,
        portError,
        tenantError,
    ).any { it != null }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                    )
                    .padding(12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.feature_settings_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedIconButton(
                        onClick = onCloseClick,
                    ) {
                        Icon(
                            imageVector = MifosIcons.Close,
                            contentDescription = stringResource(Res.string.feature_settings_close_bottomsheet),
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(4.dp),
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
                        colors = ButtonDefaults.elevatedButtonColors(
                            contentColor = Color.White,
                        ),
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
            onCloseClick = {},
        )
    }
}
