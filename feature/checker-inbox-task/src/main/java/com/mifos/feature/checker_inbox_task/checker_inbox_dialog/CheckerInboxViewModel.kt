package com.mifos.feature.checker_inbox_task.checker_inbox_dialog

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import com.mifos.core.objects.checkerinboxandtasks.CheckerInboxSearchTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject


@HiltViewModel
class CheckerInboxViewModel @Inject constructor(
    private val dataManager: DataManagerCheckerInbox
) : ViewModel() {

    private val _searchTemplate = MutableStateFlow<CheckerInboxSearchTemplate?>(null)
    val searchTemplate : StateFlow<CheckerInboxSearchTemplate?> = _searchTemplate

    init {
        loadSearchTemplate()
    }

    fun loadSearchTemplate() {
        viewModelScope.launch {
            try {
                dataManager.getCheckerInboxSearchTemplate()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        _searchTemplate.value = response
                    }, { throwable ->
                        Log.e("CheckerInboxViewModel", "Error loading search template", throwable)
                    })
            } catch (e: Exception) {
                Log.e("CheckerInboxViewModel", "Error loading search template")
            }
        }
    }
}