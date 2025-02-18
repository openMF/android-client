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

import com.mifos.core.data.repository.NewIndividualCollectionSheetRepository
import com.mifos.core.entity.organisation.Staff
import com.mifos.core.network.DataManager
import com.mifos.core.network.datamanager.DataManagerCollectionSheet
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import com.mifos.room.entities.organisation.OfficeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class NewIndividualCollectionSheetRepositoryImp @Inject constructor(
    private val dataManager: DataManager,
    private val dataManagerCollection: DataManagerCollectionSheet,
) : NewIndividualCollectionSheetRepository {

    override suspend fun getIndividualCollectionSheet(payload: RequestCollectionSheetPayload?): IndividualCollectionSheet {
        return dataManagerCollection.getIndividualCollectionSheet(payload)
    }

    override fun offices(): Flow<List<OfficeEntity>> {
        return dataManager.offices()
    }

    override suspend fun getStaffInOffice(officeId: Int): List<Staff> {
        return dataManager.getStaffInOffice(officeId)
    }
}
