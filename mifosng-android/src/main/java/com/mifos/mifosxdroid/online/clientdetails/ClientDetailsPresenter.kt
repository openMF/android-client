package com.mifos.mifosxdroid.online.clientdetails

import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerDataTable
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.zipmodels.ClientAndClientAccounts
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 07/06/16.
 */
class ClientDetailsPresenter @Inject constructor(private val mDataManagerDataTable: DataManagerDataTable,
                                                 private val mDataManagerClient: DataManagerClient) : BasePresenter<ClientDetailsMvpView?>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: ClientDetailsMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription!!.unsubscribe()
    }

    fun uploadImage(id: Int, pngFile: File) {
        checkViewAttached()
        val imagePath = pngFile.absolutePath

        // create RequestBody instance from file
        val requestFile = RequestBody.create(MediaType.parse("image/png"), pngFile)

        // MultipartBody.Part is used to send also the actual file name
        val body = MultipartBody.Part.createFormData("file", pngFile.name, requestFile)
        mvpView!!.showUploadImageProgressbar(true)
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = mDataManagerClient.uploadClientImage(id, body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ResponseBody?>() {
                    override fun onCompleted() {
                        mvpView!!.showUploadImageProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showUploadImageFailed("Unable to update image")
                    }

                    override fun onNext(response: ResponseBody?) {
                        mvpView!!.showUploadImageProgressbar(false)
                        mvpView!!.showUploadImageSuccessfully(response, imagePath)
                    }
                })
    }

    fun deleteClientImage(clientId: Int) {
        checkViewAttached()
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = mDataManagerClient.deleteClientImage(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ResponseBody?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showFetchingError("Failed to delete image")
                    }

                    override fun onNext(response: ResponseBody?) {
                        mvpView!!.showClientImageDeletedSuccessfully()
                    }
                })
    }

    fun loadClientDetailsAndClientAccounts(clientId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = Observable.zip(
                mDataManagerClient.getClientAccounts(clientId),
                mDataManagerClient.getClient(clientId)
        ) { clientAccounts, client ->
            val clientAndClientAccounts = ClientAndClientAccounts()
            clientAndClientAccounts.client = client
            clientAndClientAccounts.clientAccounts = clientAccounts
            clientAndClientAccounts
        }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ClientAndClientAccounts>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError("Client not found.")
                    }

                    override fun onNext(clientAndClientAccounts: ClientAndClientAccounts) {
                        mvpView!!.showProgressbar(false)
                        clientAndClientAccounts.clientAccounts?.let { mvpView!!.showClientAccount(it) }
                        mvpView!!.showClientInformation(clientAndClientAccounts.client)
                    }
                })
    }

}