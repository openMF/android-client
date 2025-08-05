/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createNewClient

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_Image_Upload_Successful
import androidclient.feature.client.generated.resources.feature_client_address
import androidclient.feature.client.generated.resources.feature_client_address_active
import androidclient.feature.client.generated.resources.feature_client_address_line_1
import androidclient.feature.client.generated.resources.feature_client_address_line_2
import androidclient.feature.client.generated.resources.feature_client_address_line_3
import androidclient.feature.client.generated.resources.feature_client_address_type
import androidclient.feature.client.generated.resources.feature_client_cancel
import androidclient.feature.client.generated.resources.feature_client_center_submission_date
import androidclient.feature.client.generated.resources.feature_client_city
import androidclient.feature.client.generated.resources.feature_client_client
import androidclient.feature.client.generated.resources.feature_client_client_active
import androidclient.feature.client.generated.resources.feature_client_client_classification
import androidclient.feature.client.generated.resources.feature_client_client_created_successfully
import androidclient.feature.client.generated.resources.feature_client_country
import androidclient.feature.client.generated.resources.feature_client_create_new_client
import androidclient.feature.client.generated.resources.feature_client_dob
import androidclient.feature.client.generated.resources.feature_client_error_address_type_is_required
import androidclient.feature.client.generated.resources.feature_client_error_first_name_can_not_be_empty
import androidclient.feature.client.generated.resources.feature_client_error_first_name_should_contain_only_alphabets
import androidclient.feature.client.generated.resources.feature_client_error_last_name_can_not_be_empty
import androidclient.feature.client.generated.resources.feature_client_error_last_name_should_contain_only_alphabets
import androidclient.feature.client.generated.resources.feature_client_error_middle_name_should_contain_only_alphabets
import androidclient.feature.client.generated.resources.feature_client_external_id
import androidclient.feature.client.generated.resources.feature_client_first_name_mandatory
import androidclient.feature.client.generated.resources.feature_client_gender
import androidclient.feature.client.generated.resources.feature_client_go_back
import androidclient.feature.client.generated.resources.feature_client_ic_dp_placeholder
import androidclient.feature.client.generated.resources.feature_client_last_name_mandatory
import androidclient.feature.client.generated.resources.feature_client_middle_name
import androidclient.feature.client.generated.resources.feature_client_mobile_no
import androidclient.feature.client.generated.resources.feature_client_no_staff_associated_with_office
import androidclient.feature.client.generated.resources.feature_client_office_name_mandatory
import androidclient.feature.client.generated.resources.feature_client_please_select_action
import androidclient.feature.client.generated.resources.feature_client_postal_code
import androidclient.feature.client.generated.resources.feature_client_remove_existing_photo
import androidclient.feature.client.generated.resources.feature_client_select_date
import androidclient.feature.client.generated.resources.feature_client_staff
import androidclient.feature.client.generated.resources.feature_client_state_province
import androidclient.feature.client.generated.resources.feature_client_submit
import androidclient.feature.client.generated.resources.feature_client_take_a_photo
import androidclient.feature.client.generated.resources.feature_client_upload_photo
import androidclient.feature.client.generated.resources.feature_client_waiting_for_checker_approval
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.common.utils.formatDate
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.model.objects.clients.Address
import com.mifos.feature.client.utils.PhoneNumberUtil
import com.mifos.feature.client.utils.rememberPlatformCameraLauncher
import com.mifos.room.entities.client.AddressTemplate
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.noncore.DataTableEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by Pronay Sarker on 07/07/2024 (3:45 AM)
 */

@Composable
internal fun CreateNewClientScreen(
    navigateBack: () -> Unit,
    hasDatatables: (datatables: List<DataTableEntity>, clientPayload: ClientPayloadEntity) -> Unit,
    viewmodel: CreateNewClientViewModel = koinViewModel(),
) {
    val uiState by viewmodel.createNewClientUiState.collectAsStateWithLifecycle()
    val officeList by viewmodel.showOffices.collectAsStateWithLifecycle()
    val staffInOffice by viewmodel.staffInOffices.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewmodel.loadOfficeAndClientTemplate()
    }

    CreateNewClientScreen(
        uiState = uiState,
        officeList = officeList,
        staffInOffices = staffInOffice,
        onRetry = { viewmodel.loadOfficeAndClientTemplate() },
        navigateBack = navigateBack,
        loadStaffInOffice = { viewmodel.loadStaffInOffices(it) },
        createClient = { viewmodel.createClient(clientPayload = it) },
        uploadImage = { id ->
            viewmodel.uploadImage(id)
        },
        hasDatatables = hasDatatables,
        onImageSelected = {
            viewmodel.updateSelectedImage(it)
        },
    )
}

