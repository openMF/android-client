/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.recurringDeposit.recurringAccountApproval

import androidclient.feature.recurringdeposit.generated.resources.Res
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_approval_date
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_approval_reason
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_approved_on
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_cancel
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_continue
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_failure_message
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_failure_title
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_save
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_select_date
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_success_message
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_success_title
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosDatePickerDialog
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.model.objects.template.recurring.approval.RecurringDepositApproval
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
internal fun RecurringDepositAccountApprovalScreen(
    navController: NavController,
    navigateBack: () -> Unit,
    viewModel: RecurringDepositAccountApprovalViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            RecurringDepositAccountApprovalEvent.NavigateBack -> navigateBack()
        }
    }

    RecurringDepositAccountApprovalScreen(
        navController = navController,
        state = state,
        onAction = viewModel::trySendAction,
    )
}

@Composable
internal fun RecurringDepositAccountApprovalScreen(
    navController: NavController,
    state: RecurringDepositAccountApprovalState,
    modifier: Modifier = Modifier,
    onAction: (RecurringDepositAccountApprovalAction) -> Unit,
) {
    MifosScaffold(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        ) {
            MifosBreadcrumbNavBar(navController)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = DesignToken.padding.large),
            ) {
                RecurringDepositAccountApprovalContent(
                    state = state,
                    onAction = onAction,
                    isLoading = state.isLoading,
                )

                if (state.isLoading) {
                    MifosProgressIndicatorOverlay()
                }

                if (state.approvalResponse != null) {
                    Dialog(
                        onDismissRequest = {
                            onAction(RecurringDepositAccountApprovalAction.NavigateBack)
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                        ),
                    ) {
                        Surface(
                            shape = DesignToken.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(DesignToken.padding.large),
                        ) {
                            MifosStatusDialog(
                                status = ResultStatus.SUCCESS,
                                onConfirm = {
                                    onAction(RecurringDepositAccountApprovalAction.NavigateBack)
                                },
                                btnText = stringResource(Res.string.feature_recurring_deposit_continue),
                                successTitle = stringResource(Res.string.feature_recurring_deposit_success_title),
                                successMessage = stringResource(Res.string.feature_recurring_deposit_success_message),
                                failureTitle = stringResource(Res.string.feature_recurring_deposit_failure_title),
                                failureMessage = stringResource(Res.string.feature_recurring_deposit_failure_message),
                                showButton = true,
                            )
                        }
                    }
                }

                if (state.errorMessage != null) {
                    Dialog(
                        onDismissRequest = {
                            onAction(RecurringDepositAccountApprovalAction.NavigateBack)
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                        ),
                    ) {
                        Surface(
                            shape = DesignToken.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(DesignToken.padding.large),
                        ) {
                            MifosStatusDialog(
                                status = ResultStatus.FAILURE,
                                onConfirm = {
                                    onAction(RecurringDepositAccountApprovalAction.NavigateBack)
                                },
                                btnText = stringResource(Res.string.feature_recurring_deposit_continue),
                                successTitle = stringResource(Res.string.feature_recurring_deposit_success_title),
                                successMessage = stringResource(Res.string.feature_recurring_deposit_success_message),
                                failureTitle = stringResource(Res.string.feature_recurring_deposit_failure_title),
                                failureMessage = state.errorMessage
                                    ?: stringResource(Res.string.feature_recurring_deposit_failure_message),
                                showButton = true,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun RecurringDepositAccountApprovalContent(
    state: RecurringDepositAccountApprovalState,
    onAction: (RecurringDepositAccountApprovalAction) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val scrollState = rememberScrollState()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.approvalDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= kotlin.time.Clock.System.now().toEpochMilliseconds()
            }
        },
    )
    var showDatePickerDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(state.approvalDate) {
        if (datePickerState.selectedDateMillis != state.approvalDate) {
            datePickerState.selectedDateMillis = state.approvalDate
        }
    }

    if (showDatePickerDialog) {
        MifosDatePickerDialog(
            datePickerState = datePickerState,
            confirmText = stringResource(Res.string.feature_recurring_deposit_select_date),
            dismissText = stringResource(Res.string.feature_recurring_deposit_cancel),
            onDismissRequest = {
                showDatePickerDialog = false
            },
            onConfirm = {
                datePickerState.selectedDateMillis?.let {
                    onAction(RecurringDepositAccountApprovalAction.ApprovalDateChanged(it))
                }
                showDatePickerDialog = false
            },
        )
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(DesignToken.padding.small))

        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(Res.string.feature_recurring_deposit_approved_on),
            modifier = Modifier,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.large))

        MifosDatePickerTextField(
            value = DateHelper.getDateAsStringFromLong(state.approvalDate),
            label = stringResource(Res.string.feature_recurring_deposit_approval_date),
            openDatePicker = {
                showDatePickerDialog = true
            },
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.large))

        MifosOutlinedTextField(
            value = state.reasonForApproval,
            onValueChange = {
                onAction(RecurringDepositAccountApprovalAction.ReasonForApprovalChanged(it))
            },
            label = stringResource(Res.string.feature_recurring_deposit_approval_reason),
            error = null,
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.large))

        MifosButton(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(DesignToken.spacing.dp44),
            enabled = !isLoading,
            onClick = {
                onAction(
                    RecurringDepositAccountApprovalAction.Approve(
                        RecurringDepositApproval(
                            approvedOnDate = DateHelper.getDateAsStringForApproval(state.approvalDate),
                            note = state.reasonForApproval,
                        ),
                    ),
                )
            },
        ) {
            Text(text = stringResource(Res.string.feature_recurring_deposit_save))
        }
    }
}
