/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.centerGroupList

import androidclient.feature.center.generated.resources.Res
import androidclient.feature.center.generated.resources.feature_center_failed_to_load_group_list
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.GroupListRepository
import com.mifos.room.entities.group.CenterWithAssociations
import com.mifos.room.entities.group.GroupWithAssociations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class GroupListViewModel(
    private val groupRepo: GroupListRepository,
    private val repository: GroupListRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val centerId = savedStateHandle.getStateFlow(key = Constants.CENTER_ID, initialValue = 0)

    private val _groupListUiState = MutableStateFlow<GroupListUiState>(GroupListUiState.Loading)
    val groupListUiState = _groupListUiState.asStateFlow()

    private val _groupAssociationState = MutableStateFlow<GroupWithAssociations?>(null)
    val groupAssociationState = _groupAssociationState.asStateFlow()

    fun loadGroupByCenter(id: Int) = viewModelScope.launch {
        _groupListUiState.value = GroupListUiState.Loading
        repository.getGroupsByCenter(id).catch {
            _groupListUiState.value =
                GroupListUiState.Error(Res.string.feature_center_failed_to_load_group_list)
        }.collect {
            _groupListUiState.value =
                GroupListUiState.GroupList(it.data ?: CenterWithAssociations())
        }
    }

    fun loadGroups(groupId: Int) {
        _groupListUiState.value = GroupListUiState.Loading
        viewModelScope.launch {
            groupRepo.getGroups(groupId).catch {
                _groupListUiState.value =
                    GroupListUiState.Error(Res.string.feature_center_failed_to_load_group_list)
            }.collect {
                _groupAssociationState.value = it.data
            }
        }
    }
}
