/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanAccount

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_create_loan_account
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_template
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.CreateLoanAccountUseCase
import com.mifos.core.domain.useCases.GetAllLoanUseCase
import com.mifos.core.domain.useCases.GetLoansAccountTemplateUseCase
import com.mifos.core.network.model.LoansPayload
import com.mifos.room.entities.templates.loans.LoanTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoanAccountViewModel(
    private val getAllLoanUseCase: GetAllLoanUseCase,
    private val getLoansAccountTemplateUseCase: GetLoansAccountTemplateUseCase,
    private val createLoanAccountUseCase: CreateLoanAccountUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = 0)

    private val _loanAccountUiState =
        MutableStateFlow<LoanAccountUiState>(LoanAccountUiState.Loading)
    val loanAccountUiState = _loanAccountUiState.asStateFlow()

    private val _loanAccountTemplateUiState = MutableStateFlow(LoanTemplate())
    val loanAccountTemplateUiState = _loanAccountTemplateUiState.asStateFlow()

    fun loadAllLoans() = viewModelScope.launch {
        getAllLoanUseCase().collect { result ->
            when (result) {
                is DataState.Error ->
                    _loanAccountUiState.value =
                        LoanAccountUiState.Error(Res.string.feature_loan_failed_to_load_loan)

                is DataState.Loading -> _loanAccountUiState.value = LoanAccountUiState.Loading

                is DataState.Success ->
                    _loanAccountUiState.value =
                        LoanAccountUiState.AllLoan(result.data)
            }
        }
    }

    fun loadLoanAccountTemplate(clientId: Int, productId: Int) =
        viewModelScope.launch {
            getLoansAccountTemplateUseCase(clientId, productId).collect { result ->
                when (result) {
                    is DataState.Error ->
                        _loanAccountUiState.value =
                            LoanAccountUiState.Error(Res.string.feature_loan_failed_to_load_template)

                    is DataState.Loading -> Unit

                    is DataState.Success ->
                        _loanAccountTemplateUiState.value =
                            result.data
                }
            }
        }

    fun createLoansAccount(loansPayload: LoansPayload) = viewModelScope.launch {
        createLoanAccountUseCase(loansPayload).collect { result ->
            when (result) {
                is DataState.Error ->
                    _loanAccountUiState.value =
                        LoanAccountUiState.Error(Res.string.feature_loan_failed_to_create_loan_account)

                is DataState.Loading -> _loanAccountUiState.value = LoanAccountUiState.Loading

                is DataState.Success ->
                    _loanAccountUiState.value =
                        LoanAccountUiState.LoanAccountCreatedSuccessfully
            }
        }
    }
}
