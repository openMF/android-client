/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.pathtracking.impl

import com.mifos.core.data.pathtracking.PathTrackingRepository
import com.mifos.core.model.objects.users.UserLocation
import com.mifos.core.network.pathtracking.api.PathTrackingApi

/**
 * Default [PathTrackingRepository] backed by [PathTrackingApi] (per-feature
 * Ktorfit interface, Phase C Wave 8 of store5-adoption).
 *
 * Errors propagate as exceptions — caller is responsible for wrapping in
 * `SubmitHandler.submit { ... }` (mutations) or try/catch → `ScreenState.Error`
 * (reads). No `DataState<*>`, no `withNetworkCheck`, no `runCatching`. The
 * shared `UserLocation` model is the on-wire shape — no DTO/mapper needed
 * (mirrors the legacy `DataManagerDataTable.getUserPathTracking` call, which
 * already returned the model type directly).
 */
class PathTrackingRepositoryImpl(
    private val pathTrackingApi: PathTrackingApi,
) : PathTrackingRepository {

    override suspend fun getUserPathTracking(userId: Int): List<UserLocation> =
        pathTrackingApi.getUserPathTracking(userId)

    override suspend fun addUserPathTracking(
        userId: Int,
        userLocation: UserLocation,
    ) {
        pathTrackingApi.addUserPathTracking(userId, userLocation)
    }
}
