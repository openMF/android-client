/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientUpdateDefaultAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.btn_submit
import androidclient.feature.client.generated.resources.dialog_continue
import androidclient.feature.client.generated.resources.update_default_account_choose
import androidclient.feature.client.generated.resources.update_default_account_failure_title
import androidclient.feature.client.generated.resources.update_default_account_success_message
import androidclient.feature.client.generated.resources.update_default_account_success_title
import androidclient.feature.client.generated.resources.update_default_account_title
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextButton
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.text.get

@Composable
internal fun UpdateDefaultAccountScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateDefaultAccountViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            UpdateDefaultAccountEvent.NavigateBack -> onNavigateBack()
            UpdateDefaultAccountEvent.NavigateNext -> onNavigateNext(state.id)
        }
    }

    UpdateDefaultAccountScaffold(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        modifier = modifier,
    )
    UpdateDefaultAccountDialogs(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
private fun UpdateDefaultAccountScaffold(
    state: UpdateDefaultAccountState,
    onAction: (UpdateDefaultAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        title = stringResource(Res.string.update_default_account_title),
        onBackPressed = { onAction(UpdateDefaultAccountAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->

        if (state.dialogState != UpdateDefaultAccountState.DialogState.Loading &&
            state.dialogState !is UpdateDefaultAccountState.DialogState.ShowStatusDialog
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(DesignToken.padding.large),
            ) {
                if (state.accounts.isNotEmpty()) {
                    Text(
                        text = stringResource(Res.string.update_default_account_title),
                        style = MifosTypography.labelLargeEmphasized,
                    )
                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))

                    MifosTextFieldDropdown(
                        value = state.accounts[state.currentSelectedIndex].accountNo,
                        onValueChanged = {},
                        onOptionSelected = { index, _ ->
                            onAction(UpdateDefaultAccountAction.OptionChanged(index))
                        },
                        options = state.accounts.map { it.accountNo },
                        label = stringResource(Res.string.update_default_account_choose),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        MifosOutlinedButton(
                            onClick = { onAction(UpdateDefaultAccountAction.NavigateBack) },
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
                            onClick = { onAction(UpdateDefaultAccountAction.OnSave) },
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
                        )
                    }
                } else {
                    Text("No Savings Accounts found for this client")
                }
            }
        }
    }
}

@Composable
private fun UpdateDefaultAccountDialogs(
    state: UpdateDefaultAccountState,
    onAction: (UpdateDefaultAccountAction) -> Unit,
) {
    when (state.dialogState) {
        is UpdateDefaultAccountState.DialogState.Loading -> MifosProgressIndicator()
        is UpdateDefaultAccountState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = { onAction(UpdateDefaultAccountAction.OnRetry) },
            )
        }
        is UpdateDefaultAccountState.DialogState.ShowStatusDialog -> {
            MifosStatusDialog(
                status = state.dialogState.status,
                btnText = stringResource(Res.string.dialog_continue),
                onConfirm = {
                    when (state.dialogState.status) {
                        ResultStatus.SUCCESS -> {
                            onAction(UpdateDefaultAccountAction.OnNext)
                        }
                        ResultStatus.FAILURE -> {
                            onAction(UpdateDefaultAccountAction.Dismiss)
                        }
                    }
                },
                successTitle = stringResource(Res.string.update_default_account_success_title),
                successMessage = stringResource(Res.string.update_default_account_success_message),
                failureTitle = stringResource(Res.string.update_default_account_failure_title),
                failureMessage = state.dialogState.msg,
                modifier = Modifier.fillMaxSize(),
            )
        }
        null -> Unit
    }
}
