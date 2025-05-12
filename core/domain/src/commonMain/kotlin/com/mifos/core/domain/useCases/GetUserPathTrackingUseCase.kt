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
import com.mifos.core.data.repository.PathTrackingRepository
import com.mifos.core.model.objects.users.UserLocation
import kotlinx.coroutines.flow.Flow

class GetUserPathTrackingUseCase(
    private val repository: PathTrackingRepository,
) {

    operator fun invoke(userId: Int): Flow<DataState<List<UserLocation>>> =
        repository.getUserPathTracking(userId)
}
