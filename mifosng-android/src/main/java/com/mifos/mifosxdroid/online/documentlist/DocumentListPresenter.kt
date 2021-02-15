package com.mifos.mifosxdroid.online.documentlist

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerDocument
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.noncore.Document
import okhttp3.ResponseBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 06/06/16.
 */
class DocumentListPresenter @Inject constructor(private val mDataManagerDocument: DataManagerDocument) : BasePresenter<DocumentListMvpView?>() {
    private val mSubscriptions: CompositeSubscription
    override fun attachView(mvpView: DocumentListMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.clear()
    }

    fun loadDocumentList(type: String?, id: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerDocument.getDocumentsList(type, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Document?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_fetch_documents)
                    }

                    override fun onNext(documents: List<Document?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showDocumentList(documents as List<Document>)
                    }
                }))
    }

    fun downloadDocument(entityType: String?, entityId: Int, documentId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerDocument.downloadDocument(entityType, entityId, documentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ResponseBody?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_fetch_documents)
                    }

                    override fun onNext(responseBody: ResponseBody?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showDocumentFetchSuccessfully(responseBody)
                    }
                })
        )
    }

    fun removeDocument(entityType: String?, entityId: Int, documentId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerDocument.removeDocument(entityType, entityId, documentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_remove_document)
                    }

                    override fun onNext(genericResponse: GenericResponse?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showDocumentRemovedSuccessfully()
                    }
                })
        )
    }

    init {
        mSubscriptions = CompositeSubscription()
    }
}