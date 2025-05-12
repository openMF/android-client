/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.room.entities.group.GroupEntity
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.io.IOException

class GroupsListPagingDataSource(
    private val repository: GroupsListRepository,
    private val limit: Int,
) : PagingSource<Int, GroupEntity>() {
    override fun getRefreshKey(state: PagingState<Int, GroupEntity>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(limit)
                ?: state.closestPageToPosition(
                    position,
                )?.nextKey?.minus(limit)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GroupEntity> {
        val currentOffset = params.key ?: 0
        return try {
            val groups = repository.getAllGroups(paged = true, currentOffset, limit)

            LoadResult.Page(
                data = groups,
                prevKey = if (currentOffset <= 0) null else currentOffset - limit,
                nextKey = if (groups.isEmpty()) null else currentOffset + limit,
            )
        } catch (e: ClientRequestException) {
            LoadResult.Error(e)
        } catch (e: ServerResponseException) {
            LoadResult.Error(e)
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
