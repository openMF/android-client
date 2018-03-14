/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;
import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.Payload;
import com.mifos.objects.accounts.CenterAccounts;
import com.mifos.objects.client.ActivatePayload;
import com.mifos.objects.response.SaveResponse;
import com.mifos.objects.client.Page;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.db.OfflineCenter;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.services.data.CenterPayload;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface CenterService {


    @GET(APIEndPoint.CENTERS)
    Observable<Page<Center>> getCenters(@Query("paged") boolean b,
                                        @Query("offset") int offset,
                                        @Query("limit") int limit);

    @GET(APIEndPoint.CENTERS + "/{centerId}/accounts")
    Observable<CenterAccounts> getCenterAccounts(@Path("centerId") int centerId);

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers,collectionMeetingCalendar")
    Observable<CenterWithAssociations> getCenterWithGroupMembersAndCollectionMeetingCalendar
            (@Path("centerId") int centerId);

    @GET(APIEndPoint.CENTERS)
    Observable<List<Center>> getAllCentersInOffice(@Query("officeId") int officeId,
                                                   @QueryMap Map<String, Object> additionalParams);


    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers")
    Observable<CenterWithAssociations> getAllGroupsForCenter(@Path("centerId") int centerId);

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=generateCollectionSheet")
    Observable<CollectionSheet> getCollectionSheet(@Path("centerId") long centerId,
                                                   @Body Payload payload);

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
    Observable<SaveResponse> saveCollectionSheet(
            @Path("centerId") int centerId,
            @Body CollectionSheetPayload collectionSheetPayload);

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
    Observable<SaveResponse> saveCollectionSheetAsync(
            @Path("centerId") int centerId,
            @Body CollectionSheetPayload collectionSheetPayload);


    /*@POST(APIEndPoint.CLIENTS + "")
    void uploadNewClientDetails();*/

    @POST(APIEndPoint.CENTERS)
    Observable<SaveResponse> createCenter(@Body CenterPayload centerPayload);

    @GET(APIEndPoint.CENTERS)
    Observable<List<OfflineCenter>> getCenterList(
            @Query("dateFormat") String dateFormat,
            @Query("locale") String locale,
            @Query("meetingDate") String meetingDate,
            @Query("officeId") int officeId,
            @Query("staffId") int staffId);


    /**
     * This is the service to activate the center
     * REST ENT POINT
     * https://demo.openmf.org/fineract-provider/api/v1/centers/{centerId}?command=activate
     *
     * @param centerId
     * @return GenericResponse
     */
    @POST(APIEndPoint.CENTERS + "/{centerId}?command=activate")
    Observable<GenericResponse> activateCenter(@Path("centerId") int centerId,
                                               @Body ActivatePayload activatePayload);
}
