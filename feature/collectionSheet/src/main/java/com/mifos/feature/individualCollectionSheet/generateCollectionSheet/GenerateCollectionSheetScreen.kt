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

package com.mifos.feature.individualCollectionSheet.generateCollectionSheet

import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.dbobjects.collectionsheet.CenterDetail
import com.mifos.core.dbobjects.collectionsheet.CollectionSheetPayload
import com.mifos.core.dbobjects.collectionsheet.CollectionSheetResponse
import com.mifos.core.dbobjects.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.core.dbobjects.group.Center
import com.mifos.core.dbobjects.group.Group
import com.mifos.core.dbobjects.organisation.Office
import com.mifos.core.dbobjects.organisation.Staff
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.objects.collectionsheets.CollectionSheetRequestPayload
import com.mifos.feature.collection_sheet.R
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
internal fun GenerateCollectionSheetScreen(
    onBackPressed: () -> Unit,
    viewModel: GenerateCollectionSheetViewModel = hiltViewModel(),
) {
    val state by viewModel.generateCollectionSheetUiState.collectAsStateWithLifecycle()
    val officeList by viewModel.officeListState.collectAsStateWithLifecycle()
    val staffList by viewModel.staffListState.collectAsStateWithLifecycle()
    val centerList by viewModel.centerListState.collectAsStateWithLifecycle()
    val groupList by viewModel.groupListState.collectAsStateWithLifecycle()
    val collectionSheetState by viewModel.collectionSheetState.collectAsStateWithLifecycle()
    val centerDetailsState by viewModel.centerDetailsState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadOffices()
    }

    GenerateCollectionSheetScreen(
        state = state,
        collectionSheetState = collectionSheetState,
        centerDetailsState = centerDetailsState,
        onBackPressed = onBackPressed,
        onRetry = {
        },
        officeList = officeList,
        staffList = staffList,
        centerList = centerList,
        groupList = groupList,
        onOfficeSelected = { officeId ->
            viewModel.loadStaffInOffice(officeId)
        },
        onStaffSelected = { officeId, staffId ->
            viewModel.loadCentersInOffice(officeId, staffId)
            viewModel.loadGroupsInOffice(officeId, staffId)
        },
        onCenterDetails = { repaymentDate, officeId, staffId ->
            viewModel.loadCenterDetails(repaymentDate, officeId, staffId)
        },
        loadProductiveCollectionSheet = { centerId, payload ->
            viewModel.loadProductiveCollectionSheet(centerId, payload)
        },
        loadCollectionSheet = { centerId, payload ->
            viewModel.loadCollectionSheet(centerId, payload)
        },
        submitProductiveCollectionSheet = { centerId, payload ->
            viewModel.submitProductiveSheet(centerId, payload)
        },
        submitCollectionSheet = { centerId, payload ->
            viewModel.submitCollectionSheet(centerId, payload)
        },
    )
}

