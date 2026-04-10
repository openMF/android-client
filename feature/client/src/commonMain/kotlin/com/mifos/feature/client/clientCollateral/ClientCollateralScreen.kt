/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

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

    ClientCollateralContent(
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
private fun ClientCollateralContent(
    navController: NavController,
    state: ClientCollateralState,
    onAction: (ClientCollateralAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.dialogState !is ClientCollateralState.DialogState.ShowStatusDialog) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = KptTheme.spacing.md),
        ) {
            MifosBreadcrumbNavBar(
                navController = navController,
            )

            Text(
                text = stringResource(Res.string.client_collateral_title),
                style = MifosTypography.labelLargeEmphasized,
                modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.medium))

            if (state.collaterals.isNotEmpty()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = KptTheme.spacing.md),
                ) {
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
                    )

                    MifosOutlinedTextField(
                        value = state.collaterals[state.currentSelectedIndex].name,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = stringResource(Res.string.client_collateral_name),
                    )
                    MifosOutlinedTextField(
                        value = state.collaterals[state.currentSelectedIndex].quality,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = stringResource(Res.string.client_collateral_quality),
                    )
                    MifosOutlinedTextField(
                        value = state.collaterals[state.currentSelectedIndex].unitType,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = stringResource(Res.string.client_collateral_unit_type),
                    )
                    MifosOutlinedTextField(
                        value = "${state.collaterals[state.currentSelectedIndex].basePrice}",
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = stringResource(Res.string.client_collateral_base_price),
                    )
                    MifosOutlinedTextField(
                        value = state.collaterals[state.currentSelectedIndex].pctToBase.toString(),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = stringResource(Res.string.client_collateral_pct_to_base),
                    )

                    MifosOutlinedTextField(
                        value = state.quantity,
                        onValueChange = {
                            onAction(
                                ClientCollateralAction.OnQuantityChange(
                                    it,
                                ),
                            )
                        },
                        isError = state.quantityError != null,
                        errorText = state.quantityError?.let { stringResource(it) },
                        label = stringResource(Res.string.client_collateral_quantity),
                        keyboardType = KeyboardType.Number,
                    )

                    MifosOutlinedTextField(
                        value = state.total.toString(),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = stringResource(Res.string.client_collateral_total),
                    )

                    MifosOutlinedTextField(
                        value = state.totalCollateral.toString(),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = stringResource(Res.string.client_collateral_total_collateral),
                    )
                }

                MifosTwoButtonRow(
                    firstBtnText = stringResource(Res.string.btn_back),
                    secondBtnText = stringResource(Res.string.btn_submit),
                    onFirstBtnClick = { onAction(ClientCollateralAction.NavigateBack) },
                    onSecondBtnClick = { onAction(ClientCollateralAction.OnSave) },
                    modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
                )
            } else {
                Text(stringResource(Res.string.client_collateral_no_options))
            }
        }
    }

    if (state.isOverlayLoading) {
        MifosProgressIndicatorOverlay()
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
