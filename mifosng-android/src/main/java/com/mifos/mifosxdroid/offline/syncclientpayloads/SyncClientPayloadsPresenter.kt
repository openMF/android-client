package com.mifos.mifosxdroid.offline.syncclientpayloads

import com.mifos.api.datamanager.DataManagerClient
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.client.Client
import com.mifos.objects.client.ClientPayload
import com.mifos.utils.MFErrorParser.errorMessage
import rx.Observer
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 08/07/16.
 */
class SyncClientPayloadsPresenter @Inject constructor(private val mDataManagerClient: DataManagerClient) :
    BasePresenter<SyncClientPayloadsMvpView>() {
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()

    override fun attachView(mvpView: SyncClientPayloadsMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun loadDatabaseClientPayload() {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerClient.allDatabaseClientPayload
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<ClientPayload>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showError(R.string.failed_to_load_clientpayload)
                }

                override fun onNext(clientPayloads: List<ClientPayload>) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showPayloads(clientPayloads)
                }
            })
        )
    }

    fun syncClientPayload(clientPayload: ClientPayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerClient.createClient(clientPayload!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Client> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showClientSyncFailed(errorMessage(e)!!)
                }

                override fun onNext(client: Client) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showSyncResponse()
                }
            })
        )
    }

    fun deleteAndUpdateClientPayload(id: Int, clientCreationTIme: Long) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerClient.deleteAndUpdatePayloads(id, clientCreationTIme)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<List<ClientPayload>> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showError(R.string.failed_to_update_list)
                }

                override fun onNext(clientPayloads: List<ClientPayload>) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showPayloadDeletedAndUpdatePayloads(clientPayloads)
                }
            })
        )
    }

    fun updateClientPayload(clientPayload: ClientPayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerClient.updateClientPayload(clientPayload!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ClientPayload>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_update_list)
                    }

                    override fun onNext(clientPayload: ClientPayload) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showClientPayloadUpdated(clientPayload)
                    }
                })
        )
    }
}