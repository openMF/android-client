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

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.core.entity.accounts.loan.LoanWithAssociations
import com.mifos.feature.loan.R
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequest
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class LoanRepaymentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanRepaymentRepository,
) : ViewModel() {

    val arg =
        savedStateHandle.getStateFlow(key = Constants.LOAN_WITH_ASSOCIATIONS, initialValue = "")
    val loanWithAssociations: LoanWithAssociations =
        Gson().fromJson(arg.value, LoanWithAssociations::class.java)

    private val _loanRepaymentUiState =
        MutableStateFlow<LoanRepaymentUiState>(LoanRepaymentUiState.ShowProgressbar)
    val loanRepaymentUiState: StateFlow<LoanRepaymentUiState> get() = _loanRepaymentUiState

    var clientName = loanWithAssociations.clientName
    var loanId = loanWithAssociations.id
    var loanAccountNumber = loanWithAssociations.accountNo
    var loanProductName = loanWithAssociations.loanProductName
    var amountInArrears = loanWithAssociations.summary.totalOverdue

    fun loanLoanRepaymentTemplate() {
        viewModelScope.launch {
            _loanRepaymentUiState.value = LoanRepaymentUiState.ShowProgressbar

            repository.getLoanRepayTemplate(loanId)
                .catch {
                    Log.d("loanRepaymentLog ", ", $loanId ${it.message}")
                    _loanRepaymentUiState.value =
                        LoanRepaymentUiState.ShowError(R.string.feature_loan_failed_to_load_loan_repayment)
                }
                .collect {
                    _loanRepaymentUiState.value =
                        LoanRepaymentUiState.ShowLoanRepayTemplate(
                            it ?: LoanRepaymentTemplate(),
                        )
                }
        }
    }

    /**
     *   app crashes on submit click
     */
    fun submitPayment(request: LoanRepaymentRequest) {
        viewModelScope.launch {
            _loanRepaymentUiState.value = LoanRepaymentUiState.ShowProgressbar

            try {
                val loanRepaymentResponse = repository.submitPayment(loanId, request)
                _loanRepaymentUiState.value =
                    LoanRepaymentUiState.ShowPaymentSubmittedSuccessfully(
                        loanRepaymentResponse,
                    )
            } catch (e: Exception) {
                _loanRepaymentUiState.value =
                    LoanRepaymentUiState.ShowError(R.string.feature_loan_payment_failed)
            }
        }
    }

    fun checkDatabaseLoanRepaymentByLoanId() {
        viewModelScope.launch {
            _loanRepaymentUiState.value = LoanRepaymentUiState.ShowProgressbar

            repository.getDatabaseLoanRepaymentByLoanId(loanId)
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.d("loanRepaymentLog ", ", $loanId ${it.message}")
                    _loanRepaymentUiState.value =
                        LoanRepaymentUiState.ShowError(R.string.feature_loan_failed_to_load_loan_repayment)
                }
                .collect { loanRepaymentRequest ->
                    if (loanRepaymentRequest != null) {
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
