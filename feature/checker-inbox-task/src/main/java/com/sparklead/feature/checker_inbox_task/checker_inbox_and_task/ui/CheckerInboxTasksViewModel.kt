package com.sparklead.feature.checker_inbox_task.checker_inbox_and_task.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.sparklead.feature.checker_inbox_task.checker_inbox_and_task.domain.usecase.GetCheckerInboxBadgesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 21/03/24.
 */

@HiltViewModel
class CheckerInboxTasksViewModel @Inject constructor(
    private val getCheckerInboxBadgesUseCase: GetCheckerInboxBadgesUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _checkerInboxTasksUiState =
        MutableStateFlow<CheckerInboxTasksUiState>(CheckerInboxTasksUiState.Loading)
    val checkerInboxTasksUiState = _checkerInboxTasksUiState.asStateFlow()

    fun onRefresh() {
        _isRefreshing.value = true
        loadCheckerTasksBadges()
        _isRefreshing.value = false
    }

    fun loadCheckerTasksBadges() = viewModelScope.launch(Dispatchers.IO) {
        getCheckerInboxBadgesUseCase().collect { result ->

            when (result) {
                is Resource.Error -> {
                    _checkerInboxTasksUiState.value =
                        CheckerInboxTasksUiState.Error(result.message.toString())
                }

                is Resource.Loading -> {
                    _checkerInboxTasksUiState.value = CheckerInboxTasksUiState.Loading
                }

                is Resource.Success -> {
                    _checkerInboxTasksUiState.value = CheckerInboxTasksUiState.Success(
                        result.data?.first.toString(),
                        result.data?.second.toString()
                    )
                }
            }
        }
    }
}