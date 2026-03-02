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
import androidclient.feature.loan.generated.resources.feature_loan_must_select_account
import androidclient.feature.loan.generated.resources.feature_loan_must_select_account_type
import androidclient.feature.loan.generated.resources.feature_loan_must_select_client
import androidclient.feature.loan.generated.resources.feature_loan_must_select_office
import androidclient.feature.loan.generated.resources.feature_loan_transfer_amount_can_not_be_zero
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.data.repository.AmountTransferRepository
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.model.objects.account.loan.transfer.AccountTransferRequest
import com.mifos.core.model.objects.account.loan.transfer.AccountTypeOption
import com.mifos.core.model.objects.account.loan.transfer.ClientOption
import com.mifos.core.model.objects.account.loan.transfer.OfficeOption
import com.mifos.core.model.objects.account.loan.transfer.AccountOption
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

class AmountTransferViewModel(
    savedStateHandle: SavedStateHandle,
    private val clientRepo: ClientDetailsRepository,
    private val repository: AmountTransferRepository,
) : BaseViewModel<AmountTransferUiState, AmountTransferEvent, AmountTransferAction>(
    initialState = AmountTransferUiState(),
) {
    private val route = savedStateHandle.toRoute<AmountTransferScreenRoute>()

    init {
        fetchClientDetails(route.fromClientId)
    }

    override fun handleAction(action: AmountTransferAction) {
        when (action) {
            is AmountTransferAction.OnAccountChange -> {
                mutableStateFlow.update {
                    it.copy(
                        accountId = action.id,
                        selectedAccountName = action.name,
                        accountIdError = null,
                    )
                }
            }

            is AmountTransferAction.OnAccountTypeChange -> {
                mutableStateFlow.update {
                    it.copy(
                        accountTypeId = action.id,
                        selectedAccountType = action.name,
                        accountTypeIdError = null,
                    )
                }
                // Fetch accounts when account type is selected
                fetchTemplateWithDependencies()
            }

            is AmountTransferAction.OnAmountChange -> mutableStateFlow.update {
                it.copy(amount = action.amount, amountError = null)
            }

            is AmountTransferAction.OnClientChange -> {
                mutableStateFlow.update {
                    val clientId = it.clients.getOrNull(action.index)?.id
                    it.copy(
                        selectedClientId = clientId,
                        selectedClientName = action.name,
                        selectedClientIdError = null,
                        // Reset account type and account when client changes
                        accountTypeId = null,
                        selectedAccountType = "",
                        accountTypeIdError = null,
                        accountId = null,
                        selectedAccountName = "",
                        accountIdError = null,
                    )
                }
                // Fetch account types and accounts when client is selected
                fetchTemplateWithDependencies()
            }

            is AmountTransferAction.OnDescriptionChange -> {
                mutableStateFlow.update {
                    it.copy(
                        description = action.description,
                        descriptionError = null,
                    )
                }
            }

            is AmountTransferAction.OnOfficeChanged -> {
                mutableStateFlow.update { currentState ->
                    val officeId = currentState.offices.getOrNull(action.index)?.id
                    currentState.copy(
                        selectedOfficeId = officeId,
                        selectedOfficeName = action.name,
                        selectedOfficeIdError = null,
                        // Reset all dependent fields when office changes
                        selectedClientId = null,
                        selectedClientName = "",
                        selectedClientIdError = null,
                        accountTypeId = null,
                        selectedAccountType = "",
                        accountTypeIdError = null,
                        accountId = null,
                        selectedAccountName = "",
                        accountIdError = null,
                        clients = emptyList(),
                        accountTypes = emptyList(),
                        accounts = emptyList(),
                    )
                }
                // Fetch clients when office is selected
                fetchTemplateWithDependencies()
            }

            AmountTransferAction.OnTransferClicked -> validateFields()
            AmountTransferAction.OnRetryClick -> submitTransfer()
            AmountTransferAction.OnRetryFetching -> fetchClientDetails(route.fromClientId)
            AmountTransferAction.CloseDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
                sendEvent(AmountTransferEvent.NavigateBack)
            }
        }
    }

    private fun fetchClientDetails(clientId: Int) {
        mutableStateFlow.update { it.copy(dialogState = AmountTransferUiState.DialogState.Loading) }
        viewModelScope.launch {
            try {
                val client = clientRepo.getClient(clientId)
                mutableStateFlow.update {
                    it.copy(
                        fromClientName = client.displayName,
                        fromOfficeName = client.officeName,
                        fromAccountNumber = route.fromAccountNumber,
                        currency = route.currency ?: "",
                    )
                }

                // Fetch initial template (offices and account types)
                fetchInitialTemplate()
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = AmountTransferUiState.DialogState.Error(e.message.toString()),
                    )
                }
            }
        }
    }

    private fun fetchInitialTemplate() {
        viewModelScope.launch {
            repository.getAccountTransferTemplate(
                fromOfficeId = route.fromOfficeId,
                fromClientId = route.fromClientId,
                fromAccountType = route.fromAccountType,
                fromAccountId = route.fromAccountId,
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = AmountTransferUiState.DialogState.Loading)
                        }
                    }

                    is DataState.Success -> {
                        val template = dataState.data
                        mutableStateFlow.update { state ->
                            state.copy(
                                dialogState = null,
                                offices = template.toOfficeOptions.sortedBy { it.name },
                                accountTypes = template.toAccountTypeOptions.sortedBy { it.value },
                            )
                        }
                    }

                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = AmountTransferUiState.DialogState.Error(
                                    dataState.message,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun fetchTemplateWithDependencies() {
        val currentState = state

        // Only fetch if we have at least an office selected
        if (currentState.selectedOfficeId == null) return

        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(dialogState = AmountTransferUiState.DialogState.Loading)
            }

            repository.getAccountTransferTemplate(
                fromOfficeId = route.fromOfficeId,
                fromClientId = route.fromClientId,
                fromAccountType = route.fromAccountType,
                fromAccountId = route.fromAccountId,
                toOfficeId = currentState.selectedOfficeId,
                toClientId = currentState.selectedClientId,
                toAccountType = currentState.accountTypeId,
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(dialogState = AmountTransferUiState.DialogState.Loading)
                        }
                    }

                    is DataState.Success -> {
                        val template = dataState.data
                        mutableStateFlow.update { state ->
                            // If we have clients from the template, use them
                            val newClients = if (template.toClientOptions.isNotEmpty()) {
                                template.toClientOptions.sortedBy { it.displayName }
                            } else {
                                state.clients
                            }

                            // If we have account types from the template, use them
                            val newAccountTypes = if (template.toAccountTypeOptions.isNotEmpty()) {
                                template.toAccountTypeOptions.sortedBy { it.value }
                            } else {
                                state.accountTypes
                            }

                            // If we have accounts from the template, use them
                            val newAccounts = if (template.toAccountOptions.isNotEmpty()) {
                                template.toAccountOptions.sortedBy { it.accountNo }
                            } else {
                                state.accounts
                            }

                            state.copy(
                                dialogState = null,
                                offices = if (template.toOfficeOptions.isNotEmpty()) {
                                    template.toOfficeOptions.sortedBy { it.name }
                                } else {
                                    state.offices
                                },
                                clients = newClients,
                                accountTypes = newAccountTypes,
                                accounts = newAccounts,
                            )
                        }
                    }

                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                dialogState = AmountTransferUiState.DialogState.Error(
                                    dataState.message,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun validateFields() {
        var isValid = true

        // Validate amount
        if (state.amount.toDoubleOrNull() == null) {
            mutableStateFlow.update {
                it.copy(amountError = Res.string.feature_loan_invalid_amount)
            }
            isValid = false
        } else if (state.amount.toDouble() <= 0.00) {
            mutableStateFlow.update {
                it.copy(amountError = Res.string.feature_loan_transfer_amount_can_not_be_zero)
            }
            isValid = false
        } else {
            mutableStateFlow.update { it.copy(amountError = null) }
        }

        // Validate description
        if (state.description.trim().isEmpty()) {
            mutableStateFlow.update {
                it.copy(descriptionError = Res.string.feature_loan_description_can_not_be_empty)
            }
            isValid = false
        } else {
            mutableStateFlow.update { it.copy(descriptionError = null) }
        }

        // Validate office selection
        if (state.selectedOfficeId == null) {
            mutableStateFlow.update {
                it.copy(selectedOfficeIdError = Res.string.feature_loan_must_select_office)
            }
            isValid = false
        } else {
            mutableStateFlow.update { it.copy(selectedOfficeIdError = null) }
        }

        // Validate client selection
        if (state.selectedClientId == null) {
            mutableStateFlow.update {
                it.copy(selectedClientIdError = Res.string.feature_loan_must_select_client)
            }
            isValid = false
        } else {
            mutableStateFlow.update { it.copy(selectedClientIdError = null) }
        }

        // Validate account type selection
        if (state.accountTypeId == null) {
            mutableStateFlow.update {
                it.copy(accountTypeIdError = Res.string.feature_loan_must_select_account_type)
            }
            isValid = false
        } else {
            mutableStateFlow.update { it.copy(accountTypeIdError = null) }
        }

        // Validate account selection
        if (state.accountId == null) {
            mutableStateFlow.update {
                it.copy(accountIdError = Res.string.feature_loan_must_select_account)
            }
            isValid = false
        } else {
            mutableStateFlow.update { it.copy(accountIdError = null) }
        }

        if (isValid) {
            submitTransfer()
        }
    }

    private fun submitTransfer() {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(dialogState = AmountTransferUiState.DialogState.Loading) }

            val request = AccountTransferRequest(
                fromOfficeId = route.fromOfficeId,
                fromClientId = route.fromClientId,
                fromAccountType = route.fromAccountType,
                fromAccountId = route.fromAccountId,
                toOfficeId = state.selectedOfficeId!!,
                toClientId = state.selectedClientId!!,
                toAccountType = state.accountTypeId!!,
                toAccountId = state.accountId!!,
                transferDate = DateHelper.formattedFullDate,
                transferAmount = state.amount.toDouble(),
                transferDescription = state.description,
            )

            val result = repository.submitAccountTransfer(request)

            result.fold(
                onSuccess = {
                    mutableStateFlow.update {
                        it.copy(dialogState = AmountTransferUiState.DialogState.TransferStatus("Transfer completed successfully"))
                    }
                },
                onFailure = { error ->
                    mutableStateFlow.update {
                        it.copy(
                            dialogState = AmountTransferUiState.DialogState.Error(
                                error.message ?: "Transfer failed",
                            ),
                        )
                    }
                },
            )
        }
    }
}