@Composable
internal fun GenerateCollectionSheetScreen(
    state: GenerateCollectionSheetUiState,
    collectionSheetState: CollectionSheetResponse?,
    centerDetailsState: List<CenterDetail>?,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    officeList: List<Office>,
    staffList: List<Staff>,
    centerList: List<Center>,
    groupList: List<Group>,
    onOfficeSelected: (Int) -> Unit,
    onStaffSelected: (Int, Int) -> Unit,
    onCenterDetails: (String, Int, Int) -> Unit,
    loadProductiveCollectionSheet: (Int, CollectionSheetRequestPayload) -> Unit,
    loadCollectionSheet: (Int, CollectionSheetRequestPayload) -> Unit,
    submitProductiveCollectionSheet: (Int, ProductiveCollectionSheetPayload) -> Unit,
    modifier: Modifier = Modifier,
    submitCollectionSheet: (Int, CollectionSheetPayload) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        modifier = modifier,
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_collection_sheet_generate_collection_sheet),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (state) {
                is GenerateCollectionSheetUiState.Error -> MifosSweetError(
                    message = stringResource(
                        id = state.message,
                    ),
                ) {
                    onRetry()
                }

                is GenerateCollectionSheetUiState.Loading -> MifosCircularProgress()

                is GenerateCollectionSheetUiState.CollectionSheetSuccess -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = R.string.feature_collection_sheet_collection_sheet_submitted),
                        Toast.LENGTH_SHORT,
                    ).show()
                    onBackPressed()
                }

                is GenerateCollectionSheetUiState.ProductiveSheetSuccess -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = R.string.feature_collection_sheet_productive_sheet_submitted),
                        Toast.LENGTH_SHORT,
                    ).show()
                    onBackPressed()
                }

                GenerateCollectionSheetUiState.Success -> {
                    GenerateCollectionSheetContent(
                        centerDetailsState = centerDetailsState,
                        officeList = officeList,
                        staffList = staffList,
                        centerList = centerList,
                        groupList = groupList,
                        collectionSheetState = collectionSheetState,
                        onOfficeSelected = onOfficeSelected,
                        onStaffSelected = onStaffSelected,
                        onCenterDetails = onCenterDetails,
                        loadProductiveCollectionSheet = loadProductiveCollectionSheet,
                        loadCollectionSheet = loadCollectionSheet,
                        submitProductiveCollectionSheet = submitProductiveCollectionSheet,
                        submitCollectionSheet = submitCollectionSheet,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenerateCollectionSheetContent(
    centerDetailsState: List<CenterDetail>?,
    officeList: List<Office>,
    staffList: List<Staff>,
    centerList: List<Center>,
    groupList: List<Group>,
    collectionSheetState: CollectionSheetResponse?,
    onOfficeSelected: (Int) -> Unit,
    onStaffSelected: (Int, Int) -> Unit,
    onCenterDetails: (String, Int, Int) -> Unit,
    loadProductiveCollectionSheet: (Int, CollectionSheetRequestPayload) -> Unit,
    loadCollectionSheet: (Int, CollectionSheetRequestPayload) -> Unit,
    submitProductiveCollectionSheet: (Int, ProductiveCollectionSheetPayload) -> Unit,
    modifier: Modifier = Modifier,
    submitCollectionSheet: (Int, CollectionSheetPayload) -> Unit,
) {
    var selectedOffice by remember { mutableStateOf("") }
    var selectedOfficeId by remember { mutableIntStateOf(0) }
    var selectedStaff by remember { mutableStateOf("") }
    var selectedStaffId by remember { mutableIntStateOf(0) }
    var selectedCenter by remember { mutableStateOf("") }
    var selectedCenterId by remember { mutableIntStateOf(0) }
    var selectedGroup by remember { mutableStateOf("") }
    var selectedGroupId by remember { mutableIntStateOf(0) }

    var selectedAttendanceType by remember { mutableStateOf("") }
    var selectedAttendanceTypeId by remember { mutableIntStateOf(0) }
    var selectedPaymentType by remember { mutableStateOf("") }
    var selectedPaymentTypeId by remember { mutableIntStateOf(0) }

    var calendarId by rememberSaveable { mutableStateOf<Int?>(null) }
    var productiveCenterId by rememberSaveable { mutableStateOf<Int?>(null) }

    var repaymentDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = repaymentDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        },
    )
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var isProductiveResponse by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(centerDetailsState) {
        centerDetailsState?.let {
            if (it.isNotEmpty() && it[0].meetingFallCenters?.isNotEmpty() == true) {
                calendarId = it[0].meetingFallCenters?.get(0)?.collectionMeetingCalendar?.id
                productiveCenterId = it[0].meetingFallCenters?.get(0)?.id
            }

            val payload = CollectionSheetRequestPayload().apply {
                transactionDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                    repaymentDate,
                )
                this.calendarId = calendarId
            }
            productiveCenterId?.let { it1 -> loadProductiveCollectionSheet(it1, payload) }
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
                            repaymentDate = it
                        }
                    },
                ) { Text(stringResource(id = R.string.feature_collection_sheet_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    },
                ) { Text(stringResource(id = R.string.feature_collection_sheet_cancel)) }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.heightIn(16.dp))

        MifosTextFieldDropdown(
            value = selectedOffice,
            onValueChanged = {
                selectedOffice = it
            },
            onOptionSelected = { index, value ->
                selectedOffice = value
                officeList[index].id?.let {
                    selectedOfficeId = it
                    onOfficeSelected(it)
                }
            },
            options = officeList.map { it.name.toString() },
            label = R.string.feature_collection_sheet_office,
            readOnly = true,
        )

        Spacer(modifier = Modifier.heightIn(8.dp))

        MifosDatePickerTextField(
            value = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                repaymentDate,
            ),
            label = R.string.feature_collection_sheet_repayment_date,
            openDatePicker = {
                showDatePicker = true
            },
        )

        Spacer(modifier = Modifier.heightIn(8.dp))

        MifosTextFieldDropdown(
            value = selectedStaff,
            onValueChanged = {
                selectedStaff = it
            },
            onOptionSelected = { index, value ->
                selectedStaff = value
                staffList[index].id?.let {
                    selectedStaffId = it
                }
                onStaffSelected(selectedOfficeId, selectedStaffId)
            },
            options = staffList.map { it.displayName.toString() },
            label = R.string.feature_collection_sheet_staff,
            readOnly = true,
        )

        Spacer(modifier = Modifier.heightIn(16.dp))

        Button(
            onClick = {
                onCenterDetails(
                    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                        repaymentDate,
                    ),
                    selectedOfficeId,
                    selectedStaffId,
                )
                isProductiveResponse = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(44.dp)
                .padding(start = 16.dp, end = 16.dp),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
            ),
        ) {
            Text(
                text = stringResource(id = R.string.feature_collection_sheet_productive_collection_sheet),
                fontSize = 16.sp,
            )
        }

        Spacer(modifier = Modifier.heightIn(16.dp))

        MifosTextFieldDropdown(
            value = selectedCenter,
            onValueChanged = {
                selectedCenter = it
            },
            onOptionSelected = { index, value ->
                selectedCenter = value
                centerList[index].id?.let {
                    selectedCenterId = it
                }
            },
            options = centerList.map { it.name.toString() },
            label = R.string.feature_collection_sheet_center,
            readOnly = true,
        )

        Spacer(modifier = Modifier.heightIn(8.dp))

        MifosTextFieldDropdown(
            value = selectedGroup,
            onValueChanged = {
                selectedGroup = it
            },
            onOptionSelected = { index, value ->
                selectedGroup = value
                groupList[index].id?.let {
                    selectedGroupId = it
                }
            },
            options = groupList.map { it.name.toString() },
            label = R.string.feature_collection_sheet_group,
            readOnly = true,
        )

        Spacer(modifier = Modifier.heightIn(16.dp))

        Button(
            onClick = {
                val payload = CollectionSheetRequestPayload().apply {
                    transactionDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                        repaymentDate,
                    )
                    this.calendarId = calendarId
                }
                isProductiveResponse = false
                loadCollectionSheet(selectedGroupId, payload)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(44.dp)
                .padding(start = 16.dp, end = 16.dp),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
            ),
        ) {
            Text(
                text = stringResource(id = R.string.feature_collection_sheet_generate_collection_sheet),
                fontSize = 16.sp,
            )
        }

        collectionSheetState?.let { collectionSheet ->

            // TODO The API is currently not working properly, therefore we will implement it later.
            //  I have included prior implementation code below in the comment.

            MifosTextFieldDropdown(
                value = selectedAttendanceType,
                onValueChanged = {
                    selectedAttendanceType = it
                },
                onOptionSelected = { index, value ->
                    selectedAttendanceType = value
                    selectedAttendanceTypeId = collectionSheet.attendanceTypeOptions[index].id
                },
                options = collectionSheet.attendanceTypeOptions.map { it.value.toString() },
                label = R.string.feature_collection_sheet_attendance_type,
            )

            MifosTextFieldDropdown(
                value = selectedPaymentType,
                onValueChanged = {
                    selectedPaymentType = it
                },
                onOptionSelected = { index, value ->
                    selectedPaymentType = value
                    selectedPaymentTypeId = collectionSheet.paymentTypeOptions[index].id
                },
                options = collectionSheet.paymentTypeOptions.map { it.name },
                label = R.string.feature_collection_sheet_payment_type,
            )

            collectionSheet.loanProducts.forEach {
                Text(text = "Loan: " + it.name.toString())
            }

            collectionSheet.savingsProducts.forEach {
                Text(text = "Savings: " + it.name.toString())
            }

            collectionSheet.groups.first().clients.forEach {
                Text(text = "Client: " + it.clientName + "  " + it.clientId)
                collectionSheet.loanProducts.forEach {
                }
            }

            Button(
                onClick = {
                    if (isProductiveResponse) {
                        val payload = ProductiveCollectionSheetPayload().apply {
                            this.calendarId = calendarId
                            this.transactionDate =
                                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                                    repaymentDate,
                                )
                        }
                        submitProductiveCollectionSheet(selectedCenterId, payload)
                    } else {
                        val payload = CollectionSheetPayload().apply {
                            this.calendarId = calendarId
                            this.transactionDate =
                                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                                    repaymentDate,
                                )
                            this.actualDisbursementDate =
                                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
                                    repaymentDate,
                                )
                        }
                        submitCollectionSheet(selectedGroupId, payload)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(44.dp)
                    .padding(start = 16.dp, end = 16.dp),
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.feature_collection_sheet_submit_collection_sheet),
                    fontSize = 16.sp,
                )
            }
        }
    }
}

