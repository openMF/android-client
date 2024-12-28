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
import com.mifos.core.dbobjects.group.Center
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.core.objects.clients.Page
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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

    private suspend fun getCenterDbList(): List<Center> = suspendCoroutine { continuation ->
        try {
            dataManagerCenter.allDatabaseCenters
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Center>>() {
                    override fun onCompleted() {
                    }

                    override fun onError(error: Throwable) {
                        continuation.resumeWithException(error)
                    }

                    override fun onNext(centers: Page<Center>) {
                        continuation.resume(centers.pageItems)
                    }
                })
        } catch (exception: Exception) {
            continuation.resumeWithException(exception)
        }
    }

    private fun getCenterListWithSync(
        centerList: List<Center>,
        centerDbList: List<Center>,
    ): List<Center> {
        if (centerDbList.isNotEmpty()) {
            centerList.forEach { center ->
                centerDbList.forEach { centerDb ->
                    if (center.id == centerDb.id) {
                        center.sync = true
                    }
                }
            }
        }
        return centerList
    }
}
