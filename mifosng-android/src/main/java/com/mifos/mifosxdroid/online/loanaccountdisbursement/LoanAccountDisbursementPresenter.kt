package com.mifos.mifosxdroid.online.loanaccountdisbursement

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.api.model.APIEndPoint
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.loan.LoanDisbursement
import com.mifos.objects.templates.loans.LoanTransactionTemplate
import com.mifos.utils.MFErrorParser
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 8/6/16.
 */
class LoanAccountDisbursementPresenter @Inject constructor(private val dataManagerLoan: DataManagerLoan) : BasePresenter<LoanAccountDisbursementMvpView?>() {
    private val subscriptions: CompositeSubscription
    override fun attachView(mvpView: LoanAccountDisbursementMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        subscriptions.unsubscribe()
    }

    fun loadLoanTemplate(loanId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        subscriptions.add(dataManagerLoan.getLoanTransactionTemplate(loanId, APIEndPoint.DISBURSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<LoanTransactionTemplate?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError("s")
                    }

                    override fun onNext(loanTransactionTemplate: LoanTransactionTemplate?) {
                        mvpView!!.showProgressbar(false)
                        if (loanTransactionTemplate != null) {
                            mvpView!!.showLoanTransactionTemplate(loanTransactionTemplate)
                        }
                    }
                })
        )
    }

    fun disburseLoan(loanId: Int, loanDisbursement: LoanDisbursement?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        subscriptions.add(dataManagerLoan.disburseLoan(loanId, loanDisbursement)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(genericResponse: GenericResponse?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showDisburseLoanSuccessfully(genericResponse)
                    }
                }))
    }

    init {
        subscriptions = CompositeSubscription()
    }
}