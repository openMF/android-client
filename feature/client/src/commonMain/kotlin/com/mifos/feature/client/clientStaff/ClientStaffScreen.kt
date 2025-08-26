/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientStaff

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.btn_submit
import androidclient.feature.client.generated.resources.dialog_continue
import androidclient.feature.client.generated.resources.label_assign_staff
import androidclient.feature.client.generated.resources.label_choose_staff
import androidclient.feature.client.generated.resources.msg_cannot_assign_staff
import androidclient.feature.client.generated.resources.staff_assign_failure_title
import androidclient.feature.client.generated.resources.staff_assign_success_message
import androidclient.feature.client.generated.resources.staff_assign_success_title
import androidclient.feature.client.generated.resources.title_client_assign_staff
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
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientStaffScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientStaffViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientStaffEvent.NavigateBack -> onNavigateBack()
            ClientStaffEvent.NavigateNext -> onNavigateNext(state.id)
        }
    }

    ClientStaffScaffold(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        modifier = modifier,
    )
}

@Composable
private fun ClientStaffScaffold(
    state: ClientStaffState,
    onAction: (ClientStaffAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        title = stringResource(Res.string.title_client_assign_staff),
        onBackPressed = { onAction(ClientStaffAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        ClientStaffDialogs(
            state = state,
            onAction = onAction,
        )
        if (state.dialogState == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(
                        DesignToken.padding.large,
                    ),
            ) {
                if (state.staffOptions.isNotEmpty()) {
                    Text(
                        text = stringResource(Res.string.label_assign_staff),
                        style = MifosTypography.labelLargeEmphasized,
                    )
                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))
                    MifosTextFieldDropdown(
                        value = state.staffOptions[state.currentSelectedIndex].displayName,
                        onValueChanged = {},
                        onOptionSelected = { index, value ->
                            onAction(ClientStaffAction.OptionChanged(index))
                        },
                        options = state.staffOptions.map { it.displayName },
                        label = stringResource(Res.string.label_choose_staff),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        MifosOutlinedButton(
                            onClick = {
                                onAction(ClientStaffAction.NavigateBack)
                            },
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
                            onClick = {
                                onAction(ClientStaffAction.OnSubmit)
                            },
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
                    Text(stringResource(Res.string.msg_cannot_assign_staff))
                }
            }
        }
    }
}

@Composable
private fun ClientStaffDialogs(
    state: ClientStaffState,
    onAction: (ClientStaffAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientStaffState.DialogState.Loading -> MifosProgressIndicator()

        is ClientStaffState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = {
                    onAction(ClientStaffAction.OnRetry)
                },
            )
        }

        null -> Unit
        is ClientStaffState.DialogState.ShowStatusDialog -> {
            MifosStatusDialog(
                status = state.dialogState.status,
                btnText = stringResource(Res.string.dialog_continue),
                onConfirm = {
                    onAction(ClientStaffAction.OnNext)
                },
                successTitle = stringResource(Res.string.staff_assign_success_title),
                successMessage = stringResource(Res.string.staff_assign_success_message),
                failureTitle = stringResource(Res.string.staff_assign_failure_title),
                failureMessage = state.dialogState.msg,
            )
        }
    }
}
