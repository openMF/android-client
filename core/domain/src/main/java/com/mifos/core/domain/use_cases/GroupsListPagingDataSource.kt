package com.mifos.core.domain.use_cases

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.core.objects.group.Group
import retrofit2.HttpException
import java.io.IOException

class GroupsListPagingDataSource(
    private val repository: GroupsListRepository,
    private val limit: Int,
) : PagingSource<Int, Group>() {
    override fun getRefreshKey(state: PagingState<Int, Group>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(limit)
                ?: state.closestPageToPosition(
                    position
                )?.nextKey?.minus(limit)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Group> {
        val currentOffset = params.key ?: 0
        return try {
            val groups = repository.getAllGroups(paged = true, currentOffset, limit)

            LoadResult.Page(
                data = groups,
                prevKey = if (currentOffset <= 0) null else currentOffset - limit,
                nextKey = if (groups.isEmpty()) null else currentOffset + limit,
            )
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}