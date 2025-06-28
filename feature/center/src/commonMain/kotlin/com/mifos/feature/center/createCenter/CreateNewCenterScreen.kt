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

package com.mifos.feature.center.createCenter

import androidclient.feature.center.generated.resources.Res
import androidclient.feature.center.generated.resources.feature_center_activate
import androidclient.feature.center.generated.resources.feature_center_activation_date
import androidclient.feature.center.generated.resources.feature_center_cancel
import androidclient.feature.center.generated.resources.feature_center_center_created_successfully
import androidclient.feature.center.generated.resources.feature_center_center_name
import androidclient.feature.center.generated.resources.feature_center_center_name_empty
import androidclient.feature.center.generated.resources.feature_center_center_name_should_be_more_than_4_characters
import androidclient.feature.center.generated.resources.feature_center_center_name_should_not_contains_special_characters_or_numbers
import androidclient.feature.center.generated.resources.feature_center_create
import androidclient.feature.center.generated.resources.feature_center_create_new_center
import androidclient.feature.center.generated.resources.feature_center_failed_to_load_offices
import androidclient.feature.center.generated.resources.feature_center_office
import androidclient.feature.center.generated.resources.feature_center_select
import androidclient.feature.center.generated.resources.feature_center_select_office
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.formatDate
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.room.entities.center.CenterPayloadEntity
import com.mifos.room.entities.organisation.OfficeEntity
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CreateNewCenterScreen(
    onBackPressed: () -> Unit,
    onCreateSuccess: () -> Unit,
    viewModel: CreateNewCenterViewModel = koinViewModel(),
) {
    val state by viewModel.createNewCenterUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadOffices()
    }

    CreateNewCenterScreen(
        state = state,
        onRetry = {
            viewModel.loadOffices()
        },
        createCenter = {
            viewModel.createNewCenter(it)
        },
        onCreateSuccess = onCreateSuccess,
        onBackPressed = onBackPressed,
    )
}

@Composable
internal fun CreateNewCenterScreen(
    state: CreateNewCenterUiState,
    onRetry: () -> Unit,
    createCenter: (CenterPayloadEntity) -> Unit,
    onCreateSuccess: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        title = stringResource(Res.string.feature_center_create_new_center),
        snackbarHostState = snackbarHostState,
        onBackPressed = onBackPressed,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (state) {
                is CreateNewCenterUiState.CenterCreatedSuccessfully -> {
                    MifosAlertDialog(
                        dialogTitle = "Success",
                        dialogText = stringResource(Res.string.feature_center_center_created_successfully),
                        onDismissRequest = onCreateSuccess,
                        onConfirmation = onCreateSuccess,
                    )
                }

                is CreateNewCenterUiState.Error -> MifosSweetError(message = stringResource(state.message)) {
                    onRetry()
                }

                is CreateNewCenterUiState.Loading -> MifosCircularProgress()

                is CreateNewCenterUiState.Offices -> {
                    CreateNewCenterContent(offices = state.offices, createCenter = createCenter)
                }
            }
        }
    }
}

@Composable
private fun CreateNewCenterContent(
    offices: List<OfficeEntity>,
    createCenter: (CenterPayloadEntity) -> Unit,
) {
//    val context = LocalContext.current
    var centerName by rememberSaveable { mutableStateOf("") }
    var centerNameValidator by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedOffice by rememberSaveable { mutableStateOf("") }
    var selectedOfficeValidator by rememberSaveable { mutableStateOf<String?>(null) }
    var isActivate by rememberSaveable { mutableStateOf(false) }
    var activateDate by rememberSaveable { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = activateDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var officeId by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(key1 = centerName) {
        centerNameValidator = when {
            centerName.trim()
                .isEmpty() -> getString(Res.string.feature_center_center_name_empty)

            centerName.length < 4 -> getString(Res.string.feature_center_center_name_should_be_more_than_4_characters)

            centerName.contains("[^a-zA-Z ]".toRegex()) -> getString(Res.string.feature_center_center_name_should_not_contains_special_characters_or_numbers)

            else -> null
        }
    }

    LaunchedEffect(key1 = selectedOffice) {
        selectedOfficeValidator = when {
            selectedOffice.trim()
                .isEmpty() -> getString(Res.string.feature_center_select_office)

            else -> null
        }
    }

    fun validateAllFields(): Boolean {
        when {
            centerNameValidator != null -> {
//                Toast.makeText(context, centerNameValidator, Toast.LENGTH_SHORT).show()
                return false
            }

            selectedOfficeValidator != null -> {
//                Toast.makeText(context, selectedOfficeValidator, Toast.LENGTH_SHORT).show()
                return false
            }

            else -> {
                return true
            }
        }
    }
    Column {
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
                    ) { Text(stringResource(Res.string.feature_center_select)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                        },
                    ) { Text(stringResource(Res.string.feature_center_cancel)) }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }

        MifosOutlinedTextField(
            value = centerName,
            onValueChange = { centerName = it },
            label = stringResource(Res.string.feature_center_center_name),
            error = null,
        )

        MifosTextFieldDropdown(
            value = selectedOffice,
            onValueChanged = {
                selectedOffice = it
            },
            onOptionSelected = { index, value ->
                selectedOffice = value
                offices[index].id.let {
                    officeId = it
                }
            },
            label = stringResource(Res.string.feature_center_office),
            options = offices.map { it.name.toString() },
            readOnly = true,
        )

        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isActivate,
                onCheckedChange = {
                    isActivate = it
                },
            )
            Text(text = stringResource(Res.string.feature_center_activate))
        }

        if (isActivate) {
            MifosDatePickerTextField(
                value = formatDate(activateDate),
                label = stringResource(Res.string.feature_center_activation_date),
                openDatePicker = {
                    showDatePicker = true
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        MifosButton(
            onClick = {
                if (validateAllFields()) {
                    createCenter(
                        CenterPayloadEntity(
                            name = centerName,
                            active = isActivate,
                            activationDate = if (isActivate) {
                                formatDate(activateDate)
                            } else {
                                null
                            },
                            officeId = officeId,
                            dateFormat = "dd MMMM yyyy",
                            locale = "en",
                        ),
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(44.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(),
        ) {
            Text(
                text = stringResource(Res.string.feature_center_create),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

class CreateNewCenterUiStateProvider : PreviewParameterProvider<CreateNewCenterUiState> {

    override val values = sequenceOf(
        CreateNewCenterUiState.Loading,
        CreateNewCenterUiState.Error(Res.string.feature_center_failed_to_load_offices),
        CreateNewCenterUiState.Offices(sampleOfficeList),
        CreateNewCenterUiState.CenterCreatedSuccessfully,
    )
}

@Preview
@Composable
private fun CreateNewCenterPreview(
    @PreviewParameter(CreateNewCenterUiStateProvider::class) state: CreateNewCenterUiState,
) {
    CreateNewCenterScreen(
        state = state,
        onRetry = {},
        createCenter = {},
        onCreateSuccess = {},
        onBackPressed = {},
    )
}

val sampleOfficeList = List(10) {
    OfficeEntity(id = it)
}