@Composable
internal fun CreateNewClientScreen(
    uiState: CreateNewClientUiState,
    onRetry: () -> Unit,
    officeList: List<OfficeEntity>,
    staffInOffices: List<StaffEntity>,
    loadStaffInOffice: (officeId: Int) -> Unit,
    navigateBack: () -> Unit,
    onImageSelected: (PlatformFile?) -> Unit,
    createClient: (clientPayload: ClientPayloadEntity) -> Unit,
    uploadImage: (id: Int) -> Unit,
    hasDatatables: (datatables: List<DataTableEntity>, clientPayload: ClientPayloadEntity) -> Unit,
) {
    var createClientWithImage by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    MifosScaffold(
        title = stringResource(Res.string.feature_client_create_new_client),
        snackbarHostState = snackbarHostState,
        onBackPressed = navigateBack,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (uiState) {
                CreateNewClientUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }

                is CreateNewClientUiState.ShowProgress -> {
                    MifosCircularProgress(text = uiState.message)
                }

                is CreateNewClientUiState.ShowClientTemplate -> {
                    CreateNewClientContent(
                        scope = scope,
                        snackbarHostState = snackbarHostState,
                        officeList = officeList,
                        staffInOffices = staffInOffices,
                        clientTemplate = uiState.clientsTemplate,
                        loadStaffInOffice = loadStaffInOffice,
                        createClient = createClient,
                        onHasDatatables = hasDatatables,
                        setFileForUpload = { filePath ->
                            filePath?.let {
                                createClientWithImage = true
                            }
                        },
                        onImageSelected = onImageSelected,
                        addressTemplate = uiState.addressTemplate,
                        isAddressEnabled = uiState.isAddressEnabled,
                    )
                }

                is CreateNewClientUiState.SetClientId -> {
                    if (createClientWithImage) {
                        uploadImage(uiState.id)
                    } else {
                        navigateBack.invoke()
                    }
                }

                is CreateNewClientUiState.ShowClientCreatedSuccessfully -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = getString(Res.string.feature_client_client_created_successfully),
                            duration = SnackbarDuration.Long,
                        )
                    }
                }

                is CreateNewClientUiState.OnImageUploadSuccess -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = getString(Res.string.feature_client_Image_Upload_Successful),
                            duration = SnackbarDuration.Long,
                        )
                    }
                    navigateBack.invoke()
                }

                is CreateNewClientUiState.ShowWaitingForCheckerApproval -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = getString(Res.string.feature_client_waiting_for_checker_approval),
                            duration = SnackbarDuration.Long,
                        )
                    }
                    navigateBack.invoke()
                }

                is CreateNewClientUiState.ShowError -> {
                    MifosSweetError(
                        message = stringResource(uiState.message),
                        onclick = { onRetry() },
                    )
                }

                is CreateNewClientUiState.ShowStringError -> {
                    MifosSweetError(
                        message = uiState.message,
                        onclick = { onRetry() },
                        buttonText = stringResource(Res.string.feature_client_go_back),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateNewClientContent(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    officeList: List<OfficeEntity>,
    staffInOffices: List<StaffEntity>,
    clientTemplate: ClientsTemplateEntity,
    addressTemplate: AddressTemplate?,
    isAddressEnabled: Boolean,
    loadStaffInOffice: (Int) -> Unit,
    onImageSelected: (PlatformFile?) -> Unit,
    createClient: (ClientPayloadEntity) -> Unit,
    onHasDatatables: (List<DataTableEntity>, ClientPayloadEntity) -> Unit,
    setFileForUpload: (filePath: String?) -> Unit,
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var middleName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var mobileNumber by rememberSaveable { mutableStateOf("") }
    var externalId by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var genderId by rememberSaveable { mutableIntStateOf(0) }

    var selectedAddressType by rememberSaveable { mutableStateOf("") }
    var selectedAddressTypeId by rememberSaveable { mutableIntStateOf(0) }
    var addressLine1 by rememberSaveable { mutableStateOf("") }
    var addressLine2 by rememberSaveable { mutableStateOf("") }
    var addressLine3 by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var selectedStateName by rememberSaveable { mutableStateOf("") }
    var selectedStateProvinceId by rememberSaveable { mutableIntStateOf(0) }
    var selectedCountryName by rememberSaveable { mutableStateOf("") }
    var selectedCountryId by rememberSaveable { mutableIntStateOf(0) }
    var postalCode by rememberSaveable { mutableStateOf("") }
    var isAddressActive by rememberSaveable { mutableStateOf(false) }

    var clientType by rememberSaveable { mutableStateOf("") }
    var selectedClientTypeId by rememberSaveable { mutableIntStateOf(0) }
    var clientClassification by rememberSaveable { mutableStateOf("") }
    var selectedClientClassificationId by rememberSaveable { mutableIntStateOf(0) }
    var selectedOffice by rememberSaveable { mutableStateOf("") }
    var selectedOfficeId: Int? by rememberSaveable { mutableStateOf(0) }
    var staff by rememberSaveable { mutableStateOf("") }
    var selectedStaffId: Int? by rememberSaveable { mutableStateOf(0) }

    var isActive by rememberSaveable { mutableStateOf(false) }
    var dateOfBirth by rememberSaveable {
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
    var showActivateDatepicker by rememberSaveable { mutableStateOf(false) }
    var showImagePickerDialog by rememberSaveable { mutableStateOf(false) }
    var selectedImagePath by rememberSaveable { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    val galleryLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { file ->
        file?.let {
            selectedImagePath = file.path
            onImageSelected(file)
        }
    }

    val cameraLauncher = rememberPlatformCameraLauncher { file ->
        file?.let {
            selectedImagePath = file.path
            onImageSelected(file)
        }
    }

    val hasDatatables by rememberSaveable {
        mutableStateOf(
            clientTemplate.dataTables?.isNotEmpty() ?: false,
        )
    }

    LaunchedEffect(key1 = Unit) {
        if (officeList.isNotEmpty()) {
            officeList[0].id.let { loadStaffInOffice.invoke(it) }
        }
    }

    LaunchedEffect(key1 = staffInOffices) {
        if (staffInOffices.isEmpty()) {
            snackbarHostState.showSnackbar(
                message = getString(
                    Res.string.feature_client_no_staff_associated_with_office,
                ),
            )
            staff = ""
            selectedStaffId = 0
        }
    }

    val activateDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = activationDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )
    val dateOfBirthDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateOfBirth,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    if (showImagePickerDialog) {
        MifosSelectImageDialog(
            onDismissRequest = { showImagePickerDialog = false },
            takeImage = {
                showImagePickerDialog = false
                cameraLauncher.launch()
            },
            uploadImage = {
                showImagePickerDialog = false
                galleryLauncher.launch()
            },
            removeImage = {
                showImagePickerDialog = false
                selectedImagePath = null
            },
        )
    }

    if (showActivateDatepicker || showDateOfBirthDatepicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDateOfBirthDatepicker = false
                showActivateDatepicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (showActivateDatepicker) {
                            activateDatePickerState.selectedDateMillis?.let {
                                activationDate = it
                            }
                        } else {
                            dateOfBirthDatePickerState.selectedDateMillis?.let {
                                dateOfBirth = it
                            }
                        }
                        showActivateDatepicker = false
                        showDateOfBirthDatepicker = false
                    },
                ) { Text(stringResource(Res.string.feature_client_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showActivateDatepicker = false
                        showDateOfBirthDatepicker = false
                    },
                ) { Text(stringResource(Res.string.feature_client_cancel)) }
            },
        ) {
            DatePicker(state = if (showActivateDatepicker) activateDatePickerState else dateOfBirthDatePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState),
    ) {
        ClientImageSection(selectedImagePath = selectedImagePath) {
            showImagePickerDialog = true
        }

        ClientInputTextFields(
            firstName = firstName,
            middleName = middleName,
            lastName = lastName,
            mobileNumber = mobileNumber,
            externalId = externalId,
            onFirstNameChange = { firstName = it },
            onMiddleNameChange = { middleName = it },
            onLastNameChange = { lastName = it },
            onMobileNumberChange = { mobileNumber = it },
            onExternalIdChange = { externalId = it },
        )

        Spacer(modifier = Modifier.height(16.dp))

        clientTemplate.genderOptions?.let { list ->
            MifosTextFieldDropdown(
                value = gender,
                onValueChanged = { gender = it },
                onOptionSelected = { index, value ->
                    gender = value
                    genderId = list[index].id
                },
                label = stringResource(Res.string.feature_client_gender),
                options = list.map { it.name },
                readOnly = true,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = DateHelper.getDateAsStringFromLong(dateOfBirth),
            label = stringResource(Res.string.feature_client_dob),
            openDatePicker = { showDateOfBirthDatepicker = !showDateOfBirthDatepicker },
        )

        Spacer(modifier = Modifier.height(16.dp))

        clientTemplate.clientTypeOptions?.let { list ->
            MifosTextFieldDropdown(
                value = clientType,
                onValueChanged = { clientType = it },
                onOptionSelected = { index, value ->
                    clientType = value
                    selectedClientTypeId = list[index].id
                },
                label = stringResource(Res.string.feature_client_client),
                options = list.sortedBy { it.name }.map { it.name },
                readOnly = true,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        clientTemplate.clientClassificationOptions?.let { list ->
            MifosTextFieldDropdown(
                value = clientClassification,
                onValueChanged = { clientClassification = it },
                onOptionSelected = { index, value ->
                    clientClassification = value
                    selectedClientClassificationId =
                        list[index].id
                },
                label = stringResource(Res.string.feature_client_client_classification),
                options = list.sortedBy { it.name }.map { it.name },
                readOnly = true,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
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

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = staff,
            onValueChanged = { staff = it },
            onOptionSelected = { index, value ->
                staff = value
                selectedStaffId = staffInOffices[index].id
            },
            label = stringResource(Res.string.feature_client_staff),
            options = staffInOffices.sortedBy { it.displayName }.map { it.displayName.toString() },
            readOnly = true,
            enabled = staffInOffices.isNotEmpty(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isActive,
                onCheckedChange = { isActive = !isActive },
            )
            Text(text = stringResource(Res.string.feature_client_client_active))
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
                label = stringResource(Res.string.feature_client_center_submission_date),
                openDatePicker = { showActivateDatepicker = !showActivateDatepicker },
            )
        }

        if (isAddressEnabled && addressTemplate != null) {
            val sortedAddressTypeOptions = addressTemplate.addressTypeIdOptions.sortedBy { it.name }
            val sortedCountryOptions = addressTemplate.countryIdOptions.sortedBy { it.name }
            val sortedStateOptions = addressTemplate.stateProvinceIdOptions.sortedBy { it.name }

            HorizontalDivider(modifier = Modifier.padding(16.dp))

            Text(
                stringResource(Res.string.feature_client_address),
                Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            AddressInputTextFields(
                addressLine1 = addressLine1,
                onAddressLine1Change = { addressLine1 = it },
                addressLine2 = addressLine2,
                onAddressLine2Change = { addressLine2 = it },
                addressLine3 = addressLine3,
                onAddressLine3Change = { addressLine3 = it },
                city = city,
                onCityChange = { city = it },
                postalCode = postalCode,
                onPostalCodeChange = { postalCode = it },
                selectedAddressType = selectedAddressType,
                onAddressTypeChanged = { selectedAddressType = it },
                onAddressTypeSelected = { index, value ->
                    selectedAddressType = value
                    selectedAddressTypeId = sortedAddressTypeOptions[index].id
                },
                addressTypeOptions = sortedAddressTypeOptions.map { it.name },
                selectedStateName = selectedStateName,
                onStateNameChanged = { selectedStateName = it },
                onStateSelected = { index, value ->
                    selectedStateName = value
                    selectedStateProvinceId = sortedStateOptions[index].id
                },
                stateOptions = sortedStateOptions.map { it.name },

                selectedCountryName = selectedCountryName,
                onCountryNameChanged = { selectedCountryName = it },
                onCountrySelected = { index, value ->
                    selectedCountryName = value
                    selectedCountryId = sortedCountryOptions[index].id
                },
                countryOptions = sortedCountryOptions.map { it.name },

                isAddressActive = isAddressActive,
                onAddressActiveChange = { isAddressActive = it },
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(46.dp),
            onClick = {
                val clientNames = Name(firstName, lastName, middleName)
                handleSubmitClick(
                    scope,
                    snackbarHostState,
                    clientNames,
                    clientTemplate,
                    createClient,
                    isActive,
                    onHasDatatables,
                    selectedImagePath,
                    setFileForUpload,
                    staffInOffices,
                    hasDatatables,
                    selectedOfficeId,
                    selectedClientTypeId,
                    selectedClientClassificationId,
                    genderId,
                    selectedStaffId,
                    activationDate,
                    dateOfBirth,
                    mobileNumber,
                    externalId,
                    isAddressEnabled,
                    isAddressActive,
                    selectedAddressTypeId,
                    addressLine1,
                    addressLine2,
                    addressLine3,
                    city,
                    selectedStateProvinceId,
                    selectedCountryId,
                    postalCode,
                )
            },
        ) {
            Text(text = stringResource(Res.string.feature_client_submit))
        }
    }
}

data class Name(
    val firstName: String,
    val lastName: String,
    val middleName: String,
)

private fun handleSubmitClick(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    clientNames: Name,
    clientTemplate: ClientsTemplateEntity,
    createClient: (clientPayload: ClientPayloadEntity) -> Unit,
    isActive: Boolean,
    onHasDatatables: (datatables: List<DataTableEntity>, clientPayload: ClientPayloadEntity) -> Unit,
    selectedImagePath: String?,
    setFileForUpload: (filePath: String?) -> Unit,
    staffInOffices: List<StaffEntity>,
    hasDatatables: Boolean,
    selectedOfficeId: Int?,
    selectedClientId: Int,
    selectedClientClassificationId: Int,
    genderId: Int,
    selectedStaffId: Int?,
    activationDate: Long,
    dateOfBirth: Long,
    mobileNumber: String,
    externalId: String,
    isAddressEnabled: Boolean,
    isAddressActive: Boolean,
    addressTypeId: Int,
    addressLine1: String,
    addressLine2: String,
    addressLine3: String,
    city: String,
    stateProvinceId: Int,
    countryId: Int,
    postalCode: String,
) {
    if (!isAllFieldsValid(
            scope,
            snackbarHostState,
            clientNames.firstName,
            clientNames.middleName,
            clientNames.lastName,
            addressTypeId = addressTypeId,
        )
    ) {
        return
    }

    var clientPayload = createClientPayload(
        clientNames.firstName,
        clientNames.lastName,
        selectedOfficeId,
        staffInOffices,
        isActive,
        activationDate,
        dateOfBirth,
        clientNames.middleName,
        mobileNumber,
        externalId,
        clientTemplate,
        genderId,
        selectedStaffId,
        selectedClientId,
        selectedClientClassificationId,
        isAddressEnabled,
        isAddressActive,
        addressTypeId,
        addressLine1,
        addressLine2,
        addressLine3,
        city,
        stateProvinceId,
        countryId,
        postalCode,
    )

    if (hasDatatables) {
        clientTemplate.dataTables?.let {
            onHasDatatables.invoke(it, clientPayload)
        }
    } else {
        setFileForUpload.invoke(selectedImagePath)
        clientPayload = clientPayload.copy(
            datatables = null,
        )
        createClient.invoke(clientPayload)
    }
}

private fun createClientPayload(
    firstName: String,
    lastName: String,
    selectedOfficeId: Int?,
    staffInOffices: List<StaffEntity>,
    isActive: Boolean,
    activationDate: Long,
    dateOfBirth: Long,
    middleName: String,
    mobileNumber: String,
    externalId: String,
    clientTemplate: ClientsTemplateEntity,
    genderId: Int,
    selectedStaffId: Int?,
    selectedClientId: Int,
    selectedClientClassificationId: Int,
    isAddressEnabled: Boolean,
    isAddressActive: Boolean,
    addressTypeId: Int,
    addressLine1: String,
    addressLine2: String,
    addressLine3: String,
    city: String,
    stateProvinceId: Int,
    countryId: Int,
    postalCode: String,
): ClientPayloadEntity {
    val dateFormat = "dd MMMM yyyy"
    val locale = "en"

    var clientPayload = ClientPayloadEntity(
        // Mandatory fields
        firstname = firstName,
        lastname = lastName,
        officeId = selectedOfficeId,
        legalFormId = 1,

        // Optional fields with default values
        active = isActive,
        activationDate = formatDate(activationDate),
        dateOfBirth = formatDate(dateOfBirth),
        dateFormat = dateFormat,
        locale = locale,
    )
    if (isAddressEnabled) {
        val address = Address(
            addressTypeId = addressTypeId,
            isActive = isAddressActive,
            addressLine1 = addressLine1,
            addressLine2 = addressLine2,
            addressLine3 = addressLine3,
            city = city,
            stateProvinceId = stateProvinceId,
            countryId = countryId,
            postalCode = postalCode,
        )
        clientPayload = clientPayload.copy(address = listOf(address))
    }

    // Optional fields
    if (middleName.isNotEmpty()) {
        clientPayload = clientPayload.copy(middlename = middleName)
    }
    if (PhoneNumberUtil.isGlobalPhoneNumber(mobileNumber)) {
        clientPayload = clientPayload.copy(mobileNo = mobileNumber)
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

@Composable
private fun ClientInputTextFields(
    firstName: String,
    middleName: String,
    lastName: String,
    mobileNumber: String,
    externalId: String,
    onFirstNameChange: (String) -> Unit,
    onMiddleNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onMobileNumberChange: (String) -> Unit,
    onExternalIdChange: (String) -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = stringResource(Res.string.feature_client_first_name_mandatory),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = middleName,
            onValueChange = onMiddleNameChange,
            label = stringResource(Res.string.feature_client_middle_name),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = stringResource(Res.string.feature_client_last_name_mandatory),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = mobileNumber,
            onValueChange = onMobileNumberChange,
            label = stringResource(Res.string.feature_client_mobile_no),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = externalId,
            onValueChange = onExternalIdChange,
            label = stringResource(Res.string.feature_client_external_id),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ClientImageSection(selectedImagePath: String?, onImageClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        Image(
            painter = if (selectedImagePath != null) {
                rememberAsyncImagePainter(selectedImagePath)
            } else {
                painterResource(Res.drawable.feature_client_ic_dp_placeholder)
            },
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .clickable { onImageClick() }
                .border(
                    color = MaterialTheme.colorScheme.outline,
                    width = 2.dp,
                    shape = CircleShape,
                )
                .size(80.dp)
                .clip(CircleShape),
        )
    }
}

@Composable
private fun MifosSelectImageDialog(
    onDismissRequest: () -> Unit,
    takeImage: () -> Unit,
    uploadImage: () -> Unit,
    removeImage: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(30.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.feature_client_please_select_action),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { takeImage() },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_take_a_photo),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    onClick = { uploadImage() },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_upload_photo),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    onClick = { removeImage() },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_remove_existing_photo),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressInputTextFields(
    addressLine1: String,
    onAddressLine1Change: (String) -> Unit,
    addressLine2: String,
    onAddressLine2Change: (String) -> Unit,
    addressLine3: String,
    onAddressLine3Change: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    postalCode: String,
    onPostalCodeChange: (String) -> Unit,
    selectedAddressType: String,
    onAddressTypeChanged: (String) -> Unit,
    onAddressTypeSelected: (Int, String) -> Unit,
    addressTypeOptions: List<String>,
    selectedStateName: String,
    onStateNameChanged: (String) -> Unit,
    onStateSelected: (Int, String) -> Unit,
    stateOptions: List<String>,
    selectedCountryName: String,
    onCountryNameChanged: (String) -> Unit,
    onCountrySelected: (Int, String) -> Unit,
    countryOptions: List<String>,
    isAddressActive: Boolean,
    onAddressActiveChange: (Boolean) -> Unit,
) {
    Column {
        MifosTextFieldDropdown(
            value = selectedAddressType,
            onValueChanged = onAddressTypeChanged,
            onOptionSelected = onAddressTypeSelected,
            label = stringResource(Res.string.feature_client_address_type),
            options = addressTypeOptions,
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = addressLine1,
            onValueChange = onAddressLine1Change,
            label = stringResource(Res.string.feature_client_address_line_1),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = addressLine2,
            onValueChange = onAddressLine2Change,
            label = stringResource(Res.string.feature_client_address_line_2),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = addressLine3,
            onValueChange = onAddressLine3Change,
            label = stringResource(Res.string.feature_client_address_line_3),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = city,
            onValueChange = onCityChange,
            label = stringResource(Res.string.feature_client_city),
            error = null,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = postalCode,
            onValueChange = onPostalCodeChange,
            label = stringResource(Res.string.feature_client_postal_code),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = selectedStateName,
            onValueChanged = onStateNameChanged,
            onOptionSelected = onStateSelected,
            options = stateOptions,
            label = stringResource(Res.string.feature_client_state_province),
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = selectedCountryName,
            onValueChanged = onCountryNameChanged,
            onOptionSelected = onCountrySelected,
            options = countryOptions,
            label = stringResource(Res.string.feature_client_country),
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isAddressActive,
                onCheckedChange = { onAddressActiveChange(!isAddressActive) },
            )
            Text(text = stringResource(Res.string.feature_client_address_active))
        }
    }
}

private fun isAllFieldsValid(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    firstName: String,
    middleName: String,
    lastName: String,
    addressTypeId: Int,
): Boolean {
    return when {
        !isFirstNameValid(
            firstName,
            scope,
            snackbarHostState,
        ) -> {
            false
        }

        !isMiddleNameValid(middleName, scope, snackbarHostState) -> {
            false
        }

        !isLastNameValid(lastName, scope, snackbarHostState) -> {
            false
        }

        !isAddressTypeIdValid(addressTypeId, scope, snackbarHostState) -> {
            false
        }

        else -> true
    }
}

private fun isFirstNameValid(
    name: String,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
): Boolean {
    return when {
        name.isEmpty() -> {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = getString(
                        Res.string.feature_client_error_first_name_can_not_be_empty,
                    ),
                )
            }
            return false
        }

        name.contains("[^a-zA-Z ]".toRegex()) -> {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = getString(
                        Res.string.feature_client_error_first_name_should_contain_only_alphabets,
                    ),
                )
            }
            return false
        }

        else -> true
    }
}

private fun isLastNameValid(
    name: String,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
): Boolean {
    return when {
        name.isEmpty() -> {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = getString(
                        Res.string.feature_client_error_last_name_can_not_be_empty,
                    ),
                )
            }
            return false
        }

        name.contains("[^a-zA-Z ]".toRegex()) -> {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = getString(
                        Res.string.feature_client_error_last_name_should_contain_only_alphabets,
                    ),
                )
            }
            return false
        }

        else -> true
    }
}

