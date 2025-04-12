package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 * GetClientsClientIdIdentifiersTemplateResponse
 *
 * @param allowedDocumentTypes
 */

@Serializable
data class GetClientsClientIdIdentifiersTemplateResponse(

    val allowedDocumentTypes: kotlin.collections.Set<GetClientsAllowedDocumentTypes>? = null,

    )