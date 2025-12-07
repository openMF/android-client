package com.mifos.feature.client.clientsList

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.Flow

@Composable
internal actual fun LazyColumnForClientListApi(
    pagingFlow: Flow<PagingData<ClientEntity>>,
    onRefresh: () -> Unit,
    onClientSelect: (Int) -> Unit,
    fetchImage: (Int) -> Unit,
    images: Map<Int, ByteArray?>,
    modifier: Modifier,
    sort: String?,
    onUpdateOffices: (List<String?>) -> Unit
) {
}