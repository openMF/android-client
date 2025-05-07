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
import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.network.DataManager
import com.mifos.core.network.datamanager.DataManagerCollectionSheet
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.StaffEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class NewIndividualCollectionSheetRepositoryImp(
    private val dataManager: DataManager,
    private val dataManagerCollection: DataManagerCollectionSheet,
) : NewIndividualCollectionSheetRepository {

    override suspend fun getIndividualCollectionSheet(payload: RequestCollectionSheetPayload?): IndividualCollectionSheet {
        return dataManagerCollection.getIndividualCollectionSheet(payload)
    }

    override fun offices(): Flow<DataState<List<OfficeEntity>>> {
        return dataManager.offices().asDataStateFlow()
    }

    override fun getStaffInOffice(officeId: Int): Flow<DataState<List<StaffEntity>>> {
        return dataManager.getStaffInOffice(officeId)
            .asDataStateFlow()
    }
}
