/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientAddress.addAddress

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.dialog_continue
import androidclient.feature.client.generated.resources.feature_client_add_address
import androidclient.feature.client.generated.resources.feature_client_address_city_error
import androidclient.feature.client.generated.resources.feature_client_address_country_error
import androidclient.feature.client.generated.resources.feature_client_address_creation_failure_title
import androidclient.feature.client.generated.resources.feature_client_address_line_1
import androidclient.feature.client.generated.resources.feature_client_address_line_2
import androidclient.feature.client.generated.resources.feature_client_address_line_3
import androidclient.feature.client.generated.resources.feature_client_address_line_error
import androidclient.feature.client.generated.resources.feature_client_address_postal_code_error
import androidclient.feature.client.generated.resources.feature_client_address_state_province_error
import androidclient.feature.client.generated.resources.feature_client_address_type
import androidclient.feature.client.generated.resources.feature_client_address_type_error
import androidclient.feature.client.generated.resources.feature_client_cancel
import androidclient.feature.client.generated.resources.feature_client_city
import androidclient.feature.client.generated.resources.feature_client_country
import androidclient.feature.client.generated.resources.feature_client_create_address_success_message
import androidclient.feature.client.generated.resources.feature_client_postal_code
import androidclient.feature.client.generated.resources.feature_client_state_province
import androidclient.feature.client.generated.resources.feature_client_submit
import androidclient.feature.client.generated.resources.feature_client_success_title
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.network.model.PostClientAddressRequest
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.clientAddress.ClientAddressAction
import com.mifos.feature.client.clientAddress.ClientAddressEvent
import com.mifos.feature.client.clientAddress.ClientAddressState
import com.mifos.feature.client.clientAddress.ClientAddressViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AddAddressScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int) -> Unit,
    navController: NavController,
    viewModel: ClientAddressViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientAddressEvent.NavigateBack -> onNavigateBack.invoke()
            ClientAddressEvent.NavigateNext -> onNavigateNext(state.id)
            else -> Unit
        }
    }

    ClientAddressDialogs(
        state = state,
        onAction = { viewModel.trySendAction(it) },
    )

    AddAddressScaffold(
        state = state,
        onNavigateBack = onNavigateBack,
        createAddress = { addressTypeId, addressRequest ->
            viewModel.createClientAddress(
                addressPayload = addressRequest,
                addressTypeId = addressTypeId,
            )
        },
        onAction = { viewModel.trySendAction(it) },
        navController = navController,
    )
}

@Composable
fun ClientAddressDialogs(
    state: ClientAddressState,
    onAction: (ClientAddressAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientAddressState.DialogState.Error -> {
            MifosSweetError(
                message = state.dialogState.message,
                onclick = { onAction(ClientAddressAction.OnRetry) },
            )
        }
        else -> Unit
    }
}

