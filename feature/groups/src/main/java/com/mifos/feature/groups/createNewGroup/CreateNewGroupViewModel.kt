/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.createNewGroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.datastore.PrefManager
import com.mifos.core.domain.use_cases.CreateNewGroupUseCase
import com.mifos.core.domain.use_cases.GetGroupOfficesUseCase
import com.mifos.core.objects.group.GroupPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class CreateNewGroupViewModel @Inject constructor(
    private val getGroupOfficesUseCase: GetGroupOfficesUseCase,
    private val createNewGroupUseCase: CreateNewGroupUseCase,
    private val prefManager: PrefManager,
) : ViewModel() {

    private val _createNewGroupUiState = MutableStateFlow<CreateNewGroupUiState>(
        CreateNewGroupUiState.ShowProgressbar,
    )
    val createNewGroupUiState: StateFlow<CreateNewGroupUiState>
        get() = _createNewGroupUiState

    fun getUserStatus() = prefManager.userStatus

    fun getResponse(): String {
        return when (prefManager.userStatus) {
            false -> "created successfully"
            true -> "Saved into DB Successfully"
        }
    }

    fun loadOffices() = viewModelScope.launch {
        getGroupOfficesUseCase().collect { result ->
            when (result) {
                is Resource.Loading ->
                    _createNewGroupUiState.value =
                        CreateNewGroupUiState.ShowProgressbar

                is Resource.Error ->
                    _createNewGroupUiState.value =
                        CreateNewGroupUiState.ShowFetchingError(result.message.toString())

                is Resource.Success ->
                    _createNewGroupUiState.value =
                        CreateNewGroupUiState.ShowOffices(result.data ?: emptyList())
            }
        }
    }

    fun createGroup(groupPayload: GroupPayload) = viewModelScope.launch {
        createNewGroupUseCase(groupPayload).collect { result ->
            when (result) {
                is Resource.Error ->
                    _createNewGroupUiState.value =
                        CreateNewGroupUiState.ShowFetchingError(result.message.toString())

                is Resource.Loading ->
                    _createNewGroupUiState.value =
                        CreateNewGroupUiState.ShowProgressbar

                is Resource.Success ->
                    _createNewGroupUiState.value =
                        result.data?.let { CreateNewGroupUiState.ShowGroupCreatedSuccessfully(it) }!!
            }
        }
    }
}
