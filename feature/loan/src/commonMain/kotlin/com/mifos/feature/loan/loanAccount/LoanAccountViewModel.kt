/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccount

import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.feature_loan_failed_to_create_loan_account
import kpt.feature.loan.generated.resources.feature_loan_failed_to_load_loan
import kpt.feature.loan.generated.resources.feature_loan_failed_to_load_template
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.domain.useCases.CreateLoanAccountUseCase
import com.mifos.core.domain.useCases.GetAllLoanUseCase
import com.mifos.core.domain.useCases.GetLoansAccountTemplateUseCase
import com.mifos.core.network.model.LoansPayload
import com.mifos.room.entities.templates.loans.LoanTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LoanAccountViewModel(
    private val getAllLoanUseCase: GetAllLoanUseCase,
    private val getLoansAccountTemplateUseCase: GetLoansAccountTemplateUseCase,
    private val createLoanAccountUseCase: CreateLoanAccountUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId = savedStateHandle.toRoute<LoanAccountScreenRoute>().clientId

    private val _loanAccountUiState =
        MutableStateFlow<LoanAccountUiState>(LoanAccountUiState.Loading)
    val loanAccountUiState = _loanAccountUiState.asStateFlow()

    private val _loanAccountTemplateUiState = MutableStateFlow(LoanTemplate())
    val loanAccountTemplateUiState = _loanAccountTemplateUiState.asStateFlow()

    fun loadAllLoans() = viewModelScope.launch {
        _loanAccountUiState.value = LoanAccountUiState.Loading
        getAllLoanUseCase()
            .catch {
                _loanAccountUiState.value =
                    LoanAccountUiState.Error(Res.string.feature_loan_failed_to_load_loan)
            }
            .collect { result ->
                _loanAccountUiState.value =
                    LoanAccountUiState.AllLoan(result)
            }
    }

    fun loadLoanAccountTemplate(productId: Int) =
        viewModelScope.launch {
            getLoansAccountTemplateUseCase(clientId, productId)
                .catch {
                    _loanAccountUiState.value =
                        LoanAccountUiState.Error(Res.string.feature_loan_failed_to_load_template)
                }
                .collect { result ->
                    _loanAccountTemplateUiState.value = result
                }
        }

    fun createLoansAccount(loansPayload: LoansPayload) = viewModelScope.launch {
        _loanAccountUiState.value = LoanAccountUiState.Loading
        createLoanAccountUseCase(loansPayload)
            .catch {
                _loanAccountUiState.value =
                    LoanAccountUiState.Error(Res.string.feature_loan_failed_to_create_loan_account)
            }
            .collect {
                _loanAccountUiState.value =
                    LoanAccountUiState.LoanAccountCreatedSuccessfully
            }
    }
}
