/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.testing.repository

import com.mifos.core.data.repository.SearchRepository
import com.mifos.core.modelobjects.SearchedEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestSearchRepository : SearchRepository {

    private val sampleResults = MutableStateFlow(emptyList<SearchedEntity>())

    override suspend fun searchResources(
        query: String,
        resources: String?,
        exactMatch: Boolean?,
    ): Flow<List<SearchedEntity>> {
        return sampleResults.map { list ->
            when {
                query.isBlank() && resources.isNullOrBlank() -> emptyList()
                else -> {
                    list.asSequence().filter { entity ->
                        (
                            resources.isNullOrBlank() || entity.entityType.equals(
                                resources,
                                ignoreCase = true,
                            )
                            ) && (
                            query.isBlank() || when {
                                exactMatch == true -> entity.entityName.equals(query, ignoreCase = true) ||
                                    entity.entityAccountNo.equals(query, ignoreCase = true) ||
                                    entity.parentName.equals(query, true)

                                else -> entity.entityName?.contains(query, ignoreCase = true) == true ||
                                    entity.entityAccountNo?.contains(query, ignoreCase = true) == true ||
                                    entity.parentName?.contains(query, true) == true
                            }
                            )
                    }.toList()
                }
            }
        }
    }

    @TestOnly
    fun addSampleResults(results: List<SearchedEntity>) {
        sampleResults.update { results }
    }
}
