/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.GroupListRepository
import com.mifos.core.network.DataManager
import com.mifos.room.entities.group.CenterWithAssociations
import com.mifos.room.entities.group.GroupWithAssociations
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class GroupListRepositoryImp(
    private val dataManager: DataManager,
) : GroupListRepository {

    override fun getGroups(groupId: Int): Flow<DataState<GroupWithAssociations>> {
        return dataManager.getGroups(groupId).asDataStateFlow()
    }

    override fun getGroupsByCenter(id: Int): Flow<DataState<CenterWithAssociations>> {
        return dataManager.getGroupsByCenter(id).asDataStateFlow()
    }
}
