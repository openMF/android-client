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
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ClientPayload")
data class ClientPayload(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "clientCreationTime")
    @Transient
    var clientCreationTime: Long? = null,

    @ColumnInfo(name = "errorMessage")
    @Transient
    var errorMessage: String? = null,

    @ColumnInfo(name = "firstname")
    var firstname: String? = null,

    @ColumnInfo(name = "lastname")
    var lastname: String? = null,

    @ColumnInfo(name = "middlename")
    var middlename: String? = null,

    @ColumnInfo(name = "officeId")
    var officeId: Int? = null,

    @ColumnInfo(name = "staffId")
    var staffId: Int? = null,

    @ColumnInfo(name = "genderId")
    var genderId: Int? = null,

    @ColumnInfo(name = "active")
    var active: Boolean? = null,

    @ColumnInfo(name = "activationDate")
    var activationDate: String? = null,

    @ColumnInfo(name = "submittedOnDate")
    var submittedOnDate: String? = null,

    @ColumnInfo(name = "dateOfBirth")
    var dateOfBirth: String? = null,

    @ColumnInfo(name = "mobileNo")
    var mobileNo: String? = null,

    @ColumnInfo(name = "externalId")
    var externalId: String? = null,

    @ColumnInfo(name = "clientTypeId")
    var clientTypeId: Int? = null,

    @ColumnInfo(name = "clientClassificationId")
    var clientClassificationId: Int? = null,

    @ColumnInfo(name = "address")
    var address: String? = null,

    @ColumnInfo(name = "dateFormat")
    var dateFormat: String? = "dd MMMM YYYY",

    @ColumnInfo(name = "locale")
    var locale: String? = "en",

    @ColumnInfo(name = "datatables")
    var datatables: String? = null,
) : Parcelable
