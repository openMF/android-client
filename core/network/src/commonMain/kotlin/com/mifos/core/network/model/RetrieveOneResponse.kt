/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * GetStaffResponse
 *
 * @param displayName
 * @param externalId
 * @param firstname
 * @param id
 * @param isActive
 * @param isLoanOfficer
 * @param joiningDate
 * @param lastname
 * @param officeId
 * @param officeName
 */

@Serializable
data class RetrieveOneResponse(

    val displayName: kotlin.String? = null,

    val externalId: kotlin.String? = null,

    val firstname: kotlin.String? = null,

    val id: kotlin.Long? = null,

    val isActive: kotlin.Boolean? = null,

    val isLoanOfficer: kotlin.Boolean? = null,

    @Contextual
    val joiningDate: LocalDate? = null,

    val lastname: kotlin.String? = null,

    val officeId: kotlin.Long? = null,

    val officeName: kotlin.String? = null,

)
