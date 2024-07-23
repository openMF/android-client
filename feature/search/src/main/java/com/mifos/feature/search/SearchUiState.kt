package com.mifos.feature.search

import com.mifos.core.objects.SearchedEntity

data class SearchUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchedEntities: List<SearchedEntity> = emptyList()
)