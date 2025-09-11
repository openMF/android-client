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

import androidclient.feature.collectionsheet.generated.resources.Res
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_attendance_type
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_cancel
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_center
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_collection_sheet_submitted
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_generate_collection_sheet
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_group
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_office
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_payment_type
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_productive_collection_sheet
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_productive_sheet_submitted
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_repayment_date
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_select
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_staff
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_submit_collection_sheet
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.model.objects.collectionsheets.CollectionSheetRequestPayload
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.room.entities.collectionsheet.CenterDetail
import com.mifos.room.entities.collectionsheet.CollectionSheetPayload
import com.mifos.room.entities.collectionsheet.CollectionSheetResponse
import com.mifos.room.entities.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun GenerateCollectionSheetScreen(
    onBackPressed: () -> Unit,
    viewModel: GenerateCollectionSheetViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val officeList by viewModel.officeList.collectAsStateWithLifecycle()
    val staffList by viewModel.staffList.collectAsStateWithLifecycle()
    val centerList by viewModel.centerList.collectAsStateWithLifecycle()
    val groupList by viewModel.groupList.collectAsStateWithLifecycle()
    val collectionSheetState by viewModel.collectionSheet.collectAsStateWithLifecycle()
    val centerDetailsState by viewModel.centerDetails.collectAsStateWithLifecycle()

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
    officeList: List<OfficeEntity>,
    staffList: List<StaffEntity>,
    centerList: List<CenterEntity>,
    groupList: List<GroupEntity>,
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
    val scope = rememberCoroutineScope()

    MifosScaffold(
        modifier = modifier,
        title = stringResource(Res.string.feature_collection_sheet_generate_collection_sheet),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (state) {
                is GenerateCollectionSheetUiState.Error -> MifosSweetError(
                    message = state.message,
                ) {
                    onRetry()
                }

                is GenerateCollectionSheetUiState.Loading -> MifosProgressIndicator()

                is GenerateCollectionSheetUiState.CollectionSheetSuccess -> {
                    val message = stringResource(Res.string.feature_collection_sheet_collection_sheet_submitted)
                    scope.launch {
                        snackbarHostState.showSnackbar(message = message)
                        onBackPressed()
                    }
                }

                is GenerateCollectionSheetUiState.ProductiveSheetSuccess -> {
                    val message = stringResource(Res.string.feature_collection_sheet_productive_sheet_submitted)
                    scope.launch {
                        snackbarHostState.showSnackbar(message = message)
                        onBackPressed()
                    }
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun GenerateCollectionSheetContent(
    centerDetailsState: List<CenterDetail>?,
    officeList: List<OfficeEntity>,
    staffList: List<StaffEntity>,
    centerList: List<CenterEntity>,
    groupList: List<GroupEntity>,
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

    var repaymentDate by rememberSaveable { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = repaymentDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
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
                transactionDate = DateHelper.getDateAsStringFromLong(repaymentDate)
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
                ) { Text(stringResource(Res.string.feature_collection_sheet_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    },
                ) { Text(stringResource(Res.string.feature_collection_sheet_cancel)) }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.heightIn(16.dp))

        MifosTextFieldDropdown(
            value = selectedOffice,
            onValueChanged = {
                selectedOffice = it
            },
            onOptionSelected = { index, value ->
                selectedOffice = value
                officeList[index].id.let {
                    selectedOfficeId = it
                    onOfficeSelected(it)
                }
            },
            options = officeList.map { it.name.toString() },
            label = stringResource(Res.string.feature_collection_sheet_office),
            readOnly = true,
        )

        Spacer(modifier = Modifier.heightIn(8.dp))

        MifosDatePickerTextField(
            value = DateHelper.getDateAsStringFromLong(repaymentDate),
            label = stringResource(Res.string.feature_collection_sheet_repayment_date),
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
            label = stringResource(Res.string.feature_collection_sheet_staff),
            readOnly = true,
        )

        Spacer(modifier = Modifier.heightIn(16.dp))

        MifosButton(
            onClick = {
                onCenterDetails(
                    DateHelper.getDateAsStringFromLong(repaymentDate),
                    selectedOfficeId,
                    selectedStaffId,
                )
                isProductiveResponse = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(44.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(),
        ) {
            Text(
                text = stringResource(
                    Res.string
                        .feature_collection_sheet_productive_collection_sheet,
                ),
                style = MaterialTheme.typography.bodyLarge,
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
            label = stringResource(Res.string.feature_collection_sheet_center),
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
            label = stringResource(Res.string.feature_collection_sheet_group),
            readOnly = true,
        )

        Spacer(modifier = Modifier.heightIn(16.dp))

        MifosButton(
            onClick = {
                val payload = CollectionSheetRequestPayload().apply {
                    transactionDate = DateHelper.getDateAsStringFromLong(repaymentDate)
                    this.calendarId = calendarId
                }
                isProductiveResponse = false
                loadCollectionSheet(selectedGroupId, payload)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(44.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(),
        ) {
            Text(
                text = stringResource(
                    Res.string
                        .feature_collection_sheet_generate_collection_sheet,
                ),
                style = MaterialTheme.typography.bodyLarge,
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
                label = stringResource(Res.string.feature_collection_sheet_attendance_type),
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
                label = stringResource(Res.string.feature_collection_sheet_payment_type),
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

            MifosButton(
                onClick = {
                    if (isProductiveResponse) {
                        val payload = ProductiveCollectionSheetPayload().apply {
                            this.calendarId = calendarId
                            this.transactionDate =
                                DateHelper.getDateAsStringFromLong(repaymentDate)
                        }
                        submitProductiveCollectionSheet(selectedCenterId, payload)
                    } else {
                        val payload = CollectionSheetPayload().apply {
                            this.calendarId = calendarId
                            this.transactionDate =
                                DateHelper.getDateAsStringFromLong(repaymentDate)
                            this.actualDisbursementDate =
                                DateHelper.getDateAsStringFromLong(repaymentDate)
                        }
                        submitCollectionSheet(selectedGroupId, payload)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(44.dp)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(),
            ) {
                Text(
                    text = stringResource(
                        Res.string
                            .feature_collection_sheet_submit_collection_sheet,
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}
