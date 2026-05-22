/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.network.staff.api

import com.mifos.core.model.network.RetrieveOneResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

/** Fineract `staff` domain endpoints. */
interface StaffApi {

    /**
     * Retrieve staff members, optionally filtered by office, hierarchy, role and status.
     *
     * @param status one of `active` (default), `inactive`, `all`.
     */
    @GET("staff")
    suspend fun retrieveStaff(
        @Query("officeId") officeId: Long? = null,
        @Query("staffInOfficeHierarchy") staffInOfficeHierarchy: Boolean? = false,
        @Query("loanOfficersOnly") loanOfficersOnly: Boolean? = false,
        @Query("status") status: String? = "active",
    ): List<RetrieveOneResponse>
}
