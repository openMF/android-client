package com.mifos.mifosxdroid.online.checkerinbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import com.mifos.core.objects.checkerinboxandtasks.CheckerInboxSearchTemplate
import com.mifos.core.objects.checkerinboxandtasks.CheckerTask
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


    private val searchTemplateLive: MutableLiveData<CheckerInboxSearchTemplate> by lazy {
        MutableLiveData<CheckerInboxSearchTemplate>().also {
            loadSearchTemplate()
        }
    }

    private val status = MutableLiveData<Status>()


    fun getSearchTemplate(): LiveData<CheckerInboxSearchTemplate> {
        return searchTemplateLive
    }

    fun getStatus(): LiveData<Status> {
        return status
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
            })
        )
    }
}