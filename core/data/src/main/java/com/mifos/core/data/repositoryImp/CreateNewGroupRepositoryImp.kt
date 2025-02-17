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

import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.core.entity.organisation.Office
import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.network.datamanager.DataManagerOffices
import com.mifos.room.entities.group.GroupPayload
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CreateNewGroupRepositoryImp @Inject constructor(
    private val dataManagerOffices: DataManagerOffices,
    private val dataManagerGroups: DataManagerGroups,
) : CreateNewGroupRepository {

    override suspend fun offices(): List<Office> {
        return dataManagerOffices.offices()
    }

    override suspend fun createGroup(groupPayload: GroupPayload): SaveResponse {
        return dataManagerGroups.createGroup(groupPayload)
    }
}
