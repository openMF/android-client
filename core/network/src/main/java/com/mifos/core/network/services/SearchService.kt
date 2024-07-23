/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.APIEndPoint
import com.mifos.core.objects.SearchedEntity
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author fomenkoo
 */
interface SearchService {

    @GET(APIEndPoint.SEARCH)
    suspend fun searchResources(
        @Query("query") query: String,
        @Query("resource") resource: String?,
        @Query("exactMatch") exactMatch: Boolean?
    ): List<SearchedEntity>
}