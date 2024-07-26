package com.mifos.feature.checker_inbox_task.dialog

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.White
import com.mifos.feature.checker_inbox_task.R
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date

@SuppressLint("RestrictedApi")
@Composable
fun CheckerInboxTasksFilterDialog(
    closeDialog: () -> Unit,
    filter: (String?, String?, String?, Timestamp, Timestamp) -> Unit,
    clearFilter: () -> Unit,
    fromDate: Timestamp?,
    toDate: Timestamp?,
    action: String?,
    entity: String?,
    resourceId: String?
) {

    val viewModel : CheckerInboxViewModel = hiltViewModel()
    val searchTemplate by viewModel.searchTemplate.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = true) {
        viewModel.loadSearchTemplate()
    }
    val actionList : MutableList<String> = mutableListOf()
    actionList.add(stringResource(id = R.string.feature_checker_inbox_task_all))
    searchTemplate?.actionNames?.let { actionList.addAll(it) }

    val entityList : MutableList<String> = mutableListOf()
    entityList.add(stringResource(id = R.string.feature_checker_inbox_task_all))
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
        filterResourceId = resourceId
    )
}

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckerInboxTasksFilterDialog(
    closeDialog: () -> Unit,
    actionList: List<String>,
    entityList: List<String>,
    filter: ( String?, String?, String?, Timestamp, Timestamp ) -> Unit,
    clearFilter: () -> Unit,
    filterFromDate: Timestamp?,
    filterToDate: Timestamp?,
    filterAction: String?,
    filterEntity: String?,
    filterResourceId: String?
) {

    val context = LocalContext.current
    var resourceId by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(filterResourceId ?: ""))
    }
    var action by rememberSaveable  {
        mutableStateOf( filterAction ?: "")
    }
    var entity by rememberSaveable  {
        mutableStateOf(filterEntity ?: "")
    }
    var resourceIdError by rememberSaveable { mutableStateOf(false) }
    var showFromDatePicker by rememberSaveable { mutableStateOf(false) }
    var showToDatePicker by rememberSaveable { mutableStateOf(false) }
    var fromDate : Long by rememberSaveable { mutableLongStateOf(filterFromDate?.time ?: 0) }
    var toDate : Long by rememberSaveable { mutableLongStateOf(filterToDate?.time ?: 0) }

    val formatter = SimpleDateFormat("dd/MM/yyyy")
    val initialDate : LocalDate = LocalDate.parse("01/01/2023", DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val fromDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >=  initialDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            }
        }
    )
    val toDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >=  initialDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            }
        }
    )

    if (showFromDatePicker ) {
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
                    }
                ) { Text(stringResource(id = R.string.feature_checker_inbox_task_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showFromDatePicker = false
                    }
                ) { Text(stringResource(id = R.string.feature_checker_inbox_task_cancel)) }
            }
        )
        {
            DatePicker(state = fromDatePickerState)
        }
    }


    if ( showToDatePicker ) {
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
                    }
                ) { Text(stringResource(id = R.string.feature_checker_inbox_task_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showToDatePicker = false
                    }
                ) { Text(stringResource(id = R.string.feature_checker_inbox_task_cancel)) }
            }
        )
        {
            DatePicker(state = toDatePickerState)
        }
    }

    Dialog(
        onDismissRequest = { closeDialog.invoke() }
    )
    {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.feature_checker_inbox_task_filter_checkers),
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            color = BluePrimary
                        )
                        Icon(
                            imageVector = MifosIcons.cancel,
                            contentDescription = "",
                            tint = colorResource(android.R.color.darker_gray),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .clickable { closeDialog.invoke() }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosDatePickerTextField(
                        value = if(fromDate == 0L) "" else formatter.format(Date(fromDate)),
                        label = R.string.feature_checker_inbox_task_select_from_date,
                        openDatePicker = {
                            if(fromDate == 0L)
                                fromDatePickerState.selectedDateMillis = System.currentTimeMillis()
                            else
                                fromDatePickerState.selectedDateMillis = fromDate
                            showFromDatePicker = true
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosDatePickerTextField(
                        value = if(toDate == 0L) "" else formatter.format(Date(toDate)),
                        label = R.string.feature_checker_inbox_task_select_to_date,
                        openDatePicker = {
                            if(toDate == 0L)
                                toDatePickerState.selectedDateMillis = System.currentTimeMillis()
                            else
                                toDatePickerState.selectedDateMillis = toDate
                            showToDatePicker = true
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosTextFieldDropdown(
                        value = action,
                        onValueChanged = { it->
                            action = it
                        },
                        readOnly = true,
                        onOptionSelected = { index, value ->
                            action = value
                        },
                        label = R.string.feature_checker_inbox_task_select_action,
                        options = actionList

                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosTextFieldDropdown(
                        value = entity,
                        onValueChanged = { it->
                            entity = it
                        },
                        readOnly = true,
                        onOptionSelected = { index, value ->
                            entity = value
                        },
                        label = R.string.feature_checker_inbox_task_select_entity,
                        options = entityList
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosOutlinedTextField(
                        value = resourceId,
                        onValueChange = { value ->
                            resourceId = value
                            resourceIdError = false
                        },
                        label = R.string.feature_checker_inbox_task_resourceId,
                        error = null,
                        trailingIcon = {
                            if (resourceIdError) {
                                Icon(imageVector = MifosIcons.error, contentDescription = null)
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){

                        Button(
                            onClick = {
                                clearFilter.invoke()
                            },
                            modifier = Modifier
                                .height(40.dp),
                            colors = ButtonColors(
                                containerColor = BluePrimary,
                                contentColor = White,
                                disabledContainerColor = BluePrimary,
                                disabledContentColor = White
                            )
                        ) {
                            Text(text = stringResource(id =R.string.feature_checker_inbox_task_clear_filter))
                        }

                        Button(
                            onClick = {
                                if(fromDate > toDate)
                                {
                                    Toast.makeText(context, R.string.feature_checker_inbox_task_invalid_date_range, Toast.LENGTH_SHORT).show()
                                }else {
                                    filter.invoke(
                                        action,
                                        entity,
                                        resourceId.text,
                                        Timestamp(fromDate),
                                        Timestamp(toDate)
                                    )
                                }
                            },
                            modifier = Modifier
                                .height(40.dp),
                            colors = ButtonColors(
                                containerColor = BluePrimary,
                                contentColor = White,
                                disabledContainerColor = BluePrimary,
                                disabledContentColor = Gray
                            )
                        ) {
                            Text(text = stringResource(id =R.string.feature_checker_inbox_task_apply_filter))
                        }

                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CheckerInboxTasksFilterDialogPreview(
) {
    CheckerInboxTasksFilterDialog(
        { },
        listOf<String>(),
        listOf<String>(),
        { _, _, _, _, _ -> },
        {},
        null,
        null,
        null,
        null,
        null
    )
}