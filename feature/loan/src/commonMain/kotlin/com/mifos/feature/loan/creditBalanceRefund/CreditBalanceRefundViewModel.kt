/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.creditBalanceRefund

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_error_network_not_available
import androidclient.feature.loan.generated.resources.feature_loan_profile_error_details_not_found
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.util.NetworkUnavailableException
import com.mifos.core.domain.useCases.CreditBalanceRefundUseCase
import com.mifos.core.model.objects.account.loan.creditBalanceRefund.CreditBalanceRefundInput
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreditBalanceRefundViewModel(
    savedStateHandle: SavedStateHandle,
    private val useCase: CreditBalanceRefundUseCase,
) : BaseViewModel<CreditBalanceRefundState, CreditBalanceRefundEvent, CreditBalanceRefundAction>(
    initialState = CreditBalanceRefundState(),
) {

    private val route = savedStateHandle.toRoute<CreditBalanceRefundScreenRoute>()

    private var lastSubmitInput: CreditBalanceRefundInput? = null

    init {
        mutableStateFlow.update { it.copy(loanId = route.loanId) }
        observeLoanDetails()
    }

    private fun observeLoanDetails() {
        viewModelScope.launch {
            if (state.clientName == null && state.dialogState !is CreditBalanceRefundState.DialogState.Success) {
                mutableStateFlow.update { it.copy(dialogState = CreditBalanceRefundState.DialogState.Loading) }
            }

            when (val result = useCase.getLoanRefundDetails(route.loanId)) {
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
                                networkAvailable = true,
                                dialogState = it.dialogState as? CreditBalanceRefundState.DialogState.Success,
                                clientName = loan.clientName,
                                loanAccountNumber = loan.accountNo,
                                overpaidAmount = loan.totalOverpaid,
                                transactionDate = loan.transactionDate,
                                currencyCode = loan.currencyCode,
                                decimalPlaces = loan.decimalPlaces,
                            )
                        }
                    }
                }
                is DataState.Error -> {
                    val isNetworkError = result.exception is NetworkUnavailableException
                    mutableStateFlow.update {
                        if (isNetworkError) {
                            it.copy(
                                networkAvailable = false,
                                dialogState = CreditBalanceRefundState.DialogState.Error(
                                    messageRes = Res.string.feature_error_network_not_available,
                                ),
                            )
                        } else {
                            it.copy(
                                dialogState = CreditBalanceRefundState.DialogState.Error(
                                    message = result.message,
                                ),
                            )
                        }
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
                lastSubmitInput?.let { submitRefund(it) }
            }
            is CreditBalanceRefundAction.OnSubmitRefund -> {
                lastSubmitInput = action.input
                submitRefund(action.input)
            }
        }
    }

    private fun submitRefund(input: CreditBalanceRefundInput) {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(dialogState = CreditBalanceRefundState.DialogState.Loading) }

            when (val result = useCase.submitRefund(route.loanId, input)) {
                is DataState.Loading -> Unit
                is DataState.Success -> {
                    val response = result.data
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = CreditBalanceRefundState.DialogState.Success(
                                transactionId = response.transactionId.toString(),
                            ),
                        )
                    }
                }
                is DataState.Error -> {
                    mutableStateFlow.update {
                        val isNetworkError = result.exception is NetworkUnavailableException

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

data class CreditBalanceRefundState(
    val dialogState: DialogState? = null,
    val loanId: Int = 0,
    val clientName: String? = null,
    val loanAccountNumber: String = "",
    val overpaidAmount: Double = 0.0,
    val transactionDate: String = "",
    val currencyCode: String? = null,
    val decimalPlaces: Int? = null,
    val networkAvailable: Boolean = true,
) {
    sealed interface DialogState {
        data object Loading : DialogState

        data class Error(
            val message: String? = null,
            val messageRes: org.jetbrains.compose.resources.StringResource? = null,
        ) : DialogState

        data class Success(val transactionId: String) : DialogState
    }
}

sealed interface CreditBalanceRefundAction {
    data object NavigateBack : CreditBalanceRefundAction

    data object OnRetry : CreditBalanceRefundAction

    data object OnDismissDialog : CreditBalanceRefundAction

    data class OnSubmitRefund(val input: CreditBalanceRefundInput) : CreditBalanceRefundAction
}

sealed interface CreditBalanceRefundEvent {
    data object NavigateBack : CreditBalanceRefundEvent
    data object NavigateBackWithRefresh : CreditBalanceRefundEvent
}
