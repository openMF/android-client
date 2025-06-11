package com.mifos.feature.client.clientCharges

import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.Flow

@Composable
actual fun ClientChargeContent(
    pagingFlow: Flow<PagingData<ChargesEntity>>,
    onRetry: () -> Unit,
) {
    TODO("Not yet implemented")
}