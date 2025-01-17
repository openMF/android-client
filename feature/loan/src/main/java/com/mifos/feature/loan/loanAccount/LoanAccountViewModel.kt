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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.CreateLoanAccountUseCase
import com.mifos.core.domain.useCases.GetAllLoanUseCase
import com.mifos.core.domain.useCases.GetLoansAccountTemplateUseCase
import com.mifos.core.network.model.LoansPayload
import com.mifos.feature.loan.R
import com.mifos.room.entities.templates.loans.LoanTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoanAccountViewModel @Inject constructor(
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

    fun loadAllLoans() = viewModelScope.launch(Dispatchers.IO) {
        getAllLoanUseCase().collect { result ->
            when (result) {
                is Resource.Error ->
                    _loanAccountUiState.value =
                        LoanAccountUiState.Error(R.string.feature_loan_failed_to_load_loan)

                is Resource.Loading -> _loanAccountUiState.value = LoanAccountUiState.Loading

                is Resource.Success ->
                    _loanAccountUiState.value =
                        LoanAccountUiState.AllLoan(result.data ?: emptyList())
            }
        }
    }

    fun loadLoanAccountTemplate(clientId: Int, productId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            getLoansAccountTemplateUseCase(clientId, productId).collect { result ->
                when (result) {
                    is Resource.Error ->
                        _loanAccountUiState.value =
                            LoanAccountUiState.Error(R.string.feature_loan_failed_to_load_template)

                    is Resource.Loading -> Unit

                    is Resource.Success ->
                        _loanAccountTemplateUiState.value =
                            result.data ?: LoanTemplate()
                }
            }
        }

    fun createLoansAccount(loansPayload: LoansPayload) = viewModelScope.launch(Dispatchers.IO) {
        createLoanAccountUseCase(loansPayload).collect { result ->
            when (result) {
                is Resource.Error ->
                    _loanAccountUiState.value =
                        LoanAccountUiState.Error(R.string.feature_loan_failed_to_create_loan_account)

                is Resource.Loading -> _loanAccountUiState.value = LoanAccountUiState.Loading

                is Resource.Success ->
                    _loanAccountUiState.value =
                        LoanAccountUiState.LoanAccountCreatedSuccessfully
            }
        }
    }
}
