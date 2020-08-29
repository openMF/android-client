package com.mifos.mifosxdroid.online.clientidentifiers

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerClient
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.noncore.Identifier
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 06/06/16.
 */
class ClientIdentifiersPresenter @Inject constructor(private val mDataManagerClient: DataManagerClient) : BasePresenter<ClientIdentifiersMvpView?>() {
    private val mSubscriptions: CompositeSubscription
    override fun attachView(mvpView: ClientIdentifiersMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.clear()
    }

    fun loadIdentifiers(clientId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerClient.getClientIdentifiers(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Identifier?>?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_fetch_identifier)
                    }

                    override fun onNext(identifiers: List<Identifier?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showClientIdentifiers(identifiers as MutableList<Identifier>)
                    }
                }))
    }

    /**
     * Method to call Identifier Delete endpoint to remove an identifier.
     * @param clientId ClientID whose identifier has to be removed
     * @param identifierId Id of the identifier to be removed
     * @param position Position of the identifier to be removed. This will be sent on successful
     * request to notify that identifier at this position has been removed.
     */
    fun deleteIdentifier(clientId: Int, identifierId: Int, position: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerClient.deleteClientIdentifier(clientId, identifierId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_delete_identifier)
                    }

                    override fun onNext(genericResponse: GenericResponse?) {
                        mvpView!!.identifierDeletedSuccessfully(position)
                        mvpView!!.showProgressbar(false)
                    }
                }))
    }

    init {
        mSubscriptions = CompositeSubscription()
    }
}