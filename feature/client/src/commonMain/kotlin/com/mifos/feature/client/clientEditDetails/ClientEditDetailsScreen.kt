/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientEditDetails

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.account_no
import androidclient.feature.client.generated.resources.activation_date
import androidclient.feature.client.generated.resources.client_details_update_failure_title
import androidclient.feature.client.generated.resources.client_details_updated
import androidclient.feature.client.generated.resources.client_details_updated_success_message
import androidclient.feature.client.generated.resources.dialog_continue
import androidclient.feature.client.generated.resources.email_address
import androidclient.feature.client.generated.resources.feature_client_account_information
import androidclient.feature.client.generated.resources.feature_client_cancel
import androidclient.feature.client.generated.resources.feature_client_client
import androidclient.feature.client.generated.resources.feature_client_client_classification
import androidclient.feature.client.generated.resources.feature_client_dob
import androidclient.feature.client.generated.resources.feature_client_error
import androidclient.feature.client.generated.resources.feature_client_error_first_name_can_not_be_empty
import androidclient.feature.client.generated.resources.feature_client_error_first_name_should_contain_only_alphabets
import androidclient.feature.client.generated.resources.feature_client_error_last_name_can_not_be_empty
import androidclient.feature.client.generated.resources.feature_client_error_last_name_should_contain_only_alphabets
import androidclient.feature.client.generated.resources.feature_client_error_middle_name_should_contain_only_alphabets
import androidclient.feature.client.generated.resources.feature_client_external_id
import androidclient.feature.client.generated.resources.feature_client_first_name_mandatory
import androidclient.feature.client.generated.resources.feature_client_gender
import androidclient.feature.client.generated.resources.feature_client_last_name_mandatory
import androidclient.feature.client.generated.resources.feature_client_middle_name
import androidclient.feature.client.generated.resources.feature_client_office_name_mandatory
import androidclient.feature.client.generated.resources.feature_client_office_use
import androidclient.feature.client.generated.resources.feature_client_personal_details
import androidclient.feature.client.generated.resources.feature_client_phone_no
import androidclient.feature.client.generated.resources.feature_client_select_date
import androidclient.feature.client.generated.resources.feature_client_staff
import androidclient.feature.client.generated.resources.feature_client_submit
import androidclient.feature.client.generated.resources.legal_form
import androidclient.feature.client.generated.resources.submission_date
import androidclient.feature.client.generated.resources.update_details
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults.colors
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.common.utils.formatDate
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.utils.PhoneNumberUtil
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Created by Samarth Chaplot on 25/08/2025.
 */

