/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.search.impl

import com.mifos.core.data.search.SearchRepository
import com.mifos.core.model.objects.SearchedEntity
import com.mifos.core.network.search.api.SearchApi

class SearchRepositoryImpl(
    private val searchApi: SearchApi,
) : SearchRepository {

    override suspend fun searchResources(
        query: String,
        resources: String?,
        exactMatch: Boolean?,
    ): List<SearchedEntity> = searchApi.searchResources(query, resources, exactMatch)
}
