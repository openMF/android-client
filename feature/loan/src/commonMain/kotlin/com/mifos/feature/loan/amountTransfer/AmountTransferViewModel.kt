/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.amountTransfer

import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.feature_loan_description_can_not_be_empty
import androidclient.feature.loan.generated.resources.feature_loan_invalid_amount
import androidclient.feature.loan.generated.resources.feature_loan_transfer_amount_can_not_be_zero
import androidx.lifecycle.viewModelScope
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.loan.amountTransfer.AmountTransferUiState.DialogState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class AmountTransferViewModel() :
    BaseViewModel<AmountTransferUiState, AmountTransferEvent, AmountTransferAction>(
        initialState = AmountTransferUiState(
            dialogState = DialogState.Loading,
        ),
    ) {
    override fun handleAction(action: AmountTransferAction) {
        when (action) {
            is AmountTransferAction.OnAccountChange -> {
                mutableStateFlow.update {
                    it.copy(
                        accountId = action.id,
                        selectedAccountName = action.name,
                    )
                }
            }

            is AmountTransferAction.OnAccountTypeChange -> {
                mutableStateFlow.update {
                    it.copy(
                        accountTypeId = action.id,
                        selectedAccountType = action.name,
                    )
                }
            }

            is AmountTransferAction.OnAmountChange -> mutableStateFlow.update {
                it.copy(amount = action.amount)
            }

            is AmountTransferAction.OnClientChange -> {
                mutableStateFlow.update {
                    it.copy(
                        selectedClientId = action.id,
                        selectedClientName = action.name,
                    )
                }
            }

            is AmountTransferAction.OnDescriptionChange -> {
                mutableStateFlow.update { it.copy(description = action.description) }
            }

            is AmountTransferAction.OnOfficeChanged -> {
                mutableStateFlow.update {
                    it.copy(
                        selectedOfficeId = action.id,
                        selectedOfficeName = action.name,
                    )
                }
            }

            AmountTransferAction.OnTransferClicked -> validateFields()
            AmountTransferAction.OnRetryClick -> fetchTemplate()
        }
    }

    init {
        fetchTemplate()
    }

    private fun fetchTemplate() {
        mutableStateFlow.update { it.copy(dialogState = DialogState.Loading) }
        viewModelScope.launch {
            delay(2000) // todo update whole block of code
            mutableStateFlow.update {
                it.copy(
                    dialogState = null,
                )
            }
        }
    }

    private fun validateFields() {
        // validate first then submit
        if (state.amount.toDoubleOrNull() == null) {
            mutableStateFlow.update {
                it.copy(amountError = Res.string.feature_loan_invalid_amount)
            }
        } else if (state.amount.toDouble() <= 0.00) {
            mutableStateFlow.update {
                it.copy(
                    amountError = Res.string.feature_loan_transfer_amount_can_not_be_zero,
                )
            }
        } else {
            mutableStateFlow.update { it.copy(amountError = null) }
        }

        if (state.description.trim().isEmpty()) {
            mutableStateFlow.update {
                it.copy(
                    descriptionError = Res.string.feature_loan_description_can_not_be_empty,
                )
            }
        } else {
            mutableStateFlow.update { it.copy(descriptionError = null) }
        }

        if (state.amountError == null && state.descriptionError == null) {
            onTransferClicked()
        }
    }

    private fun onTransferClicked() {
    }
}

data class AmountTransferUiState(
    val dialogState: DialogState? = null,
    val amountError: StringResource? = null,
    val descriptionError: StringResource? = null,

    val selectedOfficeName: String = "",
    val selectedClientName: String = "",
    val selectedAccountType: String = "",
    val selectedAccountName: String = "",

    val selectedOfficeId: Int? = null,
    val selectedClientId: Int? = null,
    val accountTypeId: Int? = null,
    val accountId: Int? = null,
    val description: String = "",
    val amount: String = "0.00",
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
}

sealed interface AmountTransferAction {
    data class OnOfficeChanged(val id: Int, val name: String) : AmountTransferAction
    data class OnClientChange(val id: Int, val name: String) : AmountTransferAction
    data class OnAccountTypeChange(val id: Int, val name: String) : AmountTransferAction
    data class OnAccountChange(val id: Int, val name: String) : AmountTransferAction
    data class OnAmountChange(val amount: String) : AmountTransferAction
    data class OnDescriptionChange(val description: String) : AmountTransferAction
    data object OnTransferClicked : AmountTransferAction
    data object OnRetryClick : AmountTransferAction
}

sealed interface AmountTransferEvent {
    data object NavigateBack : AmountTransferEvent
}
