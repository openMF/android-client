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

import kpt.feature.settings.generated.resources.Res
import kpt.feature.settings.generated.resources.feature_settings_api_path_placeholder
import kpt.feature.settings.generated.resources.feature_settings_configuration_saved
import kpt.feature.settings.generated.resources.feature_settings_demo
import kpt.feature.settings.generated.resources.feature_settings_development
import kpt.feature.settings.generated.resources.feature_settings_environment
import kpt.feature.settings.generated.resources.feature_settings_hostname_placeholder
import kpt.feature.settings.generated.resources.feature_settings_label_api_path
import kpt.feature.settings.generated.resources.feature_settings_label_hostname
import kpt.feature.settings.generated.resources.feature_settings_label_port
import kpt.feature.settings.generated.resources.feature_settings_label_protocol
import kpt.feature.settings.generated.resources.feature_settings_label_tenant
import kpt.feature.settings.generated.resources.feature_settings_local
import kpt.feature.settings.generated.resources.feature_settings_note_text
import kpt.feature.settings.generated.resources.feature_settings_or
import kpt.feature.settings.generated.resources.feature_settings_protocol_placeholder
import kpt.feature.settings.generated.resources.feature_settings_quick_setup
import kpt.feature.settings.generated.resources.feature_settings_restart_application
import kpt.feature.settings.generated.resources.feature_settings_restart_now
import kpt.feature.settings.generated.resources.feature_settings_title
import kpt.feature.settings.generated.resources.feature_settings_update_config_btn_text
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
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.LocalKptColors
import kpt.core.base.designsystem.theme.LocalKptTypography
import kpt.core.base.designsystem.theme.LocalKptShapes
import kpt.core.base.designsystem.theme.LocalKptSpacing

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
                    .padding(LocalKptSpacing.current.lg),
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
                .background(LocalKptColors.current.surfaceVariant.copy(alpha = .1f)),
            contentPadding = PaddingValues(LocalKptSpacing.current.sm),
            verticalArrangement = Arrangement.spacedBy(LocalKptSpacing.current.sm),
            state = lazyListState,
        ) {
            item {
                Text(
                    text = stringResource(Res.string.feature_settings_quick_setup),
                    modifier = Modifier.padding(horizontal = LocalKptSpacing.current.md, vertical = LocalKptSpacing.current.xs),
                    fontWeight = FontWeight.SemiBold,
                    style = LocalKptTypography.current.titleMedium,
                    fontFamily = FontFamily.SansSerif,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = LocalKptSpacing.current.md, vertical = DesignToken.padding.medium),
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
                            containerColor = LocalKptColors.current.primary,
                            disabledContainerColor = LocalKptColors.current.surface,
                            disabledContentColor = LocalKptColors.current.primary,
                            contentColor = LocalKptColors.current.onPrimary,
                        ),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = stringResource(Res.string.feature_settings_local),
                                style = LocalKptTypography.current.bodyMedium,
                                fontFamily = FontFamily.SansSerif,
                            )
                            Text(
                                text = stringResource(Res.string.feature_settings_development),
                                style = LocalKptTypography.current.bodyMedium,
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
                            containerColor = LocalKptColors.current.primary,
                            disabledContainerColor = LocalKptColors.current.surface,
                            disabledContentColor = LocalKptColors.current.primary,
                            contentColor = LocalKptColors.current.onPrimary,
                        ),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = stringResource(Res.string.feature_settings_demo),
                                style = LocalKptTypography.current.bodyMedium,
                                fontFamily = FontFamily.SansSerif,
                            )
                            Text(
                                text = stringResource(Res.string.feature_settings_environment),
                                style = LocalKptTypography.current.bodyMedium,
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
                            .padding(horizontal = LocalKptSpacing.current.sm),
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
                    style = LocalKptTypography.current.labelMedium,
                    modifier = Modifier.padding(start = LocalKptSpacing.current.md),
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
                    shape = LocalKptShapes.current.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LocalKptColors.current.primary,
                        unfocusedBorderColor = LocalKptColors.current.outlineVariant,
                        focusedContainerColor = LocalKptColors.current.surface,
                        unfocusedContainerColor = LocalKptColors.current.surface,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = LocalKptSpacing.current.md),
                    label = "",
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = LocalKptTypography.current.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_hostname),
                    style = LocalKptTypography.current.labelMedium,
                    modifier = Modifier.padding(start = LocalKptSpacing.current.md),
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
                    shape = LocalKptShapes.current.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LocalKptColors.current.primary,
                        unfocusedBorderColor = LocalKptColors.current.outlineVariant,
                        focusedContainerColor = LocalKptColors.current.surface,
                        unfocusedContainerColor = LocalKptColors.current.surface,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = LocalKptSpacing.current.md),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = LocalKptTypography.current.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_api_path),
                    style = LocalKptTypography.current.labelMedium,
                    modifier = Modifier.padding(start = LocalKptSpacing.current.md),
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
                    shape = LocalKptShapes.current.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LocalKptColors.current.primary,
                        unfocusedBorderColor = LocalKptColors.current.outlineVariant,
                        focusedContainerColor = LocalKptColors.current.surface,
                        unfocusedContainerColor = LocalKptColors.current.surface,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = DesignToken.padding.large),
                    label = "",
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = LocalKptTypography.current.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_port),
                    style = LocalKptTypography.current.labelMedium,
                    modifier = Modifier.padding(start = LocalKptSpacing.current.md),
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
                    shape = LocalKptShapes.current.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LocalKptColors.current.primary,
                        unfocusedBorderColor = LocalKptColors.current.outlineVariant,
                        focusedContainerColor = LocalKptColors.current.surface,
                        unfocusedContainerColor = LocalKptColors.current.surface,
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = LocalKptSpacing.current.md),
                    label = "",
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = LocalKptTypography.current.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }

            item {
                Text(
                    stringResource(Res.string.feature_settings_label_tenant),
                    style = LocalKptTypography.current.labelMedium,
                    modifier = Modifier.padding(start = LocalKptSpacing.current.md),
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
                    shape = LocalKptShapes.current.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LocalKptColors.current.primary,
                        unfocusedBorderColor = LocalKptColors.current.outlineVariant,
                        focusedContainerColor = LocalKptColors.current.surface,
                        unfocusedContainerColor = LocalKptColors.current.surface,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = LocalKptSpacing.current.md),
                    label = "",
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = LocalKptTypography.current.bodyLarge.fontSize,
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
                        .padding(horizontal = LocalKptSpacing.current.md)
                        .clip(LocalKptShapes.current.large)
                        .background(AppColors.customYellow.copy(alpha = 0.1f)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = LocalKptSpacing.current.sm),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            "⚠\uFE0F",
                            modifier = Modifier.padding(horizontal = LocalKptSpacing.current.sm),
                        )
                        Text(
                            text = stringResource(Res.string.feature_settings_note_text),
                            style = LocalKptTypography.current.titleSmall,
                            color = AppColors.customYellow,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = FontFamily.SansSerif,
                        )
                    }
                }
                Spacer(Modifier.height(LocalKptSpacing.current.sm))
            }

            item {
                ElevatedButton(
                    onClick = {
                        onEvent(UpdateServerConfigEvent.UpdateServerConfig)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DesignToken.sizes.buttonHeight)
                        .padding(horizontal = LocalKptSpacing.current.md),
                    enabled = !hasAnyError,
                    shape = LocalKptShapes.current.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LocalKptColors.current.primary,
                        disabledContainerColor = LocalKptColors.current.surface,
                        disabledContentColor = LocalKptColors.current.primary,
                        contentColor = LocalKptColors.current.onPrimary,
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
        shape = LocalKptShapes.current.extraLarge,
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

            Spacer(modifier = Modifier.height(LocalKptSpacing.current.md))

            Text(
                text = stringResource(Res.string.feature_settings_configuration_saved),
                style = LocalKptTypography.current.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(LocalKptSpacing.current.sm))
            Text(
                text = stringResource(Res.string.feature_settings_restart_application),
                style = LocalKptTypography.current.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.largeMediumIncreased))

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(DesignToken.sizes.topAppBarHeight),
                    color = LocalKptColors.current.primary,
                    trackColor = LocalKptColors.current.primaryContainer,
                    strokeWidth = LocalKptSpacing.current.sm,
                )
                Text(
                    text = "$countdown",
                    style = LocalKptTypography.current.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = LocalKptColors.current.primary,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.spacing.extraLargeIncreased))

            TextButton(
                onClick = onDismiss,
                shape = DesignToken.shapes.circle,
                modifier = Modifier.fillMaxWidth().height(DesignToken.sizes.buttonHeight),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = LocalKptColors.current.primary,
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
