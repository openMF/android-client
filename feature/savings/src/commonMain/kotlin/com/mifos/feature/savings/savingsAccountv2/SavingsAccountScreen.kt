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
import androidclient.feature.savings.generated.resources.feature_savings_charges_click_on_add_new
import androidclient.feature.savings.generated.resources.step_charges
import androidclient.feature.savings.generated.resources.step_charges_add
import androidclient.feature.savings.generated.resources.step_charges_add_new
import androidclient.feature.savings.generated.resources.step_charges_edit_charge
import androidclient.feature.savings.generated.resources.step_charges_view
import androidclient.feature.savings.generated.resources.step_details
import androidclient.feature.savings.generated.resources.step_preview
import androidclient.feature.savings.generated.resources.step_terms
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.util.TextFieldsValidator.doubleNumberValidator
import com.mifos.feature.savings.savingsAccountv2.pages.ChargesPage
import com.mifos.feature.savings.savingsAccountv2.pages.DetailsPage
import com.mifos.feature.savings.savingsAccountv2.pages.PreviewPage
import com.mifos.feature.savings.savingsAccountv2.pages.TermsPage
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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

    val snackbarHostState = remember { SnackbarHostState() }

    NewSavingsAccountDialog(
        state = state,
        onAction = { viewModel.trySendAction(it) },
        snackbarHostState = snackbarHostState,
    )

    SavingsAccountScaffold(
        modifier = modifier.fillMaxWidth(),
        state = state,
        onAction = { viewModel.trySendAction(it) },
        navController = navController,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
private fun SavingsAccountScaffold(
    navController: NavController,
    state: SavingsAccountState,
    modifier: Modifier = Modifier,
    onAction: (SavingsAccountAction) -> Unit,
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

            is SavingsAccountState.ScreenState.Error -> {
                MifosSweetError(
                    message = state.screenState.message,
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
    snackbarHostState: SnackbarHostState,
) {
    when (state.dialogState) {
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

        is SavingsAccountState.DialogState.SuccessResponseStatus -> {
            LaunchedEffect(state.launchEffectKey) {
                snackbarHostState.showSnackbar(
                    message = state.dialogState.msg,
                )

                if (state.dialogState.successStatus) {
                    delay(1000)
                    onAction(SavingsAccountAction.Finish)
                }
            }
        }

        null -> Unit
    }
}

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
private fun AddNewChargeDialog(
    isEdit: Boolean,
    index: Int = -1,
    state: SavingsAccountState,
    onAction: (SavingsAccountAction) -> Unit,
) {
    LaunchedEffect(state.chargeAmount) {
        if (state.chargeAmount.isNotBlank()) {
            val amountError = doubleNumberValidator(state.chargeAmount)
            onAction(SavingsAccountAction.OnChargesAmountChangeError(amountError))
        } else {
            onAction(SavingsAccountAction.OnChargesAmountChangeError(null))
        }
    }
    fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis >= Clock.System.now().toEpochMilliseconds().minus(86_400_000L)
    }
    AddChargeBottomSheet(
        title = if (isEdit) {
            stringResource(Res.string.step_charges_edit_charge)
        } else {
            stringResource(Res.string.step_charges_add_new) + " " + stringResource(Res.string.step_charges)
        },
        confirmText = if (isEdit) {
            stringResource(Res.string.step_charges_edit_charge)
        } else {
            stringResource(Res.string.step_charges_add)
        },
        dismissText = stringResource(Res.string.feature_savings_back),
        showDatePicker = state.showChargesDatePick,
        selectedChargeName = if (state.chooseChargeIndex == -1) {
            ""
        } else {
            state.savingsProductTemplate?.chargeOptions?.getOrNull(state.chooseChargeIndex)?.name
                ?: ""
        },
        selectedDate = state.chargeDate,
        chargeAmount = state.chargeAmount,
        chargeType = if (state.chooseChargeIndex == -1) {
            ""
        } else {
            state.savingsProductTemplate?.chargeOptions?.get(state.chooseChargeIndex)?.chargeCalculationType?.value
                ?: ""
        },
        chargeCollectedOn = if (state.chooseChargeIndex == -1) {
            ""
        } else {
            state.savingsProductTemplate?.chargeOptions?.getOrNull(state.chooseChargeIndex)?.chargeTimeType?.value
                ?: ""
        },
        chargeOptions = state.savingsProductTemplate?.chargeOptions?.map { it.name ?: "" }
            ?: emptyList(),
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
            if (isSelectableDate(newDate)) {
                onAction(
                    SavingsAccountAction.OnChargesDateChange(
                        DateHelper.getDateAsStringFromLong(
                            newDate,
                        ),
                    ),
                )
            }
        },
        amountError = if (state.chargeAmountError != null) stringResource(state.chargeAmountError) else null,
        onAmountChange = { amount ->
            onAction(SavingsAccountAction.OnChargesAmountChange(amount))
        },
    )
}

@Composable
private fun ShowChargesDialog(
    state: SavingsAccountState,
    onAction: (SavingsAccountAction) -> Unit,
) {
    var expandedIndex: Int? by rememberSaveable { mutableStateOf(-1) }

    MifosBottomSheet(
        onDismiss = {
            onAction(SavingsAccountAction.DismissDialog)
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(KptTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
            ) {
                Text(
                    text = stringResource(Res.string.step_charges_view) + " " + stringResource(
                        Res.string.step_charges,
                    ),
                    style = MifosTypography.titleMediumEmphasized,
                )

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
                                            SavingsAccountAction.DeleteChargeFromSelectedCharges(
                                                index,
                                            ),
                                        )
                                    }

                                    is Actions.Edit -> {
                                        onAction(SavingsAccountAction.EditChargeDialog(index))
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
                } else {
                    MifosEmptyCard(
                        msg = stringResource(Res.string.feature_savings_charges_click_on_add_new),
                    )
                }
                MifosTwoButtonRow(
                    firstBtnText = stringResource(Res.string.feature_savings_back),
                    secondBtnText = stringResource(Res.string.step_charges_add_new),
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
