package com.mifos.mifosxdroid.online.datatablelistfragment

import com.mifos.api.DataManager
import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.client.Client
import com.mifos.objects.client.ClientPayload
import com.mifos.services.data.GroupLoanPayload
import com.mifos.services.data.LoansPayload
import com.mifos.utils.MFErrorParser
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class DataTableListPresenter @Inject constructor(private val mDataManagerLoan: DataManagerLoan, private val mDataManager: DataManager,
                                                 private val dataManagerClient: DataManagerClient) : BasePresenter<DataTableListMvpView?>() {
    private val mSubscription: CompositeSubscription?
    override fun attachView(mvpView: DataTableListMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscription?.unsubscribe()
    }

    fun createLoansAccount(loansPayload: LoansPayload?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription!!.add(mDataManagerLoan.createLoansAccount(loansPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Loans?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showMessage(R.string.generic_failure_message)
                    }

                    override fun onNext(loans: Loans?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showMessage(R.string.loan_creation_success)
                    }
                }))
    }

    fun createGroupLoanAccount(loansPayload: GroupLoanPayload?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription!!.add(mDataManager.createGroupLoansAccount(loansPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Loans?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showMessage(R.string.generic_failure_message)
                    }

                    override fun onNext(loans: Loans?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showMessage(R.string.loan_creation_success)
                    }
                })
        )
    }

    fun createClient(clientPayload: ClientPayload?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription!!.add(dataManagerClient.createClient(clientPayload)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeOn(Schedulers.io())
                ?.subscribe(object : Subscriber<Client?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showMessage(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(client: Client?) {
                        mvpView!!.showProgressbar(false)
                        if (client != null) {
                            if (client.clientId != null) {
                                mvpView!!.showClientCreatedSuccessfully(
                                        client)
                            } else {
                                mvpView!!.showWaitingForCheckerApproval(
                                        R.string.waiting_for_checker_approval)
                            }
                        }
                    }
                }))
    }

    init {
        mSubscription = CompositeSubscription()
    }
}