/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.newFixedDepositAccount.pages

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.client_identifier_btn_next
import androidclient.feature.client.generated.resources.client_identifier_btn_view
import androidclient.feature.client.generated.resources.feature_share_account_charge_active_charge
import androidclient.feature.client.generated.resources.feature_share_account_charge_add_new
import androidclient.feature.client.generated.resources.feature_share_account_charge_add_new_charge
import androidclient.feature.client.generated.resources.feature_share_account_charge_btn_add_new
import androidclient.feature.client.generated.resources.feature_share_account_charge_click_on_add_new
import androidclient.feature.client.generated.resources.feature_share_account_charge_edit_charge
import androidclient.feature.client.generated.resources.feature_share_account_charge_view_charges
import androidclient.feature.client.generated.resources.step_charges
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
import androidx.compose.material3.MaterialTheme
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
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountAction
import com.mifos.feature.client.newFixedDepositAccount.NewFixedDepositAccountState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChargesPage(
    state: NewFixedDepositAccountState,
    onAction: (NewFixedDepositAccountAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val chargesState = state.fixedDepositAccountCharges

    Column(modifier = Modifier.fillMaxSize().padding(bottom = DesignToken.padding.large)) {
        Column(
            modifier = modifier.weight(1f).verticalScroll(rememberScrollState()),
        ) {
            Text(
                stringResource(Res.string.step_charges),
                style = MifosTypography.labelLargeEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))

            // Add New Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Row(
                    modifier = Modifier.clickable {
                        onAction(
                            NewFixedDepositAccountAction.NewFixedDepositAccountChargesAction.OnShowAddChargeDialog,
                        )
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
                        text = stringResource(Res.string.feature_share_account_charge_add_new),
                        color = MaterialTheme.colorScheme.primary,
                        style = MifosTypography.labelLargeEmphasized,
                    )
                }
            }

            Spacer(Modifier.height(DesignToken.padding.large))

            // Active Charges Row
            MifosRowWithTextAndButton(
                onBtnClick = {
                    onAction(
                        NewFixedDepositAccountAction.NewFixedDepositAccountChargesAction.ShowListOfChargesDialog,
                    )
                },
                btnText = stringResource(Res.string.client_identifier_btn_view),
                text = "${chargesState.addedCharges.size} ${stringResource(Res.string.feature_share_account_charge_active_charge)}",
                btnEnabled = chargesState.addedCharges.isNotEmpty(),
            )
            Spacer(Modifier.height(DesignToken.padding.large))
        }

        // Back and Next Buttons
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.btn_back),
            secondBtnText = stringResource(Res.string.client_identifier_btn_next),
            onFirstBtnClick = {
                onAction(NewFixedDepositAccountAction.PreviousStep)
            },
            onSecondBtnClick = {
                onAction(NewFixedDepositAccountAction.OnNextPress)
            },
        )
    }
}

@Composable
internal fun AddNewChargeDialog(
    isEdit: Boolean,
    index: Int = -1,
    state: NewFixedDepositAccountState,
    onAction: (NewFixedDepositAccountAction) -> Unit,
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
            stringResource(Res.string.feature_share_account_charge_btn_add_new)
        },
        dismissText = stringResource(Res.string.btn_back),
        selectedChargeName = if (state.fixedDepositAccountCharges.selectedChargeIndex == null) {
            ""
        } else {
            state.template.chargeOptions?.getOrNull(state.fixedDepositAccountCharges.selectedChargeIndex)?.name
                ?: ""
        },
        chargeAmount = state.fixedDepositAccountCharges.chargeAmount,
        chargeType = if (state.fixedDepositAccountCharges.selectedChargeIndex == null) {
            ""
        } else {
            state.template.chargeOptions?.getOrNull(state.fixedDepositAccountCharges.selectedChargeIndex)?.chargeCalculationType?.value
                ?: ""
        },
        chargeCollectedOn = if (state.fixedDepositAccountCharges.selectedChargeIndex == null) {
            ""
        } else {
            state.template.chargeOptions?.getOrNull(state.fixedDepositAccountCharges.selectedChargeIndex)?.chargeTimeType?.value
                ?: ""
        },
        chargeOptions = state.template.chargeOptions?.map { it.name ?: "" } ?: emptyList(),
        onConfirm = {
            if (isEdit) {
                onAction(
                    NewFixedDepositAccountAction.NewFixedDepositAccountChargesAction.OnEditCharge(
                        index,
                    ),
                )
            } else {
                onAction(NewFixedDepositAccountAction.NewFixedDepositAccountChargesAction.AddChargeToList)
            }
        },
        onDismiss = {
            onAction(
                NewFixedDepositAccountAction.OnDismissDialog,
            )
        },
        onChargeSelected = { index, _ ->
            onAction(
                NewFixedDepositAccountAction.NewFixedDepositAccountChargesAction.OnChargeSelected(
                    index,
                ),
            )
        },
        onDatePick = {},
        onDateChange = {},
        onAmountChange = { amount ->
            onAction(
                NewFixedDepositAccountAction.NewFixedDepositAccountChargesAction.OnChargeAmountChange(
                    amount,
                ),
            )
        },
    )
}

@Composable
internal fun ShowChargesDialog(
    state: NewFixedDepositAccountState,
    onAction: (NewFixedDepositAccountAction) -> Unit,
) {
    var expandedIndex: Int? by rememberSaveable { mutableStateOf(-1) }

    MifosBottomSheet(
        onDismiss = {
            onAction(NewFixedDepositAccountAction.OnDismissDialog)
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(DesignToken.padding.large),
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
            ) {
                Text(
                    text = stringResource(Res.string.feature_share_account_charge_view_charges),
                    style = MifosTypography.titleMediumEmphasized,
                )

                if (state.fixedDepositAccountCharges.addedCharges.isNotEmpty()) {
                    state.fixedDepositAccountCharges.addedCharges.forEachIndexed { index, charge ->
                        val chargesValue =
                            state.template.chargeOptions?.firstOrNull { it.id == charge.chargeId }
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
                                            NewFixedDepositAccountAction.NewFixedDepositAccountChargesAction.DeleteChargeFromSelectedCharges(
                                                index,
                                            ),
                                        )
                                    }

                                    is Actions.Edit -> {
                                        onAction(
                                            NewFixedDepositAccountAction.NewFixedDepositAccountChargesAction.EditChargeDialog(
                                                index,
                                            ),
                                        )
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

                MifosTwoButtonRow(
                    firstBtnText = stringResource(Res.string.btn_back),
                    secondBtnText = stringResource(Res.string.feature_share_account_charge_add_new),
                    onFirstBtnClick = { onAction(NewFixedDepositAccountAction.OnDismissDialog) },
                    onSecondBtnClick = { onAction(NewFixedDepositAccountAction.NewFixedDepositAccountChargesAction.OnShowAddChargeDialog) },
                )
            }
        },
    )
}
