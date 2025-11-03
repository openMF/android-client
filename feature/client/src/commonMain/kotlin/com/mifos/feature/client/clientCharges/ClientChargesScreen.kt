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
import androidclient.feature.client.generated.resources.action_add
import androidclient.feature.client.generated.resources.action_view
import androidclient.feature.client.generated.resources.add_charge_title
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.dialog_continue
import androidclient.feature.client.generated.resources.feature_client_charge_amount
import androidclient.feature.client.generated.resources.feature_client_charge_cancel
import androidclient.feature.client.generated.resources.feature_client_charge_invalid_amount_format
import androidclient.feature.client.generated.resources.feature_client_charge_select
import androidclient.feature.client.generated.resources.feature_client_charge_title
import androidclient.feature.client.generated.resources.feature_client_charges
import androidclient.feature.client.generated.resources.feature_client_choose_charge
import androidclient.feature.client.generated.resources.feature_client_created_charge_failure_title
import androidclient.feature.client.generated.resources.feature_client_created_charge_success_message
import androidclient.feature.client.generated.resources.feature_client_created_charge_success_title
import androidclient.feature.client.generated.resources.feature_client_due_date
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client_charges
import androidclient.feature.client.generated.resources.feature_client_message_field_required
import androidclient.feature.client.generated.resources.feature_client_no_charges_found
import androidclient.feature.client.generated.resources.feature_client_view_charges
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.common.utils.Constants.DATE_FORMAT_LONG
import com.mifos.core.common.utils.Constants.LOCALE_EN
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.common.utils.formatDate
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.model.objects.payloads.ChargesPayload
import com.mifos.core.model.objects.template.client.ChargeTemplate
import com.mifos.core.ui.components.MifosActionsChargeListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.util.EventsEffect
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
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
    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientChargesEvent.NavigateBack -> navigateBack()
        }
    }

    val pullRefreshState = rememberPullToRefreshState()

    ClientChargesScaffold(
        modifier = modifier,
        state = state,
        navController = navController,
        onAction = remember { viewModel::trySendAction },
        pullRefreshState = pullRefreshState,
    )

    ClientAddChargesDialog(
        state = state,
        onAction = remember { viewModel::trySendAction },
    )
}

@Composable
fun ClientChargesScaffold(
    state: ClientChargesState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onAction: (ClientChargesAction) -> Unit,
    pullRefreshState: PullToRefreshState,
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
                    Column(
                        modifier = modifier.fillMaxWidth().padding(DesignToken.padding.large)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
                    ) {
                        when (state.chargeDialogState) {
                            is ClientChargesViewModel.ChargeDialogState.AllChargesV2 -> {
                                ClientAddChargeDialogContent(
                                    chargeTemplate = state.chargeDialogState.chargeTemplate,
                                    state = state,
                                    onAction = onAction,
                                )
                            }

                            is ClientChargesViewModel.ChargeDialogState.Error -> MifosErrorComponent(
                                message = state.chargeDialogState.message.toString(),
                                isRetryEnabled = true,
                                onRetry = { onAction(ClientChargesAction.OnRetry) },
                            )

                            is ClientChargesViewModel.ChargeDialogState.Loading -> MifosProgressIndicator()
                        }

                        MifosRowWithTextAndButton(
                            onBtnClick = {
                                onAction(ClientChargesAction.ShowCharges)
                            },
                            btnText = stringResource(Res.string.action_view),
                            text = state.totalClientCharges.toString() + " " + stringResource(Res.string.feature_client_charges),
                            btnEnabled = true,
                        )
                        if (state.showCharges) {
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
                }
            }
        }
    }
}

