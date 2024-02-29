/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services

import com.mifos.api.model.APIEndPoint
import com.mifos.objects.SearchedEntity
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * @author fomenkoo
 */
interface SearchService {
    @GET(APIEndPoint.SEARCH)
    suspend fun searchResources(
        @Query("query") clientName: String?,
        @Query("resource") resources: String?,
        @Query("exactMatch") exactMatch: Boolean?
    ): List<SearchedEntity>
}