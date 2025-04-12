package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 *
 *
 * @param id
 * @param name
 * @param position
 */

@Serializable
data class GetClientsAllowedDocumentTypes(

    val id: kotlin.Long? = null,

    val name: kotlin.String? = null,

    val position: kotlin.Int? = null,

    )