@Composable
internal fun ClientEditDetailsScreen(
    navigateBack: () -> Unit,
    navController: NavController,
    onNavigateNext: (Int) -> Unit,
    viewModel: ClientEditDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadClientDetails()
        viewModel.loadOfficeAndClientTemplate()
    }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientEditDetailsEvent.NavigateBack -> navigateBack()
            ClientEditDetailsEvent.NavigateNext -> onNavigateNext(state.id)
            ClientEditDetailsEvent.OnUpdateSuccess -> {
                navigateBack()
            }
        }
    }

    ClientEditDetailsScaffold(
        navController = navController,
        navigateBack = navigateBack,
        state = state,
        officeList = state.showOffices,
        staffInOffices = state.staffInOffices,
        loadStaffInOffice = { viewModel.loadStaffInOffices(officeId = it) },
        updateClient = { viewModel.updateClient(clientPayload = it) },
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientEditDetailsScaffold(
    state: ClientEditDetailsState,
    navController: NavController,
    navigateBack: () -> Unit,
    officeList: List<OfficeEntity>,
    staffInOffices: List<StaffEntity>,
    loadStaffInOffice: (officeId: Int) -> Unit,
    updateClient: (clientPayload: ClientPayloadEntity) -> Unit,
    modifier: Modifier = Modifier,
    onAction: (ClientEditDetailsAction) -> Unit,
) {
    val scope = rememberCoroutineScope()

    MifosScaffold(
        title = stringResource(Res.string.update_details),
        onBackPressed = { navigateBack.invoke() },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            MifosBreadcrumbNavBar(navController)
            when (state.dialogState) {
                is ClientEditDetailsState.DialogState.Loading -> {
                    MifosProgressIndicator()
                }
                is ClientEditDetailsState.DialogState.ShowUpdateDetailsContent -> {
                    UpdateClientDetailsContent(
                        state = state,
                        scope = scope,
                        officeList = officeList,
                        staffInOffices = staffInOffices,
                        clientTemplate = state.clientsTemplate,
                        loadStaffInOffice = loadStaffInOffice,
                        updateClient = updateClient,
                        navigateBack = navigateBack,
                    )
                }
                is ClientEditDetailsState.DialogState.Error -> {
                    MifosSweetError(
                        message = state.dialogState.message,
                    )
                }
                is ClientEditDetailsState.DialogState.ShowStatusDialog -> {
                    MifosStatusDialog(
                        status = state.dialogState.status,
                        btnText = stringResource(Res.string.dialog_continue),
                        onConfirm = { onAction(ClientEditDetailsAction.OnNext) },
                        successTitle = stringResource(Res.string.client_details_updated),
                        successMessage = stringResource(Res.string.client_details_updated_success_message),
                        failureTitle = stringResource(Res.string.client_details_update_failure_title),
                        failureMessage = state.dialogState.msg,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun UpdateClientDetailsContent(
    state: ClientEditDetailsState,
    scope: CoroutineScope,
    officeList: List<OfficeEntity>,
    staffInOffices: List<StaffEntity>,
    clientTemplate: ClientsTemplateEntity,
    loadStaffInOffice: (Int) -> Unit,
    updateClient: (ClientPayloadEntity) -> Unit,
    navigateBack: () -> Unit,
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var middleName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var mobileNumber by rememberSaveable { mutableStateOf("") }
    var externalId by rememberSaveable { mutableStateOf("") }
    var emailAddress by rememberSaveable { mutableStateOf("") }
    var acccountNo by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var genderId by rememberSaveable { mutableIntStateOf(0) }

    var firstNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var middleNameError by rememberSaveable { mutableStateOf<String?>(null) }
    var lastNameError by rememberSaveable { mutableStateOf<String?>(null) }

    var legalForm by rememberSaveable { mutableStateOf("") }
    var clientType by rememberSaveable { mutableStateOf("") }
    var selectedLegalFormId by rememberSaveable { mutableIntStateOf(1) }
    var selectedClientTypeId by rememberSaveable { mutableIntStateOf(0) }
    var clientClassification by rememberSaveable { mutableStateOf("") }
    var selectedClientClassificationId by rememberSaveable { mutableIntStateOf(0) }
    var selectedOffice by rememberSaveable { mutableStateOf("") }
    var selectedOfficeId: Int? by rememberSaveable { mutableStateOf(1) }
    var staff by rememberSaveable { mutableStateOf("") }
    var selectedStaffId: Int? by rememberSaveable { mutableStateOf(0) }

    var isActive by rememberSaveable { mutableStateOf(false) }
    var dateOfBirth by rememberSaveable {
        mutableLongStateOf(
            Clock.System.now().toEpochMilliseconds(),
        )
    }
    var submissionDate by rememberSaveable {
        mutableLongStateOf(
            Clock.System.now().toEpochMilliseconds(),
        )
    }
    var activationDate by rememberSaveable {
        mutableLongStateOf(
            Clock.System.now().toEpochMilliseconds(),
        )
    }
    var showDateOfBirthDatepicker by rememberSaveable { mutableStateOf(false) }
    var showDateOfSubmissionDatepicker by rememberSaveable { mutableStateOf(false) }
    var showDateOfActivationDatepicker by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val dateOfBirthDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateOfBirth,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    val activateDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = activationDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    val submissionDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = submissionDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    LaunchedEffect(key1 = Unit) {
        if (officeList.isNotEmpty()) {
            officeList[0].id.let { loadStaffInOffice.invoke(it) }
        }
    }

    LaunchedEffect(state.client) {
        state.client?.let { client ->
            firstName = client.firstname ?: ""
            middleName = client.middlename ?: ""
            lastName = client.lastname ?: ""
            mobileNumber = client.mobileNo ?: ""
            externalId = client.externalId ?: ""
            acccountNo = client.accountNo ?: ""
            legalForm = client.legalForm?.value ?: ""
            selectedLegalFormId = client.legalForm?.id ?: 1
            selectedOffice = client.officeName ?: ""
            selectedOfficeId = client.officeId
            staff = client.staffName ?: ""
            selectedStaffId = client.staffId
            emailAddress = client.emailAddress ?: ""

            client.dateOfBirth.let { dateOfBirth = dateListToTimestamp(it) }
            client.activationDate.let { activationDate = dateListToTimestamp(it) }
            client.timeline?.submittedOnDate?.let { submissionDate = dateListToTimestamp(it) }

            dateOfBirthDatePickerState.let {
                it.selectedDateMillis = dateOfBirth
                it.displayedMonthMillis = dateOfBirth
            }

            submissionDatePickerState.let {
                it.selectedDateMillis = submissionDate
                it.displayedMonthMillis = submissionDate
            }

            activateDatePickerState.let {
                it.selectedDateMillis = activationDate
                it.displayedMonthMillis = activationDate
            }
        }
    }

    if (showDateOfBirthDatepicker || showDateOfSubmissionDatepicker || showDateOfActivationDatepicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDateOfBirthDatepicker = false
                showDateOfSubmissionDatepicker = false
                showDateOfActivationDatepicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (showDateOfActivationDatepicker) {
                            activateDatePickerState.selectedDateMillis?.let {
                                activationDate = it
                            }
                        } else if (showDateOfSubmissionDatepicker) {
                            submissionDatePickerState.selectedDateMillis?.let {
                                submissionDate = it
                            }
                        } else {
                            dateOfBirthDatePickerState.selectedDateMillis?.let {
                                dateOfBirth = it
                            }
                        }
                        showDateOfBirthDatepicker = false
                        showDateOfSubmissionDatepicker = false
                        showDateOfActivationDatepicker = false
                    },
                ) { Text(stringResource(Res.string.feature_client_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDateOfBirthDatepicker = false
                        showDateOfSubmissionDatepicker = false
                        showDateOfActivationDatepicker = false
                    },
                ) { Text(stringResource(Res.string.feature_client_cancel)) }
            },
        ) {
            if (showDateOfBirthDatepicker) {
                DatePicker(state = dateOfBirthDatePickerState)
            } else if (showDateOfSubmissionDatepicker) {
                DatePicker(state = submissionDatePickerState)
            } else {
                DatePicker(state = activateDatePickerState)
            }
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    MifosScaffold(
        bottomBar = {
            UpdateClientDetailsBottomBar(
                onCancelClick = { navigateBack.invoke() },
                onSubmitClick = {
                    val clientNames = Name(firstName, lastName, middleName)
                    handleSubmitClick(
                        scope,
                        clientNames,
                        clientTemplate,
                        updateClient,
                        isActive,
                        staffInOffices,
                        selectedClientId = selectedClientTypeId,
                        selectedClientClassificationId,
                        genderId,
                        selectedStaffId,
                        activationDate,
                        dateOfBirth,
                        submissionDate = submissionDate,
                        emailAddress,
                        mobileNumber,
                        externalId,
                        selectedLegalFormId,
                        onFirstNameError = { firstNameError = it },
                        onMiddleNameError = { middleNameError = it },
                        onLastNameError = { lastNameError = it },
                    )
                },
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = DesignToken.padding.largeIncreased,
                    start = DesignToken.padding.largeIncreased,
                    end = DesignToken.padding.largeIncreased,
                    bottom = contentPadding.calculateBottomPadding(),
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            keyboardController?.hide()
                        },
                    )
                }
                .verticalScroll(state = scrollState),
        ) {
            Text(
                text = stringResource(Res.string.feature_client_personal_details),
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                letterSpacing = MaterialTheme.typography.labelLarge.letterSpacing,
                color = MaterialTheme.colorScheme.onSurface,
            )
            ClientInputTextFields(
                firstName = firstName,
                middleName = middleName,
                lastName = lastName,
                mobileNumber = mobileNumber,
                emailAddress = emailAddress,
                firstNameError = firstNameError,
                middleNameError = middleNameError,
                lastNameError = lastNameError,
                onFirstNameChange = { firstName = it },
                onMiddleNameChange = { middleName = it },
                onLastNameChange = { lastName = it },
                onMobileNumberChange = { mobileNumber = it },
                onEmailAddressChange = { emailAddress = it },
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.small))
            clientTemplate.genderOptions?.let { list ->
                MifosTextFieldDropdown(
                    enabled = list.isNotEmpty(),
                    value = gender,
                    onValueChanged = { gender = it },
                    onOptionSelected = { index, value ->
                        gender = value
                        genderId = list[index].id
                    },
                    label = stringResource(Res.string.feature_client_gender),
                    options = list.sortedBy { it.id }.map { it.name },
                    readOnly = true,
                )
            }
            MifosDatePickerTextField(
                value = DateHelper.getDateAsStringFromLong(dateOfBirth),
                label = stringResource(Res.string.feature_client_dob),
                openDatePicker = { showDateOfBirthDatepicker = !showDateOfBirthDatepicker },
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.small))

            Text(
                text = stringResource(Res.string.feature_client_account_information),
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                letterSpacing = MaterialTheme.typography.labelLarge.letterSpacing,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(DesignToken.spacing.small))

            MifosOutlinedTextField(
                value = acccountNo,
                onValueChange = { acccountNo = it },
                label = stringResource(Res.string.account_no),
                shape = DesignToken.shapes.medium,
                textStyle = MifosTypography.bodyLarge,
                colors = colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                ),
                config = MifosTextFieldConfig(
                    enabled = false,
                    isError = false,
                    trailingIcon = if (false) {
                        {
                            Icon(
                                imageVector = MifosIcons.Error,
                                contentDescription = stringResource(Res.string.feature_client_error),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    } else {
                        null
                    },
                ),
            )
            Spacer(modifier = Modifier.height(DesignToken.spacing.small))

            MifosTextFieldDropdown(
                enabled = false,
                value = selectedOffice,
                onValueChanged = { selectedOffice = it },
                onOptionSelected = { index, value ->
                    selectedOffice = value
                    selectedOfficeId = officeList[index].id

                    if (selectedOfficeId != null) {
                        scope.launch {
                            loadStaffInOffice.invoke(selectedOfficeId!!)
                        }
                    }
                },
                label = stringResource(Res.string.feature_client_office_name_mandatory),
                options = officeList.sortedBy { it.name }.map { it.name.toString() },
                readOnly = true,
            )

            MifosOutlinedTextField(
                value = externalId,
                onValueChange = { externalId = it },
                label = stringResource(Res.string.feature_client_external_id),
                shape = DesignToken.shapes.medium,
                textStyle = MifosTypography.bodyLarge,
                colors = colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                ),
                config = MifosTextFieldConfig(
                    isError = false,
                    trailingIcon = if (false) {
                        {
                            Icon(
                                imageVector = MifosIcons.Error,
                                contentDescription = stringResource(Res.string.feature_client_error),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    } else {
                        null
                    },
                ),
            )
            Spacer(modifier = Modifier.height(DesignToken.spacing.small))

            Text(
                text = stringResource(Res.string.feature_client_office_use),
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                letterSpacing = MaterialTheme.typography.labelLarge.letterSpacing,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(DesignToken.spacing.small))

            clientTemplate.clientLegalFormOptions?.let { list ->
                MifosTextFieldDropdown(
                    value = legalForm,
                    onValueChanged = { legalForm = it },
                    onOptionSelected = { index, value ->
                        legalForm = value
                        selectedLegalFormId = list[index].id
                    },
                    label = stringResource(Res.string.legal_form),
                    options = list.sortedBy { it.id }.map { it.value },
                    readOnly = true,
                )
            }

            clientTemplate.clientTypeOptions?.let { list ->
                MifosTextFieldDropdown(
                    enabled = clientType.isNotEmpty(),
                    value = clientType,
                    onValueChanged = { clientType = it },
                    onOptionSelected = { index, value ->
                        clientType = value
                        selectedClientTypeId = list[index].id
                    },
                    label = stringResource(Res.string.feature_client_client),
                    options = list.sortedBy { it.id }.map { it.name },
                    readOnly = true,
                )
            }

            clientTemplate.clientClassificationOptions?.let { list ->
                MifosTextFieldDropdown(
                    enabled = clientClassification.isNotEmpty(),
                    value = clientClassification,
                    onValueChanged = { clientClassification = it },
                    onOptionSelected = { index, value ->
                        clientClassification = value
                        selectedClientClassificationId = list[index].id
                    },
                    label = stringResource(Res.string.feature_client_client_classification),
                    options = list.sortedBy { it.id }.map { it.name },
                    readOnly = true,
                )
            }

            MifosDatePickerTextField(
                enabled = false,
                value = DateHelper.getDateAsStringFromLong(submissionDate),
                label = stringResource(Res.string.submission_date),
                openDatePicker = { showDateOfSubmissionDatepicker = false },
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.small))
            MifosDatePickerTextField(
                enabled = false,
                value = DateHelper.getDateAsStringFromLong(activationDate),
                label = stringResource(Res.string.activation_date),
                openDatePicker = { showDateOfActivationDatepicker = false },
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.small))

            MifosTextFieldDropdown(
                enabled = false,
                value = staff,
                onValueChanged = { staff = it },
                onOptionSelected = { index, value ->
                    staff = value
                    selectedStaffId = staffInOffices[index].id
                },
                label = stringResource(Res.string.feature_client_staff),
                options = staffInOffices.sortedBy { it.displayName }.map { it.displayName.toString() },
                readOnly = true,
            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.small))
        }
    }
}

