/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.search.api

import com.mifos.core.model.objects.SearchedEntity
import com.mifos.core.network.APIEndPoint
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

/**
 * Fineract `search` endpoint. Query-driven, no caching — each call returns fresh results.
 */
interface SearchApi {

    @GET(APIEndPoint.SEARCH)
    suspend fun searchResources(
        @Query("query") query: String,
        @Query("resource") resource: String?,
        @Query("exactMatch") exactMatch: Boolean?,
    ): List<SearchedEntity>
}
