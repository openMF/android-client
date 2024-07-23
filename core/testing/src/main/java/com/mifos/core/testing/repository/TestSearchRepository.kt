package com.mifos.core.testing.repository

import com.mifos.core.data.repository.SearchRepository
import com.mifos.core.objects.SearchedEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestSearchRepository: SearchRepository {

    private val sampleResults = MutableStateFlow(emptyList<SearchedEntity>())

    override suspend fun searchResources(
        query: String?,
        resources: String?,
        exactMatch: Boolean?
    ): Flow<List<SearchedEntity>> {
        return sampleResults.map { list ->
            when {
                query.isNullOrBlank() && resources.isNullOrBlank() -> emptyList()
                else -> {
                    list.asSequence().filter { entity ->
                        (resources.isNullOrBlank() || entity.entityType.equals(resources, ignoreCase = true)) &&
                                (query.isNullOrBlank() || when {
                                    exactMatch == true -> entity.entityName.equals(query, ignoreCase = true) ||
                                            entity.entityAccountNo.equals(query, ignoreCase = true) || entity.parentName.equals(query, true)

                                    else -> entity.entityName?.contains(query, ignoreCase = true) == true ||
                                            entity.entityAccountNo?.contains(query, ignoreCase = true) == true ||
                                            entity.parentName?.contains(query, true) == true
                                })
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