private data class Name(
    val firstName: String,
    val lastName: String,
    val middleName: String,
)

@Composable
private fun ClientInputTextFields(
    firstName: String,
    middleName: String,
    lastName: String,
    mobileNumber: String,
    emailAddress: String,
    firstNameError: String?,
    middleNameError: String?,
    lastNameError: String?,
    onFirstNameChange: (String) -> Unit,
    onMiddleNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onMobileNumberChange: (String) -> Unit,
    onEmailAddressChange: (String) -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.height(DesignToken.spacing.small))
        MifosOutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = stringResource(Res.string.feature_client_first_name_mandatory),
            shape = DesignToken.shapes.medium,
            textStyle = MifosTypography.bodyLarge,
            colors = colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
            config = MifosTextFieldConfig(
                isError = firstNameError != null,
                errorText = firstNameError,
                trailingIcon = if (firstNameError != null) {
                    {
                        Icon(
                            imageVector = MifosIcons.Error,
                            contentDescription = stringResource(Res.string.feature_client_error),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                } else {
                    null
                },
            ),
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.small))
        MifosOutlinedTextField(
            value = middleName,
            onValueChange = onMiddleNameChange,
            label = stringResource(Res.string.feature_client_middle_name),
            shape = DesignToken.shapes.medium,
            textStyle = MifosTypography.bodyLarge,
            colors = colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
            config = MifosTextFieldConfig(
                isError = middleNameError != null,
                errorText = middleNameError,
                trailingIcon = if (middleNameError != null) {
                    {
                        Icon(
                            imageVector = MifosIcons.Error,
                            contentDescription = stringResource(Res.string.feature_client_error),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                } else {
                    null
                },
            ),
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.small))
        MifosOutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = stringResource(Res.string.feature_client_last_name_mandatory),
            shape = DesignToken.shapes.medium,
            textStyle = MifosTypography.bodyLarge,
            colors = colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
            config = MifosTextFieldConfig(
                isError = lastNameError != null,
                errorText = lastNameError,
                trailingIcon = if (lastNameError != null) {
                    {
                        Icon(
                            imageVector = MifosIcons.Error,
                            contentDescription = stringResource(Res.string.feature_client_error),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                } else {
                    null
                },
            ),
        )
        Spacer(modifier = Modifier.height(DesignToken.spacing.small))
        MifosOutlinedTextField(
            value = mobileNumber,
            onValueChange = onMobileNumberChange,
            label = stringResource(Res.string.feature_client_phone_no),
            shape = DesignToken.shapes.medium,
            textStyle = MifosTypography.bodyLarge,
            colors = colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
            config = MifosTextFieldConfig(
                isError = false,
                trailingIcon = if (false) {
                    {
                        Icon(
                            imageVector = MifosIcons.Error,
                            contentDescription = stringResource(Res.string.feature_client_error),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                } else {
                    null
                },
            ),
        )

        Spacer(modifier = Modifier.height(DesignToken.spacing.small))
        MifosOutlinedTextField(
            value = emailAddress,
            onValueChange = onEmailAddressChange,
            label = stringResource(Res.string.email_address),
            shape = DesignToken.shapes.medium,
            textStyle = MifosTypography.bodyLarge,
            colors = colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
            config = MifosTextFieldConfig(
                isError = false,
                trailingIcon = if (false) {
                    {
                        Icon(
                            imageVector = MifosIcons.Error,
                            contentDescription = stringResource(Res.string.feature_client_error),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                } else {
                    null
                },
            ),
        )
    }
}

@Composable
private fun UpdateClientDetailsBottomBar(
    onCancelClick: () -> Unit,
    onSubmitClick: () -> Unit,
) {
    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth().padding(DesignToken.padding.small),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .heightIn(DesignToken.sizes.avatarMedium),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                shape = RoundedCornerShape(DesignToken.sizes.iconMinyMiny),
                border = BorderStroke(
                    width = Dp.Hairline,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ),
                onClick = { onCancelClick.invoke() },
            ) {
                Icon(imageVector = MifosIcons.Close, contentDescription = "")
                Spacer(modifier = Modifier.width(DesignToken.spacing.small))
                Text(
                    text = stringResource(Res.string.feature_client_cancel),
                    fontWeight = FontWeight.Medium,
                    fontSize = MaterialTheme.typography.labelLarge.fontSize,
                    letterSpacing = MaterialTheme.typography.labelLarge.letterSpacing,
                    lineHeight = MaterialTheme.typography.labelLarge.lineHeight,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.width(DesignToken.spacing.small))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .heightIn(DesignToken.sizes.avatarMedium),
                shape = RoundedCornerShape(DesignToken.sizes.iconMinyMiny),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                onClick = { onSubmitClick.invoke() },
            ) {
                Icon(imageVector = MifosIcons.Check, contentDescription = "")
                Spacer(modifier = Modifier.width(DesignToken.spacing.small))
                Text(
                    text = stringResource(Res.string.feature_client_submit),
                    fontWeight = FontWeight.Medium,
                    fontSize = MaterialTheme.typography.labelLarge.fontSize,
                    letterSpacing = MaterialTheme.typography.labelLarge.letterSpacing,
                    lineHeight = MaterialTheme.typography.labelLarge.lineHeight,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private fun handleSubmitClick(
    scope: CoroutineScope,
    clientNames: Name,
    clientTemplate: ClientsTemplateEntity,
    updateClient: (clientPayload: ClientPayloadEntity) -> Unit,
    isActive: Boolean,
    staffInOffices: List<StaffEntity>,
    selectedClientId: Int,
    selectedClientClassificationId: Int,
    genderId: Int,
    selectedStaffId: Int?,
    activationDate: Long,
    dateOfBirth: Long,
    submissionDate: Long,
    emailAddress: String,
    mobileNumber: String,
    externalId: String,
    selectedLegalFormId: Int?,
    onFirstNameError: (String?) -> Unit,
    onMiddleNameError: (String?) -> Unit,
    onLastNameError: (String?) -> Unit,
): Boolean {
    if (!isAllFieldsValid(
            scope,
            clientNames.firstName,
            clientNames.middleName,
            clientNames.lastName,
            onFirstNameError,
            onMiddleNameError,
            onLastNameError,
        )
    ) {
        return false
    }

    val clientPayload = createClientPayload(
        clientNames.firstName,
        clientNames.lastName,
        staffInOffices,
        isActive,
        activationDate,
        dateOfBirth,
        submissionDate,
        clientNames.middleName,
        mobileNumber,
        emailAddress,
        externalId,
        clientTemplate,
        genderId,
        selectedStaffId,
        selectedClientId,
        selectedClientClassificationId,
        selectedLegalFormId,
    )

    updateClient.invoke(clientPayload)
    return true
}

private fun createClientPayload(
    firstName: String,
    lastName: String,
    staffInOffices: List<StaffEntity>,
    isActive: Boolean,
    activationDate: Long,
    dateOfBirth: Long,
    submissionDate: Long,
    middleName: String,
    mobileNumber: String,
    emailAddress: String,
    externalId: String,
    clientTemplate: ClientsTemplateEntity,
    genderId: Int,
    selectedStaffId: Int?,
    selectedClientId: Int,
    selectedClientClassificationId: Int,
    legalFormId: Int?,
): ClientPayloadEntity {
    val dateFormat = "dd MMMM yyyy"
    val locale = "en"

    var clientPayload = ClientPayloadEntity(
        // Mandatory fields
        firstname = firstName,
        lastname = lastName,

        // Optional fields with default values
        middlename = middleName,
        legalFormId = legalFormId,
        active = isActive,
        activationDate = formatDate(activationDate),
        submittedOnDate = formatDate(submissionDate),
        dateOfBirth = formatDate(dateOfBirth),
        dateFormat = dateFormat,
        locale = locale,
    )

    if (PhoneNumberUtil.isGlobalPhoneNumber(mobileNumber)) {
        clientPayload = clientPayload.copy(mobileNo = mobileNumber)
    }
    if (emailAddress.isNotEmpty()) {
        clientPayload = clientPayload.copy(emailAddress = emailAddress)
    }
    if (externalId.isNotEmpty()) {
        clientPayload = clientPayload.copy(externalId = externalId)
    }
    if (clientTemplate.genderOptions?.isNotEmpty() == true) {
        clientPayload = clientPayload.copy(genderId = genderId)
    }
    if (staffInOffices.isNotEmpty()) {
        clientPayload = clientPayload.copy(staffId = selectedStaffId)
    }
    if (clientTemplate.clientTypeOptions?.isNotEmpty() == true) {
        clientPayload = clientPayload.copy(clientTypeId = selectedClientId)
    }
    if (clientTemplate.clientClassificationOptions?.isNotEmpty() == true) {
        clientPayload = clientPayload.copy(clientClassificationId = selectedClientClassificationId)
    }
    return clientPayload
}

private fun isAllFieldsValid(
    scope: CoroutineScope,
    firstName: String,
    middleName: String,
    lastName: String,
    onFirstNameError: (String?) -> Unit,
    onMiddleNameError: (String?) -> Unit,
    onLastNameError: (String?) -> Unit,
): Boolean {
    return when {
        !isFirstNameValid(
            firstName,
            onFirstNameError,
            scope,
        ) -> {
            false
        }

        !isMiddleNameValid(middleName, scope, onMiddleNameError) -> {
            false
        }

        !isLastNameValid(lastName, onLastNameError, scope) -> {
            false
        }

        else -> true
    }
}

private fun isFirstNameValid(
    name: String,
    onFirstNameError: (String?) -> Unit,
    scope: CoroutineScope,
): Boolean {
    return when {
        name.isEmpty() -> {
            scope.launch {
                onFirstNameError(
                    getString(
                        Res.string.feature_client_error_first_name_can_not_be_empty,
                    ),
                )
            }
            return false
        }

        name.contains("[^a-zA-Z ]".toRegex()) -> {
            scope.launch {
                onFirstNameError(
                    getString(
                        Res.string.feature_client_error_first_name_should_contain_only_alphabets,
                    ),
                )
            }
            return false
        }

        else -> {
            scope.launch {
                onFirstNameError(
                    null,
                )
            }
            return true
        }
    }
}

private fun isLastNameValid(
    name: String,
    onLastNameError: (String?) -> Unit,
    scope: CoroutineScope,
): Boolean {
    return when {
        name.isEmpty() -> {
            scope.launch {
                onLastNameError(
                    getString(
                        Res.string.feature_client_error_last_name_can_not_be_empty,
                    ),
                )
            }
            return false
        }

        name.contains("[^a-zA-Z ]".toRegex()) -> {
            scope.launch {
                onLastNameError(
                    getString(
                        Res.string.feature_client_error_last_name_should_contain_only_alphabets,
                    ),
                )
            }
            return false
        }

        else -> {
            scope.launch {
                onLastNameError(
                    null,
                )
            }
            return true
        }
    }
}

private fun isMiddleNameValid(
    name: String,
    scope: CoroutineScope,
    onMiddleNameError: (String?) -> Unit,
): Boolean {
    return when {
        name.isEmpty() -> {
            true
        }

        name.contains("[^a-zA-Z ]".toRegex()) -> {
            scope.launch {
                onMiddleNameError(
                    getString(
                        Res.string.feature_client_error_middle_name_should_contain_only_alphabets,
                    ),
                )
            }
            return false
        }

        else -> {
            scope.launch {
                onMiddleNameError(
                    null,
                )
            }
            return true
        }
    }
}

/**
 * Converts a date represented as a List<Int?> [year, month, day] to a Long timestamp
 * @param dateList The date as a List of integers [year, month, day]
 * @return The date as milliseconds since epoch
 */
@OptIn(ExperimentalTime::class)
fun dateListToTimestamp(dateList: List<Int?>): Long {
    if (dateList.size < 3 || dateList.any { it == null }) {
        return Clock.System.now().toEpochMilliseconds()
    }

    val year = dateList[0]!!
    val month = dateList[1]!!
    val day = dateList[2]!!

    val date = LocalDate(year, month, day)
    val dateTime = LocalDateTime(date.year, date.month, date.day, 12, 0, 0)
    val instant = dateTime.toInstant(TimeZone.currentSystemDefault())

    return instant.toEpochMilliseconds()
}
