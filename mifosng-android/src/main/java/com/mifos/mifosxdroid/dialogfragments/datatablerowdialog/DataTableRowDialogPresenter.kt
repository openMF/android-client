package com.mifos.mifosxdroid.dialogfragments.datatablerowdialog

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerDataTable
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.utils.MFErrorParser.errorMessage
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 08/06/16.
 */
class DataTableRowDialogPresenter @Inject constructor(private val dataManagerDataTable: DataManagerDataTable) :
    BasePresenter<DataTableRowDialogMvpView>() {
    private val subscriptions: CompositeSubscription = CompositeSubscription()

    override fun attachView(mvpView: DataTableRowDialogMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        subscriptions.unsubscribe()
    }

    fun addDataTableEntry(table: String?, entityId: Int, payload: Map<String, String>) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        subscriptions.add(dataManagerDataTable.addDataTableEntry(table, entityId, payload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {
                    mvpView?.showProgressbar(false)
                }

                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showError(errorMessage(e)!!)
                }

                override fun onNext(genericResponse: GenericResponse) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showDataTableEntrySuccessfully(genericResponse)
                }
            })
        )
    }
}