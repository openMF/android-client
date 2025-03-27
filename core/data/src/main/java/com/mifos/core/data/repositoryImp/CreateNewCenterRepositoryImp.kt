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

import com.mifos.core.data.repository.CreateNewCenterRepository
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.room.entities.center.CenterPayloadEntity

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CreateNewCenterRepositoryImp(
    private val dataManagerCenter: DataManagerCenter,
) : CreateNewCenterRepository {

    override suspend fun createCenter(centerPayload: CenterPayloadEntity) {
        dataManagerCenter.createCenter(centerPayload)
    }
}
