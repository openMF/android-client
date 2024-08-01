package com.mifos.feature.groups.group_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mifos.core.data.repository.GroupsListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val PAGE_SIZE = 10

@HiltViewModel
class GroupsListViewModel @Inject constructor(
    repository: GroupsListRepository
) : ViewModel() {

    val data = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            enablePlaceholders = false,
            prefetchDistance = 1,
        ),
        pagingSourceFactory = {
            com.mifos.core.domain.use_cases.GroupsListPagingDataSource(repository, PAGE_SIZE)
        }
    ).flow.cachedIn(viewModelScope)

}