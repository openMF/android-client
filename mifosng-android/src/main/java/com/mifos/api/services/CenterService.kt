/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services

import com.mifos.api.GenericResponse
import com.mifos.api.model.APIEndPoint
import com.mifos.api.model.CollectionSheetPayload
import com.mifos.api.model.Payload
import com.mifos.objects.accounts.CenterAccounts
import com.mifos.objects.client.ActivatePayload
import com.mifos.objects.client.Page
import com.mifos.objects.db.CollectionSheet
import com.mifos.objects.db.OfflineCenter
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.response.SaveResponse
import com.mifos.services.data.CenterPayload
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
        @Query("limit") limit: Int
    ): Observable<Page<Center>>

    @GET(APIEndPoint.CENTERS + "/{centerId}/accounts")
    fun getCenterAccounts(@Path("centerId") centerId: Int): Observable<CenterAccounts>

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers,collectionMeetingCalendar")
    fun getCenterWithGroupMembersAndCollectionMeetingCalendar(@Path("centerId") centerId: Int): Observable<CenterWithAssociations>

    @GET(APIEndPoint.CENTERS)
    fun getAllCentersInOffice(
        @Query("officeId") officeId: Int,
        @QueryMap additionalParams: Map<String, String>
    ): Observable<List<Center>>

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers")
    fun getAllGroupsForCenter(@Path("centerId") centerId: Int): Observable<CenterWithAssociations>

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=generateCollectionSheet")
    fun getCollectionSheet(
        @Path("centerId") centerId: Long,
        @Body payload: Payload?
    ): Observable<CollectionSheet>

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
    fun saveCollectionSheet(
        @Path("centerId") centerId: Int,
        @Body collectionSheetPayload: CollectionSheetPayload?
    ): Observable<SaveResponse>

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
    fun saveCollectionSheetAsync(
        @Path("centerId") centerId: Int,
        @Body collectionSheetPayload: CollectionSheetPayload?
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
        @Query("staffId") staffId: Int
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
        @Body activatePayload: ActivatePayload?
    ): Observable<GenericResponse>
}