package com.mifos.feature.checker_inbox_task.checker_inbox_tasks


/**
 * Created by Aditya Gupta on 21/03/24.
 */

sealed class CheckerInboxTasksUiState {

    data object Loading : CheckerInboxTasksUiState()

    data class Error(val message: String) : CheckerInboxTasksUiState()

    data class Success(val checkerInboxBadge: String, val rescheduleLoanBadge: String) :
        CheckerInboxTasksUiState()
}