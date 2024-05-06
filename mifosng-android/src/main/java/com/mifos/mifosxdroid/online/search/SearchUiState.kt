package com.mifos.mifosxdroid.online.search

import com.mifos.core.objects.SearchedEntity

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class SearchUiState {

    data class ShowProgress(val state: Boolean) : SearchUiState()

    data class ShowError(val message: String) : SearchUiState()

    data class ShowSearchedResources(val searchedEntities: List<SearchedEntity>) : SearchUiState()

    data object ShowNoResultFound : SearchUiState()

}
