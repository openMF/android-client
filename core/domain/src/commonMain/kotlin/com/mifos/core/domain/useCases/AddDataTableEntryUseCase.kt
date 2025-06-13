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
import com.mifos.core.data.repository.DataTableRowDialogRepository
import com.mifos.core.network.GenericResponse

class AddDataTableEntryUseCase(
    private val repository: DataTableRowDialogRepository,
) {
    suspend operator fun invoke(
        table: String,
        entityId: Int,
        payload: Map<String, String>,
    ): DataState<GenericResponse> {
        return try {
            val response = repository.addDataTableEntry(table, entityId, payload)
            DataState.Success(response)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
