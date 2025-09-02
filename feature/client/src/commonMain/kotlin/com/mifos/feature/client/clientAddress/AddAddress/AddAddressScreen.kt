package com.mifos.feature.client.clientAddress.AddAddress

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_collateral_failure_title
import androidclient.feature.client.generated.resources.client_collateral_success_message
import androidclient.feature.client.generated.resources.client_collateral_success_title
import androidclient.feature.client.generated.resources.dialog_continue
import androidclient.feature.client.generated.resources.feature_client_add_address
import androidclient.feature.client.generated.resources.feature_client_address_line_1
import androidclient.feature.client.generated.resources.feature_client_address_line_2
import androidclient.feature.client.generated.resources.feature_client_address_line_3
import androidclient.feature.client.generated.resources.feature_client_address_type
import androidclient.feature.client.generated.resources.feature_client_cancel
import androidclient.feature.client.generated.resources.feature_client_city
import androidclient.feature.client.generated.resources.feature_client_country
import androidclient.feature.client.generated.resources.feature_client_postal_code
import androidclient.feature.client.generated.resources.feature_client_state_province
import androidclient.feature.client.generated.resources.feature_client_submit
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.clientAddress.ClientAddressAction
import com.mifos.feature.client.clientAddress.ClientAddressEvent
import com.mifos.feature.client.clientAddress.ClientAddressState
import com.mifos.feature.client.clientAddress.ClientAddressViewModel
import com.mifos.room.entities.client.AddressTemplate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
internal fun AddAddressScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int) -> Unit,
    viewModel: ClientAddressViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientAddressEvent.NavigateBack -> onNavigateBack.invoke()
            ClientAddressEvent.NavigateNext -> onNavigateNext(state.id)
            else -> {}
        }
    }

    ClientAddressDialogs(
        state = state,
        onNavigateBack = onNavigateBack,
        onAction = { viewModel.trySendAction(it) }
    )

}


@Composable
fun ClientAddressDialogs(
    state: ClientAddressState,
    onNavigateBack: () -> Unit,
    onAction: (ClientAddressAction) -> Unit
) {
    when(state.dialogState) {
        is ClientAddressState.DialogState.Loading -> {
            MifosCircularProgress()
        }

        is ClientAddressState.DialogState.ShowStatusDialog -> {
            MifosStatusDialog(
                status = state.dialogState.status,
                btnText = stringResource(Res.string.dialog_continue),
                onConfirm = { onAction(ClientAddressAction.OnNext) },
                successTitle = stringResource(Res.string.client_collateral_success_title),
                successMessage = stringResource(Res.string.client_collateral_success_message),
                failureTitle = stringResource(Res.string.client_collateral_failure_title),
                failureMessage = state.dialogState.msg,
                modifier = Modifier.fillMaxSize(),
            )
        }

        is ClientAddressState.DialogState.Error -> {

        }

        else -> {
            AddAddressForm(
                onNavigateBack = onNavigateBack,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun AddAddressForm(
    onNavigateBack: () -> Unit,
    onAction: (ClientAddressAction) -> Unit
) {

    val addressTemplate = AddressTemplate()
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
    var isAddressActive by rememberSaveable { mutableStateOf(false) }

    val isSubmitEnabled by rememberSaveable {mutableStateOf(true)}

    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current



    MifosScaffold(
        title = "Add Address",
        onBackPressed = { onNavigateBack.invoke() },
        bottomBar = {
            AddAddressFormBottomBar(
                onCancelClick = { onNavigateBack.invoke() },
                onSubmitClick = { onAction(ClientAddressAction.ShowStatusDialog) },
                isSubmitEnabled = isSubmitEnabled
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = DesignToken.padding.largeIncreased,
                    end = DesignToken.padding.largeIncreased,
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
                text = stringResource(Res.string.feature_client_add_address),
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.labelLarge.fontSize,
                letterSpacing = MaterialTheme.typography.labelLarge.letterSpacing,
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (isAddressEnabled && addressTemplate != null) {
                val sortedAddressTypeOptions = addressTemplate.addressTypeIdOptions.sortedBy { it.name }
                val sortedCountryOptions = addressTemplate.countryIdOptions.sortedBy { it.name }
                val sortedStateOptions = addressTemplate.stateProvinceIdOptions.sortedBy { it.name }

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
        }
    }
}


@Composable
private fun AddAddressFormBottomBar(
    onCancelClick: () -> Unit,
    onSubmitClick: () -> Unit,
    isSubmitEnabled: Boolean
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

        MifosOutlinedTextField(
            value = addressLine1,
            onValueChange = onAddressLine1Change,
            label = stringResource(Res.string.feature_client_address_line_1),
            error = null,
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
            error = null,
        )

        Spacer(modifier = Modifier.height(DesignToken.padding.large))

        MifosTextFieldDropdown(
            value = selectedStateName,
            onValueChanged = onStateNameChanged,
            onOptionSelected = onStateSelected,
            options = stateOptions,
            label = stringResource(Res.string.feature_client_state_province),
            readOnly = true,
        )

        MifosTextFieldDropdown(
            value = selectedCountryName,
            onValueChanged = onCountryNameChanged,
            onOptionSelected = onCountrySelected,
            options = countryOptions,
            label = stringResource(Res.string.feature_client_country),
            readOnly = true,
        )

        MifosOutlinedTextField(
            value = postalCode,
            onValueChange = onPostalCodeChange,
            label = stringResource(Res.string.feature_client_postal_code),
            error = null,
            keyboardType = KeyboardType.Number,
        )

//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Checkbox(
//                checked = isAddressActive,
//                onCheckedChange = { onAddressActiveChange(!isAddressActive) },
//            )
//            Text(text = stringResource(Res.string.feature_client_address_active))
//        }
    }
}
