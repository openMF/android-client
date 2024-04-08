package com.mifos.feature.groupsList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.mifos.feature.groupsList.domain.repository.GroupsListRepository
import com.mifos.feature.groupsList.domain.use_case.GroupsListPagingDataSource
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
            GroupsListPagingDataSource(repository, PAGE_SIZE)
        }
    ).flow.cachedIn(viewModelScope)

}