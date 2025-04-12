package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 * GetClientsClientIdIdentifiersResponse
 *
 * @param clientId
 * @param description
 * @param documentKey
 * @param documentType
 * @param id
 */

@Serializable
data class GetClientsClientIdIdentifiersResponse(

    val clientId: kotlin.Long? = null,

    val description: kotlin.String? = null,

    val documentKey: kotlin.String? = null,

    val documentType: GetClientsDocumentType? = null,

    val id: kotlin.Long? = null,

    )