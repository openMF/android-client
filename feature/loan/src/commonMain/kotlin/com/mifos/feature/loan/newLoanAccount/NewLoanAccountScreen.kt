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
import androidclient.feature.loan.generated.resources.new_loan_account_title
import androidclient.feature.loan.generated.resources.step_charges
import androidclient.feature.loan.generated.resources.step_details
import androidclient.feature.loan.generated.resources.step_preview
import androidclient.feature.loan.generated.resources.step_schedule
import androidclient.feature.loan.generated.resources.step_terms
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosBasicDialog
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextFieldConfig
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosListingComponentOutline
import com.mifos.core.ui.components.MifosListingRowItem
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.loan.newLoanAccount.pages.ChargesPage
import com.mifos.feature.loan.newLoanAccount.pages.DetailsPage
import com.mifos.feature.loan.newLoanAccount.pages.PreviewPage
import com.mifos.feature.loan.newLoanAccount.pages.SchedulePage
import com.mifos.feature.loan.newLoanAccount.pages.TermsPage
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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

    NewLoanAccountDialogs(
        state = state,
        onAction = { viewModel.trySendAction(it) },
    )

    NewLoanAccountScaffold(
        modifier = modifier,
        state = state,
        onAction = { viewModel.trySendAction(it) },
        navController = navController,
    )
}

@Composable
private fun NewLoanAccountScaffold(
    navController: NavController,
    state: NewLoanAccountState,
    modifier: Modifier = Modifier,
    onAction: (NewLoanAccountAction) -> Unit,
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
            ChargesPage {
                onAction(NewLoanAccountAction.NextStep)
            }
        },
        Step(stringResource(Res.string.step_schedule)) {
            SchedulePage {
                onAction(NewLoanAccountAction.NextStep)
            }
        },
        Step(stringResource(Res.string.step_preview)) {
            PreviewPage {
                onAction(NewLoanAccountAction.NextStep)
            }
        },
    )

    MifosScaffold(
        title = stringResource(Res.string.new_loan_account_title),
        onBackPressed = { onAction(NewLoanAccountAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        when (state.screenState) {
            NewLoanAccountState.ScreenState.Loading -> MifosProgressIndicator()
            NewLoanAccountState.ScreenState.Success -> {
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
                            onAction(NewLoanAccountAction.OnStepChange(newIndex))
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }
            }

            NewLoanAccountState.ScreenState.NetworkError -> {
                MifosErrorComponent(
                    isNetworkConnected = state.networkConnection,
                    isRetryEnabled = true,
                    onRetry = {
                        onAction(NewLoanAccountAction.Retry)
                    },
                )
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
) {
    when (state.dialogState) {
        is NewLoanAccountState.DialogState.Error -> {
            MifosErrorComponent(
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = {
                    onAction(NewLoanAccountAction.Retry)
                },
            )
        }

        null -> Unit

        NewLoanAccountState.DialogState.AddNewCollateral -> AddNewCollateralDialog(
            state = state,
            onAction = onAction,
        )

        NewLoanAccountState.DialogState.ShowCollaterals -> ShowCollateralsDialog(
            state = state,
            onAction = onAction,
        )
    }
}

@Composable
private fun AddNewCollateralDialog(
    state: NewLoanAccountState,
    onAction: (NewLoanAccountAction) -> Unit,
) {
    MifosBasicDialog(
        title = "Add New Collateral",
        confirmText = "Add",
        dismissText = "Cancel",
        isConfirmEnabled = state.isCollateralBtnEnabled,
        onConfirm = {
            onAction(NewLoanAccountAction.AddCollateralToList)
        },
        onDismissRequest = {
            onAction(NewLoanAccountAction.DismissAddCollateralDialog)
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
                    label = "Collateral",
                )
                Spacer(modifier = Modifier.height(DesignToken.padding.medium))
                MifosOutlinedTextField(
                    value = state.collateralQuantity.toString(),
                    onValueChange = {
                        onAction(NewLoanAccountAction.OnCollateralQuantityChanged(it.toIntOrNull() ?: 0))
                    },
                    label = "Quantity",
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
                    label = "Total value",
                    config = MifosTextFieldConfig(
                        readOnly = true,
                        enabled = false,
                    ),
                )
                Spacer(modifier = Modifier.height(DesignToken.padding.large))
                MifosOutlinedTextField(
                    value = state.totalCollateral.toString(),
                    onValueChange = {},
                    label = "Total Collateral Value",
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
        title = "Add New Collateral",
        confirmText = "Add New",
        dismissText = "Back",
        isConfirmEnabled = state.isCollateralBtnEnabled,
        onConfirm = {
            onAction(NewLoanAccountAction.ShowAddCollateralDialog)
        },
        onDismissRequest = {
            onAction(NewLoanAccountAction.DismissAddCollateralDialog)
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
                                key = "Quantity",
                                value = it.quantity.toString(),
                            )
                            MifosListingRowItem(
                                key = "Total Value",
                                value = it.totalValue.toString(),
                            )
                            MifosListingRowItem(
                                key = "Total Collateral Value",
                                value = it.totalCollateral.toString(),
                            )
                        }
                    }
                }
            }
        },
    )
}
