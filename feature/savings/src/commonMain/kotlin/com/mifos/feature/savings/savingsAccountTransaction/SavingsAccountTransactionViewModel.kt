/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.savings.savingsAccountTransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.SavingsAccountTransactionRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.account.saving.SavingsAccountTransactionResponse
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.accounts.savings.SavingsTransactionData
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplateEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class SavingsAccountTransactionViewModel(
    private val prefManager: UserPreferencesRepository,
    private val repository: SavingsAccountTransactionRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val arg = savedStateHandle.getStateFlow(key = "arg", initialValue = "")
    private val savingsTransactionData: SavingsTransactionData =
        Json.decodeFromString<SavingsTransactionData>(arg.value)

    val accountId = savingsTransactionData.savingsAccountWithAssociations.id
    val savingsAccountNumber = savingsTransactionData.savingsAccountWithAssociations.accountNo
    val clientName = savingsTransactionData.savingsAccountWithAssociations.clientName
    val transactionType = savingsTransactionData.transactionType
    private val savingsAccountType = savingsTransactionData.depositType

    private val _savingsAccountTransactionUiState =
        MutableStateFlow<SavingsAccountTransactionUiState>(SavingsAccountTransactionUiState.ShowProgressbar)
    val savingsAccountTransactionUiState: StateFlow<SavingsAccountTransactionUiState> get() = _savingsAccountTransactionUiState.asStateFlow()

    fun setUserOffline() {
        viewModelScope.launch {
            prefManager.updateUserStatus(Constants.USER_OFFLINE)
        }
    }

    fun loadSavingAccountTemplate() {
        viewModelScope.launch {
            if (accountId != null) {
                repository.getSavingsAccountTransactionTemplate(
                    savingsAccountType?.endpoint!!,
                    accountId,
                    transactionType,
                ).collect { state ->
                    when (state) {
                        is DataState.Error ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowError(state.message)

                        DataState.Loading ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowProgressbar

                        is DataState.Success ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowSavingAccountTemplate(
                                    state.data ?: SavingsAccountTransactionTemplateEntity(),
                                )
                    }
                }
            }
        }
    }

    fun processTransaction(request: SavingsAccountTransactionRequestEntity) {
        viewModelScope.launch {
            if (accountId != null) {
                repository.processTransaction(
                    savingsAccountType?.endpoint!!,
                    accountId,
                    transactionType,
                    request,
                ).collect { dataState ->
                    when (dataState) {
                        is DataState.Error ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowError(dataState.message)

                        DataState.Loading ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowProgressbar

                        is DataState.Success ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowTransactionSuccessfullyDone(
                                    dataState.data ?: SavingsAccountTransactionResponse(),
                                )
                    }
                }
            }
        }
    }

    fun checkInDatabaseSavingAccountTransaction() {
        viewModelScope.launch {
            if (accountId != null) {
                repository.getSavingsAccountTransaction(accountId)
                    .collect { dataState ->
                        when (dataState) {
                            is DataState.Error -> {
                                _savingsAccountTransactionUiState.value =
                                    SavingsAccountTransactionUiState.ShowError(dataState.message)
                            }

                            DataState.Loading ->
                                _savingsAccountTransactionUiState.value =
                                    SavingsAccountTransactionUiState.ShowProgressbar

                            is DataState.Success -> {
                                val savings = dataState.data
                                if (savings != null) {
                                    _savingsAccountTransactionUiState.value =
                                        SavingsAccountTransactionUiState.ShowSavingAccountTransactionExistInDatabase
                                } else {
                                    _savingsAccountTransactionUiState.value =
                                        SavingsAccountTransactionUiState.ShowSavingAccountTransactionDoesNotExistInDatabase
                                }
                            }
                        }
                    }
            }
        }
    }
}
