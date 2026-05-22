/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.collectionsheet.api

import com.mifos.core.model.GenericResponse
import com.mifos.core.model.network.IndividualCollectionSheetPayload
import com.mifos.core.model.network.RequestCollectionSheetPayload
import com.mifos.core.model.objects.collectionsheet.CenterDetail
import com.mifos.core.model.objects.collectionsheet.CollectionSheetPayload
import com.mifos.core.model.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.model.objects.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.core.model.objects.collectionsheets.CollectionSheetRequestPayload
import com.mifos.core.network.APIEndPoint
import com.mifos.room.center.entity.CenterWithAssociations
import com.mifos.room.collectionsheet.entity.CollectionSheetResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

/** Fineract `collection-sheet` domain endpoints — individual + productive collection sheets. */
interface CollectionSheetApi {

    @POST(APIEndPoint.COLLECTION_SHEET + "?command=generateCollectionSheet")
    suspend fun getIndividualCollectionSheet(
        @Body payload: RequestCollectionSheetPayload?,
    ): IndividualCollectionSheet

    @POST(APIEndPoint.COLLECTION_SHEET + "?command=saveCollectionSheet")
    suspend fun saveIndividualCollectionSheet(
        @Body payload: IndividualCollectionSheetPayload?,
    ): GenericResponse

    @GET(APIEndPoint.CENTERS)
    suspend fun fetchCenterDetails(
        @Query("dateFormat") format: String?,
        @Query("locale") locale: String?,
        @Query("meetingDate") meetingDate: String?,
        @Query("officeId") officeId: Int,
        @Query("staffId") staffId: Int,
    ): List<CenterDetail>

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=generateCollectionSheet")
    suspend fun fetchProductiveSheet(
        @Path("centerId") centerId: Int,
        @Body payload: CollectionSheetRequestPayload?,
    ): CollectionSheetResponse

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
    suspend fun submitProductiveSheet(
        @Path("centerId") centerId: Int,
        @Body payload: ProductiveCollectionSheetPayload?,
    ): GenericResponse

    @POST(APIEndPoint.GROUPS + "/{groupId}?command=generateCollectionSheet")
    suspend fun fetchCollectionSheet(
        @Path("groupId") groupId: Int,
        @Body payload: CollectionSheetRequestPayload?,
    ): CollectionSheetResponse

    @POST(APIEndPoint.GROUPS + "/{groupId}?command=saveCollectionSheet")
    suspend fun submitCollectionSheet(
        @Path("groupId") groupId: Int,
        @Body payload: CollectionSheetPayload?,
    ): GenericResponse

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers,collectionMeetingCalendar")
    suspend fun fetchGroupsAssociatedWithCenter(
        @Path("centerId") centerId: Int,
    ): CenterWithAssociations
}
