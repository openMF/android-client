package com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition

import com.mifos.api.datamanager.DataManagerLoan
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.objects.accounts.loan.LoanRepaymentResponse
import com.mifos.utils.MFErrorParser.errorMessage
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 28/07/16.
 */
class SyncLoanRepaymentTransactionPresenter @Inject constructor(val mDataManagerLoan: DataManagerLoan) :
    BasePresenter<SyncLoanRepaymentTransactionMvpView>() {
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()

    override fun attachView(mvpView: SyncLoanRepaymentTransactionMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun loadDatabaseLoanRepaymentTransactions() {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerLoan.databaseLoanRepayments
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<LoanRepaymentRequest>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showError(R.string.failed_to_load_loanrepayment)
                }

                override fun onNext(loanRepaymentRequests: List<LoanRepaymentRequest>) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showLoanRepaymentTransactions(loanRepaymentRequests)
                }
            })
        )
    }

    fun loanPaymentTypeOption() {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerLoan
            .paymentTypeOption
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
                    mvpView?.showPaymentTypeOption(paymentTypeOptions)
                }
            })
        )
    }

    fun syncLoanRepayment(loanId: Int, loanRepaymentRequest: LoanRepaymentRequest?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerLoan.submitPayment(loanId, loanRepaymentRequest!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanRepaymentResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showPaymentFailed(errorMessage(e)!!)
                }

                override fun onNext(loanRepaymentResponse: LoanRepaymentResponse) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showPaymentSubmittedSuccessfully()
                }
            })
        )
    }

    fun deleteAndUpdateLoanRepayments(loanId: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerLoan.deleteAndUpdateLoanRepayments(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<LoanRepaymentRequest>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_update_list)
                    }

                    override fun onNext(loanRepaymentRequests: List<LoanRepaymentRequest>) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showLoanRepaymentDeletedAndUpdateLoanRepayment(
                            loanRepaymentRequests
                        )
                    }
                })
        )
    }

    fun updateLoanRepayment(loanRepaymentRequest: LoanRepaymentRequest?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerLoan.updateLoanRepaymentTransaction(loanRepaymentRequest!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<LoanRepaymentRequest>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_load_loanrepayment)
                    }

                    override fun onNext(loanRepaymentRequest: LoanRepaymentRequest) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showLoanRepaymentUpdated(loanRepaymentRequest)
                    }
                })
        )
    }
}