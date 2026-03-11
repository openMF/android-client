/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.createShareAccount

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_share_account_back
import androidclient.feature.client.generated.resources.feature_share_account_charge_add
import androidclient.feature.client.generated.resources.feature_share_account_charge_add_new_charge
import androidclient.feature.client.generated.resources.feature_share_account_charge_btn_add_new
import androidclient.feature.client.generated.resources.feature_share_account_charge_click_on_add_new
import androidclient.feature.client.generated.resources.feature_share_account_charge_edit_charge
import androidclient.feature.client.generated.resources.feature_share_account_charge_view_charges
import androidclient.feature.client.generated.resources.feature_share_account_charges
import androidclient.feature.client.generated.resources.feature_share_account_details
import androidclient.feature.client.generated.resources.feature_share_account_preview
import androidclient.feature.client.generated.resources.feature_share_account_terms
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.AddChargeBottomSheet
import com.mifos.core.ui.components.MifosActionsChargeListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.client.createShareAccount.pages.ChargesPage
import com.mifos.feature.client.createShareAccount.pages.DetailsPage
import com.mifos.feature.client.createShareAccount.pages.PreviewPage
import com.mifos.feature.client.createShareAccount.pages.TermsPage
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun CreateShareAccountScreen(
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateShareAccountViewModel = koinViewModel(),
    navController: NavController,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            CreateShareAccountEvent.NavigateBack -> onNavigateBack()
            CreateShareAccountEvent.Finish -> onFinish()
        }
    }

    CreateShareAccountDialog(
        state = state,
        onAction = { viewModel.trySendAction(it) },
        snackbarHostState = snackbarHostState,
    )

    CreateShareAccountContent(
        modifier = modifier,
        state = state,
        onAction = { viewModel.trySendAction(it) },
        navController = navController,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun CreateShareAccountDialog(
    state: CreateShareAccountState,
    onAction: (CreateShareAccountAction) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    when (state.dialogState) {
        is CreateShareAccountState.DialogState.AddNewCharge -> {
            AddNewChargeDialog(
                isEdit = state.dialogState.edit,
                index = state.dialogState.index,
                state = state,
                onAction = onAction,
            )
        }

        CreateShareAccountState.DialogState.ShowCharges -> {
            ShowChargesDialog(
                state = state,
                onAction = onAction,
            )
        }

        is CreateShareAccountState.DialogState.SuccessResponseStatus -> {
            LaunchedEffect(state.launchEffectKey) {
                snackbarHostState.showSnackbar(
                    message = state.dialogState.msg,
                )

                if (state.dialogState.successStatus) {
                    delay(1000)
                    onAction(CreateShareAccountAction.NavigateBack)
                }
            }
        }

        null -> Unit
    }
}

@Composable
private fun CreateShareAccountContent(
    state: CreateShareAccountState,
    modifier: Modifier = Modifier,
    onAction: (CreateShareAccountAction) -> Unit,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
) {
    val steps = listOf(
        Step(name = stringResource(Res.string.feature_share_account_details)) {
            DetailsPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(name = stringResource(Res.string.feature_share_account_terms)) {
            TermsPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(name = stringResource(Res.string.feature_share_account_charges)) {
            ChargesPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(name = stringResource(Res.string.feature_share_account_preview)) {
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
        when (state.screenState) {
            is CreateShareAccountState.ScreenState.Loading -> MifosProgressIndicator()
            is CreateShareAccountState.ScreenState.Success -> {
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
                            onAction(CreateShareAccountAction.OnStepChange(newIndex))
                        },
                        modifier = Modifier
                            .fillMaxWidth().align(Alignment.CenterHorizontally),
                    )
                }
            }

            is CreateShareAccountState.ScreenState.Error -> {
                MifosSweetError(
                    message = state.screenState.message,
                    onclick = { onAction(CreateShareAccountAction.Retry) },
                )
            }
        }

        if (state.isOverLayLoadingActive) {
            MifosProgressIndicatorOverlay()
        }
    }
}

@Composable
private fun AddNewChargeDialog(
    isEdit: Boolean,
    index: Int = -1,
    state: CreateShareAccountState,
    onAction: (CreateShareAccountAction) -> Unit,
) {
    AddChargeBottomSheet(
        title = if (isEdit) {
            stringResource(Res.string.feature_share_account_charge_edit_charge)
        } else {
            stringResource(Res.string.feature_share_account_charge_add_new_charge)
        },
        confirmText = if (isEdit) {
            stringResource(Res.string.feature_share_account_charge_edit_charge)
        } else {
            stringResource(Res.string.feature_share_account_charge_add)
        },
        dismissText = stringResource(Res.string.feature_share_account_back),
        selectedChargeName = if (state.chooseChargeIndex == null) {
            ""
        } else {
            state.chargeOptions[state.chooseChargeIndex].name ?: ""
        },
        chargeAmount = state.chargeAmount,
        chargeType = if (state.chooseChargeIndex == null) {
            ""
        } else {
            state.chargeOptions[state.chooseChargeIndex].chargeCalculationType?.value
                ?: ""
        },
        chargeCollectedOn = if (state.chooseChargeIndex == null) {
            ""
        } else {
            state.chargeOptions[state.chooseChargeIndex].chargeTimeType?.value
                ?: ""
        },
        chargeOptions = state.chargeOptions.map { it.name ?: "" },
        onConfirm = {
            if (isEdit) {
                onAction(CreateShareAccountAction.EditCharge(index))
            } else {
                onAction(CreateShareAccountAction.AddChargeToList)
            }
        },
        onDismiss = { onAction(CreateShareAccountAction.OnDismissDialog) },
        onChargeSelected = { index, _ ->
            onAction(CreateShareAccountAction.OnChooseChargeIndexChange(index))
        },
        onDatePick = {},
        onDateChange = {},
        onAmountChange = { amount ->
            onAction(CreateShareAccountAction.OnChargeAmountChange(amount))
        },
    )
}

@Composable
private fun ShowChargesDialog(
    state: CreateShareAccountState,
    onAction: (CreateShareAccountAction) -> Unit,
) {
    var expandedIndex: Int? by rememberSaveable { mutableStateOf(-1) }

    MifosBottomSheet(
        onDismiss = {
            onAction(CreateShareAccountAction.OnDismissDialog)
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(KptTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
            ) {
                Text(
                    text = stringResource(Res.string.feature_share_account_charge_view_charges),
                    style = MifosTypography.titleMediumEmphasized,
                )
                if (state.addedCharges.isNotEmpty()) {
                    state.addedCharges.forEachIndexed { index, charge ->
                        val chargesValue = state.chargeOptions
                            .firstOrNull { it.id == charge.chargeId }
                        MifosActionsChargeListingComponent(
                            chargeTitle = chargesValue?.name ?: "",
                            type = chargesValue?.chargeCalculationType?.value ?: "",
                            collectedOn = chargesValue?.chargeTimeType?.value ?: "",
                            amount = charge.amount.toString(),
                            onActionClicked = { action ->
                                when (action) {
                                    is Actions.Delete -> {
                                        expandedIndex = -1
                                        onAction(
                                            CreateShareAccountAction.DeleteChargeFromSelectedCharges(
                                                index,
                                            ),
                                        )
                                    }

                                    is Actions.Edit -> {
                                        onAction(CreateShareAccountAction.EditChargeDialog(index))
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
                        msg = stringResource(Res.string.feature_share_account_charge_click_on_add_new),
                    )
                }
            }

            MifosTwoButtonRow(
                firstBtnText = stringResource(Res.string.feature_share_account_back),
                secondBtnText = stringResource(Res.string.feature_share_account_charge_btn_add_new),
                onFirstBtnClick = {
                    onAction(CreateShareAccountAction.OnDismissDialog)
                },
                onSecondBtnClick = {
                    onAction(CreateShareAccountAction.ShowAddChargeDialog)
                },
            )
        },
    )
}
