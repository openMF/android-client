/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.newLoanAccount

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.add
import androidclient.feature.loan.generated.resources.add_new
import androidclient.feature.loan.generated.resources.add_new_charge
import androidclient.feature.loan.generated.resources.add_new_collateral
import androidclient.feature.loan.generated.resources.back
import androidclient.feature.loan.generated.resources.click_on_add_new
import androidclient.feature.loan.generated.resources.collateral
import androidclient.feature.loan.generated.resources.edit_charge
import androidclient.feature.loan.generated.resources.feature_loan_cancel
import androidclient.feature.loan.generated.resources.quantity
import androidclient.feature.loan.generated.resources.step_charges
import androidclient.feature.loan.generated.resources.step_details
import androidclient.feature.loan.generated.resources.step_preview
import androidclient.feature.loan.generated.resources.step_schedule
import androidclient.feature.loan.generated.resources.step_terms
import androidclient.feature.loan.generated.resources.total_collateral_value
import androidclient.feature.loan.generated.resources.total_value
import androidclient.feature.loan.generated.resources.view_charges
import androidclient.feature.loan.generated.resources.view_collaterals
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosBasicDialog
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.AddChargeBottomSheet
import com.mifos.core.ui.components.MifosActionsChargeListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosListingComponentOutline
import com.mifos.core.ui.components.MifosListingRowItem
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.loan.newLoanAccount.pages.ChargesPage
import com.mifos.feature.loan.newLoanAccount.pages.DetailsPage
import com.mifos.feature.loan.newLoanAccount.pages.PreviewPage
import com.mifos.feature.loan.newLoanAccount.pages.SchedulePage
import com.mifos.feature.loan.newLoanAccount.pages.TermsPage
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
internal fun NewLoanAccountScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewLoanAccountViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            NewLoanAccountEvent.NavigateBack -> onNavigateBack()
            NewLoanAccountEvent.Finish -> onFinish()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    NewLoanAccountDialogs(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        snackbarHostState = snackbarHostState,
    )

    NewLoanAccountScaffold(
        modifier = modifier,
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        navController = navController,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalTime::class)
@Suppress("SuspiciousIndentation")
@Composable
private fun NewLoanAccountScaffold(
    navController: NavController,
    state: NewLoanAccountState,
    modifier: Modifier = Modifier,
    onAction: (NewLoanAccountAction) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val steps = listOf(
        Step(stringResource(Res.string.step_details)) {
            DetailsPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(stringResource(Res.string.step_terms)) {
            TermsPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(stringResource(Res.string.step_charges)) {
            ChargesPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(stringResource(Res.string.step_schedule)) {
            SchedulePage(
                state = state,
                onAction = onAction,
            )
        },
        Step(stringResource(Res.string.step_preview)) {
            PreviewPage(
                state = state,
                onAction = onAction,
            )
        },
    )

    MifosScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            MifosBreadcrumbNavBar(
                navController,
            )
            when (state.screenState) {
                is NewLoanAccountState.ScreenState.Loading -> MifosProgressIndicator()
                is NewLoanAccountState.ScreenState.Success -> {
                    MifosStepper(
                        steps = steps,
                        currentIndex = state.currentStep,
                        onStepChange = { newIndex ->
                            onAction(NewLoanAccountAction.OnStepChange(newIndex))
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }

                is NewLoanAccountState.ScreenState.Error -> {
                    MifosSweetError(
                        message = state.screenState.message,
                        onclick = { onAction(NewLoanAccountAction.Retry) },
                    )
                }

                null -> Unit
            }
        }

        if (state.isOverLayLoadingActive) {
            MifosProgressIndicatorOverlay()
        }
    }
}

@Composable
private fun NewLoanAccountDialogs(
    state: NewLoanAccountState,
    onAction: (NewLoanAccountAction) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    when (state.dialogState) {
        NewLoanAccountState.DialogState.AddNewCollateral -> AddNewCollateralDialog(
            state = state,
            onAction = onAction,
        )

        NewLoanAccountState.DialogState.ShowCollaterals -> ShowCollateralsDialog(
            state = state,
            onAction = onAction,
        )

        is NewLoanAccountState.DialogState.AddNewCharge -> AddNewChargeDialog(
            isEdit = state.dialogState.edit,
            state = state,
            onAction = onAction,
            index = state.dialogState.index,
        )

        NewLoanAccountState.DialogState.ShowCharges -> ShowChargesDialog(
            state = state,
            onAction = onAction,
        )

        NewLoanAccountState.DialogState.ShowOverDueCharges -> ShowChargesDialog(
            state = state,
            onAction = onAction,
            isOverDue = true,
        )

        is NewLoanAccountState.DialogState.SuccessResponseStatus -> {
            LaunchedEffect(state.launchEffectKey) {
                snackbarHostState.showSnackbar(
                    message = state.dialogState.msg,
                )

                if (state.dialogState.successStatus) {
                    delay(1000)
                    onAction(NewLoanAccountAction.Finish)
                }
            }
        }

        null -> Unit
    }
}

@Composable
private fun AddNewCollateralDialog(
    state: NewLoanAccountState,
    onAction: (NewLoanAccountAction) -> Unit,
) {
    MifosBasicDialog(
        title = stringResource(Res.string.add_new_collateral),
        confirmText = stringResource(Res.string.add),
        dismissText = stringResource(Res.string.feature_loan_cancel),
        isConfirmEnabled = state.isCollateralBtnEnabled,
        onConfirm = {
            onAction(NewLoanAccountAction.AddCollateralToList)
        },
        onDismissRequest = {
            onAction(NewLoanAccountAction.DismissDialog)
        },
        content = {
            Column {
                MifosTextFieldDropdown(
                    value = if (state.collateralSelectedIndex == -1) {
                        ""
                    } else {
                        state.collaterals[state.collateralSelectedIndex].name
                    },
                    onValueChanged = {},
                    onOptionSelected = { index, value ->
                        onAction(NewLoanAccountAction.SelectedCollateralIndexChange(index))
                    },
                    options = state.collaterals.map { it.name },
                    label = stringResource(Res.string.collateral),
                )
                Spacer(modifier = Modifier.height(DesignToken.padding.medium))
                MifosOutlinedTextField(
                    value = state.collateralQuantity.toString(),
                    onValueChange = {
                        onAction(
                            NewLoanAccountAction.OnCollateralQuantityChanged(
                                it.toIntOrNull() ?: 0,
                            ),
                        )
                    },
                    label = stringResource(Res.string.quantity),
                    config = MifosTextFieldConfig(
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                    ),
                )
                Spacer(modifier = Modifier.height(DesignToken.padding.large))
                MifosOutlinedTextField(
                    value = state.collateralTotal.toString(),
                    onValueChange = {},
                    label = stringResource(Res.string.total_value),
                    config = MifosTextFieldConfig(
                        readOnly = true,
                        enabled = false,
                    ),
                )
                Spacer(modifier = Modifier.height(DesignToken.padding.large))
                MifosOutlinedTextField(
                    value = state.totalCollateral.toString(),
                    onValueChange = {},
                    label = stringResource(Res.string.total_collateral_value),
                    config = MifosTextFieldConfig(
                        readOnly = true,
                        enabled = false,
                    ),
                )
            }
        },
    )
}

@Composable
private fun ShowCollateralsDialog(
    state: NewLoanAccountState,
    onAction: (NewLoanAccountAction) -> Unit,
) {
    MifosBasicDialog(
        title = stringResource(Res.string.view_collaterals),
        confirmText = stringResource(Res.string.add_new),
        dismissText = stringResource(Res.string.back),
        onConfirm = {
            onAction(NewLoanAccountAction.ShowAddCollateralDialog)
        },
        onDismissRequest = {
            onAction(NewLoanAccountAction.DismissDialog)
        },
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
            ) {
                state.addedCollaterals.forEach {
                    MifosListingComponentOutline {
                        Column(
                            Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
                        ) {
                            MifosListingRowItem(
                                key = it.name,
                                value = "",
                                keyStyle = MifosTypography.titleSmallEmphasized,
                            )
                            MifosListingRowItem(
                                key = stringResource(Res.string.quantity),
                                value = it.quantity.toString(),
                            )
                            MifosListingRowItem(
                                key = stringResource(Res.string.total_value),
                                value = it.totalValue.toString(),
                            )
                            MifosListingRowItem(
                                key = stringResource(Res.string.total_collateral_value),
                                value = it.totalCollateral.toString(),
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun AddNewChargeDialog(
    isEdit: Boolean,
    index: Int = -1,
    state: NewLoanAccountState,
    onAction: (NewLoanAccountAction) -> Unit,
) {
    AddChargeBottomSheet(
        title = if (isEdit) {
            stringResource(Res.string.edit_charge)
        } else {
            stringResource(Res.string.add_new_charge)
        },
        confirmText = if (isEdit) {
            stringResource(Res.string.edit_charge)
        } else {
            stringResource(Res.string.add)
        },
        dismissText = stringResource(Res.string.back),
        showDatePicker = state.showChargesDatePick,
        selectedChargeName = if (state.chooseChargeIndex == -1) {
            ""
        } else {
            state.loanTemplate?.chargeOptions[state.chooseChargeIndex]?.name ?: ""
        },
        selectedDate = state.chargeDate,
        chargeAmount = state.chargeAmount,
        chargeType = if (state.chooseChargeIndex == -1) {
            ""
        } else {
            state.loanTemplate?.chargeOptions[state.chooseChargeIndex]?.chargeCalculationType?.value
                ?: ""
        },
        chargeCollectedOn = if (state.chooseChargeIndex == -1) {
            ""
        } else {
            state.loanTemplate?.chargeOptions[state.chooseChargeIndex]?.chargeTimeType?.value
                ?: ""
        },
        chargeOptions = state.loanTemplate?.chargeOptions?.map { it.name ?: "" } ?: emptyList(),
        onConfirm = {
            if (isEdit) {
                onAction(NewLoanAccountAction.EditCharge(index))
            } else {
                onAction(NewLoanAccountAction.AddChargeToList)
            }
        },
        onDismiss = { onAction(NewLoanAccountAction.DismissDialog) },
        onChargeSelected = { index, _ ->
            onAction(NewLoanAccountAction.OnChooseChargeIndexChange(index))
        },
        onDatePick = { show ->
            onAction(NewLoanAccountAction.OnChargesDatePick(show))
        },
        onDateChange = { newDate ->
            onAction(
                NewLoanAccountAction.OnChargesDateChange(
                    DateHelper.getDateAsStringFromLong(
                        newDate,
                    ),
                ),
            )
        },
        onAmountChange = { amount ->
            onAction(NewLoanAccountAction.OnChargesAmountChange(amount))
        },
    )
}

@Composable
private fun ShowChargesDialog(
    isOverDue: Boolean = false,
    state: NewLoanAccountState,
    onAction: (NewLoanAccountAction) -> Unit,
) {
    var expandedIndex: Int? by rememberSaveable { mutableStateOf(-1) }

    MifosBottomSheet(
        onDismiss = {
            onAction(NewLoanAccountAction.DismissDialog)
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(DesignToken.padding.large),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
            ) {
                Text(
                    text = stringResource(Res.string.view_charges),
                    style = MifosTypography.titleMediumEmphasized,
                )
                if (isOverDue) {
                    state.loanTemplate?.overdueCharges?.forEachIndexed { index, it ->
                        MifosActionsChargeListingComponent(
                            chargeTitle = it.name.toString(),
                            type = it.chargeCalculationType?.value.toString(),
                            date = it.formattedDueDate,
                            collectedOn = it.chargeTimeType?.value.toString(),
                            amount = it.amount.toString(),
                            onActionClicked = {},
                            isExpanded = expandedIndex == index,
                            onExpandToggle = {
                                expandedIndex = if (expandedIndex == index) -1 else index
                            },
                        )
                    }
                } else {
                    if (state.addedCharges.isNotEmpty()) {
                        state.addedCharges.forEachIndexed { index, it ->
                            MifosActionsChargeListingComponent(
                                chargeTitle = it.name.toString(),
                                type = it.type.toString(),
                                date = it.date,
                                collectedOn = it.collectedOn,
                                amount = it.amount.toString(),
                                onActionClicked = { action ->
                                    when (action) {
                                        is Actions.Delete -> {
                                            expandedIndex = -1
                                            onAction(
                                                NewLoanAccountAction.DeleteChargeFromSelectedCharges(
                                                    index,
                                                ),
                                            )
                                        }

                                        is Actions.Edit -> {
                                            onAction(NewLoanAccountAction.EditChargeDialog(index))
                                        }

                                        else -> {}
                                    }
                                },
                                isExpanded = expandedIndex == index,
                                onExpandToggle = {
                                    expandedIndex = if (expandedIndex == index) -1 else index
                                },
                            )
                        }
                    } else {
                        MifosEmptyCard(
                            msg = stringResource(Res.string.click_on_add_new),
                        )
                    }
                }

                MifosTwoButtonRow(
                    firstBtnText = stringResource(Res.string.back),
                    secondBtnText = stringResource(Res.string.add_new),
                    onFirstBtnClick = {
                        onAction(NewLoanAccountAction.DismissDialog)
                    },
                    onSecondBtnClick = {
                        onAction(NewLoanAccountAction.ShowAddChargeDialog)
                    },
                )
            }
        },
    )
}
