package com.mifos.mifosxdroid.online.checkerinbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import android.util.Log
import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerCheckerInbox
import com.mifos.objects.CheckerTask
import com.mifos.objects.checkerinboxandtasks.CheckerInboxSearchTemplate
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

enum class Status {
    APPROVE_SUCCESS,
    APPROVE_ERROR,
    REJECT_SUCCESS,
    REJECT_ERROR,
    DELETE_SUCCESS,
    DELETE_ERROR,
}

class CheckerInboxViewModel @Inject constructor(
        val dataManager: DataManagerCheckerInbox,
        val subscription: CompositeSubscription
) : ViewModel() {

    private val checkerTasksLive: MutableLiveData<List<CheckerTask>> by lazy {
        MutableLiveData<List<CheckerTask>>().also {
            loadCheckerTasks()
        }
    }

    private val searchTemplateLive: MutableLiveData<CheckerInboxSearchTemplate> by lazy {
        MutableLiveData<CheckerInboxSearchTemplate>().also {
            loadSearchTemplate()
        }
    }

    private val status = MutableLiveData<Status>()

    fun getCheckerTasks(): LiveData<List<CheckerTask>> {
        return checkerTasksLive
    }

    fun getSearchTemplate(): LiveData<CheckerInboxSearchTemplate>{
        return searchTemplateLive
    }

    fun getStatus(): LiveData<Status> {
        return status
    }

    private fun loadCheckerTasks(actionName: String? = null, entityName: String? = null,
                         resourceId: Int? = null) {
        subscription.add(dataManager.getCheckerTaskList(actionName, entityName, resourceId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<CheckerTask>>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onNext(checkerTasks: List<CheckerTask>) {
                        checkerTasksLive.postValue(checkerTasks)
                    }
                }))
    }

    fun approveCheckerEntry(auditId: Int) {
        subscription.add(dataManager.approveCheckerEntry(auditId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        status.value = Status.APPROVE_ERROR
                    }

                    override fun onNext(Response: GenericResponse) {
                        status.value = Status.APPROVE_SUCCESS
                    }
                }))
    }

    fun rejectCheckerEntry(auditId: Int) {
        subscription.add(dataManager.rejectCheckerEntry(auditId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        status.value = Status.REJECT_ERROR
                    }

                    override fun onNext(Response: GenericResponse) {
                        status.value = Status.REJECT_SUCCESS
                    }
                }))
    }

    fun deleteCheckerEntry(auditId: Int) {
        subscription.add(dataManager.deleteCheckerEntry(auditId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        status.value = Status.DELETE_ERROR
                    }

                    override fun onNext(Response: GenericResponse) {
                        status.value = Status.DELETE_SUCCESS
                    }
                }))
    }

    private fun loadSearchTemplate() {
        subscription.add(dataManager.getCheckerInboxSearchTemplate()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<CheckerInboxSearchTemplate>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onNext(checkerInboxSearchTemplate: CheckerInboxSearchTemplate) {
                        searchTemplateLive.postValue(checkerInboxSearchTemplate)
                    }
                }))
    }
}