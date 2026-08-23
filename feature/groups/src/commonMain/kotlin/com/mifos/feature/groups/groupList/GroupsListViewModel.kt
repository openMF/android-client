/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.groupList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.room.entities.group.GroupEntity
import kpt.core.base.store.paging.PagingScreenStream

class GroupsListViewModel(
    repository: GroupsListRepository,
) : ViewModel() {

    /**
     * Offline-first paged group-list stream — the native Store5 paging idiom that drives the
     * list body ([GroupsListScreen]'s `PagingScreenContent`). Replaces the previous online
     * Paging3 `Pager` (`GroupsListPagingDataSource`). The surrounding selection-mode toolbar /
     * sync dialog / FAB state is unchanged and still lives in the Route/Screen.
     */
    val pagingStream: PagingScreenStream<GroupEntity> =
        repository.groupListPagingStream(scope = viewModelScope)

    /** Pull-to-refresh / retry for the paged list. Resets the paging cursor to page 0. */
    fun retry() = pagingStream.refresh()
}
