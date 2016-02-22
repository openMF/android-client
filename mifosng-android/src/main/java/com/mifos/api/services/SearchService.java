package com.mifos.api.services;

import com.mifos.objects.SearchedEntity;
import com.mifos.api.model.APIEndPoint;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * @author fomenkoo
 */
public interface SearchService {

    @GET(APIEndPoint.SEARCH + "?resource=clients")
    void searchClientsByName(@Query("query") String clientName,
                             Callback<List<SearchedEntity>> listCallback);
}
