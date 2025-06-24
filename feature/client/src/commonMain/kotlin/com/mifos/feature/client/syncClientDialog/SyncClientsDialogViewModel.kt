/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.syncClientDialog

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_error_network_not_available
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.MFErrorParser
import com.mifos.core.data.repository.SyncClientsDialogRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.zipmodels.LoanAndLoanRepayment
import com.mifos.room.entities.zipmodels.SavingsAccountAndTransactionTemplate
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncClientsDialogViewModel(
    private val repository: SyncClientsDialogRepository,
    private val prefManager: UserPreferencesRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private var mClientList: List<ClientEntity> = ArrayList()

    private val mFailedSyncClient: MutableList<ClientEntity> = ArrayList()
    private var mLoanAccountList: List<LoanAccountEntity> = ArrayList()
    private var mSavingsAccountList: List<SavingsAccountEntity> = ArrayList()
    private var mLoanAccountSyncStatus = false
    private var mClientSyncIndex = 0
    private var mLoanAndRepaymentSyncIndex = 0
    private var mSavingsAndTransactionSyncIndex = 0
    private var maxSingleSyncClientProgressBar = 0

    private val _syncClientsDialogUiState =
        MutableStateFlow<SyncClientsDialogUiState>(SyncClientsDialogUiState.Loading)
    val syncClientsDialogUiState: StateFlow<SyncClientsDialogUiState> = _syncClientsDialogUiState

    private val _syncClientData: MutableStateFlow<SyncClientsDialogData> = MutableStateFlow(
        SyncClientsDialogData(),
    )
    val syncClientData: StateFlow<SyncClientsDialogData> = _syncClientData

    val isNetworkAvailable = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    fun setClientList(clientsList: List<ClientEntity>) {
        mClientList = clientsList
        _syncClientData.update { it.copy(clientList = clientsList) }
    }

    fun syncClient() {
        viewModelScope.launch {
            val userStatus = prefManager.userInfo.first().userStatus
            if (userStatus == Constants.USER_ONLINE) {
                checkNetworkConnection {
                    syncClientAndUpdateUI()
                }
            }
        }
    }

    private fun syncClientAndUpdateUI() {
        resetIndexes()
        updateTotalSyncProgressBarAndCount()
        if (mClientSyncIndex != mClientList.size) {
            updateClientName()
            syncClientAccounts(mClientList[mClientSyncIndex].id)
        } else {
            _syncClientData.update { it.copy(isSyncSuccess = true) }
        }
    }

    private fun checkNetworkConnectionAndSyncLoanAndLoanRepayment() {
        checkNetworkConnection {
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let { syncLoanAndLoanRepayment(it) }
        }
    }

    private fun resetIndexes() {
        mLoanAccountSyncStatus = false
        mLoanAndRepaymentSyncIndex = 0
        mSavingsAndTransactionSyncIndex = 0
    }

    private fun checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate() {
        checkNetworkConnection {
            val endPoint =
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType?.endpoint
            val id = mSavingsAccountList[mSavingsAndTransactionSyncIndex].id
            if (endPoint != null && id != null) {
                syncSavingsAccountAndTemplate(endPoint, id)
            }
        }
    }

    fun checkAccountsSyncStatusAndSyncAccounts() {
        if (mLoanAccountList.isNotEmpty() && !mLoanAccountSyncStatus) {
            // Sync the Active Loan and LoanRepayment
            checkNetworkConnectionAndSyncLoanAndLoanRepayment()
        } else if (mSavingsAccountList.isNotEmpty()) {
            // Sync the Active Savings Account
            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
        } else {
            // If LoanAccounts and SavingsAccount are null then sync Client to Database
            maxSingleSyncClientProgressBar = 1
            syncClient(mClientList[mClientSyncIndex])
        }
    }

    fun setLoanAccountSyncStatusTrue() {
        if (mLoanAndRepaymentSyncIndex == mLoanAccountList.size) {
            mLoanAccountSyncStatus = true
        }
    }

    suspend fun onAccountSyncFailed(e: Throwable?) {
        try {
            if (e is ClientRequestException || e is ServerResponseException) {
                val singleSyncClientMax = maxSingleSyncClientProgressBar
                _syncClientData.update { it.copy(singleSyncCount = singleSyncClientMax) }
                mFailedSyncClient.add(mClientList[mClientSyncIndex])
                mClientSyncIndex += 1
                _syncClientData.update { it.copy(failedSyncGroupCount = mFailedSyncClient.size) }
                syncClient()
            }
        } catch (throwable: Throwable) {
            MFErrorParser.errorMessage(throwable)
        }
    }

    /**
     * Sync the Client Account with Client Id. This method fetching the Client Accounts from the
     * REST API using Ktorfit and saving these accounts to Database with DatabaseHelperClient
     * and then DataManagerClient gives the returns the Clients Accounts to Presenter.
     *
     * onNext : As Client Accounts Successfully sync then now sync the there Loan and LoanRepayment
     * onError :
     *
     * @param clientId Client Id
     */
    private fun syncClientAccounts(clientId: Int) = viewModelScope.launch {
        val clientAccounts = repository.syncClientAccounts(clientId)
        mLoanAccountList = getActiveLoanAccounts(
            clientAccounts
                .loanAccounts,
        )
        mSavingsAccountList = getSyncableSavingsAccounts(
            clientAccounts
                .savingsAccounts,
        )

        // Updating UI
        maxSingleSyncClientProgressBar = mLoanAccountList.size +
            mSavingsAccountList.size
        checkAccountsSyncStatusAndSyncAccounts()
    }

    /**
     * This Method Syncing the Client's Loans and their LoanRepayment. This is the Observable.zip
     * In Which two request is going to server Loans and LoanRepayment and This request will not
     * complete till that both request successfully got response (200 OK). In Which one will fail
     * then response will come in onError. and If both request is 200 response then response will
     * come in onNext.
     *
     * @param loanId Loan Id
     */
    private fun syncLoanAndLoanRepayment(loanId: Int) {
        viewModelScope.launch {
            try {
                combine(
                    repository.syncLoanById(loanId),
                    repository.syncLoanRepaymentTemplate(loanId),
                ) { loanWithAssociations, loanRepaymentTemplate ->
                    LoanAndLoanRepayment().apply {
                        this.loanWithAssociations = loanWithAssociations.data
                        this.loanRepaymentTemplate = loanRepaymentTemplate.data
                    }
                }.collect { loanAndLoanRepayment ->
                    mLoanAndRepaymentSyncIndex += 1
                    _syncClientData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex) }
                    if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size) {
                        checkNetworkConnectionAndSyncLoanAndLoanRepayment()
                    } else {
                        setLoanAccountSyncStatusTrue()
                        checkAccountsSyncStatusAndSyncAccounts()
                    }
                }
            } catch (e: Throwable) {
                onAccountSyncFailed(e)
            }
        }
    }

    /**
     * This Method Fetch SavingsAccount and SavingsAccountTransactionTemplate and Sync them in
     * Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private fun syncSavingsAccountAndTemplate(savingsAccountType: String, savingsAccountId: Int) {
        viewModelScope.launch {
            combine(
                repository.syncSavingsAccount(
                    savingsAccountType,
                    savingsAccountId,
                    Constants.TRANSACTIONS,
                ),
                repository.syncSavingsAccountTransactionTemplate(
                    savingsAccountType,
                    savingsAccountId,
                    Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT,
                ),
            ) { savingsAccountWithAssociations, savingsAccountTransactionTemplate ->
                SavingsAccountAndTransactionTemplate(
                    savingsAccountTransactionTemplate = savingsAccountTransactionTemplate.data,
                    savingsAccountWithAssociations = savingsAccountWithAssociations.data,
                )
            }.catch {
                onAccountSyncFailed(it)
            }.collect {
                mSavingsAndTransactionSyncIndex += 1
                _syncClientData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex) }

                if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                    checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
                } else {
                    syncClient(mClientList[mClientSyncIndex])
                }
            }
        }
    }

    /**
     * This Method Saving the Clients to Database, If their Accounts, Loan and LoanRepayment
     * saved successfully to Synced.
     *
     * @param client
     */
    fun syncClient(client: com.mifos.room.entities.client.ClientEntity) {
        val updatedClient = client.copy(
            groupId = mClientList[mClientSyncIndex].id,
            sync = true,
        )
        viewModelScope.launch {
            try {
                repository.syncClientInDatabase(updatedClient)

                val singleSyncClientMax = maxSingleSyncClientProgressBar
                _syncClientData.update { it.copy(singleSyncCount = singleSyncClientMax) }
                mClientSyncIndex += 1
                syncClient()
            } catch (e: Exception) {
                _syncClientsDialogUiState.value =
                    SyncClientsDialogUiState.Error(message = e.message.toString())
            }
        }
    }

    private fun updateTotalSyncProgressBarAndCount() {
        _syncClientData.update { it.copy(totalSyncCount = mClientSyncIndex) }
    }

    private fun updateClientName() {
        val clientName = mClientList[mClientSyncIndex].firstname +
            mClientList[mClientSyncIndex].lastname
        _syncClientData.update { it.copy(clientName = clientName) }
    }

    private fun checkNetworkConnection(
        taskWhenOnline: () -> Unit,
    ) {
        if (isNetworkAvailable.value) {
            taskWhenOnline.invoke()
        } else {
            _syncClientsDialogUiState.value = SyncClientsDialogUiState.Error(
                messageResId = Res.string.feature_client_error_network_not_available,
                imageVector = MifosIcons.WifiOff,
            )
        }
    }

    fun getActiveLoanAccounts(loanAccountList: List<LoanAccountEntity>?): List<LoanAccountEntity> {
        return loanAccountList
            ?.filter { it.status?.active == true }
            ?: emptyList()
    }

    fun getSyncableSavingsAccounts(savingsAccounts: List<SavingsAccountEntity>?): List<SavingsAccountEntity> {
        return savingsAccounts
            ?.filter { savingsAccount ->
                savingsAccount.depositType?.value == "Savings" &&
                    savingsAccount.status?.active == true &&
                    savingsAccount.depositType?.isRecurring == false
            } ?: emptyList()
    }
}
