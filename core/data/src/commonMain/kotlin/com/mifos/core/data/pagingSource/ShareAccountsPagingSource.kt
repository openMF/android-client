package com.mifos.core.data.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mifos.core.model.objects.account.share.ShareAccounts
import com.mifos.core.network.datamanager.DataManagerShare

class ShareAccountsPagingSource(
    private val dataManagerShare: DataManagerShare,
) : PagingSource<Int, ShareAccounts>() {

    override fun getRefreshKey(state: PagingState<Int, ShareAccounts>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(PAGE_SIZE)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(PAGE_SIZE)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShareAccounts> {
        val position = params.key ?: 0
        return try {
            val (shareAccounts, totalRecords) = getShareAccountsList()
            LoadResult.Page(
                data = shareAccounts,
                prevKey = if (position <= 0) null else position - PAGE_SIZE,
                nextKey = if (position >= totalRecords) null else position + PAGE_SIZE,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun getShareAccountsList(): Pair<List<ShareAccounts>, Int> {
        val response = dataManagerShare.getAllShareAccounts()
        return Pair(response.pageItems, response.totalFilteredRecords)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
