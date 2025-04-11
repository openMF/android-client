package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 * PostClientsClientIdResponse
 *
 * @param clientId
 * @param officeId
 * @param resourceExternalId
 * @param resourceId
 */

@Serializable
data class PostClientsClientIdResponse(

    val clientId: kotlin.Long? = null,

    val officeId: kotlin.Long? = null,

    val resourceExternalId: kotlin.String? = null,

    val resourceId: kotlin.Long? = null,

    )