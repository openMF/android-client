/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.common.utils.DataState
import com.mifos.core.model.GenericResponse
import com.mifos.core.model.objects.collectionsheet.CollectionSheetPayload
import com.mifos.core.model.objects.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.core.model.objects.collectionsheets.CollectionSheetRequestPayload
import com.mifos.room.center.entity.CenterEntity
import com.mifos.room.center.entity.CenterWithAssociations
import com.mifos.room.collectionsheet.entity.CenterDetail
import com.mifos.room.collectionsheet.entity.CollectionSheetResponse
import com.mifos.room.group.entity.GroupEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 12/08/23.
 */
interface GenerateCollectionSheetRepository {

    fun getCentersInOffice(id: Int, params: Map<String, String>): Flow<DataState<List<CenterEntity>>>

    fun getGroupsByOffice(
        office: Int,
        params: Map<String, String>,
    ): Flow<DataState<List<GroupEntity>>>

    suspend fun fetchGroupsAssociatedWithCenter(centerId: Int): CenterWithAssociations

    fun fetchCenterDetails(
        format: String?,
        locale: String?,
        meetingDate: String?,
        officeId: Int,
        staffId: Int,
    ): Flow<DataState<List<CenterDetail>>>

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
