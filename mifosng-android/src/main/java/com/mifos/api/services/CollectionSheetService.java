package com.mifos.api.services;

import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;
import com.mifos.api.model.IndividualCollectionSheetPayload;
import com.mifos.api.model.RequestCollectionSheetPayload;
import com.mifos.objects.collectionsheet.CollectionSheetPayload;
import com.mifos.objects.collectionsheet.IndividualCollectionSheet;
import com.mifos.objects.collectionsheet.CenterDetail;
import com.mifos.objects.collectionsheet.CollectionSheetResponse;
import com.mifos.objects.collectionsheet.CollectionSheetRequestPayload;
import com.mifos.objects.collectionsheet.ProductiveCollectionSheetPayload;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Tarun on 06-07-2017.
 */

public interface CollectionSheetService {


    @POST(APIEndPoint.COLLECTIONSHEET + "?command=generateCollectionSheet")
    Observable<IndividualCollectionSheet> getIndividualCollectionSheet(
            @Body RequestCollectionSheetPayload payload);

    @POST(APIEndPoint.COLLECTIONSHEET + "?command=saveCollectionSheet")
    Observable<GenericResponse> saveindividualCollectionSheet(
            @Body IndividualCollectionSheetPayload payload);


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
    Observable<List<CenterDetail>> fetchCenterDetails(
            @Query("dateFormat") String format,
            @Query("locale") String locale,
            @Query("meetingDate") String meetingDate,
            @Query("officeId") int officeId,
            @Query("staffId") int staffId
    );

    /**
     * Request Endpoint to fetch Productive CollectionSheet
     * @param centerId Center Id
     * @param payload CollectionSheetRequestPayload
     * @return CollectionSheetResponse
     */
    @POST(APIEndPoint.CENTERS + "/{centerId}" + "?command=generateCollectionSheet")
    Observable<CollectionSheetResponse> fetchProductiveSheet(
            @Path("centerId") int centerId,
            @Body CollectionSheetRequestPayload payload
    );

    @POST(APIEndPoint.CENTERS + "/{centerId}" + "?command=saveCollectionSheet")
    Observable<GenericResponse> submitProductiveSheet(
            @Path("centerId") int centerId,
            @Body ProductiveCollectionSheetPayload payload
    );

    @POST(APIEndPoint.GROUPS + "/{groupId}" + "?command=generateCollectionSheet")
    Observable<CollectionSheetResponse> fetchCollectionSheet(
            @Path("groupId") int groupId,
            @Body CollectionSheetRequestPayload payload
    );

    @POST(APIEndPoint.GROUPS + "/{groupId}" + "?command=saveCollectionSheet")
    Observable<GenericResponse> submitCollectionSheet(
            @Path("groupId") int groupId, @Body CollectionSheetPayload payload
    );

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers, collectionMeetingCalendar")
    Observable<CenterWithAssociations> fetchGroupsAssociatedWithCenter(
            @Path("centerId") int centerId);
}