package com.mifos.mifosxdroid.online.loanaccountapproval

import com.mifos.api.DataManager
import com.mifos.api.GenericResponse
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.accounts.loan.LoanApproval
import com.mifos.utils.MFErrorParser
import retrofit2.HttpException
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 8/6/16.
 */
class LoanAccountApprovalPresenter @Inject constructor(private val mDataManager: DataManager) : BasePresenter<LoanAccountApprovalMvpView?>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: LoanAccountApprovalMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription!!.unsubscribe()
    }

    fun approveLoan(loanId: Int, loanApproval: LoanApproval?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = mDataManager.approveLoan(loanId, loanApproval)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        try {
                            if (e is HttpException) {
                                val errorMessage = e.response()?.errorBody()
                                        ?.string()
                                mvpView!!.showLoanApproveFailed(
                                        MFErrorParser.parseError(errorMessage)
                                                .errors[0].defaultUserMessage)
                            }
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    }

                    override fun onNext(genericResponse: GenericResponse?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showLoanApproveSuccessfully(genericResponse)
                    }
                })
    }

}