class GenerateCollectionUiStateProvider : PreviewParameterProvider<GenerateCollectionSheetUiState> {

    override val values: Sequence<GenerateCollectionSheetUiState>
        get() = sequenceOf(
            GenerateCollectionSheetUiState.Success,
            GenerateCollectionSheetUiState.Loading,
            GenerateCollectionSheetUiState.Error(R.string.feature_collection_sheet_failed_to_load_office),
            GenerateCollectionSheetUiState.CollectionSheetSuccess,
            GenerateCollectionSheetUiState.ProductiveSheetSuccess,
        )
}

@Preview(showBackground = true)
@Composable
private fun GenerateCollectionSheetScreenPreview(
    @PreviewParameter(GenerateCollectionUiStateProvider::class) state: GenerateCollectionSheetUiState,
) {
    GenerateCollectionSheetScreen(
        state = state,
        collectionSheetState = CollectionSheetResponse(),
        centerDetailsState = emptyList(),
        onBackPressed = { },
        onRetry = { },
        officeList = emptyList(),
        staffList = emptyList(),
        centerList = emptyList(),
        groupList = emptyList(),
        onOfficeSelected = { },
        onStaffSelected = { _, _ -> },
        onCenterDetails = { _, _, _ -> },
        loadProductiveCollectionSheet = { _, _ -> },
        loadCollectionSheet = { _, _ -> },
        submitProductiveCollectionSheet = { _, _ -> },
        submitCollectionSheet = { _, _ -> },
    )
}

