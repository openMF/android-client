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
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.room.entities.group.GroupPayloadEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 10/08/23.
 */
class CreateNewGroupViewModel(
//    private val getGroupOfficesUseCase: GetGroupOfficesUseCase,
//    private val createNewGroupUseCase: CreateNewGroupUseCase,
    private val repository: CreateNewGroupRepository,
    private val prefManager: UserPreferencesRepository,
) : ViewModel() {

    val userStatus: StateFlow<Boolean> = prefManager.userInfo
        .map { it.userStatus }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    private val _createNewGroupUiState = MutableStateFlow<CreateNewGroupUiState>(
        CreateNewGroupUiState.ShowProgressbar,
    )
    val createNewGroupUiState: StateFlow<CreateNewGroupUiState>
        get() = _createNewGroupUiState

    fun getResponse(): String {
        return when (userStatus.value) {
            false -> "created successfully"
            true -> "Saved into DB Successfully"
        }
    }

    fun loadOffices() {
        viewModelScope.launch {
            repository.offices().collect { dataState ->
                when (dataState) {
                    is DataState.Error -> {
                        _createNewGroupUiState.value =
                            CreateNewGroupUiState.ShowFetchingError(dataState.message)
                    }

                    DataState.Loading -> {
                        _createNewGroupUiState.value =
                            CreateNewGroupUiState.ShowProgressbar
                    }

                    is DataState.Success -> {
                        val offices = dataState.data
                        if (offices == null) {
                            _createNewGroupUiState.value =
                                CreateNewGroupUiState.ShowFetchingError("No offices found")
                        } else {
                            _createNewGroupUiState.value =
                                CreateNewGroupUiState.ShowOffices(offices)
                        }
                    }
                }
            }
        }
    }

    fun createGroup(groupPayload: GroupPayloadEntity) {
        _createNewGroupUiState.value =
            CreateNewGroupUiState.ShowProgressbar

        viewModelScope.launch {
            try {
                val savedResponse = repository.createGroup(groupPayload)
                _createNewGroupUiState.value =
                    CreateNewGroupUiState.ShowGroupCreatedSuccessfully(savedResponse)
            } catch (e: Exception) {
                _createNewGroupUiState.value =
                    CreateNewGroupUiState.ShowFetchingError(e.message.toString())
            }
        }
    }
}
