/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.APIEndPoint
import com.mifos.core.objects.SearchedEntity
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * @author fomenkoo
 */
interface SearchService {
    @GET(APIEndPoint.SEARCH)
    fun searchResources(
        @Query("query") clientName: String?,
        @Query("resource") resources: String?,
        @Query("exactMatch") exactMatch: Boolean?
    ): Observable<List<SearchedEntity>>
}