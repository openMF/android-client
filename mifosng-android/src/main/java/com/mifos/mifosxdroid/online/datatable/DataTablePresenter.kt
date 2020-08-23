package com.mifos.mifosxdroid.online.datatable

import com.mifos.api.datamanager.DataManagerDataTable
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.noncore.DataTable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 12/02/17.
 */
class DataTablePresenter @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) : BasePresenter<DataTableMvpView?>() {
    private val subscriptions: CompositeSubscription
    override fun attachView(mvpView: DataTableMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        subscriptions.clear()
    }

    /**
     * This method load the DataTable.
     *
     * Table name can be
     * "m_client"
     * "m_group"
     * "m_loan"
     * "m_office"
     * "m_saving_account"
     * "m_product_loan"
     * "m_savings_product
     *
     * Response: List<DataTable></DataTable>
     */
    fun loadDataTable(tableName: String?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mvpView!!.showResetVisibility()
        subscriptions.add(dataManagerDataTable.getDataTable(tableName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<DataTable?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(R.string.failed_to_fetch_datatable)
                    }

                    override fun onNext(dataTables: List<DataTable?>?) {
                        mvpView!!.showProgressbar(false)
                        if (!dataTables!!.isEmpty()) {
                            mvpView!!.showDataTables(dataTables as List<DataTable>?)
                        } else {
                            mvpView!!.showEmptyDataTables()
                        }
                    }
                }))
    }

    init {
        subscriptions = CompositeSubscription()
    }
}