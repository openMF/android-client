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
import com.mifos.core.data.repository.CreateNewGroupRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.room.entities.group.GroupPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class CreateNewGroupViewModel @Inject constructor(
//    private val getGroupOfficesUseCase: GetGroupOfficesUseCase,
//    private val createNewGroupUseCase: CreateNewGroupUseCase,
    private val repository: CreateNewGroupRepository,
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

    fun loadOffices() {
        viewModelScope.launch {
            _createNewGroupUiState.value =
                CreateNewGroupUiState.ShowProgressbar

            repository.offices()
                .catch {
                    _createNewGroupUiState.value =
                        CreateNewGroupUiState.ShowFetchingError(it.message.toString())
                }
                .collect {
                    _createNewGroupUiState.value =
                        CreateNewGroupUiState.ShowOffices(it)
                }
        }
    }

    fun createGroup(groupPayload: GroupPayload) {
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
