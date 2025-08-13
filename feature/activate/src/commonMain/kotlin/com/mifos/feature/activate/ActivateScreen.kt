/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.activate

import androidclient.feature.activate.generated.resources.Res
import androidclient.feature.activate.generated.resources.feature_activate
import androidclient.feature.activate.generated.resources.feature_activate_activation_date
import androidclient.feature.activate.generated.resources.feature_activate_cancel
import androidclient.feature.activate.generated.resources.feature_activate_client
import androidclient.feature.activate.generated.resources.feature_activate_failed_to_activate_client
import androidclient.feature.activate.generated.resources.feature_activate_select
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.common.utils.formatDate
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.model.objects.clients.ActivatePayload
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.util.DevicePreview
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ActivateScreen(
    onBackPressed: () -> Unit,
    viewModel: ActivateViewModel = koinViewModel(),
) {
    val state by viewModel.activateUiState.collectAsStateWithLifecycle()
    val id by viewModel.id.collectAsStateWithLifecycle()
    val activateType by viewModel.activateType.collectAsStateWithLifecycle()

    ActivateScreen(
        state = state,
        onActivate = {
            val clientIdAsInt: Int = try {
                id.toInt()
            } catch (e: Exception) {
                0
            }
            when (activateType) {
                Constants.ACTIVATE_CLIENT -> viewModel.activateClient(
                    clientId = clientIdAsInt,
                    clientPayload = it,
                )

                Constants.ACTIVATE_CENTER -> viewModel.activateCenter(
                    centerId = clientIdAsInt,
                    centerPayload = it,
                )

                Constants.ACTIVATE_GROUP -> viewModel.activateGroup(
                    groupId = clientIdAsInt,
                    groupPayload = it,
                )

                else -> {}
            }
        },
        onBackPressed = onBackPressed,
    )
}

@Composable
internal fun ActivateScreen(
    state: ActivateUiState,
    onActivate: (ActivatePayload) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        title = stringResource(Res.string.feature_activate),
        onBackPressed = onBackPressed,
    ) { paddingValues ->
        Column(
            modifier = modifier.padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            when (state) {
                is ActivateUiState.ActivatedSuccessfully -> {
                    MifosAlertDialog(
                        dialogTitle = "Success",
                        dialogText = stringResource(state.message),
                        onConfirmation = onBackPressed,
                        onDismissRequest = onBackPressed,
                    )
                }

                is ActivateUiState.Error -> MifosSweetError(message = stringResource(state.message)) {}

                is ActivateUiState.Loading -> MifosCircularProgress()

                is ActivateUiState.Initial -> ActivateContent(onActivate = onActivate)
            }
        }
    }
}

@Composable
private fun ActivateContent(
    onActivate: (ActivatePayload) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        var showDatePicker by rememberSaveable { mutableStateOf(false) }
        var activateDate by rememberSaveable { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = activateDate,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
                }
            },
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            datePickerState.selectedDateMillis?.let {
                                activateDate = it
                            }
                        },
                    ) { Text(stringResource(Res.string.feature_activate_select)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                        },
                    ) { Text(stringResource(Res.string.feature_activate_cancel)) }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }

        MifosDatePickerTextField(
            value = DateHelper.getDateAsStringFromLong(activateDate),
            label = stringResource(Res.string.feature_activate_activation_date),
            openDatePicker = {
                showDatePicker = true
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosButton(
            onClick = {
                onActivate(
                    ActivatePayload(
                        activationDate = formatDate(activateDate),
                        dateFormat = Constants.DATE_FORMAT_LONG,
                        locale = Constants.LOCALE_EN,
                    ),
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(44.dp)
                .padding(start = 16.dp, end = 16.dp),
            contentPadding = PaddingValues(),
        ) {
            Text(
                text = stringResource(Res.string.feature_activate),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@DevicePreview
@Composable
private fun ActivateScreenPreviewInitial() {
    ActivateScreen(
        state = ActivateUiState.Initial,
        onActivate = {},
        onBackPressed = {},
    )
}

@DevicePreview
@Composable
private fun ActivateScreenPreviewLoading() {
    ActivateScreen(
        state = ActivateUiState.Loading,
        onActivate = {},
        onBackPressed = {},
    )
}

@DevicePreview
@Composable
private fun ActivateScreenPreviewActivatedSuccessfully() {
    ActivateScreen(
        state = ActivateUiState.ActivatedSuccessfully(Res.string.feature_activate_client),
        onActivate = {},
        onBackPressed = {},
    )
}

@DevicePreview
@Composable
private fun ActivateScreenPreviewError() {
    ActivateScreen(
        state = ActivateUiState.Error(Res.string.feature_activate_failed_to_activate_client),
        onActivate = {},
        onBackPressed = {},
    )
}
