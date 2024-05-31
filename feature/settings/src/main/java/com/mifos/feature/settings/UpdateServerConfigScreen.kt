package com.mifos.feature.settings

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.model.ServerConfig
import com.mifos.core.ui.util.DevicePreviews

@Composable
fun UpdateServerConfigScreenRoute(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateServerConfigViewModel = hiltViewModel(),
) {
    val protocolError by viewModel.protocolError.collectAsStateWithLifecycle()
    val apiPathError by viewModel.apiPathError.collectAsStateWithLifecycle()
    val endPointError by viewModel.endPointError.collectAsStateWithLifecycle()
    val portError by viewModel.portError.collectAsStateWithLifecycle()
    val tenantError by viewModel.tenantError.collectAsStateWithLifecycle()

    UpdateServerConfigScreenContent(
        modifier = modifier,
        serverConfig = viewModel.state.value,
        protocolError = protocolError,
        apiPathError = apiPathError,
        endPointError = endPointError,
        portError = portError,
        tenantError = tenantError,
        onEvent = viewModel::onEvent,
        onCloseClick = onCloseClick
    )
}

@VisibleForTesting
@Composable
internal fun UpdateServerConfigScreenContent(
    modifier: Modifier = Modifier,
    serverConfig: ServerConfig,
    protocolError: Int? = null,
    apiPathError: Int? = null,
    endPointError: Int? = null,
    portError: Int? = null,
    tenantError: Int? = null,
    onEvent: (UpdateServerConfigEvent) -> Unit,
    onCloseClick: () -> Unit,
) {
    val hasAnyError = listOf(
        protocolError,
        apiPathError,
        endPointError,
        portError,
        tenantError
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Update Server Configuration",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    OutlinedIconButton(
                        onClick = onCloseClick,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close BottomSheet"
                        )
                    }
                }
            }

            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                item {
                    MifosOutlinedTextField(
                        value = serverConfig.protocol,
                        onValueChange = {
                            onEvent(UpdateServerConfigEvent.UpdateProtocol(it))
                        },
                        label = stringResource(R.string.feature_settings_label_protocol),
                        leadingIcon = Icons.Default.AddLink,
                        placeholder = stringResource(R.string.feature_settings_protocol_placeholder),
                        isError = protocolError != null,
                        errorText = protocolError?.let { stringResource(it) },
                        keyboardType = KeyboardType.Uri
                    )
                }

                item {
                    MifosOutlinedTextField(
                        value = serverConfig.endPoint,
                        onValueChange = {
                            onEvent(UpdateServerConfigEvent.UpdateEndPoint(it))
                        },
                        label = stringResource(R.string.feature_settings_label_endpoint),
                        leadingIcon = Icons.Default.Link,
                        placeholder = stringResource(R.string.feature_settings_endpoint_placeholder),
                        isError = endPointError != null,
                        errorText = endPointError?.let { stringResource(it) }
                    )
                }

                item {
                    MifosOutlinedTextField(
                        value = serverConfig.apiPath,
                        onValueChange = {
                            onEvent(UpdateServerConfigEvent.UpdateApiPath(it))
                        },
                        label = stringResource(R.string.feature_settings_label_api_path),
                        leadingIcon = Icons.Default.Link,
                        placeholder = stringResource(R.string.feature_settings_api_path_placeholder),
                        isError = apiPathError != null,
                        errorText = apiPathError?.let { stringResource(it) }
                    )
                }

                item {
                    MifosOutlinedTextField(
                        value = serverConfig.port,
                        onValueChange = {
                            onEvent(UpdateServerConfigEvent.UpdatePort(it))
                        },
                        label = stringResource(R.string.feature_settings_label_port),
                        leadingIcon = Icons.Default.Link,
                        placeholder = stringResource(R.string.feature_settings_port_placeholder),
                        isError = portError != null,
                        errorText = portError?.let { stringResource(it) },
                        keyboardType = KeyboardType.Number
                    )
                }

                item {

                    MifosOutlinedTextField(
                        value = serverConfig.tenant,
                        onValueChange = {
                            onEvent(UpdateServerConfigEvent.UpdateTenant(it))
                        },
                        label = stringResource(R.string.feature_settings_label_tenant),
                        leadingIcon = Icons.Default.Link,
                        placeholder = stringResource(R.string.feature_settings_tenant_placeholder),
                        isError = tenantError != null,
                        errorText = tenantError?.let { stringResource(it) }
                    )
                }

                item {
                    FilledTonalButton(
                        onClick = {
                            onEvent(UpdateServerConfigEvent.DeleteServerConfig)
                            onCloseClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(bottom = 4.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "deleteConfig"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DELETE CONFIG")
                    }
                }

                item {
                    ElevatedButton(
                        onClick = {
                            onEvent(UpdateServerConfigEvent.UpdateServerConfig)
                            onCloseClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = !hasAnyError,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = BluePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "updateConfig"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("UPDATE CONFIG")
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun UpdateServerConfigScreenInvalidData(
    modifier: Modifier = Modifier
) {
    MaterialTheme {
        UpdateServerConfigScreenContent(
            serverConfig = ServerConfig(
                protocol = "",
                endPoint = "",
                apiPath = "",
                port = "",
                tenant = ""
            ),
            onEvent = {},
            onCloseClick = {}
        )
    }
}