/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mifos.core.network.datamanager.DataManagerCharge
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.first

class ClientChargesPagingSource(
    private val clientId: Int,
    private val dataManagerCharge: DataManagerCharge,
) :
    PagingSource<Int, ChargesEntity>() {

    override fun getRefreshKey(state: PagingState<Int, ChargesEntity>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(10)
                ?: state.closestPageToPosition(
                    position,
                )?.nextKey?.minus(10)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChargesEntity> {
        val position = params.key ?: 0
        return try {
            val (clientChargesList, totalCharges) = getClientChargeList(clientId, position)

            LoadResult.Page(
                data = clientChargesList,
                prevKey = if (position <= 0) null else position - 10,
                nextKey = if (position >= totalCharges) null else position + 10,
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private suspend fun getClientChargeList(
        clientId: Int,
        position: Int,
    ): Pair<List<ChargesEntity>, Int> {
        val page = dataManagerCharge.getClientCharges(
            clientId = clientId,
            offset = position,
            limit = 10,
        ).first()

        return Pair(page.pageItems, page.totalFilteredRecords)
    }
}
