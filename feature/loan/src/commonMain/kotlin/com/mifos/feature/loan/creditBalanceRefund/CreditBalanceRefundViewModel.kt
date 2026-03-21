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
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.room.entities.accounts.loans.CreditBalanceRefundRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Credit Balance Refund screen.
 * Implements the MVI pattern via [BaseViewModel].
 * Handles network connectivity, loading loan details, and submitting refunds.
 */
class CreditBalanceRefundViewModel(
    savedStateHandle: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val repository: CreditBalanceRefundRepository,
) : BaseViewModel<CreditBalanceRefundState, CreditBalanceRefundEvent, CreditBalanceRefundAction>(
    initialState = CreditBalanceRefundState(),
) {

    private val route = savedStateHandle.toRoute<CreditBalanceRefundScreenRoute>()
    private var loadJob: Job? = null

    /** Cached refund request for retry operations */
    private var lastSubmitRequest: CreditBalanceRefundRequest? = null

    init {
        mutableStateFlow.update { it.copy(loanId = route.loanId) }
        observeNetworkAndLoad()
    }

    /**
     * Observes the global network monitor. When online, triggers the initial loan fetch.
     * If the app starts offline, shows a network error immediately.
     */
    private fun observeNetworkAndLoad() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(networkAvailable = isConnected) }

                if (isConnected) {
                    if (state.clientName == null && state.dialogState !is CreditBalanceRefundState.DialogState.Success) {
                        loadLoanDetails()
                    }
                } else if (state.clientName == null) {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = CreditBalanceRefundState.DialogState.Error(
                                messageRes = Res.string.feature_error_network_not_available,
                            ),
                        )
                    }
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
                if (!state.networkAvailable) {
                    viewModelScope.launch {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = CreditBalanceRefundState.DialogState.Error(
                                    messageRes = Res.string.feature_error_network_not_available,
                                ),
                            )
                        }
                    }
                    return
                }

                if (state.clientName == null) {
                    loadLoanDetails()
                } else {
                    lastSubmitRequest?.let { submitRefund(it) }
                }
            }
            is CreditBalanceRefundAction.OnSubmitRefund -> {
                lastSubmitRequest = action.request
                if (!state.networkAvailable) {
                    viewModelScope.launch {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = CreditBalanceRefundState.DialogState.Error(
                                    messageRes = Res.string.feature_error_network_not_available,
                                ),
                            )
                        }
                    }
                    return
                }
                submitRefund(action.request)
            }
            is CreditBalanceRefundAction.LoanLoadRetry -> {
                loadLoanDetails()
            }
        }
    }

    private fun loadLoanDetails() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            mutableStateFlow.update { it.copy(dialogState = CreditBalanceRefundState.DialogState.Loading) }

            repository.getLoanById(route.loanId).collect { result ->
                when (result) {
                    is DataState.Loading -> Unit
                    is DataState.Success -> {
                        val loan = result.data
                        if (loan == null) {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = CreditBalanceRefundState.DialogState.Error(
                                        messageRes = Res.string.feature_loan_profile_error_details_not_found,
                                    ),
                                )
                            }
                        } else {
                            mutableStateFlow.update {
                                it.copy(
                                    dialogState = null,
                                    clientName = loan.clientName,
                                    loanAccountNumber = loan.accountNo,
                                    overpaidAmount = loan.totalOverpaid,
                                    currencyCode = loan.currency.code,
                                    decimalPlaces = loan.currency.decimalPlaces,
                                )
                            }
                        }
                    }
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            val isNetworkError = !it.networkAvailable ||
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

    private fun submitRefund(request: CreditBalanceRefundRequest) {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(dialogState = CreditBalanceRefundState.DialogState.Loading) }

            when (val result = repository.submitRefund(route.loanId, request)) {
                is DataState.Loading -> Unit
                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = CreditBalanceRefundState.DialogState.Success(
                                transactionId = result.data.resourceId.toString(),
                            ),
                        )
                    }
                }
                is DataState.Error -> {
                    mutableStateFlow.update {
                        val isNetworkError = !it.networkAvailable ||
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
