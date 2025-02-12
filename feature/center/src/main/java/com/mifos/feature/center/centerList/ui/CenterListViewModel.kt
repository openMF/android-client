/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.centerList.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.CenterListRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.feature.center.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CenterListViewModel @Inject constructor(
    private val prefManager: PrefManager,
    private val repository: CenterListRepository,
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
        if (prefManager.userStatus) {
            loadCentersFromDb()
        } else {
            loadCentersFromApi()
        }
    }

    private fun loadCentersFromApi() = viewModelScope.launch(Dispatchers.IO) {
        val response = repository.getAllCenters()
        _centerListUiState.value = CenterListUiState.CenterList(response)
    }

    private fun loadCentersFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            _centerListUiState.value = CenterListUiState.Loading

            repository.allDatabaseCenters()
                .catch {
                    _centerListUiState.value =
                        CenterListUiState.Error(R.string.feature_center_failed_to_load_db_centers)
                }.collect {
                    _centerListUiState.value =
                        CenterListUiState.CenterListDb(it.pageItems)
                }
        }
    }
}
