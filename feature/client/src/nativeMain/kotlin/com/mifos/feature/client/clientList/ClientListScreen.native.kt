package com.mifos.feature.client.clientList

import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.Flow

@Composable
actual fun LazyColumnForClientListApi(
    pagingFlow: Flow<PagingData<ClientEntity>>,
    isInSelectionMode: Boolean,
    selectedItems: ClientSelectionState,
    failedRefresh: () -> Unit,
    onClientSelect: (Int) -> Unit,
    selectedMode: () -> Unit
) {
    TODO("Not yet implemented")
}