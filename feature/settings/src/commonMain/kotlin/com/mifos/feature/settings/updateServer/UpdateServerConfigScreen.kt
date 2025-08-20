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
import androidclient.feature.settings.generated.resources.feature_settings_hostname_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_label_api_path
import androidclient.feature.settings.generated.resources.feature_settings_label_hostname
import androidclient.feature.settings.generated.resources.feature_settings_label_port
import androidclient.feature.settings.generated.resources.feature_settings_label_protocol
import androidclient.feature.settings.generated.resources.feature_settings_label_tenant
import androidclient.feature.settings.generated.resources.feature_settings_note_text
import androidclient.feature.settings.generated.resources.feature_settings_protocol_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_restart
import androidclient.feature.settings.generated.resources.feature_settings_title
import androidclient.feature.settings.generated.resources.feature_settings_update_config_btn_text
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
                .padding(it)
                .background(Color(0xfff8f9fa).copy(alpha = .1f)),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            state = lazyListState,
        ) {
            item {
                Text(
                    "Quick Setup",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.SansSerif,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ElevatedButton(
                        onClick = {
                            onEvent(UpdateServerConfigEvent.UpdateProtocol(ServerConfig.LOCALHOST.protocol))
                            onEvent(UpdateServerConfigEvent.UpdateEndPoint(ServerConfig.LOCALHOST.endPoint))
                            onEvent(UpdateServerConfigEvent.UpdatePort(ServerConfig.LOCALHOST.port))
                        },
                        modifier = Modifier
                            .height(72.dp)
                            .weight(1f),
                        enabled = !hasAnyError,
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff007AFF),
                            disabledContainerColor = Color.White,
                            disabledContentColor = Color(0xff007AFF),
                            contentColor = Color.White,
                        ),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                "\uD83C\uDFE0 Local",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.SansSerif,
                            )
                            Text(
                                "Development",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.width(12.dp),
                    )

                    ElevatedButton(
                        onClick = {
                            onEvent(UpdateServerConfigEvent.UseDefaultConfig)
                        },
                        modifier = Modifier
                            .height(72.dp)
                            .weight(1f),
                        enabled = !hasAnyError,
                        shape = RoundedCornerShape(20),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff007AFF),
                            disabledContainerColor = Color.White,
                            disabledContentColor = Color(0xff007AFF),
                            contentColor = Color.White,
                        ),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                "\uD83C\uDF10 Demo",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.SansSerif,
                            )
                            Text(
                                "Environment",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray,
                    )

                    Text(
                        "OR",
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        fontFamily = FontFamily.SansSerif,
                    )

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray,
                    )
                }
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_protocol),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 16.dp),
                    fontFamily = FontFamily.SansSerif,
                )
                MifosOutlinedTextField(
                    value = serverConfig.protocol,
                    placeholder = stringResource(Res.string.feature_settings_protocol_placeholder),
                    leadingIcon = MifosIcons.AddLink,
                    errorText = protocolError,
                    keyboardType = KeyboardType.Uri,
                    errorTextTag = serverConfig.protocol,
                    onValueChange = {
                        onEvent(UpdateServerConfigEvent.UpdateProtocol(it))
                    },
                    shape = RoundedCornerShape(16),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xffdddddd),
                        unfocusedBorderColor = Color(0xffdddddd),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                    label = "",
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_hostname),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 16.dp),
                    fontFamily = FontFamily.SansSerif,
                )
                MifosOutlinedTextField(
                    value = serverConfig.endPoint,
                    label = "",
                    leadingIcon = MifosIcons.Link,
                    isError = endPointError != null,
                    errorText = endPointError,
                    placeholder = stringResource(Res.string.feature_settings_hostname_placeholder),
                    onValueChange = {
                        onEvent(UpdateServerConfigEvent.UpdateEndPoint(it))
                    },
                    shape = RoundedCornerShape(16),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xffdddddd),
                        unfocusedBorderColor = Color(0xffdddddd),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_api_path),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 16.dp),
                    fontFamily = FontFamily.SansSerif,
                )

                MifosOutlinedTextField(
                    value = serverConfig.apiPath,
                    leadingIcon = MifosIcons.Link,
                    isError = apiPathError != null,
                    errorText = apiPathError,
                    keyboardType = KeyboardType.Uri,
                    errorTextTag = serverConfig.apiPath,
                    placeholder = stringResource(Res.string.feature_settings_api_path_placeholder),
                    onValueChange = {
                        onEvent(UpdateServerConfigEvent.UpdateApiPath(it))
                    },
                    shape = RoundedCornerShape(16),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xffdddddd),
                        unfocusedBorderColor = Color(0xffdddddd),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                    label = "",
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_port),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 16.dp),
                    fontFamily = FontFamily.SansSerif,
                )
                MifosOutlinedTextField(
                    value = serverConfig.port,
                    leadingIcon = MifosIcons.Link,
                    isError = portError != null,
                    errorText = portError,
                    keyboardType = KeyboardType.Number,
                    errorTextTag = serverConfig.port,
                    onValueChange = {
                        onEvent(UpdateServerConfigEvent.UpdatePort(it))
                    },
                    shape = RoundedCornerShape(16),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xffdddddd),
                        unfocusedBorderColor = Color(0xffdddddd),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                    label = "",
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_tenant),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 16.dp),
                    fontFamily = FontFamily.SansSerif,
                )
                MifosOutlinedTextField(
                    value = serverConfig.tenant,
                    leadingIcon = MifosIcons.Link,
                    isError = tenantError != null,
                    errorText = tenantError,
                    keyboardType = KeyboardType.Uri,
                    errorTextTag = serverConfig.tenant,
                    onValueChange = {
                        onEvent(UpdateServerConfigEvent.UpdateTenant(it))
                    },
                    shape = RoundedCornerShape(16),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xffdddddd),
                        unfocusedBorderColor = Color(0xffdddddd),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                    label = "",
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16))
                        .border(
                            1.dp,
                            Color(0xffffeaa7),
                            RoundedCornerShape(16),
                        )
                        .background(Color(0xfffff3cd)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            "⚠\uFE0F",
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                        Text(
                            text = stringResource(Res.string.feature_settings_note_text),
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xff856404),
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = FontFamily.SansSerif,
                        )
                    }
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
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    enabled = !hasAnyError,
                    shape = RoundedCornerShape(16),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff007AFF),
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color(0xff007AFF),
                        contentColor = Color.White,
                    ),
                ) {
                    Text(
                        "⬇\uFE0F  " +
                            stringResource(
                                Res.string.feature_settings_update_config_btn_text,
                            ).uppercase(),
                        fontFamily = FontFamily.SansSerif,
                    )
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
