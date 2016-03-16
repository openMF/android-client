/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.Payload;
import com.mifos.api.model.SaveResponse;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.db.OfflineCenter;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.services.data.CenterPayload;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface CenterService {


    @GET(APIEndPoint.CENTERS)
    Observable<List<Center>> getAllCenters();

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers,collectionMeetingCalendar")
    Observable<CenterWithAssociations> getCenterWithGroupMembersAndCollectionMeetingCalendar(@Path("centerId") int centerId);

    @GET(APIEndPoint.CENTERS)
    void getAllCentersInOffice(@Query("officeId") int officeId, @QueryMap Map<String, Object> additionalParams,
                               Callback<List<Center>> centersCallback);

    @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers")
    void getAllGroupsForCenter(@Path("centerId") int centerId,
                               Callback<CenterWithAssociations> centerWithAssociationsCallback);

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=generateCollectionSheet")
    void getCollectionSheet(@Path("centerId") long centerId, @Body Payload payload, Callback<CollectionSheet> callback);

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
    SaveResponse saveCollectionSheet(@Path("centerId") int centerId, @Body CollectionSheetPayload collectionSheetPayload);

    @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
    void saveCollectionSheet(@Path("centerId") int centerId, @Body CollectionSheetPayload collectionSheetPayload, Callback<SaveResponse> saveResponseCallback);


    @POST(APIEndPoint.CLIENTS + "")
    void uploadNewClientDetails();

    @POST(APIEndPoint.CENTERS)
    void createCenter(@Body CenterPayload centerPayload, Callback<Center> callback);

    @GET(APIEndPoint.CENTERS)
    void getCenterList(@Query("dateFormat") String dateFormat, @Query("locale") String locale,
                       @Query("meetingDate") String meetingDate, @Query("officeId") int officeId,
                       @Query("staffId") int staffId, Callback<List<OfflineCenter>> callback);
}
