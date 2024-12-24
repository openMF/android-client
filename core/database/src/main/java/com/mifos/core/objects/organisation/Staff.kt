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
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 14/07/14.
 */
@Parcelize
@Entity("Staff")
data class Staff(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo("firstname")
    var firstname: String? = null,

    @ColumnInfo("lastname")
    var lastname: String? = null,

    @ColumnInfo("mobileNo")
    var mobileNo: String? = null,

    @ColumnInfo("displayName")
    var displayName: String? = null,

    @ColumnInfo("officeId")
    var officeId: Int? = null,

    @ColumnInfo("officeName")
    var officeName: String? = null,

    @ColumnInfo("isLoanOfficer")
    var isLoanOfficer: Boolean? = null,

    @ColumnInfo("isActive")
    var isActive: Boolean? = null,
) : MifosBaseModel(), Parcelable
