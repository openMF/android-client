package com.mifos.feature.center.center_list.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.CenterListRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.domain.use_cases.GetCenterListDbUseCase
import com.mifos.feature.center.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CenterListViewModel @Inject constructor(
    private val prefManager: PrefManager,
    private val repository: CenterListRepository,
    private val getCenterListDbUseCase: GetCenterListDbUseCase
) : ViewModel() {

    // for refresh feature
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshCenterList() {
        _isRefreshing.value = true
        getCenterList()
        _isRefreshing.value = false
    }

    private val _centerListUiState = MutableStateFlow<CenterListUiState>(CenterListUiState.Loading)
    val centerListUiState = _centerListUiState.asStateFlow()

    fun getCenterList() {
        if (prefManager.userStatus) loadCentersFromDb()
        else loadCentersFromApi()
    }

    private fun loadCentersFromApi() = viewModelScope.launch(Dispatchers.IO) {
        val response = repository.getAllCenters()
        _centerListUiState.value = CenterListUiState.CenterList(response)
    }

    private fun loadCentersFromDb() = viewModelScope.launch(Dispatchers.IO) {
        getCenterListDbUseCase().collect { result ->
            when (result) {
                is Resource.Error -> _centerListUiState.value =
                    CenterListUiState.Error(R.string.feature_center_failed_to_load_db_centers)

                is Resource.Loading -> _centerListUiState.value = CenterListUiState.Loading

                is Resource.Success -> _centerListUiState.value =
                    CenterListUiState.CenterListDb(result.data ?: emptyList())
            }
        }
    }
}
