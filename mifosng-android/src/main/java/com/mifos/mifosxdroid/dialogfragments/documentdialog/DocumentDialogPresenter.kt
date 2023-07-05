package com.mifos.mifosxdroid.dialogfragments.documentdialog

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerDocument
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.utils.MFErrorParser
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.adapter.rxjava.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.File
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 8/6/16.
 */
class DocumentDialogPresenter @Inject constructor(private val mDataManagerDocument: DataManagerDocument) :
    BasePresenter<DocumentDialogMvpView>() {
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()

    override fun attachView(mvpView: DocumentDialogMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.clear()
    }

    fun createDocument(type: String?, id: Int, name: String?, desc: String?, file: File) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(mDataManagerDocument
            .createDocument(type, id, name, desc, getRequestFileBody(file))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {
                    mvpView?.showProgressbar(false)
                }

                override fun onError(e: Throwable) {
                    mvpView?.showProgressbar(false)
                    val errorMessage: String
                    try {
                        if (e is HttpException) {
                            errorMessage = e.response().errorBody().string()
                            mvpView?.showUploadError(
                                MFErrorParser.parseError(errorMessage)
                                    .developerMessage
                            )
                            mvpView?.showError(R.string.failed_to_upload_document)
                        } else {
                            mvpView?.showError(R.string.failed_to_upload_document)
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler
                            .handleError(throwable)
                    }
                }

                override fun onNext(genericResponse: GenericResponse) {
                    mvpView?.showProgressbar(false)
                    mvpView?.showDocumentedCreatedSuccessfully(genericResponse)
                }
            })
        )
    }

    fun updateDocument(
        entityType: String?, entityId: Int, documentId: Int,
        name: String?, desc: String?, file: File
    ) {
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerDocument.updateDocument(
                entityType, entityId, documentId,
                name, desc, getRequestFileBody(file)
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showError(R.string.failed_to_update_document)
                    }

                    override fun onNext(genericResponse: GenericResponse) {
                        mvpView?.showProgressbar(false)
                        mvpView?.showDocumentUpdatedSuccessfully()
                    }
                })
        )
    }

    private fun getRequestFileBody(file: File): MultipartBody.Part {
        // create RequestBody instance from file
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }
}