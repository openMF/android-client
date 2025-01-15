/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.APIEndPoint
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.core.objects.collectionsheets.CollectionSheetRequestPayload
import com.mifos.room.entities.collectionsheet.CenterDetail
import com.mifos.room.entities.collectionsheet.CollectionSheetPayload
import com.mifos.room.entities.collectionsheet.CollectionSheetResponse
import com.mifos.room.entities.collectionsheet.IndividualCollectionSheet
import com.mifos.room.entities.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.room.entities.group.CenterWithAssociations
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Tarun on 06-07-2017.
 */
interface CollectionSheetService {
    @POST(APIEndPoint.COLLECTION_SHEET + "?command=generateCollectionSheet")
    suspend fun getIndividualCollectionSheet(
        @Body payload: RequestCollectionSheetPayload?,
    ): IndividualCollectionSheet

    @POST(APIEndPoint.COLLECTION_SHEET + "?command=saveCollectionSheet")
    suspend fun saveindividualCollectionSheet(
        @Body payload: IndividualCollectionSheetPayload?,
    ): GenericResponse
    // Productive CollectionSheet Endpoints

    /**
     * Endpoint to fetch Productive CollectionSheet
     * @param format "dd MMMM yyyy"
     * @param locale "en"
     * @param meetingDate Meeting Date for the center
     * @param officeId Office Id
     * @param staffId Staff Id
     * @return List of CenterDetail
     */
    @GET(APIEndPoint.CENTERS)
    suspend fun fetchCenterDetails(
        @Query("dateFormat") format: String?,
        @Query("locale") locale: String?,
        @Query("meetingDate") meetingDate: String?,
        @Query("officeId") officeId: Int,
        @Query("staffId") staffId: Int,
    ): List<CenterDetail>

    /**
     * Request Endpoint to fetch Productive CollectionSheet
     * @param centerId Center Id
     * @param payload CollectionSheetRequestPayload
     * @return CollectionSheetResponse
     */
    @POST(APIEndPoint.CENTERS + "/{centerId}" + "?command=generateCollectionSheet")
    suspend fun fetchProductiveSheet(
        @Path("centerId") centerId: Int,
        @Body payload: CollectionSheetRequestPayload?,
    ): CollectionSheetResponse

    @POST(APIEndPoint.CENTERS + "/{centerId}" + "?command=saveCollectionSheet")
    suspend fun submitProductiveSheet(
        @Path("centerId") centerId: Int,
        @Body payload: ProductiveCollectionSheetPayload?,
    ): GenericResponse

    @POST(APIEndPoint.GROUPS + "/{groupId}" + "?command=generateCollectionSheet")
    suspend fun fetchCollectionSheet(
        @Path("groupId") groupId: Int,
        @Body payload: CollectionSheetRequestPayload?,
    ): CollectionSheetResponse

    @POST(APIEndPoint.GROUPS + "/{groupId}" + "?command=saveCollectionSheet")
    suspend fun submitCollectionSheet(
        @Path("groupId") groupId: Int,
        @Body payload: CollectionSheetPayload?,
    ): GenericResponse

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers, collectionMeetingCalendar")
    suspend fun fetchGroupsAssociatedWithCenter(
        @Path("centerId") centerId: Int,
    ): CenterWithAssociations
}
