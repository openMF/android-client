/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.client

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.mifos.core.objects.noncore.DataTablePayload
import kotlinx.parcelize.Parcelize

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
/**
 * Created by ADMIN on 16-Jun-15.
 */
@Parcelize
@Entity("ClientPayload")
data class ClientPayload(
    @PrimaryKey(autoGenerate = true)
    @Transient
    var id: Int? = null,

    @ColumnInfo("clientCreationTime")
    @Transient
    var clientCreationTime: Long? = null,

    @ColumnInfo("errorMessage")
    @Transient
    var errorMessage: String? = null,

    @ColumnInfo("firstname")
    var firstname: String? = null,

    @ColumnInfo("lastname")
    var lastname: String? = null,

    @ColumnInfo("middlename")
    var middlename: String? = null,

    @ColumnInfo("officeId")
    var officeId: Int? = null,

    @ColumnInfo("staffId")
    var staffId: Int? = null,

    @ColumnInfo("genderId")
    var genderId: Int? = null,

    @ColumnInfo("active")
    var active: Boolean? = null,

    @ColumnInfo("activationDate")
    var activationDate: String? = null,

    @ColumnInfo("submittedOnDate")
    var submittedOnDate: String? = null,

    @ColumnInfo("dateOfBirth")
    var dateOfBirth: String? = null,

    @ColumnInfo("mobileNo")
    var mobileNo: String? = null,

    @ColumnInfo("externalId")
    var externalId: String? = null,

    @ColumnInfo("clientTypeId")
    var clientTypeId: Int? = null,

    @ColumnInfo("clientClassificationId")
    var clientClassificationId: Int? = null,

    var address: List<Address>? = ArrayList(),

    @ColumnInfo("dateFormat")
    var dateFormat: String? = "dd MMMM YYYY",

    @ColumnInfo("locale")
    var locale: String? = "en",

    var datatables: List<DataTablePayload>? = null,
) : MifosBaseModel(), Parcelable
