package com.mifos.mifosxdroid.dialogfragments.chargedialog

import com.mifos.api.DataManager
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.client.ChargeCreationResponse
import com.mifos.objects.templates.clients.ChargeOptions
import com.mifos.objects.templates.clients.ChargeTemplate
import com.mifos.services.data.ChargesPayload
import com.mifos.utils.MFErrorParser.parseError
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 08/06/16.
 */
class ChargeDialogPresenter @Inject constructor(private val mDataManager: DataManager) :
    BasePresenter<ChargeDialogMvpView>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: ChargeDialogMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription?.unsubscribe()
    }

    fun loadAllChargesV2(clientId: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        if (mSubscription != null) mSubscription?.unsubscribe()
        mSubscription = mDataManager.getAllChargesV2(clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ChargeTemplate>() {
                override fun onCompleted() {
                    mvpView?.showProgressbar(false)
                }

                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            mvpView?.showFetchingError(
                                parseError(errorMessage)
                                    .errors[0].defaultUserMessage
                            )
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(chargeTemplate: ChargeTemplate) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showAllChargesV2(chargeTemplate)
                }
            })
    }

    fun createCharges(clientId: Int, payload: ChargesPayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        if (mSubscription != null) mSubscription?.unsubscribe()
        mSubscription = mDataManager.createCharges(clientId, payload)
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
                                parseError(errorMessage)
                                    .errors[0].defaultUserMessage
                            )
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(chargeCreationResponse: ChargeCreationResponse) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showChargesCreatedSuccessfully(chargeCreationResponse)
                }
            })
    }

    fun filterChargeName(chargeOptions: List<ChargeOptions>?): List<String> {
        val chargeNameList = ArrayList<String>()
        Observable.from<ChargeOptions>(chargeOptions)
            .subscribe { chargeOptions -> chargeNameList.add(chargeOptions.name) }
        return chargeNameList
    }
}