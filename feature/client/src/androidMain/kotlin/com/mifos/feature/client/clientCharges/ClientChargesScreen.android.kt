/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientCharges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.btn_back
import androidclient.feature.client.generated.resources.client_identifier_btn_add
import androidclient.feature.client.generated.resources.client_identifier_btn_view
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_client_charges
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_more_charges
import androidclient.feature.client.generated.resources.feature_client_no_charges_found
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosActionsChargeListingComponent
import com.mifos.core.ui.components.MifosEmptyUi
import com.mifos.core.ui.components.MifosPagingAppendProgress
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountAction
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun ClientChargeContent(
    pagingFlow: Flow<PagingData<ChargesEntity>>,
    onAction:  (ClientChargesAction) -> Unit,
) {
    val chargesPagingList = pagingFlow.collectAsLazyPagingItems()

    when (chargesPagingList.loadState.refresh) {
        is LoadState.Error -> {
            MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_load_client_charges)) {
                onAction(ClientChargesAction.OnRetry)
            }
        }

        is LoadState.Loading -> MifosProgressIndicator()

        is LoadState.NotLoading -> {
            if (chargesPagingList.itemCount == 0) {
                MifosEmptyUi(
                    text = stringResource(Res.string.feature_client_no_charges_found),
                    icon = MifosIcons.Payments,
                )
            } else {
                val chargesList = List(chargesPagingList.itemCount){index ->
                    chargesPagingList[index]
                }.filterNotNull()
                // Use a composite key of id and index to guarantee uniqueness,
                // preventing LazyColumn crashes when duplicate ids are present in paged data.

                MifosBottomSheet(onDismiss = {
                    onAction(ClientChargesAction.CloseDialog)
                },
                    content = {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(DesignToken.padding.large),
                            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.largeIncreased),
                        ) {
                            Text(
                                text = stringResource(Res.string.client_identifier_btn_view),
                                style = MifosTypography.titleMediumEmphasized,
                            )
                            chargesList.forEach { it ->
                                MifosActionsChargeListingComponent(
                                    chargeTitle = it.name.toString(),
                                    type = it.chargeCalculationType?.value.toString(),
                                    date = it.formattedDueDate,
                                    collectedOn = it.formattedDueDate,
                                    amount = it.amount.toString(),
//                            menuList = ,
                                    onActionClicked = {},
                                    isExpandable = true
                                )
                            }
                            MifosTwoButtonRow(
                                firstBtnText = stringResource(Res.string.btn_back),
                                secondBtnText = stringResource(Res.string.client_identifier_btn_add),
                                onFirstBtnClick = {

                                },
                                onSecondBtnClick = {
//                                    onAction(NewLoanAccountAction.ShowAddChargeDialog)
                                },
                            )
                        }
                    }
                )
            }

//                LazyColumn {
//                    items(
//                        chargesPagingList.itemCount,
//                        key = { index ->
//                            val id = chargesPagingList[index]?.id
//                            if (id != null) "id_${id}_index_$index" else "index_$index"
//                        },
//                    ) { index ->
//                        chargesPagingList[index]?.let { ChargesItems(it) }
//                    }
//
//                    when (chargesPagingList.loadState.append) {
//                        is LoadState.Error -> {
//                            item {
//                                MifosSweetError(message = stringResource(Res.string.feature_client_failed_to_load_more_charges)) {
//                                    onRetry()
//                                }
//                            }
//                        }
//
//                        is LoadState.Loading -> {
//                            item {
//                                MifosPagingAppendProgress()
//                            }
//                        }
//
//                        is LoadState.NotLoading -> Unit
//                    }
//
//                    if (
//                        chargesPagingList.loadState.append is LoadState.NotLoading &&
//                        chargesPagingList.loadState.append.endOfPaginationReached &&
//                        chargesPagingList.itemCount > 0
//                    ) {
//                        item {
//                            Text(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(6.dp),
//                                text = stringResource(Res.string.feature_client_no_more_charges_available),
//                                style = MaterialTheme.typography.bodyMedium,
//                                textAlign = TextAlign.Center,
//                            )
//                        }
//                    }
//                }
            }
        }
    }
