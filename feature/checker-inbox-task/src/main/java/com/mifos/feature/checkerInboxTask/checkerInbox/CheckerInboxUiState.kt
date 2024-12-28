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

import com.mifos.core.`object`.checkerinboxtask.CheckerTask

sealed class CheckerInboxUiState {

    data object Loading : CheckerInboxUiState()

    data class Error(val message: Int) : CheckerInboxUiState()

    data class CheckerTasksList(val checkerTasks: List<CheckerTask>) : CheckerInboxUiState()

    data class SuccessResponse(val message: Int) : CheckerInboxUiState()
}
