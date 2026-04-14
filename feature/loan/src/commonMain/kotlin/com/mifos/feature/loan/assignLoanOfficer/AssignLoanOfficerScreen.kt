/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.assignLoanOfficer

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_assign_loan_officer_new_officer
import androidclient.feature.loan.generated.resources.feature_loan_assign_loan_officer_required
import androidclient.feature.loan.generated.resources.feature_loan_assign_loan_officer_title
import androidclient.feature.loan.generated.resources.feature_loan_assign_loan_officer_to_officer
import androidclient.feature.loan.generated.resources.feature_loan_assignment_date
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_profile_change_loan_officer
import androidclient.feature.loan.generated.resources.feature_loan_profile_current_loan_officer
import androidclient.feature.loan.generated.resources.feature_loan_profile_officer_staff_id
import androidclient.feature.loan.generated.resources.feature_loan_select_date
import androidclient.feature.loan.generated.resources.feature_loan_submit
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.model.entity.accounts.loan.StaffOption
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosTwoButtonRow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun AssignLoanOfficerScreen(
    navigateBack: () -> Unit,
    viewModel: AssignLoanOfficerViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is AssignLoanOfficerEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                AssignLoanOfficerEffect.NavigateBack -> navigateBack()
            }
        }
    }

    val contentState = uiState as? AssignLoanOfficerUiState.Content
    val hasExistingLoanOfficer = (contentState?.loan?.loanOfficerId ?: 0) > 0
    val submitInProgress = contentState?.submitInProgress == true
    MifosScaffold(
        title = if (hasExistingLoanOfficer) {
            stringResource(Res.string.feature_loan_profile_change_loan_officer)
        } else {
            stringResource(Res.string.feature_loan_assign_loan_officer_title)
        },
        onBackPressed = { if (!submitInProgress) navigateBack() },
        snackbarHostState = snackbarHostState,
    ) { padding ->
        when (val state = uiState) {
            AssignLoanOfficerUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
                    MifosProgressIndicator()
                }
            }
            is AssignLoanOfficerUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding))
            }
            is AssignLoanOfficerUiState.Content -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
                    AssignLoanOfficerForm(
                        uiState = state,
                        modifier = Modifier.fillMaxSize(),
                        onOfficerSelected = viewModel::onOfficerSelected,
                        onAssignmentDateMillis = viewModel::onAssignmentDateMillis,
                        onSubmit = { viewModel.submit() },
                        onCancel = { if (!state.submitInProgress) navigateBack() },
                    )
                    if (state.submitInProgress) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(KptTheme.colorScheme.background.copy(alpha = 0.7f)),
                        ) {
                            MifosProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun AssignLoanOfficerForm(
    uiState: AssignLoanOfficerUiState.Content,
    onOfficerSelected: (Int) -> Unit,
    onAssignmentDateMillis: (Long) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pickDate by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.assignmentDateMillis.takeIf { it > 0L }
            ?: Clock.System.now().toEpochMilliseconds(),
    )
    val officerLabels = uiState.officers.map { staffDisplayLabel(it) }
    val officerErrorText = stringResource(Res.string.feature_loan_assign_loan_officer_required)

    if (pickDate) {
        DatePickerDialog(
            onDismissRequest = { pickDate = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let(onAssignmentDateMillis)
                        pickDate = false
                    },
                ) { Text(stringResource(Res.string.feature_loan_select_date)) }
            },
            dismissButton = {
                TextButton(onClick = { pickDate = false }) {
                    Text(stringResource(Res.string.feature_loan_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val loan = uiState.loan
    val hasExistingLoanOfficer = loan != null && loan.loanOfficerId > 0

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = KptTheme.spacing.md),
    ) {
        Spacer(Modifier.height(KptTheme.spacing.md))

        if (hasExistingLoanOfficer && loan != null) {
            Text(
                text = stringResource(Res.string.feature_loan_profile_current_loan_officer),
                style = MifosTypography.labelMediumEmphasized,
            )
            Spacer(Modifier.height(KptTheme.spacing.xs))
            Text(
                text = loan.loanOfficerName.takeIf { it.isNotBlank() }
                    ?: stringResource(
                        Res.string.feature_loan_profile_officer_staff_id,
                        loan.loanOfficerId.toString(),
                    ),
                style = MifosTypography.bodyLarge,
            )
            if (loan.loanOfficerName.isNotBlank()) {
                Text(
                    text = stringResource(
                        Res.string.feature_loan_profile_officer_staff_id,
                        loan.loanOfficerId.toString(),
                    ),
                    style = MifosTypography.bodySmall,
                    color = KptTheme.colorScheme.secondary,
                )
            }
            Spacer(Modifier.height(KptTheme.spacing.md))
        }

        MifosTextFieldDropdown(
            value = if (uiState.selectedOfficerIndex < 0) {
                ""
            } else {
                officerLabels.getOrNull(uiState.selectedOfficerIndex).orEmpty()
            },
            onValueChanged = {},
            onOptionSelected = { index, _ -> onOfficerSelected(index) },
            options = officerLabels,
            label = if (hasExistingLoanOfficer) {
                stringResource(Res.string.feature_loan_assign_loan_officer_new_officer)
            } else {
                stringResource(Res.string.feature_loan_assign_loan_officer_to_officer)
            },
            errorMessage = if (uiState.officerShowError) officerErrorText else null,
        )

        Spacer(Modifier.height(KptTheme.spacing.md))

        MifosDatePickerTextField(
            value = DateHelper.getDateAsStringFromLong(uiState.assignmentDateMillis),
            label = stringResource(Res.string.feature_loan_assignment_date),
            openDatePicker = { if (!uiState.submitInProgress) pickDate = true },
        )

        Spacer(Modifier.height(KptTheme.spacing.lg))

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_loan_cancel),
            secondBtnText = stringResource(Res.string.feature_loan_submit),
            onFirstBtnClick = { if (!uiState.submitInProgress) onCancel() },
            onSecondBtnClick = onSubmit,
            isButtonIconVisible = false,
            isSecondButtonEnabled = uiState.selectedOfficerIndex >= 0 && !uiState.submitInProgress,
        )

        Spacer(Modifier.height(DesignToken.padding.medium))
    }
}

private fun staffDisplayLabel(officer: StaffOption): String {
    officer.displayName?.takeIf { it.isNotBlank() }?.let { return it }
    val composed = listOfNotNull(
        officer.firstname?.takeIf { it.isNotBlank() },
        officer.lastname?.takeIf { it.isNotBlank() },
    ).joinToString(" ")
    if (composed.isNotBlank()) return composed
    return "#${officer.id}"
}
