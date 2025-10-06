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

package com.mifos.feature.client.clientCharges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.action_view
import androidclient.feature.client.generated.resources.add_charge_title
import androidclient.feature.client.generated.resources.feature_client_charge_amount
import androidclient.feature.client.generated.resources.feature_client_charge_cancel
import androidclient.feature.client.generated.resources.feature_client_charge_created_successfully
import androidclient.feature.client.generated.resources.feature_client_charge_dialog
import androidclient.feature.client.generated.resources.feature_client_charge_id
import androidclient.feature.client.generated.resources.feature_client_charge_invalid_amount_format
import androidclient.feature.client.generated.resources.feature_client_charge_name
import androidclient.feature.client.generated.resources.feature_client_charge_select
import androidclient.feature.client.generated.resources.feature_client_charge_submit
import androidclient.feature.client.generated.resources.feature_client_charges
import androidclient.feature.client.generated.resources.feature_client_due_date
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_charges
import androidclient.feature.client.generated.resources.feature_client_message_field_required
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.PagingData
import com.mifos.core.common.utils.Constants.DATE_FORMAT_LONG
import com.mifos.core.common.utils.Constants.LOCALE_EN
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.common.utils.formatDate
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.template.client.ChargeTemplate
import com.mifos.core.ui.components.MifosActionsChargeListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosIcon
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.util.DevicePreview
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountAction
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun ClientChargesScreen(
    navigateBack: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ClientChargesViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val chargeDialogState = state.chargeDialogState
    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientChargesEvent.NavigateBack -> navigateBack()
        }
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()

    // Show success snackbar when charge is created
    LaunchedEffect(state.showSuccessSnackbar) {
        if (state.showSuccessSnackbar) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = getString(Res.string.feature_client_charge_created_successfully),
                )
            }
            viewModel.trySendAction(ClientChargesAction.CloseDialog)
        }
    }

    ClientChargesScaffold(
        state = state,
        navController = navController,
        onAction = remember { viewModel::trySendAction },
        snackbarHostState = snackbarHostState,
        pullRefreshState = pullRefreshState
    )
}