// TODO Previous implementation of logic

//    private fun submitCollectionSheet() {
//        val payload = CollectionSheetPayload()
//        payload.calendarId = calendarId
//        payload.transactionDate = binding.meetingDateFieldContainer.editText?.text.toString()
//        payload.actualDisbursementDate = binding.meetingDateFieldContainer.editText?.text.toString()
//        for (i in 0 until binding.tableSheet.childCount) {
//            //In the tableRows which depicts the details of that client.
//            //Loop through all the view of this TableRows.
//            val row = binding.tableSheet.getChildAt(i) as TableRow
//            for (j in 0 until row.childCount) {
//                //In a particular TableRow
//                //Loop through the views and check if it's LinearLayout
//                //because the required views - EditTexts are there only.
//                val v = row.getChildAt(j)
//                if (v is LinearLayout) {
//                    //So, we got into the container containing the EditTexts
//                    //Now, extract the values and the loanId associated to this
//                    //particular TextView which was set as its Tag.
//                    for (k in 0 until v.childCount) {
//                        val et = v.getChildAt(k) as EditText
//                        val typeId = et.tag.toString().split(":").toTypedArray()
//                        when (typeId[0]) {
//                            TYPE_LOAN -> {
//                                val loanId = typeId[1].toInt()
//                                val amount = et.text.toString().toDouble()
//                                payload.bulkRepaymentTransactions.add(
//                                    BulkRepaymentTransactions(loanId, amount)
//                                )
//                            }
//
//                            TYPE_SAVING -> {
//                                //Add to Savings
//                                val savingsId = typeId[1].toInt()
//                                val amountSaving = et.text.toString()
//                                payload.bulkSavingsDueTransactions.add(
//                                    BulkSavingsDueTransaction(savingsId, amountSaving)
//                                )
//                            }
//                        }
//                    }
//                } else if (v is Spinner) {
//                    //Attendance
//                    val clientId = v.tag as Int
//                    val attendanceTypeId = attendanceTypeOptions[v.selectedItem.toString()]!!
//                    payload.clientsAttendance
//                        .add(ClientsAttendance(clientId, attendanceTypeId))
//                }
//            }
//        }
//
//        //Check if Additional details are there
//        if (binding.tableAdditional.childCount > 0) {
//            for (i in 0..5) {
//                val row = binding.tableAdditional.getChildAt(i) as TableRow
//                val v = row.getChildAt(1)
//                if (v is Spinner) {
//                    val paymentId = additionalPaymentTypeMap[v.selectedItem.toString()]!!
//                    if (paymentId != -1) {
//                        payload.paymentTypeId = paymentId
//                    }
//                } else if (v is EditText) {
//                    val value = v.text.toString()
//                    if (value != "") {
//                        when (i) {
//                            1 -> payload.accountNumber = value
//                            2 -> payload.checkNumber = value
//                            3 -> payload.routingCode = value
//                            4 -> payload.receiptNumber = value
//                            5 -> payload.bankNumber = value
//                        }
//                    }
//                }
//            }
//        }
//
//        //Payload with all the items is ready. Now, hit the endpoint and submit it.
//        viewModel.submitCollectionSheet(groupId, payload)
//    }

