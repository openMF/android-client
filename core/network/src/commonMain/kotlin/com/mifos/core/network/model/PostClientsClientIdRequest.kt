package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 * PostClientsClientIdRequest
 *
 * @param activationDate
 * @param dateFormat
 * @param locale
 */

@Serializable
data class PostClientsClientIdRequest(

    val activationDate: kotlin.String? = null,

    val dateFormat: kotlin.String? = null,

    val locale: kotlin.String? = null,

    )