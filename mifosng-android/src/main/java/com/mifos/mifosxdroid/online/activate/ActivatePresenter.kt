package com.mifos.mifosxdroid.online.activate

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerCenter
import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.client.ActivatePayload
import com.mifos.utils.MFErrorParser.errorMessage
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 09/02/17.
 */
class ActivatePresenter @Inject constructor(
    private val dataManagerClient: DataManagerClient,
    private val dataManagerCenter: DataManagerCenter,
    private val dataManagerGroups: DataManagerGroups
) : BasePresenter<ActivateMvpView>() {
    private val subscriptions: CompositeSubscription = CompositeSubscription()

    override fun attachView(mvpView: ActivateMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        subscriptions.unsubscribe()
    }

    fun activateClient(clientId: Int, clientActivate: ActivatePayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        subscriptions.add(
            dataManagerClient.activateClient(clientId, clientActivate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        errorMessage(e)?.let { mvpView?.showError(it) }
                    }

                    override fun onNext(genericResponse: GenericResponse) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showActivatedSuccessfully(
                            R.string.client_activated_successfully
                        )
                    }
                })
        )
    }

    fun activateCenter(centerId: Int, activatePayload: ActivatePayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        subscriptions.add(
            dataManagerCenter.activateCenter(centerId, activatePayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        errorMessage(e)?.let { mvpView?.showError(it) }
                    }

                    override fun onNext(genericResponse: GenericResponse) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showActivatedSuccessfully(
                            R.string.center_activated_successfully
                        )
                    }
                })
        )
    }

    fun activateGroup(groupId: Int, activatePayload: ActivatePayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        subscriptions.add(
            dataManagerGroups.activateGroup(groupId, activatePayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        errorMessage(e)?.let { mvpView?.showError(it) }
                    }

                    override fun onNext(genericResponse: GenericResponse) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showActivatedSuccessfully(R.string.group_created_successfully)
                    }
                })
        )
    }
}