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
import com.mifos.core.common.utils.Resource
import com.mifos.core.datastore.PrefManager
import com.mifos.core.domain.useCases.GetSavingsAccountTransactionTemplateUseCase
import com.mifos.core.domain.useCases.GetSavingsAccountTransactionUseCase
import com.mifos.core.domain.useCases.ProcessTransactionUseCase
import com.mifos.core.entity.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.entity.accounts.savings.SavingsTransactionData
import com.mifos.core.entity.templates.savings.SavingsAccountTransactionTemplate
import com.mifos.core.objects.account.saving.SavingsAccountTransactionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
@HiltViewModel
class SavingsAccountTransactionViewModel @Inject constructor(
    private val getSavingsAccountTransactionTemplateUseCase: GetSavingsAccountTransactionTemplateUseCase,
    private val processTransactionUseCase: ProcessTransactionUseCase,
    private val getSavingsAccountTransactionUseCase: GetSavingsAccountTransactionUseCase,
    private val prefManager: PrefManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val arg = savedStateHandle.getStateFlow(key = "arg", initialValue = "")
    private val savingsTransactionData: SavingsTransactionData = Gson().fromJson(arg.value, SavingsTransactionData::class.java)

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

    fun loadSavingAccountTemplate() =
        accountId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                getSavingsAccountTransactionTemplateUseCase(
                    savingsAccountType?.endpoint,
                    it,
                    transactionType,
                ).collect { result ->
                    when (result) {
                        is Resource.Error ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowError(result.message.toString())

                        is Resource.Loading ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowProgressbar

                        is Resource.Success ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowSavingAccountTemplate(
                                    result.data ?: SavingsAccountTransactionTemplate(),
                                )
                    }
                }
            }
        }

    fun processTransaction(request: SavingsAccountTransactionRequest) =
        accountId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                processTransactionUseCase(
                    savingsAccountType?.endpoint,
                    it,
                    transactionType,
                    request,
                ).collect { result ->
                    when (result) {
                        is Resource.Error ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowError(result.message.toString())

                        is Resource.Loading ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowProgressbar

                        is Resource.Success ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowTransactionSuccessfullyDone(
                                    result.data ?: SavingsAccountTransactionResponse(),
                                )
                    }
                }
            }
        }

    fun checkInDatabaseSavingAccountTransaction() =
        viewModelScope.launch(Dispatchers.IO) {
            accountId?.let {
                getSavingsAccountTransactionUseCase(it).collect { result ->
                    when (result) {
                        is Resource.Error ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowError(result.message.toString())

                        is Resource.Loading ->
                            _savingsAccountTransactionUiState.value =
                                SavingsAccountTransactionUiState.ShowProgressbar

                        is Resource.Success -> {
                            if (result.data != null) {
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
