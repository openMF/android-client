/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.checker.inbox.task.checkerInboxTasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.GetCheckerInboxBadgesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 21/03/24.
 */

class CheckerInboxTasksViewModel(
    private val getCheckerInboxBadgesUseCase: GetCheckerInboxBadgesUseCase,
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

    fun loadCheckerTasksBadges() = viewModelScope.launch {
        getCheckerInboxBadgesUseCase().collect { result ->

            when (result) {
                is DataState.Error -> {
                    Logger.e("CheckerInbox: ${result.exception.message}", result.exception)

                    _checkerInboxTasksUiState.value =
                        CheckerInboxTasksUiState.Error(result.message)
                }

                is DataState.Loading -> {
                    _checkerInboxTasksUiState.value = CheckerInboxTasksUiState.Loading
                }

                is DataState.Success -> {
                    _checkerInboxTasksUiState.value = CheckerInboxTasksUiState.Success(
                        result.data.first.toString(),
                        result.data.second.toString(),
                    )
                }
            }
        }
    }
}
