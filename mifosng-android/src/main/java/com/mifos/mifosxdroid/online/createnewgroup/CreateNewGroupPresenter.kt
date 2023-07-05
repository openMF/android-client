package com.mifos.mifosxdroid.online.createnewgroup

import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.api.datamanager.DataManagerOffices
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.group.GroupPayload
import com.mifos.objects.organisation.Office
import com.mifos.objects.response.SaveResponse
import com.mifos.utils.MFErrorParser
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 06/06/16.
 */
class CreateNewGroupPresenter @Inject constructor(
    private val mDataManagerOffices: DataManagerOffices,
    private val mDataManagerGroups: DataManagerGroups
) : BasePresenter<CreateNewGroupMvpView?>() {
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun loadOffices() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerOffices.offices
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Office>>() {
                override fun onCompleted() {
                    mvpView!!.showProgressbar(false)
                }

                override fun onError(e: Throwable) {
                    mvpView!!.showProgressbar(false)
                    mvpView!!.showFetchingError("Failed to fetch office list")
                }

                override fun onNext(offices: List<Office>) {
                    mvpView!!.showProgressbar(false)
                    mvpView!!.showOffices(offices)
                }
            })
        )
    }

    fun createGroup(groupPayload: GroupPayload) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerGroups.createGroup(groupPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SaveResponse>() {
                override fun onCompleted() {
                    mvpView!!.showProgressbar(false)
                }

                override fun onError(e: Throwable) {
                    mvpView!!.showProgressbar(false)
                    mvpView!!.showFetchingError(MFErrorParser.errorMessage(e))
                }

                override fun onNext(saveResponse: SaveResponse) {
                    mvpView!!.showProgressbar(false)
                    mvpView!!.showGroupCreatedSuccessfully(saveResponse)
                }
            })
        )
    }

}