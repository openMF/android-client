/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.groupLoanAccount

import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.feature_loan_failed_to_create_loan_account
import kpt.feature.loan.generated.resources.feature_loan_failed_to_load_loan
import kpt.feature.loan.generated.resources.feature_loan_failed_to_load_template
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.domain.useCases.CreateGroupLoansAccountUseCase
import com.mifos.core.domain.useCases.GetAllLoanUseCase
import com.mifos.core.domain.useCases.GetGroupLoansAccountTemplateUseCase
import com.mifos.core.model.objects.payloads.GroupLoanPayload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class GroupLoanAccountViewModel(
    private val getAllLoanUseCase: GetAllLoanUseCase,
    private val getGroupLoansAccountTemplateUseCase: GetGroupLoansAccountTemplateUseCase,
    private val createGroupLoansAccountUseCase: CreateGroupLoansAccountUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val groupId = savedStateHandle.toRoute<GroupLoanScreenRoute>().groupId

    private val _groupLoanAccountUiState =
        MutableStateFlow<GroupLoanAccountUiState>(GroupLoanAccountUiState.Loading)
    val groupLoanAccountUiState = _groupLoanAccountUiState.asStateFlow()

    private val _loanProducts = MutableStateFlow<List<com.mifos.core.model.objects.organisations.LoanProducts>>(emptyList())
    val loanProducts = _loanProducts.asStateFlow()

    fun loadAllLoans() = viewModelScope.launch {
        _groupLoanAccountUiState.value = GroupLoanAccountUiState.Loading
        getAllLoanUseCase()
            .catch {
                _groupLoanAccountUiState.value =
                    GroupLoanAccountUiState.Error(Res.string.feature_loan_failed_to_load_loan)
            }
            .collect { loanProducts ->
                _loanProducts.value = loanProducts
            }
    }

    fun loadGroupLoansAccountTemplate(productId: Int) =
        viewModelScope.launch {
            _groupLoanAccountUiState.value = GroupLoanAccountUiState.Loading
            getGroupLoansAccountTemplateUseCase(groupId, productId)
                .catch {
                    _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.Error(Res.string.feature_loan_failed_to_load_template)
                }
                .collect { template ->
                    _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.GroupLoanAccountTemplate(
                            template,
                        )
                }
        }

    fun createGroupLoanAccount(loansPayload: GroupLoanPayload) =
        viewModelScope.launch {
            _groupLoanAccountUiState.value = GroupLoanAccountUiState.Loading
            createGroupLoansAccountUseCase(loansPayload)
                .catch {
                    _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.Error(Res.string.feature_loan_failed_to_create_loan_account)
                }
                .collect {
                    _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.GroupLoanAccountCreatedSuccessfully
                }
        }
}
