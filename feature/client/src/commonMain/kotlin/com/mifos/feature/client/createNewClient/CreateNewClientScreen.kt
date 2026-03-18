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
import androidclient.feature.client.generated.resources.feature_client_country
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
import androidclient.feature.client.generated.resources.feature_client_ic_dp_placeholder
import androidclient.feature.client.generated.resources.feature_client_last_name_mandatory
import androidclient.feature.client.generated.resources.feature_client_middle_name
import androidclient.feature.client.generated.resources.feature_client_no_staff_associated_with_office
import androidclient.feature.client.generated.resources.feature_client_office_name_mandatory
import androidclient.feature.client.generated.resources.feature_client_phone_no
import androidclient.feature.client.generated.resources.feature_client_please_select_action
import androidclient.feature.client.generated.resources.feature_client_postal_code
import androidclient.feature.client.generated.resources.feature_client_remove_existing_photo
import androidclient.feature.client.generated.resources.feature_client_select_date
import androidclient.feature.client.generated.resources.feature_client_staff
import androidclient.feature.client.generated.resources.feature_client_state_province
import androidclient.feature.client.generated.resources.feature_client_submit
import androidclient.feature.client.generated.resources.feature_client_take_a_photo
import androidclient.feature.client.generated.resources.feature_client_upload_photo
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
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosCheckBox
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.util.EventsEffect
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
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun CreateNewClientScreen(
    navigateBack: () -> Unit,
    navigateToClientDetails: (Int) -> Unit,
    hasDatatables: (datatables: List<DataTableEntity>, clientPayload: ClientPayloadEntity) -> Unit,
    viewModel: CreateNewClientViewModel = koinViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is CreateNewClientEvent.NavigateBack -> navigateBack()
            is CreateNewClientEvent.NavigateToClientDetails -> navigateToClientDetails(event.clientId)
            is CreateNewClientEvent.ShowSnackBar -> {
                snackbarHostState.showSnackbar(event.message)
            }

            is CreateNewClientEvent.HasDatatables -> {
                hasDatatables(event.datatables, event.clientPayload)
            }
        }
    }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    CreateNewClientScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = {
            viewModel.trySendAction(it)
        },
    )
}

