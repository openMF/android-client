/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.createNewGroup

import androidclient.feature.groups.generated.resources.Res
import androidclient.feature.groups.generated.resources.feature_groups_activation_date
import androidclient.feature.groups.generated.resources.feature_groups_active
import androidclient.feature.groups.generated.resources.feature_groups_create_new_group
import androidclient.feature.groups.generated.resources.feature_groups_dismiss
import androidclient.feature.groups.generated.resources.feature_groups_error_group_name_cannot_be_empty
import androidclient.feature.groups.generated.resources.feature_groups_error_group_name_must_be_at_least_four_characters_long
import androidclient.feature.groups.generated.resources.feature_groups_error_group_name_should_contain_only_alphabets
import androidclient.feature.groups.generated.resources.feature_groups_error_office_not_selected
import androidclient.feature.groups.generated.resources.feature_groups_external_id
import androidclient.feature.groups.generated.resources.feature_groups_name
import androidclient.feature.groups.generated.resources.feature_groups_office_name_mandatory
import androidclient.feature.groups.generated.resources.feature_groups_select_date
import androidclient.feature.groups.generated.resources.feature_groups_submit
import androidclient.feature.groups.generated.resources.feature_groups_submit_date
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.common.utils.formatDate
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.core.ui.components.MifosAlertDialog
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.room.entities.group.GroupPayloadEntity
import com.mifos.room.entities.organisation.OfficeEntity
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Created by Pronay Sarker on 30/06/2024 (7:53 AM)
 */

