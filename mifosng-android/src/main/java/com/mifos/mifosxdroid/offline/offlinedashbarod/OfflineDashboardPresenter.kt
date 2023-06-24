package com.mifos.mifosxdroid.offline.offlinedashbarod

import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.objects.client.ClientPayload
import com.mifos.objects.group.GroupPayload
import com.mifos.services.data.CenterPayload
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 20/07/16.
 */
class OfflineDashboardPresenter @Inject constructor(
    dataManagerClient: DataManagerClient,
    dataManagerGroups: DataManagerGroups,
    dataManagerCenter: DataManagerCenter,
    dataManagerLoan: DataManagerLoan,
    dataManagerSavings: DataManagerSavings
) : BasePresenter<OfflineDashboardMvpView>() {
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()
    private val mDataManagerClient: DataManagerClient
    private val mDataManagerGroups: DataManagerGroups
    private val mDataManagerCenters: DataManagerCenter
    private val mDataManagerLoan: DataManagerLoan
    private val mDataManagerSavings: DataManagerSavings

    init {
        mDataManagerClient = dataManagerClient
        mDataManagerGroups = dataManagerGroups
        mDataManagerCenters = dataManagerCenter
        mDataManagerLoan = dataManagerLoan
        mDataManagerSavings = dataManagerSavings
    }

    override fun attachView(mvpView: OfflineDashboardMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun loadDatabaseClientPayload() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerClient.allDatabaseClientPayload
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<ClientPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView!!.showProgressbar(false)
                    mvpView!!.showError(R.string.failed_to_load_clientpayload)
                }

                override fun onNext(clientPayloads: List<ClientPayload>) {
                    mvpView!!.showClients(clientPayloads)
                    mvpView!!.showProgressbar(false)
                }
            })
        )
    }

    fun loadDatabaseGroupPayload() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerGroups.allDatabaseGroupPayload
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<GroupPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView!!.showProgressbar(false)
                    mvpView!!.showError(R.string.failed_to_load_grouppayload)
                }

                override fun onNext(groupPayloads: List<GroupPayload>) {
                    mvpView!!.showGroups(groupPayloads)
                    mvpView!!.showProgressbar(false)
                }
            })
        )
    }

    fun loadDatabaseCenterPayload() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerCenters.allDatabaseCenterPayload
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<CenterPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView!!.showProgressbar(false)
                    mvpView!!.showError(R.string.failed_to_load_centerpayload)
                }

                override fun onNext(centerPayloads: List<CenterPayload>) {
                    mvpView!!.showCenters(centerPayloads)
                    mvpView!!.showProgressbar(false)
                }
            })
        )
    }

    fun loadDatabaseLoanRepaymentTransactions() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerLoan.databaseLoanRepayments
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<LoanRepaymentRequest>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView!!.showProgressbar(false)
                    mvpView!!.showError(R.string.failed_to_load_loanrepayment)
                }

                override fun onNext(loanRepaymentRequests: List<LoanRepaymentRequest>) {
                    mvpView!!.showProgressbar(false)
                    mvpView!!.showLoanRepaymentTransactions(loanRepaymentRequests)
                }
            })
        )
    }

    fun loadDatabaseSavingsAccountTransactions() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerSavings.allSavingsAccountTransactions
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<SavingsAccountTransactionRequest>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(R.string.failed_to_load_savingaccounttransaction)
                    }

                    override fun onNext(transactionRequests: List<SavingsAccountTransactionRequest>) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showSavingsAccountTransaction(transactionRequests)
                    }
                })
        )
    }
}