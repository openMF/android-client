/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.loan

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("LoanAccount")
data class LoanAccount(
    @ColumnInfo("clientId")
    var clientId: Long = 0,

    @ColumnInfo("groupId")
    var groupId: Long = 0,

    @ColumnInfo("centerId")
    var centerId: Long = 0,

    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo("accountNo")
    var accountNo: String? = null,

    @ColumnInfo("externalId")
    var externalId: String? = null,

    @ColumnInfo("productId")
    var productId: Int? = null,

    @ColumnInfo("productName")
    var productName: String? = null,

    @ColumnInfo("status")
    @Embedded
    var status: Status? = null,

    @ColumnInfo("loanType")
    @Embedded
    var loanType: LoanType? = null,

    @ColumnInfo("loanCycle")
    var loanCycle: Int? = null,

    @ColumnInfo("inArrears")
    var inArrears: Boolean? = null,
) : MifosBaseModel(), Parcelable {

    fun isInArrears(): Boolean? {
        return inArrears
    }
}