@Composable
internal fun CreateNewClientScreen(
    state: CreateNewClientState,
    snackbarHostState: SnackbarHostState,
    onAction: (CreateNewClientAction) -> Unit,
) {
    val scope = rememberCoroutineScope()

    MifosScaffold(
        snackbarHostState = snackbarHostState,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            when (state.screenState) {
                is CreateNewClientState.ScreenState.Error -> {
                    MifosSweetError(
                        message = state.screenState.message,
                        onclick = {
                            onAction(CreateNewClientAction.Retry)
                        },
                    )
                }

                CreateNewClientState.ScreenState.Loading -> {
                    MifosProgressIndicator()
                }

                CreateNewClientState.ScreenState.Success -> {
                    if (state.clientsTemplate != null) {
                        CreateNewClientContent(
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            officeList = state.officeOptions,
                            staffInOffices = state.staffInOffices,
                            clientTemplate = state.clientsTemplate,
                            addressTemplate = state.addressTemplate,
                            isAddressEnabled = state.isAddressEnabled,
                            formState = state.formState,
                            onAction = onAction,
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (state.showOverLayProgressIndicator) {
                MifosProgressIndicatorOverlay()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun CreateNewClientContent(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    officeList: List<OfficeEntity>,
    staffInOffices: List<StaffEntity>,
    clientTemplate: ClientsTemplateEntity,
    addressTemplate: AddressTemplate?,
    isAddressEnabled: Boolean,
    formState: CreateNewClientState.ClientFormState,
    onAction: (CreateNewClientAction) -> Unit,
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    val galleryLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { file ->
        file?.let {
            onAction(CreateNewClientAction.UpdateSelectedImageFile(it))
        }
    }

    val cameraLauncher = rememberPlatformCameraLauncher { file ->
        file?.let {
            onAction(CreateNewClientAction.UpdateSelectedImageFile(it))
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (officeList.isNotEmpty()) {
            officeList[0].id.let { onAction(CreateNewClientAction.LoadStaffInOffices(it)) }
        }
    }

    LaunchedEffect(key1 = staffInOffices) {
        if (staffInOffices.isEmpty()) {
            snackbarHostState.showSnackbar(
                message = getString(
                    Res.string.feature_client_no_staff_associated_with_office,
                ),
            )
            onAction(CreateNewClientAction.UpdateStaff("", 0))
        }
    }

    val activateDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = formState.activationDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        },
    )
    val dateOfBirthDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
            }
        },
    )

    if (formState.showImagePickerDialog) {
        MifosSelectImageDialog(
            onDismissRequest = {
                onAction(CreateNewClientAction.ToggleImagePickerDialog(false))
            },
            takeImage = {
                onAction(CreateNewClientAction.ToggleImagePickerDialog(false))
                cameraLauncher.launch()
            },
            uploadImage = {
                onAction(CreateNewClientAction.ToggleImagePickerDialog(false))
                galleryLauncher.launch()
            },
            removeImage = {
                onAction(CreateNewClientAction.ToggleImagePickerDialog(false))
                onAction(CreateNewClientAction.UpdateSelectedImageFile(null))
            },
        )
    }

    if (formState.showActivateDatepicker || formState.showDateOfBirthDatepicker) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(CreateNewClientAction.ToggleActivationDatePicker(false))
                onAction(CreateNewClientAction.ToggleDateOfBirthPicker(false))
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        if (formState.showActivateDatepicker) {
                            activateDatePickerState.selectedDateMillis?.let {
                                onAction(CreateNewClientAction.UpdateActivationDate(it))
                            }
                        } else {
                            dateOfBirthDatePickerState.selectedDateMillis?.let {
                                onAction(CreateNewClientAction.UpdateDateOfBirth(it))
                            }
                        }

                        onAction(CreateNewClientAction.ToggleActivationDatePicker(false))
                        onAction(CreateNewClientAction.ToggleDateOfBirthPicker(false))
                    },
                ) {
                    Text(stringResource(Res.string.feature_client_select_date))
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(CreateNewClientAction.ToggleActivationDatePicker(false))
                        onAction(CreateNewClientAction.ToggleDateOfBirthPicker(false))
                    },
                ) {
                    Text(stringResource(Res.string.feature_client_cancel))
                }
            },
        ) {
            DatePicker(
                state = if (formState.showActivateDatepicker) {
                    activateDatePickerState
                } else {
                    dateOfBirthDatePickerState
                },
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(KptTheme.spacing.md)
            .verticalScroll(state = scrollState),
    ) {
        ClientImageSection(
            selectedImage = formState.selectedImage,
        ) {
            onAction(CreateNewClientAction.ToggleImagePickerDialog(true))
        }

        ClientInputTextFields(
            firstName = formState.firstName,
            middleName = formState.middleName,
            lastName = formState.lastName,
            mobileNumber = formState.mobileNumber,
            externalId = formState.externalId,

            onFirstNameChange = {
                onAction(CreateNewClientAction.UpdateFirstName(it))
            },
            onMiddleNameChange = {
                onAction(CreateNewClientAction.UpdateMiddleName(it))
            },
            onLastNameChange = {
                onAction(CreateNewClientAction.UpdateLastName(it))
            },
            onMobileNumberChange = {
                onAction(CreateNewClientAction.UpdateMobileNumber(it))
            },
            onExternalIdChange = {
                onAction(CreateNewClientAction.UpdateExternalId(it))
            },
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        clientTemplate.genderOptions?.let { list ->
            MifosTextFieldDropdown(
                value = formState.gender,
                onValueChanged = { },
                onOptionSelected = { index, value ->
                    onAction(
                        CreateNewClientAction.UpdateGender(
                            name = value,
                            id = list[index].id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_client_gender),
                options = list.map { it.name },
                readOnly = true,
            )
        }

        MifosDatePickerTextField(
            value = formState.dateOfBirth
                ?.let { DateHelper.getDateAsStringFromLong(it) } ?: "",
            label = stringResource(Res.string.feature_client_dob),
            openDatePicker = {
                onAction(
                    CreateNewClientAction.ToggleDateOfBirthPicker(
                        !formState.showDateOfBirthDatepicker,
                    ),
                )
            },
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        clientTemplate.clientTypeOptions?.let { list ->
            val sorted = list.sortedBy { it.name }

            MifosTextFieldDropdown(
                value = formState.clientType,
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(
                        CreateNewClientAction.UpdateClientType(
                            name = value,
                            id = sorted[index].id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_client_client),
                options = sorted.map { it.name },
                readOnly = true,
            )
        }

        clientTemplate.clientClassificationOptions?.let { list ->
            val sorted = list.sortedBy { it.name }

            MifosTextFieldDropdown(
                value = formState.clientClassification,
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(
                        CreateNewClientAction.UpdateClientClassification(
                            name = value,
                            id = sorted[index].id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_client_client_classification),
                options = sorted.map { it.name },
                readOnly = true,
            )
        }

        val sortedOffices = officeList.sortedBy { it.name }

        MifosTextFieldDropdown(
            value = formState.selectedOffice,
            onValueChanged = {},
            onOptionSelected = { index, value ->
                val officeId = sortedOffices[index].id

                onAction(
                    CreateNewClientAction.UpdateOffice(
                        name = value,
                        id = officeId,
                    ),
                )

                onAction(CreateNewClientAction.LoadStaffInOffices(officeId))
            },
            label = stringResource(Res.string.feature_client_office_name_mandatory),
            options = sortedOffices.map { it.name.toString() },
            readOnly = true,
        )

        AnimatedVisibility(
            visible = staffInOffices.isNotEmpty(),
        ) {
            val sortedStaff = staffInOffices.sortedBy { it.displayName }

            MifosTextFieldDropdown(
                value = formState.staff,
                onValueChanged = {},
                onOptionSelected = { index, value ->
                    onAction(
                        CreateNewClientAction.UpdateStaff(
                            name = value,
                            id = sortedStaff[index].id,
                        ),
                    )
                },
                label = stringResource(Res.string.feature_client_staff),
                options = sortedStaff.map { it.displayName.toString() },
                readOnly = true,
            )
        }

        MifosCheckBox(
            checked = formState.isActive,
            onCheckChanged = {
                onAction(CreateNewClientAction.ToggleClientActive(it))
            },
            text = stringResource(Res.string.feature_client_client_active),
        )

        AnimatedVisibility(
            visible = formState.isActive,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top,
            ) + fadeIn(
                initialAlpha = 0.3f,
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut(),
        ) {
            Column {
                Spacer(modifier = Modifier.height(KptTheme.spacing.md))

                MifosDatePickerTextField(
                    value = DateHelper.getDateAsStringFromLong(formState.activationDate),
                    label = stringResource(Res.string.feature_client_center_submission_date),
                    openDatePicker = {
                        onAction(
                            CreateNewClientAction.ToggleActivationDatePicker(
                                !formState.showActivateDatepicker,
                            ),
                        )
                    },
                )
            }
        }

        if (isAddressEnabled && addressTemplate != null) {
            val sortedAddressTypeOptions = addressTemplate.addressTypeIdOptions.sortedBy { it.name }
            val sortedCountryOptions = addressTemplate.countryIdOptions.sortedBy { it.name }
            val sortedStateOptions = addressTemplate.stateProvinceIdOptions.sortedBy { it.name }

            HorizontalDivider(modifier = Modifier.padding(KptTheme.spacing.md))

            Text(
                stringResource(Res.string.feature_client_address),
                Modifier.padding(horizontal = KptTheme.spacing.md),
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            AddressInputTextFields(
                addressLine1 = formState.addressLine1,
                onAddressLine1Change = {
                    onAction(CreateNewClientAction.UpdateAddressLine1(it))
                },
                addressLine2 = formState.addressLine2,
                onAddressLine2Change = {
                    onAction(CreateNewClientAction.UpdateAddressLine2(it))
                },
                addressLine3 = formState.addressLine3,
                onAddressLine3Change = {
                    onAction(CreateNewClientAction.UpdateAddressLine3(it))
                },
                city = formState.city,
                onCityChange = {
                    onAction(CreateNewClientAction.UpdateCity(it))
                },
                postalCode = formState.postalCode,
                onPostalCodeChange = {
                    onAction(CreateNewClientAction.UpdatePostalCode(it))
                },
                selectedAddressType = formState.selectedAddressType,
                onAddressTypeChanged = {
                    onAction(
                        CreateNewClientAction.UpdateAddressType(
                            it,
                            formState.selectedAddressTypeId,
                        ),
                    )
                },
                onAddressTypeSelected = { index, value ->
                    onAction(
                        CreateNewClientAction.UpdateAddressType(
                            name = value,
                            id = sortedAddressTypeOptions[index].id,
                        ),
                    )
                },
                addressTypeOptions = sortedAddressTypeOptions.map { it.name },
                selectedStateName = formState.selectedStateName,
                onStateNameChanged = {
                    onAction(
                        CreateNewClientAction.UpdateState(
                            it,
                            formState.selectedStateProvinceId,
                        ),
                    )
                },
                onStateSelected = { index, value ->
                    onAction(
                        CreateNewClientAction.UpdateState(
                            name = value,
                            id = sortedStateOptions[index].id,
                        ),
                    )
                },
                stateOptions = sortedStateOptions.map { it.name },
                selectedCountryName = formState.selectedCountryName,
                onCountryNameChanged = {
                    onAction(
                        CreateNewClientAction.UpdateCountry(
                            it,
                            formState.selectedCountryId,
                        ),
                    )
                },
                onCountrySelected = { index, value ->
                    onAction(
                        CreateNewClientAction.UpdateCountry(
                            name = value,
                            id = sortedCountryOptions[index].id,
                        ),
                    )
                },
                countryOptions = sortedCountryOptions.map { it.name },
                isAddressActive = formState.isAddressActive,
                onAddressActiveChange = {
                    onAction(CreateNewClientAction.ToggleAddressActive(it))
                },
            )
        }
        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosButton(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(DesignToken.spacing.dp46),
            onClick = {
                onAction(CreateNewClientAction.CreateClient)
            },
        ) {
            Text(text = stringResource(Res.string.feature_client_submit))
        }
    }
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
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        MifosOutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = stringResource(Res.string.feature_client_first_name_mandatory),
            error = null,
        )

        MifosOutlinedTextField(
            value = middleName,
            onValueChange = onMiddleNameChange,
            label = stringResource(Res.string.feature_client_middle_name),
            error = null,
        )

        MifosOutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = stringResource(Res.string.feature_client_last_name_mandatory),
            error = null,
        )

        MifosOutlinedTextField(
            value = mobileNumber,
            onValueChange = onMobileNumberChange,
            label = stringResource(Res.string.feature_client_phone_no),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        MifosOutlinedTextField(
            value = externalId,
            onValueChange = onExternalIdChange,
            label = stringResource(Res.string.feature_client_external_id),
            error = null,
        )
    }
}

@Composable
private fun ClientImageSection(
    selectedImage: PlatformFile?,
    onImageClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = KptTheme.spacing.md),
    ) {
        val imageBitmapState = produceState<ImageBitmap?>(
            initialValue = null,
            key1 = selectedImage,
        ) {
            value = selectedImage?.toImageBitmap()
        }

        val painter = when {
            imageBitmapState.value != null -> {
                BitmapPainter(imageBitmapState.value!!)
            }

            else -> painterResource(Res.drawable.feature_client_ic_dp_placeholder)
        }

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .clickable { onImageClick() }
                .border(
                    color = KptTheme.colorScheme.outline,
                    width = DesignToken.strokes.dp2,
                    shape = CircleShape,
                )
                .size(DesignToken.sizes.dp80)
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
            colors = CardDefaults.cardColors(KptTheme.colorScheme.surface),
            shape = DesignToken.shapes.largeIncreased,
        ) {
            Column(
                modifier = Modifier
                    .padding(DesignToken.padding.dp30),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.feature_client_please_select_action),
                    modifier = Modifier.fillMaxWidth(),
                    style = KptTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(DesignToken.spacing.largeIncreased))

                Button(
                    onClick = { takeImage() },
                    colors = ButtonDefaults.buttonColors(KptTheme.colorScheme.secondary),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_take_a_photo),
                        modifier = Modifier.fillMaxWidth(),
                        style = KptTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    onClick = { uploadImage() },
                    colors = ButtonDefaults.buttonColors(KptTheme.colorScheme.secondary),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_upload_photo),
                        modifier = Modifier.fillMaxWidth(),
                        style = KptTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    onClick = { removeImage() },
                    colors = ButtonDefaults.buttonColors(KptTheme.colorScheme.secondary),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_remove_existing_photo),
                        modifier = Modifier.fillMaxWidth(),
                        style = KptTheme.typography.bodyLarge,
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

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosOutlinedTextField(
            value = addressLine1,
            onValueChange = onAddressLine1Change,
            label = stringResource(Res.string.feature_client_address_line_1),
            error = null,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosOutlinedTextField(
            value = addressLine2,
            onValueChange = onAddressLine2Change,
            label = stringResource(Res.string.feature_client_address_line_2),
            error = null,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosOutlinedTextField(
            value = addressLine3,
            onValueChange = onAddressLine3Change,
            label = stringResource(Res.string.feature_client_address_line_3),
            error = null,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosOutlinedTextField(
            value = city,
            onValueChange = onCityChange,
            label = stringResource(Res.string.feature_client_city),
            error = null,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosOutlinedTextField(
            value = postalCode,
            onValueChange = onPostalCodeChange,
            label = stringResource(Res.string.feature_client_postal_code),
            error = null,
            keyboardType = KeyboardType.Number,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosTextFieldDropdown(
            value = selectedStateName,
            onValueChanged = onStateNameChanged,
            onOptionSelected = onStateSelected,
            options = stateOptions,
            label = stringResource(Res.string.feature_client_state_province),
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        MifosTextFieldDropdown(
            value = selectedCountryName,
            onValueChanged = onCountryNameChanged,
            onOptionSelected = onCountrySelected,
            options = countryOptions,
            label = stringResource(Res.string.feature_client_country),
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

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
    isAddressEnabled: Boolean,
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

        isAddressEnabled && !isAddressTypeIdValid(addressTypeId, scope, snackbarHostState) -> {
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
        addressTypeId <= 0 -> {
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
    PreviewParameterProvider<CreateNewClientState.ScreenState> {
    override val values: Sequence<CreateNewClientState.ScreenState>
        get() = sequenceOf(
            CreateNewClientState.ScreenState.Loading,
            CreateNewClientState.ScreenState.Success,
            CreateNewClientState.ScreenState.Error("Some thing went wrong"),
        )
}

@Composable
@Preview
private fun PreviewCreateNewClientScreen(
    @PreviewParameter(CreateNewClientScreenPreviewProvider::class) createNewClientUiState: CreateNewClientState.ScreenState,
) {
    CreateNewClientScreen(
        state = CreateNewClientState(
            screenState = createNewClientUiState,
        ),
        snackbarHostState = remember { SnackbarHostState() },
    ) {
    }
}
