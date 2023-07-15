package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog

import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.ClientAccounts
import com.mifos.objects.accounts.loan.LoanAccount
import com.mifos.objects.accounts.savings.SavingsAccount
import com.mifos.objects.client.Client
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
 * Created by Rajan Maurya on 08/08/16.
 */
class SyncClientsDialogPresenter @Inject constructor(
    private val mDataManagerClient: DataManagerClient,
    private val mDataManagerLoan: DataManagerLoan,
    private val mDataManagerSavings: DataManagerSavings
) : BasePresenter<SyncClientsDialogMvpView>() {
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()
    private var mClientList: List<Client>
    private val mFailedSyncClient: MutableList<Client>
    private var mLoanAccountList: List<LoanAccount>
    private var mSavingsAccountList: List<SavingsAccount>
    private var mLoanAccountSyncStatus = false
    private var mClientSyncIndex = 0
    private var mLoanAndRepaymentSyncIndex = 0
    private var mSavingsAndTransactionSyncIndex = 0

    init {
        mClientList = ArrayList()
        mFailedSyncClient = ArrayList()
        mLoanAccountList = ArrayList()
        mSavingsAccountList = ArrayList()
    }

    override fun attachView(mvpView: SyncClientsDialogMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    /**
     * This Method Start Syncing Clients. Start Syncing the Client Accounts.
     *
     * @param clients Selected Clients For Syncing
     */
    fun startSyncingClients(clients: List<Client>) {
        mClientList = clients
        checkNetworkConnectionAndSyncClient()
    }

    private fun syncClientAndUpdateUI() {
        mLoanAndRepaymentSyncIndex = 0
        mSavingsAndTransactionSyncIndex = 0
        mLoanAccountSyncStatus = false
        updateTotalSyncProgressBarAndCount()
        if (mClientSyncIndex != mClientList.size) {
            updateClientName()
            syncClientAccounts(mClientList[mClientSyncIndex].id)
        } else {
            mvpView?.showClientsSyncSuccessfully()
        }
    }

    fun checkNetworkConnectionAndSyncClient() {
        if (mvpView?.isOnline == true) {
            syncClientAndUpdateUI()
        } else {
            mvpView?.showNetworkIsNotAvailable()
            mvpView?.dismissDialog()
        }
    }

    fun checkNetworkConnectionAndSyncLoanAndLoanRepayment() {
        if (mvpView?.isOnline == true) {
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let { syncLoanAndLoanRepayment(it) }
        } else {
            mvpView?.showNetworkIsNotAvailable()
            mvpView?.dismissDialog()
        }
    }

    fun checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate() {
        if (mvpView?.isOnline == true) {
            mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let {
                syncSavingsAccountAndTemplate(
                    mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType?.endpoint,
                    it
                )
            }
        } else {
            mvpView?.showNetworkIsNotAvailable()
            mvpView?.dismissDialog()
        }
    }

    fun checkAccountsSyncStatusAndSyncAccounts() {
        if (mLoanAccountList.isNotEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            checkNetworkConnectionAndSyncLoanAndLoanRepayment()
        } else if (mSavingsAccountList.isNotEmpty()) {
            //Sync the Active Savings Account
            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
        } else {
            // If LoanAccounts and SavingsAccount are null then sync Client to Database
            mvpView?.maxSingleSyncClientProgressBar = 1
            syncClient(mClientList[mClientSyncIndex])
        }
    }

    fun setLoanAccountSyncStatusTrue() {
        if (mLoanAndRepaymentSyncIndex == mLoanAccountList.size) {
            mLoanAccountSyncStatus = true
        }
    }

    fun onAccountSyncFailed(e: Throwable?) {
        try {
            if (e is HttpException) {
                val singleSyncClientMax = mvpView?.maxSingleSyncClientProgressBar
                if (singleSyncClientMax != null) {
                    mvpView?.updateSingleSyncClientProgressBar(singleSyncClientMax)
                }
                mFailedSyncClient.add(mClientList[mClientSyncIndex])
                mClientSyncIndex += 1
                mvpView?.showSyncedFailedClients(mFailedSyncClient.size)
                checkNetworkConnectionAndSyncClient()
            }
        } catch (throwable: Throwable) {
            RxJavaPlugins.getInstance().errorHandler.handleError(throwable)
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
                        mvpView?.showError(R.string.failed_to_sync_clientaccounts)

                        //Updating UI
                        mFailedSyncClient.add(mClientList[mClientSyncIndex])
                        mvpView?.showSyncedFailedClients(mFailedSyncClient.size)
                        mClientSyncIndex += 1
                        checkNetworkConnectionAndSyncClient()
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

                        //Updating UI
                        mvpView?.maxSingleSyncClientProgressBar = mLoanAccountList.size +
                                mSavingsAccountList.size
                        checkAccountsSyncStatusAndSyncAccounts()
                    }
                })
        )
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
        checkViewAttached()
        mSubscriptions.add(
            Observable.combineLatest(
                mDataManagerLoan.syncLoanById(loanId),
                mDataManagerLoan.syncLoanRepaymentTemplate(loanId)
            ) { loanWithAssociations, loanRepaymentTemplate ->
                val loanAndLoanRepayment = LoanAndLoanRepayment()
                loanAndLoanRepayment.loanWithAssociations = loanWithAssociations
                loanAndLoanRepayment.loanRepaymentTemplate = loanRepaymentTemplate
                loanAndLoanRepayment
            }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<LoanAndLoanRepayment>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        onAccountSyncFailed(e)
                    }

                    override fun onNext(loanAndLoanRepayment: LoanAndLoanRepayment) {
                        mLoanAndRepaymentSyncIndex += 1
                        mvpView?.updateSingleSyncClientProgressBar(mLoanAndRepaymentSyncIndex)
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
     * This Method Fetch SavingsAccount and SavingsAccountTransactionTemplate and Sync them in
     * Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private fun syncSavingsAccountAndTemplate(savingsAccountType: String?, savingsAccountId: Int) {
        checkViewAttached()
        mSubscriptions.add(
            Observable.combineLatest(
                mDataManagerSavings.syncSavingsAccount(
                    savingsAccountType, savingsAccountId,
                    Constants.TRANSACTIONS
                ),
                mDataManagerSavings.syncSavingsAccountTransactionTemplate(
                    savingsAccountType,
                    savingsAccountId, Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT
                )
            ) { savingsAccountWithAssociations, savingsAccountTransactionTemplate ->
                val accountAndTransactionTemplate = SavingsAccountAndTransactionTemplate()
                accountAndTransactionTemplate.savingsAccountTransactionTemplate =
                    savingsAccountTransactionTemplate
                accountAndTransactionTemplate.savingsAccountWithAssociations =
                    savingsAccountWithAssociations
                accountAndTransactionTemplate
            }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountAndTransactionTemplate>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        onAccountSyncFailed(e)
                    }

                    override fun onNext(savingsAccountAndTransactionTemplate: SavingsAccountAndTransactionTemplate) {
                        mSavingsAndTransactionSyncIndex += 1
                        mvpView?.updateSingleSyncClientProgressBar(
                            mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex
                        )
                        if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
                        } else {
                            syncClient(mClientList[mClientSyncIndex])
                        }
                    }
                })
        )
    }

    /**
     * This Method Saving the Clients to Database, If their Accounts, Loan and LoanRepayment
     * saved successfully to Synced.
     *
     * @param client
     */
    fun syncClient(client: Client) {
        checkViewAttached()
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
                        val singleSyncClientMax = mvpView?.maxSingleSyncClientProgressBar
                        if (singleSyncClientMax != null) {
                            mvpView?.updateSingleSyncClientProgressBar(singleSyncClientMax)
                        }
                        mClientSyncIndex += 1
                        checkNetworkConnectionAndSyncClient()
                    }
                })
        )
    }

    private fun updateTotalSyncProgressBarAndCount() {
        mvpView?.updateTotalSyncClientProgressBarAndCount(mClientSyncIndex)
    }

    private fun updateClientName() {
        val clientName = mClientList[mClientSyncIndex].firstname +
                mClientList[mClientSyncIndex].lastname
        mvpView?.showSyncingClient(clientName)
    }
}