/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncSavingsAccountTransaction

import androidclient.feature.offline.generated.resources.Res
import androidclient.feature.offline.generated.resources.feature_offline_error_fix_before_sync
import androidclient.feature.offline.generated.resources.feature_offline_failed_to_load_paymentoptions
import androidclient.feature.offline.generated.resources.feature_offline_failed_to_load_savingaccounttransaction
import androidclient.feature.offline.generated.resources.feature_offline_failed_to_update_list
import androidclient.feature.offline.generated.resources.feature_offline_failed_to_update_savingsaccount
import androidclient.feature.offline.generated.resources.feature_offline_no_transaction_to_sync
import androidclient.feature.offline.generated.resources.feature_offline_nothing_to_sync
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.SyncSavingsAccountTransactionRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncSavingsAccountTransactionViewModel(
//    private val processTransactionUseCase: ProcessTransactionUseCase,
    private val repository: SyncSavingsAccountTransactionRepository,
    private val prefManager: UserPreferencesRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _syncSavingsAccountTransactionUiState =
        MutableStateFlow<SyncSavingsAccountTransactionUiState>(SyncSavingsAccountTransactionUiState.Loading)
    val syncSavingsAccountTransactionUiState: StateFlow<SyncSavingsAccountTransactionUiState>
        get() = _syncSavingsAccountTransactionUiState.asStateFlow()

    private var mPaymentTypeOptions: List<PaymentTypeOptionEntity> = emptyList()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val isNetworkAvailable = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun refreshTransactions() {
        _isRefreshing.value = true
        loadDatabaseSavingsAccountTransactions()
        loadPaymentTypeOption()
        _isRefreshing.value = false
    }

    private var mSavingsAccountTransactionRequests: MutableList<SavingsAccountTransactionRequestEntity> =
        ArrayList()

    private var mTransactionIndex = 0
    private var mTransactionsFailed = 0

    val userStatus: StateFlow<Boolean> = prefManager.userInfo
        .map { it.userStatus }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    fun syncSavingsAccountTransactions() {
        if (mSavingsAccountTransactionRequests.size != 0) {
            mTransactionIndex = 0
            checkErrorAndSync()
        } else {
            _syncSavingsAccountTransactionUiState.value =
                SyncSavingsAccountTransactionUiState.ShowError(Res.string.feature_offline_nothing_to_sync)
        }
    }

    /**
     * This Method Check, SavingsAccountTransactionRequest Error Message is null or not, If
     * error message will not null. It means that SavingsAccountTransactionRequest already tried to
     * synced but there is some error to sync that Transaction.
     * and If error message  is null. It means SavingsAccountTransactionRequest never synced before,
     * start syncing that SavingsAccountTransactionRequest.
     */
    private fun checkErrorAndSync() {
        for (i in mSavingsAccountTransactionRequests.indices) {
            if (mSavingsAccountTransactionRequests[i].errorMessage == null) {
                mTransactionIndex = i
                val savingAccountType = mSavingsAccountTransactionRequests[i].savingsAccountType
                val savingAccountId = mSavingsAccountTransactionRequests[i].savingAccountId
                val transactionType = mSavingsAccountTransactionRequests[i].transactionType
                if (savingAccountId != null) {
                    processTransaction(
                        savingAccountType,
                        savingAccountId,
                        transactionType,
                        mSavingsAccountTransactionRequests[i],
                    )
                }
                break
            } else if (checkTransactionsSyncBeforeOrNot()) {
                _syncSavingsAccountTransactionUiState.value =
                    SyncSavingsAccountTransactionUiState.ShowError(Res.string.feature_offline_error_fix_before_sync)
            }
        }
    }

    /**
     * This Method delete the SavingsAccountTransactionRequest from Database and load again
     * List<SavingsAccountTransactionRequest> and Update the UI.
     </SavingsAccountTransactionRequest> */
    fun showTransactionSyncSuccessfully() {
        mSavingsAccountTransactionRequests[mTransactionIndex].savingAccountId?.let {
            deleteAndUpdateSavingsAccountTransaction(
                it,
            )
        }
    }

    /**
     * This Method will be called whenever Transaction sync failed. This Method set the Error
     * message to the SavingsAccountTransactionRequest and update
     * SavingsAccountTransactionRequest into the Database
     *
     * @param errorMessage Server Error Message
     */
    fun showTransactionSyncFailed(errorMessage: String?) {
        val transaction =
            mSavingsAccountTransactionRequests[mTransactionIndex].copy(errorMessage = errorMessage)
        updateSavingsAccountTransaction(transaction)
    }

    /**
     * This Method will be called when Transaction will be sync successfully and deleted from
     * Database then This Method Start Syncing next Transaction.
     *
     * @param transaction SavingsAccountTransactionRequest
     */
    fun showTransactionUpdatedSuccessfully(transaction: SavingsAccountTransactionRequestEntity) {
        mSavingsAccountTransactionRequests[mTransactionIndex] = transaction
        updateUiState()
        mTransactionIndex += 1
        if (mSavingsAccountTransactionRequests.size != mTransactionIndex) {
            syncSavingsAccountTransactions()
        }
    }

    /**
     * This Method Update the UI. This Method will be called when sync transaction will be
     * deleted from Database and  load again Transaction from Database
     * List<SavingsAccountTransactionRequest>.
     *
     * @param transactions List<SavingsAccountTransactionRequest>
     </SavingsAccountTransactionRequest></SavingsAccountTransactionRequest> */
    fun showTransactionDeletedAndUpdated(transactions: MutableList<SavingsAccountTransactionRequestEntity>) {
        mTransactionIndex = 0
        mSavingsAccountTransactionRequests = transactions
        updateUiState()
        if (mSavingsAccountTransactionRequests.isNotEmpty()) {
            syncSavingsAccountTransactions()
        } else {
            _syncSavingsAccountTransactionUiState.value =
                SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions(Res.string.feature_offline_nothing_to_sync)
        }
    }

    private fun checkTransactionsSyncBeforeOrNot(): Boolean {
        mTransactionsFailed = mSavingsAccountTransactionRequests.count { it.errorMessage != null }
        return mTransactionsFailed == mSavingsAccountTransactionRequests.size
    }

    /**
     * This Method Load the List<SavingsAccountTransactionRequest> from
     * SavingsAccountTransactionRequest_Table and Update the UI
     </SavingsAccountTransactionRequest> */
    fun loadDatabaseSavingsAccountTransactions() {
        viewModelScope.launch {
            repository.allSavingsAccountTransactions()
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            _syncSavingsAccountTransactionUiState.value =
                                SyncSavingsAccountTransactionUiState.ShowError(Res.string.feature_offline_failed_to_load_savingaccounttransaction)
                        }

                        DataState.Loading ->
                            _syncSavingsAccountTransactionUiState.value =
                                SyncSavingsAccountTransactionUiState.Loading

                        is DataState.Success -> {
                            val transactions = dataState.data
                            if (transactions.isNotEmpty()) {
                                mSavingsAccountTransactionRequests = transactions.toMutableList()
                                updateUiState()
                            } else {
                                _syncSavingsAccountTransactionUiState.value =
                                    SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions(
                                        Res.string.feature_offline_no_transaction_to_sync,
                                    )
                            }
                        }
                    }
                }
        }
    }

    /**
     * THis Method Load the Payment Type Options from Database PaymentTypeOption_Table
     * and update the UI.
     */
    fun loadPaymentTypeOption() = viewModelScope.launch {
        repository.paymentTypeOption().collect { state ->
            when (state) {
                is DataState.Success -> {
                    mPaymentTypeOptions = state.data
                    updateUiState()
                }

                is DataState.Error -> {
                    _syncSavingsAccountTransactionUiState.value =
                        SyncSavingsAccountTransactionUiState.ShowError(Res.string.feature_offline_failed_to_load_paymentoptions)
                }

                is DataState.Loading -> {
                    _syncSavingsAccountTransactionUiState.value =
                        SyncSavingsAccountTransactionUiState.Loading
                }
            }
        }
    }

    private fun updateUiState() {
        if (mSavingsAccountTransactionRequests.isNotEmpty()) {
            _syncSavingsAccountTransactionUiState.value =
                SyncSavingsAccountTransactionUiState.ShowSavingsAccountTransactions(
                    mSavingsAccountTransactionRequests,
                    mPaymentTypeOptions,
                )
        } else {
            _syncSavingsAccountTransactionUiState.value =
                SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions(Res.string.feature_offline_no_transaction_to_sync)
        }
    }

    /**
     * This Method is for Sync Offline Saved SavingsAccountTransaction to the Server.
     * This method will be called when user will be in online mode and user's Internet connection
     * will be working well. If the Transaction will failed to sync then
     * updateSavingsAccountTransaction(SavingsAccountTransactionRequest request) save the developer
     * error message to Database with the failed Transaction. otherwise
     * deleteAndUpdateSavingsAccountTransaction(int savingsAccountId) delete the sync
     * Transaction from Database and load again Transaction List from
     * SavingsAccountTransactionRequest_Table and then sync next.
     *
     * @param type            SavingsAccount type
     * @param accountId       SavingsAccount Id
     * @param transactionType Transaction type
     * @param request         SavingsAccountTransactionRequest
     */
    private fun processTransaction(
        type: String?,
        accountId: Int,
        transactionType: String?,
        request: SavingsAccountTransactionRequestEntity?,
    ) = viewModelScope.launch {
        require(!type.isNullOrBlank()) { "Account type must not be null or blank" }
        requireNotNull(request) { "Request must not be null" }
        repository.processTransaction(
            type,
            accountId,
            transactionType,
            request,
        ).collect { state ->
            when (state) {
                is DataState.Error -> showTransactionSyncFailed(state.message)

                DataState.Loading ->
                    _syncSavingsAccountTransactionUiState.value =
                        SyncSavingsAccountTransactionUiState.Loading

                is DataState.Success -> showTransactionSyncSuccessfully()
            }
        }
    }

    /**
     * This Method delete the SavingsAccountTransactionRequest from the Database and load again
     * List<SavingsAccountTransactionRequest> from the SavingsAccountTransactionRequest_Table.
     * and returns the List<SavingsAccountTransactionRequest>.
     *
     * @param savingsAccountId SavingsAccountTransactionRequest's SavingsAccount Id
     </SavingsAccountTransactionRequest></SavingsAccountTransactionRequest> */
    private fun deleteAndUpdateSavingsAccountTransaction(savingsAccountId: Int) {
        viewModelScope.launch {
            repository.deleteAndUpdateTransactions(savingsAccountId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error ->
                            _syncSavingsAccountTransactionUiState.value =
                                SyncSavingsAccountTransactionUiState.ShowError(Res.string.feature_offline_failed_to_update_list)

                        DataState.Loading ->
                            _syncSavingsAccountTransactionUiState.value =
                                SyncSavingsAccountTransactionUiState.Loading

                        is DataState.Success -> {
                            showTransactionDeletedAndUpdated(
                                dataState.data.toMutableList(),
                            )
                        }
                    }
                }
        }
    }

    /**
     * This Method Update the SavingsAccountTransactionRequest in the Database. This will be called
     * whenever any transaction will be failed to sync then the sync developer error message will
     * be added to SavingsAccountTransactionRequest to update in Database.
     *
     * @param request SavingsAccountTransactionRequest
     */
    private fun updateSavingsAccountTransaction(request: SavingsAccountTransactionRequestEntity?) =
        viewModelScope.launch {
            _syncSavingsAccountTransactionUiState.value =
                SyncSavingsAccountTransactionUiState.Loading
            try {
                repository.updateLoanRepaymentTransaction(request!!)
                showTransactionUpdatedSuccessfully(request)
            } catch (e: Exception) {
                _syncSavingsAccountTransactionUiState.value =
                    SyncSavingsAccountTransactionUiState.ShowError(Res.string.feature_offline_failed_to_update_savingsaccount)
            }
        }
}
