/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.model.objects.SearchedEntity
import com.mifos.core.network.BaseApiManager
import kotlinx.coroutines.flow.Flow

/**
 * Created by Rajan Maurya on 12/12/16.
 */
class DataManagerSearch(
    private val baseApiManager: BaseApiManager,
) {
    fun searchResources(
        query: String,
        resource: String?,
        exactMatch: Boolean?,
    ): Flow<List<SearchedEntity>> {
        return baseApiManager.searchService.searchResources(query, resource, exactMatch)
    }
}
