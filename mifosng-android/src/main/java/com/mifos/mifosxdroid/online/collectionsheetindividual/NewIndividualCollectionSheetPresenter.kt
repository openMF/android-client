package com.mifos.mifosxdroid.online.collectionsheetindividual

import com.mifos.api.DataManager
import com.mifos.api.datamanager.DataManagerCollectionSheet
import com.mifos.api.model.RequestCollectionSheetPayload
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.collectionsheet.IndividualCollectionSheet
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff
import com.mifos.utils.MFErrorParser
import retrofit2.adapter.rxjava.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Tarun on 05-07-2017.
 */
class NewIndividualCollectionSheetPresenter @Inject internal constructor(
    private val mDataManager: DataManager,
    private val mDataManagerCollection: DataManagerCollectionSheet
) : BasePresenter<IndividualCollectionSheetMvpView?>() {
    private val mSubscription: CompositeSubscription?

    override fun detachView() {
        super.detachView()
        mSubscription?.unsubscribe()
    }

    fun fetchIndividualCollectionSheet(requestCollectionSheetPayload: RequestCollectionSheetPayload?) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscription?.add(mDataManagerCollection
            .getIndividualCollectionSheet(requestCollectionSheetPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<IndividualCollectionSheet?>() {
                override fun onCompleted() {
                    mvpView?.showSuccess()
                }

                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    if (e is HttpException) {
                        try {
                            val errorMessage = e.response().errorBody()
                                .string()
                            mvpView?.showError(
                                MFErrorParser.parseError(errorMessage)
                                    .errors[0].defaultUserMessage
                            )
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    } else {
                        mvpView?.showError(e.localizedMessage)
                    }
                }

                override fun onNext(individualCollectionSheet: IndividualCollectionSheet?) {
                    mvpView?.showProgressbar(false)
                    if (individualCollectionSheet?.clients?.size!! > 0) {
                        mvpView?.showSheet(individualCollectionSheet)
                    } else {
                        mvpView?.showNoSheetFound()
                    }
                }
            })
        )
    }

    fun fetchOffices() {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscription?.add(mDataManager.offices
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Office?>?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response().errorBody()
                                .string()
                            mvpView?.showError(
                                MFErrorParser.parseError(errorMessage)
                                    .errors[0].defaultUserMessage
                            )
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(officeList: List<Office?>?) {
                    mvpView?.setOfficeSpinner(officeList as List<Office>?)
                    mvpView?.showProgressbar(false)
                }
            })
        )
    }

    fun fetchStaff(officeId: Int) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscription?.add(mDataManager.getStaffInOffice(officeId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Staff?>?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response().errorBody()
                                .string()
                            mvpView?.showError(
                                MFErrorParser.parseError(errorMessage)
                                    .errors[0].defaultUserMessage
                            )
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(staffList: List<Staff?>?) {
                    mvpView?.setStaffSpinner(staffList as List<Staff>?)
                    mvpView?.showProgressbar(false)
                }
            })
        )
    }

    fun filterOffices(offices: List<Office>?): List<String> {
        val officesList: MutableList<String> = ArrayList()
        Observable.from(offices)
            .subscribe { office -> officesList.add(office.name) }
        return officesList
    }

    fun filterStaff(staffs: List<Staff>?): List<String> {
        val staffList: MutableList<String> = ArrayList()
        Observable.from(staffs)
            .subscribe { staff -> staffList.add(staff.displayName) }
        return staffList
    }

    init {
        mSubscription = CompositeSubscription()
    }
}