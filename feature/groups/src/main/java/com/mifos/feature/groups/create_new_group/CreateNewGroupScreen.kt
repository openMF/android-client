package com.mifos.feature.groups.create_new_group

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Network
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse
import com.mifos.feature.groups.R
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Pronay Sarker on 30/06/2024 (7:53 AM)
 */

@Composable
fun CreateNewGroupScreen(
    viewModel: CreateNewGroupViewModel = hiltViewModel(),
    onGroupCreated: (group: SaveResponse?) -> Unit,
) {
    val uiState by viewModel.createNewGroupUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadOffices()
    }

    CreateNewGroupScreen(
        uiState = uiState,
        onRetry = { viewModel.loadOffices() },
        invokeGroupCreation = { groupPayload ->
            viewModel.createGroup(groupPayload)
        },
        onGroupCreated = onGroupCreated,
        getResponse = { viewModel.getResponse() }
    )
}

@Composable
fun CreateNewGroupScreen(
    uiState: CreateNewGroupUiState,
    onRetry: () -> Unit,
    invokeGroupCreation: (GroupPayload) -> Unit,
    onGroupCreated: (group: SaveResponse?) -> Unit,
    getResponse: () -> String
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState) {
            is CreateNewGroupUiState.ShowFetchingError -> {
                MifosSweetError(
                    message = uiState.message,
                    onclick = { onRetry.invoke() }
                )
            }

            is CreateNewGroupUiState.ShowGroupCreatedSuccessfully -> {
                Toast.makeText(context, "Group " +  getResponse() , Toast.LENGTH_LONG)
                    .show()
                onGroupCreated.invoke(uiState.saveResponse)
            }

            is CreateNewGroupUiState.ShowOffices -> {
                CreateNewGroupContent(
                    officeList = uiState.offices,
                    invokeGroupCreation = invokeGroupCreation
                )
            }

            CreateNewGroupUiState.ShowProgressbar -> {
                MifosCircularProgress()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewGroupContent(
    officeList: List<Office>,
    invokeGroupCreation: (GroupPayload) -> Unit,
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

    val context = LocalContext.current
    val density = LocalDensity.current
    val scrollState = rememberScrollState()
    var officeId by rememberSaveable { mutableIntStateOf(0) }
    var activationDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    var submittedOnDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }

    val activateDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = activationDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )
    val sumittedDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = submittedOnDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
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
                    }
                ) { Text(stringResource(id = R.string.feature_groups_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        activationDatePicker = false
                        submitDatePicker = false
                    }
                ) { Text(stringResource(id = R.string.feature_groups_dismiss)) }
            }
        )
        {
            DatePicker(state = if (submitDatePicker) sumittedDatePickerState else activateDatePickerState)

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            style = MaterialTheme.typography.headlineSmall,
            text = stringResource(id = R.string.feature_groups_create_new_group),
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = stringResource(id = R.string.feature_groups_name),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = selectedOffice,
            onValueChanged = {
                selectedOffice = it
            },
            onOptionSelected = { index, value ->
                selectedOffice = value
                officeList[index].id?.let {
                    officeId = it
                }

            },
            label = R.string.feature_groups_office_name_mandatory,
            options = officeList.map { it.name.toString() },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                submittedOnDate
            ),
            label = R.string.feature_groups_submit_date,
            openDatePicker = {
                submitDatePicker = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = externalId,
            onValueChange = { externalId = it },
            label = stringResource(id = R.string.feature_groups_external_id),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                modifier = Modifier.padding(start = 8.dp),
                colors = CheckboxDefaults.colors(
                    if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                ),
                checked = isActive,
                onCheckedChange = { isActive = !isActive }
            )
            Text(text = stringResource(id = R.string.feature_groups_active))
        }

        AnimatedVisibility(
            visible = isActive,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            MifosDatePickerTextField(
                value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                    activationDate
                ),
                label = R.string.feature_groups_activation_date,
                openDatePicker = {
                    activationDatePicker = true
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(46.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
            ),
            onClick = {
                if (validateFields(groupName, selectedOffice, context)) {
                    if (Network.isOnline(context)) {
                        val activationDateInString = if (isActive) SimpleDateFormat(
                            "dd MMMM yyyy",
                            Locale.getDefault()
                        ).format(
                            activationDate
                        ) else null

                        val submittedOnDateInString = SimpleDateFormat(
                            "dd MMMM yyyy",
                            Locale.getDefault()
                        ).format(
                            submittedOnDate
                        )

                        invokeGroupCreation.invoke(
                            GroupPayload(
                                name = groupName,
                                externalId = externalId,
                                active = isActive,
                                activationDate = activationDateInString,
                                submittedOnDate = submittedOnDateInString,
                                officeId = officeId,
                                dateFormat = "dd MMMM yyyy",
                                locale = "en"
                            )
                        )
                    } else {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.feature_groups_error_not_connected_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }) {
            Text(text = stringResource(id = R.string.feature_groups_submit))
        }
    }
}

fun validateFields(groupName: String, officeName: String, context: Context): Boolean {
    return when {
        groupName.isEmpty() -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_groups_error_group_name_cannot_be_empty),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        groupName.trim().length < 4 -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_groups_error_group_name_must_be_at_least_four_characters_long),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        groupName.contains("[^a-zA-Z ]".toRegex()) -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_groups_error_group_name_should_contain_only_alphabets),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        officeName.isEmpty() -> {
            Toast.makeText(
                context,
                context.resources.getString(R.string.feature_groups_error_office_not_selected),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        else -> true
    }
}

class CreateNewGroupScreenPreviewProvider : PreviewParameterProvider<CreateNewGroupUiState> {
    override val values: Sequence<CreateNewGroupUiState>
        get() = sequenceOf(
            CreateNewGroupUiState.ShowProgressbar,
            CreateNewGroupUiState.ShowOffices(listOf()),
            CreateNewGroupUiState.ShowFetchingError("Failed to fetch Offices"),
            CreateNewGroupUiState.ShowGroupCreatedSuccessfully(saveResponse = SaveResponse()),
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewCreateNewGroupScreen(
    @PreviewParameter(CreateNewGroupScreenPreviewProvider::class) createNewGroupUiState: CreateNewGroupUiState
) {
    CreateNewGroupScreen(
        uiState = createNewGroupUiState,
        onRetry = {},
        invokeGroupCreation = {},
        onGroupCreated = { _ ->

        },
        getResponse = { "" }
    )
}
