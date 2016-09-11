/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.objects.group.GroupPayload;

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
public interface GroupService {

    @GET(APIEndPoint.GROUPS)
    Observable<Page<Group>> getGroups(@Query("paged") boolean b,
                                      @Query("offset") int offset,
                                      @Query("limit") int limit);

    @GET(APIEndPoint.GROUPS + "/{groupId}?associations=all")
    Observable<GroupWithAssociations> getGroupWithAssociations(@Path("groupId") int groupId);

    @GET(APIEndPoint.GROUPS)
    Observable<List<Group>> getAllGroupsInOffice(@Query("officeId") int officeId,
                                                 @QueryMap Map<String, Object> params);

    @POST(APIEndPoint.GROUPS)
    Observable<Group> createGroup(@Body GroupPayload groupPayload);

    @GET(APIEndPoint.GROUPS + "/{groupId}")
    Observable<Group> getGroup(@Path("groupId") int groupId);

    @GET(APIEndPoint.GROUPS + "/{groupId}/accounts")
    Observable<GroupAccounts> getGroupAccounts(@Path("groupId") int groupId);

}
