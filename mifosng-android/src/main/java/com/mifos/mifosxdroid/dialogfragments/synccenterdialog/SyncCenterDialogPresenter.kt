package com.mifos.mifosxdroid.dialogfragments.synccenterdialog

import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.CenterAccounts
import com.mifos.objects.accounts.ClientAccounts
import com.mifos.objects.accounts.GroupAccounts
import com.mifos.objects.accounts.loan.LoanAccount
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.objects.accounts.savings.SavingsAccount
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.objects.client.Client
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.Group
import com.mifos.objects.group.GroupWithAssociations
import com.mifos.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate
import com.mifos.objects.zipmodels.LoanAndLoanRepayment
import com.mifos.objects.zipmodels.SavingsAccountAndTransactionTemplate
import com.mifos.utils.Constants
import com.mifos.utils.Utils
import retrofit2.adapter.rxjava.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.functions.Func2
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by mayankjindal on 10/07/17.
 */
class SyncCenterDialogPresenter @Inject constructor(
    private val mDataManagerCenter: DataManagerCenter,
    private val mDataManagerLoan: DataManagerLoan,
    private val mDataManagerSavings: DataManagerSavings,
    private val mDataManagerGroups: DataManagerGroups,
    private val mDataManagerClient: DataManagerClient
) : BasePresenter<SyncCenterDialogMvpView>() {
    private var mLoanAccountList: List<LoanAccount>
    private var mSavingsAccountList: List<SavingsAccount>
    private var mMemberLoanAccountsList: List<LoanAccount>
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()
    private var mCenterList: List<Center>
    private val mFailedSyncCenter: MutableList<Center>
    private var mGroups: List<Group>
    private var mClients: List<Client>
    private var mLoanAccountSyncStatus = false
    private var mSavingAccountSyncStatus = false
    private var mCenterSyncIndex = 0
    private var mLoanAndRepaymentSyncIndex = 0
    private var mSavingsAndTransactionSyncIndex = 0
    private var mMemberLoanSyncIndex = 0
    private var mClientSyncIndex = 0
    private var mGroupSyncIndex = 0

    init {
        mCenterList = ArrayList()
        mFailedSyncCenter = ArrayList()
        mLoanAccountList = ArrayList()
        mSavingsAccountList = ArrayList()
        mMemberLoanAccountsList = ArrayList()
        mClients = ArrayList()
        mGroups = ArrayList()
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    /**
     * This Method Start Syncing Centers. Start Syncing the Centers Accounts.
     *
     * @param centers Selected Centers For Syncing
     */
    fun startSyncingCenters(centers: List<Center>) {
        mCenterList = centers
        checkNetworkConnectionAndSyncCenter()
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
            syncCenterAccounts(mCenterList[mCenterSyncIndex].id)
        } else {
            mvpView?.showCentersSyncSuccessfully()
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
     * This Method checking network connection before starting center synchronization
     */
    fun checkNetworkConnectionAndSyncCenter() {
        if (mvpView?.isOnline == true) {
            syncCenterAndUpdateUI()
        } else {
            mvpView?.showNetworkIsNotAvailable()
            mvpView?.dismissDialog()
        }
    }

    /**
     * This Method will be called when ever any request will be failed synced.
     *
     * @param e Throwable
     */
    private fun onAccountSyncFailed(e: Throwable) {
        try {
            if (e is HttpException) {
                val singleSyncCenterMax = mvpView?.maxSingleSyncCenterProgressBar
                if (singleSyncCenterMax != null) {
                    mvpView?.updateSingleSyncCenterProgressBar(singleSyncCenterMax)
                }
                mFailedSyncCenter.add(mCenterList[mCenterSyncIndex])
                mCenterSyncIndex += 1
                mvpView?.showSyncedFailedCenters(mFailedSyncCenter.size)
                checkNetworkConnectionAndSyncCenter()
            }
        } catch (throwable: Throwable) {
            RxJavaPlugins.getInstance().errorHandler.handleError(throwable)
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
        checkViewAttached()
        mSubscriptions.add(
            mDataManagerCenter.syncCenterAccounts(centerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<CenterAccounts>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showError(R.string.failed_to_sync_centeraccounts)

                        //Updating UI
                        mFailedSyncCenter.add(mCenterList[mCenterSyncIndex])
                        mvpView?.showSyncedFailedCenters(mFailedSyncCenter.size)
                        mCenterSyncIndex += 1
                        checkNetworkConnectionAndSyncCenter()
                    }

                    override fun onNext(centerAccounts: CenterAccounts) {
                        mLoanAccountList = Utils.getActiveLoanAccounts(
                            centerAccounts
                                .loanAccounts
                        )
                        mSavingsAccountList = Utils.getActiveSavingsAccounts(
                            centerAccounts
                                .savingsAccounts
                        )
                        mMemberLoanAccountsList = Utils.getActiveLoanAccounts(
                            centerAccounts
                                .memberLoanAccounts
                        )
                        //Updating UI
                        mvpView?.maxSingleSyncCenterProgressBar = (mLoanAccountList.size +
                                mSavingsAccountList.size + mMemberLoanAccountsList.size)
                        checkAccountsSyncStatusAndSyncAccounts()
                    }
                })
        )
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
            //Sync the Active Loan and LoanRepayment
            checkNetworkConnectionAndSyncLoanAndLoanRepayment()
        } else if (mSavingsAccountList.isNotEmpty() && !mSavingAccountSyncStatus) {
            //Sync the Active Savings Account
            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
        } else if (mMemberLoanAccountsList.isNotEmpty()) {
            //Sync the Active member Loan Account
            checkNetworkConnectionAndSyncMemberLoanAndMemberLoanRepayment()
        } else {
            mvpView?.maxSingleSyncCenterProgressBar = 1
            loadCenterAssociateGroups(mCenterList[mCenterSyncIndex].id)
        }
    }

    /**
     * This Method Checking network connection and  Syncing the LoanAndLoanRepayment.
     * If found no internet connection then stop syncing the Centers and dismiss the dialog.
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
     * This Method Checking network connection and  Syncing the MemberLoanAndMemberLoanRepayment.
     * If found no internet connection then stop syncing the Centers and dismiss the dialog.
     */
    private fun checkNetworkConnectionAndSyncMemberLoanAndMemberLoanRepayment() {
        if (mvpView?.isOnline == true) {
            mMemberLoanAccountsList[mMemberLoanSyncIndex].id?.let {
                syncMemberLoanAndMemberLoanRepayment(
                    it
                )
            }
        } else {
            mvpView?.showNetworkIsNotAvailable()
            mvpView?.dismissDialog()
        }
    }

    /**
     * This method checking the network connection and syncing the SavingsAccountAndTransactions.
     * If found no internet connection then stop syncing the centers and dismiss the dialog.
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
                        mvpView?.updateSingleSyncCenterProgressBar(mLoanAndRepaymentSyncIndex)
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
     * This Method Syncing the Member's Loan and their LoanRepayment. This is the
     * Observable.combineLatest In Which two request is going to server Loans and LoanRepayment
     * and This request will not complete till that both request completed successfully with
     * response (200 OK). If one will fail then response will come in onError. and If both
     * request is 200 response then response will come in onNext.
     *
     * @param loanId Loan Id
     */
    private fun syncMemberLoanAndMemberLoanRepayment(loanId: Int) {
        checkViewAttached()
        mSubscriptions.add(
            getLoanAndLoanRepayment(loanId)
                .subscribe(object : Subscriber<LoanAndLoanRepayment>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        onAccountSyncFailed(e)
                    }

                    override fun onNext(loanAndLoanRepayment: LoanAndLoanRepayment) {
                        mMemberLoanSyncIndex += 1
                        mvpView?.updateSingleSyncCenterProgressBar(
                            (mLoanAndRepaymentSyncIndex
                                    + mSavingsAndTransactionSyncIndex + mMemberLoanSyncIndex)
                        )
                        if (mMemberLoanSyncIndex != mMemberLoanAccountsList.size) {
                            checkNetworkConnectionAndSyncMemberLoanAndMemberLoanRepayment()
                        } else {
                            loadCenterAssociateGroups(mCenterList[mCenterSyncIndex].id)
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
                        mvpView?.updateSingleSyncCenterProgressBar(
                            mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex
                        )
                        if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
                        } else {
                            setSavingAccountSyncStatusTrue()
                            checkAccountsSyncStatusAndSyncAccounts()
                        }
                    }
                })
        )
    }

    /**
     * This Method syncing the Center into the Database.
     *
     * @param center Center
     */
    private fun syncCenter(center: Center) {
        checkViewAttached()
        center.isSync = true
        mSubscriptions.add(
            mDataManagerCenter.syncCenterInDatabase(center)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(Action1 {
                    mvpView?.updateGroupSyncProgressBar(0)
                    mvpView?.updateSingleSyncCenterProgressBar(
                        mvpView!!.maxSingleSyncCenterProgressBar
                    )
                    mCenterSyncIndex += 1
                    checkNetworkConnectionAndSyncCenter()
                })
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
            mDataManagerLoan.syncLoanRepaymentTemplate(loanId),
            object : Func2<LoanWithAssociations?, LoanRepaymentTemplate?, LoanAndLoanRepayment> {
                override fun call(
                    loanWithAssociations: LoanWithAssociations?,
                    loanRepaymentTemplate: LoanRepaymentTemplate?
                ): LoanAndLoanRepayment {
                    return LoanAndLoanRepayment(
                        loanWithAssociations,
                        loanRepaymentTemplate
                    )
                }
            })
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
            ),
            object :
                Func2<SavingsAccountWithAssociations?, SavingsAccountTransactionTemplate?, SavingsAccountAndTransactionTemplate> {
                override fun call(
                    savingsAccountWithAssociations: SavingsAccountWithAssociations?,
                    savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate?
                ): SavingsAccountAndTransactionTemplate {
                    return SavingsAccountAndTransactionTemplate(
                        savingsAccountWithAssociations, savingsAccountTransactionTemplate
                    )
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    /**
     * This Method fetching the Center Associate Groups List.
     *
     * @param centerId Center Id
     */
    private fun loadCenterAssociateGroups(centerId: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerCenter.getCenterWithAssociations(centerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<CenterWithAssociations>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        onAccountSyncFailed(e)
                    }

                    override fun onNext(centerWithAssociations: CenterWithAssociations) {
                        mGroups = centerWithAssociations.groupMembers
                        mGroupSyncIndex = 0
                        resetIndexes()
                        if (mGroups.isNotEmpty()) {
                            mvpView?.setGroupSyncProgressBarMax(mGroups.size)
                            syncGroupAccounts(mGroups[mGroupSyncIndex].id)
                        } else {
                            syncCenter(mCenterList[mCenterSyncIndex])
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
                        mClients = Utils.getActiveClients(groupWithAssociations.clientMembers)
                        mClientSyncIndex = 0
                        resetIndexes()
                        if (mClients.isNotEmpty()) {
                            mvpView?.setClientSyncProgressBarMax(mClients.size)
                            syncClientAccounts(mClients[mClientSyncIndex].id)
                        } else {
                            syncGroup(mGroups[mGroupSyncIndex])
                        }
                    }
                })
        )
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
                            groupAccounts
                                .loanAccounts
                        )
                        mSavingsAccountList = Utils.getActiveSavingsAccounts(
                            groupAccounts
                                .savingsAccounts
                        )
                        checkAccountsSyncStatusAndSyncGroupAccounts()
                    }
                })
        )
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
     * This Method check the Group LoanAccount isEmpty or not, If LoanAccount is not Empty the sync
     * the loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty the sync the groups locally, Yep Groups has been synced.
     */
    private fun checkAccountsSyncStatusAndSyncGroupAccounts() {
        if (!mLoanAccountList.isEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let {
                syncGroupLoanAndLoanRepayment(
                    it
                )
            }
        } else if (!mSavingsAccountList.isEmpty()) {
            //Sync the Active Savings Account
            syncGroupSavingsAccountAndTemplate(
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType.endpoint,
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].id
            )
        } else {
            loadGroupAssociateClients(mGroups[mGroupSyncIndex].id)
        }
    }

    /**
     * This Method check the Client LoanAccount isEmpty or not, If LoanAccount is not Empty the sync
     * the loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty the sync the groups locally, Yep Groups has been synced.
     */
    private fun checkAccountsSyncStatusAndSyncClientAccounts() {
        if (!mLoanAccountList.isEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let {
                syncClientLoanAndLoanRepayment(
                    it
                )
            }
        } else if (!mSavingsAccountList.isEmpty()) {
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
     * This Method Saving the Groups to Database, If their Accounts, Loan and LoanRepayment
     * saved Synced successfully.
     *
     * @param group
     */
    private fun syncGroup(group: Group) {
        checkViewAttached()
        group.centerId = mCenterList[mCenterSyncIndex].id
        group.isSync = true
        mSubscriptions.add(
            mDataManagerGroups.syncGroupInDatabase(group)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Group>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showError(R.string.failed_to_sync_client)
                    }

                    override fun onNext(Group: Group) {
                        resetIndexes()
                        mvpView?.updateClientSyncProgressBar(0)
                        mGroupSyncIndex += 1
                        mvpView?.updateGroupSyncProgressBar(mGroupSyncIndex)
                        if (mGroups.size == mGroupSyncIndex) {
                            syncCenter(mCenterList[mCenterSyncIndex])
                        } else {
                            syncGroupAccounts(mGroups[mGroupSyncIndex].id)
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
        client.groupId = mGroups.get(mGroupSyncIndex).id
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
                            syncGroup(mGroups[mGroupSyncIndex])
                        } else {
                            syncClientAccounts(mClients[mClientSyncIndex].id)
                        }
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
    private fun syncGroupLoanAndLoanRepayment(loanId: Int) {
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
                                syncGroupLoanAndLoanRepayment(
                                    it
                                )
                            }
                        } else {
                            setLoanAccountSyncStatusTrue()
                            checkAccountsSyncStatusAndSyncGroupAccounts()
                        }
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
     * This Method Fetching the Groups SavingsAccount and SavingsAccountTransactionTemplate and Sync
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private fun syncGroupSavingsAccountAndTemplate(
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
                            syncGroupSavingsAccountAndTemplate(
                                mSavingsAccountList[mSavingsAndTransactionSyncIndex]
                                    .depositType.endpoint,
                                mSavingsAccountList[mSavingsAndTransactionSyncIndex].id
                            )
                        } else {
                            loadGroupAssociateClients(mGroups[mGroupSyncIndex].id)
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

    private fun updateTotalSyncProgressBarAndCount() {
        mvpView?.updateTotalSyncCenterProgressBarAndCount(mCenterSyncIndex)
    }

    private fun updateCenterName() {
        val centerName = mCenterList[mCenterSyncIndex].name
        mvpView?.showSyncingCenter(centerName)
    }
}