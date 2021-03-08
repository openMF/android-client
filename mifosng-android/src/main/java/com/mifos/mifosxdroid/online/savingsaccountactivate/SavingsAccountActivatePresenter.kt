package com.mifos.mifosxdroid.online.savingsaccountactivate

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerSavings
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.utils.MFErrorParser
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

/**
 * Created by Tarun on 01/06/17.
 */
class SavingsAccountActivatePresenter @Inject constructor(private val mDataManagerSavings: DataManagerSavings) : BasePresenter<SavingsAccountActivateMvpView?>() {
    private val mSubscriptions: CompositeSubscription
    override fun attachView(mvpView: SavingsAccountActivateMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.clear()
    }

    fun activateSavings(savingsAccountId: Int, request: HashMap<String, Any?>?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerSavings.activateSavings(savingsAccountId, request)
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
                        mvpView!!.showSavingAccountActivatedSuccessfully(genericResponse)
                    }
                }))
    }

    init {
        mSubscriptions = CompositeSubscription()
    }
}