@Composable
private fun AddAddressScaffold(
    state: ClientAddressState,
    onNavigateBack: () -> Unit,
    createAddress: (addressTypeId: Int, addressPayload: PostClientAddressRequest) -> Unit,
    navController: NavController,
    onAction: (ClientAddressAction) -> Unit,
) {
    val isAddressEnabled = true
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

    var addressTypeError: String? by rememberSaveable { mutableStateOf(null) }
    var addressLine1Error: String? by rememberSaveable { mutableStateOf(null) }
    var cityError: String? by rememberSaveable { mutableStateOf(null) }
    var stateProvinceError: String? by rememberSaveable { mutableStateOf(null) }
    var countryError: String? by rememberSaveable { mutableStateOf(null) }
    var postalCodeError: String? by rememberSaveable { mutableStateOf(null) }

    var isSubmitEnabled by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = listOf(selectedAddressTypeId, addressLine1, city, selectedStateProvinceId, selectedCountryId, postalCode)) {
        isSubmitEnabled = areRequiredAddressFieldsFilled(
            addressTypeId = selectedAddressTypeId,
            addressLine1 = addressLine1,
            city = city,
            stateProvinceId = selectedStateProvinceId,
            countryId = selectedCountryId,
            postalCode = postalCode,
        )
    }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    MifosScaffold(
        title = "Add Address",
        onBackPressed = { onNavigateBack.invoke() },
        bottomBar = {
            when (state.addressFormScreenState) {
                is ClientAddressState.AddressFormScreenState.ShowAddressForm -> {
                    AddAddressFormBottomBar(
                        onCancelClick = { onNavigateBack.invoke() },
                        onSubmitClick = {
                            val addressErrors = validateFields(
                                addressTypeId = selectedAddressTypeId,
                                addressLine1 = addressLine1,
                                city = city,
                                stateProvinceId = selectedStateProvinceId,
                                countryId = selectedCountryId,
                                postalCode = postalCode,
                                scope = scope,
                            )

                            addressTypeError = addressErrors.addressTypeError
                            addressLine1Error = addressErrors.addressLine1Error
                            cityError = addressErrors.cityError
                            stateProvinceError = addressErrors.stateProvinceError
                            countryError = addressErrors.countryError
                            postalCodeError = addressErrors.postalCodeError

                            if (isSubmitEnabled && addressErrors.isValid) {
                                createAddress.invoke(
                                    selectedAddressTypeId,
                                    PostClientAddressRequest(
                                        addressLine1 = addressLine1,
                                        addressLine2 = addressLine2,
                                        addressLine3 = addressLine3,
                                        city = city,
                                        stateProvinceId = selectedStateProvinceId,
                                        countryId = selectedCountryId,
                                        postalCode = postalCode,
                                    ),
                                )
                            }
                        },
                        isSubmitEnabled = isSubmitEnabled,
                    )
                }
                else -> Unit
            }
        },
    ) { paddingValues ->
        when (state.addressFormScreenState) {
            is ClientAddressState.AddressFormScreenState.Loading -> MifosProgressIndicator()
            is ClientAddressState.AddressFormScreenState.ShowAddressForm -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding(),
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    keyboardController?.hide()
                                },
                            )
                        },
                    verticalArrangement = Arrangement.Center,
                ) {
                    MifosBreadcrumbNavBar(navController)
                    LazyColumn(
                        modifier = Modifier.padding(
                            start = DesignToken.padding.large,
                            end = DesignToken.padding.large,
                        ),
                    ) {
                        item {
                            Text(
                                text = stringResource(Res.string.feature_client_add_address),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                                letterSpacing = MaterialTheme.typography.labelLarge.letterSpacing,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            if (isAddressEnabled && state.addressTemplate != null) {
                                val sortedAddressTypeOptions = state.addressTemplate.addressTypeIdOptions.sortedBy { it.name }
                                val sortedCountryOptions = state.addressTemplate.countryIdOptions.sortedBy { it.name }
                                val sortedStateOptions = state.addressTemplate.stateProvinceIdOptions.sortedBy { it.name }

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
                                    addressTypeError = addressTypeError,
                                    addressLineError = addressLine1Error,
                                    cityError = cityError,
                                    stateProvinceError = stateProvinceError,
                                    countryError = countryError,
                                    postalCodeError = postalCodeError,
                                )
                            }
                        }
                    }
                }
            }

            is ClientAddressState.AddressFormScreenState.ShowStatusDialog -> {
                MifosStatusDialog(
                    status = state.addressFormScreenState.status,
                    btnText = stringResource(Res.string.dialog_continue),
                    onConfirm = { if (state.addressFormScreenState.status == ResultStatus.SUCCESS) onAction(ClientAddressAction.OnNext) else onAction(ClientAddressAction.OnRetry) },
                    successTitle = stringResource(Res.string.feature_client_success_title),
                    successMessage = stringResource(Res.string.feature_client_create_address_success_message),
                    failureTitle = stringResource(Res.string.feature_client_address_creation_failure_title),
                    failureMessage = state.addressFormScreenState.msg,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun AddAddressFormBottomBar(
    onCancelClick: () -> Unit,
    onSubmitClick: () -> Unit,
    isSubmitEnabled: Boolean,
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
                enabled = isSubmitEnabled,
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
    addressTypeError: String?,
    addressLineError: String?,
    cityError: String?,
    stateProvinceError: String?,
    countryError: String?,
    postalCodeError: String?,
) {
    Column {
        MifosTextFieldDropdown(
            value = selectedAddressType,
            onValueChanged = onAddressTypeChanged,
            onOptionSelected = onAddressTypeSelected,
            label = stringResource(Res.string.feature_client_address_type),
            options = addressTypeOptions,
            readOnly = true,
            errorMessage = addressTypeError,
        )

        MifosOutlinedTextField(
            value = addressLine1,
            onValueChange = onAddressLine1Change,
            label = stringResource(Res.string.feature_client_address_line_1),
            error = addressLineError,
        )

        Spacer(modifier = Modifier.height(DesignToken.padding.large))

        MifosOutlinedTextField(
            value = addressLine2,
            onValueChange = onAddressLine2Change,
            label = stringResource(Res.string.feature_client_address_line_2),
            error = null,
        )

        Spacer(modifier = Modifier.height(DesignToken.padding.large))

        MifosOutlinedTextField(
            value = addressLine3,
            onValueChange = onAddressLine3Change,
            label = stringResource(Res.string.feature_client_address_line_3),
            error = null,
        )

        Spacer(modifier = Modifier.height(DesignToken.padding.large))

        MifosOutlinedTextField(
            value = city,
            onValueChange = onCityChange,
            label = stringResource(Res.string.feature_client_city),
            error = cityError,
        )

        Spacer(modifier = Modifier.height(DesignToken.padding.large))

        MifosTextFieldDropdown(
            value = selectedStateName,
            onValueChanged = onStateNameChanged,
            onOptionSelected = onStateSelected,
            options = stateOptions,
            label = stringResource(Res.string.feature_client_state_province),
            readOnly = true,
            errorMessage = stateProvinceError,
        )

        MifosTextFieldDropdown(
            value = selectedCountryName,
            onValueChanged = onCountryNameChanged,
            onOptionSelected = onCountrySelected,
            options = countryOptions,
            label = stringResource(Res.string.feature_client_country),
            readOnly = true,
            errorMessage = countryError,
        )

        MifosOutlinedTextField(
            value = postalCode,
            onValueChange = onPostalCodeChange,
            label = stringResource(Res.string.feature_client_postal_code),
            error = postalCodeError,
            keyboardType = KeyboardType.Number,
        )
    }
}

private data class AddressValidationResult(
    val addressTypeError: String? = null,
    val addressLine1Error: String? = null,
    val cityError: String? = null,
    val stateProvinceError: String? = null,
    val countryError: String? = null,
    val postalCodeError: String? = null,
) {
    val isValid: Boolean
        get() = listOf(
            addressTypeError,
            addressLine1Error,
            cityError,
            stateProvinceError,
            countryError,
            postalCodeError,
        ).all { it == null }
}

private fun validateAddressFields(
    addressTypeId: Int,
    addressLine1: String,
    city: String,
    stateProvinceId: Int,
    countryId: Int,
    postalCode: String,
    scope: CoroutineScope,
): AddressValidationResult {
    var result = AddressValidationResult()
    scope.launch {
        val addressTypeError =
            if (addressTypeId <= 0) getString(Res.string.feature_client_address_type_error) else null

        val addressLine1Error =
            if (addressLine1.isBlank()) getString(Res.string.feature_client_address_line_error) else null

        val cityError =
            if (city.isBlank()) getString(Res.string.feature_client_address_city_error) else null

        val stateProvinceError =
            if (stateProvinceId <= 0) getString(Res.string.feature_client_address_state_province_error) else null

        val countryError =
            if (countryId <= 0) getString(Res.string.feature_client_address_country_error) else null

        val postalCodeError =
            if (postalCode.isBlank()) getString(Res.string.feature_client_address_postal_code_error) else null

        result = AddressValidationResult(
            addressTypeError = addressTypeError,
            addressLine1Error = addressLine1Error,
            cityError = cityError,
            stateProvinceError = stateProvinceError,
            countryError = countryError,
            postalCodeError = postalCodeError,
        )
    }
    return result
}

private fun areRequiredAddressFieldsFilled(
    addressTypeId: Int,
    addressLine1: String,
    city: String,
    stateProvinceId: Int,
    countryId: Int,
    postalCode: String,
): Boolean {
    return addressTypeId > 0 &&
        addressLine1.isNotBlank() &&
        city.isNotBlank() &&
        stateProvinceId > 0 &&
        countryId > 0 &&
        postalCode.isNotBlank()
}

private fun validateFields(
    addressTypeId: Int,
    addressLine1: String,
    city: String,
    stateProvinceId: Int,
    countryId: Int,
    postalCode: String,
    scope: CoroutineScope,
): AddressValidationResult {
    return validateAddressFields(
        addressTypeId = addressTypeId,
        addressLine1 = addressLine1,
        city = city,
        stateProvinceId = stateProvinceId,
        countryId = countryId,
        postalCode = postalCode,
        scope = scope,
    )
}
