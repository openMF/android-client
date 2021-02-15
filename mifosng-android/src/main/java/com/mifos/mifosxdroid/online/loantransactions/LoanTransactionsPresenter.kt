package com.mifos.mifosxdroid.online.loantransactions

import com.mifos.api.DataManager
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.loan.LoanWithAssociations
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 7/6/16.
 */
class LoanTransactionsPresenter @Inject constructor(private val mDataManager: DataManager) : BasePresenter<LoanTransactionsMvpView?>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: LoanTransactionsMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription!!.unsubscribe()
    }

    fun loadLoanTransaction(loan: Int) {
        checkViewAttached()
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = mDataManager.getLoanTransactions(loan)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<LoanWithAssociations?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showFetchingError("Failed to fetch LoanTransaction")
                    }

                    override fun onNext(loanWithAssociations: LoanWithAssociations?) {
                        loanWithAssociations?.let { mvpView!!.showLoanTransaction(it) }
                    }
                })
    }

}