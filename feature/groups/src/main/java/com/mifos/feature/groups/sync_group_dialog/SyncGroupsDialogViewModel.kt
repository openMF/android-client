package com.mifos.feature.groups.sync_group_dialog

import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.NetworkUtilsWrapper
import com.mifos.core.data.repository.SyncGroupsDialogRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.accounts.GroupAccounts
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.group.GroupWithAssociations
import com.mifos.core.objects.zipmodels.LoanAndLoanRepayment
import com.mifos.core.objects.zipmodels.SavingsAccountAndTransactionTemplate
import com.mifos.feature.groups.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class SyncGroupsDialogViewModel @Inject constructor(
    private val repository: SyncGroupsDialogRepository,
    private val networkUtilsWrapper: NetworkUtilsWrapper,
    private val prefManager: PrefManager
) : ViewModel() {

    private var mGroupList: List<Group> = ArrayList()
    private val mFailedSyncGroup: MutableList<Group> = ArrayList()
    private var mClients: List<Client> = ArrayList()
    private var mLoanAccountList: List<LoanAccount> = ArrayList()
    private var mSavingsAccountList: List<SavingsAccount> = ArrayList()
    private var mLoanAccountSyncStatus = false
    private var mGroupSyncIndex = 0
    private var mClientSyncIndex = 0
    private var mLoanAndRepaymentSyncIndex = 0
    private var mSavingsAndTransactionSyncIndex = 0
    private var maxSingleSyncGroupProgressBar = 0

    private val _syncGroupsDialogUiState = MutableStateFlow<SyncGroupsDialogUiState>(
        SyncGroupsDialogUiState.Loading)
    val syncGroupsDialogUiState: StateFlow<SyncGroupsDialogUiState> = _syncGroupsDialogUiState

    private val _syncGroupData: MutableStateFlow<SyncGroupDialogData> = MutableStateFlow(
        SyncGroupDialogData()
    )
    val syncGroupData: StateFlow<SyncGroupDialogData> = _syncGroupData

    fun setGroupList(groupList: List<Group>) {
        mGroupList = groupList
        _syncGroupData.update { it.copy(groupList = groupList) }
    }

    /**
     * This Method checking network connection before starting group synchronization
     */
    fun syncGroups() {
        if(prefManager.userStatus == Constants.USER_ONLINE)  {
            checkNetworkConnection {
                syncGroupAndUpdateUI()
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
            val endPoint = mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType?.endpoint
            val id = mSavingsAccountList[mSavingsAndTransactionSyncIndex].id
            if(endPoint != null && id != null) {
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
            //Sync the Active Loan and LoanRepayment
            checkNetworkConnectionAndSyncLoanAndLoanRepayment()
        } else if (mSavingsAccountList.isNotEmpty()) {
            //Sync the Active Savings Account
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
            if (e is HttpException) {
                val singleSyncGroupMax = maxSingleSyncGroupProgressBar
                _syncGroupData.update { it.copy(singleSyncCount = singleSyncGroupMax) }
                mFailedSyncGroup.add(mGroupList[mGroupSyncIndex])
                mGroupSyncIndex += 1
                _syncGroupData.update { it.copy(failedSyncGroupCount = mFailedSyncGroup.size) }
                syncGroups()
            }
        } catch (throwable: Throwable) {
            RxJavaPlugins.getInstance().errorHandler.handleError(throwable)
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
        repository.syncGroupAccounts(groupId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GroupAccounts>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(groupAccounts: GroupAccounts) {
                    mLoanAccountList = getActiveLoanAccounts(
                        groupAccounts.loanAccounts
                    )
                    mSavingsAccountList = getActiveSavingsAccounts(
                        groupAccounts.savingsAccounts
                    )

                    //Updating UI
                    maxSingleSyncGroupProgressBar = mLoanAccountList.size + mSavingsAccountList.size
                    checkAccountsSyncStatusAndSyncAccounts()
                }
            })

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
        getLoanAndLoanRepayment(loanId)
            .subscribe(object : Subscriber<LoanAndLoanRepayment>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(loanAndLoanRepayment: LoanAndLoanRepayment) {
                    mLoanAndRepaymentSyncIndex += 1
                    _syncGroupData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex) }

                    if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size) {
                        checkNetworkConnectionAndSyncLoanAndLoanRepayment()
                    } else {
                        setLoanAccountSyncStatusTrue()
                        checkAccountsSyncStatusAndSyncAccounts()
                    }
                }
            })

    }

    /**
     * This Method Fetching the  SavingsAccount and SavingsAccountTransactionTemplate and Syncing
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private fun syncSavingsAccountAndTemplate(savingsAccountType: String, savingsAccountId: Int) {
        getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
            .subscribe(object : Subscriber<SavingsAccountAndTransactionTemplate>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(savingsAccountAndTransactionTemplate: SavingsAccountAndTransactionTemplate) {
                    mSavingsAndTransactionSyncIndex += 1
                    _syncGroupData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex) }
                    if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                        checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
                    } else {
                        mGroupList[mGroupSyncIndex].id?.let { loadGroupAssociateClients(it) }
                    }
                }
            })

    }

    /**
     * This Method fetching the Group Associate Clients List.
     *
     * @param groupId Group Id
     */
    private fun loadGroupAssociateClients(groupId: Int) {
        _syncGroupsDialogUiState.value = SyncGroupsDialogUiState.Loading
        repository.getGroupWithAssociations(groupId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GroupWithAssociations>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(groupWithAssociations: GroupWithAssociations) {
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
            })

    }

    /**
     * This Method Saving the Clients to Database, If their Accounts, Loan and LoanRepayment
     * saved Synced successfully.
     *
     * @param client
     */
    private fun syncClient(client: Client) {
        client.groupId = mGroupList[mGroupSyncIndex].id
        client.sync = true
        repository.syncClientInDatabase(client)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Client>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncGroupsDialogUiState.value = SyncGroupsDialogUiState.Error(message = e.message.toString())
                }

                override fun onNext(client: Client) {
                    resetIndexes()
                    mClientSyncIndex += 1
                    _syncGroupData.update { it.copy(clientSyncCount = mClientSyncIndex) }
                    if (mClients.size == mClientSyncIndex) {
                        syncGroup(mGroupList[mGroupSyncIndex])
                    } else {
                        syncClientAccounts(mClients[mClientSyncIndex].id)
                    }
                }
            })

    }

    /**
     * This Method check the Client LoanAccount isEmpty or not, If LoanAccount is not Empty the sync
     * the loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty the sync the groups locally, Yep Groups has been synced.
     */
    private fun checkAccountsSyncStatusAndSyncClientAccounts() {
        if (mLoanAccountList.isNotEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let {
                syncClientLoanAndLoanRepayment(it)
            }
        } else if (mSavingsAccountList.isNotEmpty()) {
            //Sync the Active Savings Account
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
    private fun syncClientAccounts(clientId: Int) {
        repository.syncClientAccounts(clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ClientAccounts>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(clientAccounts: ClientAccounts) {
                    mLoanAccountList = getActiveLoanAccounts(
                        clientAccounts
                            .loanAccounts
                    )
                    mSavingsAccountList = getSyncableSavingsAccounts(
                        clientAccounts
                            .savingsAccounts
                    )
                    checkAccountsSyncStatusAndSyncClientAccounts()
                }
            })

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
        getLoanAndLoanRepayment(loanId)
            .subscribe(object : Subscriber<LoanAndLoanRepayment>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(loanAndLoanRepayment: LoanAndLoanRepayment) {
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
            })

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
        savingsAccountId: Int
    ) {
        getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
            .subscribe(object : Subscriber<SavingsAccountAndTransactionTemplate>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(savingsAccountAndTransactionTemplate: SavingsAccountAndTransactionTemplate) {
                    mSavingsAndTransactionSyncIndex += 1
                    if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                        mSavingsAccountList[mSavingsAndTransactionSyncIndex]
                            .depositType?.endpoint?.let {
                                mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let { it1 ->
                                    syncClientSavingsAccountAndTemplate(
                                        it,
                                        it1
                                    )
                                }
                            }
                    } else {
                        syncClient(mClients[mClientSyncIndex])
                    }
                }
            })

    }

    /**
     * This Method syncing the Group into the Database.
     *
     * @param group Group
     */
    private fun syncGroup(group: Group) {
        group.sync = true
        repository.syncGroupInDatabase(group)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                _syncGroupData.update { it.copy(clientSyncCount = 0) }
                val singleSyncClientMax = maxSingleSyncGroupProgressBar
                _syncGroupData.update { it.copy(singleSyncCount = singleSyncClientMax) }
                mGroupSyncIndex += 1
                syncGroups()
            }

    }

    /**
     * This Method Fetching the LoanAndLoanRepayment
     *
     * @param loanId Loan Id
     * @return LoanAndLoanRepayment
     */
    private fun getLoanAndLoanRepayment(loanId: Int): Observable<LoanAndLoanRepayment> {
        return Observable.combineLatest(
            repository.syncLoanById(loanId),
            repository.syncLoanRepaymentTemplate(loanId)
        ) { loanWithAssociations, loanRepaymentTemplate ->
            LoanAndLoanRepayment(
                loanWithAssociations,
                loanRepaymentTemplate
            )
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    /**
     * This method fetching SavingsAccountAndTemplate.
     *
     * @param savingsAccountType
     * @param savingsAccountId
     * @return SavingsAccountAndTransactionTemplate
     */
    private fun getSavingsAccountAndTemplate(
        savingsAccountType: String, savingsAccountId: Int
    ): Observable<SavingsAccountAndTransactionTemplate> {
        return Observable.combineLatest(
            repository.syncSavingsAccount(
                savingsAccountType, savingsAccountId,
                Constants.TRANSACTIONS
            ),
            repository.syncSavingsAccountTransactionTemplate(
                savingsAccountType,
                savingsAccountId, Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT
            )
        ) { savingsAccountWithAssociations, savingsAccountTransactionTemplate ->
            SavingsAccountAndTransactionTemplate(
                savingsAccountWithAssociations, savingsAccountTransactionTemplate
            )
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
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
        taskWhenOnline: () -> Unit
    ) {
        if(networkUtilsWrapper.isNetworkConnected()) {
            taskWhenOnline.invoke()
        } else {
            _syncGroupsDialogUiState.value = SyncGroupsDialogUiState.Error(
                messageResId = R.string.feature_groups_error_not_connected_internet,
                imageVector = MifosIcons.WifiOff
            )
        }
    }

    fun getActiveLoanAccounts(loanAccountList: List<LoanAccount>?): List<LoanAccount> {
        val loanAccounts: MutableList<LoanAccount> = ArrayList()
        Observable.from(loanAccountList)
            .filter { loanAccount -> loanAccount.status?.active }
            .subscribe { loanAccount -> loanAccounts.add(loanAccount) }
        return loanAccounts
    }

    fun getActiveSavingsAccounts(savingsAccounts: List<SavingsAccount>?): List<SavingsAccount> {
        val accounts: MutableList<SavingsAccount> = ArrayList()
        Observable.from(savingsAccounts)
            .filter { savingsAccount ->
                savingsAccount.status?.active == true &&
                        !savingsAccount.depositType!!.isRecurring
            }
            .subscribe { savingsAccount -> accounts.add(savingsAccount) }
        return accounts
    }

    fun getSyncableSavingsAccounts(savingsAccounts: List<SavingsAccount>?): List<SavingsAccount> {
        val accounts: MutableList<SavingsAccount> = ArrayList()
        Observable.from(savingsAccounts)
            .filter { savingsAccount ->
                savingsAccount.depositType?.value == "Savings" &&
                        savingsAccount.status?.active == true &&
                        !savingsAccount.depositType!!.isRecurring
            }
            .subscribe { savingsAccount -> accounts.add(savingsAccount) }
        return accounts
    }
}