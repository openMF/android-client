/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("SavingsAccount")
data class SavingsAccount(
    @ColumnInfo("clientId")
    @Transient
    var clientId: Long = 0,

    @ColumnInfo("groupId")
    @Transient
    var groupId: Long = 0,

    @ColumnInfo("centerId")
    var centerId: Long = 0,

    @PrimaryKey
    var id: Int? = null,

    @ColumnInfo("accountNo")
    var accountNo: String? = null,

    @ColumnInfo("productId")
    var productId: Int? = null,

    @ColumnInfo("productName")
    var productName: String? = null,

    @ColumnInfo("status")
    @Embedded
    var status: Status? = null,

    @ColumnInfo("currency")
    @Embedded
    var currency: Currency? = null,

    @ColumnInfo("accountBalance")
    var accountBalance: Double? = null,

    @ColumnInfo("depositType")
    @Embedded
    var depositType: DepositType? = null,
) : MifosBaseModel(), Parcelable
