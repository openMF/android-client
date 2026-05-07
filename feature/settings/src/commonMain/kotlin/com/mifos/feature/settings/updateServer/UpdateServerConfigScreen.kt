/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.updateServer

import androidclient.feature.settings.generated.resources.Res
import androidclient.feature.settings.generated.resources.feature_settings_api_path_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_configuration_saved
import androidclient.feature.settings.generated.resources.feature_settings_demo
import androidclient.feature.settings.generated.resources.feature_settings_development
import androidclient.feature.settings.generated.resources.feature_settings_environment
import androidclient.feature.settings.generated.resources.feature_settings_hostname_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_label_api_path
import androidclient.feature.settings.generated.resources.feature_settings_label_hostname
import androidclient.feature.settings.generated.resources.feature_settings_label_port
import androidclient.feature.settings.generated.resources.feature_settings_label_protocol
import androidclient.feature.settings.generated.resources.feature_settings_label_tenant
import androidclient.feature.settings.generated.resources.feature_settings_local
import androidclient.feature.settings.generated.resources.feature_settings_note_text
import androidclient.feature.settings.generated.resources.feature_settings_or
import androidclient.feature.settings.generated.resources.feature_settings_protocol_placeholder
import androidclient.feature.settings.generated.resources.feature_settings_quick_setup
import androidclient.feature.settings.generated.resources.feature_settings_restart_application
import androidclient.feature.settings.generated.resources.feature_settings_restart_now
import androidclient.feature.settings.generated.resources.feature_settings_title
import androidclient.feature.settings.generated.resources.feature_settings_update_config_btn_text
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.ServerConfig
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.util.DevicePreview
import com.mifos.core.ui.util.ShareUtils
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {},
            )
            RestartCountdownDialog(
                durationSeconds = 5,
                onDismiss = {
                    showCountdown = false
                    ShareUtils.restartApplication()
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(KptTheme.spacing.lg),
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
                .background(KptTheme.colorScheme.surfaceVariant.copy(alpha = .1f)),
            contentPadding = PaddingValues(KptTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            state = lazyListState,
        ) {
            item {
                Text(
                    text = stringResource(Res.string.feature_settings_quick_setup),
                    modifier = Modifier.padding(horizontal = KptTheme.spacing.md, vertical = KptTheme.spacing.xs),
                    fontWeight = FontWeight.SemiBold,
                    style = KptTheme.typography.titleMedium,
                    fontFamily = FontFamily.SansSerif,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = KptTheme.spacing.md, vertical = DesignToken.padding.medium),
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
                            .height(DesignToken.sizes.profile)
                            .weight(1f),
                        enabled = !hasAnyError,
                        shape = DesignToken.shapes.largeIncreased,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KptTheme.colorScheme.primary,
                            disabledContainerColor = KptTheme.colorScheme.surface,
                            disabledContentColor = KptTheme.colorScheme.primary,
                            contentColor = KptTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = stringResource(Res.string.feature_settings_local),
                                style = KptTheme.typography.bodyMedium,
                                fontFamily = FontFamily.SansSerif,
                            )
                            Text(
                                text = stringResource(Res.string.feature_settings_development),
                                style = KptTheme.typography.bodyMedium,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.width(DesignToken.spacing.medium),
                    )

                    ElevatedButton(
                        onClick = {
                            onEvent(UpdateServerConfigEvent.UseDefaultConfig)
                        },
                        modifier = Modifier
                            .height(DesignToken.sizes.profile)
                            .weight(1f),
                        enabled = !hasAnyError,
                        shape = DesignToken.shapes.largeIncreased,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KptTheme.colorScheme.primary,
                            disabledContainerColor = KptTheme.colorScheme.surface,
                            disabledContentColor = KptTheme.colorScheme.primary,
                            contentColor = KptTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = stringResource(Res.string.feature_settings_demo),
                                style = KptTheme.typography.bodyMedium,
                                fontFamily = FontFamily.SansSerif,
                            )
                            Text(
                                text = stringResource(Res.string.feature_settings_environment),
                                style = KptTheme.typography.bodyMedium,
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
                        text = stringResource(Res.string.feature_settings_or),
                        modifier = Modifier
                            .padding(horizontal = KptTheme.spacing.sm),
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
                    style = KptTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = KptTheme.spacing.md),
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
                    shape = KptTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KptTheme.colorScheme.primary,
                        unfocusedBorderColor = KptTheme.colorScheme.outlineVariant,
                        focusedContainerColor = KptTheme.colorScheme.surface,
                        unfocusedContainerColor = KptTheme.colorScheme.surface,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = KptTheme.spacing.md),
                    label = "",
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = KptTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_hostname),
                    style = KptTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = KptTheme.spacing.md),
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
                    shape = KptTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KptTheme.colorScheme.primary,
                        unfocusedBorderColor = KptTheme.colorScheme.outlineVariant,
                        focusedContainerColor = KptTheme.colorScheme.surface,
                        unfocusedContainerColor = KptTheme.colorScheme.surface,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = KptTheme.spacing.md),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = KptTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_api_path),
                    style = KptTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = KptTheme.spacing.md),
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
                    shape = KptTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KptTheme.colorScheme.primary,
                        unfocusedBorderColor = KptTheme.colorScheme.outlineVariant,
                        focusedContainerColor = KptTheme.colorScheme.surface,
                        unfocusedContainerColor = KptTheme.colorScheme.surface,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = DesignToken.padding.large),
                    label = "",
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = KptTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_port),
                    style = KptTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = KptTheme.spacing.md),
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
                    shape = KptTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KptTheme.colorScheme.primary,
                        unfocusedBorderColor = KptTheme.colorScheme.outlineVariant,
                        focusedContainerColor = KptTheme.colorScheme.surface,
                        unfocusedContainerColor = KptTheme.colorScheme.surface,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = KptTheme.spacing.md),
                    label = "",
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = KptTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_tenant),
                    style = KptTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = KptTheme.spacing.md),
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
                    shape = KptTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KptTheme.colorScheme.primary,
                        unfocusedBorderColor = KptTheme.colorScheme.outlineVariant,
                        focusedContainerColor = KptTheme.colorScheme.surface,
                        unfocusedContainerColor = KptTheme.colorScheme.surface,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = KptTheme.spacing.md),
                    label = "",
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = KptTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DesignToken.sizes.profile)
                        .padding(horizontal = KptTheme.spacing.md)
                        .clip(KptTheme.shapes.large)
                        .background(AppColors.customYellow.copy(alpha = 0.1f)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = KptTheme.spacing.sm),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            "⚠\uFE0F",
                            modifier = Modifier.padding(horizontal = KptTheme.spacing.sm),
                        )
                        Text(
                            text = stringResource(Res.string.feature_settings_note_text),
                            style = KptTheme.typography.titleSmall,
                            color = AppColors.customYellow,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = FontFamily.SansSerif,
                        )
                    }
                }
                Spacer(Modifier.height(KptTheme.spacing.sm))
            }

            item {
                ElevatedButton(
                    onClick = {
                        onEvent(UpdateServerConfigEvent.UpdateServerConfig)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DesignToken.sizes.buttonHeight)
                        .padding(horizontal = KptTheme.spacing.md),
                    enabled = !hasAnyError,
                    shape = KptTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KptTheme.colorScheme.primary,
                        disabledContainerColor = KptTheme.colorScheme.surface,
                        disabledContentColor = KptTheme.colorScheme.primary,
                        contentColor = KptTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(
                        "  " +
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
private fun RestartCountdownDialog(
    durationSeconds: Int = 5,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var countdown by rememberSaveable { mutableStateOf(durationSeconds) }

    val progress = countdown.toFloat() / durationSeconds.toFloat()

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000L)
            countdown--
        } else {
            onDismiss()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KptTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = DesignToken.elevation.appBar),
    ) {
        Column(
            modifier = Modifier.padding(DesignToken.padding.extraLargeIncreased),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(DesignToken.sizes.buttonHeight)
                    .background(AppColors.customEnable.copy(alpha = 0.1f), DesignToken.shapes.circle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = MifosIcons.Check,
                    contentDescription = null,
                    tint = AppColors.customEnable,
                    modifier = Modifier.size(DesignToken.sizes.avatarSmall),
                )
            }

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            Text(
                text = stringResource(Res.string.feature_settings_configuration_saved),
                style = KptTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
            Text(
                text = stringResource(Res.string.feature_settings_restart_application),
                style = KptTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.largeMediumIncreased))

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(DesignToken.sizes.topAppBarHeight),
                    color = KptTheme.colorScheme.primary,
                    trackColor = KptTheme.colorScheme.primaryContainer,
                    strokeWidth = KptTheme.spacing.sm,
                )
                Text(
                    text = "$countdown",
                    style = KptTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = KptTheme.colorScheme.primary,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.spacing.extraLargeIncreased))

            TextButton(
                onClick = onDismiss,
                shape = DesignToken.shapes.circle,
                modifier = Modifier.fillMaxWidth().height(DesignToken.sizes.buttonHeight),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = KptTheme.colorScheme.primary,
                ),
            ) {
                Text(stringResource(Res.string.feature_settings_restart_now))
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
            onBackClick = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
