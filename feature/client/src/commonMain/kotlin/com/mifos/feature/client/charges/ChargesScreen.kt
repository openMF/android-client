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

package com.mifos.feature.client.charges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.action_add
import androidclient.feature.client.generated.resources.action_view
import androidclient.feature.client.generated.resources.add_charge_title
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.charges_update
import androidclient.feature.client.generated.resources.charges_view_charges
import androidclient.feature.client.generated.resources.dialog_continue
import androidclient.feature.client.generated.resources.feature_client_charge_amount
import androidclient.feature.client.generated.resources.feature_client_charge_cancel
import androidclient.feature.client.generated.resources.feature_client_charge_select
import androidclient.feature.client.generated.resources.feature_client_charges
import androidclient.feature.client.generated.resources.feature_client_choose_charge
import androidclient.feature.client.generated.resources.feature_client_created_charge_failure_title
import androidclient.feature.client.generated.resources.feature_client_created_charge_success_message
import androidclient.feature.client.generated.resources.feature_client_created_charge_success_title
import androidclient.feature.client.generated.resources.feature_client_due_date
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsChargeListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun ChargesScreen(
    navigateBack: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ChargesViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ChargesEvent.NavigateBack -> navigateBack()
        }
    }

    ChargesContent(
        modifier = modifier,
        state = state,
        navController = navController,
        onAction = remember { viewModel::trySendAction },
    )

    ChargesDialog(
        state = state,
        onAction = remember { viewModel::trySendAction },
    )
}

@Composable
fun ChargesContent(
    state: ChargesState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onAction: (ChargesAction) -> Unit,
) {
    if (state.dialogState !is ChargesState.DialogState.Error) {
        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            MifosBreadcrumbNavBar(navController)

            Column(
                modifier = Modifier.fillMaxWidth().padding(DesignToken.padding.large),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
            ) {
                ChargeAddFields(
                    state = state,
                    onAction = onAction,
                )

                MifosRowWithTextAndButton(
                    onBtnClick = {
                        onAction(ChargesAction.OnShowChargeBottomSheet)
                    },
                    btnText = stringResource(Res.string.action_view),
                    text = state.totalCharges.toString() + " " + stringResource(Res.string.feature_client_charges),
                    btnEnabled = state.totalCharges != 0,
                )
            }
        }
    }
}

@Composable
private fun ChargesDialog(
    state: ChargesState,
    onAction: (ChargesAction) -> Unit,
) {
    when (state.dialogState) {
        is ChargesState.DialogState.Loading -> {
            if (state.isOverlayLoading) MifosProgressIndicatorOverlay() else MifosProgressIndicator()
        }

        is ChargesState.DialogState.Error -> {
            MifosErrorComponent(
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = { onAction(ChargesAction.OnRetry) },
            )
        }

        is ChargesState.DialogState.ShowStatusDialog -> {
            val status = state.dialogState.status
            MifosStatusDialog(
                status = status,
                btnText = stringResource(Res.string.dialog_continue),
                onConfirm = {
                    if (status == ResultStatus.SUCCESS) {
                        onAction(ChargesAction.NavigateBack)
                    } else {
                        onAction(ChargesAction.DismissDialog)
                    }
                },
                successTitle = stringResource(Res.string.feature_client_created_charge_success_title),
                successMessage = stringResource(Res.string.feature_client_created_charge_success_message),
                failureTitle = stringResource(Res.string.feature_client_created_charge_failure_title),
                failureMessage = state.dialogState.message,
                modifier = Modifier.fillMaxSize().background(Color.White),
            )
        }

        null -> Unit
        ChargesState.DialogState.ShowChargeBottomSheet -> {
            ShowChargeBottomSheet(
                state = state,
                onAction = onAction,
            )
        }
    }
}

