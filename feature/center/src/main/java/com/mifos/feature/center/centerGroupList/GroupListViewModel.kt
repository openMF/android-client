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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.GetGroupsByCenterUseCase
import com.mifos.core.domain.useCases.GetGroupsUseCase
import com.mifos.feature.center.R
import com.mifos.room.entities.group.CenterWithAssociations
import com.mifos.room.entities.group.GroupWithAssociations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupListViewModel @Inject constructor(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val getGroupsByCenterUseCase: GetGroupsByCenterUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val centerId = savedStateHandle.getStateFlow(key = Constants.CENTER_ID, initialValue = 0)

    private val _groupListUiState = MutableStateFlow<GroupListUiState>(GroupListUiState.Loading)
    val groupListUiState = _groupListUiState.asStateFlow()

    private val _groupAssociationState = MutableStateFlow<GroupWithAssociations?>(null)
    val groupAssociationState = _groupAssociationState.asStateFlow()

    fun loadGroupByCenter(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        getGroupsByCenterUseCase(id).collect { result ->
            when (result) {
                is Resource.Error ->
                    _groupListUiState.value =
                        GroupListUiState.Error(R.string.feature_center_failed_to_load_group_list)

                is Resource.Loading -> _groupListUiState.value = GroupListUiState.Loading

                is Resource.Success ->
                    _groupListUiState.value =
                        GroupListUiState.GroupList(result.data ?: CenterWithAssociations())
            }
        }
    }

    fun loadGroups(groupId: Int) = viewModelScope.launch(Dispatchers.IO) {
        getGroupsUseCase(groupId).collect { result ->
            when (result) {
                is Resource.Error ->
                    _groupListUiState.value =
                        GroupListUiState.Error(R.string.feature_center_failed_to_load_group_list)

                is Resource.Loading -> _groupListUiState.value = GroupListUiState.Loading

                is Resource.Success -> _groupAssociationState.value = result.data
            }
        }
    }
}
