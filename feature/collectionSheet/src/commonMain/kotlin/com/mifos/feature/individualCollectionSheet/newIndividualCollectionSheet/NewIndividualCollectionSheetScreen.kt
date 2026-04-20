/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.individualCollectionSheet.newIndividualCollectionSheet

import androidclient.feature.collectionsheet.generated.resources.Res
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_cancel
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_clear
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_date
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_fill_collection_sheet_message
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_fill_now
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_found_sheet
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_generate
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_generate_new
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_member
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_office
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_repayment_date
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_select
import androidclient.feature.collectionsheet.generated.resources.feature_collection_sheet_staff
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.DevicePreview
import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun NewIndividualCollectionSheetScreen(
    viewModel: NewIndividualCollectionSheetViewModel = koinViewModel(),
    onDetail: (String, IndividualCollectionSheet) -> Unit,
) {
    val state = viewModel.newIndividualCollectionSheetUiState.collectAsStateWithLifecycle().value

    NewIndividualCollectionSheetScreen(
        state = state,
        getStaffList = {
            viewModel.getStaffList(it)
        },
        generateCollection = { id, idStaff, date ->
            viewModel.getIndividualCollectionSheet(
                RequestCollectionSheetPayload().apply {
                    officeId = id
                    transactionDate = date
                    staffId = idStaff
                    locale = ApiDateFormatter.LOCALE
                    dateFormat = ApiDateFormatter.DATE_FORMAT
                },
            )
        },
        onDetail = onDetail,
    )
}

