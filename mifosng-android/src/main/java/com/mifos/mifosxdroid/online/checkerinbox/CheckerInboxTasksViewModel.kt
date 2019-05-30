package com.mifos.mifosxdroid.online.checkerinbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import android.util.Log
import com.mifos.api.datamanager.DataManagerCheckerInbox
import com.mifos.objects.CheckerTask
import com.mifos.objects.checkerinboxandtasks.CheckerInboxSearchTemplate
import com.mifos.objects.checkerinboxandtasks.RescheduleLoansTask
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class CheckerInboxTasksViewModel @Inject constructor(
        val dataManager: DataManagerCheckerInbox,
        val subscription: CompositeSubscription): ViewModel() {

    private val checkerTasksLive: MutableLiveData<List<CheckerTask>> by lazy {
        MutableLiveData<List<CheckerTask>>().also {
            loadCheckerTasks()
        }
    }

    var status = MutableLiveData<Boolean>()

    private val rescheduleLoanTasksLive: MutableLiveData<List<RescheduleLoansTask>> by lazy {
        MutableLiveData<List<RescheduleLoansTask>>().also {
            loadRescheduleLoanTasks()
        }
    }

    fun getRescheduleLoanTasks(): MutableLiveData<List<RescheduleLoansTask>> {
        return rescheduleLoanTasksLive
    }

    fun loadRescheduleLoanTasks() {
        subscription.add(dataManager.getRechdeduleLoansTaskList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<RescheduleLoansTask>>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        status.value = false
                    }

                    override fun onNext(rescheduleLoanTasks: List<RescheduleLoansTask>) {
                        rescheduleLoanTasksLive.postValue(rescheduleLoanTasks)
                    }
                }))
    }


    fun getCheckerTasks(): LiveData<List<CheckerTask>> {
        return checkerTasksLive
    }

    fun loadCheckerTasks() {
        subscription.add(dataManager.getCheckerTaskList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<CheckerTask>>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        status.value = false
                    }

                    override fun onNext(checkerTasks: List<CheckerTask>) {
                        checkerTasksLive.postValue(checkerTasks)
                        status.value = true
                    }
                }))
    }
}