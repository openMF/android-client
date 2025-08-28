/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.client

import com.mifos.core.model.objects.clients.Address
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.entities.noncore.DataTablePayload
import com.mifos.room.utils.Entity
import com.mifos.room.utils.PrimaryKey
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "ClientPayload",
)
data class ClientPayloadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val clientCreationTime: Long? = null,

    val errorMessage: String? = null,

    val firstname: String? = null,

    val lastname: String? = null,

    val middlename: String? = null,

    val emailAddress: String? = null,

    val officeId: Int? = null,

    val staffId: Int? = null,

    val genderId: Int? = null,

    val active: Boolean? = null,

    val activationDate: String? = null,

    val submittedOnDate: String? = null,

    val dateOfBirth: String? = null,

    val mobileNo: String? = null,

    val externalId: String? = null,

    val clientTypeId: Int? = null,

    val clientClassificationId: Int? = null,

    val address: List<Address>? = emptyList(),

    val dateFormat: String? = null,

    val locale: String? = null,

    val datatables: List<DataTablePayload>? = null,

    // 1 for Person (Individual client)
    val legalFormId: Int? = null,
) : Parcelable