@Composable
private fun ClientAddChargesDialog(
    state: ClientChargesState,
    onAction: (ClientChargesAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientChargesState.DialogState.Loading -> MifosProgressIndicator()
        is ClientChargesState.DialogState.Error -> {
            MifosErrorComponent(
                isRetryEnabled = true,
                onRetry = { onAction(ClientChargesAction.OnRetry) },
            )
        }
        is ClientChargesState.DialogState.ShowStatusDialog -> {
            MifosStatusDialog(
                status = state.dialogState.status,
                btnText = stringResource(Res.string.dialog_continue),
                onConfirm = { onAction(ClientChargesAction.NavigateBack) },
                successTitle = stringResource(Res.string.feature_client_created_charge_success_title),
                successMessage = stringResource(Res.string.feature_client_created_charge_success_message),
                failureTitle = stringResource(Res.string.feature_client_created_charge_failure_title),
                failureMessage = state.dialogState.message,
                modifier = Modifier.fillMaxSize().background(Color.White),
            )
        }
        null -> Unit
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ClientAddChargeDialogContent(
    state: ClientChargesState,
    onAction: (ClientChargesAction) -> Unit,
    chargeTemplate: ChargeTemplate,
) {
    val locale by rememberSaveable { mutableStateOf(LOCALE_EN) }
    val selectedChargeOption = chargeTemplate.chargeOptions.find { it.id == state.chargeId }
    val currencyDecimalPlaces = selectedChargeOption?.currency?.decimalPlaces?.toInt() ?: 2
    val amountValidation = validateAmount(state.amount.toString(), currencyDecimalPlaces)
    val isAmountValid = amountValidation == AmountValidationResult.VALID

    val dueDatePickerState = key(state.dueDate) {
        rememberDatePickerState(
            initialSelectedDateMillis = state.dueDate,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= Clock.System.now().toEpochMilliseconds()
                }
            },
        )
    }

    if (state.showDueDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                onAction(ClientChargesAction.OnDueDatePick(false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(ClientChargesAction.OnDueDatePick(false))
                        dueDatePickerState.selectedDateMillis?.let {
                            onAction(ClientChargesAction.OnDueDateChange(it))
                        }
                    },
                ) { Text(stringResource(Res.string.feature_client_charge_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(ClientChargesAction.OnDueDatePick(false))
                    },
                ) { Text(stringResource(Res.string.feature_client_charge_cancel)) }
            },
        ) {
            DatePicker(state = dueDatePickerState)
        }
    }

    val isChargeNameValid = validateChargeName(state.chargeName.toString())
    val isFormValid = isAmountValid && isChargeNameValid && validateDate(state.dueDate)

    Box {
        Column(
            modifier = Modifier
                .padding(bottom = DesignToken.padding.medium)
                .align(Alignment.TopStart),

            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.small),
        ) {
            Text(
                text = stringResource(Res.string.feature_client_charges),
                color = MaterialTheme.colorScheme.primary,
                style = MifosTypography.labelLargeEmphasized,
            )

            MifosTextFieldDropdown(
                value = state.chargeName.toString(),
                onValueChanged = { onAction(ClientChargesAction.OnChargeNameChange(it)) },
                label = stringResource(Res.string.feature_client_choose_charge),
                readOnly = true,
                onOptionSelected = { index, value ->
                    chargeTemplate.chargeOptions[index].id?.let {
                        onAction(
                            ClientChargesAction.OnChargeIdChange(
                                it,
                            ),
                        )
                    }
                    onAction(ClientChargesAction.OnChargeNameChange(value))
                    onAction(ClientChargesAction.OnChargeNameTouched)
                    onAction(ClientChargesAction.OnShowAddCharge)
                },
                options = chargeTemplate.chargeOptions.map { it.name ?: "" },
                errorMessage = if (!isChargeNameValid && state.chargeNameTouched) {
                    stringResource(Res.string.feature_client_message_field_required)
                } else {
                    null
                },
            )

            if (state.showAddCharges) {
                MifosOutlinedTextField(
                    value = state.amount ?: "",
                    onValueChange = { value ->
                        onAction(ClientChargesAction.OnAmountChange(value))
                        onAction(ClientChargesAction.OnAmountTouched)
                    },
                    label = stringResource(Res.string.feature_client_charge_amount),
                    error = when {
                        amountValidation == AmountValidationResult.EMPTY && state.amountTouched ->
                            stringResource(Res.string.feature_client_message_field_required)

                        amountValidation == AmountValidationResult.INVALID_FORMAT && state.amountTouched ->
                            stringResource(Res.string.feature_client_charge_invalid_amount_format)

                        else -> null
                    },
                    trailingIcon = {
                        if (!isAmountValid && state.amountTouched) {
                            Icon(imageVector = MifosIcons.Error, contentDescription = null)
                        }
                    },
                )

                MifosOutlinedTextField(
                    value = if (state.chargeId == -1) {
                        ""
                    } else {
                        chargeTemplate.chargeOptions.find { it.name == state.chargeName }?.chargeCalculationType?.value
                            ?: ""
                    },
                    onValueChange = {},
                    label = stringResource(Res.string.feature_client_charge_title),
                    config = MifosTextFieldConfig(
                        readOnly = true,
                        enabled = false,
                    ),
                )
                MifosOutlinedTextField(
                    value = if (state.chargeId == -1) {
                        ""
                    } else {
                        chargeTemplate.chargeOptions.find { it.name == state.chargeName }?.chargeTimeType?.value
                            ?: ""
                    },
                    onValueChange = {},
                    label = stringResource(Res.string.feature_client_charge_title),
                    config = MifosTextFieldConfig(
                        readOnly = true,
                        enabled = false,
                    ),
                )

                MifosDatePickerTextField(
                    value = state.dueDate?.let {
                        DateHelper.getDateAsStringFromLong(state.dueDate!!)
                    } ?: stringResource(Res.string.feature_client_due_date),
                    label = if (state.dueDate == null) "" else stringResource(Res.string.feature_client_due_date),
                    openDatePicker = {
                        onAction(ClientChargesAction.OnDueDatePick(true))
                    },
                )

                Spacer(modifier = Modifier.height(DesignToken.padding.extraExtraLarge))
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable {
                                onAction(ClientChargesAction.OnShowAddCharge)
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
        }
        if (state.showAddCharges) {
            MifosTwoButtonRow(
                firstBtnText = stringResource(Res.string.btn_back),
                secondBtnText = stringResource(Res.string.action_add),
                onFirstBtnClick = {
                    onAction(ClientChargesAction.CloseAddChargesDialog)
                },
                onSecondBtnClick = {
                    val payload = ChargesPayload(
                        amount = state.amount,
                        dateFormat = DATE_FORMAT_LONG,
                        chargeId = state.chargeId,
                        dueDate = state.dueDate?.let { formatDate(it) },
                        locale = locale,
                    )
                    onAction(ClientChargesAction.CreateCharge(payload))
                },
                isSecondButtonEnabled = isFormValid,
                modifier = Modifier.background(color = Color.White)
                    .align(Alignment.BottomCenter)
                    .padding(top = DesignToken.padding.medium),
            )
        }
    }
}

