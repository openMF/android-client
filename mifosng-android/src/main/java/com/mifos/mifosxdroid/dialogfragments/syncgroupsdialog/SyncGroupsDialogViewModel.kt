package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.accounts.GroupAccounts
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.group.GroupWithAssociations
import com.mifos.core.objects.zipmodels.LoanAndLoanRepayment
import com.mifos.core.objects.zipmodels.SavingsAccountAndTransactionTemplate
import com.mifos.utils.Constants
import com.mifos.utils.NetworkUtilsWrapper
import com.mifos.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
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
class SyncGroupsDialogViewModel @Inject constructor(private val repository: SyncGroupsDialogRepository) :
    ViewModel() {

    @Inject
    lateinit var networkUtilsWrapper: NetworkUtilsWrapper

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


    private val _syncGroupsDialogUiState = MutableLiveData<SyncGroupsDialogUiState>()
    val syncGroupsDialogUiState: LiveData<SyncGroupsDialogUiState> = _syncGroupsDialogUiState

    private fun checkNetworkConnection(): Boolean {
        return networkUtilsWrapper.isNetworkConnected()
    }

    fun startSyncingGroups(groups: List<Group>) {
        mGroupList = groups
        checkNetworkConnectionAndSyncGroup()
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
            _syncGroupsDialogUiState.value = SyncGroupsDialogUiState.ShowGroupsSyncSuccessfully
        }
    }

    /**
     * This Method checking network connection before starting group synchronization
     */
    private fun checkNetworkConnectionAndSyncGroup() {
        if (checkNetworkConnection()) {
            syncGroupAndUpdateUI()
        } else {
            _syncGroupsDialogUiState.value = SyncGroupsDialogUiState.ShowNetworkIsNotAvailable
            _syncGroupsDialogUiState.value = SyncGroupsDialogUiState.DismissDialog
        }
    }

    /**
     * This Method Checking network connection and  Syncing the LoanAndLoanRepayment.
     * If found no internet connection then stop syncing the groups and dismiss the dialog.
     */
    private fun checkNetworkConnectionAndSyncLoanAndLoanRepayment() {
        if (checkNetworkConnection()) {
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let { syncLoanAndLoanRepayment(it) }
        } else {
            _syncGroupsDialogUiState.value = SyncGroupsDialogUiState.ShowNetworkIsNotAvailable
            _syncGroupsDialogUiState.value = SyncGroupsDialogUiState.DismissDialog
        }
    }

    /**
     * This method checking the network connection and syncing the SavingsAccountAndTransactions.
     * If found no internet connection then stop syncing the groups and dismiss the dialog.
     */
    private fun checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate() {
        if (checkNetworkConnection()) {
            mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType?.endpoint?.let {
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let { it1 ->
                    syncSavingsAccountAndTemplate(
                        it,
                        it1
                    )
                }
            }
        } else {
            _syncGroupsDialogUiState.value = SyncGroupsDialogUiState.ShowNetworkIsNotAvailable
            _syncGroupsDialogUiState.value = SyncGroupsDialogUiState.DismissDialog
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
                _syncGroupsDialogUiState.value =
                    SyncGroupsDialogUiState.UpdateSingleSyncGroupProgressBar(singleSyncGroupMax)
                mFailedSyncGroup.add(mGroupList[mGroupSyncIndex])
                mGroupSyncIndex += 1
                _syncGroupsDialogUiState.value =
                    SyncGroupsDialogUiState.ShowSyncedFailedGroups(mFailedSyncGroup.size)
                checkNetworkConnectionAndSyncGroup()
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
                    mLoanAccountList = Utils.getActiveLoanAccounts(
                        groupAccounts.loanAccounts
                    )
                    mSavingsAccountList = Utils.getActiveSavingsAccounts(
                        groupAccounts.savingsAccounts
                    )

                    //Updating UI
                    maxSingleSyncGroupProgressBar = mLoanAccountList.size +
                            mSavingsAccountList.size
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
                    _syncGroupsDialogUiState.value =
                        SyncGroupsDialogUiState.UpdateSingleSyncGroupProgressBar(
                            mLoanAndRepaymentSyncIndex
                        )
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
                    _syncGroupsDialogUiState.value =
                        SyncGroupsDialogUiState.UpdateSingleSyncGroupProgressBar(
                            mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex
                        )
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
        _syncGroupsDialogUiState.value = SyncGroupsDialogUiState.ShowProgressbar
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
                        _syncGroupsDialogUiState.value =
                            SyncGroupsDialogUiState.SetClientSyncProgressBarMax(mClients.size)
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
                    _syncGroupsDialogUiState.value =
                        SyncGroupsDialogUiState.ShowError(e.message.toString())
                }

                override fun onNext(client: Client) {
                    resetIndexes()
                    mClientSyncIndex += 1
                    _syncGroupsDialogUiState.value =
                        SyncGroupsDialogUiState.UpdateClientSyncProgressBar(mClientSyncIndex)
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
                syncClientLoanAndLoanRepayment(
                    it
                )
            }
        } else if (mSavingsAccountList.isNotEmpty()) {
            //Sync the Active Savings Account
            mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType?.endpoint?.let {
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
                    mLoanAccountList = Utils.getActiveLoanAccounts(
                        clientAccounts
                            .loanAccounts
                    )
                    mSavingsAccountList = Utils.getSyncableSavingsAccounts(
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
                            syncClientLoanAndLoanRepayment(
                                it
                            )
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
                _syncGroupsDialogUiState.value =
                    SyncGroupsDialogUiState.UpdateClientSyncProgressBar(0)
                val singleSyncClientMax = maxSingleSyncGroupProgressBar
                _syncGroupsDialogUiState.value =
                    SyncGroupsDialogUiState.UpdateSingleSyncGroupProgressBar(singleSyncClientMax)
                mGroupSyncIndex += 1
                checkNetworkConnectionAndSyncGroup()
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
        _syncGroupsDialogUiState.value =
            SyncGroupsDialogUiState.UpdateTotalSyncGroupProgressBarAndCount(mGroupSyncIndex)
    }

    private fun updateGroupName() {
        val groupName = mGroupList[mGroupSyncIndex].name
        _syncGroupsDialogUiState.value = groupName?.let {
            SyncGroupsDialogUiState.ShowSyncingGroup(
                it
            )
        }
    }
}