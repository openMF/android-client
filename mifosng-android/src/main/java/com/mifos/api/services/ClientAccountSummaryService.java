package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.AccountSummary;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Tarun on 12/03/2016.
 */
public interface ClientAccountSummaryService {
    @GET(APIEndPoint.RUNREPORT + "/ClientSummary")
    void getSummary(@Query("R_clientId") int clientId, @Query("genericResultSet") boolean genericSet,
                    Callback<List<AccountSummary>> clientAccountsCallback);
}
