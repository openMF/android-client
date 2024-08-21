package com.mifos.feature.checker_inbox_task.checker_inbox

import com.mifos.core.objects.checkerinboxandtasks.CheckerInboxSearchTemplate
import com.mifos.core.objects.checkerinboxandtasks.CheckerTask

sealed class CheckerInboxUiState {

    data object Loading : CheckerInboxUiState()

    data class Error(val message: Int) : CheckerInboxUiState()

    data class CheckerTasksList(val checkerTasks: List<CheckerTask>) : CheckerInboxUiState()

    data class SuccessResponse(val message: Int) : CheckerInboxUiState()
}