@Composable
fun ShowChargeBottomSheet(
    state: ChargesState,
    onAction: (ChargesAction) -> Unit,
) {
    var expandedIndex: Int by rememberSaveable { mutableStateOf(-1) }

    MifosBottomSheet(
        onDismiss = {
            onAction(ChargesAction.DismissDialog)
        },
        content = {
            Box(
                modifier = Modifier
                    .heightIn(max = DesignToken.spacing.half),
            ) {
                Column(
                    modifier = Modifier.padding(DesignToken.padding.large),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
                ) {
                    Text(
                        text = stringResource(Res.string.charges_view_charges),
                        style = MifosTypography.titleMediumEmphasized,
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
                    ) {
                        items(state.chargesList) {
                            MifosActionsChargeListingComponent(
                                chargeTitle = it.name.toString(),
                                type = it.chargeCalculationType?.value.toString(),
                                date = it.formattedDueDate,
                                collectedOn = it.formattedDueDate,
                                amount = it.amount.toString(),
                                onActionClicked = { action ->
                                    when (action) {
                                        is Actions.Delete -> {
                                            onAction(ChargesAction.DeleteCharge(it.id))
                                        }

                                        is Actions.Edit -> {
                                            onAction(ChargesAction.FetchEditChargeData(it.id))
                                        }

                                        else -> {}
                                    }
                                },
                                isExpanded = expandedIndex == it.id,
                                onExpandToggle = {
                                    expandedIndex = if (expandedIndex == it.id) -1 else it.id
                                },
                            )
                        }
                    }

                    MifosTwoButtonRow(
                        firstBtnText = stringResource(Res.string.btn_back),
                        secondBtnText = stringResource(Res.string.action_add),
                        onFirstBtnClick = {
                            onAction(ChargesAction.DismissDialog)
                        },
                        onSecondBtnClick = {
                            onAction(ChargesAction.DismissDialog)
                        },
                    )
                }
                if (state.isOverlayLoading) {
                    MifosProgressIndicatorOverlay(
                        modifier = Modifier
                            .matchParentSize(),
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ChargeAddFields(
    state: ChargesState,
    onAction: (ChargesAction) -> Unit,
) {
    val dueDatePickerState = key(state.dueDate) {
        rememberDatePickerState(
            initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
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
                onAction(ChargesAction.OnDueDatePick(false))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(ChargesAction.OnDueDatePick(false))
                        dueDatePickerState.selectedDateMillis?.let {
                            onAction(
                                ChargesAction.OnDueDateChange(
                                    DateHelper.getDateAsStringFromLong(
                                        it,
                                    ),
                                ),
                            )
                        }
                    },
                ) { Text(stringResource(Res.string.feature_client_charge_select)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(ChargesAction.OnDueDatePick(false))
                    },
                ) { Text(stringResource(Res.string.feature_client_charge_cancel)) }
            },
        ) {
            DatePicker(state = dueDatePickerState)
        }
    }

    Box {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = DesignToken.padding.medium),
        ) {
            Text(
                text = stringResource(Res.string.feature_client_charges),
                color = MaterialTheme.colorScheme.primary,
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(modifier = Modifier.height(DesignToken.padding.medium))

            MifosTextFieldDropdown(
                value = if (state.chargeOptionIndex == null) {
                    ""
                } else {
                    state.chargeTemplate?.chargeOptions[state.chargeOptionIndex]?.name.let {
                        it ?: ""
                    }
                },
                onValueChanged = {},
                label = stringResource(Res.string.feature_client_choose_charge),
                readOnly = true,
                onOptionSelected = { index, value ->
                    onAction(ChargesAction.OnChargeOptionChange(index))
                },
                options = state.chargeTemplate?.chargeOptions?.map { it.name ?: "" } ?: emptyList(),
                errorMessage = state.chargeOptionError?.let { stringResource(it) },
            )

            if (state.showChargeAddFields) {
                MifosOutlinedTextField(
                    value = state.amount ?: "",
                    onValueChange = { value ->
                        onAction(ChargesAction.OnAmountChange(value))
                    },
                    label = stringResource(Res.string.feature_client_charge_amount),
                    config = MifosTextFieldConfig(
                        isError = state.amountValidationError != null,
                        errorText = state.amountValidationError?.let { stringResource(it) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                    ),
                )

                Spacer(modifier = Modifier.height(DesignToken.padding.medium))

                MifosDatePickerTextField(
                    value = state.dueDate,
                    label = stringResource(Res.string.feature_client_due_date),
                    openDatePicker = {
                        onAction(ChargesAction.OnDueDatePick(true))
                    },
                )

                Spacer(modifier = Modifier.height(DesignToken.padding.medium))

                MifosTwoButtonRow(
                    firstBtnText =
                    stringResource(
                        Res.string.btn_back,
                    ),
                    secondBtnText = if (state.isUpdate) {
                        stringResource(Res.string.charges_update)
                    } else {
                        stringResource(Res.string.action_add)
                    },
                    onFirstBtnClick = {
                        onAction(ChargesAction.CloseChargeAddFields)
                    },
                    onSecondBtnClick = {
                        onAction(ChargesAction.CreateCharge)
                    },
                    modifier = Modifier.padding(top = DesignToken.padding.medium),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable {
                                onAction(ChargesAction.OnShowAddCharge)
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
    }
}
