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

import com.mifos.core.data.repository.PathTrackingRepository
import com.mifos.core.`object`.users.UserLocation
import com.mifos.core.network.datamanager.DataManagerDataTable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class PathTrackingRepositoryImp @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) :
    PathTrackingRepository {

    override suspend fun getUserPathTracking(userId: Int): List<UserLocation> {
        return dataManagerDataTable.getUserPathTracking(userId)
    }
}
