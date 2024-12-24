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
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 22/05/14.
 */
@Parcelize
@Entity("LoanRepaymentRequest")
data class LoanRepaymentRequest(
    @PrimaryKey
    @Transient
    var timeStamp: Long = 0,

    @ColumnInfo("loanId")
    @Transient
    var loanId: Int? = null,

    @ColumnInfo("errorMessage")
    @Transient
    var errorMessage: String? = null,

    @ColumnInfo("dateFormat")
    var dateFormat: String? = null,

    @ColumnInfo("locale")
    var locale: String? = null,

    @ColumnInfo("transactionDate")
    var transactionDate: String? = null,

    @ColumnInfo("transactionAmount")
    var transactionAmount: String? = null,

    @ColumnInfo("paymentTypeId")
    var paymentTypeId: String? = null,

    @ColumnInfo("note")
    var note: String? = null,

    @ColumnInfo("accountNumber")
    var accountNumber: String? = null,

    @ColumnInfo("checkNumber")
    var checkNumber: String? = null,

    @ColumnInfo("routingCode")
    var routingCode: String? = null,

    @ColumnInfo("receiptNumber")
    var receiptNumber: String? = null,

    @ColumnInfo("bankNumber")
    var bankNumber: String? = null,
) : MifosBaseModel(), Parcelable
