/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mifos.core.network.datamanager.DataManagerCharge
import com.mifos.room.entities.client.ChargesEntity
import kotlinx.coroutines.flow.first

class ClientChargesPagingSource(
    private val resourceType: String,
    private val resourceId: Int,
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
            val (clientChargesList, totalCharges) = getListOfPagingCharges(resourceType, resourceId, position)

            LoadResult.Page(
                data = clientChargesList,
                prevKey = if (position <= 0) null else position - 10,
                nextKey = if (position >= totalCharges) null else position + 10,
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private suspend fun getListOfPagingCharges(
        resourceType: String,
        resourceId: Int,
        position: Int,
    ): Pair<List<ChargesEntity>, Int> {
        val page = dataManagerCharge.getListOfPagingCharges(
            resourceType = resourceType,
            resourceId = resourceId,
            offset = position,
            limit = 10,
        ).first()

        return Pair(page.pageItems, page.totalFilteredRecords)
    }
}
