/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepayment

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan_repayment
import androidclient.feature.loan.generated.resources.feature_loan_payment_failed
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoanRepaymentViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanRepaymentRepository,
) : ViewModel() {

    val arg = savedStateHandle.toRoute<LoanRepaymentScreenRoute>()

    private val _loanRepaymentUiState =
        MutableStateFlow<LoanRepaymentUiState>(LoanRepaymentUiState.ShowProgressbar)
    val loanRepaymentUiState: StateFlow<LoanRepaymentUiState> get() = _loanRepaymentUiState

    fun loanLoanRepaymentTemplate() {
        viewModelScope.launch {
            repository.getLoanRepayTemplate(arg.loanId).collect { state ->
                when (state) {
                    is DataState.Error ->
                        _loanRepaymentUiState.value =
                            LoanRepaymentUiState.ShowError(
                                Res.string
                                    .feature_loan_failed_to_load_loan_repayment,
                            )
                    DataState.Loading ->
                        _loanRepaymentUiState.value = LoanRepaymentUiState.ShowProgressbar
                    is DataState.Success ->
                        _loanRepaymentUiState.value =
                            LoanRepaymentUiState.ShowLoanRepayTemplate(
                                state.data ?: LoanRepaymentTemplateEntity(),
                            )
                }
            }
        }
    }

    /**
     *   app crashes on submit click
     */
    fun submitPayment(request: LoanRepaymentRequestEntity) {
        viewModelScope.launch {
            _loanRepaymentUiState.value = LoanRepaymentUiState.ShowProgressbar

            try {
                val loanRepaymentResponse = repository.submitPayment(arg.loanId, request)
                _loanRepaymentUiState.value =
                    LoanRepaymentUiState.ShowPaymentSubmittedSuccessfully(
                        loanRepaymentResponse,
                    )
            } catch (e: Exception) {
                _loanRepaymentUiState.value =
                    LoanRepaymentUiState.ShowError(Res.string.feature_loan_payment_failed)
            }
        }
    }

    fun checkDatabaseLoanRepaymentByLoanId() {
        viewModelScope.launch {
            repository.getDatabaseLoanRepaymentByLoanId(arg.loanId).collect { state ->
                when (state) {
                    is DataState.Error ->
                        _loanRepaymentUiState.value =
                            LoanRepaymentUiState.ShowError(
                                Res.string
                                    .feature_loan_failed_to_load_loan_repayment,
                            )
                    DataState.Loading ->
                        _loanRepaymentUiState.value = LoanRepaymentUiState.ShowProgressbar
                    is DataState.Success -> {
                        if (state.data != null) {
                            _loanRepaymentUiState.value =
                                LoanRepaymentUiState.ShowLoanRepaymentExistInDatabase
                        } else {
                            _loanRepaymentUiState.value =
                                LoanRepaymentUiState.ShowLoanRepaymentDoesNotExistInDatabase
                        }
                    }
                }
            }
        }
    }
}
