/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.groupLoanAccount

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_create_loan_account
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_template
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.CreateGroupLoansAccountUseCase
import com.mifos.core.domain.useCases.GetAllLoanUseCase
import com.mifos.core.domain.useCases.GetGroupLoansAccountTemplateUseCase
import com.mifos.core.model.objects.payloads.GroupLoanPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupLoanAccountViewModel(
    private val getAllLoanUseCase: GetAllLoanUseCase,
    private val getGroupLoansAccountTemplateUseCase: GetGroupLoansAccountTemplateUseCase,
    private val createGroupLoansAccountUseCase: CreateGroupLoansAccountUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val groupId = savedStateHandle.getStateFlow(key = Constants.GROUP_ID, initialValue = 0)

    private val _groupLoanAccountUiState =
        MutableStateFlow<GroupLoanAccountUiState>(GroupLoanAccountUiState.Loading)
    val groupLoanAccountUiState = _groupLoanAccountUiState.asStateFlow()

    private val _loanProducts = MutableStateFlow<List<com.mifos.core.model.objects.organisations.LoanProducts>>(emptyList())
    val loanProducts = _loanProducts.asStateFlow()

    fun loadAllLoans() = viewModelScope.launch {
        getAllLoanUseCase().collect { result ->
            when (result) {
                is DataState.Error ->
                    _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.Error(Res.string.feature_loan_failed_to_load_loan)

                is DataState.Loading ->
                    _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.Loading

                is DataState.Success -> _loanProducts.value = result.data
            }
        }
    }

    fun loadGroupLoansAccountTemplate(groupId: Int, productId: Int) =
        viewModelScope.launch {
            getGroupLoansAccountTemplateUseCase(groupId, productId).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _groupLoanAccountUiState.value =
                            GroupLoanAccountUiState.Error(Res.string.feature_loan_failed_to_load_template)

                    is DataState.Loading -> GroupLoanAccountUiState.Loading

                    is DataState.Success ->
                        _groupLoanAccountUiState.value =
                            GroupLoanAccountUiState.GroupLoanAccountTemplate(
                                result.data,
                            )
                }
            }
        }

    fun createGroupLoanAccount(loansPayload: GroupLoanPayload) =
        viewModelScope.launch {
            createGroupLoansAccountUseCase(loansPayload).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _groupLoanAccountUiState.value =
                            GroupLoanAccountUiState.Error(Res.string.feature_loan_failed_to_create_loan_account)

                    is DataState.Loading ->
                        _groupLoanAccountUiState.value =
                            GroupLoanAccountUiState.Loading

                    is DataState.Success ->
                        _groupLoanAccountUiState.value =
                            GroupLoanAccountUiState.GroupLoanAccountCreatedSuccessfully
                }
            }
        }
}
