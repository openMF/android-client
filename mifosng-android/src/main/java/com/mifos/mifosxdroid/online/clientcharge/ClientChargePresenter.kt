package com.mifos.mifosxdroid.online.clientcharge

import com.mifos.api.datamanager.DataManagerCharge
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.client.Charges
import com.mifos.objects.client.Page
import com.mifos.utils.MFErrorParser
import retrofit2.HttpException
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 5/6/16.
 */
class ClientChargePresenter @Inject constructor(private val mDataManagerCharge: DataManagerCharge) : BasePresenter<ClientChargeMvpView>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: ClientChargeMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription?.unsubscribe()
    }

    fun loadCharges(clientId: Int, offset: Int, limit: Int) {
        mvpView?.showProgressbar(true)
        if (mSubscription != null) mSubscription?.unsubscribe()
        mSubscription = mDataManagerCharge.getClientCharges(clientId, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Charges>>() {
                    override fun onCompleted() {
                        mvpView?.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        try {
                            if (e is HttpException) {
                                val errorMessage = e.response()?.errorBody()
                                        ?.string()
                                mvpView?.showFetchingErrorCharges(MFErrorParser
                                        .parseError(errorMessage)
                                        .errors[0].defaultUserMessage)
                            }
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    }

                    override fun onNext(chargesPage: Page<Charges>) {
                        mvpView?.showProgressbar(false)
                        if (chargesPage.pageItems.size > 0) {
                            mvpView?.showChargesList(chargesPage)
                        } else {
                            mvpView?.showEmptyCharges()
                        }
                    }
                })
    }

}