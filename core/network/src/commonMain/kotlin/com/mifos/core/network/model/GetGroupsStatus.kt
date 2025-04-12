package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 *
 *
 * @param code
 * @param description
 * @param id
 */

@Serializable
data class GetGroupsStatus(

    val code: kotlin.String? = null,

    val description: kotlin.String? = null,

    val id: kotlin.Long? = null,

    )