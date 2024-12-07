/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.groupList

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
    repository: GroupsListRepository,
) : ViewModel() {

    val data = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            enablePlaceholders = false,
            prefetchDistance = 1,
        ),
        pagingSourceFactory = {
            com.mifos.core.domain.useCases.GroupsListPagingDataSource(repository, PAGE_SIZE)
        },
    ).flow.cachedIn(viewModelScope)
}
