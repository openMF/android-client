package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 *
 *
 * @param active
 * @param hierarchy
 * @param id
 * @param name
 * @param officeId
 * @param officeName
 * @param status
 */

@Serializable
data class GetCentersPageItems(

    val active: kotlin.Boolean? = null,

    val hierarchy: kotlin.String? = null,

    val id: kotlin.Long? = null,

    val name: kotlin.String? = null,

    val officeId: kotlin.Long? = null,

    val officeName: kotlin.String? = null,

    val status: GetCentersStatus? = null,

    )