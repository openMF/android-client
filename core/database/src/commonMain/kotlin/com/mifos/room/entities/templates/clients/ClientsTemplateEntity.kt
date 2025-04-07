/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.clients

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.entities.noncore.DataTableEntity
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
    tableName = "ClientsTemplate",
)
data class ClientsTemplateEntity(
    val activationDate: List<Int> = emptyList(),

    @PrimaryKey(autoGenerate = true)
    val officeId: Int = 0,

    val officeOptions: List<OfficeOptionsEntity>? = emptyList(),

    val staffOptions: List<StaffOptionsEntity>? = emptyList(),

    val savingProductOptions: List<SavingProductOptionsEntity>? = emptyList(),

    val genderOptions: List<OptionsEntity>? = emptyList(),

    val clientTypeOptions: List<OptionsEntity>? = emptyList(),

    val clientClassificationOptions: List<OptionsEntity>? = emptyList(),

    val clientLegalFormOptions: List<InterestTypeEntity>? = emptyList(),

    val dataTables: List<DataTableEntity>? = emptyList(),
) : Parcelable
