/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.dbobjects.templates.clients

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
/**
 * Created by rajan on 13/3/16.
 */
@Parcelize
@Table(database = MifosDatabase::class, name = "ClientTemplateStaffOptions")
@ModelContainer
data class StaffOptions(
    @PrimaryKey
    var id: Int = 0,
    val firstname: String = "",
    val lastname: String = "",
    val displayName: String = "",
    val officeId: Int = 0,
    val officeName: String = "",
    @SerializedName("isLoanOfficer")
    @Column
    var isLoanOfficer: Boolean = false,
    @SerializedName("isActive")
    @Column
    var isActive: Boolean = false,
) : MifosBaseModel(), Parcelable
