/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.group

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.mifos.core.objects.Timeline
import com.mifos.core.objects.client.Status
import kotlinx.parcelize.Parcelize

/**
 * This is Center Model Table
 * Created by ishankhanna on 11/03/14.
 */
@Parcelize
@Entity("Center")
data class Center(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo("sync")
    @Transient
    var sync: Boolean = false,

    @ColumnInfo("accountNo")
    var accountNo: String? = null,

    @ColumnInfo("name")
    var name: String? = null,

    @ColumnInfo("officeId")
    var officeId: Int? = null,

    @ColumnInfo("officeName")
    var officeName: String? = null,

    @ColumnInfo("staffId")
    var staffId: Int? = null,

    @ColumnInfo("staffName")
    var staffName: String? = null,

    @ColumnInfo("hierarchy")
    var hierarchy: String? = null,

    var status: Status? = null,

    @ColumnInfo("active")
    var active: Boolean? = null,

    @ColumnInfo("centerDate")
    @Embedded
    @Transient
    var centerDate: CenterDate? = null,

    var activationDate: List<Int?> = ArrayList(),

    var timeline: Timeline? = null,

    var externalId: String? = null,
) : MifosBaseModel(), Parcelable
