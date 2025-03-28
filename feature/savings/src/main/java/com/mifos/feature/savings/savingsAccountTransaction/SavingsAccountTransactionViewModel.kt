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
import com.google.gson.Gson
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.SavingsAccountTransactionRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.model.objects.account.saving.SavingsAccountTransactionResponse
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.accounts.savings.SavingsTransactionData
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplateEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 13/08/23.
 */
class SavingsAccountTransactionViewModel(
//    private val getSavingsAccountTransactionTemplateUseCase: GetSavingsAccountTransactionTemplateUseCase,
//    private val processTransactionUseCase: ProcessTransactionUseCase,
//    private val getSavingsAccountTransactionUseCase: GetSavingsAccountTransactionUseCase,
    private val prefManager: PrefManager,
    private val repository: SavingsAccountTransactionRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val arg = savedStateHandle.getStateFlow(key = "arg", initialValue = "")
    private val savingsTransactionData: SavingsTransactionData =
        Gson().fromJson(arg.value, SavingsTransactionData::class.java)

    val accountId = savingsTransactionData.savingsAccountWithAssociations.id
    val savingsAccountNumber = savingsTransactionData.savingsAccountWithAssociations.accountNo
    val clientName = savingsTransactionData.savingsAccountWithAssociations.clientName
    val transactionType = savingsTransactionData.transactionType
    private val savingsAccountType = savingsTransactionData.depositType

    private val _savingsAccountTransactionUiState =
        MutableStateFlow<SavingsAccountTransactionUiState>(SavingsAccountTransactionUiState.ShowProgressbar)
    val savingsAccountTransactionUiState: StateFlow<SavingsAccountTransactionUiState> get() = _savingsAccountTransactionUiState

    fun setUserOffline() {
        prefManager.userStatus = Constants.USER_OFFLINE
    }

    fun loadSavingAccountTemplate() {
        viewModelScope.launch {
            if (accountId != null) {
                _savingsAccountTransactionUiState.value =
                    SavingsAccountTransactionUiState.ShowProgressbar

                repository.getSavingsAccountTransactionTemplate(
                    savingsAccountType?.endpoint,
                    accountId,
                    transactionType,
                ).catch {
                    _savingsAccountTransactionUiState.value =
                        SavingsAccountTransactionUiState.ShowError(it.message.toString())
                }.collect { template ->
                    _savingsAccountTransactionUiState.value =
                        SavingsAccountTransactionUiState.ShowSavingAccountTemplate(
                            template ?: SavingsAccountTransactionTemplateEntity(),
                        )
                }
            }
        }
    }

    fun processTransaction(request: SavingsAccountTransactionRequestEntity) {
        viewModelScope.launch {
            if (accountId != null) {
                _savingsAccountTransactionUiState.value =
                    SavingsAccountTransactionUiState.ShowProgressbar

                repository.processTransaction(
                    savingsAccountType?.endpoint,
                    accountId,
                    transactionType,
                    request,
                ).catch {
                    _savingsAccountTransactionUiState.value =
                        SavingsAccountTransactionUiState.ShowError(it.message.toString())
                }.collect {
                    _savingsAccountTransactionUiState.value =
                        SavingsAccountTransactionUiState.ShowTransactionSuccessfullyDone(
                            it ?: SavingsAccountTransactionResponse(),
                        )
                }
            }
        }
    }

    fun checkInDatabaseSavingAccountTransaction() {
        viewModelScope.launch {
            if (accountId != null) {
                _savingsAccountTransactionUiState.value =
                    SavingsAccountTransactionUiState.ShowProgressbar

                repository.getSavingsAccountTransaction(accountId)
                    .catch {
                        _savingsAccountTransactionUiState.value =
                            SavingsAccountTransactionUiState.ShowError(it.message.toString())
                    }.collect { savings ->
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
