/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.checker.inbox.task.checkerInbox

import androidclient.feature.checker_inbox_task.generated.resources.Res
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_approve_success
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_delete_success
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_failed_to_Load_Checker_Inbox
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_failed_to_approve
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_failed_to_delete
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_failed_to_reject
import androidclient.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_reject_success
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.DataState
import com.mifos.core.domain.useCases.ApproveCheckerUseCase
import com.mifos.core.domain.useCases.DeleteCheckerUseCase
import com.mifos.core.domain.useCases.GetCheckerTasksUseCase
import com.mifos.core.domain.useCases.RejectCheckerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckerInboxViewModel(
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
    ) = viewModelScope.launch {
        getCheckerInboxUseCase(actionName, entityName, resourceId).collect { result ->
            when (result) {
                is DataState.Error ->
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.Error(Res.string.feature_checker_inbox_task_failed_to_Load_Checker_Inbox)

                is DataState.Loading -> _checkerInboxUiState.value = CheckerInboxUiState.Loading

                is DataState.Success ->
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.CheckerTasksList(result.data)
            }
        }
    }

    fun approveCheckerEntry(auditId: Int) = viewModelScope.launch {
        approveCheckerUseCase(auditId).collect { result ->
            when (result) {
                is DataState.Error ->
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.Error(Res.string.feature_checker_inbox_task_failed_to_approve)

                is DataState.Loading -> Unit

                is DataState.Success -> {
                    loadCheckerTasks()
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.SuccessResponse(Res.string.feature_checker_inbox_task_approve_success)
                }
            }
        }
    }

    fun rejectCheckerEntry(auditId: Int) = viewModelScope.launch {
        rejectCheckerUseCase(auditId).collect { result ->
            when (result) {
                is DataState.Error ->
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.Error(Res.string.feature_checker_inbox_task_failed_to_reject)

                is DataState.Loading -> Unit

                is DataState.Success -> {
                    loadCheckerTasks()
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.SuccessResponse(Res.string.feature_checker_inbox_task_reject_success)
                }
            }
        }
    }

    fun deleteCheckerEntry(auditId: Int) = viewModelScope.launch {
        deleteCheckerUseCase(auditId).collect { result ->
            when (result) {
                is DataState.Error ->
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.Error(Res.string.feature_checker_inbox_task_failed_to_delete)

                is DataState.Loading -> Unit

                is DataState.Success -> {
                    loadCheckerTasks()
                    _checkerInboxUiState.value =
                        CheckerInboxUiState.SuccessResponse(Res.string.feature_checker_inbox_task_delete_success)
                }
            }
        }
    }
}
