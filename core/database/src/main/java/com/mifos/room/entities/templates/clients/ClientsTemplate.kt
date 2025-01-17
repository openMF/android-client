/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.clients

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.room.entities.noncore.DataTable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ClientsTemplate")
data class ClientsTemplate(
    @ColumnInfo(name = "activationDate")
    var activationDate: IntArray = intArrayOf(),

    @PrimaryKey
    @ColumnInfo(name = "officeId")
    var officeId: Int = 0,

    @ColumnInfo(name = "officeOptions")
    var officeOptions: List<OfficeOptions> = ArrayList(),

    @ColumnInfo(name = "staffOptions")
    var staffOptions: List<StaffOptions> = ArrayList(),

    @ColumnInfo(name = "savingProductOptions")
    var savingProductOptions: List<SavingProductOptions> = ArrayList(),

    @ColumnInfo(name = "genderOptions")
    var genderOptions: List<Options> = ArrayList(),

    @ColumnInfo(name = "clientTypeOptions")
    var clientTypeOptions: List<Options> = ArrayList(),

    @ColumnInfo(name = "clientClassificationOptions")
    var clientClassificationOptions: List<Options> = ArrayList(),

    @ColumnInfo(name = "clientLegalFormOptions")
    var clientLegalFormOptions: List<InterestType> = ArrayList(),

    @ColumnInfo(name = "dataTables")
    var dataTables: List<DataTable> = ArrayList(),
) : Parcelable {

    override fun toString(): String {
        return "ClientsTemplate{" +
            "activationDate=" + activationDate.contentToString() +
            ", officeId=" + officeId +
            ", officeOptions=" + officeOptions +
            ", staffOptions=" + staffOptions +
            ", savingProductOptions=" + savingProductOptions +
            ", genderOptions=" + genderOptions +
            ", clientTypeOptions=" + clientTypeOptions +
            ", clientClassificationOptions=" + clientClassificationOptions +
            ", clientLegalFormOptions=" + clientLegalFormOptions +
            '}'
    }
}
