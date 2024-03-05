package com.mifos.core.network.services

import com.mifos.core.model.APIEndPoint
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.network.model.RequestCollectionSheetPayload
import com.mifos.core.objects.collectionsheet.CenterDetail
import com.mifos.core.objects.collectionsheet.CollectionSheetPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetRequestPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetResponse
import com.mifos.core.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.core.objects.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.core.objects.group.CenterWithAssociations
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

/**
 * Created by Tarun on 06-07-2017.
 */
interface CollectionSheetService {
    @POST(APIEndPoint.COLLECTION_SHEET + "?command=generateCollectionSheet")
    fun getIndividualCollectionSheet(
        @Body payload: RequestCollectionSheetPayload?
    ): Observable<IndividualCollectionSheet>

    @POST(APIEndPoint.COLLECTION_SHEET + "?command=saveCollectionSheet")
    fun saveindividualCollectionSheet(
        @Body payload: IndividualCollectionSheetPayload?
    ): Observable<GenericResponse>
    //Productive CollectionSheet Endpoints
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
    fun fetchCenterDetails(
        @Query("dateFormat") format: String?,
        @Query("locale") locale: String?,
        @Query("meetingDate") meetingDate: String?,
        @Query("officeId") officeId: Int,
        @Query("staffId") staffId: Int
    ): Observable<List<CenterDetail>>

    /**
     * Request Endpoint to fetch Productive CollectionSheet
     * @param centerId Center Id
     * @param payload CollectionSheetRequestPayload
     * @return CollectionSheetResponse
     */
    @POST(APIEndPoint.CENTERS + "/{centerId}" + "?command=generateCollectionSheet")
    fun fetchProductiveSheet(
        @Path("centerId") centerId: Int,
        @Body payload: CollectionSheetRequestPayload?
    ): Observable<CollectionSheetResponse>

    @POST(APIEndPoint.CENTERS + "/{centerId}" + "?command=saveCollectionSheet")
    fun submitProductiveSheet(
        @Path("centerId") centerId: Int,
        @Body payload: ProductiveCollectionSheetPayload?
    ): Observable<GenericResponse>

    @POST(APIEndPoint.GROUPS + "/{groupId}" + "?command=generateCollectionSheet")
    fun fetchCollectionSheet(
        @Path("groupId") groupId: Int,
        @Body payload: CollectionSheetRequestPayload?
    ): Observable<CollectionSheetResponse>

    @POST(APIEndPoint.GROUPS + "/{groupId}" + "?command=saveCollectionSheet")
    fun submitCollectionSheet(
        @Path("groupId") groupId: Int, @Body payload: CollectionSheetPayload?
    ): Observable<GenericResponse>

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers, collectionMeetingCalendar")
    fun fetchGroupsAssociatedWithCenter(
        @Path("centerId") centerId: Int
    ): Observable<CenterWithAssociations>
}