@OptIn(ExperimentalTime::class)
@Composable
internal fun NewIndividualCollectionSheetScreen(
    state: NewIndividualCollectionSheetUiState,
    getStaffList: (Int) -> Unit,
    generateCollection: (Int, Int, String) -> Unit,
    modifier: Modifier = Modifier,
    onDetail: (String, IndividualCollectionSheet) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var selectedOffice by rememberSaveable { mutableStateOf("") }
    var selectedStaff by rememberSaveable { mutableStateOf("") }
    var officeId by rememberSaveable { mutableIntStateOf(0) }
    var staffId by rememberSaveable { mutableIntStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    var showCollectionSheetDialog by rememberSaveable { mutableStateOf(false) }

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var repaymentDate by rememberSaveable { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = repaymentDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )
    var individualCollectionSheet by rememberSaveable {
        mutableStateOf<IndividualCollectionSheet?>(
            null,
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
        modifier = modifier,
        // TODO check old code and see why it was here and implement if necessary
//        isAppBarPresent = false,
        snackbarHostState = snackbarHostState,
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
        if (showCollectionSheetDialog) {
            MifosBottomSheet(
                content = {
                    CollectionSheetDialogContent(
                        date = DateHelper.getDateAsStringFromLong(repaymentDate),
                        member = individualCollectionSheet?.clients?.size.toString(),
                        fillNow = {
                            showCollectionSheetDialog = false
                            individualCollectionSheet?.let {
                                onDetail(
                                    DateHelper.getDateAsStringFromLong(repaymentDate),
                                    it,
                                )
                            }
                        },
                        onDismiss = {
                            showCollectionSheetDialog = false
                        },
                    )
                },
                onDismiss = {
                    showCollectionSheetDialog = false
                },
            )
        }
        if (state.isLoading) {
            MifosProgressIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    modifier = Modifier.padding(KptTheme.spacing.md),
                    text = stringResource(Res.string.feature_collection_sheet_generate_new),
                    style = KptTheme.typography.titleLarge,
                )
                Text(
                    modifier = Modifier.padding(
                        start = KptTheme.spacing.md,
                        end = KptTheme.spacing.md,
                    ),
                    text = stringResource(Res.string.feature_collection_sheet_fill_collection_sheet_message),
                    style = KptTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(KptTheme.spacing.md))
                MifosTextFieldDropdown(
                    value = selectedOffice,
                    onValueChanged = {
                        selectedOffice = it
                        selectedStaff = ""
                    },
                    onOptionSelected = { index, value ->
                        state.officeList[index].id.let {
                            getStaffList(it)
                            officeId = it
                        }
                        selectedOffice = value
                        selectedStaff = ""
                    },
                    label = stringResource(Res.string.feature_collection_sheet_office),
                    options = state.officeList.map { it.name.toString() },
                )
                Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
                MifosDatePickerTextField(
                    value = DateHelper.getDateAsStringFromLong(repaymentDate),
                    label = stringResource(Res.string.feature_collection_sheet_repayment_date),
                    openDatePicker = {
                        showDatePicker = true
                    },
                )
                Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
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
                    label = stringResource(Res.string.feature_collection_sheet_staff),
                    options = state.staffList.map { it.displayName.toString() },
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MifosButton(
                        onClick = {
                            keyboardController?.hide()
                            generateCollection(
                                officeId,
                                staffId,
                                DateHelper.getDateAsStringFromLong(repaymentDate),
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(KptTheme.spacing.md),
                        contentPadding = PaddingValues(),
                        enabled = selectedOffice != "",
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_collection_sheet_generate),
                            style = KptTheme.typography.bodyLarge,
                        )
                    }
                    MifosButton(
                        onClick = {
                            selectedOffice = ""
                            repaymentDate = Clock.System.now().toEpochMilliseconds()
                            selectedStaff = ""
                            individualCollectionSheet = null
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(KptTheme.spacing.md),
                        contentPadding = PaddingValues(),
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_collection_sheet_clear),
                            style = KptTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectionSheetDialogContent(
    date: String,
    member: String,
    fillNow: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    MifosBottomSheet(
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = KptTheme.spacing.md,
                        end = KptTheme.spacing.md,
                        bottom = KptTheme.spacing.md,
                    ),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    text = stringResource(Res.string.feature_collection_sheet_found_sheet),
                    style = KptTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(KptTheme.spacing.md))
                Text(
                    text = stringResource(Res.string.feature_collection_sheet_fill_collection_sheet_message),
                    style = KptTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(KptTheme.spacing.md))
                Row {
                    Text(
                        text = stringResource(Res.string.feature_collection_sheet_date),
                        style = KptTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.width(KptTheme.spacing.md))
                    Text(
                        text = date,
                        style = KptTheme.typography.bodyLarge,
                    )
                }

                Row {
                    Text(
                        text = stringResource(Res.string.feature_collection_sheet_member),
                        style = KptTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.width(KptTheme.spacing.md))
                    Text(
                        text = member,
                        style = KptTheme.typography.bodyLarge,
                    )
                }
                Spacer(modifier = Modifier.height(KptTheme.spacing.md))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    MifosButton(
                        onClick = fillNow,
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_collection_sheet_fill_now),
                            style = KptTheme.typography.bodyLarge,
                        )
                    }
                    Spacer(modifier = Modifier.width(KptTheme.spacing.md))
                    MifosButton(
                        onClick = onDismiss,
                    ) {
                        Text(
                            text = stringResource(Res.string.feature_collection_sheet_cancel),
                            style = KptTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        },
        onDismiss = onDismiss,
    )
}

@DevicePreview
@Composable
private fun NewIndividualCollectionSheetPreview() {
    Column {
        NewIndividualCollectionSheetScreen(
            state = NewIndividualCollectionSheetUiState(staffList = sampleStaffList),
            getStaffList = {},
            generateCollection = { _, _, _ ->
            },
            onDetail = { _, _ -> },
        )
        NewIndividualCollectionSheetScreen(
            state = NewIndividualCollectionSheetUiState(officeList = sampleOfficeList),
            getStaffList = {},
            generateCollection = { _, _, _ ->
            },
            onDetail = { _, _ -> },
        )
        NewIndividualCollectionSheetScreen(
            state = NewIndividualCollectionSheetUiState(error = "Error Occurred"),
            getStaffList = {},
            generateCollection = { _, _, _ ->
            },
            onDetail = { _, _ -> },
        )
        NewIndividualCollectionSheetScreen(
            state = NewIndividualCollectionSheetUiState(isLoading = true),
            getStaffList = {},
            generateCollection = { _, _, _ ->
            },
            onDetail = { _, _ -> },
        )
        NewIndividualCollectionSheetScreen(
            state = NewIndividualCollectionSheetUiState(individualCollectionSheet = IndividualCollectionSheet()),
            getStaffList = {},
            generateCollection = { _, _, _ ->
            },
            onDetail = { _, _ -> },
        )
    }
}

val sampleStaffList = List(10) {
    StaffEntity(firstname = "FirstName", lastname = "LastName", isActive = true)
}

val sampleOfficeList = List(10) {
    OfficeEntity(id = it, name = "Name")
}

@DevicePreview
@Composable
private fun CollectionSheetDialogContentPreview() {
    CollectionSheetDialogContent(date = "19 June 2024", member = "5", fillNow = {}, onDismiss = {})
}
