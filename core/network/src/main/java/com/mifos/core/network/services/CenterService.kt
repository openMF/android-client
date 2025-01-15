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

import com.mifos.core.entity.center.CenterPayload
import com.mifos.core.entity.group.Center
import com.mifos.core.entity.group.CenterWithAssociations
import com.mifos.core.model.APIEndPoint
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.CollectionSheetPayload
import com.mifos.core.network.model.Payload
import com.mifos.core.objects.clients.ActivatePayload
import com.mifos.core.objects.clients.Page
import com.mifos.core.objects.databaseobjects.CollectionSheet
import com.mifos.core.objects.databaseobjects.OfflineCenter
import com.mifos.core.objects.responses.SaveResponse
import com.mifos.room.entities.accounts.CenterAccounts
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import rx.Observable

/**
 * @author fomenkoo
 */
interface CenterService {
    @GET(APIEndPoint.CENTERS)
    fun getCenters(
        @Query("paged") b: Boolean,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Observable<Page<Center>>

    @GET(APIEndPoint.CENTERS + "/{centerId}/accounts")
    fun getCenterAccounts(@Path("centerId") centerId: Int): Observable<CenterAccounts>

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers,collectionMeetingCalendar")
    suspend fun getCenterWithGroupMembersAndCollectionMeetingCalendar(@Path("centerId") centerId: Int): CenterWithAssociations

    @GET(APIEndPoint.CENTERS)
    suspend fun getAllCentersInOffice(
        @Query("officeId") officeId: Int,
        @QueryMap additionalParams: Map<String, String>,
    ): List<Center>

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers")
    fun getAllGroupsForCenter(@Path("centerId") centerId: Int): Observable<CenterWithAssociations>

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=generateCollectionSheet")
    fun getCollectionSheet(
        @Path("centerId") centerId: Long,
        @Body payload: Payload?,
    ): Observable<CollectionSheet>

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
    fun saveCollectionSheet(
        @Path("centerId") centerId: Int,
        @Body collectionSheetPayload: CollectionSheetPayload?,
    ): Observable<SaveResponse>

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
    fun saveCollectionSheetAsync(
        @Path("centerId") centerId: Int,
        @Body collectionSheetPayload: CollectionSheetPayload?,
    ): Observable<SaveResponse>

    /*@POST(APIEndPoint.CLIENTS + "")
    void uploadNewClientDetails();*/
    @POST(APIEndPoint.CENTERS)
    fun createCenter(@Body centerPayload: CenterPayload?): Observable<SaveResponse>

    @GET(APIEndPoint.CENTERS)
    fun getCenterList(
        @Query("dateFormat") dateFormat: String?,
        @Query("locale") locale: String?,
        @Query("meetingDate") meetingDate: String?,
        @Query("officeId") officeId: Int,
        @Query("staffId") staffId: Int,
    ): Observable<List<OfflineCenter>>

    /**
     * This is the service to activate the center
     * REST ENT POINT
     * https://demo.openmf.org/fineract-provider/api/v1/centers/{centerId}?command=activate
     *
     * @param centerId
     * @return GenericResponse
     */
    @POST(APIEndPoint.CENTERS + "/{centerId}?command=activate")
    fun activateCenter(
        @Path("centerId") centerId: Int,
        @Body activatePayload: ActivatePayload?,
    ): Observable<GenericResponse>
}
