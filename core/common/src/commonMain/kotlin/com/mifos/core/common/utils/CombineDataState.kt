/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.common.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

fun <T1, T2, R> combineDataState(
    flow1: Flow<DataState<T1>>,
    flow2: Flow<DataState<T2>>,
    transform: (T1, T2) -> R,
): Flow<DataState<R>> = combine(flow1, flow2) { state1, state2 ->
    when {
        state1 is DataState.Loading || state2 is DataState.Loading -> DataState.Loading
        state1 is DataState.Error -> DataState.Error(state1.exception)
        state2 is DataState.Error -> DataState.Error(state2.exception)
        state1 is DataState.Success && state2 is DataState.Success ->
            DataState.Success(transform(state1.data, state2.data))
        else -> DataState.Loading
    }
}
