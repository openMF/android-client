package com.mifos.mifosxdroid.online.search

import com.mifos.objects.SearchedEntity

data class SearchUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchedEntities: List<SearchedEntity> = emptyList()
)