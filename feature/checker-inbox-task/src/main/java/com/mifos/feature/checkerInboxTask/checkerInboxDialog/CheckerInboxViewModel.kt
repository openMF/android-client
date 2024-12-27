/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.checkerInboxTask.checkerInboxDialog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.modelobjects.checkerinboxtask.CheckerInboxSearchTemplate
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class CheckerInboxViewModel @Inject constructor(
    private val dataManager: DataManagerCheckerInbox,
) : ViewModel() {

    private val _searchTemplate = MutableStateFlow<CheckerInboxSearchTemplate?>(null)
    val searchTemplate: StateFlow<CheckerInboxSearchTemplate?> = _searchTemplate

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
