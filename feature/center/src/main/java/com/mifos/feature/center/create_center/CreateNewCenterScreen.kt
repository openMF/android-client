@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.center.create_center

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.data.CenterPayload
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.objects.organisation.Office
import com.mifos.feature.center.R
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CreateNewCenterScreen(
    onCreateSuccess: () -> Unit
) {

    val viewModel: CreateNewCenterViewModel = hiltViewModel()
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
        onCreateSuccess = onCreateSuccess
    )
}

@Composable
fun CreateNewCenterScreen(
    state: CreateNewCenterUiState,
    onRetry: () -> Unit,
    createCenter: (CenterPayload) -> Unit,
    onCreateSuccess: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        title = stringResource(id = R.string.feature_center_create_new_center),
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (state) {
                is CreateNewCenterUiState.CenterCreatedSuccessfully -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = R.string.feature_center_center_created_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    onCreateSuccess()
                }

                is CreateNewCenterUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {
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
fun CreateNewCenterContent(offices: List<Office>, createCenter: (CenterPayload) -> Unit) {

    val context = LocalContext.current
    var centerName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var centerNameValidator by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedOffice by rememberSaveable { mutableStateOf("") }
    var selectedOfficeValidator by rememberSaveable { mutableStateOf<String?>(null) }
    var isActivate by rememberSaveable { mutableStateOf(false) }
    var activateDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = activateDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var officeId by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(key1 = centerName) {
        centerNameValidator = when {
            centerName.text.trim()
                .isEmpty() -> context.getString(R.string.feature_center_center_name_empty)

            centerName.text.length < 4 -> context.getString(R.string.feature_center_center_name_should_be_more_than_4_characters)

            centerName.text.contains("[^a-zA-Z ]".toRegex()) -> context.getString(R.string.feature_center_center_name_should_not_contains_special_characters_or_numbers)

            else -> null
        }
    }

    LaunchedEffect(key1 = selectedOffice) {
        selectedOfficeValidator = when {
            selectedOffice.trim()
                .isEmpty() -> context.getString(R.string.feature_center_select_office)

            else -> null
        }
    }

    fun validateAllFields(): Boolean {
        when {
            centerNameValidator != null -> {
                Toast.makeText(context, centerNameValidator, Toast.LENGTH_SHORT).show()
                return false
            }

            selectedOfficeValidator != null -> {
                Toast.makeText(context, selectedOfficeValidator, Toast.LENGTH_SHORT).show()
                return false
            }

            else -> {
                return true
            }
        }
    }


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
                    }
                ) { Text(stringResource(id = R.string.feature_center_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) { Text(stringResource(id = R.string.feature_center_cancel)) }
            }
        )
        {
            DatePicker(state = datePickerState)
        }
    }

    MifosOutlinedTextField(
        value = centerName,
        onValueChange = { centerName = it },
        label = R.string.feature_center_center_name,
        error = null
    )

    MifosTextFieldDropdown(
        value = selectedOffice,
        onValueChanged = {
            selectedOffice = it
        },
        onOptionSelected = { index, value ->
            selectedOffice = value
            offices[index].id?.let {
                officeId = it
            }

        },
        label = R.string.feature_center_office,
        options = offices.map { it.name.toString() },
        readOnly = true
    )

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isActivate,
            onCheckedChange = {
                isActivate = it
            }
        )
        Text(text = stringResource(id = R.string.feature_center_activate))
    }

    if (isActivate) {
        MifosDatePickerTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                activateDate
            ),
            label = R.string.feature_center_activation_date,
            openDatePicker = {
                showDatePicker = true
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    Button(
        onClick = {
            if (validateAllFields()) {
                createCenter(
                    CenterPayload(
                        name = centerName.text,
                        active = isActivate,
                        activationDate = if (isActivate) SimpleDateFormat(
                            "dd MMMM yyyy",
                            Locale.getDefault()
                        ).format(
                            activateDate
                        ) else null,
                        officeId = officeId,
                        dateFormat = "dd MMMM yyyy",
                        locale = "en"
                    )
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(44.dp)
            .padding(start = 16.dp, end = 16.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
        )
    ) {
        Text(text = stringResource(id = R.string.feature_center_create), fontSize = 16.sp)
    }
}

class CreateNewCenterUiStateProvider : PreviewParameterProvider<CreateNewCenterUiState> {

    override val values = sequenceOf(
        CreateNewCenterUiState.Loading,
        CreateNewCenterUiState.Error(R.string.feature_center_failed_to_load_offices),
        CreateNewCenterUiState.Offices(sampleOfficeList),
        CreateNewCenterUiState.CenterCreatedSuccessfully
    )
}


@Preview(showBackground = true)
@Composable
private fun CreateNewCenterPreview(
    @PreviewParameter(CreateNewCenterUiStateProvider::class) state: CreateNewCenterUiState
) {
    CreateNewCenterScreen(
        state = state,
        onRetry = {},
        createCenter = {},
        onCreateSuccess = {}
    )
}

val sampleOfficeList = List(10) {
    Office(name = "Office $it")
}