@Composable
internal fun CreateNewGroupScreen(
    viewModel: CreateNewGroupViewModel = koinViewModel(),
    onGroupCreated: (group: SaveResponse?, userStatus: Boolean) -> Unit,
    onBackPressed: () -> Unit,
) {
    val uiState by viewModel.createNewGroupUiState.collectAsStateWithLifecycle()
    val userStatus by viewModel.userStatus.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadOffices()
    }

    CreateNewGroupScreen(
        uiState = uiState,
        onRetry = { viewModel.loadOffices() },
        invokeGroupCreation = { groupPayload ->
            viewModel.createGroup(groupPayload)
        },
        onGroupCreated = { onGroupCreated(it, userStatus) },
        getResponse = { viewModel.getResponse() },
        onBackPressed = onBackPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateNewGroupScreen(
    uiState: CreateNewGroupUiState,
    onRetry: () -> Unit,
    onBackPressed: () -> Unit,
    invokeGroupCreation: (GroupPayloadEntity) -> Unit,
    onGroupCreated: (group: SaveResponse?) -> Unit,
    modifier: Modifier = Modifier,
    getResponse: () -> String,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        modifier = modifier,
        title = stringResource(Res.string.feature_groups_create_new_group),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
        ) {
            when (uiState) {
                is CreateNewGroupUiState.ShowFetchingError -> {
                    MifosSweetError(
                        message = uiState.message,
                        onclick = { onRetry.invoke() },
                    )
                }

                is CreateNewGroupUiState.ShowGroupCreatedSuccessfully -> {
                    MifosAlertDialog(
                        dialogTitle = "Success",
                        dialogText = "Group" + getResponse(),
                        confirmationText = "OK",
                        onConfirmation = {
                            onGroupCreated.invoke(uiState.saveResponse)
                        },
                        onDismissRequest = { },
                    )
                }

                is CreateNewGroupUiState.ShowOffices -> {
                    CreateNewGroupContent(
                        officeList = uiState.offices,
                        invokeGroupCreation = invokeGroupCreation,
                    )
                }

                CreateNewGroupUiState.ShowProgressbar -> {
                    MifosProgressIndicator()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun CreateNewGroupContent(
    officeList: List<OfficeEntity>,
    modifier: Modifier = Modifier,
    invokeGroupCreation: (GroupPayloadEntity) -> Unit,
) {
    var groupName by rememberSaveable {
        mutableStateOf("")
    }
    var selectedOffice by rememberSaveable {
        mutableStateOf("")
    }
    var externalId by rememberSaveable {
        mutableStateOf("")
    }
    var submitDatePicker by rememberSaveable {
        mutableStateOf(false)
    }
    var activationDatePicker by rememberSaveable {
        mutableStateOf(false)
    }
    var isActive by rememberSaveable {
        mutableStateOf(false)
    }

    var groupValidationError: StringResource? by rememberSaveable { mutableStateOf(null) }
    var officeValidationError: StringResource? by rememberSaveable { mutableStateOf(null) }

    val density = LocalDensity.current
    val scrollState = rememberScrollState()
    var officeId by rememberSaveable { mutableIntStateOf(0) }

    var activationDate by rememberSaveable {
        mutableLongStateOf(
            Clock.System.now().toEpochMilliseconds(),
        )
    }
    var submittedOnDate by rememberSaveable {
        mutableLongStateOf(
            Clock.System.now().toEpochMilliseconds(),
        )
    }

    val activateDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = activationDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    val sumittedDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = submittedOnDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    if (activationDatePicker || submitDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                submitDatePicker = false
                activationDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (submitDatePicker) {
                            sumittedDatePickerState.selectedDateMillis?.let {
                                submittedOnDate = it
                            }
                        } else {
                            activateDatePickerState.selectedDateMillis?.let {
                                activationDate = it
                            }
                        }
                        submitDatePicker = false
                        activationDatePicker = false
                    },
                ) { Text(stringResource(Res.string.feature_groups_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        activationDatePicker = false
                        submitDatePicker = false
                    },
                ) { Text(stringResource(Res.string.feature_groups_dismiss)) }
            },
        ) {
            DatePicker(state = if (submitDatePicker) sumittedDatePickerState else activateDatePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = groupName,
            onValueChange = {
                groupName = it
                groupValidationError = null
            },
            label = stringResource(Res.string.feature_groups_name),
            error = groupValidationError?.let { stringResource(it) },
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = selectedOffice,
            onValueChanged = {
                selectedOffice = it
            },
            onOptionSelected = { index, value ->
                selectedOffice = value
                officeList[index].id.let {
                    officeId = it
                }
                officeValidationError = null
            },
            label = stringResource(Res.string.feature_groups_office_name_mandatory),
            options = officeList.map { it.name.toString() },
            readOnly = true,
            errorMessage = officeValidationError?.let { stringResource(it) },
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = DateHelper.getDateAsStringFromLong(submittedOnDate),
            label = stringResource(Res.string.feature_groups_submit_date),
            openDatePicker = {
                submitDatePicker = true
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = externalId,
            onValueChange = { externalId = it },
            label = stringResource(Res.string.feature_groups_external_id),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                modifier = Modifier.padding(start = 8.dp),
                checked = isActive,
                onCheckedChange = { isActive = !isActive },
            )
            Text(text = stringResource(Res.string.feature_groups_active))
        }

        AnimatedVisibility(
            visible = isActive,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top,
            ) + fadeIn(
                initialAlpha = 0.3f,
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut(),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            MifosDatePickerTextField(
                value = DateHelper.getDateAsStringFromLong(activationDate),
                label = stringResource(Res.string.feature_groups_activation_date),
                openDatePicker = {
                    activationDatePicker = true
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(46.dp),
            onClick = {
                groupValidationError = validateGroupField(groupName)
                officeValidationError = validateOffice(selectedOffice)

                if (groupValidationError == null && officeValidationError == null) {
                    val activationDateInString = formatDate(activationDate)
                    val submittedOnDateInString = formatDate(submittedOnDate)

                    invokeGroupCreation.invoke(
                        GroupPayloadEntity(
                            name = groupName,
                            externalId = externalId,
                            active = isActive,
                            activationDate = activationDateInString,
                            submittedOnDate = submittedOnDateInString,
                            officeId = officeId,
                            dateFormat = "dd MMMM yyyy",
                            locale = "en",
                        ),
                    )
                }
            },
        ) {
            Text(text = stringResource(Res.string.feature_groups_submit))
        }
    }
}

private fun validateGroupField(
    groupName: String,
): StringResource? {
    return when {
        groupName.isEmpty() ->
            Res.string.feature_groups_error_group_name_cannot_be_empty

        groupName.trim().length < 4 -> Res.string.feature_groups_error_group_name_must_be_at_least_four_characters_long

        groupName.contains("[^a-zA-Z ]".toRegex()) -> Res.string.feature_groups_error_group_name_should_contain_only_alphabets

        else -> null
    }
}

private fun validateOffice(
    officeName: String,
): StringResource? {
    return when {
        officeName.isEmpty() -> Res.string.feature_groups_error_office_not_selected

        else -> null
    }
}

private class CreateNewGroupScreenPreviewProvider :
    PreviewParameterProvider<CreateNewGroupUiState> {
    override val values: Sequence<CreateNewGroupUiState>
        get() = sequenceOf(
            CreateNewGroupUiState.ShowProgressbar,
            CreateNewGroupUiState.ShowOffices(listOf()),
            CreateNewGroupUiState.ShowFetchingError("Failed to fetch Offices"),
            CreateNewGroupUiState.ShowGroupCreatedSuccessfully(saveResponse = SaveResponse()),
        )
}

@Composable
@Preview
private fun PreviewCreateNewGroupScreen(
    @PreviewParameter(CreateNewGroupScreenPreviewProvider::class) createNewGroupUiState: CreateNewGroupUiState,
) {
    CreateNewGroupScreen(
        uiState = createNewGroupUiState,
        onRetry = {},
        invokeGroupCreation = {},
        onGroupCreated = { _ ->
        },
        getResponse = { "" },
        onBackPressed = {},
    )
}
