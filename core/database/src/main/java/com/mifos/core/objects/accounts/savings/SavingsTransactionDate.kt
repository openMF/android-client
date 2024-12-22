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

/**
 *
 * Created by Rajan Maurya on 17/08/16.
 */
@Parcelize
@Entity("SavingsTransactionDate")
data class SavingsTransactionDate(
    @PrimaryKey
    var transactionId: Int? = null,

    @ColumnInfo("year")
    var year: Int? = null,

    @ColumnInfo("month")
    var month: Int? = null,

    @ColumnInfo("day")
    var day: Int? = null,
) : MifosBaseModel(), Parcelable