@Composable
fun ClientChargeContent(
    pagingFlow: Flow<PagingData<ChargesEntity>>,
    onAction: (ClientChargesAction) -> Unit,
) {
    val chargesPagingList = pagingFlow.collectAsLazyPagingItems()

    when (chargesPagingList.loadState.refresh) {
        is LoadState.Error -> {
            MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_load_client_charges)) {
                onAction(ClientChargesAction.OnRetry)
            }
        }

        is LoadState.Loading -> MifosProgressIndicator()

        is LoadState.NotLoading -> {
            if (chargesPagingList.itemCount == 0) {
                MifosBottomSheet(
                    onDismiss = {
                        onAction(ClientChargesAction.CloseShowChargesDialog)
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        MifosEmptyUi(
                            text = stringResource(Res.string.feature_client_no_charges_found),
                            icon = MifosIcons.Payments,
                            modifier = Modifier.height(256.dp),

                        )
                    }
                }
            } else {
                val chargesList = List(chargesPagingList.itemCount) { index ->
                    chargesPagingList[index]
                }.filterNotNull()

                MifosBottomSheet(
                    onDismiss = {
                        onAction(ClientChargesAction.CloseShowChargesDialog)
                    },
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(DesignToken.padding.large),
                            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
                        ) {
                            Text(
                                text = stringResource(Res.string.feature_client_view_charges),
                                style = MifosTypography.titleMediumEmphasized,
                            )
                            chargesList.forEach { it ->
                                MifosActionsChargeListingComponent(
                                    chargeTitle = it.name.toString(),
                                    type = it.chargeCalculationType?.value.toString(),
                                    date = it.formattedDueDate,
                                    collectedOn = it.formattedDueDate,
                                    amount = it.amount.toString(),
                                    onActionClicked = {},
                                    isExpandable = false,
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}

fun validateChargeName(name: String): Boolean = name.isNotBlank()

fun validateDate(date: Long?): Boolean = date != null

enum class AmountValidationResult {
    EMPTY,
    INVALID_FORMAT,
    VALID,
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
