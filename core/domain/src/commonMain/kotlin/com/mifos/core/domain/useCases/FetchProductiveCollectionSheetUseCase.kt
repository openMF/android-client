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
import com.mifos.core.common.utils.asDataStateFlow
import com.mifos.core.data.repository.GenerateCollectionSheetRepository
import com.mifos.core.model.objects.collectionsheets.CollectionSheetRequestPayload
import com.mifos.room.entities.collectionsheet.CollectionSheetResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchProductiveCollectionSheetUseCase(
    private val repository: GenerateCollectionSheetRepository,
) {

    operator fun invoke(
        centerId: Int,
        payload: CollectionSheetRequestPayload?,
    ): Flow<DataState<CollectionSheetResponse>> = flow {
        emit(repository.fetchProductiveCollectionSheet(centerId, payload))
    }.asDataStateFlow()
}
