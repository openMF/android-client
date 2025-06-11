/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.room.entities.zipmodels.GroupAndGroupAccounts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetGroupDetailsUseCase(
    private val repository: GroupDetailsRepository,
) {
    operator fun invoke(groupId: Int): Flow<DataState<GroupAndGroupAccounts>> =
        combine(
            repository.getGroup(groupId),
            repository.getGroupAccounts(groupId),
        ) { group, groupAccounts ->
            if (group is DataState.Success && groupAccounts is DataState.Success) {
                DataState.Success(
                    GroupAndGroupAccounts(
                        group = group.data,
                        groupAccounts = groupAccounts.data,
                    ),
                )
            } else if (group is DataState.Error || groupAccounts is DataState.Error) {
                val exception = (group as? DataState.Error)?.exception
                    ?: (groupAccounts as? DataState.Error)?.exception
                    ?: Exception("Unknown error")
                DataState.Error(exception)
            } else {
                DataState.Loading
            }
        }
}
