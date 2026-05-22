/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.client.entity

import com.mifos.core.model.objects.clients.Address
import com.mifos.room.datatable.entity.DataTablePayload
import kotlinx.serialization.Serializable
import androidx.room3.Entity
import androidx.room3.PrimaryKey

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
)