//    private fun submitProductiveSheet() {
//        val payload = ProductiveCollectionSheetPayload()
//        payload.calendarId = calendarId
//        payload.transactionDate = binding.meetingDateFieldContainer.editText?.text.toString()
//        for (i in 0 until binding.tableSheet.childCount) {
//            //In the tableRows which depicts the details of that client.
//            //Loop through all the view of this TableRows.
//            val row = binding.tableSheet.getChildAt(i) as TableRow
//            for (j in 0 until row.childCount) {
//                //In a particular TableRow
//                //Loop through the views and check if it's LinearLayout
//                //because the required views - EditTexts are there only.
//                val v = row.getChildAt(j)
//                if (v is LinearLayout) {
//                    //So, we got into the container containing the EditTexts
//                    //Now, extract the values and the loanId associated to this
//                    //particular TextView which was set as its Tag.
//                    for (k in 0 until v.childCount) {
//                        val et = v.getChildAt(k) as EditText
//                        val typeId = et.tag.toString().split(":").toTypedArray()
//                        when (typeId[0]) {
//                            TYPE_LOAN -> {
//                                val loanId = typeId[1].toInt()
//                                val amount = et.text.toString().toDouble()
//                                payload.bulkRepaymentTransactions.add(
//                                    BulkRepaymentTransactions(loanId, amount)
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        //Payload with all the items is ready. Now, hit the endpoint and submit it.
//        productiveCenterId?.let { viewModel.submitProductiveSheet(it, payload) }
//    }

