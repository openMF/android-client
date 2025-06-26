/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.groupDetails

import androidclient.feature.groups.generated.resources.Res
import androidclient.feature.groups.generated.resources.feature_groups_failed_to_fetch_group_and_account
import androidclient.feature.groups.generated.resources.feature_groups_failed_to_load_client
import androidclient.feature.groups.generated.resources.feature_groups_no_group_clients
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.GroupDetailsRepository
import com.mifos.core.domain.useCases.GetGroupDetailsUseCase
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.group.GroupEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupDetailsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: GroupDetailsRepository,
    private val getGroupDetailsUseCase: GetGroupDetailsUseCase,
) : ViewModel() {

    val groupId = savedStateHandle.getStateFlow(key = Constants.GROUP_ID, initialValue = 0)

    private val _groupDetailsUiState =
        MutableStateFlow<GroupDetailsUiState>(GroupDetailsUiState.Loading)
    val groupDetailsUiState = _groupDetailsUiState.asStateFlow()

    private val _loanAccounts = MutableStateFlow<List<LoanAccountEntity>>(emptyList())
    val loanAccounts = _loanAccounts.asStateFlow()

    private val _savingsAccounts = MutableStateFlow<List<SavingsAccountEntity>>(emptyList())
    val savingsAccounts = _savingsAccounts.asStateFlow()

    private val _groupAssociateClients = MutableStateFlow<List<ClientEntity>>(emptyList())
    val groupAssociateClients = _groupAssociateClients.asStateFlow()

    fun getGroupDetails(groupId: Int) {
        viewModelScope.launch {
            getGroupDetailsUseCase.invoke(groupId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            _groupDetailsUiState.value =
                                GroupDetailsUiState.Error(Res.string.feature_groups_failed_to_fetch_group_and_account)
                        }

                        DataState.Loading ->
                            _groupDetailsUiState.value =
                                GroupDetailsUiState.Loading

                        is DataState.Success -> {
                            val account = dataState.data
                            _groupDetailsUiState.value =
                                GroupDetailsUiState.ShowGroup(account.group ?: GroupEntity())
                            _loanAccounts.value =
                                account.groupAccounts?.loanAccounts ?: emptyList()
                            _savingsAccounts.value =
                                account.groupAccounts?.savingsAccounts ?: emptyList()
                        }
                    }
                }
        }
    }

    fun getGroupAssociateClients(groupId: Int) {
        viewModelScope.launch {
            repository.getGroupWithAssociations(groupId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            _groupDetailsUiState.value =
                                GroupDetailsUiState.Error(Res.string.feature_groups_failed_to_load_client)
                        }

                        DataState.Loading -> {
                            _groupDetailsUiState.value =
                                GroupDetailsUiState.Loading
                        }

                        is DataState.Success -> {
                            if (dataState.data.clientMembers.isNotEmpty()) {
                                _groupAssociateClients.value =
                                    dataState.data.clientMembers
                            } else {
                                _groupDetailsUiState.value =
                                    GroupDetailsUiState.Error(Res.string.feature_groups_no_group_clients)
                            }
                        }
                    }
                }
        }
    }
}
