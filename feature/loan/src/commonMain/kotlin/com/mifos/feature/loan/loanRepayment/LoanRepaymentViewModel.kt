/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanRepayment

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_failed_to_load_loan_repayment
import androidclient.feature.loan.generated.resources.feature_loan_payment_failed
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_details_not_found
import androidclient.feature.loan.generated.resources.feature_loan_profile_failed_to_load_loan
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.LoanAccountSummaryRepository
import com.mifos.core.data.repository.LoanRepaymentRepository
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.round

class LoanRepaymentViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: LoanRepaymentRepository,
    private val summaryRepository: LoanAccountSummaryRepository,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<LoanRepaymentScreenRoute>()
    private val _loanDetailsState = MutableStateFlow(LoanDetails())
    val loanDetailsState = _loanDetailsState.asStateFlow()

    private val _loanRepaymentUiState =
        MutableStateFlow<LoanRepaymentUiState>(LoanRepaymentUiState.ShowProgressbar)
    val loanRepaymentUiState: StateFlow<LoanRepaymentUiState> get() = _loanRepaymentUiState

    init {
        if (args.loanAccountNumber.isEmpty()) {
            loadLoanById()
        } else {
            _loanDetailsState.value = _loanDetailsState.value.copy(
                loanAccountNumber = args.loanAccountNumber,
                loanId = args.loanId,
                clientName = args.clientName,
                loanProductName = args.loanProductName,
                amountInArrears = args.amountInArrears,
            )
        }
    }

    fun loadLoanById() {
        viewModelScope.launch {
            summaryRepository.getLoanById(args.loanId).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        _loanRepaymentUiState.value = LoanRepaymentUiState.ShowProgressbar
                    }

                    is DataState.Success -> {
                        val loanWithAssociations = dataState.data
                        if (loanWithAssociations == null) {
                            _loanRepaymentUiState.value = LoanRepaymentUiState.ShowError(
                                Res.string.feature_loan_profile_error_details_not_found,
                            )
                            return@collect
                        }
                        _loanDetailsState.value = _loanDetailsState.value.copy(
                            loanId = loanWithAssociations.id,
                            clientName = loanWithAssociations.clientName,
                            loanProductName = loanWithAssociations.loanProductName,
                            amountInArrears = loanWithAssociations.summary.totalOverdue,
                            loanAccountNumber = loanWithAssociations.accountNo,
                        )
                        checkDatabaseLoanRepaymentByLoanId()
                    }

                    is DataState.Error -> {
                        _loanRepaymentUiState.value = LoanRepaymentUiState.ShowError(
                            Res.string.feature_loan_profile_failed_to_load_loan,
                        )
                    }
                }
            }
        }
    }

    fun loadLoanRepaymentTemplate() {
        viewModelScope.launch {
            repository.getLoanRepayTemplate(args.loanId).collect { state ->
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
                        val template = state.data
                        if (template == null) {
                            _loanRepaymentUiState.value = LoanRepaymentUiState.ShowError(
                                Res.string.feature_loan_failed_to_load_loan_repayment,
                            )
                            return@collect
                        }
                        _loanRepaymentUiState.value = LoanRepaymentUiState.ShowLoanRepayTemplate(template)
                    }
                }
            }
        }
    }

    fun submitPayment(request: LoanRepaymentRequestEntity) {
        viewModelScope.launch {
            _loanRepaymentUiState.value = LoanRepaymentUiState.ShowProgressbar

            try {
                val loanRepaymentResponse = repository.submitPayment(args.loanId, request)
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
            repository.getDatabaseLoanRepaymentByLoanId(args.loanId).collect { state ->
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

    fun calculateTotal(fees: String, amount: String, additionalPayment: String): Double {
        fun setValue(value: String): Double {
            if (value.isEmpty()) return 0.0
            return try {
                value.toDouble()
            } catch (e: NumberFormatException) {
                0.0
            }
        }

        val total = setValue(fees) + setValue(amount) + setValue(additionalPayment)
        return round(total * 100) / 100.0
    }

    fun formatCurrency(amount: Double?, code: String?, decimalPlaces: Int?): String {
        return CurrencyFormatter.format(
            balance = amount,
            currencyCode = code,
            maximumFractionDigits = decimalPlaces ?: 2,
        )
    }

    fun isAllFieldsValid(
        amount: String,
        additionalPayment: String,
        fees: String,
        paymentType: String,
    ): Boolean {
        return listOf(amount, additionalPayment, fees).all {
            it.trim().toDoubleOrNull()?.let { n -> n >= 0 } == true
        } && paymentType.isNotBlank()
    }
}

data class LoanDetails(
    val clientName: String = "",
    val loanId: Int = 0,
    val loanAccountNumber: String = "",
    val loanProductName: String = "",
    val amountInArrears: Double? = 0.0,
)
