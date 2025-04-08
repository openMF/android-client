/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.organisation

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 14/07/14.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class Staff(
    @PrimaryKey
    var id: Int? = null,

    @Column
    var firstname: String? = null,

    @Column
    var lastname: String? = null,

    @Column
    var mobileNo: String? = null,

    @Column
    var displayName: String? = null,

    @Column
    var officeId: Int? = null,

    @Column
    var officeName: String? = null,

    @Column
    var isLoanOfficer: Boolean? = null,

    @Column
    var isActive: Boolean? = null,
) : MifosBaseModel(), Parcelable

data class StaffResponse(
    @SerializedName("displayName")
    val displayName: String? = null,

    @SerializedName("externalId")
    val externalId: String? = null,

    @SerializedName("firstname")
    val firstname: String? = null,

    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("isActive")
    val isActive: Boolean? = null,

    @SerializedName("isLoanOfficer")
    val isLoanOfficer: Boolean? = null,

    @SerializedName("joiningDate")
    val joiningDate: List<Int>? = null,

    @SerializedName("lastname")
    val lastname: String? = null,

    @SerializedName("officeId")
    val officeId: Long? = null,

    @SerializedName("officeName")
    val officeName: String? = null,
)
