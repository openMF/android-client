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
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("SavingsAccountSummary")
class Summary(
    @PrimaryKey
    @Transient
    var savingsId: Int? = null,

    var currency: Currency? = null,

    @ColumnInfo("totalDeposits")
    var totalDeposits: Double? = null,

    @ColumnInfo("accountBalance")
    var accountBalance: Double? = null,

    @ColumnInfo("totalWithdrawals")
    var totalWithdrawals: Double? = null,

    @ColumnInfo("totalInterestEarned")
    var totalInterestEarned: Double? = null,
) : MifosBaseModel(), Parcelable
