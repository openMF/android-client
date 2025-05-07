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

import com.mifos.core.common.utils.Page
import com.mifos.core.model.objects.clients.ActivatePayload
import com.mifos.core.model.objects.databaseobjects.CollectionSheet
import com.mifos.core.model.objects.databaseobjects.OfflineCenter
import com.mifos.core.model.objects.responses.SaveResponse
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.CollectionSheetPayload
import com.mifos.core.network.model.Payload
import com.mifos.room.basemodel.APIEndPoint
import com.mifos.room.entities.accounts.CenterAccounts
import com.mifos.room.entities.center.CenterPayloadEntity
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.CenterWithAssociations
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import kotlinx.coroutines.flow.Flow

/**
 * @author fomenkoo
 */
interface CenterService {
    @GET(APIEndPoint.CENTERS)
    fun getCenters(
        @Query("paged") b: Boolean,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Flow<Page<CenterEntity>>

    @GET(APIEndPoint.CENTERS + "/{centerId}/accounts")
    suspend fun getCenterAccounts(@Path("centerId") centerId: Int): CenterAccounts

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers,collectionMeetingCalendar")
    suspend fun getCenterWithGroupMembersAndCollectionMeetingCalendar(
        @Path("centerId") centerId: Int,
    ): CenterWithAssociations

    @GET(APIEndPoint.CENTERS)
    fun getAllCentersInOffice(
        @Query("officeId") officeId: Int,
        @QueryMap additionalParams: Map<String, String>,
    ): Flow<List<CenterEntity>>

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers")
    fun getAllGroupsForCenter(@Path("centerId") centerId: Int): Flow<CenterWithAssociations>

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=generateCollectionSheet")
    fun getCollectionSheet(
        @Path("centerId") centerId: Long,
        @Body payload: Payload?,
    ): Flow<CollectionSheet>

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
    fun saveCollectionSheet(
        @Path("centerId") centerId: Int,
        @Body collectionSheetPayload: CollectionSheetPayload?,
    ): Flow<SaveResponse>

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
    fun saveCollectionSheetAsync(
        @Path("centerId") centerId: Int,
        @Body collectionSheetPayload: CollectionSheetPayload?,
    ): Flow<SaveResponse>

    /*@POST(APIEndPoint.CLIENTS + "")
    void uploadNewClientDetails();*/
    @POST(APIEndPoint.CENTERS)
    suspend fun createCenter(@Body centerPayload: CenterPayloadEntity?): SaveResponse

    @GET(APIEndPoint.CENTERS)
    fun getCenterList(
        @Query("dateFormat") dateFormat: String?,
        @Query("locale") locale: String?,
        @Query("meetingDate") meetingDate: String?,
        @Query("officeId") officeId: Int,
        @Query("staffId") staffId: Int,
    ): Flow<List<OfflineCenter>>

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
    ): Flow<GenericResponse>
}
