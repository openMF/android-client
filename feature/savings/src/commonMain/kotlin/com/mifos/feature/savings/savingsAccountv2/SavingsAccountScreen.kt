/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountv2

import androidclient.feature.savings.generated.resources.Res
import androidclient.feature.savings.generated.resources.feature_savings_back
import androidclient.feature.savings.generated.resources.feature_savings_cancel
import androidclient.feature.savings.generated.resources.feature_savings_create_savings_account
import androidclient.feature.savings.generated.resources.feature_savings_error_not_connected_internet
import androidclient.feature.savings.generated.resources.step_charges
import androidclient.feature.savings.generated.resources.step_details
import androidclient.feature.savings.generated.resources.step_preview
import androidclient.feature.savings.generated.resources.step_terms
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.AddChargeBottomSheet
import com.mifos.core.ui.components.MifosActionsChargeListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.savings.savingsAccountv2.pages.ChargesPage
import com.mifos.feature.savings.savingsAccountv2.pages.DetailsPage
import com.mifos.feature.savings.savingsAccountv2.pages.PreviewPage
import com.mifos.feature.savings.savingsAccountv2.pages.TermsPage
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SavingsAccountScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavingsAccountViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            SavingsAccountEvent.NavigateBack -> onNavigateBack()
            SavingsAccountEvent.Finish -> onFinish()
        }
    }

    NewSavingsAccountDialog(
        state = state,
        onAction = { viewModel.trySendAction(it) },
    )

    SavingsAccountScaffold(
        modifier = modifier.fillMaxWidth(),
        state = state,
        onAction = { viewModel.trySendAction(it) },
        navController = navController,
    )
}

