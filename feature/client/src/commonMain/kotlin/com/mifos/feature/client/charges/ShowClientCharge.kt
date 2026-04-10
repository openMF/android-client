/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.client.charges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.action_add
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.charges_view_charges
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client_charges
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsChargeListingComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShowClientCharge(
    pagingFlow: Flow<PagingData<ChargesEntity>>,
    onAction: (ChargesAction) -> Unit,
) {
    val chargesPagingList = pagingFlow.collectAsLazyPagingItems()

    var expandedIndex by rememberSaveable { mutableStateOf(-1) }

    when (chargesPagingList.loadState.refresh) {
        is LoadState.Error -> {
            MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_load_client_charges)) {
                onAction(ChargesAction.OnRetry)
            }
        }

        is LoadState.Loading -> MifosProgressIndicator()

        is LoadState.NotLoading -> {
            MifosBottomSheet(
                onDismiss = {
                    onAction(ChargesAction.DismissDialog)
                },
                content = {
                    Column(
                        modifier = Modifier.padding(DesignToken.padding.large)
                            .heightIn(max = DesignToken.spacing.half),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
                    ) {
                        Text(
                            text = stringResource(Res.string.charges_view_charges),
                            style = MifosTypography.titleMediumEmphasized,
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
                        ) {
                            items(
                                count = chargesPagingList.itemCount,
                                key = { index -> chargesPagingList[index]?.id ?: index },
                            ) { index ->
                                chargesPagingList[index]?.let { charge ->
                                    MifosActionsChargeListingComponent(
                                        chargeTitle = charge.name.orEmpty(),
                                        type = charge.chargeCalculationType?.value.toString(),
                                        date = charge.formattedDueDate,
                                        collectedOn = charge.formattedDueDate,
                                        amount = charge.amount.toString(),
                                        onActionClicked = { action ->
                                            when (action) {
                                                is Actions.Delete -> {
                                                    onAction(ChargesAction.DeleteCharge(charge.id))
                                                }

                                                is Actions.Edit -> {
                                                    onAction(ChargesAction.FetchEditChargeData(charge.id))
                                                }

                                                else -> {}
                                            }
                                        },
                                        isExpanded = expandedIndex == charge.id,
                                        onExpandToggle = {
                                            expandedIndex =
                                                if (expandedIndex == charge.id) -1 else charge.id
                                        },
                                    )
                                }
                            }
                        }

                        MifosTwoButtonRow(
                            firstBtnText = stringResource(Res.string.btn_back),
                            secondBtnText = stringResource(Res.string.action_add),
                            onFirstBtnClick = {
                                onAction(ChargesAction.DismissDialog)
                            },
                            onSecondBtnClick = {
                                onAction(ChargesAction.CreateCharge)
                            },
                        )
                    }
                },
            )
        }
    }
}
