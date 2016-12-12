/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.SearchedEntity;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface SearchService {

    @GET(APIEndPoint.SEARCH)
    Observable<List<SearchedEntity>> searchResources(@Query("query") String clientName,
                                                     @Query("resource") String resources,
                                                     @Query("exactMatch") Boolean exactMatch);
}
