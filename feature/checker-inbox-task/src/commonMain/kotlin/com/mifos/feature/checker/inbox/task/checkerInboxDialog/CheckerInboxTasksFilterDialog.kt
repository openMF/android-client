/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.checker.inbox.task.checkerInboxDialog

import androidclient.feature.checker_inbox_task.generated.resources.Res
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_all
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_apply_filter
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_cancel
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_clear_filter
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_filter_checkers
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_invalid_date_range
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_resourceId
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_select
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_select_action
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_select_entity
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_select_from_date
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_select_to_date
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper.format
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
internal fun CheckerInboxTasksFilterDialog(
    closeDialog: () -> Unit,
    filter: (String?, String?, String?, Instant, Instant) -> Unit,
    clearFilter: () -> Unit,
    fromDate: Instant?,
    toDate: Instant?,
    action: String?,
    entity: String?,
    resourceId: String?,
    viewModel: CheckerInboxDialogViewmodel = koinViewModel(),
) {
    val searchTemplate by viewModel.searchTemplate.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.loadSearchTemplate()
    }
    val actionList: MutableList<String> = mutableListOf()
    actionList.add(stringResource(Res.string.feature_checker_inbox_task_all))
    searchTemplate?.actionNames?.let { actionList.addAll(it) }

    val entityList: MutableList<String> = mutableListOf()
    entityList.add(stringResource(Res.string.feature_checker_inbox_task_all))
    searchTemplate?.entityNames?.let { entityList.addAll(it) }

    CheckerInboxTasksFilterDialog(
        closeDialog = closeDialog,
        actionList = actionList,
        entityList = entityList,
        filter = filter,
        clearFilter = clearFilter,
        filterFromDate = fromDate,
        filterToDate = toDate,
        filterAction = action,
        filterEntity = entity,
        filterResourceId = resourceId,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun CheckerInboxTasksFilterDialog(
    closeDialog: () -> Unit,
    actionList: List<String>,
    entityList: List<String>,
    filter: (String?, String?, String?, Instant, Instant) -> Unit,
    clearFilter: () -> Unit,
    filterFromDate: Instant?,
    filterToDate: Instant?,
    filterAction: String?,
    filterEntity: String?,
    filterResourceId: String?,
) {
    var resourceId by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(filterResourceId ?: ""))
    }
    var action by rememberSaveable {
        mutableStateOf(filterAction ?: "")
    }
    var entity by rememberSaveable {
        mutableStateOf(filterEntity ?: "")
    }

    var showInvalidDateRangeError by remember { mutableStateOf(false) }
    val invalidDateRangeMessage = stringResource(Res.string.feature_checker_inbox_task_invalid_date_range)

    var resourceIdError by rememberSaveable { mutableStateOf(false) }
    var showFromDatePicker by rememberSaveable { mutableStateOf(false) }
    var showToDatePicker by rememberSaveable { mutableStateOf(false) }
    var fromDate: Long by rememberSaveable { mutableLongStateOf(filterFromDate?.toEpochMilliseconds() ?: 0L) }
    var toDate: Long by rememberSaveable { mutableLongStateOf(filterToDate?.toEpochMilliseconds() ?: 0L) }

    val initialDate: LocalDate = LocalDate.parse("2023-01-01")
    val fromDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val selectedDate = Instant.fromEpochMilliseconds(utcTimeMillis)
                    .toLocalDateTime(TimeZone.UTC)
                    .date

                return selectedDate >= initialDate
            }
        },
    )
    val toDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val initialInstant = initialDate.atStartOfDayIn(TimeZone.UTC)
                return utcTimeMillis >= initialInstant.toEpochMilliseconds()
            }
        },
    )

    if (showFromDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showFromDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFromDatePicker = false
                        fromDatePickerState.selectedDateMillis?.let {
                            fromDate = it
                        }
                    },
                ) { Text(stringResource(Res.string.feature_checker_inbox_task_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showFromDatePicker = false
                    },
                ) { Text(stringResource(Res.string.feature_checker_inbox_task_cancel)) }
            },
        ) {
            DatePicker(state = fromDatePickerState)
        }
    }

    if (showToDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showToDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showToDatePicker = false
                        toDatePickerState.selectedDateMillis?.let {
                            toDate = it
                        }
                    },
                ) { Text(stringResource(Res.string.feature_checker_inbox_task_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showToDatePicker = false
                    },
                ) { Text(stringResource(Res.string.feature_checker_inbox_task_cancel)) }
            },
        ) {
            DatePicker(state = toDatePickerState)
        }
    }

    Dialog(
        onDismissRequest = { closeDialog.invoke() },
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_checker_inbox_task_filter_checkers),
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        )
                        Icon(
                            imageVector = MifosIcons.Cancel,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .clickable { closeDialog.invoke() },
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosDatePickerTextField(
                        value = if (fromDate == 0L) {
                            ""
                        } else {
                            val localDate = Instant.fromEpochMilliseconds(fromDate)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date

                            localDate.format("dd-MM-yyyy")
                        },
                        label = stringResource(Res.string.feature_checker_inbox_task_select_from_date),
                        openDatePicker = {
                            if (fromDate == 0L) {
                                fromDatePickerState.selectedDateMillis = Clock.System.now().toEpochMilliseconds()
                            } else {
                                fromDatePickerState.selectedDateMillis = fromDate
                            }
                            showFromDatePicker = true
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosDatePickerTextField(
                        value = if (toDate == 0L) {
                            ""
                        } else {
                            val localDate = Instant.fromEpochMilliseconds(toDate)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date

                            localDate.format("dd-MM-yyyy")
                        },
                        label = stringResource(Res.string.feature_checker_inbox_task_select_to_date),
                        openDatePicker = {
                            if (toDate == 0L) {
                                toDatePickerState.selectedDateMillis = Clock.System.now().toEpochMilliseconds()
                            } else {
                                toDatePickerState.selectedDateMillis = toDate
                            }
                            showToDatePicker = true
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosTextFieldDropdown(
                        value = action,
                        onValueChanged = {
                            action = it
                        },
                        readOnly = true,
                        onOptionSelected = { index, value ->
                            action = value
                        },
                        label = stringResource(Res.string.feature_checker_inbox_task_select_action),
                        options = actionList,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosTextFieldDropdown(
                        value = entity,
                        onValueChanged = {
                            entity = it
                        },
                        readOnly = true,
                        onOptionSelected = { index, value ->
                            entity = value
                        },
                        label = stringResource(Res.string.feature_checker_inbox_task_select_entity),
                        options = entityList,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosOutlinedTextField(
                        value = resourceId,
                        onValueChanged = { value ->
                            resourceId = value
                            resourceIdError = false
                        },
                        label = stringResource(Res.string.feature_checker_inbox_task_resourceId),
                        error = null,
                        trailingIcon = {
                            if (resourceIdError) {
                                Icon(imageVector = MifosIcons.Error, contentDescription = null)
                            }
                        },
                    )

                    if (showInvalidDateRangeError) {
                        Text(
                            text = invalidDateRangeMessage,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .padding(top = 8.dp, start = 16.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Button(
                            onClick = {
                                clearFilter.invoke()
                            },
                            modifier = Modifier.height(40.dp),
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        ) {
                            Text(
                                text = stringResource(Res.string.feature_checker_inbox_task_clear_filter),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        Button(
                            onClick = {
                                showInvalidDateRangeError = fromDate > toDate
                                if (!showInvalidDateRangeError) {
                                    filter.invoke(
                                        action,
                                        entity,
                                        resourceId.text,
                                        Instant.fromEpochMilliseconds(fromDate),
                                        Instant.fromEpochMilliseconds(toDate),
                                    )
                                }
                            },
                            modifier = Modifier.height(40.dp),
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        ) {
                            Text(
                                text = stringResource(Res.string.feature_checker_inbox_task_apply_filter),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun CheckerInboxTasksFilterDialogPreview() {
    CheckerInboxTasksFilterDialog(
        { },
        listOf(),
        listOf(),
        { _, _, _, _, _ -> },
        {},
        null,
        null,
        null,
        null,
        null,
    )
}
