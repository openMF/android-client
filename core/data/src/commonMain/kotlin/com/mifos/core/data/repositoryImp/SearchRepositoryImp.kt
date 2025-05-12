/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.SearchRepository
import com.mifos.core.model.objects.SearchedEntity
import com.mifos.core.network.datamanager.DataManagerSearch
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class SearchRepositoryImp(
    private val dataManagerSearch: DataManagerSearch,
) : SearchRepository {
    override fun searchResources(
        query: String,
        resources: String?,
        exactMatch: Boolean?,
    ): Flow<DataState<List<SearchedEntity>>> {
        return dataManagerSearch.searchResources(query, resources, exactMatch)
            .asDataStateFlow()
    }
}
