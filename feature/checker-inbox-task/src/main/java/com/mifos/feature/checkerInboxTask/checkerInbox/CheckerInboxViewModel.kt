/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.checkerInboxTask.checkerInbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.useCases.ApproveCheckerUseCase
import com.mifos.core.domain.useCases.DeleteCheckerUseCase
import com.mifos.core.domain.useCases.GetCheckerTasksUseCase
import com.mifos.core.domain.useCases.RejectCheckerUseCase
import com.mifos.feature.checker_inbox_task.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckerInboxViewModel @Inject constructor(
    val getCheckerInboxUseCase: GetCheckerTasksUseCase,
    val approveCheckerUseCase: ApproveCheckerUseCase,
    val rejectCheckerUseCase: RejectCheckerUseCase,
    val deleteCheckerUseCase: DeleteCheckerUseCase,
) : ViewModel() {

    private val _checkerInboxUiState =
        MutableStateFlow<CheckerInboxUiState>(CheckerInboxUiState.Loading)
    val checkerInboxUiState = _checkerInboxUiState.asStateFlow()

    fun loadCheckerTasks(
        actionName: String? = null,
        entityName: String? = null,
        resourceId: Int? = null,
    ) = viewModelScope.launch(Dispatchers.IO) {
        getCheckerInboxUseCase(actionName, entityName, resourceId).collect { result ->
            when (result) {
                is Resource.Error ->
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.Error(R.string.feature_checker_inbox_task_failed_to_Load_Check_Inbox)

                is Resource.Loading -> _checkerInboxUiState.value = CheckerInboxUiState.Loading

                is Resource.Success ->
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.CheckerTasksList(result.data ?: emptyList())
            }
        }
    }

    fun approveCheckerEntry(auditId: Int) = viewModelScope.launch(Dispatchers.IO) {
        approveCheckerUseCase(auditId).collect { result ->
            when (result) {
                is Resource.Error ->
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.Error(R.string.feature_checker_inbox_task_failed_to_approve)

                is Resource.Loading -> Unit

                is Resource.Success -> {
                    loadCheckerTasks()
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.SuccessResponse(R.string.feature_checker_inbox_task_approve_success)
                }
            }
        }
    }

    fun rejectCheckerEntry(auditId: Int) = viewModelScope.launch(Dispatchers.IO) {
        rejectCheckerUseCase(auditId).collect { result ->
            when (result) {
                is Resource.Error ->
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.Error(R.string.feature_checker_inbox_task_failed_to_reject)

                is Resource.Loading -> Unit

                is Resource.Success -> {
                    loadCheckerTasks()
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.SuccessResponse(R.string.feature_checker_inbox_task_reject_success)
                }
            }
        }
    }

    fun deleteCheckerEntry(auditId: Int) = viewModelScope.launch(Dispatchers.IO) {
        deleteCheckerUseCase(auditId).collect { result ->
            when (result) {
                is Resource.Error ->
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.Error(R.string.feature_checker_inbox_task_failed_to_delete)

                is Resource.Loading -> Unit

                is Resource.Success -> {
                    loadCheckerTasks()
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.SuccessResponse(R.string.feature_checker_inbox_task_delete_success)
                }
            }
        }
    }
}