@Composable
private fun SavingsAccountScaffold(
    navController: NavController,
    state: SavingsAccountState,
    modifier: Modifier = Modifier,
    onAction: (SavingsAccountAction) -> Unit,
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
            ChargesPage (
                state = state,
                onAction = onAction,
            )
        },
        Step(stringResource(Res.string.step_preview)) {
            PreviewPage {
                onAction(SavingsAccountAction.NextStep)
            }
        },
    )

    MifosScaffold(
        title = stringResource(Res.string.feature_savings_create_savings_account),
        onBackPressed = { onAction(SavingsAccountAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        when (state.screenState) {
            is SavingsAccountState.ScreenState.Loading -> MifosProgressIndicator()
            is SavingsAccountState.ScreenState.Success -> {
                Column(
                    Modifier.fillMaxSize().padding(paddingValues),
                ) {
                    MifosBreadcrumbNavBar(
                        navController,
                    )
                    MifosStepper(
                        steps = steps,
                        currentIndex = state.currentStep,
                        onStepChange = { newIndex ->
                            onAction(SavingsAccountAction.OnStepChange(newIndex))
                        },
                        modifier = Modifier
                            .fillMaxWidth().align(Alignment.CenterHorizontally),
                    )
                }
            }
            is SavingsAccountState.ScreenState.NetworkError -> {
                MifosSweetError(
                    message = stringResource(Res.string.feature_savings_error_not_connected_internet),
                    onclick = { onAction(SavingsAccountAction.Retry) },
                )
            }
        }
        if (state.isOverLayLoadingActive) {
            MifosProgressIndicatorOverlay()
        }
    }
}

@Composable
private fun NewSavingsAccountDialog(
    state: SavingsAccountState,
    onAction: (SavingsAccountAction) -> Unit,
) {
    when (state.dialogState) {
        is SavingsAccountState.DialogState.Error -> {
            MifosSweetError(
                message = state.dialogState.message,
                onclick = { onAction(SavingsAccountAction.Retry) },
            )
        }
        is SavingsAccountState.DialogState.AddNewCharge -> AddNewChargeDialog(
            isEdit = state.dialogState.edit,
            state = state,
            onAction = onAction,
            index = state.dialogState.index,
        )
        is SavingsAccountState.DialogState.ShowCharges -> ShowChargesDialog(
            state = state,
            onAction = onAction,
        )
        null -> Unit
    }
}

@Composable
private fun AddNewChargeDialog(
    isEdit: Boolean,
    index: Int = -1,
    state: SavingsAccountState,
    onAction: (SavingsAccountAction) -> Unit,
) {
    AddChargeBottomSheet(
        title = if (isEdit) {
            "Edit charge"
        } else {
            "Add new charge"
        },
        confirmText = if (isEdit) {
            "Edit charge"
        } else {
            "Add"
        },
        dismissText = stringResource(Res.string.feature_savings_cancel),
        showDatePicker = state.showChargesDatePick,
        selectedChargeName = if (state.chooseChargeIndex == -1) {
            ""
        } else {
            ""
//            state.loanTemplate?.chargeOptions[state.chooseChargeIndex]?.name ?: ""
        },
        selectedDate = state.chargeDate,
        chargeAmount = state.chargeAmount,
        chargeType = if (state.chooseChargeIndex == -1) {
            ""
        } else {
//            state.loanTemplate?.chargeOptions[state.chooseChargeIndex]?.chargeCalculationType?.value
//                ?: ""
            ""
        },
        chargeCollectedOn = if (state.chooseChargeIndex == -1) {
            ""
        } else {
            ""
//            state.loanTemplate?.chargeOptions[state.chooseChargeIndex]?.chargeTimeType?.value
//                ?: ""
        },
//        chargeOptions = state.loanTemplate?.chargeOptions?.map { it.name ?: "" } ?: emptyList(),
        chargeOptions = emptyList(),
        onConfirm = {
            if (isEdit) {
                onAction(SavingsAccountAction.EditCharge(index))
            } else {
                onAction(SavingsAccountAction.AddChargeToList)
            }
        },
        onDismiss = { onAction(SavingsAccountAction.DismissDialog) },
        onChargeSelected = { index, _ ->
            onAction(SavingsAccountAction.OnChooseChargeIndexChange(index))
        },
        onDatePick = { show ->
            onAction(SavingsAccountAction.OnChargesDatePick(show))
        },
        onDateChange = { newDate ->
            onAction(SavingsAccountAction.OnChargesDateChange(DateHelper.getDateAsStringFromLong(newDate)))
        },
        onAmountChange = { amount ->
            onAction(SavingsAccountAction.OnChargesAmountChange(amount))
        },
    )
}

@Composable
private fun ShowChargesDialog(
    isOverDue: Boolean = false,
    state: SavingsAccountState,
    onAction: (SavingsAccountAction) -> Unit,
) {
    MifosBottomSheet(
        onDismiss = {
            onAction(SavingsAccountAction.DismissDialog)
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(DesignToken.padding.large),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
            ) {
                Text(
                    text = "View Charges",
                    style = MifosTypography.titleMediumEmphasized,
                )
                if (isOverDue) {
//                    state.loanTemplate?.overdueCharges?.forEachIndexed { index, it ->
//                        MifosActionsChargeListingComponent(
//                            chargeTitle = it.name.toString(),
//                            type = it.chargeCalculationType?.value.toString(),
//                            date = it.formattedDueDate,
//                            collectedOn = it.chargeTimeType?.value.toString(),
//                            amount = it.amount.toString(),
//                            onActionClicked = {},
//                            isExpandable = false,
//                        )
//                    }
                } else {
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
                                        onAction(SavingsAccountAction.DeleteChargeFromSelectedCharges(index))
                                    }
                                    is Actions.Edit -> {
                                        onAction(SavingsAccountAction.EditChargeDialog(index))
                                    }
                                    else -> {}
                                }
                            },
                            isExpandable = true,
                        )
                    }
                }

                MifosTwoButtonRow(
                    firstBtnText = stringResource(Res.string.feature_savings_back),
                    secondBtnText = "Add New",
                    onFirstBtnClick = {
                        onAction(SavingsAccountAction.DismissDialog)
                    },
                    onSecondBtnClick = {
                        onAction(SavingsAccountAction.ShowAddChargeDialog)
                    },
                )
            }
        },
    )
}
