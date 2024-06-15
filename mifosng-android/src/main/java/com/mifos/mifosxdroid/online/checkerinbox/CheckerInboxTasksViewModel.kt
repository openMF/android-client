package com.mifos.mifosxdroid.online.checkerinbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import com.mifos.core.objects.checkerinboxandtasks.CheckerTask
import com.mifos.core.objects.checkerinboxandtasks.RescheduleLoansTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CheckerInboxTasksViewModel @Inject constructor(
    val dataManager: DataManagerCheckerInbox
) : ViewModel() {

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

    fun loadRescheduleLoanTasks() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = dataManager.getRechdeduleLoansTaskList()
            rescheduleLoanTasksLive.postValue(response)
        } catch (exception: Exception) {
            status.value = false
        }
    }


    fun getCheckerTasks(): LiveData<List<CheckerTask>> {
        return checkerTasksLive
    }

    fun loadCheckerTasks() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val response = dataManager.getCheckerTaskList()
            checkerTasksLive.postValue(response)
            status.value = true
        } catch (exception: Exception) {
            status.value = false
        }
    }
}