package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.accounts.GroupAccounts;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by nellyk on 3/20/2016.
 */

public interface GroupAccountService {

    @GET(APIEndPoint.GROUPS + "/{groupId}/accounts")
    void getAllGroupsOfClient(@Path("groupId") int groupId, Callback<GroupAccounts> groupAccountsCallback);

}
