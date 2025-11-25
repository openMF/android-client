/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.recurringDeposit.newRecurringDepositAccount

import androidclient.feature.recurringdeposit.generated.resources.Res
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_back
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_cancel
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_create_recurring_deposit_account
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_next
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_next_button
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_charges
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_details
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_interest
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_settings
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_terms
import androidclient.feature.recurringdeposit.generated.resources.recurring_step_charges_add
import androidclient.feature.recurringdeposit.generated.resources.recurring_step_charges_add_new
import androidclient.feature.recurringdeposit.generated.resources.recurring_step_charges_edit_charge
import androidclient.feature.recurringdeposit.generated.resources.recurring_step_charges_view
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.AddChargeBottomSheet
import com.mifos.core.ui.components.MifosActionsChargeListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosStepper
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.core.ui.components.Step
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.util.TextFieldsValidator.doubleNumberValidator
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountAction.NavigateToStep
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.ChargesPage
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.DetailsPage
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.InterestPage
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.SettingPage
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages.TermsPage
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun RecurringAccountScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecurringAccountViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            RecurringAccountEvent.NavigateBack -> onNavigateBack()
            RecurringAccountEvent.Finish -> onFinish()
        }

    }


    RecurringAccountScaffold(
        navController = navController,
        modifier = modifier,
        state = state,
        onAction = { viewModel.trySendAction(it) },
    )
    val snackbarHostState = remember { SnackbarHostState() }
    NewRecurringAccountDialog(
        state = state,
        onAction = {viewModel.trySendAction(it)},
        snackbarHostState = snackbarHostState

    )
}

