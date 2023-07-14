package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog

import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.ClientAccounts
import com.mifos.objects.accounts.GroupAccounts
import com.mifos.objects.accounts.loan.LoanAccount
import com.mifos.objects.accounts.savings.SavingsAccount
import com.mifos.objects.client.Client
import com.mifos.objects.group.Group
import com.mifos.objects.group.GroupWithAssociations
import com.mifos.objects.zipmodels.LoanAndLoanRepayment
import com.mifos.objects.zipmodels.SavingsAccountAndTransactionTemplate
import com.mifos.utils.Constants
import com.mifos.utils.Utils
import retrofit2.adapter.rxjava.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 11/09/16.
 */
class SyncGroupsDialogPresenter @Inject constructor(
    private val mDataManagerGroups: DataManagerGroups,
    private val mDataManagerLoan: DataManagerLoan,
    private val mDataManagerSavings: DataManagerSavings,
    private val mDataManagerClient: DataManagerClient
) : BasePresenter<SyncGroupsDialogMvpView>() {
    private val mSubscriptions: CompositeSubscription
    private var mGroupList: List<Group>
    private val mFailedSyncGroup: MutableList<Group>
    private var mClients: List<Client>
    private var mLoanAccountList: List<LoanAccount>
    private var mSavingsAccountList: List<SavingsAccount>
    private var mLoanAccountSyncStatus = false
    private var mGroupSyncIndex = 0
    private var mClientSyncIndex = 0
    private var mLoanAndRepaymentSyncIndex = 0
    private var mSavingsAndTransactionSyncIndex = 0

    init {
        mSubscriptions = CompositeSubscription()
        mGroupList = ArrayList()
        mFailedSyncGroup = ArrayList()
        mLoanAccountList = ArrayList()
        mSavingsAccountList = ArrayList()
        mClients = ArrayList()
    }

    override fun attachView(mvpView: SyncGroupsDialogMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.clear()
    }

    /**
     * This Method Start Syncing Groups. Start Syncing the Groups Accounts.
     *
     * @param groups Selected Groups For Syncing
     */
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
            syncGroupAccounts(mGroupList[mGroupSyncIndex].id)
        } else {
            mvpView?.showGroupsSyncSuccessfully()
        }
    }

    /**
     * This Method checking network connection before starting group synchronization
     */
    private fun checkNetworkConnectionAndSyncGroup() {
        if (mvpView?.isOnline == true) {
            syncGroupAndUpdateUI()
        } else {
            mvpView?.showNetworkIsNotAvailable()
            mvpView?.dismissDialog()
        }
    }

    /**
     * This Method Checking network connection and  Syncing the LoanAndLoanRepayment.
     * If found no internet connection then stop syncing the groups and dismiss the dialog.
     */
    private fun checkNetworkConnectionAndSyncLoanAndLoanRepayment() {
        if (mvpView?.isOnline == true) {
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let { syncLoanAndLoanRepayment(it) }
        } else {
            mvpView?.showNetworkIsNotAvailable()
            mvpView?.dismissDialog()
        }
    }

    /**
     * This method checking the network connection and syncing the SavingsAccountAndTransactions.
     * If found no internet connection then stop syncing the groups and dismiss the dialog.
     */
    private fun checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate() {
        if (mvpView?.isOnline == true) {
            syncSavingsAccountAndTemplate(
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType.endpoint,
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].id
            )
        } else {
            mvpView?.showNetworkIsNotAvailable()
            mvpView?.dismissDialog()
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
            mvpView?.maxSingleSyncGroupProgressBar = 1
            loadGroupAssociateClients(mGroupList[mGroupSyncIndex].id)
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
                val singleSyncGroupMax = mvpView?.maxSingleSyncGroupProgressBar
                if (singleSyncGroupMax != null) {
                    mvpView?.updateSingleSyncGroupProgressBar(singleSyncGroupMax)
                }
                mFailedSyncGroup.add(mGroupList[mGroupSyncIndex])
                mGroupSyncIndex += 1
                mvpView?.showSyncedFailedGroups(mFailedSyncGroup.size)
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
        checkViewAttached()
        mSubscriptions.add(
            mDataManagerGroups.syncGroupAccounts(groupId)
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
                        mvpView?.maxSingleSyncGroupProgressBar = mLoanAccountList.size +
                                mSavingsAccountList.size
                        checkAccountsSyncStatusAndSyncAccounts()
                    }
                })
        )
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
        checkViewAttached()
        mSubscriptions.add(
            getLoanAndLoanRepayment(loanId)
                .subscribe(object : Subscriber<LoanAndLoanRepayment>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        onAccountSyncFailed(e)
                    }

                    override fun onNext(loanAndLoanRepayment: LoanAndLoanRepayment) {
                        mLoanAndRepaymentSyncIndex += 1
                        mvpView?.updateSingleSyncGroupProgressBar(mLoanAndRepaymentSyncIndex)
                        if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size) {
                            checkNetworkConnectionAndSyncLoanAndLoanRepayment()
                        } else {
                            setLoanAccountSyncStatusTrue()
                            checkAccountsSyncStatusAndSyncAccounts()
                        }
                    }
                })
        )
    }

    /**
     * This Method Fetching the  SavingsAccount and SavingsAccountTransactionTemplate and Syncing
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private fun syncSavingsAccountAndTemplate(savingsAccountType: String, savingsAccountId: Int) {
        checkViewAttached()
        mSubscriptions.add(
            getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
                .subscribe(object : Subscriber<SavingsAccountAndTransactionTemplate>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        onAccountSyncFailed(e)
                    }

                    override fun onNext(savingsAccountAndTransactionTemplate: SavingsAccountAndTransactionTemplate) {
                        mSavingsAndTransactionSyncIndex += 1
                        mvpView?.updateSingleSyncGroupProgressBar(
                            mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex
                        )
                        if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
                        } else {
                            loadGroupAssociateClients(mGroupList[mGroupSyncIndex].id)
                        }
                    }
                })
        )
    }

    /**
     * This Method fetching the Group Associate Clients List.
     *
     * @param groupId Group Id
     */
    private fun loadGroupAssociateClients(groupId: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerGroups.getGroupWithAssociations(groupId)
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
                            mvpView?.setClientSyncProgressBarMax(mClients.size)
                            syncClientAccounts(mClients[mClientSyncIndex].id)
                        } else {
                            syncGroup(mGroupList[mGroupSyncIndex])
                        }
                    }
                })
        )
    }

    /**
     * This Method Saving the Clients to Database, If their Accounts, Loan and LoanRepayment
     * saved Synced successfully.
     *
     * @param client
     */
    private fun syncClient(client: Client) {
        checkViewAttached()
        client.groupId = mGroupList[mGroupSyncIndex].id
        client.isSync = true
        mSubscriptions.add(
            mDataManagerClient.syncClientInDatabase(client)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Client>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showError(R.string.failed_to_sync_client)
                    }

                    override fun onNext(client: Client) {
                        resetIndexes()
                        mClientSyncIndex += 1
                        mvpView?.updateClientSyncProgressBar(mClientSyncIndex)
                        if (mClients.size == mClientSyncIndex) {
                            syncGroup(mGroupList[mGroupSyncIndex])
                        } else {
                            syncClientAccounts(mClients[mClientSyncIndex].id)
                        }
                    }
                })
        )
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
            syncClientSavingsAccountAndTemplate(
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType.endpoint,
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].id
            )
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
        checkViewAttached()
        mSubscriptions.add(
            mDataManagerClient.syncClientAccounts(clientId)
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
        )
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
        checkViewAttached()
        mSubscriptions.add(
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
        )
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
        checkViewAttached()
        mSubscriptions.add(
            getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
                .subscribe(object : Subscriber<SavingsAccountAndTransactionTemplate>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        onAccountSyncFailed(e)
                    }

                    override fun onNext(savingsAccountAndTransactionTemplate: SavingsAccountAndTransactionTemplate) {
                        mSavingsAndTransactionSyncIndex += 1
                        if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                            syncClientSavingsAccountAndTemplate(
                                mSavingsAccountList[mSavingsAndTransactionSyncIndex]
                                    .depositType.endpoint,
                                mSavingsAccountList[mSavingsAndTransactionSyncIndex].id
                            )
                        } else {
                            syncClient(mClients[mClientSyncIndex])
                        }
                    }
                })
        )
    }

    /**
     * This Method syncing the Group into the Database.
     *
     * @param group Group
     */
    private fun syncGroup(group: Group) {
        checkViewAttached()
        group.isSync = true
        mSubscriptions.add(mDataManagerGroups.syncGroupInDatabase(group)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                mvpView?.updateClientSyncProgressBar(0)
                val singleSyncClientMax = mvpView?.maxSingleSyncGroupProgressBar
                if (singleSyncClientMax != null) {
                    mvpView?.updateSingleSyncGroupProgressBar(singleSyncClientMax)
                }
                mGroupSyncIndex += 1
                checkNetworkConnectionAndSyncGroup()
            }
        )
    }

    /**
     * This Method Fetching the LoanAndLoanRepayment
     *
     * @param loanId Loan Id
     * @return LoanAndLoanRepayment
     */
    private fun getLoanAndLoanRepayment(loanId: Int): Observable<LoanAndLoanRepayment> {
        return Observable.combineLatest(
            mDataManagerLoan.syncLoanById(loanId),
            mDataManagerLoan.syncLoanRepaymentTemplate(loanId)
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
            mDataManagerSavings.syncSavingsAccount(
                savingsAccountType, savingsAccountId,
                Constants.TRANSACTIONS
            ),
            mDataManagerSavings.syncSavingsAccountTransactionTemplate(
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
        mvpView?.updateTotalSyncGroupProgressBarAndCount(mGroupSyncIndex)
    }

    private fun updateGroupName() {
        val groupName = mGroupList[mGroupSyncIndex].name
        mvpView?.showSyncingGroup(groupName)
    }
}