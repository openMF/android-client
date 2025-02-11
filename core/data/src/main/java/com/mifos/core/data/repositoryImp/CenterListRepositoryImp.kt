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

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mifos.core.data.pagingSource.CenterListPagingSource
import com.mifos.core.data.repository.CenterListRepository
import com.mifos.core.model.objects.clients.Page
import com.mifos.core.network.datamanager.DataManagerCenter
import com.mifos.room.entities.group.Center
import com.mifos.room.entities.group.CenterWithAssociations
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class CenterListRepositoryImp @Inject constructor(private val dataManagerCenter: DataManagerCenter) :
    CenterListRepository {

    override fun getAllCenters(): Flow<PagingData<Center>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            pagingSourceFactory = {
                CenterListPagingSource(dataManagerCenter)
            },
        ).flow
    }

    override suspend fun getCentersGroupAndMeeting(id: Int): CenterWithAssociations {
        return dataManagerCenter.getCentersGroupAndMeeting(id)
    }

    override fun allDatabaseCenters(): Flow<Page<Center>> {
        return dataManagerCenter.allDatabaseCenters
    }
}
