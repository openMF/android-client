/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.syncCentersDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.SyncCentersDialogRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.zipmodels.LoanAndLoanRepayment
import com.mifos.room.entities.zipmodels.SavingsAccountAndTransactionTemplate
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class SyncCentersDialogViewModel(
    private val repository: SyncCentersDialogRepository,
    private val prefManager: UserPreferencesRepository,
//    private val networkUtilsWrapper: NetworkUtilsWrapper,
) : ViewModel() {

    private val _syncCentersDialogUiState =
        MutableStateFlow<SyncCentersDialogUiState>(SyncCentersDialogUiState.Loading)
    val syncCentersDialogUiState: StateFlow<SyncCentersDialogUiState> = _syncCentersDialogUiState

    private val _syncCenterData: MutableStateFlow<SyncCentersDialogData> = MutableStateFlow(
        SyncCentersDialogData(),
    )
    val syncCenterData: StateFlow<SyncCentersDialogData> = _syncCenterData

    private var mLoanAccountList: List<LoanAccountEntity> = emptyList()
    private var mSavingsAccountList: List<SavingsAccountEntity> = emptyList()
    private var mMemberLoanAccountsList: List<LoanAccountEntity> = emptyList()
    private var mCenterList: List<CenterEntity> = emptyList()

    private val mFailedSyncCenter: MutableList<CenterEntity> = mutableListOf()
    private var mGroups: List<GroupEntity> = emptyList()
    private var mClients: List<ClientEntity> = emptyList()
    private var mLoanAccountSyncStatus = false
    private var mSavingAccountSyncStatus = false
    private var mCenterSyncIndex = 0
    private var mLoanAndRepaymentSyncIndex = 0
    private var mSavingsAndTransactionSyncIndex = 0
    private var mMemberLoanSyncIndex = 0
    private var mClientSyncIndex = 0
    private var mGroupSyncIndex = 0
    private var maxSingleSyncCenterProgressBar = 0

    fun setCentersList(centersList: List<CenterEntity>) {
        mCenterList = centersList
        _syncCenterData.update { it.copy(centersList = centersList) }
    }

    fun syncCenter() {
        viewModelScope.launch {
            val userStatus = prefManager.userInfo.first().userStatus
            if (!userStatus) {
                checkNetworkConnection {
                    syncCenterAndUpdateUI()
                }
            }
        }
    }

    /**
     * This Method checking that mCenterSyncIndex and mCenterList Size are equal or not. If they
     * are equal, It means all centers have been synced otherwise continue syncing centers.
     */
    private fun syncCenterAndUpdateUI() {
        resetIndexes()
        updateTotalSyncProgressBarAndCount()
        if (mCenterSyncIndex != mCenterList.size) {
            updateCenterName()
            mCenterList[mCenterSyncIndex].id?.let { syncCenterAccounts(it) }
        } else {
            _syncCenterData.update { it.copy(isSyncSuccess = true) }
        }
    }

    /**
     * This Method for resetting the mLoanAndRepaymentSyncIndex and mSavingsAndTransactionSyncIndex
     * and  mMemberLoanSyncIndex and mLoanAccountSyncStatus and mSavingAccountSyncStatus.
     */
    private fun resetIndexes() {
        mLoanAccountSyncStatus = false
        mSavingAccountSyncStatus = false
        mLoanAndRepaymentSyncIndex = 0
        mSavingsAndTransactionSyncIndex = 0
        mMemberLoanSyncIndex = 0
    }

    /**
     * This Method will be called when ever any request will be failed synced.
     *
     * @param e Throwable
     */
    private fun onAccountSyncFailed(e: Throwable) {
        try {
            if (e is ClientRequestException || e is ServerResponseException) {
                val singleSyncCenterMax = maxSingleSyncCenterProgressBar
                _syncCenterData.update { it.copy(singleSyncCount = singleSyncCenterMax) }
                mFailedSyncCenter.add(mCenterList[mCenterSyncIndex])
                mCenterSyncIndex += 1
                _syncCenterData.update { it.copy(failedSyncGroupCount = mFailedSyncCenter.size) }
                syncCenter()
            }
        } catch (throwable: Throwable) {
            Logger.e("Error", throwable)
        }
    }

    /**
     * Sync the Center Account with Center Id. This method fetching the Center Accounts from the
     * REST API using retrofit 2 and saving these accounts to Database with DatabaseHelperCenter
     * and then DataManagerCenter returns the Center Accounts to Presenter.
     *
     *
     *
     *
     * onNext : As Center Accounts Successfully sync then sync there Loan and LoanRepayment
     * onError :
     *
     * @param centerId Center Id
     */
    private fun syncCenterAccounts(centerId: Int) {
        viewModelScope.launch {
            repository.syncCenterAccounts(centerId)
                .catch { e ->
                    onAccountSyncFailed(e)
                }.collect { centerAccounts ->
                    mLoanAccountList = getActiveLoanAccounts(
                        centerAccounts.data?.loanAccounts,
                    )
                    mSavingsAccountList = getActiveSavingsAccounts(
                        centerAccounts.data?.savingsAccounts,
                    )
                    mMemberLoanAccountsList = getActiveLoanAccounts(
                        centerAccounts.data?.memberLoanAccounts,
                    )
                    // Updating UI
                    maxSingleSyncCenterProgressBar = (
                        mLoanAccountList.size +
                            mSavingsAccountList.size + mMemberLoanAccountsList.size
                        )
                    checkAccountsSyncStatusAndSyncAccounts()
                }
        }
    }

    /**
     * This Method check the LoanAccount isEmpty or not, If LoanAccount is not Empty the sync the
     * loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty then check to the next MemberLoanAccount isEmpty or not. if
     * MemberLoanAccount is not empty then sync the MemberLoanAccount locally and if
     * MemberLoanAccount are Empty the sync the Centers locally, Yep Centers has been synced.
     */
    private fun checkAccountsSyncStatusAndSyncAccounts() {
        if (mLoanAccountList.isNotEmpty() && !mLoanAccountSyncStatus) {
            // Sync the Active Loan and LoanRepayment
            checkNetworkConnectionAndSyncLoanAndLoanRepayment()
        } else if (mSavingsAccountList.isNotEmpty() && !mSavingAccountSyncStatus) {
            // Sync the Active Savings Account
            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
        } else if (mMemberLoanAccountsList.isNotEmpty()) {
            // Sync the Active member Loan Account
            checkNetworkConnectionAndSyncMemberLoanAndMemberLoanRepayment()
        } else {
            maxSingleSyncCenterProgressBar = 1
            mCenterList[mCenterSyncIndex].id?.let { loadCenterAssociateGroups(it) }
        }
    }

    /**
     * This Method Checking network connection and  Syncing the LoanAndLoanRepayment.
     * If found no internet connection then stop syncing the Centers and dismiss the dialog.
     */
    private fun checkNetworkConnectionAndSyncLoanAndLoanRepayment() {
        checkNetworkConnection {
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let { syncLoanAndLoanRepayment(it) }
        }
    }

    /**
     * This Method Checking network connection and  Syncing the MemberLoanAndMemberLoanRepayment.
     * If found no internet connection then stop syncing the Centers and dismiss the dialog.
     */
    private fun checkNetworkConnectionAndSyncMemberLoanAndMemberLoanRepayment() {
        checkNetworkConnection {
            val endPoint =
                mMemberLoanAccountsList[mMemberLoanSyncIndex].loanType?.value
            val id = mSavingsAccountList[mSavingsAndTransactionSyncIndex].id
            if (endPoint != null && id != null) {
                syncSavingsAccountAndTemplate(endPoint, id)
            }
        }
    }

    /**
     * This method checking the network connection and syncing the SavingsAccountAndTransactions.
     * If found no internet connection then stop syncing the centers and dismiss the dialog.
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
     * This Method for setting mLoanAccountSyncStatus = true, It means LoanAccounts have been
     * synced locally.
     */
    private fun setLoanAccountSyncStatusTrue() {
        if (mLoanAndRepaymentSyncIndex == mLoanAccountList.size) {
            mLoanAccountSyncStatus = true
        }
    }

    /**
     * This Method for setting mSavingAccountSyncStatus = true, It means LoanAccounts have been
     * synced locally.
     */
    private fun setSavingAccountSyncStatusTrue() {
        if (mSavingsAndTransactionSyncIndex == mSavingsAccountList.size) {
            mSavingAccountSyncStatus = true
        }
    }

    /**
     * This Method Syncing the Center's Loan and their LoanRepayment. This is the
     * Observable.combineLatest In Which two request is going to server Loans and LoanRepayment
     * and This request will not complete till that both request completed successfully with
     * response (200 OK). If one will fail then response will come in onError. and If both
     * request is 200 response then response will come in onNext.
     *
     * @param loanId Loan Id
     */
    private fun syncLoanAndLoanRepayment(loanId: Int) {
        viewModelScope.launch {
            getLoanAndLoanRepayment(loanId)
                .catch {
                    onAccountSyncFailed(it)
                }
                .collect {
                    mLoanAndRepaymentSyncIndex += 1
                    _syncCenterData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex) }
                    if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size) {
                        checkNetworkConnectionAndSyncLoanAndLoanRepayment()
                    } else {
                        setLoanAccountSyncStatusTrue()
                        checkAccountsSyncStatusAndSyncAccounts()
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
            getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
                .catch {
                    onAccountSyncFailed(it)
                }.collect {
                    mSavingsAndTransactionSyncIndex += 1
                    _syncCenterData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex) }
                    if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                        checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
                    } else {
                        setSavingAccountSyncStatusTrue()
                        checkAccountsSyncStatusAndSyncAccounts()
                    }
                }
        }
    }

    /**
     * This Method syncing the Center into the Database.
     *
     * @param center Center
     */
    private fun syncCenter(center: CenterEntity) {
        val updatedCenter = center.copy(
            id = mCenterList[mCenterSyncIndex].id,
            sync = true,
        )
        viewModelScope.launch {
            try {
                repository.syncCenterInDatabase(updatedCenter)

                val singleSyncCenterMax = maxSingleSyncCenterProgressBar
                _syncCenterData.update { it.copy(singleSyncCount = singleSyncCenterMax) }
                mCenterSyncIndex += 1
                syncCenter()
            } catch (e: Exception) {
                Logger.e("Error") {
                    "syncCenter: ${e.message}"
                }
            }
        }
    }

    /**
     * This Method Fetching the LoanAndLoanRepayment
     *
     * @param loanId Loan Id
     * @return LoanAndLoanRepayment
     */
    private suspend fun getLoanAndLoanRepayment(loanId: Int): Flow<LoanAndLoanRepayment> {
        return combine(
            repository.syncLoanById(loanId),
            repository.syncLoanRepaymentTemplate(loanId),
        ) { loanWithAssociations, loanRepaymentTemplate ->
            LoanAndLoanRepayment(
                loanWithAssociations.data,
                loanRepaymentTemplate.data,
            )
        }
    }

    /**
     * This method fetching SavingsAccountAndTemplate.
     *
     * @param savingsAccountType
     * @param savingsAccountId
     * @return SavingsAccountAndTransactionTemplate
     */
    private fun getSavingsAccountAndTemplate(
        savingsAccountType: String,
        savingsAccountId: Int,
    ): Flow<SavingsAccountAndTransactionTemplate> {
        return combine(
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
                savingsAccountWithAssociations.data,
                savingsAccountTransactionTemplate.data,
            )
        }
    }

    /**
     * This Method fetching the Center Associate Groups List.
     *
     * @param centerId Center Id
     */
    private fun loadCenterAssociateGroups(centerId: Int) {
        viewModelScope.launch {
            _syncCentersDialogUiState.value = SyncCentersDialogUiState.Loading

            repository.getCenterWithAssociations(centerId)
                .catch {
                    onAccountSyncFailed(it)
                }.collect { centerWithAssociations ->
                    mGroups = centerWithAssociations.data?.groupMembers ?: emptyList()
                    mGroupSyncIndex = 0
                    resetIndexes()
                    if (mGroups.isNotEmpty()) {
                        _syncCenterData.update { it.copy(totalGroupsSyncCount = mGroups.size) }
                        mGroups[mGroupSyncIndex].id?.let { syncGroupAccounts(it) }
                    } else {
                        syncCenter(mCenterList[mCenterSyncIndex])
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
        _syncCentersDialogUiState.value = SyncCentersDialogUiState.Loading
        viewModelScope.launch {
            repository.getGroupWithAssociations(groupId)
                .catch {
                    onAccountSyncFailed(it)
                }.collect { groupWithAssociations ->
                    mClients = getActiveClients(groupWithAssociations.data?.clientMembers)
                    mClientSyncIndex = 0
                    resetIndexes()
                    if (mClients.isNotEmpty()) {
                        _syncCenterData.update { it.copy(totalClientSyncCount = mClients.size) }
                        syncClientAccounts(mClients[mClientSyncIndex].id)
                    } else {
                        syncGroup(mGroups[mGroupSyncIndex])
                    }
                }
        }
    }

    /**
     * Sync the Group Account with Group Id. This method fetching the Group Accounts from the
     * REST API using retrofit 2 and saving these accounts to Database with DatabaseHelperGroup
     * and then DataManagerGroup gives the returns the Group Accounts to Presenter.
     *
     *
     *
     *
     * onNext : As Group Accounts Successfully sync then now sync the there Loan and LoanRepayment
     * onError :
     *
     * @param groupId Group Id
     */
    private fun syncGroupAccounts(groupId: Int) {
        viewModelScope.launch {
            repository.syncGroupAccounts(groupId)
                .catch { e ->
                    onAccountSyncFailed(e)
                }
                .collect { groupAccounts ->
                    mLoanAccountList = getActiveLoanAccounts(
                        groupAccounts.data?.loanAccounts,
                    )
                    mSavingsAccountList = getActiveSavingsAccounts(
                        groupAccounts.data?.savingsAccounts,
                    )
                    checkAccountsSyncStatusAndSyncGroupAccounts()
                }
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
     * This Method check the Group LoanAccount isEmpty or not, If LoanAccount is not Empty the sync
     * the loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty the sync the groups locally, Yep Groups has been synced.
     */
    private fun checkAccountsSyncStatusAndSyncGroupAccounts() {
        if (mLoanAccountList.isNotEmpty() && !mLoanAccountSyncStatus) {
            // Sync the Active Loan and LoanRepayment
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let {
                syncGroupLoanAndLoanRepayment(
                    it,
                )
            }
        } else if (mSavingsAccountList.isNotEmpty()) {
            // Sync the Active Savings Account
            mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let {
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType?.endpoint?.let { it1 ->
                    syncGroupSavingsAccountAndTemplate(
                        it1,
                        it,
                    )
                }
            }
        } else {
            mGroups[mGroupSyncIndex].id?.let { loadGroupAssociateClients(it) }
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
                syncClientLoanAndLoanRepayment(
                    it,
                )
            }
        } else if (mSavingsAccountList.isNotEmpty()) {
            // Sync the Active Savings Account
            mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let {
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType?.endpoint?.let { it1 ->
                    syncClientSavingsAccountAndTemplate(
                        it1,
                        it,
                    )
                }
            }
        } else {
            syncClient(mClients[mClientSyncIndex])
        }
    }

    /**
     * This Method Saving the Groups to Database, If their Accounts, Loan and LoanRepayment
     * saved Synced successfully.
     *
     * @param group
     */
    private fun syncGroup(group: GroupEntity) {
        val updatedGroup = group.copy(
            centerId = mCenterList[mCenterSyncIndex].id,
            sync = true,
        )

        viewModelScope.launch {
            try {
                repository.syncGroupInDatabase(updatedGroup)

                resetIndexes()
                mGroupSyncIndex += 1
                if (mGroups.size == mGroupSyncIndex) {
                    syncCenter(mCenterList[mCenterSyncIndex])
                } else {
                    mGroups[mGroupSyncIndex].id?.let { syncGroupAccounts(it) }
                }
            } catch (e: Exception) {
                _syncCentersDialogUiState.value =
                    SyncCentersDialogUiState.Error(message = e.message.toString())
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
            groupId = mGroups[mGroupSyncIndex].id,
            sync = true,
        )
        viewModelScope.launch {
            try {
                repository.syncClientInDatabase(updatedClient)

                resetIndexes()
                mClientSyncIndex += 1
                if (mClients.size == mClientSyncIndex) {
                    syncGroup(mGroups[mGroupSyncIndex])
                } else {
                    syncClientAccounts(mClients[mClientSyncIndex].id)
                }
            } catch (e: Exception) {
                _syncCentersDialogUiState.value =
                    SyncCentersDialogUiState.Error(message = e.message.toString())
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
    private fun syncGroupLoanAndLoanRepayment(loanId: Int) {
        viewModelScope.launch {
            getLoanAndLoanRepayment(loanId)
                .catch {
                    onAccountSyncFailed(it)
                }
                .collect {
                    mLoanAndRepaymentSyncIndex += 1
                    if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size) {
                        mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let {
                            syncGroupLoanAndLoanRepayment(
                                it,
                            )
                        }
                    } else {
                        setLoanAccountSyncStatusTrue()
                        checkAccountsSyncStatusAndSyncGroupAccounts()
                    }
                }
        }
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
            getLoanAndLoanRepayment(loanId)
                .catch {
                    onAccountSyncFailed(it)
                }.collect {
                    mLoanAndRepaymentSyncIndex += 1
                    if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size) {
                        mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let {
                            syncClientLoanAndLoanRepayment(
                                it,
                            )
                        }
                    } else {
                        setLoanAccountSyncStatusTrue()
                        checkAccountsSyncStatusAndSyncClientAccounts()
                    }
                }
        }
    }

    /**
     * This Method Fetching the Groups SavingsAccount and SavingsAccountTransactionTemplate and Sync
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private fun syncGroupSavingsAccountAndTemplate(
        savingsAccountType: String,
        savingsAccountId: Int,
    ) {
        viewModelScope.launch {
            getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
                .catch {
                    onAccountSyncFailed(it)
                }.collect {
                    mSavingsAndTransactionSyncIndex += 1
                    if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                        mSavingsAccountList[mSavingsAndTransactionSyncIndex]
                            .depositType?.endpoint?.let {
                                mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let { it1 ->
                                    syncGroupSavingsAccountAndTemplate(
                                        it,
                                        it1,
                                    )
                                }
                            }
                    } else {
                        mGroups[mGroupSyncIndex].id?.let { loadGroupAssociateClients(it) }
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
            getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
                .catch {
                    onAccountSyncFailed(it)
                }.collect {
                    mSavingsAndTransactionSyncIndex += 1
                    if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                        mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let {
                            mSavingsAccountList[mSavingsAndTransactionSyncIndex]
                                .depositType?.endpoint?.let { it1 ->
                                    syncClientSavingsAccountAndTemplate(
                                        it1,
                                        it,
                                    )
                                }
                        }
                    } else {
                        syncClient(mClients[mClientSyncIndex])
                    }
                }
        }
    }

    private fun updateTotalSyncProgressBarAndCount() {
        _syncCenterData.update { it.copy(totalSyncCount = mCenterSyncIndex) }
    }

    private fun updateCenterName() {
        val centerName = mCenterList[mCenterSyncIndex].name
        if (centerName != null) {
            _syncCenterData.update { it.copy(centerName = centerName) }
        }
    }

    private fun checkNetworkConnection(
        taskWhenOnline: () -> Unit,
    ) {
// TODO: Commented out since we dont have a network connection checker now.

//        if (networkUtilsWrapper.isNetworkConnected()) {
        taskWhenOnline.invoke()
//        } else {
//            _syncCentersDialogUiState.value = SyncCentersDialogUiState.Error(
//                messageResId = R.string.feature_center_error_not_connected_internet,
//                imageVector = MifosIcons.WifiOff,
//            )
//        }
    }

    fun getActiveLoanAccounts(loanAccountList: List<LoanAccountEntity>?): List<LoanAccountEntity> {
        return loanAccountList?.filter {
            it.status?.active == true
        }.orEmpty()
    }

    fun getActiveSavingsAccounts(savingsAccounts: List<SavingsAccountEntity>?): List<SavingsAccountEntity> {
        return savingsAccounts
            ?.filter { account ->
                account.status?.active == true && account.depositType?.isRecurring == false
            }
            .orEmpty()
    }

    fun getActiveClients(clients: List<ClientEntity>?): List<ClientEntity> {
        return clients
            ?.filter { it.active }
            .orEmpty()
    }

    fun getSyncableSavingsAccounts(savingsAccounts: List<SavingsAccountEntity>?): List<SavingsAccountEntity> {
        return savingsAccounts
            ?.filter { account ->
                account.depositType?.value == "Savings" &&
                    account.status?.active == true &&
                    account.depositType?.isRecurring == false
            }
            .orEmpty()
    }
}
