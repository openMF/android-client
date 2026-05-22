/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.activate.ui

import androidclient.feature.activate.generated.resources.Res
import androidclient.feature.activate.generated.resources.feature_activate
import androidclient.feature.activate.generated.resources.feature_activate_activation_date
import androidclient.feature.activate.generated.resources.feature_activate_cancel
import androidclient.feature.activate.generated.resources.feature_activate_dialog_title_error
import androidclient.feature.activate.generated.resources.feature_activate_dialog_title_success
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.common.utils.formatDate
import com.mifos.core.data.store.SubmitState
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.model.objects.clients.ActivatePayload
import com.mifos.core.ui.components.MifosAlertDialog
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import template.core.base.ui.submit.SubmitProgressOverlay
import template.core.base.ui.submit.SubmitResultHandler
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun ActivateScreen(
    onBackPressed: () -> Unit,
    viewModel: ActivateViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()

    // Terminal-state side effects: Submitted -> show success dialog (which dismisses to back);
    // Failed -> show error dialog (which dismisses to reset). SubmitResultHandler fires once
    // per terminal transition.
    var resultDialog by rememberSaveable { mutableStateOf<ResultDialog?>(null) }
    SubmitResultHandler(
        state = submitState,
        onSubmitted = { resultDialog = ResultDialog.Success },
        onFailed = { _, _ -> resultDialog = ResultDialog.Failure },
    )

    ActivateScreen(
        onActivate = { payload ->
            val targetId: Int = try {
                viewModel.id
            } catch (e: Exception) {
                0
            }
            val action = when (viewModel.activateType) {
                Constants.ACTIVATE_CLIENT -> ActivateAction.ActivateClient(targetId, payload)
                Constants.ACTIVATE_CENTER -> ActivateAction.ActivateCenter(targetId, payload)
                Constants.ACTIVATE_GROUP -> ActivateAction.ActivateGroup(targetId, payload)
                else -> null
            }
            action?.let { viewModel.actionChannel.trySend(it) }
        },
        onBackPressed = onBackPressed,
        isSubmitting = submitState is SubmitState.Submitting,
    )

    SubmitProgressOverlay(visible = submitState is SubmitState.Submitting)

    when (resultDialog) {
        ResultDialog.Success -> MifosAlertDialog(
            dialogTitle = stringResource(Res.string.feature_activate_dialog_title_success),
            dialogText = stringResource(state.successMessage),
            onConfirmation = {
                resultDialog = null
                viewModel.onSubmitConsumed()
                onBackPressed()
            },
            onDismissRequest = {
                resultDialog = null
                viewModel.onSubmitConsumed()
                onBackPressed()
            },
        )

        ResultDialog.Failure -> MifosAlertDialog(
            dialogTitle = stringResource(Res.string.feature_activate_dialog_title_error),
            dialogText = stringResource(state.failureMessage),
            onConfirmation = {
                resultDialog = null
                viewModel.onSubmitConsumed()
            },
            onDismissRequest = {
                resultDialog = null
                viewModel.onSubmitConsumed()
            },
        )

        null -> Unit
    }
}

private enum class ResultDialog { Success, Failure }

@Composable
internal fun ActivateScreen(
    onActivate: (ActivatePayload) -> Unit,
    onBackPressed: () -> Unit,
    isSubmitting: Boolean,
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
            ActivateContent(onActivate = onActivate, enabled = !isSubmitting)
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ActivateContent(
    onActivate: (ActivatePayload) -> Unit,
    enabled: Boolean,
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
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            datePickerState.selectedDateMillis?.let { activateDate = it }
                        },
                    ) { Text(stringResource(Res.string.feature_activate_select)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDatePicker = false },
                    ) { Text(stringResource(Res.string.feature_activate_cancel)) }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }

        MifosDatePickerTextField(
            value = DateHelper.getDateAsStringFromLong(activateDate),
            label = stringResource(Res.string.feature_activate_activation_date),
            openDatePicker = { showDatePicker = true },
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosButton(
            onClick = {
                onActivate(
                    ActivatePayload(
                        activationDate = formatDate(activateDate),
                        dateFormat = ApiDateFormatter.DATE_FORMAT,
                        locale = ApiDateFormatter.LOCALE,
                    ),
                )
            },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(DesignToken.spacing.dp44)
                .padding(horizontal = KptTheme.spacing.md),
            contentPadding = PaddingValues(),
        ) {
            Text(
                text = stringResource(Res.string.feature_activate),
                style = KptTheme.typography.bodySmall,
            )
        }
    }
}

// region Previews

private class ActivateScreenPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true) // idle, submitting
}

@Preview
@Composable
private fun ActivateScreenPreview(
    @PreviewParameter(ActivateScreenPreviewProvider::class) isSubmitting: Boolean,
) {
    ActivateScreen(
        onActivate = {},
        onBackPressed = {},
        isSubmitting = isSubmitting,
    )
}

// endregion
