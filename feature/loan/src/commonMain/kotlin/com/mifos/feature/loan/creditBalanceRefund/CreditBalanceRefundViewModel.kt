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
import androidclient.feature.loan.generated.resources.feature_error_network_not_available
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_details_not_found
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.CreditBalanceRefundRepository
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.accounts.loans.CreditBalanceRefundRequest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreditBalanceRefundViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: CreditBalanceRefundRepository,
) : BaseViewModel<CreditBalanceRefundState, CreditBalanceRefundEvent, CreditBalanceRefundAction>(
    initialState = CreditBalanceRefundState(),
) {

    private val route = savedStateHandle.toRoute<CreditBalanceRefundScreenRoute>()

    /** Cached refund request for retry operations */
    private var lastSubmitRequest: CreditBalanceRefundRequest? = null

    init {
        mutableStateFlow.update { it.copy(loanId = route.loanId) }
        observeLoanDetails()
    }

    /**
     * Observes loan details from the repository.
     * The repository handles network availability and refresh triggers internally.
     * Network status is derived from [DataState.Error] messages emitted by the repository.
     */
    private fun observeLoanDetails() {
        viewModelScope.launch {
            repository.getLoanById(route.loanId).collect { loanResult ->
                mutableStateFlow.update { currentState ->
                    var newState = currentState
                    when (loanResult) {
                        is DataState.Loading -> {
                            if (newState.clientName == null && newState.dialogState !is CreditBalanceRefundState.DialogState.Success) {
                                newState = newState.copy(dialogState = CreditBalanceRefundState.DialogState.Loading)
                            }
                        }
                        is DataState.Success -> {
                            val loan = loanResult.data
                            if (loan == null) {
                                newState = newState.copy(
                                    dialogState = CreditBalanceRefundState.DialogState.Error(
                                        messageRes = Res.string.feature_loan_profile_error_details_not_found,
                                    ),
                                )
                            } else {
                                newState = newState.copy(
                                    networkAvailable = true,
                                    dialogState = newState.dialogState as? CreditBalanceRefundState.DialogState.Success,
                                    clientName = loan.clientName,
                                    loanAccountNumber = loan.accountNo,
                                    overpaidAmount = loan.totalOverpaid,
                                    currencyCode = loan.currencyCode,
                                    decimalPlaces = loan.decimalPlaces,
                                )
                            }
                        }
                        is DataState.Error -> {
                            val isNetworkError = loanResult.message == "Network not available" ||
                                loanResult.message.contains("Unable to resolve host", ignoreCase = true) ||
                                loanResult.message.contains("Failed to connect", ignoreCase = true)

                            newState = if (isNetworkError) {
                                newState.copy(
                                    networkAvailable = false,
                                    dialogState = CreditBalanceRefundState.DialogState.Error(
                                        messageRes = Res.string.feature_error_network_not_available,
                                    ),
                                )
                            } else {
                                newState.copy(
                                    dialogState = CreditBalanceRefundState.DialogState.Error(
                                        message = loanResult.message,
                                    ),
                                )
                            }
                        }
                    }
                    newState
                }
            }
        }
    }

    override fun handleAction(action: CreditBalanceRefundAction) {
        when (action) {
            is CreditBalanceRefundAction.NavigateBack -> {
                if (state.dialogState is CreditBalanceRefundState.DialogState.Success) {
                    sendEvent(CreditBalanceRefundEvent.NavigateBackWithRefresh)
                } else {
                    sendEvent(CreditBalanceRefundEvent.NavigateBack)
                }
            }
            is CreditBalanceRefundAction.OnDismissDialog -> {
                if (state.dialogState is CreditBalanceRefundState.DialogState.Success) {
                    sendEvent(CreditBalanceRefundEvent.NavigateBackWithRefresh)
                } else {
                    mutableStateFlow.update { it.copy(dialogState = null) }
                }
            }
            is CreditBalanceRefundAction.OnRetry -> {
                lastSubmitRequest?.let { submitRefund(it) }
            }
            is CreditBalanceRefundAction.OnSubmitRefund -> {
                lastSubmitRequest = action.request
                submitRefund(action.request)
            }
        }
    }

    private fun submitRefund(request: CreditBalanceRefundRequest) {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(dialogState = CreditBalanceRefundState.DialogState.Loading) }

            when (val result = repository.submitRefund(route.loanId, request)) {
                is DataState.Loading -> Unit
                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = CreditBalanceRefundState.DialogState.Success(
                                transactionId = result.data.resourceId?.toString().orEmpty(),
                            ),
                        )
                    }
                }
                is DataState.Error -> {
                    mutableStateFlow.update {
                        val isNetworkError = result.message == "Network not available" ||
                            result.message.contains("Unable to resolve host", ignoreCase = true) ||
                            result.message.contains("Failed to connect", ignoreCase = true)

                        if (isNetworkError) {
                            it.copy(
                                dialogState = CreditBalanceRefundState.DialogState.Error(
                                    messageRes = Res.string.feature_error_network_not_available,
                                ),
                            )
                        } else {
                            it.copy(dialogState = CreditBalanceRefundState.DialogState.Error(message = result.message))
                        }
                    }
                }
            }
        }
    }
}
