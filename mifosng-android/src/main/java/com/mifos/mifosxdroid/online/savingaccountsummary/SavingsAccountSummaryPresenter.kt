package com.mifos.mifosxdroid.online.savingaccountsummary

import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 07/06/16.
 */
class SavingsAccountSummaryPresenter @Inject constructor(private val mDataManagerSavings: DataManagerSavings) : BasePresenter<SavingsAccountSummaryMvpView?>() {
    private val mSubscriptions: CompositeSubscription
    override fun attachView(mvpView: SavingsAccountSummaryMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    //This Method will hit end point ?associations=transactions
    fun loadSavingAccount(type: String?, accountId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerSavings
                .getSavingsAccount(type, accountId, Constants.TRANSACTIONS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SavingsAccountWithAssociations?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_fetch_savingsaccount)
                    }

                    override fun onNext(savingsAccountWithAssociations: SavingsAccountWithAssociations?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showSavingAccount(savingsAccountWithAssociations)
                    }
                }))
    }

    init {
        mSubscriptions = CompositeSubscription()
    }
}