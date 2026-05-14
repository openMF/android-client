/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.createGuarantor

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_address_line_1
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_address_line_2
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_city
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_date_of_birth
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_existing_client
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_first_name
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_last_name
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_mobile
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_name
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_relationship
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_residence_phone
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_title
import androidclient.feature.loan.generated.resources.feature_loan_create_guarantor_zip
import androidclient.feature.loan.generated.resources.feature_loan_select_date
import androidclient.feature.loan.generated.resources.feature_loan_submit
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosCheckBox
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosTwoButtonRow
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CreateGuarantorScreenRoute(
    navigateBack: () -> Unit,
    viewModel: CreateGuarantorViewModel = koinViewModel(),
) {
    val uiState by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { effect ->
            when (effect) {
                is CreateGuarantorEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                CreateGuarantorEffect.NavigateBack -> navigateBack()
            }
        }
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_loan_create_guarantor_title),
        onBackPressed = {
            val submitting = (uiState as? CreateGuarantorUiState.Content)?.submitInProgress == true
            if (!submitting) navigateBack()
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        when (val state = uiState) {
            CreateGuarantorUiState.Loading -> MifosProgressIndicator()
            is CreateGuarantorUiState.Error -> {
                MifosSweetError(
                    message = state.message,
                    isRetryEnabled = true,
                    onclick = { viewModel.trySendAction(CreateGuarantorAction.Retry) },
                )
            }
            is CreateGuarantorUiState.Content -> {
                CreateGuarantorContent(
                    state = state,
                    modifier = Modifier.padding(paddingValues),
                    onAction = viewModel::trySendAction,
                    navigateBack = navigateBack,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGuarantorContent(
    state: CreateGuarantorUiState.Content,
    modifier: Modifier = Modifier,
    onAction: (CreateGuarantorAction) -> Unit,
    navigateBack: () -> Unit,
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val filteredClientOptions = state.filteredClientOptions

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = DesignToken.padding.largeIncreased),
    ) {
        MifosCheckBox(
            text = stringResource(Res.string.feature_loan_create_guarantor_existing_client),
            checked = state.existingClient,
            onCheckChanged = { onAction(CreateGuarantorAction.ToggleExistingClient(it)) },
        )

        Spacer(Modifier.height(DesignToken.padding.medium))

        if (state.existingClient) {
            MifosTextFieldDropdown(
                value = state.clientQuery,
                onValueChanged = { onAction(CreateGuarantorAction.UpdateClientQuery(it)) },
                label = stringResource(Res.string.feature_loan_create_guarantor_name),
                options = filteredClientOptions.map { it.displayName },
                onOptionSelected = { index, _ ->
                    filteredClientOptions.getOrNull(index)?.let { selectedClient ->
                        onAction(
                            CreateGuarantorAction.SelectClient(
                                clientId = selectedClient.id,
                                label = selectedClient.displayName,
                            ),
                        )
                    }
                },
                errorMessage = state.clientError,
                readOnly = false,
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
        }

        MifosTextFieldDropdown(
            value = state.relationshipOptions.getOrNull(state.selectedRelationshipIndex)?.value.orEmpty(),
            onValueChanged = {},
            label = stringResource(Res.string.feature_loan_create_guarantor_relationship),
            options = state.relationshipOptions.map { it.value },
            onOptionSelected = { index, _ -> onAction(CreateGuarantorAction.SelectRelationship(index)) },
            errorMessage = state.relationshipError,
        )

        if (!state.existingClient) {
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosOutlinedTextField(
                value = state.firstName,
                onValueChange = { onAction(CreateGuarantorAction.UpdateFirstName(it)) },
                label = stringResource(Res.string.feature_loan_create_guarantor_first_name),
                isError = state.firstNameError != null,
                errorText = state.firstNameError,
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosOutlinedTextField(
                value = state.lastName,
                onValueChange = { onAction(CreateGuarantorAction.UpdateLastName(it)) },
                label = stringResource(Res.string.feature_loan_create_guarantor_last_name),
                isError = state.lastNameError != null,
                errorText = state.lastNameError,
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosDatePickerTextField(
                value = state.dateOfBirthMillis?.let(DateHelper::getDateAsStringFromLong).orEmpty(),
                label = stringResource(Res.string.feature_loan_create_guarantor_date_of_birth),
                openDatePicker = { showDatePicker = true },
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosOutlinedTextField(
                value = state.addressLine1,
                onValueChange = { onAction(CreateGuarantorAction.UpdateAddressLine1(it)) },
                label = stringResource(Res.string.feature_loan_create_guarantor_address_line_1),
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosOutlinedTextField(
                value = state.addressLine2,
                onValueChange = { onAction(CreateGuarantorAction.UpdateAddressLine2(it)) },
                label = stringResource(Res.string.feature_loan_create_guarantor_address_line_2),
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosOutlinedTextField(
                value = state.city,
                onValueChange = { onAction(CreateGuarantorAction.UpdateCity(it)) },
                label = stringResource(Res.string.feature_loan_create_guarantor_city),
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosOutlinedTextField(
                value = state.zip,
                onValueChange = { onAction(CreateGuarantorAction.UpdateZip(it)) },
                label = stringResource(Res.string.feature_loan_create_guarantor_zip),
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosOutlinedTextField(
                value = state.mobile,
                onValueChange = { onAction(CreateGuarantorAction.UpdateMobile(it)) },
                label = stringResource(Res.string.feature_loan_create_guarantor_mobile),
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosOutlinedTextField(
                value = state.residencePhone,
                onValueChange = { onAction(CreateGuarantorAction.UpdateResidencePhone(it)) },
                label = stringResource(Res.string.feature_loan_create_guarantor_residence_phone),
            )
        }

        Spacer(Modifier.height(DesignToken.padding.large))

        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_loan_cancel),
            secondBtnText = stringResource(Res.string.feature_loan_submit),
            onFirstBtnClick = { if (!state.submitInProgress) navigateBack() },
            onSecondBtnClick = { onAction(CreateGuarantorAction.Submit) },
            isButtonIconVisible = false,
            isSecondButtonEnabled = state.canSubmit && !state.submitInProgress,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(DesignToken.padding.large))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.dateOfBirthMillis,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(CreateGuarantorAction.UpdateDateOfBirth(datePickerState.selectedDateMillis))
                        showDatePicker = false
                    },
                ) {
                    Text(stringResource(Res.string.feature_loan_select_date))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(Res.string.feature_loan_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
