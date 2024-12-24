/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.templates.loans

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("LoanRepaymentTemplate")
data class LoanRepaymentTemplate(
    @PrimaryKey(autoGenerate = true)
    var loanId: Int? = null,

    var type: Type? = null,

    var date: MutableList<Int>? = null,

    var currency: Currency? = null,

    @ColumnInfo("amount")
    var amount: Double? = null,

    @ColumnInfo("principalPortion")
    var principalPortion: Double? = null,

    @ColumnInfo("interestPortion")
    var interestPortion: Double? = null,

    @ColumnInfo("feeChargesPortion")
    var feeChargesPortion: Double? = null,

    @ColumnInfo("penaltyChargesPortion")
    var penaltyChargesPortion: Double? = null,

    var paymentTypeOptions: MutableList<com.mifos.core.objects.PaymentTypeOption>? = null,
) : MifosBaseModel(), Parcelable