//    private fun inflateProductiveCollectionTable(collectionSheetResponse: CollectionSheetResponse?) {
//
//        //Clear old views in case they are present.
//        if (binding.tableSheet.childCount > 0) {
//            binding.tableSheet.removeAllViews()
//        }
//        if (binding.tableAdditional.visibility == View.VISIBLE) {
//            binding.tableAdditional.removeAllViews()
//            binding.tableAdditional.visibility = View.GONE
//        }
//
//        //A List to be used to inflate Attendance Spinners
//        val attendanceTypes = ArrayList<String?>()
//        attendanceTypeOptions.clear()
//        attendanceTypeOptions = viewModel.filterAttendanceTypes(
//            collectionSheetResponse
//                ?.attendanceTypeOptions, attendanceTypes
//        )
//
//        //Add the heading Row
//        val headingRow = TableRow(context)
//        val headingRowParams = TableRow.LayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        headingRowParams.gravity = Gravity.CENTER
//        headingRowParams.setMargins(0, 0, 0, 10)
//        headingRow.layoutParams = headingRowParams
//        val tvGroupName = TextView(context)
//        tvGroupName.text = collectionSheetResponse!!.groups[0].groupName
//        tvGroupName.setTypeface(tvGroupName.typeface, Typeface.BOLD)
//        tvGroupName.gravity = Gravity.CENTER
//        headingRow.addView(tvGroupName)
//        for (loanProduct in collectionSheetResponse.loanProducts) {
//            val tvProduct = TextView(context)
//            tvProduct.text = getString(
//                R.string.collection_heading_charges,
//                loanProduct.name
//            )
//            tvProduct.setTypeface(tvProduct.typeface, Typeface.BOLD)
//            tvProduct.gravity = Gravity.CENTER
//            headingRow.addView(tvProduct)
//        }
//        val tvAttendance = TextView(context)
//        tvAttendance.text = getString(R.string.attendance)
//        tvAttendance.gravity = Gravity.CENTER
//        tvAttendance.setTypeface(tvAttendance.typeface, Typeface.BOLD)
//        headingRow.addView(tvAttendance)
//        binding.tableSheet.addView(headingRow)
//        for (clientCollectionSheet in collectionSheetResponse.groups[0].clients) {
//            //Insert rows
//            val row = TableRow(context)
//            val rowParams = TableRow.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            rowParams.gravity = Gravity.CENTER
//            rowParams.setMargins(0, 0, 0, 10)
//            row.layoutParams = rowParams
//
//
//            //Column 1: Client Name and Id
//            val tvClientName = TextView(context)
//            tvClientName.text = clientCollectionSheet.clientName?.let {
//                concatIdWithName(
//                    it,
//                    clientCollectionSheet.clientId
//                )
//            }
//            row.addView(tvClientName)
//
//            //Subsequent columns: The Loan products
//            for (loanProduct in collectionSheetResponse.loanProducts) {
//                //Since there may be several items in this column, create a container.
//                val productContainer = LinearLayout(context)
//                productContainer.orientation = LinearLayout.HORIZONTAL
//
//                //Iterate through all the loans in of this type and add in the container
//                for (loanCollectionSheet in clientCollectionSheet.loans!!) {
//                    if (loanProduct.name == loanCollectionSheet.productShortName) {
//                        //This loan should be shown in this column. So, add it in the container.
//                        val editText = EditText(context)
//                        editText.inputType = InputType.TYPE_CLASS_NUMBER or
//                                InputType.TYPE_NUMBER_FLAG_DECIMAL
//                        editText.setText(String.format(Locale.getDefault(), "%f", 0.0))
//                        //Set the loan id as the Tag of the EditText which
//                        //will later be used as the identifier for this.
//                        editText.tag = TYPE_LOAN + ":" + loanCollectionSheet.loanId
//                        productContainer.addView(editText)
//                    }
//                }
//                row.addView(productContainer)
//            }
//            binding.tableSheet.addView(row)
//        }
//        if (binding.btnSubmitProductive.visibility != View.VISIBLE) {
//            //Show the button the first time sheet is loaded.
//            binding.btnSubmitProductive.visibility = View.VISIBLE
//            binding.btnSubmitProductive.setOnClickListener(this)
//        }
//
//        //If this block has been executed, that the CollectionSheet
//        //which is already shown on screen is for center - Productive.
//        binding.btnSubmitProductive.tag = TAG_TYPE_PRODUCTIVE
//    }

