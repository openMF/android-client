@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.individual_collection_sheet.new_individual_collection_sheet.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.feature.collection_sheet.R
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NewIndividualCollectionSheetScreen(
    viewModel: NewIndividualCollectionSheetViewModel = hiltViewModel(),
    onDetail: (String, IndividualCollectionSheet) -> Unit
) {
    val state = viewModel.newIndividualCollectionSheetUiState.collectAsStateWithLifecycle().value

    NewIndividualCollectionSheetScreen(
        state = state,
        getStaffList = {
            viewModel.getStaffList(it)
        },
        generateCollection = { _officeId, _staffId, _repaymentDate ->
            viewModel.getIndividualCollectionSheet(RequestCollectionSheetPayload().apply {
                officeId = _officeId
                transactionDate = _repaymentDate
                staffId = _staffId
            })
        },
        onDetail = onDetail
    )
}


@Composable
fun NewIndividualCollectionSheetScreen(
    state: NewIndividualCollectionSheetUiState,
    getStaffList: (Int) -> Unit,
    generateCollection: (Int, Int, String) -> Unit,
    onDetail: (String, IndividualCollectionSheet) -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    var selectedOffice by rememberSaveable { mutableStateOf("") }
    var selectedStaff by rememberSaveable { mutableStateOf("") }
    var officeId by rememberSaveable { mutableIntStateOf(0) }
    var staffId by rememberSaveable { mutableIntStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    var showCollectionSheetDialog by rememberSaveable { mutableStateOf(false) }

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var repaymentDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = repaymentDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )
    var individualCollectionSheet by rememberSaveable {
        mutableStateOf<IndividualCollectionSheet?>(
            null
        )
    }

    LaunchedEffect(key1 = state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    LaunchedEffect(key1 = state.individualCollectionSheet) {
        state.individualCollectionSheet?.let {
            individualCollectionSheet = it
            showCollectionSheetDialog = true
        }
    }

    MifosScaffold(
        topBar = { },
        snackbarHostState = snackbarHostState,
        bottomBar = { }
    ) { paddingValues ->
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
                                repaymentDate = it
                            }
                        }
                    ) { Text(stringResource(id = R.string.feature_collection_sheet_select)) }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                        }
                    ) { Text(stringResource(id = R.string.feature_collection_sheet_cancel)) }
                }
            )
            {
                DatePicker(state = datePickerState)
            }
        }
        if (showCollectionSheetDialog) {
            MifosBottomSheet(
                content = {
                    CollectionSheetDialogContent(
                        date = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                            repaymentDate
                        ),
                        member = individualCollectionSheet?.clients?.size.toString(),
                        fillNow = {
                            showCollectionSheetDialog = false
                            individualCollectionSheet?.let {
                                onDetail(
                                    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                                        repaymentDate
                                    ),
                                    it
                                )
                            }
                        },
                        onDismiss = {
                            showCollectionSheetDialog = false
                        })
                },
                onDismiss = {
                    showCollectionSheetDialog = false
                })
        }
        if (state.isLoading) {
            MifosCircularProgress()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(id = R.string.feature_collection_sheet_generate_new),
                    style = TextStyle(
                        fontSize = 24.sp
                    )
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    text = stringResource(id = R.string.feature_collection_sheet_fill_collection_sheet_message),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                MifosTextFieldDropdown(
                    value = selectedOffice,
                    onValueChanged = {
                        selectedOffice = it
                        selectedStaff = ""
                    },
                    onOptionSelected = { index, value ->
                        state.officeList[index].id?.let {
                            getStaffList(it)
                            officeId = it
                        }
                        selectedOffice = value
                        selectedStaff = ""
                    },
                    label = R.string.feature_collection_sheet_office,
                    options = state.officeList.map { it.name.toString() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                MifosDatePickerTextField(
                    value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                        repaymentDate
                    ),
                    label = R.string.feature_collection_sheet_repayment_date,
                    openDatePicker = {
                        showDatePicker = true
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                MifosTextFieldDropdown(
                    value = selectedStaff,
                    onValueChanged = {
                        selectedStaff = it
                    },
                    onOptionSelected = { index, value ->
                        state.staffList[index].id?.let {
                            staffId = it
                        }
                        selectedStaff = value
                    },
                    label = R.string.feature_collection_sheet_staff,
                    options = state.staffList.map { it.displayName.toString() }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            generateCollection(
                                officeId,
                                staffId,
                                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                                    repaymentDate
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        contentPadding = PaddingValues(),
                        enabled = selectedStaff != "" && selectedOffice != "",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.feature_collection_sheet_generate),
                            fontSize = 16.sp
                        )
                    }
                    Button(
                        onClick = {
                            selectedOffice = ""
                            repaymentDate = System.currentTimeMillis()
                            selectedStaff = ""
                            individualCollectionSheet = null
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        contentPadding = PaddingValues(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.feature_collection_sheet_clear),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CollectionSheetDialogContent(
    date: String,
    member: String,
    fillNow: () -> Unit,
    onDismiss: () -> Unit
) {
    MifosBottomSheet(content = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.feature_collection_sheet_found_sheet),
                style = TextStyle(
                    fontSize = 24.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.feature_collection_sheet_fill_collection_sheet_message),
                style = TextStyle(
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(
                    text = stringResource(id = R.string.feature_collection_sheet_date),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = date,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            Row {
                Text(
                    text = stringResource(id = R.string.feature_collection_sheet_member),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = member,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = fillNow,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_collection_sheet_fill_now),
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_collection_sheet_cancel),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }, onDismiss = onDismiss)
}

class NewIndividualCollectionSheetUiStateProvider :
    PreviewParameterProvider<NewIndividualCollectionSheetUiState> {

    override val values: Sequence<NewIndividualCollectionSheetUiState>
        get() = sequenceOf(
            NewIndividualCollectionSheetUiState(staffList = sampleStaffList),
            NewIndividualCollectionSheetUiState(officeList = sampleOfficeList),
            NewIndividualCollectionSheetUiState(error = "Error Occurred"),
            NewIndividualCollectionSheetUiState(isLoading = true),
            NewIndividualCollectionSheetUiState(individualCollectionSheet = IndividualCollectionSheet())
        )
}

@Preview(showBackground = true)
@Composable
private fun NewIndividualCollectionSheetPreview(
    @PreviewParameter(NewIndividualCollectionSheetUiStateProvider::class) newIndividualCollectionSheetUiState: NewIndividualCollectionSheetUiState
) {
    NewIndividualCollectionSheetScreen(
        state = newIndividualCollectionSheetUiState,
        getStaffList = {},
        generateCollection = { _, _, _ ->
        },
        onDetail = { _, _ -> }
    )
}

val sampleStaffList = List(10) {
    Staff(firstname = "FirstName", lastname = "LastName", isActive = true)
}

val sampleOfficeList = List(10) {
    Office(name = "Name")
}

@Preview
@Composable
private fun CollectionSheetDialogContentPreview() {
    CollectionSheetDialogContent(date = "19 June 2024", member = "5", fillNow = {}, onDismiss = {})
}