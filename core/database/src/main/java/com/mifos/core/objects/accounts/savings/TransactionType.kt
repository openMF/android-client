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
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("TransactionType")
data class TransactionType(
    @PrimaryKey
    var id: Int? = null,

    var code: String? = null,

    var value: String? = null,

    @ColumnInfo("deposit")
    var deposit: Boolean? = null,

    @ColumnInfo("withdrawal")
    var withdrawal: Boolean? = null,

    var interestPosting: Boolean? = null,

    var feeDeduction: Boolean? = null,

    var initiateTransfer: Boolean? = null,

    var approveTransfer: Boolean? = null,

    var withdrawTransfer: Boolean? = null,

    var rejectTransfer: Boolean? = null,

    var overdraftInterest: Boolean? = null,

    var writtenoff: Boolean? = null,

    var overdraftFee: Boolean? = null,
) : MifosBaseModel(), Parcelable