//    private fun inflateCollectionTable(collectionSheetResponse: CollectionSheetResponse?) {
//        //Clear old views in case they are present.
//        if (binding.tableSheet.childCount > 0) {
//            binding.tableSheet.removeAllViews()
//        }
//
//        //A List to be used to inflate Attendance Spinners
//        val attendanceTypes = ArrayList<String?>()
//        attendanceTypeOptions.clear()
//        attendanceTypeOptions = viewModel.filterAttendanceTypes(
//            collectionSheetResponse
//                ?.attendanceTypeOptions, attendanceTypes
//        )
//        additionalPaymentTypeMap.clear()
//        additionalPaymentTypeMap = viewModel.filterPaymentTypes(
//            collectionSheetResponse
//                ?.paymentTypeOptions, paymentTypes as MutableList<String?>
//        )
//
//        //Add the heading Row
//        val headingRow = TableRow(context)
//        val headingRowParams = TableRow.LayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        headingRowParams.gravity = Gravity.CENTER
//        headingRowParams.setMargins(0, 0, 0, 10)
//        headingRow.layoutParams = headingRowParams
//        val tvGroupName = TextView(context)
//        tvGroupName.text = collectionSheetResponse!!.groups[0].groupName
//        tvGroupName.setTypeface(tvGroupName.typeface, Typeface.BOLD)
//        tvGroupName.gravity = Gravity.CENTER
//        headingRow.addView(tvGroupName)
//        for (loanProduct in collectionSheetResponse.loanProducts) {
//            val tvProduct = TextView(context)
//            tvProduct.text = getString(R.string.collection_loan_product, loanProduct.name)
//            tvProduct.setTypeface(tvProduct.typeface, Typeface.BOLD)
//            tvProduct.gravity = Gravity.CENTER
//            headingRow.addView(tvProduct)
//        }
//        for (savingsProduct in collectionSheetResponse.savingsProducts) {
//            val tvSavingProduct = TextView(context)
//            tvSavingProduct.text = getString(
//                R.string.collection_saving_product,
//                savingsProduct.name
//            )
//            tvSavingProduct.setTypeface(tvSavingProduct.typeface, Typeface.BOLD)
//            tvSavingProduct.gravity = Gravity.CENTER
//            headingRow.addView(tvSavingProduct)
//        }
//        val tvAttendance = TextView(context)
//        tvAttendance.text = getString(R.string.attendance)
//        tvAttendance.gravity = Gravity.CENTER
//        tvAttendance.setTypeface(tvAttendance.typeface, Typeface.BOLD)
//        headingRow.addView(tvAttendance)
//        binding.tableSheet.addView(headingRow)
//        for (clientCollectionSheet in collectionSheetResponse
//            .groups[0].clients) {
//            //Insert rows
//            val row = TableRow(context)
//            val rowParams = TableRow.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            rowParams.gravity = Gravity.CENTER
//            rowParams.setMargins(0, 0, 0, 10)
//            row.layoutParams = rowParams
//
//
//            //Column 1: Client Name and Id
//            val tvClientName = TextView(context)
//            tvClientName.text = clientCollectionSheet.clientName?.let {
//                concatIdWithName(
//                    it,
//                    clientCollectionSheet.clientId
//                )
//            }
//            row.addView(tvClientName)
//
//            //Subsequent columns: The Loan products
//            for (loanProduct in collectionSheetResponse.loanProducts) {
//                //Since there may be several items in this column, create a container.
//                val productContainer = LinearLayout(context)
//                productContainer.orientation = LinearLayout.HORIZONTAL
//
//                //Iterate through all the loans in of this type and add in the container
//                for (loan in clientCollectionSheet.loans!!) {
//                    if (loanProduct.name == loan.productShortName) {
//                        //This loan should be shown in this column. So, add it in the container.
//                        val editText = EditText(context)
//                        editText.inputType = InputType.TYPE_CLASS_NUMBER or
//                                InputType.TYPE_NUMBER_FLAG_DECIMAL
//                        editText.setText(String.format(Locale.getDefault(), "%f", 0.0))
//                        //Set the loan id as the Tag of the EditText
//                        // in format 'TYPE:ID' which
//                        //will later be used as the identifier for this.
//                        editText.tag = TYPE_LOAN + ":" + loan.loanId
//                        productContainer.addView(editText)
//                    }
//                }
//                row.addView(productContainer)
//            }
//
//            //After Loans, show Savings columns
//            for (product in collectionSheetResponse.savingsProducts) {
//                //Since there may be several Savings items in this column, create a container.
//                val productContainer = LinearLayout(context)
//                productContainer.orientation = LinearLayout.HORIZONTAL
//
//                //Iterate through all the Savings in of this type and add in the container
//                for (saving in clientCollectionSheet.savings) {
//                    if (saving.productId == product.id) {
//                        //Add the saving in the container
//                        val editText = EditText(context)
//                        editText.inputType = InputType.TYPE_CLASS_NUMBER or
//                                InputType.TYPE_NUMBER_FLAG_DECIMAL
//                        editText.setText(String.format(Locale.getDefault(), "%f", 0.0))
//                        //Set the Saving id as the Tag of the EditText
//                        // in 'TYPE:ID' format which
//                        //will later be used as the identifier for this.
//                        editText.tag = TYPE_SAVING + ":" + saving.savingsId
//                        productContainer.addView(editText)
//                    }
//                }
//                row.addView(productContainer)
//            }
//            binding.tableSheet.addView(row)
//        }
//        if (binding.btnSubmitProductive.visibility != View.VISIBLE) {
//            //Show the button the first time sheet is loaded.
//            binding.btnSubmitProductive.visibility = View.VISIBLE
//            binding.btnSubmitProductive.setOnClickListener(this)
//        }
//
//        //If this block has been executed, that means the CollectionSheet
//        //which is already shown is for groups.
//        binding.btnSubmitProductive.tag = TAG_TYPE_COLLECTION
//        if (binding.tableAdditional.visibility != View.VISIBLE) {
//            binding.tableAdditional.visibility = View.VISIBLE
//        }
//        //Show Additional Views
//        val rowPayment = TableRow(context)
//        val tvLabelPayment = TextView(context)
//        tvLabelPayment.text = getString(R.string.payment_type)
//        rowPayment.addView(tvLabelPayment)
//        binding.tableAdditional.addView(rowPayment)
//        val rowAccount = TableRow(context)
//        val tvLabelAccount = TextView(context)
//        tvLabelAccount.text = getString(R.string.account_number)
//        rowAccount.addView(tvLabelAccount)
//        val etPayment = EditText(context)
//        rowAccount.addView(etPayment)
//        binding.tableAdditional.addView(rowAccount)
//        val rowCheck = TableRow(context)
//        val tvLabelCheck = TextView(context)
//        tvLabelCheck.text = getString(R.string.cheque_number)
//        rowCheck.addView(tvLabelCheck)
//        val etCheck = EditText(context)
//        rowCheck.addView(etCheck)
//        binding.tableAdditional.addView(rowCheck)
//        val rowRouting = TableRow(context)
//        val tvLabelRouting = TextView(context)
//        tvLabelRouting.text = getString(R.string.routing_code)
//        rowRouting.addView(tvLabelRouting)
//        val etRouting = EditText(context)
//        rowRouting.addView(etRouting)
//        binding.tableAdditional.addView(rowRouting)
//        val rowReceipt = TableRow(context)
//        val tvLabelReceipt = TextView(context)
//        tvLabelReceipt.text = getString(R.string.receipt_number)
//        rowReceipt.addView(tvLabelReceipt)
//        val etReceipt = EditText(context)
//        rowReceipt.addView(etReceipt)
//        binding.tableAdditional.addView(rowReceipt)
//        val rowBank = TableRow(context)
//        val tvLabelBank = TextView(context)
//        tvLabelBank.text = getString(R.string.bank_number)
//        rowBank.addView(tvLabelBank)
//        val etBank = EditText(context)
//        rowBank.addView(etBank)
//        binding.tableAdditional.addView(rowBank)
//    }
