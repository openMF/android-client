package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 * GetClientsResponse
 *
 * @param pageItems
 * @param totalFilteredRecords
 */

@Serializable
data class GetClientsResponse(

    val pageItems: kotlin.collections.Set<GetClientsPageItemsResponse>? = null,

    val totalFilteredRecords: kotlin.Int? = null,

    )