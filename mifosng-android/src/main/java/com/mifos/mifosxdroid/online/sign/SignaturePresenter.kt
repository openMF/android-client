package com.mifos.mifosxdroid.online.sign

import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerDocument
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.File
import javax.inject.Inject

/**
 * Created by Tarun on 29-06-2017.
 */
class SignaturePresenter @Inject constructor(private val mDataManagerDocument: DataManagerDocument) : BasePresenter<SignatureMvpView?>() {
    private val mSubscriptions: CompositeSubscription
    override fun attachView(mvpView: SignatureMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.clear()
    }

    fun createDocument(type: String?, id: Int, name: String?, desc: String?, file: File?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerDocument
                .createDocument(type, id, name, desc, getRequestFileBody(file))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(R.string.failed_to_upload_document)
                    }

                    override fun onNext(genericResponse: GenericResponse?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showSignatureUploadedSuccessfully(genericResponse)
                    }
                }))
    }

    private fun getRequestFileBody(file: File?): MultipartBody.Part {
        // create RequestBody instance from file
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("file", file!!.name, requestFile)
    }

    init {
        mSubscriptions = CompositeSubscription()
    }
}