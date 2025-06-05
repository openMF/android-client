/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.checker.inbox.task.checkerInboxDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mifos.core.common.utils.MFErrorParser
import com.mifos.core.model.objects.checkerinboxtask.CheckerInboxSearchTemplate
import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CheckerInboxDialogViewmodel(
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
                dataManager.getCheckerInboxSearchTemplate().collect { response ->
                    _searchTemplate.value = response
                }
            } catch (e: Exception) {
                Logger.e("CheckerInboxViewModel", e) { "Error loading search template" }
                MFErrorParser.errorMessage(e)
            }
        }
    }
}
