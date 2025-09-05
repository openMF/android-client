/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.model

import com.mifos.core.model.objects.account.share.ShareAccounts
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

    val accountNo: String? = null,

    val active: Boolean? = null,

    val displayName: String? = null,

    val emailAddress: String? = null,

    val fullName: String? = null,

    val id: Long? = null,

    val officeId: Long? = null,

    val officeName: String? = null,

    val status: GetClientStatus? = null,

    val externalId: String? = null,

    val legalForm: GetClientStatus? = null,

    val dateOfBirth: List<Int?> = emptyList(),

    val staffOptions: List<StaffOption> = emptyList(),

    val savingAccountOptions: List<SavingAccountOption> = emptyList(),

    val shareAccounts: List<ShareAccounts> = emptyList(),
)
