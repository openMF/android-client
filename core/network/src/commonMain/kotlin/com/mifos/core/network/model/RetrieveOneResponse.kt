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