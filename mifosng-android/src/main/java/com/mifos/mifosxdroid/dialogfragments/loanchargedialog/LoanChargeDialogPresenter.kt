package com.mifos.mifosxdroid.dialogfragments.loanchargedialog

import com.mifos.api.DataManager
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.client.ChargeCreationResponse
import com.mifos.services.data.ChargesPayload
import com.mifos.utils.MFErrorParser
import okhttp3.ResponseBody
import retrofit2.HttpException
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 9/6/16.
 */
class LoanChargeDialogPresenter @Inject constructor(private val mDataManager: DataManager) :
    BasePresenter<LoanChargeDialogMvpView>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: LoanChargeDialogMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription?.unsubscribe()
    }

    fun loanAllChargesV3(loanId: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        if (mSubscription != null) mSubscription?.unsubscribe()
        mSubscription = mDataManager.getAllChargesV3(loanId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {
                    mvpView?.showProgressbar(false)
                }

                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            mvpView?.showError(
                                MFErrorParser
                                    .parseError(errorMessage)
                                    .errors[0].defaultUserMessage
                            )
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(response: ResponseBody) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showAllChargesV3(response)
                }
            })
    }

    fun createLoanCharges(loanId: Int, chargesPayload: ChargesPayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        if (mSubscription != null) mSubscription?.unsubscribe()
        mSubscription = mDataManager.createLoanCharges(loanId, chargesPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ChargeCreationResponse>() {
                override fun onCompleted() {
                    mvpView?.showProgressbar(false)
                }

                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            mvpView?.showChargeCreatedFailure(
                                MFErrorParser
                                    .parseError(errorMessage)
                                    .errors[0].defaultUserMessage
                            )
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(chargeCreationResponse: ChargeCreationResponse) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showLoanChargesCreatedSuccessfully(chargeCreationResponse)
                }
            })
    }
}