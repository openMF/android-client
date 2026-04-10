/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.recurringDeposit.newRecurringDepositAccount.pages

import androidclient.feature.recurringdeposit.generated.resources.Res
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_active_charge
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_add_new_charge
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_back
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_btn_add
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_btn_add_new
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_click_on_add_new
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_edit_charge
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_next
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_step_charges
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_view
import androidclient.feature.recurringdeposit.generated.resources.feature_recurring_deposit_view_charge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.AddChargeBottomSheet
import com.mifos.core.ui.components.MifosActionsChargeListingComponent
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosRowWithTextAndButton
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountAction
import com.mifos.feature.recurringDeposit.newRecurringDepositAccount.RecurringAccountState
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme

@Composable
fun ChargesPage(
    state: RecurringAccountState,
    modifier: Modifier = Modifier,
    onAction: (RecurringAccountAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(bottom = KptTheme.spacing.md)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                stringResource(Res.string.feature_recurring_deposit_step_charges),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Row(
                    modifier = Modifier.clickable {
                        onAction(
                            RecurringAccountAction.ShowAddChargeDialog,
                        )
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = MifosIcons.Add,
                        contentDescription = null,
                        tint = KptTheme.colorScheme.primary,
                        modifier = Modifier.size(DesignToken.sizes.iconSmall),
                    )

                    Text(
                        text = stringResource(Res.string.feature_recurring_deposit_btn_add_new),
                        color = KptTheme.colorScheme.primary,
                        style = MifosTypography.labelLargeEmphasized,
                    )
                }
            }

            Spacer(Modifier.height(KptTheme.spacing.md))

            MifosRowWithTextAndButton(
                onBtnClick = {
                    onAction(
                        RecurringAccountAction.ShowListOfChargesDialog,
                    )
                },
                btnText = stringResource(Res.string.feature_recurring_deposit_view),
                text = state.addedCharges.size.toString() + " " + stringResource(Res.string.feature_recurring_deposit_active_charge),
                btnEnabled = state.addedCharges.isNotEmpty(),
            )
            Spacer(Modifier.height(KptTheme.spacing.md))
        }
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.feature_recurring_deposit_back),
            secondBtnText = stringResource(Res.string.feature_recurring_deposit_next),
            onFirstBtnClick = {
                onAction(RecurringAccountAction.OnBackPress)
            },
            onSecondBtnClick = {
                onAction(RecurringAccountAction.OnNextPress)
            },
        )
    }
}

@Composable
internal fun AddNewChargeDialog(
    isEdit: Boolean,
    index: Int = -1,
    state: RecurringAccountState,
    onAction: (RecurringAccountAction) -> Unit,
) {
    AddChargeBottomSheet(
        title = if (isEdit) {
            stringResource(Res.string.feature_recurring_deposit_edit_charge)
        } else {
            stringResource(Res.string.feature_recurring_deposit_add_new_charge)
        },
        confirmText = if (isEdit) {
            stringResource(Res.string.feature_recurring_deposit_edit_charge)
        } else {
            stringResource(Res.string.feature_recurring_deposit_btn_add)
        },
        dismissText = stringResource(Res.string.feature_recurring_deposit_back),
        selectedChargeName = if (state.chooseChargeIndex == null) {
            ""
        } else {
            state.template.chargeOptions?.get(state.chooseChargeIndex)?.name ?: ""
        },
        chargeAmount = state.chargeAmount,
        chargeType = if (state.chooseChargeIndex == null) {
            ""
        } else {
            state.template.chargeOptions?.get(state.chooseChargeIndex)?.chargeCalculationType?.value
                ?: ""
        },
        chargeCollectedOn = if (state.chooseChargeIndex == null) {
            ""
        } else {
            state.template.chargeOptions?.get(state.chooseChargeIndex)?.chargeTimeType?.value
                ?: ""
        },
        chargeOptions = state.template.chargeOptions?.map { it.name ?: "" } ?: emptyList(),
        onConfirm = {
            if (isEdit) {
                onAction(RecurringAccountAction.EditCharge(index))
            } else {
                onAction(RecurringAccountAction.AddChargeToList)
            }
        },
        onDismiss = { onAction(RecurringAccountAction.OnDismissDialog) },
        onChargeSelected = { index, _ ->
            onAction(RecurringAccountAction.OnChooseChargeIndexChange(index))
        },
        onDatePick = {},
        onDateChange = {},
        onAmountChange = { amount ->
            onAction(RecurringAccountAction.OnChargeAmountChange(amount))
        },
    )
}

@Composable
internal fun ShowChargesDialog(
    state: RecurringAccountState,
    onAction: (RecurringAccountAction) -> Unit,
) {
    var expandedIndex: Int? by rememberSaveable { mutableStateOf(-1) }

    MifosBottomSheet(
        onDismiss = {
            onAction(RecurringAccountAction.OnDismissDialog)
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(KptTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
            ) {
                Text(
                    text = stringResource(Res.string.feature_recurring_deposit_view_charge),
                    style = MifosTypography.titleMediumEmphasized,
                )

                if (state.addedCharges.isNotEmpty()) {
                    state.addedCharges.forEachIndexed { index, charge ->
                        val chargesValue = state.template.chargeOptions
                            ?.firstOrNull { it.id == charge.chargeId }
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
                                            RecurringAccountAction.DeleteChargeFromSelectedCharges(
                                                index,
                                            ),
                                        )
                                    }

                                    is Actions.Edit -> {
                                        onAction(RecurringAccountAction.EditChargeDialog(index))
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
                        msg = stringResource(Res.string.feature_recurring_deposit_click_on_add_new),
                    )
                }

                MifosTwoButtonRow(
                    firstBtnText = stringResource(Res.string.feature_recurring_deposit_back),
                    secondBtnText = stringResource(Res.string.feature_recurring_deposit_btn_add_new),
                    onFirstBtnClick = {
                        onAction(RecurringAccountAction.OnDismissDialog)
                    },
                    onSecondBtnClick = {
                        onAction(RecurringAccountAction.ShowAddChargeDialog)
                    },
                )
            }
        },
    )
}
