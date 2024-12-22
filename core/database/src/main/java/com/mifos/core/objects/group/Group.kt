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
import com.mifos.core.model.MifosBaseModel
import com.mifos.core.objects.Timeline
import com.mifos.core.objects.client.Status
import kotlinx.parcelize.Parcelize

/**
 * This is the Groups Model Table
 * Created by ishankhanna on 28/06/14.
 */
@Parcelize
@Entity("Group")
data class Group(
    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo("accountNo")
    var accountNo: String? = null,

    @ColumnInfo("sync")
    @Transient
    var sync: Boolean = false,

    @ColumnInfo("name")
    var name: String? = null,

    var status: Status? = null,

    @ColumnInfo("active")
    var active: Boolean? = null,

    @ColumnInfo("groupDate")
    @Embedded
    @Transient
    var groupDate: GroupDate? = null,

    var activationDate: List<Int> = ArrayList(),

    @ColumnInfo("officeId")
    var officeId: Int? = null,

    @ColumnInfo("officeName")
    var officeName: String? = null,

    @ColumnInfo("centerId")
    var centerId: Int? = 0,

    @ColumnInfo("centerName")
    var centerName: String? = null,

    @ColumnInfo("staffId")
    var staffId: Int? = null,

    @ColumnInfo("staffName")
    var staffName: String? = null,

    @ColumnInfo("hierarchy")
    var hierarchy: String? = null,

    @ColumnInfo("groupLevel")
    var groupLevel: Int = 0,

    var timeline: Timeline? = null,

    var externalId: String? = null,
) : MifosBaseModel(), Parcelable
