/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.GenerateCollectionSheetRepository
import com.mifos.room.entities.group.GroupEntity
import kotlinx.coroutines.flow.Flow

class GetGroupsByOfficeUseCase(
    private val repository: GenerateCollectionSheetRepository,
) {

    operator fun invoke(
        officeId: Int,
        params: Map<String, String>,
    ): Flow<DataState<List<GroupEntity>>> = repository.getGroupsByOffice(officeId, params)
}
