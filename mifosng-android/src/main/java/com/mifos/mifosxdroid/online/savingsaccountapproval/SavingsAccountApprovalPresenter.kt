package com.mifos.mifosxdroid.online.savingsaccountapproval

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.loan.SavingsApproval
import com.mifos.utils.MFErrorParser
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 09/06/16.
 */
class SavingsAccountApprovalPresenter @Inject constructor(private val dataManagerSavings: DataManagerSavings) : BasePresenter<SavingsAccountApprovalMvpView?>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: SavingsAccountApprovalMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription!!.unsubscribe()
    }

    fun approveSavingsApplication(savingsAccountId: Int, savingsApproval: SavingsApproval?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = dataManagerSavings
                .approveSavingsApplication(savingsAccountId, savingsApproval)
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
                        mvpView!!.showSavingAccountApprovedSuccessfully(genericResponse)
                    }
                })
    }

}