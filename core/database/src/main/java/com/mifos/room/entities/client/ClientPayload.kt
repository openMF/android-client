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

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ClientPayload")
data class ClientPayload(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    @Transient
    val clientCreationTime: Long? = null,

    @Transient
    val errorMessage: String? = null,

    val firstname: String? = null,

    val lastname: String? = null,

    val middlename: String? = null,

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

    val address: String? = null,

    val dateFormat: String? = "dd MMMM YYYY",

    val locale: String? = "en",

    val datatables: String? = null,
) : Parcelable
