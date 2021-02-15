package com.mifos.mifosxdroid.online.loanrepaymentschedule

import com.mifos.api.DataManager
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.loan.LoanWithAssociations
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 08/06/16.
 */
class LoanRepaymentSchedulePresenter @Inject constructor(private val mDataManager: DataManager) : BasePresenter<LoanRepaymentScheduleMvpView?>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: LoanRepaymentScheduleMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription!!.unsubscribe()
    }

    fun loadLoanRepaySchedule(loanId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = mDataManager.getLoanRepaySchedule(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<LoanWithAssociations?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError("Failed to load LoanRepayment")
                    }

                    override fun onNext(loanWithAssociations: LoanWithAssociations?) {
                        mvpView!!.showProgressbar(false)
                        loanWithAssociations?.let { mvpView!!.showLoanRepaySchedule(it) }
                    }
                })
    }

}