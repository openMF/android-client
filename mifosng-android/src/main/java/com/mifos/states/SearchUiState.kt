package com.mifos.states

import com.mifos.objects.SearchedEntity

/**
 * Created by Aditya Gupta on 06/08/23.
 */
sealed class SearchUiState {

    data class ShowProgress(val state: Boolean) : SearchUiState()

    data class ShowError(val message: String) : SearchUiState()

    data class ShowSearchedResources(val searchedEntities: List<SearchedEntity>) : SearchUiState()

    object ShowNoResultFound : SearchUiState()

}
