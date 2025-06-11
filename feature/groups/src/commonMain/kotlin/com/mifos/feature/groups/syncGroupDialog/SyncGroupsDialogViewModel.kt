/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.groups.syncGroupDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.SyncGroupsDialogRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.domain.useCases.GetLoanAndLoanRepaymentUseCase
import com.mifos.core.domain.useCases.GetSavingsAccountAndTemplateUseCase
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.group.GroupEntity
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncGroupsDialogViewModel(
    private val repository: SyncGroupsDialogRepository,
    private val prefManager: UserPreferencesRepository,
    private val getSavingsAccountAndTemplateUseCase: GetSavingsAccountAndTemplateUseCase,
    private val getLoanAndLoanRepaymentUseCase: GetLoanAndLoanRepaymentUseCase,
//    private val networkUtilsWrapper: NetworkUtilsWrapper,
) : ViewModel() {

    private var mGroupList: List<GroupEntity> = emptyList()

    private val mFailedSyncGroup: MutableList<GroupEntity> = mutableListOf()
    private var mClients: List<ClientEntity> = emptyList()
    private var mLoanAccountList: List<LoanAccountEntity> = emptyList()
    private var mSavingsAccountList: List<SavingsAccountEntity> = emptyList()
    private var mLoanAccountSyncStatus = false
    private var mGroupSyncIndex = 0
    private var mClientSyncIndex = 0
    private var mLoanAndRepaymentSyncIndex = 0
    private var mSavingsAndTransactionSyncIndex = 0
    private var maxSingleSyncGroupProgressBar = 0

    private val _syncGroupsDialogUiState = MutableStateFlow<SyncGroupsDialogUiState>(
        SyncGroupsDialogUiState.Loading,
    )
    val syncGroupsDialogUiState: StateFlow<SyncGroupsDialogUiState> = _syncGroupsDialogUiState

    private val _syncGroupData: MutableStateFlow<SyncGroupDialogData> = MutableStateFlow(
        SyncGroupDialogData(),
    )
    val syncGroupData: StateFlow<SyncGroupDialogData> = _syncGroupData

    fun setGroupList(groupList: List<GroupEntity>) {
        mGroupList = groupList
        _syncGroupData.update { it.copy(groupList = groupList) }
    }

    /**
     * This Method checking network connection before starting group synchronization
     */
    fun syncGroups() {
        viewModelScope.launch {
            val userStatus = prefManager.userInfo.first().userStatus
            if (userStatus == Constants.USER_ONLINE) {
                checkNetworkConnection {
                    syncGroupAndUpdateUI()
                }
            }
        }
    }

    /**
     * This Method checking that mGroupSyncIndex and mGroupList Size are equal or not. If they
     * are equal, It means all groups have been synced otherwise continue syncing groups.
     */
    private fun syncGroupAndUpdateUI() {
        resetIndexes()
        updateTotalSyncProgressBarAndCount()
        if (mGroupSyncIndex != mGroupList.size) {
            updateGroupName()
            mGroupList[mGroupSyncIndex].id?.let { syncGroupAccounts(it) }
        } else {
            _syncGroupData.update { it.copy(isSyncSuccess = true) }
        }
    }

    /**
     * This Method Checking network connection and  Syncing the LoanAndLoanRepayment.
     * If found no internet connection then stop syncing the groups and dismiss the dialog.
     */
    private fun checkNetworkConnectionAndSyncLoanAndLoanRepayment() {
        checkNetworkConnection {
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let { syncLoanAndLoanRepayment(it) }
        }
    }

    /**
     * This method checking the network connection and syncing the SavingsAccountAndTransactions.
     * If found no internet connection then stop syncing the groups and dismiss the dialog.
     */
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

    /**
     * This Method check the LoanAccount isEmpty or not, If LoanAccount is not Empty the sync the
     * loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty the sync the groups locally, Yep Groups has been synced.
     */
    private fun checkAccountsSyncStatusAndSyncAccounts() {
        if (mLoanAccountList.isNotEmpty() && !mLoanAccountSyncStatus) {
            // Sync the Active Loan and LoanRepayment
            checkNetworkConnectionAndSyncLoanAndLoanRepayment()
        } else if (mSavingsAccountList.isNotEmpty()) {
            // Sync the Active Savings Account
            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
        } else {
            // If LoanAccounts and SavingsAccount are null then sync Client to Database
            maxSingleSyncGroupProgressBar = 1
            mGroupList[mGroupSyncIndex].id?.let { loadGroupAssociateClients(it) }
        }
    }

    /**
     * This Method for setting mLoanAccountSyncStatus = true, It means LoanAccounts have been
     * synced locally.
     */
    private fun setLoanAccountSyncStatusTrue() {
        if (mLoanAndRepaymentSyncIndex == mLoanAccountList.size) {
            mLoanAccountSyncStatus = true
        }
    }

    /**
     * This Method for resetting the mLoanAndRepaymentSyncIndex and mSavingsAndTransactionSyncIndex
     * and mLoanAccountSyncStatus.
     */
    private fun resetIndexes() {
        mLoanAccountSyncStatus = false
        mLoanAndRepaymentSyncIndex = 0
        mSavingsAndTransactionSyncIndex = 0
    }

    /**
     * This Method will be called when ever any request will be failed synced.
     *
     * @param e Throwable
     */
    private fun onAccountSyncFailed(e: Throwable) {
        try {
            if (e is ClientRequestException || e is ServerResponseException) {
                val singleSyncGroupMax = maxSingleSyncGroupProgressBar
                _syncGroupData.update { it.copy(singleSyncCount = singleSyncGroupMax) }
                mFailedSyncGroup.add(mGroupList[mGroupSyncIndex])
                mGroupSyncIndex += 1
                _syncGroupData.update { it.copy(failedSyncGroupCount = mFailedSyncGroup.size) }
                syncGroups()
            }
            Logger.d("debug", Throwable(e.toString()))
        } catch (throwable: Throwable) {
            Logger.d("debug", throwable)
        }
    }

    /**
     * Sync the Group Account with Group Id. This method fetching the Group Accounts from the
     * REST API using retrofit 2 and saving these accounts to Database with DatabaseHelperClient
     * and then DataManagerClient returns the Group Accounts to Presenter.
     *
     *
     *
     *
     * onNext : As Group Accounts Successfully sync then sync there Loan and LoanRepayment
     * onError :
     *
     * @param groupId Client Id
     */
    private fun syncGroupAccounts(groupId: Int) {
        viewModelScope.launch {
            repository.syncGroupAccounts(groupId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> onAccountSyncFailed(dataState.exception)

                        DataState.Loading -> Unit

                        is DataState.Success -> {
                            val groupAccounts = dataState.data

                            mLoanAccountList = getActiveLoanAccounts(
                                groupAccounts.loanAccounts,
                            )
                            mSavingsAccountList = getActiveSavingsAccounts(
                                groupAccounts.savingsAccounts,
                            )

                            // Updating UI
                            maxSingleSyncGroupProgressBar =
                                mLoanAccountList.size + mSavingsAccountList.size
                            checkAccountsSyncStatusAndSyncAccounts()
                        }
                    }
                }
        }
    }

    /**
     * This Method Syncing the Group's Loan and their LoanRepayment. This is the
     * Observable.combineLatest In Which two request is going to server Loans and LoanRepayment
     * and This request will not complete till that both request completed successfully with
     * response (200 OK). If one will fail then response will come in onError. and If both
     * request is 200 response then response will come in onNext.
     *
     * @param loanId Loan Id
     */
    private fun syncLoanAndLoanRepayment(loanId: Int) {
        viewModelScope.launch {
            getLoanAndLoanRepaymentUseCase.invoke(loanId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> onAccountSyncFailed(dataState.exception)

                        DataState.Loading -> Unit

                        is DataState.Success -> {
                            mLoanAndRepaymentSyncIndex += 1
                            _syncGroupData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex) }

                            if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size) {
                                checkNetworkConnectionAndSyncLoanAndLoanRepayment()
                            } else {
                                setLoanAccountSyncStatusTrue()
                                checkAccountsSyncStatusAndSyncAccounts()
                            }
                        }
                    }
                }
        }
    }

    /**
     * This Method Fetching the  SavingsAccount and SavingsAccountTransactionTemplate and Syncing
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private fun syncSavingsAccountAndTemplate(savingsAccountType: String, savingsAccountId: Int) {
        viewModelScope.launch {
            getSavingsAccountAndTemplateUseCase.invoke(
                savingsAccountType,
                savingsAccountId,
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Error -> onAccountSyncFailed(dataState.exception)

                    DataState.Loading -> Unit

                    is DataState.Success -> {
                        mSavingsAndTransactionSyncIndex += 1
                        _syncGroupData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex) }
                        if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
                        } else {
                            mGroupList[mGroupSyncIndex].id?.let { loadGroupAssociateClients(it) }
                        }
                    }
                }
            }
        }
    }

    /**
     * This Method fetching the Group Associate Clients List.
     *
     * @param groupId Group Id
     */
    private fun loadGroupAssociateClients(groupId: Int) {
        viewModelScope.launch {
            repository.getGroupWithAssociations(groupId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> onAccountSyncFailed(dataState.exception)

                        DataState.Loading ->
                            _syncGroupsDialogUiState.value =
                                SyncGroupsDialogUiState.Loading

                        is DataState.Success -> {
                            val groupWithAssociations = dataState.data
                            mClients = groupWithAssociations.clientMembers
                            mClientSyncIndex = 0
                            resetIndexes()
                            if (mClients.isNotEmpty()) {
                                _syncGroupData.update { it.copy(totalClientSyncCount = mClients.size) }
                                syncClientAccounts(mClients[mClientSyncIndex].id)
                            } else {
                                syncGroup(mGroupList[mGroupSyncIndex])
                            }
                        }
                    }
                }
        }
    }

    /**
     * This Method Saving the Clients to Database, If their Accounts, Loan and LoanRepayment
     * saved Synced successfully.
     *
     * @param client
     */
    private fun syncClient(client: ClientEntity) {
        val updatedClient = client.copy(
            groupId = mGroupList[mGroupSyncIndex].id,
            sync = true,
        )
        viewModelScope.launch {
            try {
                repository.syncClientInDatabase(updatedClient)

                resetIndexes()
                mClientSyncIndex += 1
                _syncGroupData.update { it.copy(clientSyncCount = mClientSyncIndex) }
                if (mClients.size == mClientSyncIndex) {
                    syncGroup(mGroupList[mGroupSyncIndex])
                } else {
                    syncClientAccounts(mClients[mClientSyncIndex].id)
                }
            } catch (e: Exception) {
                _syncGroupsDialogUiState.value =
                    SyncGroupsDialogUiState.Error(message = e.message.toString())
            }
        }
    }

    /**
     * This Method check the Client LoanAccount isEmpty or not, If LoanAccount is not Empty the sync
     * the loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty the sync the groups locally, Yep Groups has been synced.
     */
    private fun checkAccountsSyncStatusAndSyncClientAccounts() {
        if (mLoanAccountList.isNotEmpty() && !mLoanAccountSyncStatus) {
            // Sync the Active Loan and LoanRepayment
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let {
                syncClientLoanAndLoanRepayment(it)
            }
        } else if (mSavingsAccountList.isNotEmpty()) {
            // Sync the Active Savings Account
            mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType?.endpoint?.let {
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let { it1 ->
                    syncClientSavingsAccountAndTemplate(it, it1)
                }
            }
        } else {
            syncClient(mClients[mClientSyncIndex])
        }
    }

    /**
     * Sync the Client Account with Client Id. This method fetching the Client Accounts from the
     * REST API using retrofit 2 and saving these accounts to Database with DatabaseHelperClient
     * and then DataManagerClient gives the returns the Clients Accounts to Presenter.
     *
     *
     *
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
        checkAccountsSyncStatusAndSyncClientAccounts()
    }

    /**
     * This Method Syncing the Client's Loan and their LoanRepayment. This is the
     * Observable.combineLatest In Which two request is going to server Loans and LoanRepayment
     * and This request will not complete till that both request completed successfully with
     * response (200 OK). If one will fail then response will come in onError. and If both
     * request is 200 response then response will come in onNext.
     *
     * @param loanId Loan Id
     */
    private fun syncClientLoanAndLoanRepayment(loanId: Int) {
        viewModelScope.launch {
            getLoanAndLoanRepaymentUseCase.invoke(loanId)
                .collect { dataState ->
                    when (dataState) {
                        is DataState.Error -> onAccountSyncFailed(dataState.exception)

                        DataState.Loading -> Unit

                        is DataState.Success -> {
                            mLoanAndRepaymentSyncIndex += 1
                            if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size) {
                                mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let {
                                    syncClientLoanAndLoanRepayment(it)
                                }
                            } else {
                                setLoanAccountSyncStatusTrue()
                                checkAccountsSyncStatusAndSyncClientAccounts()
                            }
                        }
                    }
                }
        }
    }

    /**
     * This Method Fetching the Client SavingsAccount and SavingsAccountTransactionTemplate and Sync
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private fun syncClientSavingsAccountAndTemplate(
        savingsAccountType: String,
        savingsAccountId: Int,
    ) {
        viewModelScope.launch {
            getSavingsAccountAndTemplateUseCase.invoke(
                savingsAccountType,
                savingsAccountId,
            ).collect { dataState ->
                when (dataState) {
                    is DataState.Error -> onAccountSyncFailed(dataState.exception)

                    DataState.Loading -> Unit

                    is DataState.Success -> {
                        mSavingsAndTransactionSyncIndex += 1
                        if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                            mSavingsAccountList[mSavingsAndTransactionSyncIndex]
                                .depositType?.endpoint?.let {
                                    mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let { it1 ->
                                        syncClientSavingsAccountAndTemplate(
                                            it,
                                            it1,
                                        )
                                    }
                                }
                        } else {
                            syncClient(mClients[mClientSyncIndex])
                        }
                    }
                }
            }
        }
    }

    /**
     * This Method syncing the Group into the Database.
     *
     * @param group Group
     */
    private fun syncGroup(group: GroupEntity) {
        val updatedGroup = group.copy(sync = true)
        viewModelScope.launch {
            try {
                repository.syncGroupInDatabase(updatedGroup)

                _syncGroupData.update { it.copy(clientSyncCount = 0) }
                val singleSyncClientMax = maxSingleSyncGroupProgressBar
                _syncGroupData.update { it.copy(singleSyncCount = singleSyncClientMax) }
                mGroupSyncIndex += 1
                syncGroups()
            } catch (_: Exception) {
            }
        }
    }

    private fun updateTotalSyncProgressBarAndCount() {
        _syncGroupData.update { it.copy(totalSyncCount = mGroupSyncIndex) }
    }

    private fun updateGroupName() {
        val groupName = mGroupList[mGroupSyncIndex].name
        groupName?.let {
            _syncGroupData.update { it.copy(groupName = groupName) }
        }
    }

    private fun checkNetworkConnection(
        taskWhenOnline: () -> Unit,
    ) {
//        if (networkUtilsWrapper.isNetworkConnected()) {
        taskWhenOnline.invoke()
//        } else {
//            _syncGroupsDialogUiState.value = SyncGroupsDialogUiState.Error(
//                messageResId = R.string.feature_groups_error_not_connected_internet,
//                imageVector = MifosIcons.WifiOff,
//            )
//        }
    }

    fun getActiveLoanAccounts(loanAccountList: List<LoanAccountEntity>?): List<LoanAccountEntity> {
        return loanAccountList
            ?.filter { it.status?.active == true }
            ?: emptyList()
    }

    fun getActiveSavingsAccounts(savingsAccounts: List<SavingsAccountEntity>?): List<SavingsAccountEntity> {
        return savingsAccounts
            ?.filter { it.status?.active == true && it.depositType?.isRecurring == false }
            ?: emptyList()
    }

    fun getSyncableSavingsAccounts(savingsAccounts: List<SavingsAccountEntity>?): List<SavingsAccountEntity> {
        return savingsAccounts
            ?.filter {
                it.depositType?.value == "Savings" &&
                    it.status?.active == true &&
                    it.depositType?.isRecurring == false
            } ?: emptyList()
    }
}
