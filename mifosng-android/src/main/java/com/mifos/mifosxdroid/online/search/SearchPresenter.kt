package com.mifos.mifosxdroid.online.search

import com.mifos.App
import com.mifos.api.datamanager.DataManagerSearch
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.SearchedEntity
import com.mifos.utils.Network
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 06/06/16.
 */
class SearchPresenter @Inject constructor(private val dataManagerSearch: DataManagerSearch) :
    BasePresenter<SearchMvpView>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: SearchMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription?.unsubscribe()
    }

    fun searchResources(query: String?, resources: String?, exactMatch: Boolean?) {
        checkViewAttached()
        val context = App.context
        if (context != null && !Network.isOnline(context)) {
            mvpView?.showProgressbar(false)
            mvpView?.showMessage(R.string.no_internet_connection)
            return
        }
        mvpView?.showProgressbar(true)
        if (mSubscription != null) mSubscription?.unsubscribe()
        mSubscription = dataManagerSearch.searchResources(query, resources, exactMatch)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<SearchedEntity>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showMessage(R.string.failed_to_fetch_resources_of_query)
                    mvpView?.showProgressbar(false)
                }

                override fun onNext(searchedEntities: List<SearchedEntity>) {
                    mvpView?.showProgressbar(false)
                    if (searchedEntities.isEmpty()) {
                        mvpView?.showNoResultFound()
                    } else {
                        mvpView?.showSearchedResources(searchedEntities)
                    }
                }
            })
    }
}