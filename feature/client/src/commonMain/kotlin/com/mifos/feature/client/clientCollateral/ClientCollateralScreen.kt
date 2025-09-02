/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientCollateral

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.btn_submit
import androidclient.feature.client.generated.resources.client_collateral_base_price
import androidclient.feature.client.generated.resources.client_collateral_choose_type
import androidclient.feature.client.generated.resources.client_collateral_failure_title
import androidclient.feature.client.generated.resources.client_collateral_name
import androidclient.feature.client.generated.resources.client_collateral_no_options
import androidclient.feature.client.generated.resources.client_collateral_pct_to_base
import androidclient.feature.client.generated.resources.client_collateral_quality
import androidclient.feature.client.generated.resources.client_collateral_quantity
import androidclient.feature.client.generated.resources.client_collateral_success_message
import androidclient.feature.client.generated.resources.client_collateral_success_title
import androidclient.feature.client.generated.resources.client_collateral_title
import androidclient.feature.client.generated.resources.client_collateral_total
import androidclient.feature.client.generated.resources.client_collateral_total_collateral
import androidclient.feature.client.generated.resources.client_collateral_unit_type
import androidclient.feature.client.generated.resources.dialog_continue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextButton
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientCollateralScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ClientCollateralViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientCollateralEvent.NavigateBack -> onNavigateBack()
            ClientCollateralEvent.NavigateNext -> onNavigateNext(state.id)
        }
    }

    ClientCollateralScaffold(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        modifier = modifier,
        navController = navController,
    )
    ClientCollateralDialogs(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
private fun ClientCollateralScaffold(
    navController: NavController,
    state: ClientCollateralState,
    onAction: (ClientCollateralAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        title = stringResource(Res.string.client_collateral_title),
        onBackPressed = { onAction(ClientCollateralAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->

        if (state.dialogState != ClientCollateralState.DialogState.Loading &&
            state.dialogState !is ClientCollateralState.DialogState.ShowStatusDialog
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                MifosBreadcrumbNavBar(navController)
                if (state.collaterals.isNotEmpty()) {
                    Column(
                        modifier
                            .fillMaxWidth()
                            .padding(horizontal = DesignToken.padding.large)
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.padding.large),
                    ) {
                        Text(
                            text = stringResource(Res.string.client_collateral_title),
                            style = MifosTypography.labelLargeEmphasized,
                        )
                        Spacer(Modifier.height(DesignToken.padding.large))

                        MifosTextFieldDropdown(
                            value = state.collaterals[state.currentSelectedIndex].name,
                            onValueChanged = {
                                onAction(ClientCollateralAction.OptionChanged(state.currentSelectedIndex))
                                onAction(ClientCollateralAction.OnQuantityChange(state.quantity))
                            },
                            onOptionSelected = { index, _ ->
                                onAction(ClientCollateralAction.OptionChanged(index))
                            },
                            options = state.collaterals.map { it.name },
                            label = stringResource(Res.string.client_collateral_choose_type),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(DesignToken.padding.large))

                        MifosOutlinedTextField(
                            value = state.collaterals[state.currentSelectedIndex].name,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = stringResource(Res.string.client_collateral_name),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        MifosOutlinedTextField(
                            value = state.collaterals[state.currentSelectedIndex].quality,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = stringResource(Res.string.client_collateral_quality),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        MifosOutlinedTextField(
                            value = state.collaterals[state.currentSelectedIndex].unitType,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = stringResource(Res.string.client_collateral_unit_type),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        MifosOutlinedTextField(
                            value = "${state.collaterals[state.currentSelectedIndex].basePrice}",
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = stringResource(Res.string.client_collateral_base_price),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        MifosOutlinedTextField(
                            value = state.collaterals[state.currentSelectedIndex].pctToBase.toString(),
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = stringResource(Res.string.client_collateral_pct_to_base),
                            modifier = Modifier.fillMaxWidth(),
                        )

                        MifosOutlinedTextField(
                            value = if (state.quantity == -1)"" else state.quantity.toString(),
                            onValueChange = {
                                onAction(ClientCollateralAction.OnQuantityChange(it.toIntOrNull() ?: -1))
                            },
                            label = stringResource(Res.string.client_collateral_quantity),
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        MifosOutlinedTextField(
                            value = state.total.toString(),
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = stringResource(Res.string.client_collateral_total),
                            modifier = Modifier.fillMaxWidth(),
                        )

                        MifosOutlinedTextField(
                            value = state.totalCollateral.toString(),
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = stringResource(Res.string.client_collateral_total_collateral),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        MifosOutlinedButton(
                            onClick = { onAction(ClientCollateralAction.NavigateBack) },
                            leadingIcon = {
                                Icon(
                                    imageVector = MifosIcons.ChevronLeft,
                                    contentDescription = null,
                                    modifier = Modifier.size(DesignToken.sizes.iconAverage),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            },
                            text = {
                                Text(
                                    text = stringResource(Res.string.btn_back),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MifosTypography.labelLarge,
                                )
                            },
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.padding(DesignToken.padding.small))
                        MifosTextButton(
                            onClick = { onAction(ClientCollateralAction.OnSave) },
                            leadingIcon = {
                                Icon(
                                    imageVector = MifosIcons.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(DesignToken.sizes.iconAverage),
                                )
                            },
                            text = {
                                Text(
                                    text = stringResource(Res.string.btn_submit),
                                    style = MifosTypography.labelLarge,
                                )
                            },
                            modifier = Modifier.weight(1f),
                            enabled = state.isEnabled,
                        )
                    }
                } else {
                    Text(stringResource(Res.string.client_collateral_no_options))
                }
            }
        }
    }
}

@Composable
private fun ClientCollateralDialogs(
    state: ClientCollateralState,
    onAction: (ClientCollateralAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientCollateralState.DialogState.Loading -> MifosProgressIndicator()
        is ClientCollateralState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = { onAction(ClientCollateralAction.OnRetry) },
            )
        }
        is ClientCollateralState.DialogState.ShowStatusDialog -> {
            MifosStatusDialog(
                status = state.dialogState.status,
                btnText = stringResource(Res.string.dialog_continue),
                onConfirm = { onAction(ClientCollateralAction.OnNext) },
                successTitle = stringResource(Res.string.client_collateral_success_title),
                successMessage = stringResource(Res.string.client_collateral_success_message),
                failureTitle = stringResource(Res.string.client_collateral_failure_title),
                failureMessage = state.dialogState.msg,
                modifier = Modifier.fillMaxSize(),
            )
        }
        null -> Unit
    }
}
