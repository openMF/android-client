package com.mifos.feature.client.clientUpcomingCharges

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_more_clients
import androidclient.feature.client.generated.resources.feature_client_no_more_clients_available
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosPagingAppendProgress
import com.mifos.core.ui.components.MifosActionsClientFeeListingComponent

@Composable
actual fun ChargesListContent(
    state: ClientUpcomingChargesState,
    onAction: (ClientUpcomingChargesAction) -> Unit,
    refresh : () -> Unit
) {
    val chargesPagingList = state.chargesFlow.collectAsLazyPagingItems()

    when(chargesPagingList.loadState.refresh){
        is LoadState.Error -> MifosSweetError(
            message = "Try again",
            onclick = refresh
        )
        LoadState.Loading -> MifosCircularProgress()

        is LoadState.NotLoading -> Unit
    }

    LazyColumn {
        items(
            count = chargesPagingList.,
            key = { index -> chargesPagingList[index]?.id ?: index },
        ){ index ->
            chargesPagingList[index]?.let { charge ->
                MifosActionsClientFeeListingComponent(
                    name = charge.name ?: "Not available",
                    dueAsOf = "",
                    due = ,
                    paid = (charge.currency.displaySymbol ?: "" + charge.amountPaid ?: 0.0),
                    waived = charge.amountWaived ?: 0.0,
                    outstanding = ,
                    menuList = listOf(),
                    isActive = ,
                    onClick = {},
                    onActionClicked = {}
                )
            }
        }

        when (chargesPagingList.loadState.append) {
            is LoadState.Error -> {
                item {
                    MifosSweetError(message = org.jetbrains.compose.resources.stringResource(Res.string.feature_client_failed_to_more_clients)) {
                        refresh()
                    }
                }
            }

            is LoadState.Loading -> {
                item {
                    MifosPagingAppendProgress()
                }
            }

            is LoadState.NotLoading -> {
                if (chargesPagingList.loadState.append.endOfPaginationReached &&
                    chargesPagingList.itemCount > 0
                ) {
                    item {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                            text = org.jetbrains.compose.resources.stringResource(Res.string.feature_client_no_more_clients_available),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}