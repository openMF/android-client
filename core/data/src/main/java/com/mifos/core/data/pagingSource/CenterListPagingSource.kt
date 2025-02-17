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

import android.graphics.pdf.LoadParams
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.room.entities.group.Center
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CenterListPagingSource(private val dataManagerCenter: DataManagerCenter) :
    PagingSource<Int, Center>() {

    override fun getRefreshKey(state: PagingState<Int, Center>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(10) ?: state.closestPageToPosition(
                position,
            )?.nextKey?.minus(10)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Center> {
        val position = params.key ?: 0
        return try {
            val getCenters = getCenterList(position)
            val centerList = getCenters.first
            val totalCenters = getCenters.second
            val centerDbList = getCenterDbList()
            val centerListWithSync = getCenterListWithSync(centerList, centerDbList)
            LoadResult.Page(
                data = centerListWithSync,
                prevKey = if (position <= 0) null else position - 10,
                nextKey = if (position >= totalCenters) null else position + 10,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun getCenterList(position: Int): Pair<List<Center>, Int> {
        val pagedClient = dataManagerCenter.getCenters(true, position, 10)
        return Pair(pagedClient.pageItems, pagedClient.totalFilteredRecords)
    }

    private suspend fun getCenterDbList(): List<Center> {
        return dataManagerCenter.allDatabaseCenters
            .map { it.pageItems }
            .first()
    }

    private fun getCenterListWithSync(
        centerList: List<Center>,
        centerDbList: List<Center>,
    ): List<Center> {
        if (centerDbList.isNotEmpty()) {
            return centerList.map { center ->
                if (centerDbList.any { it.id == center.id }) {
                    center.copy(sync = true)
                } else {
                    center
                }
            }
        }
        return centerList
    }
}