@Composable
fun ClientChargesScaffold(
    state: ClientChargesState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onAction: (ClientChargesAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    pullRefreshState: PullToRefreshState
) {
    LaunchedEffect(Unit) {
        onAction(ClientChargesAction.LoadChargeTemplate)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        MifosBreadcrumbNavBar(navController)
        PullToRefreshBox(
            state = pullRefreshState,
            onRefresh = { onAction(ClientChargesAction.Refresh) },
            isRefreshing = state.isRefreshing,
        ) {
            when {
                state.isLoading -> MifosProgressIndicator()
                state.error != null -> {
                    MifosSweetError(
                        message = state.error!!,
                    )
                }
                else -> {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                        ) {

                            ClientAddChargesDialogs(state, onAction)

                            ChargeDialogScreen(
                            state = state.chargeDialogState,
                            onDismiss = { onAction(ClientChargesAction.CloseDialog) },
                            onCreateCharge = { payload -> onAction(ClientChargesAction.CreateCharge(payload)) },
                            onRetry = { onAction(ClientChargesAction.LoadChargeTemplate) },
                            onChargeCreated = {
                                onAction(ClientChargesAction.OnRetry)
                            },
                        )


                        }
                        MifosRowWithTextAndButton(
                            onBtnClick = {
                                onAction(ClientChargesAction.ShowCharges)
                            },
                            btnText = stringResource(Res.string.action_view),
                            text =  state.addedCharges.size.toString() + " " + stringResource(Res.string.feature_client_charges),
                            btnEnabled = state.addedCharges.isNotEmpty(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowChargesContent(
    state: ClientChargesState,
    onAction: (ClientChargesAction) -> Unit,
) {
    Column {
        when (val charges = state.chargesFlow) {
            is Flow<*> -> {
                ClientChargeContent(
                    pagingFlow = charges as Flow<PagingData<ChargesEntity>>,
                    onAction = onAction,
                )
            }

            null -> MifosProgressIndicator()
            else -> {
                MifosProgressIndicator()
            }
        }
    }
}

@Composable
private fun ClientAddChargesDialogs(
    state: ClientChargesState,
    onAction: (ClientChargesAction) -> Unit
) {
    when {
        state.showCharges -> ShowChargesContent(state, onAction)
//        state.showAddCharges -> ChargeDialogScreen(
//            state = state.chargeDialogState,
//            onDismiss = { onAction(ClientChargesAction.CloseDialog) },
//            onCreateCharge = { payload -> onAction(ClientChargesAction.CreateCharge(payload)) },
//            onRetry = { onAction(ClientChargesAction.LoadChargeTemplate) },
//            onChargeCreated = {
//                onAction(ClientChargesAction.OnRetry)
//            },
//        )
    }
}

@Composable
fun ChargesItems(charges: ChargesEntity) {
    val currencyCode = charges.currency?.code ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        MifosCenterDetailsText(
            stringResource(Res.string.feature_client_charge_id),
            charges.chargeId.toString(),
        )
        MifosCenterDetailsText(
            stringResource(Res.string.feature_client_charge_name),
            charges.name ?: "",
        )
        MifosCenterDetailsText(
            stringResource(Res.string.feature_client_charge_amount),
            CurrencyFormatter.format(
                charges.amount,
                currencyCode,
                2,
            ),
        )
        MifosCenterDetailsText(
            stringResource(Res.string.feature_client_due_date),
            DateHelper.getDateAsString(charges.dueDate ?: emptyList()),
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun MifosCenterDetailsText(field: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = field,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
expect fun ClientChargeContent(
    pagingFlow: Flow<PagingData<ChargesEntity>>,
    onAction: (ClientChargesAction) -> Unit,
)

@Composable
internal fun ChargeDialogScreen(
    state: ClientChargesViewModel.ChargeDialogState,
    onDismiss: () -> Unit,
    onCreateCharge: (ChargesPayload) -> Unit,
    onChargeCreated: () -> Unit,
    onRetry: () -> Unit,
) {
        when (state) {
            is ClientChargesViewModel.ChargeDialogState.AllChargesV2 -> {
                ChargeDialogContent(
                    chargeTemplate = state.chargeTemplate,
                    selectedChargeId = -1,
                    selectedChargeName = "",
                    onDismiss = onDismiss,
                    onCreate = onCreateCharge,
//                    showAddCharge = false
                )
            }
            is ClientChargesViewModel.ChargeDialogState.Error -> MifosSweetError(
                message = stringResource(state.message),
            ) {
                onRetry()
            }
            is ClientChargesViewModel.ChargeDialogState.Loading -> MifosProgressIndicator()
            is ClientChargesViewModel.ChargeDialogState.ChargesCreatedSuccessfully -> {
                onChargeCreated()
            }
        }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ChargeDialogContent(
    chargeTemplate: ChargeTemplate,
    selectedChargeId: Int,
    selectedChargeName: String,
    onDismiss: () -> Unit,
    onCreate: (ChargesPayload) -> Unit,
) {
    var chargeTitle by rememberSaveable { mutableStateOf("") }
    var chargeTitleTouched by rememberSaveable { mutableStateOf(false) }

    var amount by rememberSaveable { mutableStateOf("") }
    var amountTouched by rememberSaveable { mutableStateOf(false) }

    var chargeName by rememberSaveable { mutableStateOf(selectedChargeName) }
    var chargeNameTouched by rememberSaveable { mutableStateOf(false) }

    val locale by rememberSaveable { mutableStateOf(LOCALE_EN) }
    var dueDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var collectedOn by rememberSaveable { mutableStateOf<Long?>(null) }
    var chargeId by rememberSaveable { mutableIntStateOf(selectedChargeId) }
    val selectedChargeOption = chargeTemplate.chargeOptions.find { it.id == chargeId }
    val currencyDecimalPlaces = selectedChargeOption?.currency?.decimalPlaces?.toInt() ?: 2
    val amountValidation = validateAmount(amount, currencyDecimalPlaces)
    val isAmountValid = amountValidation == AmountValidationResult.VALID

    var showAddCharge by rememberSaveable { mutableStateOf(false) }

    val dueDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
            }
        },
    )
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        dueDatePickerState.selectedDateMillis?.let {
                            dueDate = it
                        }
                    },
                ) { Text(stringResource(Res.string.feature_client_charge_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                ) { Text(stringResource(Res.string.feature_client_charge_cancel)) }
            },
        ) {
            DatePicker(state = dueDatePickerState)
        }
    }

    val isChargeNameValid = validateChargeName(chargeName)
    val isFormValid = isAmountValid && isChargeNameValid && validateDate(dueDate) && validateDate(collectedOn)

    Column(
        modifier = Modifier.padding(20.dp)
            .verticalScroll(rememberScrollState()),
    ) {

        Text(
            text = stringResource(Res.string.feature_client_charges),
            color = MaterialTheme.colorScheme.primary,
            style = MifosTypography.labelLargeEmphasized
        )

        MifosTextFieldDropdown(
            value = chargeName,
            onValueChanged = { value ->
                chargeName = value
                chargeNameTouched = true
            },
            label = stringResource(Res.string.feature_client_charge_name),
            readOnly = true,
            onOptionSelected = { index, value ->
                chargeId = chargeTemplate.chargeOptions[index].id ?: -1
                chargeName = value
                chargeNameTouched = true
                showAddCharge = true
            },
            options = chargeTemplate.chargeOptions.map { it.name ?: "" },
            errorMessage = if (!isChargeNameValid && chargeNameTouched) {
                stringResource(Res.string.feature_client_message_field_required)
            } else {
                null
            },
        )

        if (!showAddCharge) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd) // Aligns to the right
                        .clickable {
                            showAddCharge = true
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = MifosIcons.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(DesignToken.sizes.iconSmall),
                    )

                    Text(
                        text = stringResource(Res.string.add_charge_title),
                        color = MaterialTheme.colorScheme.primary,
                        style = MifosTypography.labelLargeEmphasized,
                    )
                }
            }
        }


        if(showAddCharge) {
            Spacer(modifier = Modifier.height(16.dp))

            MifosOutlinedTextField(
                value = chargeTitle,
                onValueChange = { value ->
                    chargeTitle = value
                },
                label = stringResource(Res.string.feature_client_charge_name),
            )
            Spacer(modifier = Modifier.height(16.dp))

            MifosOutlinedTextField(
                value = amount,
                onValueChange = { value ->
                    amount = value
                    amountTouched = true
                },
                label = stringResource(Res.string.feature_client_charge_amount),
                error = when {
                    amountValidation == AmountValidationResult.EMPTY && amountTouched ->
                        stringResource(Res.string.feature_client_message_field_required)

                    amountValidation == AmountValidationResult.INVALID_FORMAT && amountTouched ->
                        stringResource(Res.string.feature_client_charge_invalid_amount_format)

                    else -> null
                },
                trailingIcon = {
                    if (!isAmountValid && amountTouched) {
                        Icon(imageVector = MifosIcons.Error, contentDescription = null)
                    }
                },
            )
            Spacer(modifier = Modifier.height(16.dp))

            MifosDatePickerTextField(
                value = collectedOn?.let{
                    DateHelper.getDateAsStringFromLong(collectedOn!!)
                } ?: stringResource(Res.string.feature_client_charge_name),
                label = if (collectedOn == null) "" else stringResource(Res.string.feature_client_due_date),
                openDatePicker = {
                    showDatePicker = true
                },
            )
            Spacer(modifier = Modifier.height(16.dp))

            MifosDatePickerTextField(
                value = dueDate?.let{
                    DateHelper.getDateAsStringFromLong(dueDate!!)
                } ?: stringResource(Res.string.feature_client_due_date),
                label = if (dueDate == null) "" else stringResource(Res.string.feature_client_due_date),
                openDatePicker = {
                    showDatePicker = true
                },
            )
            Spacer(modifier = Modifier.height(16.dp))

            MifosTwoButtonRow(
                firstBtnText = stringResource(Res.string.add_charge_title),
                secondBtnText = stringResource(Res.string.action_view),
                onFirstBtnClick = {
                    onDismiss()
                },
                onSecondBtnClick = {
//                onAction(NewLoanAccountAction.OnDetailsSubmit)
                },
//            isSecondButtonEnabled = state.isDetailsNextEnabled,
                modifier = Modifier.fillMaxWidth()
                    .imePadding(),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}

fun validateAmount(amount: String, decimalPlaces: Int): AmountValidationResult {
    if (amount.isBlank()) return AmountValidationResult.EMPTY

    val trimmed = amount.trim()
    val regex = if (decimalPlaces == 0) {
        Regex("^[1-9]\\d*$")
    } else {
        Regex("^\\s*(?=.*[1-9])\\d*(\\.\\d{1,$decimalPlaces})?\\s*$")
    }

    return if (regex.matches(trimmed)) {
        AmountValidationResult.VALID
    } else {
        AmountValidationResult.INVALID_FORMAT
    }
}

fun validateChargeName(name: String): Boolean = name.isNotBlank()

fun validateDate(date: Long?): Boolean = date != null

enum class AmountValidationResult {
    EMPTY,
    INVALID_FORMAT,
    VALID,
}

private class ChargeDialogScreenUiStateProvider : PreviewParameterProvider<ClientChargesViewModel.ChargeDialogState> {
    override val values: Sequence<ClientChargesViewModel.ChargeDialogState>
        get() = sequenceOf(
            ClientChargesViewModel.ChargeDialogState.AllChargesV2(
                ChargeTemplate(false, emptyList()),
                selectedChargeName = "Charges Name",
                selectedChargeId = 0,
            ),
            ClientChargesViewModel.ChargeDialogState.Error(Res.string.feature_client_failed_to_load_charges),
            ClientChargesViewModel.ChargeDialogState.Loading,
            ClientChargesViewModel.ChargeDialogState.ChargesCreatedSuccessfully,
        )
}

@DevicePreview
@Composable
private fun ChargeDialogScreenPreview(
    @PreviewParameter(ChargeDialogScreenUiStateProvider::class) state: ClientChargesViewModel.ChargeDialogState,
) {
    ChargeDialogScreen(
        state = state,
        onDismiss = {},
        onCreateCharge = {},
        onChargeCreated = {},
        onRetry = {},
    )
}


///*
// * Copyright 2024 Mifos Initiative
// *
// * This Source Code Form is subject to the terms of the Mozilla Public
// * License, v. 2.0. If a copy of the MPL was not distributed with this
// * file, You can obtain one at https://mozilla.org/MPL/2.0/.
// *
// * See https://github.com/openMF/android-client/blob/master/LICENSE.md
// */
//@file:OptIn(ExperimentalMaterial3Api::class)
//
//package com.mifos.feature.client.clientCharges
//
//import androidclient.feature.client.generated.resources.Res
//import androidclient.feature.client.generated.resources.action_view
//import androidclient.feature.client.generated.resources.add_charge_title
//import androidclient.feature.client.generated.resources.client_apply_new_applications_title
//import androidclient.feature.client.generated.resources.feature_client_charge_amount
//import androidclient.feature.client.generated.resources.feature_client_charge_created_successfully
//import androidclient.feature.client.generated.resources.feature_client_charge_id
//import androidclient.feature.client.generated.resources.feature_client_charge_name
//import androidclient.feature.client.generated.resources.feature_client_charges
//import androidclient.feature.client.generated.resources.feature_client_due_date
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.SnackbarHostState
//import androidx.compose.material3.Text
//import androidx.compose.material3.pulltorefresh.PullToRefreshBox
//import androidx.compose.material3.pulltorefresh.PullToRefreshState
//import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import androidx.paging.PagingData
//import com.mifos.core.common.utils.CurrencyFormatter
//import com.mifos.core.common.utils.DateHelper
//import com.mifos.core.designsystem.component.MifosBottomSheet
//import com.mifos.core.designsystem.component.MifosScaffold
//import com.mifos.core.designsystem.component.MifosSweetError
//import com.mifos.core.designsystem.icon.MifosIcons
//import com.mifos.core.designsystem.theme.DesignToken
//import com.mifos.core.designsystem.theme.MifosTypography
//import com.mifos.core.ui.components.MifosActionsChargeListingComponent
//import com.mifos.core.ui.components.MifosBreadcrumbNavBar
//import com.mifos.core.ui.components.MifosIcon
//import com.mifos.core.ui.components.MifosProgressIndicator
//import com.mifos.core.ui.components.MifosRowWithTextAndButton
//import com.mifos.core.ui.util.EventsEffect
//import com.mifos.feature.client.clientChargeDialog.ChargeDialogScreen
//import com.mifos.feature.loan.newLoanAccount.NewLoanAccountAction
//import com.mifos.room.entities.client.ChargesEntity
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.launch
//import org.jetbrains.compose.resources.StringResource
//import org.jetbrains.compose.resources.getString
//import org.jetbrains.compose.resources.stringResource
//import org.koin.compose.viewmodel.koinViewModel
//
//@Composable
//fun ClientChargesScreen(
//    navigateBack: () -> Unit,
//    navController: NavController,
//    modifier: Modifier = Modifier,
//    viewModel: ClientChargesViewModel = koinViewModel(),
//) {
//    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
//    val chargeDialogState = state.chargeDialogUiState
//    EventsEffect(viewModel.eventFlow) { event ->
//        when (event) {
//            ClientChargesEvent.NavigateBack -> navigateBack()
//        }
//    }
//
//    val scope = rememberCoroutineScope()
//    val snackbarHostState = remember { SnackbarHostState() }
//    val pullRefreshState = rememberPullToRefreshState()
//
//    // Show success snackbar when charge is created
//    LaunchedEffect(state.showSuccessSnackbar) {
//        if (state.showSuccessSnackbar) {
//            scope.launch {
//                snackbarHostState.showSnackbar(
//                    message = getString(Res.string.feature_client_charge_created_successfully),
//                )
//            }
//            viewModel.trySendAction(ClientChargesAction.CloseDialog)
//        }
//    }
//
////    ClientAddChargesDialogs(state, onAction = { viewModel.trySendAction(it) })
//
//    ClientChargesScaffold(
//        state = state,
//        navController = navController,
//        onAction = remember { viewModel::trySendAction },
//        snackbarHostState = snackbarHostState,
//        pullRefreshState = pullRefreshState
//    )
//}
//
//@Composable
//fun ClientChargesScaffold(
//    state: ClientChargesState,
//    navController: NavController,
//    modifier: Modifier = Modifier,
//    onAction: (ClientChargesAction) -> Unit,
//    snackbarHostState: SnackbarHostState,
//    pullRefreshState: PullToRefreshState
//) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//        ) {
//            MifosBreadcrumbNavBar(navController)
//            PullToRefreshBox(
//                state = pullRefreshState,
//                onRefresh = { onAction(ClientChargesAction.Refresh) },
//                isRefreshing = state.isRefreshing,
//            ) {
//                when {
//                    state.isLoading -> MifosProgressIndicator()
//                    state.error != null -> {
//                        MifosSweetError(
//                            message = state.error!!,
////                            onRetry = { onAction(ClientChargesAction.OnRetry) }
//                        )
//                    }
//                    else -> {
//                        Column{
//
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.End,
//                            ) {
//                                Row(
//                                    modifier = Modifier.clickable {
//                                        onAction(ClientChargesAction.AddCharge)
//                                    },
//                                    verticalAlignment = Alignment.CenterVertically,
//                                ) {
//                                    Icon(
//                                        imageVector = MifosIcons.Add,
//                                        contentDescription = null,
//                                        tint = MaterialTheme.colorScheme.primary,
//                                        modifier = Modifier.size(DesignToken.sizes.iconSmall),
//                                    )
//
//                                    Text(
//                                        text = stringResource(Res.string.add_charge_title),
//                                        color = MaterialTheme.colorScheme.primary,
//                                        style = MifosTypography.labelLargeEmphasized,
//                                    )
//                                }
//                            }
//                            ClientAddChargesDialogs(state, onAction)
//                            MifosRowWithTextAndButton(
//                                onBtnClick = {
//                                    onAction(ClientChargesAction.ShowCharges)
//                                },
//                                btnText = stringResource(Res.string.action_view),
//                                text =  state.addedCharges.size.toString() + " " + stringResource(Res.string.feature_client_charges),
//                                btnEnabled = state.addedCharges.isNotEmpty(),
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//}
//
//@Composable
//private fun ShowChargesContent(
//    state: ClientChargesState,
//    onAction: (ClientChargesAction) -> Unit,
//) {
//    Column {
//        when (val charges = state.chargesFlow) {
//            is Flow<*> -> {
//                ClientChargeContent(
//                    pagingFlow = charges as Flow<PagingData<ChargesEntity>>,
//                    onAction = onAction,
//                )
//            }
//
//            null -> MifosProgressIndicator()
//            else -> {
//                MifosProgressIndicator()
//            }
//        }
//    }
//}
//
//@Composable
//private fun ClientAddChargesDialogs(
//    state: ClientChargesState,
//    onAction: (ClientChargesAction) -> Unit
//){
//    when (state.dialogState) {
//        ClientChargesState.DialogState.ShowCharges -> ShowChargesContent(state, onAction)
//        ClientChargesState.DialogState.AddCharge -> ChargeDialogScreen(
//            state = state.chargeDialogUiState,
//            onDismiss = { onAction(ClientChargesAction.CloseDialog) },
//            onCreateCharge = { payload -> onAction(ClientChargesAction.CreateCharge(payload)) },
//            onRetry = { onAction(ClientChargesAction.LoadChargeTemplate) },
//            onChargeCreated = {
//                onAction(ClientChargesAction.OnRetry)
//            },
//        )
//        null -> {}
//    }
//}
//@Composable
//fun ChargesItems(charges: ChargesEntity) {
//    val currencyCode = charges.currency?.code ?: ""
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//        shape = RoundedCornerShape(0.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//    ) {
//        Spacer(modifier = Modifier.height(8.dp))
//        MifosCenterDetailsText(
//            stringResource(Res.string.feature_client_charge_id),
//            charges.chargeId.toString(),
//        )
//        MifosCenterDetailsText(
//            stringResource(Res.string.feature_client_charge_name),
//            charges.name ?: "",
//        )
//        MifosCenterDetailsText(
//            stringResource(Res.string.feature_client_charge_amount),
//            CurrencyFormatter.format(
//                charges.amount,
//                currencyCode,
//                2,
//            ),
//        )
//        MifosCenterDetailsText(
//            stringResource(Res.string.feature_client_due_date),
//            DateHelper.getDateAsString(charges.dueDate ?: emptyList()),
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//    }
//}
//
//@Composable
//private fun MifosCenterDetailsText(field: String, value: String) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        Text(
//            modifier = Modifier
//                .weight(1f)
//                .padding(start = 16.dp),
//            text = field,
//            style = MaterialTheme.typography.bodyLarge,
//            textAlign = TextAlign.Start,
//        )
//        Text(
//            modifier = Modifier.weight(1f),
//            text = value,
//            style = MaterialTheme.typography.bodyLarge,
//            textAlign = TextAlign.Start,
//        )
//    }
//}
//
//@Composable
//expect fun ClientChargeContent(
//    pagingFlow: Flow<PagingData<ChargesEntity>>,
//    onAction: (ClientChargesAction) -> Unit,
//)
//
/////*
//// * Copyright 2024 Mifos Initiative
//// *
//// * This Source Code Form is subject to the terms of the Mozilla Public
//// * License, v. 2.0. If a copy of the MPL was not distributed with this
//// * file, You can obtain one at https://mozilla.org/MPL/2.0/.
//// *
//// * See https://github.com/openMF/android-client/blob/master/LICENSE.md
//// */
////@file:OptIn(ExperimentalMaterial3Api::class)
////
////package com.mifos.feature.client.clientCharges
////
////import androidclient.feature.client.generated.resources.Res
////import androidclient.feature.client.generated.resources.feature_client_charge_amount
////import androidclient.feature.client.generated.resources.feature_client_charge_created_successfully
////import androidclient.feature.client.generated.resources.feature_client_charge_id
////import androidclient.feature.client.generated.resources.feature_client_charge_name
////import androidclient.feature.client.generated.resources.feature_client_charges
////import androidclient.feature.client.generated.resources.feature_client_due_date
////import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client_charges
////import androidx.compose.foundation.layout.Column
////import androidx.compose.foundation.layout.Row
////import androidx.compose.foundation.layout.Spacer
////import androidx.compose.foundation.layout.fillMaxWidth
////import androidx.compose.foundation.layout.height
////import androidx.compose.foundation.layout.padding
////import androidx.compose.foundation.shape.RoundedCornerShape
////import androidx.compose.material3.Card
////import androidx.compose.material3.CardDefaults
////import androidx.compose.material3.ExperimentalMaterial3Api
////import androidx.compose.material3.Icon
////import androidx.compose.material3.IconButton
////import androidx.compose.material3.MaterialTheme
////import androidx.compose.material3.SnackbarHostState
////import androidx.compose.material3.Text
////import androidx.compose.material3.pulltorefresh.PullToRefreshBox
////import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
////import androidx.compose.runtime.Composable
////import androidx.compose.runtime.getValue
////import androidx.compose.runtime.mutableStateOf
////import androidx.compose.runtime.remember
////import androidx.compose.runtime.rememberCoroutineScope
////import androidx.compose.runtime.saveable.rememberSaveable
////import androidx.compose.runtime.setValue
////import androidx.compose.ui.Alignment
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.text.style.TextAlign
////import androidx.compose.ui.unit.dp
////import androidx.lifecycle.compose.collectAsStateWithLifecycle
////import androidx.paging.PagingData
////import com.mifos.core.common.utils.CurrencyFormatter
////import com.mifos.core.common.utils.DateHelper
////import com.mifos.core.designsystem.component.MifosScaffold
////import com.mifos.core.designsystem.component.MifosSweetError
////import com.mifos.core.designsystem.icon.MifosIcons
////import com.mifos.core.model.objects.payloads.ChargesPayload
////import com.mifos.core.ui.components.MifosProgressIndicator
////import com.mifos.core.ui.util.DevicePreview
////import com.mifos.feature.client.clientChargeDialog.ChargeDialogScreen
////import com.mifos.feature.client.clientChargeDialog.ChargeDialogUiState
////import com.mifos.room.entities.client.ChargesEntity
////import kotlinx.coroutines.flow.Flow
////import kotlinx.coroutines.flow.flowOf
////import kotlinx.coroutines.launch
////import org.jetbrains.compose.resources.getString
////import org.jetbrains.compose.resources.stringResource
////import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
////import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
////import org.koin.compose.viewmodel.koinViewModel
////
////@Composable
////fun ClientChargesScreen(
////    onBackPressed: () -> Unit,
////    viewModel: ClientChargesViewModel = koinViewModel(),
////) {
////    val clientChargeUiState by viewModel.clientChargesUiState.collectAsStateWithLifecycle()
////    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
////    val chargeDialogUiState by viewModel.chargeDialogUiState.collectAsStateWithLifecycle()
////
////    ClientChargesScreen(
////        state = clientChargeUiState,
////        dialogState = chargeDialogUiState,
////        onShowDialog = viewModel::loadChargeTemplate,
////        onCreateCharge = { payload -> viewModel.createCharge(payload) },
////        onChargeCreated = viewModel::loadCharges,
////        onBackPressed = onBackPressed,
////        onRetry = viewModel::loadCharges,
////        onRefresh = viewModel::refreshChargesList,
////        refreshState = refreshState,
////    )
////}
////
////@Composable
////fun ClientChargesScreen(
////    state: ClientChargeUiState,
////    dialogState: ChargeDialogUiState,
////    onShowDialog: () -> Unit,
////    onCreateCharge: (ChargesPayload) -> Unit,
////    onChargeCreated: () -> Unit,
////    onBackPressed: () -> Unit,
////    onRetry: () -> Unit,
////    onRefresh: () -> Unit,
////    refreshState: Boolean,
////) {
////    val scope = rememberCoroutineScope()
////    val snackbarHostState = remember { SnackbarHostState() }
////    val pullRefreshState = rememberPullToRefreshState()
////    var showClientChargeDialog by rememberSaveable { mutableStateOf(false) }
////    var showChargeCreatedSuccess by rememberSaveable { mutableStateOf(false) }
////
////    if (showClientChargeDialog) {
////        ChargeDialogScreen(
////            state = dialogState,
////            onDismiss = {
////                showClientChargeDialog = false
////            },
////            onCreateCharge = onCreateCharge,
////            onRetry = onRetry,
////            onChargeCreated = {
////                onChargeCreated()
////                showClientChargeDialog = false
////                showChargeCreatedSuccess = true
////            },
////        )
////    }
////
////    MifosScaffold(
////        title = stringResource(Res.string.feature_client_charges),
////        onBackPressed = onBackPressed,
////        actions = {
////            IconButton(
////                onClick = {
////                    onShowDialog()
////                    showClientChargeDialog = true
////                },
////            ) {
////                Icon(imageVector = MifosIcons.Add, contentDescription = null)
////            }
////        },
////        snackbarHostState = snackbarHostState,
////    ) { paddingValues ->
////        Column(modifier = Modifier.padding(paddingValues)) {
////            PullToRefreshBox(
////                state = pullRefreshState,
////                onRefresh = onRefresh,
////                isRefreshing = refreshState,
////            ) {
////                when (state) {
////                    is ClientChargeUiState.ChargesList -> ClientChargeContent(
////                        pagingFlow = state.chargesPage,
////                        onRetry = onRetry,
////                    )
////
////                    is ClientChargeUiState.Error ->
////                        MifosSweetError(
////                            message = stringResource(state.message),
////                        ) {
////                            onRetry()
////                        }
////
////                    is ClientChargeUiState.Loading -> MifosProgressIndicator()
////                }
////            }
////        }
////    }
////
////    if (showChargeCreatedSuccess) {
////        scope.launch {
////            snackbarHostState.showSnackbar(
////                message = getString(Res.string.feature_client_charge_created_successfully),
////            )
////        }
////        showChargeCreatedSuccess = false
////    }
////}
////
////@Composable
////expect fun ClientChargeContent(
////    pagingFlow: Flow<PagingData<ChargesEntity>>,
////    onRetry: () -> Unit,
////)
////
////@Composable
////fun ChargesItems(charges: ChargesEntity) {
////    val currencyCode = charges.currency?.code ?: ""
////
////    Card(
////        modifier = Modifier
////            .fillMaxWidth()
////            .padding(8.dp),
////        shape = RoundedCornerShape(0.dp),
////        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
////    ) {
////        Spacer(modifier = Modifier.height(8.dp))
////        MifosCenterDetailsText(
////            stringResource(Res.string.feature_client_charge_id),
////            charges.chargeId.toString(),
////        )
////        MifosCenterDetailsText(
////            stringResource(Res.string.feature_client_charge_name),
////            charges.name ?: "",
////        )
////        MifosCenterDetailsText(
////            stringResource(Res.string.feature_client_charge_amount),
////            CurrencyFormatter.format(
////                charges.amount,
////                currencyCode,
////                2,
////            ),
////        )
////        MifosCenterDetailsText(
////            stringResource(Res.string.feature_client_due_date),
////            DateHelper.getDateAsString(charges.dueDate ?: emptyList()),
////        )
////        Spacer(modifier = Modifier.height(8.dp))
////    }
////}
////
////@Composable
////private fun MifosCenterDetailsText(field: String, value: String) {
////    Row(
////        modifier = Modifier
////            .fillMaxWidth(),
////        verticalAlignment = Alignment.CenterVertically,
////    ) {
////        Text(
////            modifier = Modifier
////                .weight(1f)
////                .padding(start = 16.dp),
////            text = field,
////            style = MaterialTheme.typography.bodyLarge,
////            textAlign = TextAlign.Start,
////        )
////        Text(
////            modifier = Modifier.weight(1f),
////            text = value,
////            style = MaterialTheme.typography.bodyLarge,
////            textAlign = TextAlign.Start,
////        )
////    }
////}
////
////private class ClientChargesScreenUiStateProvider : PreviewParameterProvider<ClientChargeUiState> {
////
////    override val values: Sequence<ClientChargeUiState>
////        get() = sequenceOf(
////            ClientChargeUiState.Loading,
////            ClientChargeUiState.Error(Res.string.feature_client_failed_to_load_client_charges),
////            ClientChargeUiState.ChargesList(flowOf(PagingData.from(sampleClientCharge))),
////        )
////}
////
////@DevicePreview
////@Composable
////private fun ClientChargesScreenPreview(
////    @PreviewParameter(ClientChargesScreenUiStateProvider::class) state: ClientChargeUiState,
////) {
////    ClientChargesScreen(
////        state = state,
////        dialogState = ChargeDialogUiState.Loading,
////        onShowDialog = {},
////        onCreateCharge = {},
////        onChargeCreated = {},
////        onBackPressed = {},
////        onRetry = {},
////        onRefresh = {},
////        refreshState = false,
////    )
////}
////
////val sampleClientCharge = List(10) {
////    ChargesEntity(name = "charge $it", amount = it.toDouble())
////}
