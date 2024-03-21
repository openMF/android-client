package com.mifos.feature.groupsList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.feature.groupsList.domain.repository.GroupsListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val DATA_FETCHING_LIMIT = 10

@HiltViewModel
class GroupsListViewModel @Inject constructor(
    private val repository: GroupsListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GroupsListState>(GroupsListState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        getGroups()
    }

    fun refreshData() {
        _isLoading.value = true
        getGroups()
        _isLoading.value = false
    }

    private fun getGroups() {
        viewModelScope.launch {
            val data = repository.getAllGroups(DATA_FETCHING_LIMIT)

            _uiState.value = GroupsListState.ShowGroupsList(data)
        }
    }
}