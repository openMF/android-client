/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.CheckerInboxTasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Created by Aditya Gupta on 21/03/24.
 */

class GetCheckerInboxBadgesUseCase(
    private val repository: CheckerInboxTasksRepository,
) {
    operator fun invoke(): Flow<DataState<Pair<Int, Int>>> = combine(
        repository.getCheckerTaskList(),
        repository.getRescheduleLoansTaskList(),
    ) { checkerTaskState, rescheduleTaskState ->

        if (checkerTaskState is DataState.Loading || rescheduleTaskState is DataState.Loading) {
            return@combine DataState.Loading
        }

        val errors = mutableListOf<Throwable>()
        if (checkerTaskState is DataState.Error) errors.add(checkerTaskState.exception)
        if (rescheduleTaskState is DataState.Error) errors.add(rescheduleTaskState.exception)

        if (errors.isNotEmpty()) {
            val combined = CombinedException(errors)
            return@combine DataState.Error(combined)
        }

        val checkerTaskSize = checkerTaskState.data?.size ?: 0
        val rescheduleTaskSize = rescheduleTaskState.data?.size ?: 0
        DataState.Success(checkerTaskSize to rescheduleTaskSize)
    }
}

class CombinedException(
    val errors: List<Throwable>,
) : Exception(
    errors.joinToString(separator = "\n") { it.message ?: "Unknown error" },
)
