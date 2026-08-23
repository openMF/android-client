/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.room.entities.zipmodels.GroupAndGroupAccounts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetGroupDetailsUseCase(
    private val repository: GroupDetailsRepository,
) {
    // offline-first-template-migration 03-core-datastate-removal (D18): plain upstream Flows —
    // `combine` propagates either's exception automatically.
    operator fun invoke(groupId: Int): Flow<GroupAndGroupAccounts> =
        combine(
            repository.getGroup(groupId),
            repository.getGroupAccounts(groupId),
        ) { group, groupAccounts ->
            GroupAndGroupAccounts(
                group = group,
                groupAccounts = groupAccounts,
            )
        }
}
