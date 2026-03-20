/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.creditBalanceRefund

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_error_bad_request
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_error_network
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_error_not_found
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_error_server
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_error_unauthorized
import androidclient.feature.loan.generated.resources.feature_loan_credit_balance_refund_error_unknown
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.CurrencyFormatter
import com.mifos.core.data.repository.CreditBalanceRefundRepository
import com.mifos.room.entities.accounts.loans.CreditBalanceRefundRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Credit Balance Refund feature.
 * Manages state, validation, and submission of credit balance refund transactions.
 *
 * @property savedStateHandle SavedStateHandle for extracting navigation arguments
 * @property repository CreditBalanceRefundRepository for API communication
 */
class CreditBalanceRefundViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: CreditBalanceRefundRepository,
) : ViewModel() {

    private val arg = savedStateHandle.toRoute<CreditBalanceRefundScreenRoute>()

    private val _uiState = MutableStateFlow<CreditBalanceRefundUiState>(CreditBalanceRefundUiState.Initial)
    val uiState: StateFlow<CreditBalanceRefundUiState> get() = _uiState

    init {
        _uiState.value = CreditBalanceRefundUiState.ShowRefundForm(
            loanId = arg.loanId,
            clientName = arg.clientName,
            loanAccountNumber = arg.loanAccountNumber,
            overpaidAmount = arg.overpaidAmount,
            currencyCode = arg.currencyCode,
            decimalPlaces = arg.decimalPlaces,
        )
    }

    /**
     * Validates the transaction amount.
     * Checks if the amount is numeric, positive, and within the overpaid limit.
     *
     * @param amount The amount string to validate
     * @param maxAmount The maximum allowed amount (overpaid amount)
     * @return true if the amount is valid, false otherwise
     */
    fun validateAmount(amount: String, maxAmount: Double): Boolean {
        if (amount.isBlank()) {
            return false
        }

        val amountValue = amount.toDoubleOrNull() ?: return false

        if (amountValue <= 0) {
            return false
        }

        if (amountValue > maxAmount) {
            return false
        }

        return true
    }

    /**
     * Formats a currency amount according to the currency code and decimal places.
     *
     * @param amount The amount to format
     * @param code The currency code (e.g., "USD", "EUR")
     * @param decimalPlaces The number of decimal places to display
     * @return Formatted currency string
     */
    fun formatCurrency(amount: Double?, code: String?, decimalPlaces: Int?): String {
        return CurrencyFormatter.format(
            balance = amount,
            currencyCode = code,
            maximumFractionDigits = decimalPlaces ?: 2,
        )
    }

    /**
     * Submits the credit balance refund transaction.
     * Builds the request, calls the repository, and handles success/error responses.
     *
     * @param request The CreditBalanceRefundRequest containing transaction details
     */
    fun submitRefund(request: CreditBalanceRefundRequest) {
        viewModelScope.launch {
            // Update state to Loading
            _uiState.value = CreditBalanceRefundUiState.Loading

            try {
                // Call repository to submit refund
                val response = repository.submitRefund(arg.loanId, request)

                // Extract transaction ID from response - handle null case
                val transactionId = response.resourceId?.toString()
                    ?: throw IllegalStateException("Transaction ID is null in response")

                // Update state to Success
                _uiState.value = CreditBalanceRefundUiState.Success(transactionId)
            } catch (e: Exception) {
                // Handle errors and update state to Error
                val errorMessage = when {
                    e.message?.contains("400") == true ->
                        Res.string.feature_loan_credit_balance_refund_error_bad_request
                    e.message?.contains("401") == true || e.message?.contains("403") == true ->
                        Res.string.feature_loan_credit_balance_refund_error_unauthorized
                    e.message?.contains("404") == true ->
                        Res.string.feature_loan_credit_balance_refund_error_not_found
                    e.message?.contains("500") == true ->
                        Res.string.feature_loan_credit_balance_refund_error_server
                    e.message?.contains("network", ignoreCase = true) == true ||
                        e.message?.contains("connection", ignoreCase = true) == true ||
                        e.message?.contains("timeout", ignoreCase = true) == true ->
                        Res.string.feature_loan_credit_balance_refund_error_network
                    else ->
                        Res.string.feature_loan_credit_balance_refund_error_unknown
                }

                _uiState.value = CreditBalanceRefundUiState.Error(errorMessage)
            }
        }
    }

    /**
     * Resets the UI state back to the form state.
     * Used for retry functionality after an error.
     */
    fun resetToForm() {
        _uiState.value = CreditBalanceRefundUiState.ShowRefundForm(
            loanId = arg.loanId,
            clientName = arg.clientName,
            loanAccountNumber = arg.loanAccountNumber,
            overpaidAmount = arg.overpaidAmount,
            currencyCode = arg.currencyCode,
            decimalPlaces = arg.decimalPlaces,
        )
    }
}
