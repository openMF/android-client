package com.mifos.mifosxdroid.online.datatabledata

import com.google.gson.JsonArray
import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerDataTable
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.utils.MFErrorParser
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 7/6/16.
 */
class DataTableDataPresenter @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) : BasePresenter<DataTableDataMvpView?>() {
    private val subscriptions: CompositeSubscription
    override fun attachView(mvpView: DataTableDataMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        subscriptions.unsubscribe()
    }

    fun loadDataTableInfo(table: String?, entityId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        subscriptions.add(dataManagerDataTable.getDataTableInfo(table, entityId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<JsonArray?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_fetch_datatable_info)
                    }

                    override fun onNext(jsonElements: JsonArray?) {
                        mvpView!!.showProgressbar(false)
                        if (jsonElements!!.size() == 0) {
                            mvpView!!.showEmptyDataTable()
                        } else {
                            mvpView!!.showDataTableInfo(jsonElements)
                        }
                    }
                }))
    }

    fun deleteDataTableEntry(table: String?, entity: Int, rowId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        subscriptions.add(dataManagerDataTable.deleteDataTableEntry(table, entity, rowId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(genericResponse: GenericResponse?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showDataTableDeletedSuccessfully()
                    }
                })
        )
    }

    init {
        subscriptions = CompositeSubscription()
    }
}