data class AmountTransferUiState(
    val dialogState: DialogState? = null,

    val amountError: StringResource? = null,
    val descriptionError: StringResource? = null,
    val selectedOfficeIdError: StringResource? = null,
    val selectedClientIdError: StringResource? = null,
    val accountTypeIdError: StringResource? = null,
    val accountIdError: StringResource? = null,

    // Source account details
    val fromOfficeName: String? = null,
    val fromClientName: String? = null,
    val fromAccountNumber: String? = null,
    val fromAccountTypeName: String = "Loan account",

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

    // Template data - to destination options
    val offices: List<OfficeOption> = emptyList(),
    val clients: List<ClientOption> = emptyList(),
    val accountTypes: List<AccountTypeOption> = emptyList(),
    val accounts: List<AccountOption> = emptyList(),
    val currency: String = "",
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data class TransferStatus(val message: String) : DialogState
        data object Loading : DialogState
    }
}

sealed interface AmountTransferAction {
    data class OnOfficeChanged(val index: Int, val name: String) : AmountTransferAction
    data class OnClientChange(val index: Int, val name: String) : AmountTransferAction
    data class OnAccountTypeChange(val id: Int, val name: String) : AmountTransferAction
    data class OnAccountChange(val id: Int, val name: String) : AmountTransferAction
    data class OnAmountChange(val amount: String) : AmountTransferAction
    data class OnDescriptionChange(val description: String) : AmountTransferAction
    data object OnTransferClicked : AmountTransferAction
    data object OnRetryClick : AmountTransferAction
    data object CloseDialog : AmountTransferAction
    data object OnRetryFetching : AmountTransferAction
}

sealed interface AmountTransferEvent {
    data object NavigateBack : AmountTransferEvent
}
