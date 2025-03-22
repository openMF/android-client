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
import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.room.entities.client.ClientEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * Created by Aditya Gupta on 21/02/24.
 */

class ClientListPagingSource(
    private val dataManagerClient: DataManagerClient,
) : PagingSource<Int, ClientEntity>() {

    override fun getRefreshKey(state: PagingState<Int, ClientEntity>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(10) ?: state.closestPageToPosition(
                position,
            )?.nextKey?.minus(10)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ClientEntity> {
        val position = params.key ?: 0
        return try {
            val getClients = getClientList(position)
            val clientList = getClients.first
            val totalClients = getClients.second
            val clientDbList = getClientDbList()
            val clientListWithSync = getClientListWithSync(clientList, clientDbList)
            LoadResult.Page(
                data = clientListWithSync,
                prevKey = if (position <= 0) null else position - 10,
                nextKey = if (position >= totalClients) null else position + 10,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun getClientList(position: Int): Pair<List<ClientEntity>, Int> {
        val response = dataManagerClient.getAllClients(position, 10)
        return Pair(response.pageItems, response.totalFilteredRecords)
    }

    private suspend fun getClientDbList(): List<ClientEntity> {
        return dataManagerClient.allDatabaseClients
            .map { it.pageItems }
            .firstOrNull() ?: emptyList()
    }

    private fun getClientListWithSync(
        clientList: List<ClientEntity>,
        clientDbList: List<ClientEntity>,
    ): List<ClientEntity> {
        if (clientDbList.isNotEmpty()) {
            clientList.forEach { client ->
                clientDbList.forEach { clientDb ->
                    if (client.id == clientDb.id) {
                        // TODO:: Unused result of data class copy, fix this implementation
                        client.copy(sync = true)
                    }
                }
            }
        }
        return clientList
    }
}
