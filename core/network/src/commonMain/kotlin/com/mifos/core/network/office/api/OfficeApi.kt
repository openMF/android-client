/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.office.api

import com.mifos.core.model.network.GetOfficesResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

/** Fineract `office` domain endpoints. */
interface OfficeApi {

    @GET("offices")
    suspend fun retrieveOffices(
        @Query("includeAllOffices") includeAllOffices: Boolean? = false,
        @Query("orderBy") orderBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
    ): List<GetOfficesResponse>
}