@Composable
private fun RecurringAccountScaffold(
    navController: NavController,
    state: RecurringAccountState,
    modifier: Modifier = Modifier,
    onAction: (RecurringAccountAction) -> Unit,
) {
    val steps = listOf(
        Step(name = stringResource(Res.string.feature_recurring_deposit_step_details)) {
            DetailsPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(name = stringResource(Res.string.feature_recurring_deposit_step_terms)) {
            TermsPage(
                onNext = { onAction(RecurringAccountAction.OnNextPress) },
            )
        },
        Step(name = stringResource(Res.string.feature_recurring_deposit_step_settings)) {
            SettingPage(
                state = state,
                onAction = onAction,
            )
        },
        Step(name = stringResource(Res.string.feature_recurring_deposit_step_interest)) {
            InterestPage(
                onNext = { onAction(RecurringAccountAction.OnNextPress) },
            )
        },
        Step(name = stringResource(Res.string.feature_recurring_deposit_step_charges)) {
            ChargesPage(
                state = state,
                onAction = onAction,
            )
        },
    )

    MifosScaffold(
        title = stringResource(Res.string.feature_recurring_deposit_create_recurring_deposit_account),
        onBackPressed = { onAction(RecurringAccountAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
        ) {
            MifosBreadcrumbNavBar(navController)
            when (state.screenState) {
                is RecurringAccountState.ScreenState.Error -> {
                    MifosErrorComponent(
                        message = state.screenState.message,
                        isRetryEnabled = true,
                    ) {
                        onAction(RecurringAccountAction.Retry)
                    }
                }

                is RecurringAccountState.ScreenState.Loading -> {
                    MifosProgressIndicator()
                }

                is RecurringAccountState.ScreenState.Success -> {
                    MifosStepper(
                        steps = steps,
                        currentIndex = state.currentStep,
                        onStepChange = { newIndex ->
                            onAction(NavigateToStep(newIndex))
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }
            }
        }
    }
}
@Composable
private fun NewRecurringAccountDialog(
    state: RecurringAccountState,
    onAction: (RecurringAccountAction) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    when (state.dialogState) {
        is RecurringAccountState.DialogState.AddNewCharge -> AddNewChargeDialog(
            isEdit = state.dialogState.edit,
            state = state,
            onAction = onAction,
            index = state.dialogState.index,
        )

        is RecurringAccountState.DialogState.showCharges -> ShowChargesDialog(
            state = state,
            onAction = onAction,
        )

        is RecurringAccountState.DialogState.SuccessResponseStatus -> {
            LaunchedEffect(state.launchEffectKey) {
                snackbarHostState.showSnackbar(
                    message = state.dialogState.msg,
                )

                if (state.dialogState.successStatus) {
                    delay(1000)
                    onAction(RecurringAccountAction.Finish)
                }
            }
        }

        null -> Unit
    }
}
@OptIn(ExperimentalTime::class,  ExperimentalTime::class)
@Composable
private fun AddNewChargeDialog(
    isEdit: Boolean,
    index: Int = -1,
    state: RecurringAccountState,
    onAction: (RecurringAccountAction) -> Unit,
) {
    var isAmountDirty by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.chargeAmount, isAmountDirty) {
        if (isAmountDirty) {
            if (state.chargeAmount.isNotEmpty()) {
                val amountError = doubleNumberValidator(state.chargeAmount)
                onAction(RecurringAccountAction.RecurringAccountChargesAction.OnChargesAmountChangeError(amountError))
            } else {
                onAction(RecurringAccountAction.RecurringAccountChargesAction.OnChargesAmountChangeError(null))
            }
        }
    }
    fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis >= Clock.System.now().toEpochMilliseconds().minus(86_400_000L)
    }
    AddChargeBottomSheet(
        title = if (isEdit) {
            stringResource(Res.string.recurring_step_charges_edit_charge)
        } else {
            stringResource(Res.string.recurring_step_charges_add_new) + " " + stringResource(Res.string.feature_recurring_deposit_step_charges)
        },
        confirmText = if (isEdit) {
            stringResource(Res.string.recurring_step_charges_edit_charge)
        } else {
            stringResource(Res.string.recurring_step_charges_add)
        },
        dismissText = stringResource(Res.string.feature_recurring_deposit_cancel),
        showDatePicker = state.showChargesDatePick,
        selectedChargeName = if (state.chooseChargeIndex == -1) {
            ""
        } else {
            state.template.chargeOptions?.getOrNull(state.chooseChargeIndex)?.name ?: ""
        },
        selectedDate = state.chargeDate,
        chargeAmount = state.chargeAmount,
        chargeType = if (state.chooseChargeIndex == -1) {
            ""
        } else {
            state.template.chargeOptions?.get(state.chooseChargeIndex)?.chargeCalculationType?.value
                ?: ""
        },
        chargeCollectedOn = if (state.chooseChargeIndex == -1) {
            ""
        } else {
            state.template.chargeOptions?.getOrNull(state.chooseChargeIndex)?.chargeTimeType?.value ?: ""
        },
        chargeOptions = state.template.chargeOptions?.map { it.name ?: "" } ?: emptyList(),
        onConfirm = {
            isAmountDirty = true
            if (state.chargeAmount.isNotEmpty() && state.chargeAmountError == null) {
                if (isEdit) {
                    onAction(RecurringAccountAction.RecurringAccountChargesAction.EditCharge(index))
                } else {
                    onAction(RecurringAccountAction.RecurringAccountChargesAction.AddChargeToList)
                }
            }
        },
        onDismiss = { onAction(RecurringAccountAction.RecurringAccountChargesAction.DismissDialog) },
        onChargeSelected = { index, _ ->
            onAction(RecurringAccountAction.RecurringAccountChargesAction.OnChooseChargeIndex(index))
        },
        onDatePick = { show ->
            onAction(RecurringAccountAction.RecurringAccountChargesAction.OnChargesDatePick(show))
        },
        onDateChange = { newDate ->
            if (isSelectableDate(newDate)) {
                onAction(RecurringAccountAction.RecurringAccountChargesAction.OnChargesDateChange(
                    DateHelper.getDateAsStringFromLong(newDate)))
            }
        },
        amountError = if (state.chargeAmountError != null) stringResource(state.chargeAmountError) else null,
        onAmountChange = { amount ->
            isAmountDirty = true
            onAction(RecurringAccountAction.RecurringAccountChargesAction.OnChargesAmountChange(amount))
        },
    )
}


@Composable
private fun ShowChargesDialog(
    state: RecurringAccountState,
    onAction: (RecurringAccountAction) -> Unit,
) {
    var expandedIndex: Int? by rememberSaveable { mutableStateOf(-1) }
    MifosBottomSheet(
        onDismiss = {
            onAction(RecurringAccountAction.RecurringAccountChargesAction.DismissDialog)
        },
        content = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(DesignToken.padding.large),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
            ) {
                item {
                    Text(
                        text = stringResource(Res.string.recurring_step_charges_view) + " " + stringResource(
                            Res.string.feature_recurring_deposit_step_charges
                        ),
                        style = MifosTypography.titleMediumEmphasized,
                    )
                }
                itemsIndexed(items = state.addedCharges) { index, it ->
                    MifosActionsChargeListingComponent(
                        chargeTitle = it.name.toString(),
                        type = it.type.toString(),
                        date = it.date,
                        collectedOn = it.collectedOn,
                        amount = it.amount.toString(),
                        onActionClicked = { action ->
                            when (action) {
                                is Actions.Delete -> {
                                    onAction(
                                        RecurringAccountAction.RecurringAccountChargesAction.DeleteChargeFromSelectedCharges(
                                            index
                                        )
                                    )
                                }

                                is Actions.Edit -> {
                                    onAction(
                                        RecurringAccountAction.RecurringAccountChargesAction.EditChargeDialog(
                                            index
                                        )
                                    )
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
                item {
                    MifosTwoButtonRow(
                        firstBtnText = stringResource(Res.string.feature_recurring_deposit_back),
                        secondBtnText = stringResource(Res.string.recurring_step_charges_add_new),
                        onFirstBtnClick = {
                            onAction(RecurringAccountAction.RecurringAccountChargesAction.DismissDialog)
                        },
                        onSecondBtnClick = {
                            onAction(RecurringAccountAction.RecurringAccountChargesAction.ShowAddChargeDialog)
                        },
                    )
                }


            }

            }
    )

}
