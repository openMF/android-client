package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 *
 *
 * @param accountNo
 * @param active
 * @param displayName
 * @param emailAddress
 * @param fullname
 * @param id
 * @param officeId
 * @param officeName
 * @param status
 */

@Serializable
data class GetClientsPageItemsResponse(

    val accountNo: kotlin.String? = null,

    val active: kotlin.Boolean? = null,

    val displayName: kotlin.String? = null,

    val emailAddress: kotlin.String? = null,

    val fullname: kotlin.String? = null,

    val id: kotlin.Long? = null,

    val officeId: kotlin.Long? = null,

    val officeName: kotlin.String? = null,

    val status: GetClientStatus? = null,

    )