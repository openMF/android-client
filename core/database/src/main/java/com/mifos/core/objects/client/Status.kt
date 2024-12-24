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
import kotlinx.parcelize.Parcelize

/**
 * This Model is the common for Client and Group. So we can use it in both client and group
 * database.
 * Created by ishankhanna on 09/02/14.
 */
@Parcelize
@Entity("Status")
data class Status(
    @PrimaryKey
    var id: Int = 0,

    @ColumnInfo("code")
    var code: String? = null,

    @ColumnInfo("value")
    var value: String? = null,
) : MifosBaseModel(), Parcelable {

    companion object {
        const val STATUS_ACTIVE = "Active"

        fun isActive(value: String): Boolean {
            return value == STATUS_ACTIVE
        }
    }
}
