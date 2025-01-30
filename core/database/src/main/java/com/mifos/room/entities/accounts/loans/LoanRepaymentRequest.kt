/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.loans

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LoanRepaymentRequest(

// TODO(check this out)
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,

    @ColumnInfo(name = "timeStamp")
    @PrimaryKey
    @Transient
    val timeStamp: Long = 0,

    @ColumnInfo(name = "loanId")
    @Transient
    val loanId: Int? = null,

    @ColumnInfo(name = "errorMessage")
    @Transient
    val errorMessage: String? = null,

    @ColumnInfo(name = "dateFormat")
    val dateFormat: String? = null,

    @ColumnInfo(name = "locale")
    val locale: String? = null,

    @ColumnInfo(name = "transactionDate")
    val transactionDate: String? = null,

    @ColumnInfo(name = "transactionAmount")
    val transactionAmount: String? = null,

    @ColumnInfo(name = "paymentTypeId")
    val paymentTypeId: String? = null,

    @ColumnInfo(name = "note")
    val note: String? = null,

    @ColumnInfo(name = "accountNumber")
    val accountNumber: String? = null,

    @ColumnInfo(name = "checkNumber")
    val checkNumber: String? = null,

    @ColumnInfo(name = "routingCode")
    val routingCode: String? = null,

    @ColumnInfo(name = "receiptNumber")
    val receiptNumber: String? = null,

    @ColumnInfo(name = "bankNumber")
    val bankNumber: String? = null,
)
