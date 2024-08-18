package com.mifos.mifosxdroid.online.checkerinbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import com.mifos.feature.checker_inbox_task.checker_inbox_dialog.CheckerInboxViewModel
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class CheckerInboxViewModelFactory @Inject constructor(
        private val dataManagerCheckerInbox: DataManagerCheckerInbox
):
        ViewModelProvider.NewInstanceFactory() {
    val subscription = CompositeSubscription()

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(CheckerInboxTasksViewModel::class.java)) {
            return CheckerInboxTasksViewModel(dataManagerCheckerInbox) as T
        }
        if (modelClass.isAssignableFrom(CheckerInboxViewModel::class.java)) {
            //return CheckerInboxViewModel(dataManagerCheckerInbox, subscription) as T
            return CheckerInboxViewModel(dataManagerCheckerInbox) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}