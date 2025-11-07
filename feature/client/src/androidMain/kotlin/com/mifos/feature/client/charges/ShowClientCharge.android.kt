/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
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
import com.mifos.core.model.objects.clients.Page
import com.mifos.core.ui.components.Actions
import com.mifos.core.ui.components.MifosActionsChargeListingComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun ShowClientCharge(
    pagingFlow: Flow<PagingData<Page<ChargesEntity>>>,
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
            val chargesList = chargesPagingList[0]?.pageItems ?: emptyList()

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
                            items(chargesList) {
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
                },
            )
        }
    }
}
