package com.mifos.mifosxdroid.online.loancharge

import com.mifos.api.DataManager
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.client.Charges
import com.mifos.utils.MFErrorParser
import retrofit2.HttpException
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 07/06/16.
 */
class LoanChargePresenter @Inject constructor(private val mDataManager: DataManager) : BasePresenter<LoanChargeMvpView?>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: LoanChargeMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription!!.unsubscribe()
    }

    fun loadLoanChargesList(loanId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = mDataManager.getListOfLoanCharges(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Charges?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        try {
                            if (e is HttpException) {
                                val errorMessage = e.response()?.errorBody()
                                        ?.string()
                                mvpView!!.showFetchingError(MFErrorParser
                                        .parseError(errorMessage)
                                        .errors[0].defaultUserMessage)
                            }
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    }

                    override fun onNext(chargesPage: List<Charges?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showLoanChargesList(chargesPage as MutableList<Charges>)
                    }
                })
    }

}