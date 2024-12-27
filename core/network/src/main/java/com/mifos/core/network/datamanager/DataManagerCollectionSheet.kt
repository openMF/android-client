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

import com.mifos.core.modelobjects.collectionsheets.CollectionSheetRequestPayload
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.CenterDetail
import com.mifos.core.objects.collectionsheet.CollectionSheetPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetResponse
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.core.objects.group.CenterWithAssociations
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Tarun on 22-07-2017.
 */
@Singleton
class DataManagerCollectionSheet @Inject constructor(
    private val mBaseApiManager: BaseApiManager,
) {
    /**
     * Individual CollectionSheet API
     */
    suspend fun getIndividualCollectionSheet(
        payload: RequestCollectionSheetPayload?,
    ): IndividualCollectionSheet {
        return mBaseApiManager.collectionSheetApi.getIndividualCollectionSheet(payload)
    }

    suspend fun saveIndividualCollectionSheet(
        payload: IndividualCollectionSheetPayload?,
    ): GenericResponse {
        return mBaseApiManager.collectionSheetApi.saveindividualCollectionSheet(payload)
    }

    /**
     * Productive CollectionSheet API
     */
    suspend fun fetchCenterDetails(
        format: String?,
        locale: String?,
        meetingDate: String?,
        officeId: Int,
        staffId: Int,
    ): List<CenterDetail> {
        return mBaseApiManager.collectionSheetApi.fetchCenterDetails(
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
        return mBaseApiManager.collectionSheetApi.fetchProductiveSheet(centerId, payload)
    }

    suspend fun submitProductiveSheet(
        centerId: Int,
        payload: ProductiveCollectionSheetPayload?,
    ): GenericResponse {
        return mBaseApiManager.collectionSheetApi.submitProductiveSheet(centerId, payload)
    }

    /**
     * CollectionSheet API
     */
    suspend fun fetchCollectionSheet(
        groupId: Int,
        payload: CollectionSheetRequestPayload?,
    ): CollectionSheetResponse {
        return mBaseApiManager.collectionSheetApi.fetchCollectionSheet(groupId, payload)
    }

    suspend fun submitCollectionSheet(
        groupId: Int,
        payload: CollectionSheetPayload?,
    ): GenericResponse {
        return mBaseApiManager.collectionSheetApi.submitCollectionSheet(groupId, payload)
    }

    /**
     * Associated groups API
     */
    suspend fun fetchGroupsAssociatedWithCenter(centerId: Int): CenterWithAssociations {
        return mBaseApiManager.collectionSheetApi.fetchGroupsAssociatedWithCenter(centerId)
    }
}
