/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientClosure

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.btn_submit
import androidclient.feature.client.generated.resources.client_closure_expected_date
import androidclient.feature.client.generated.resources.client_closure_failure_title
import androidclient.feature.client.generated.resources.client_closure_no_reasons_found
import androidclient.feature.client.generated.resources.client_closure_reason
import androidclient.feature.client.generated.resources.client_closure_success_message
import androidclient.feature.client.generated.resources.client_closure_success_title
import androidclient.feature.client.generated.resources.client_closure_title
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
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
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
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientClosureScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientClosureViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientClosureEvent.NavigateBack -> onNavigateBack()
            ClientClosureEvent.NavigateNext -> onNavigateNext(state.id)
        }
    }

    ClientClosureScaffold(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        modifier = modifier,
    )
    ClientClosureDialogs(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientClosureScaffold(
    state: ClientClosureState,
    onAction: (ClientClosureAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        title = stringResource(Res.string.client_closure_title),
        onBackPressed = { onAction(ClientClosureAction.NavigateBack) },
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

        if (state.dialogState != ClientClosureState.DialogState.Loading &&
            state.dialogState !is ClientClosureState.DialogState.ShowStatusDialog
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(DesignToken.padding.large),
            ) {
                if (state.reasons.isNotEmpty()) {
                    Text(
                        text = stringResource(Res.string.client_closure_title),
                        style = MifosTypography.labelLargeEmphasized,
                    )
                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))

                    MifosDatePickerTextField(
                        value = DateHelper.getDateAsStringFromLong(state.date),
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(Res.string.client_closure_expected_date),
                        openDatePicker = {
                            onAction(ClientClosureAction.UpdateDatePicker(true))
                        },
                    )
                    if (state.showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = {
                                onAction(ClientClosureAction.UpdateDatePicker(false))
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        onAction(ClientClosureAction.UpdateDatePicker(false))
                                        datePickerState.selectedDateMillis?.let {
                                            onAction(ClientClosureAction.UpdateDate(it))
                                        }
                                    },
                                ) { Text(stringResource(Res.string.feature_client_charge_select)) }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        onAction(ClientClosureAction.UpdateDatePicker(false))
                                    },
                                ) { Text(stringResource(Res.string.feature_client_charge_cancel)) }
                            },
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))

                    MifosTextFieldDropdown(
                        value = state.reasons[state.currentSelectedIndex].name,
                        onValueChanged = {},
                        onOptionSelected = { index, _ ->
                            onAction(ClientClosureAction.OptionChanged(index))
                        },
                        options = state.reasons.map {
                            it.name
                        },
                        label = stringResource(Res.string.client_closure_reason),
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                    )

                    Spacer(Modifier.height(DesignToken.padding.largeIncreased))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        MifosOutlinedButton(
                            onClick = { onAction(ClientClosureAction.NavigateBack) },
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
                            onClick = { onAction(ClientClosureAction.OnSubmit) },
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
                    Text(stringResource(Res.string.client_closure_no_reasons_found))
                }
            }
        }
    }
}

@Composable
private fun ClientClosureDialogs(
    state: ClientClosureState,
    onAction: (ClientClosureAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientClosureState.DialogState.Loading -> MifosProgressIndicator()
        is ClientClosureState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = { onAction(ClientClosureAction.OnRetry) },
            )
        }
        is ClientClosureState.DialogState.ShowStatusDialog -> {
            MifosStatusDialog(
                status = state.dialogState.status,
                btnText = stringResource(Res.string.dialog_continue),
                onConfirm = { onAction(ClientClosureAction.OnNext) },
                successTitle = stringResource(Res.string.client_closure_success_title),
                successMessage = stringResource(Res.string.client_closure_success_message),
                failureTitle = stringResource(Res.string.client_closure_failure_title),
                failureMessage = state.dialogState.msg,
                modifier = Modifier.fillMaxSize(),
            )
        }
        null -> Unit
    }
}
