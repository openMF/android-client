package com.mifos.mifosxdroid.online.createnewclient

import com.mifos.api.datamanager.DataManagerClient
import com.mifos.api.datamanager.DataManagerOffices
import com.mifos.api.datamanager.DataManagerStaff
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.client.Client
import com.mifos.objects.client.ClientPayload
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff
import com.mifos.objects.templates.clients.ClientsTemplate
import com.mifos.objects.templates.clients.Options
import com.mifos.utils.MFErrorParser
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.adapter.rxjava.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 6/6/16.
 */
class CreateNewClientPresenter @Inject constructor(private val mDataManagerClient: DataManagerClient,
                                                   private val mDataManagerOffices: DataManagerOffices,
                                                   private val mDataManagerStaff: DataManagerStaff) : BasePresenter<CreateNewClientMvpView?>() {
    private val mSubscriptions: CompositeSubscription
    override fun attachView(mvpView: CreateNewClientMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
    }

    fun loadClientTemplate() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerClient.clientTemplate
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ClientsTemplate?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                        loadOffices()
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showMessage(R.string.failed_to_fetch_client_template)
                    }

                    override fun onNext(t: ClientsTemplate?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showClientTemplate(t)
                    }
                }))
    }

    fun loadOffices() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerOffices.offices
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Office?>?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showMessage(R.string.failed_to_fetch_offices)
                    }

                    override fun onNext(t: List<Office?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showOffices(t)
                    }
                }))
    }

    fun loadStaffInOffices(officeId: Int) {
        checkViewAttached()
        mSubscriptions.add(mDataManagerStaff.getStaffInOffice(officeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Staff?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showMessage(R.string.failed_to_fetch_staffs)
                    }

                    override fun onNext(t: List<Staff?>?) {
                        mvpView!!.showStaffInOffices(t)
                    }
                }))
    }

    fun createClient(clientPayload: ClientPayload?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions.add(mDataManagerClient.createClient(clientPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Client?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        try {
                            if (e is HttpException) {
                                val errorMessage = e.response()!!.errorBody()!!
                                        .string()
                                mvpView!!.showMessage(MFErrorParser.parseError(errorMessage)
                                        .errors[0].defaultUserMessage)
                            }
                        } catch (throwable: Throwable) {
                            RxJavaPlugins.getInstance().errorHandler.handleError(e)
                        }
                    }

                    override fun onNext(t: Client?) {
                        mvpView!!.showProgressbar(false)
                        if (t != null) {
                            if (t.clientId != null) {
                                mvpView!!.showClientCreatedSuccessfully(
                                        R.string.client_created_successfully)
                                mvpView!!.setClientId(t.clientId)
                            } else {
                                mvpView!!.showWaitingForCheckerApproval(
                                        R.string.waiting_for_checker_approval
                                )
                            }
                        }
                    }
                }))
    }

    fun filterOptions(options: List<Options>?): List<String> {
        val filterValues: MutableList<String> = ArrayList()
        Observable.from(options)
                .subscribe { options -> filterValues.add(options.name) }
        return filterValues
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

    fun uploadImage(id: Int, pngFile: File) {
        checkViewAttached()
        mvpView!!.showProgress("Uploading Client's Picture...")
        val imagePath = pngFile.absolutePath

        // create RequestBody instance from file
        val requestFile = RequestBody.create("image/png".toMediaTypeOrNull(), pngFile)

        // MultipartBody.Part is used to send also the actual file name
        val body = MultipartBody.Part.createFormData("file", pngFile.name, requestFile)
        mSubscriptions.add(mDataManagerClient.uploadClientImage(id, body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ResponseBody?>() {
                    override fun onCompleted() {
                        mvpView!!.hideProgress()
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.hideProgress()
                        mvpView!!.showMessage(R.string.Image_Upload_Failed)
                    }

                    override fun onNext(t: ResponseBody?) {
                        mvpView!!.hideProgress()
                        mvpView!!.showMessage(R.string.Image_Upload_Successful)
                    }
                }))
    }

    init {
        mSubscriptions = CompositeSubscription()
    }
}