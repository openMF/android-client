/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.model.objects.collectionsheets.CollectionSheetRequestPayload
import com.mifos.core.network.GenericResponse
import com.mifos.room.entities.collectionsheet.CenterDetail
import com.mifos.room.entities.collectionsheet.CollectionSheetPayload
import com.mifos.room.entities.collectionsheet.CollectionSheetResponse
import com.mifos.room.entities.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.CenterWithAssociations
import com.mifos.room.entities.group.GroupEntity

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface GenerateCollectionSheetRepository {

    suspend fun getCentersInOffice(id: Int, params: Map<String, String>): List<CenterEntity>

    suspend fun getGroupsByOffice(
        office: Int,
        params: Map<String, String>,
    ): List<GroupEntity>

    suspend fun fetchGroupsAssociatedWithCenter(centerId: Int): CenterWithAssociations

    suspend fun fetchCenterDetails(
        format: String?,
        locale: String?,
        meetingDate: String?,
        officeId: Int,
        staffId: Int,
    ): List<CenterDetail>

    suspend fun fetchProductiveCollectionSheet(
        centerId: Int,
        payload: CollectionSheetRequestPayload?,
    ): CollectionSheetResponse

    suspend fun fetchCollectionSheet(
        groupId: Int,
        payload: CollectionSheetRequestPayload?,
    ): CollectionSheetResponse

    suspend fun submitProductiveSheet(
        centerId: Int,
        payload: ProductiveCollectionSheetPayload?,
    ): GenericResponse

    suspend fun submitCollectionSheet(
        groupId: Int,
        payload: CollectionSheetPayload?,
    ): GenericResponse
}
