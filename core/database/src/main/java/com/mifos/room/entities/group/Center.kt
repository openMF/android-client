/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.group

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mifos.room.entities.accounts.loans.Timeline
import com.mifos.room.entities.client.Status
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "Center",
    foreignKeys = [
        ForeignKey(
            entity = CenterDate::class,
            parentColumns = ["centerId"],
            childColumns = ["centerDate"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class Center(
    @PrimaryKey
    var id: Int? = null,

    @Transient
    var sync: Boolean = false,

    var accountNo: String? = null,

    var name: String? = null,

    var officeId: Int? = null,

    var officeName: String? = null,

    var staffId: Int? = null,

    var staffName: String? = null,

    var hierarchy: String? = null,

    var status: Status? = null,

    var active: Boolean? = null,

    @Transient
    var centerDate: CenterDate? = null,

    var activationDate: List<Int?> = emptyList(),

    var timeline: Timeline? = null,

    var externalId: String? = null,
) : Parcelable
