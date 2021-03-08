package com.mifos.mifosxdroid.online.loanaccount

import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.loan.Loans
import com.mifos.objects.organisation.LoanProducts
import com.mifos.objects.templates.loans.LoanTemplate
import com.mifos.services.data.LoansPayload
import com.mifos.utils.MFErrorParser
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 08/06/16.
 */
class LoanAccountPresenter @Inject constructor(private val mDataManagerLoan: DataManagerLoan) : BasePresenter<LoanAccountMvpView?>() {
    private val mSubscriptions: CompositeSubscription
    override fun attachView(mvpView: LoanAccountMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun loadAllLoans() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerLoan.allLoans
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<LoanProducts?>?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showMessage(R.string.failed_to_fetch_loan_products)
                    }

                    override fun onNext(productLoanses: List<LoanProducts?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showAllLoan(productLoanses as List<LoanProducts>)
                    }
                }))
    }

    fun loadLoanAccountTemplate(clientId: Int, productId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerLoan.getLoansAccountTemplate(clientId, productId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<LoanTemplate?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showMessage(R.string.failed_to_fetch_loan_template)
                    }

                    override fun onNext(loanTemplate: LoanTemplate?) {
                        mvpView!!.showProgressbar(false)
                        if (loanTemplate != null) {
                            mvpView!!.showLoanAccountTemplate(loanTemplate)
                        }
                    }
                }))
    }

    fun createLoansAccount(loansPayload: LoansPayload?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerLoan.createLoansAccount(loansPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Loans?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(loans: Loans?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showLoanAccountCreatedSuccessfully(loans)
                    }
                }))
    }

    init {
        mSubscriptions = CompositeSubscription()
    }
}