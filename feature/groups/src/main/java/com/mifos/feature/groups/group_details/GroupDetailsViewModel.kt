package com.mifos.feature.groups.group_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.GetGroupAssociateClientsUseCase
import com.mifos.core.domain.use_cases.GetGroupDetailsUseCase
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.group.Group
import com.mifos.feature.groups.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    private val getGroupDetailsUseCase: GetGroupDetailsUseCase,
    private val getGroupAssociateClientsUseCase: GetGroupAssociateClientsUseCase
) : ViewModel() {

    private val _groupDetailsUiState =
        MutableStateFlow<GroupDetailsUiState>(GroupDetailsUiState.Loading)
    val groupDetailsUiState = _groupDetailsUiState.asStateFlow()

    private val _loanAccounts = MutableStateFlow<List<LoanAccount>>(emptyList())
    val loanAccount = _loanAccounts.asStateFlow()


    private val _savingsAccounts = MutableStateFlow<List<SavingsAccount>>(emptyList())
    val savingsAccounts = _savingsAccounts.asStateFlow()

    private val _groupAssociateClients = MutableStateFlow<List<Client>>(emptyList())
    val groupAssociateClients = _groupAssociateClients.asStateFlow()


    fun getGroupDetails(groupId: Int) = viewModelScope.launch {
        getGroupDetailsUseCase(groupId).collect { result ->
            when (result) {
                is Resource.Error -> _groupDetailsUiState.value =
                    GroupDetailsUiState.Error(R.string.feature_groups_failed_to_fetch_group_and_account)

                is Resource.Loading -> _groupDetailsUiState.value = GroupDetailsUiState.Loading

                is Resource.Success -> {
                    _groupDetailsUiState.value =
                        GroupDetailsUiState.ShowGroup(result.data?.group ?: Group())
                    _loanAccounts.value = result.data?.groupAccounts?.loanAccounts ?: emptyList()
                    _savingsAccounts.value =
                        result.data?.groupAccounts?.savingsAccounts ?: emptyList()
                }
            }
        }
    }

    fun getGroupAssociateClients(groupId: Int) = viewModelScope.launch {
        getGroupAssociateClientsUseCase(groupId).collect { result ->
            when (result) {
                is Resource.Error -> _groupDetailsUiState.value =
                    GroupDetailsUiState.Error(R.string.feature_groups_failed_to_load_client)

                is Resource.Loading -> Unit
                is Resource.Success -> _groupAssociateClients.value = result.data ?: emptyList()
            }
        }
    }

}