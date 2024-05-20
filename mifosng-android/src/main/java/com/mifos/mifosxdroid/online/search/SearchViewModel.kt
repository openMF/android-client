package com.mifos.mifosxdroid.online.search


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(private val searchUseCase: SearchUseCase) : ViewModel() {

    private val _searchUiState = MutableStateFlow(SearchUiState())

    val searchUiState: StateFlow<SearchUiState>
        get() = _searchUiState.asStateFlow()

    fun searchResources(query: String?, resources: String?, exactMatch: Boolean?) {
        searchUseCase(query, resources, exactMatch).onEach {
            when (it) {
                is Resource.Loading -> {
                    _searchUiState.value = _searchUiState.value.copy(isLoading = true)
                }

                is Resource.Success -> {
                    _searchUiState.value = SearchUiState(searchedEntities = it.data!!)
                }

                is Resource.Error -> {
                    _searchUiState.value = _searchUiState.value.copy(isLoading = false, error = it.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun showError(error: String) {
        _searchUiState.value = _searchUiState.value.copy(error = error)
    }

    fun dismissDialog() {
        _searchUiState.value = _searchUiState.value.copy(isLoading = false)
    }

    fun resetErrorMessage() {
        _searchUiState.value = _searchUiState.value.copy(error = null)
    }
}
