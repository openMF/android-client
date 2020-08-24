package com.mifos.mifosxdroid.online.collectionsheet

import com.mifos.api.DataManager
import com.mifos.api.model.CollectionSheetPayload
import com.mifos.api.model.Payload
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.db.CollectionSheet
import com.mifos.objects.response.SaveResponse
import retrofit2.adapter.rxjava.HttpException
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 7/6/16.
 */
class CollectionSheetPresenter @Inject constructor(private val mDataManager: DataManager) : BasePresenter<CollectionSheetMvpView?>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: CollectionSheetMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription!!.unsubscribe()
    }

    fun loadCollectionSheet(id: Long, payload: Payload?) {
        checkViewAttached()
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = mDataManager.getCollectionSheet(id, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<CollectionSheet?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showFetchingError("Failed to fetch CollectionSheet")
                    }

                    override fun onNext(collectionSheet: CollectionSheet?) {
                        collectionSheet?.let { mvpView!!.showCollectionSheet(it) }
                    }
                })
    }

    fun saveCollectionSheet(id: Int, payload: CollectionSheetPayload?) {
        checkViewAttached()
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = mDataManager.saveCollectionSheetAsync(id, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<SaveResponse?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        if (e is HttpException) {
                            mvpView!!.showFailedToSaveCollectionSheet(e)
                        }
                    }

                    override fun onNext(saveResponse: SaveResponse?) {
                        mvpView!!.showCollectionSheetSuccessfullySaved(saveResponse)
                    }
                })
    }

}