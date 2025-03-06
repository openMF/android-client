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
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.room.entities.noncore.DataTable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ClientsTemplate")
data class ClientsTemplate(
    var activationDate: IntArray = intArrayOf(),

    @PrimaryKey
    var officeId: Int = 0,

    var officeOptions: List<OfficeOptions> = emptyList(),

    var staffOptions: List<StaffOptions> = emptyList(),

    var savingProductOptions: List<SavingProductOptions> = emptyList(),

    var genderOptions: List<Options> = emptyList(),

    var clientTypeOptions: List<Options> = emptyList(),

    var clientClassificationOptions: List<Options> = emptyList(),

    var clientLegalFormOptions: List<InterestType> = emptyList(),

    var dataTables: List<DataTable> = emptyList(),
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
