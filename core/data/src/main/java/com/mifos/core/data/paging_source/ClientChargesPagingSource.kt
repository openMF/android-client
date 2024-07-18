package com.mifos.core.data.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mifos.core.network.datamanager.DataManagerCharge
import com.mifos.core.objects.client.Charges
import com.mifos.core.objects.client.Page
import kotlinx.coroutines.suspendCancellableCoroutine
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ClientChargesPagingSource(
    private val clientId: Int,
    private val dataManagerCharge: DataManagerCharge
) :
    PagingSource<Int, Charges>() {

    override fun getRefreshKey(state: PagingState<Int, Charges>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(10) ?: state.closestPageToPosition(
                position
            )?.nextKey?.minus(10)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Charges> {
        val position = params.key ?: 0
        return try {
            val getClientCharges = getClientChargeList(clientId, position)
            val clientChargesList = getClientCharges.first
            val totalCharges = getClientCharges.second
            LoadResult.Page(
                data = clientChargesList,
                prevKey = if (position <= 0) null else position - 10,
                nextKey = if (position >= totalCharges) null else position + 10
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private suspend fun getClientChargeList(
        clientId: Int,
        position: Int
    ): Pair<List<Charges>, Int> {
        return suspendCancellableCoroutine { continuation ->
            dataManagerCharge.getClientCharges(clientId = clientId, offset = position, 10)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Charges>>() {
                    override fun onCompleted() {
                    }

                    override fun onError(exception: Throwable) {
                        continuation.resumeWithException(exception)
                    }

                    override fun onNext(page: Page<Charges>) {
                        continuation.resume(Pair(page.pageItems, page.totalFilteredRecords))
                    }
                })
        }
    }
}