private fun isMiddleNameValid(
    name: String,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
): Boolean {
    return when {
        name.isEmpty() -> {
            true
        }

        name.contains("[^a-zA-Z ]".toRegex()) -> {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = getString(
                        Res.string.feature_client_error_middle_name_should_contain_only_alphabets,
                    ),
                )
            }
            return false
        }

        else -> true
    }
}

private fun isAddressTypeIdValid(
    addressTypeId: Int,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
): Boolean {
    return when {
        addressTypeId < 0 -> {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = getString(
                        Res.string.feature_client_error_address_type_is_required,
                    ),
                )
            }
            return false
        }

        else -> true
    }
}

private class CreateNewClientScreenPreviewProvider :
    PreviewParameterProvider<CreateNewClientUiState> {
    override val values: Sequence<CreateNewClientUiState>
        get() = sequenceOf(
            CreateNewClientUiState.ShowClientTemplate(
                ClientsTemplateEntity(
                    officeOptions = listOf(),
                    staffOptions = listOf(),
                    genderOptions = listOf(),
                    clientTypeOptions = listOf(),
                    clientClassificationOptions = listOf(),
                    clientLegalFormOptions = listOf(),
                    savingProductOptions = listOf(),
                    dataTables = listOf(),
                ),
                isAddressEnabled = false,
                addressTemplate = AddressTemplate(),
            ),
            CreateNewClientUiState.ShowProgressbar,
            CreateNewClientUiState.ShowClientCreatedSuccessfully(Res.string.feature_client_client_created_successfully),
            CreateNewClientUiState.OnImageUploadSuccess(Res.string.feature_client_Image_Upload_Successful),
            CreateNewClientUiState.ShowWaitingForCheckerApproval(Res.string.feature_client_waiting_for_checker_approval),
        )
}

@Composable
@Preview
private fun PreviewCreateNewClientScreen(
    @PreviewParameter(CreateNewClientScreenPreviewProvider::class) createNewClientUiState: CreateNewClientUiState,
) {
    CreateNewClientScreen(
        uiState = createNewClientUiState,
        onRetry = { },
        officeList = listOf(),
        staffInOffices = listOf(),
        loadStaffInOffice = { },
        navigateBack = { },
        createClient = { },
        uploadImage = { _ -> },
        onImageSelected = {},
    ) { _, _ ->
    }
}
