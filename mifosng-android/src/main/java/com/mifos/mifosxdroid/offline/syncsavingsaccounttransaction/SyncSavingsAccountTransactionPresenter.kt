package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction

import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.utils.MFErrorParser.errorMessage
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 19/08/16.
 */
class SyncSavingsAccountTransactionPresenter @Inject constructor(
    val mDataManagerSavings: DataManagerSavings,
    val mDataManagerLoan: DataManagerLoan
) : BasePresenter<SyncSavingsAccountTransactionMvpView>() {
    val LOG_TAG = javaClass.simpleName
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()
    private var mSavingsAccountTransactionRequests: MutableList<SavingsAccountTransactionRequest>
    private var mTransactionIndex = 0
    private var mTransactionsFailed = 0

    init {
        mSavingsAccountTransactionRequests = ArrayList()
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun syncSavingsAccountTransactions() {
        if (mSavingsAccountTransactionRequests.size != 0) {
            mTransactionIndex = 0
            checkErrorAndSync()
        } else {
            mvpView?.showError(R.string.nothing_to_sync)
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
                processTransaction(
                    savingAccountType, savingAccountId, transactionType,
                    mSavingsAccountTransactionRequests[i]
                )
                break
            } else if (checkTransactionsSyncBeforeOrNot()) {
                mvpView?.showError(R.string.error_fix_before_sync)
            }
        }
    }

    /**
     * This Method delete the SavingsAccountTransactionRequest from Database and load again
     * List<SavingsAccountTransactionRequest> and Update the UI.
    </SavingsAccountTransactionRequest> */
    fun showTransactionSyncSuccessfully() {
        deleteAndUpdateSavingsAccountTransaction(
            mSavingsAccountTransactionRequests[mTransactionIndex].savingAccountId
        )
    }

    /**
     * This Method will be called whenever Transaction sync failed. This Method set the Error
     * message to the SavingsAccountTransactionRequest and update
     * SavingsAccountTransactionRequest into the Database
     *
     * @param errorMessage Server Error Message
     */
    fun showTransactionSyncFailed(errorMessage: String?) {
        val transaction = mSavingsAccountTransactionRequests[mTransactionIndex]
        transaction.errorMessage = errorMessage
        updateSavingsAccountTransaction(transaction)
    }

    /**
     * This Method will be called when Transaction will be sync successfully and deleted from
     * Database then This Method Start Syncing next Transaction.
     *
     * @param transaction SavingsAccountTransactionRequest
     */
    fun showTransactionUpdatedSuccessfully(transaction: SavingsAccountTransactionRequest) {
        mSavingsAccountTransactionRequests[mTransactionIndex] = transaction
        mvpView?.showSavingsAccountTransactions(mSavingsAccountTransactionRequests)
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
    fun showTransactionDeletedAndUpdated(transactions: MutableList<SavingsAccountTransactionRequest>) {
        mTransactionIndex = 0
        mSavingsAccountTransactionRequests = transactions
        mvpView?.showSavingsAccountTransactions(transactions)
        if (mSavingsAccountTransactionRequests.size != 0) {
            syncSavingsAccountTransactions()
        } else {
            mvpView?.showEmptySavingsAccountTransactions(R.string.nothing_to_sync)
        }
    }

    private fun checkTransactionsSyncBeforeOrNot(): Boolean {
        Observable.from(mSavingsAccountTransactionRequests)
            .filter { savingsAccountTransactionRequest -> savingsAccountTransactionRequest.errorMessage != null }
            .subscribe { mTransactionsFailed += 1 }
        return mTransactionsFailed == mSavingsAccountTransactionRequests.size
    }

    /**
     * This Method Load the List<SavingsAccountTransactionRequest> from
     * SavingsAccountTransactionRequest_Table and Update the UI
    </SavingsAccountTransactionRequest> */
    fun loadDatabaseSavingsAccountTransactions() {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerSavings.allSavingsAccountTransactions
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<SavingsAccountTransactionRequest>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_load_savingaccounttransaction)
                    }

                    override fun onNext(transactionRequests: List<SavingsAccountTransactionRequest>) {
                        mvpView?.showProgressbar(false)
                        if (transactionRequests.isNotEmpty()) {
                            mvpView?.showSavingsAccountTransactions(transactionRequests)
                            mSavingsAccountTransactionRequests =
                                transactionRequests as MutableList<SavingsAccountTransactionRequest>
                        } else {
                            mvpView?.showEmptySavingsAccountTransactions(
                                R.string.no_transaction_to_sync
                            )
                        }
                    }
                })
        )
    }

    /**
     * THis Method Load the Payment Type Options from Database PaymentTypeOption_Table
     * and update the UI.
     */
    fun loadPaymentTypeOption() {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerLoan.paymentTypeOption
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<PaymentTypeOption>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_load_paymentoptions)
                    }

                    override fun onNext(paymentTypeOptions: List<PaymentTypeOption>) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showPaymentTypeOptions(paymentTypeOptions)
                    }
                })
        )
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
        type: String?, accountId: Int, transactionType: String?,
        request: SavingsAccountTransactionRequest?
    ) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerSavings
                .processTransaction(type, accountId, transactionType, request!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        showTransactionSyncFailed(errorMessage(e))
                    }

                    override fun onNext(savingsAccountTransactionResponse: SavingsAccountTransactionResponse) {
                        mvpView?.showProgressbar(false)
                        showTransactionSyncSuccessfully()
                    }
                })
        )
    }

    /**
     * This Method delete the SavingsAccountTransactionRequest from the Database and load again
     * List<SavingsAccountTransactionRequest> from the SavingsAccountTransactionRequest_Table.
     * and returns the List<SavingsAccountTransactionRequest>.
     *
     * @param savingsAccountId SavingsAccountTransactionRequest's SavingsAccount Id
    </SavingsAccountTransactionRequest></SavingsAccountTransactionRequest> */
    private fun deleteAndUpdateSavingsAccountTransaction(savingsAccountId: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerSavings.deleteAndUpdateTransactions(savingsAccountId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<SavingsAccountTransactionRequest>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_update_list)
                    }

                    override fun onNext(savingsAccountTransactionRequests: List<SavingsAccountTransactionRequest>) {
                        mvpView?.showProgressbar(false)
                        showTransactionDeletedAndUpdated(savingsAccountTransactionRequests as MutableList<SavingsAccountTransactionRequest>)
                    }
                })
        )
    }

    /**
     * This Method Update the SavingsAccountTransactionRequest in the Database. This will be called
     * whenever any transaction will be failed to sync then the sync developer error message will
     * be added to SavingsAccountTransactionRequest to update in Database.
     *
     * @param request SavingsAccountTransactionRequest
     */
    private fun updateSavingsAccountTransaction(request: SavingsAccountTransactionRequest?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerSavings.updateLoanRepaymentTransaction(request!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountTransactionRequest>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_update_savingsaccount)
                    }

                    override fun onNext(savingsAccountTransactionRequest: SavingsAccountTransactionRequest) {
                        mvpView?.showProgressbar(false)
                        showTransactionUpdatedSuccessfully(savingsAccountTransactionRequest)
                    }
                })
        )
    }
}