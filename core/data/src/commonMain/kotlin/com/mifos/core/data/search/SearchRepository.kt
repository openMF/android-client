/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.search

import com.mifos.core.model.objects.SearchedEntity

interface SearchRepository {

    /**
     * Query-driven search. Each call returns fresh results (no caching, no Store —
     * search results are ephemeral). Throws on transport / HTTP failure.
     */
    suspend fun searchResources(
        query: String,
        resources: String?,
        exactMatch: Boolean?,
    ): List<SearchedEntity>
}
