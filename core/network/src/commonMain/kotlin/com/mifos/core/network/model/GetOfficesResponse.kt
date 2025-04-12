package com.mifos.core.network.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * GetOfficesResponse
 *
 * @param allowedParents
 * @param dateFormat
 * @param externalId
 * @param hierarchy
 * @param id
 * @param locale
 * @param name
 * @param nameDecorated
 * @param openingDate
 */

@Serializable
data class GetOfficesResponse(

    val allowedParents: kotlin.collections.List<GetOfficesResponse>? = null,

    val dateFormat: kotlin.String? = null,

    val externalId: kotlin.String? = null,

    val hierarchy: kotlin.String? = null,

    val id: kotlin.Long? = null,

    val locale: kotlin.String? = null,

    val name: kotlin.String? = null,

    val nameDecorated: kotlin.String? = null,

    @Contextual
    val openingDate: LocalDate? = null,

    )