/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.pathtracking.api

import com.mifos.core.model.objects.users.UserLocation
import com.mifos.core.network.GenericResponse
import com.mifos.room.basemodel.APIEndPoint
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

/**
 * Fineract `datatables/m_staff_path_tracking/{userId}` endpoints. Per-feature API
 * surface (Phase C Wave 8 of store5-adoption). Modern suspend-only contract —
 * `getUserPathTracking` and `addUserPathTracking` return `List<UserLocation>` /
 * `GenericResponse` directly instead of `Flow<...>` (legacy `DataTableService`
 * shape).
 *
 * Errors throw (`HttpException` / transport exceptions); callers handle via
 * `SubmitHandler.submit { ... }` for mutations or explicit try/catch wrapped to
 * `ScreenState<T>` for reads. No `runCatching` per RULE-NO-RUN-CATCHING-001.
 *
 * The legacy `m_staff_path_tracking` REST surface lives on the shared
 * `datatables/{tableName}/{entityId}` endpoint — see also
 * `core.network.services.DataTableService.getUserPathTracking` /
 * `addUserPathTracking` (deprecated, retained for non-feature consumers until
 * Phase D cleanup).
 */
interface PathTrackingApi {

    /**
     * Retrieve the list of saved user-location track entries for [userId] (the
     * staff/officer id, served from the `m_staff_path_tracking` data table).
     * Throws on HTTP failure or transport error.
     */
    @GET(APIEndPoint.DATATABLES + "/m_staff_path_tracking/{userId}")
    suspend fun getUserPathTracking(
        @Path("userId") userId: Int,
    ): List<UserLocation>

    /**
     * Append a new user-location entry for [userId]. Throws on HTTP failure or
     * transport error.
     */
    @POST(APIEndPoint.DATATABLES + "/m_staff_path_tracking/{userId}")
    suspend fun addUserPathTracking(
        @Path("userId") userId: Int,
        @Body userLocation: UserLocation,
    ): GenericResponse
}
