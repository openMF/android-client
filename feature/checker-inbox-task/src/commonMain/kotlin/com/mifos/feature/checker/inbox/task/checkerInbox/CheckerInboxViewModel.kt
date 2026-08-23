/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.checker.inbox.task.checkerInbox

import kpt.feature.checker_inbox_task.generated.resources.Res
import kpt.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_approve_success
import kpt.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_delete_success
import kpt.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_failed_to_Load_Checker_Inbox
import kpt.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_failed_to_approve
import kpt.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_failed_to_delete
import kpt.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_failed_to_reject
import kpt.feature.checker_inbox_task.generated.resources.feature_checker_inbox_task_reject_success
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.domain.useCases.ApproveCheckerUseCase
import com.mifos.core.domain.useCases.DeleteCheckerUseCase
import com.mifos.core.domain.useCases.GetCheckerTasksUseCase
import com.mifos.core.domain.useCases.RejectCheckerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
        _checkerInboxUiState.value = CheckerInboxUiState.Loading
        getCheckerInboxUseCase(actionName, entityName, resourceId)
            .catch {
                _checkerInboxUiState.value =
                    CheckerInboxUiState.Error(Res.string.feature_checker_inbox_task_failed_to_Load_Checker_Inbox)
            }
            .collect { checkerTasks ->
                _checkerInboxUiState.value =
                    CheckerInboxUiState.CheckerTasksList(checkerTasks)
            }
    }

    fun approveCheckerEntry(auditId: Int) = viewModelScope.launch {
        approveCheckerUseCase(auditId)
            .catch {
                _checkerInboxUiState.value =
                    CheckerInboxUiState.Error(Res.string.feature_checker_inbox_task_failed_to_approve)
            }
            .collect {
                loadCheckerTasks()
                _checkerInboxUiState.value =
                    CheckerInboxUiState.SuccessResponse(Res.string.feature_checker_inbox_task_approve_success)
            }
    }

    fun rejectCheckerEntry(auditId: Int) = viewModelScope.launch {
        rejectCheckerUseCase(auditId)
            .catch {
                _checkerInboxUiState.value =
                    CheckerInboxUiState.Error(Res.string.feature_checker_inbox_task_failed_to_reject)
            }
            .collect {
                loadCheckerTasks()
                _checkerInboxUiState.value =
                    CheckerInboxUiState.SuccessResponse(Res.string.feature_checker_inbox_task_reject_success)
            }
    }

    fun deleteCheckerEntry(auditId: Int) = viewModelScope.launch {
        deleteCheckerUseCase(auditId)
            .catch {
                _checkerInboxUiState.value =
                    CheckerInboxUiState.Error(Res.string.feature_checker_inbox_task_failed_to_delete)
            }
            .collect {
                loadCheckerTasks()
                _checkerInboxUiState.value =
                    CheckerInboxUiState.SuccessResponse(Res.string.feature_checker_inbox_task_delete_success)
            }
    }
}
