/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.objects.SearchedEntity
import com.mifos.room.basemodel.APIEndPoint
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
        @Query("exactMatch") exactMatch: Boolean?,
    ): List<SearchedEntity>
}
