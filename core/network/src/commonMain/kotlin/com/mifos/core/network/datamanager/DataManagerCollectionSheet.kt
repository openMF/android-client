/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.model.objects.collectionsheets.CollectionSheetRequestPayload
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.room.entities.collectionsheet.CenterDetail
import com.mifos.room.entities.collectionsheet.CollectionSheetPayload
import com.mifos.room.entities.collectionsheet.CollectionSheetResponse
import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import com.mifos.room.entities.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.room.entities.group.CenterWithAssociations
import kotlinx.coroutines.flow.Flow

/**
 * Created by Tarun on 22-07-2017.
 */
class DataManagerCollectionSheet(
    private val mBaseApiManager: BaseApiManager,
) {
    /**
     * Individual CollectionSheet API
     */
    suspend fun getIndividualCollectionSheet(
        payload: RequestCollectionSheetPayload?,
    ): IndividualCollectionSheet {
        return mBaseApiManager.collectionSheetService.getIndividualCollectionSheet(payload)
    }

    suspend fun saveIndividualCollectionSheet(
        payload: IndividualCollectionSheetPayload?,
    ): GenericResponse {
        return mBaseApiManager.collectionSheetService.saveIndividualCollectionSheet(payload)
    }

    /**
     * Productive CollectionSheet API
     */
    fun fetchCenterDetails(
        format: String?,
        locale: String?,
        meetingDate: String?,
        officeId: Int,
        staffId: Int,
    ): Flow<List<CenterDetail>> {
        return mBaseApiManager.collectionSheetService.fetchCenterDetails(
            format,
            locale,
            meetingDate,
            officeId,
            staffId,
        )
    }

    suspend fun fetchProductiveCollectionSheet(
        centerId: Int,
        payload: CollectionSheetRequestPayload?,
    ): CollectionSheetResponse {
        return mBaseApiManager.collectionSheetService.fetchProductiveSheet(centerId, payload)
    }

    suspend fun submitProductiveSheet(
        centerId: Int,
        payload: ProductiveCollectionSheetPayload?,
    ): GenericResponse {
        return mBaseApiManager.collectionSheetService.submitProductiveSheet(centerId, payload)
    }

    /**
     * CollectionSheet API
     */
    suspend fun fetchCollectionSheet(
        groupId: Int,
        payload: CollectionSheetRequestPayload?,
    ): CollectionSheetResponse {
        return mBaseApiManager.collectionSheetService.fetchCollectionSheet(groupId, payload)
    }

    suspend fun submitCollectionSheet(
        groupId: Int,
        payload: CollectionSheetPayload?,
    ): GenericResponse {
        return mBaseApiManager.collectionSheetService.submitCollectionSheet(groupId, payload)
    }

    /**
     * Associated groups API
     */
    suspend fun fetchGroupsAssociatedWithCenter(centerId: Int): CenterWithAssociations {
        return mBaseApiManager.collectionSheetService.fetchGroupsAssociatedWithCenter(centerId)
    }
}
