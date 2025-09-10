/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientTransfer

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.btn_submit
import androidclient.feature.client.generated.resources.client_transfer_add_note_here
import androidclient.feature.client.generated.resources.client_transfer_add_notes
import androidclient.feature.client.generated.resources.client_transfer_choose_office
import androidclient.feature.client.generated.resources.client_transfer_expected_date
import androidclient.feature.client.generated.resources.client_transfer_failure_title
import androidclient.feature.client.generated.resources.client_transfer_success_message
import androidclient.feature.client.generated.resources.client_transfer_success_title
import androidclient.feature.client.generated.resources.client_transfer_title
import androidclient.feature.client.generated.resources.dialog_continue
import androidclient.feature.client.generated.resources.feature_client_charge_cancel
import androidclient.feature.client.generated.resources.feature_client_charge_select
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
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
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun ClientTransferScreen(
    onNavigateBack: () -> Unit,
    navController: NavController,
    onNavigateNext: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientTransferViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientTransferEvent.NavigateBack -> onNavigateBack()
            ClientTransferEvent.NavigateNext -> onNavigateNext(state.id)
        }
    }

    ClientTransferScaffold(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        modifier = modifier,
        navController = navController,
    )
    ClientTransferDialogs(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun ClientTransferScaffold(
    state: ClientTransferState,
    onAction: (ClientTransferAction) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        title = stringResource(Res.string.client_transfer_title),
        onBackPressed = { onAction(ClientTransferAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.date,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
                }
            },
        )

        if (state.dialogState != ClientTransferState.DialogState.Loading &&
            state.dialogState !is ClientTransferState.DialogState.ShowStatusDialog
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                MifosBreadcrumbNavBar(navController)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = DesignToken.padding.large),
                ) {
                    if (state.offices.isNotEmpty()) {
                        Text(
                            text = stringResource(Res.string.client_transfer_title),
                            style = MifosTypography.labelLargeEmphasized,
                        )
                        Spacer(Modifier.height(DesignToken.padding.largeIncreased))

                        MifosTextFieldDropdown(
                            value = state.offices[state.currentSelectedIndex].name ?: "",
                            onValueChanged = {},
                            onOptionSelected = { index, value ->
                                onAction(ClientTransferAction.OptionChanged(index))
                            },
                            options = state.offices.map { it.name ?: "" },
                            label = stringResource(Res.string.client_transfer_choose_office),
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(Modifier.height(DesignToken.padding.largeIncreased))
                        MifosDatePickerTextField(
                            value = DateHelper.getDateAsStringFromLong(state.date),
                            modifier = Modifier.fillMaxWidth(),
                            label = stringResource(Res.string.client_transfer_expected_date),
                            openDatePicker = {
                                onAction(ClientTransferAction.UpdateDatePicker(true))
                            },
                        )
                        if (state.showDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = {
                                    onAction(ClientTransferAction.UpdateDatePicker(false))
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            onAction(ClientTransferAction.UpdateDatePicker(false))
                                            datePickerState.selectedDateMillis?.let {
                                                onAction(ClientTransferAction.UpdateDate(it))
                                            }
                                        },
                                    ) { Text(stringResource(Res.string.feature_client_charge_select)) }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            onAction(ClientTransferAction.UpdateDatePicker(false))
                                        },
                                    ) { Text(stringResource(Res.string.feature_client_charge_cancel)) }
                                },
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }

                        Spacer(Modifier.height(DesignToken.padding.largeIncreased))
                        MifosOutlinedTextField(
                            value = state.note,
                            onValueChange = {
                                onAction(ClientTransferAction.NoteChanged(it))
                            },
                            label = stringResource(Res.string.client_transfer_add_notes),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = stringResource(Res.string.client_transfer_add_note_here),
                        )

                        Spacer(Modifier.height(DesignToken.padding.largeIncreased))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            MifosOutlinedButton(
                                onClick = {
                                    onAction(ClientTransferAction.NavigateBack)
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
                                    onAction(ClientTransferAction.OnSubmit)
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
                                enabled = state.isEnabled,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientTransferDialogs(
    state: ClientTransferState,
    onAction: (ClientTransferAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientTransferState.DialogState.Loading -> MifosProgressIndicator()
        is ClientTransferState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = { onAction(ClientTransferAction.OnRetry) },
            )
        }
        is ClientTransferState.DialogState.ShowStatusDialog -> {
            MifosStatusDialog(
                status = state.dialogState.status,
                btnText = stringResource(Res.string.dialog_continue),
                onConfirm = {
                    when (state.dialogState.status) {
                        ResultStatus.SUCCESS -> {
                            onAction(ClientTransferAction.OnNext)
                        }
                        ResultStatus.FAILURE -> {
                            onAction(ClientTransferAction.DismissDialogAndClearAll)
                        }
                    }
                },
                successTitle = stringResource(Res.string.client_transfer_success_title),
                successMessage = stringResource(Res.string.client_transfer_success_message),
                failureTitle = stringResource(Res.string.client_transfer_failure_title),
                failureMessage = state.dialogState.msg,
                modifier = Modifier.fillMaxSize(),
            )
        }
        null -> Unit
    }
}
