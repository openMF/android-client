/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.checkerInboxTask.checkerInboxTasks

/**
 * Created by Aditya Gupta on 21/03/24.
 */

sealed class CheckerInboxTasksUiState {

    data object Loading : CheckerInboxTasksUiState()

    data class Error(val message: String) : CheckerInboxTasksUiState()

    data class Success(val checkerInboxBadge: String, val rescheduleLoanBadge: String) :
        CheckerInboxTasksUiState()
}
