/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.savings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SavingsAccountTransactionRequest")
data class SavingsAccountTransactionRequest(

    @Transient
    @PrimaryKey
    var savingAccountId: Int? = null,

    @ColumnInfo(name = "savingsAccountType")
    @Transient
    var savingsAccountType: String? = null,

    @ColumnInfo(name = "transactionType")
    @Transient
    var transactionType: String? = null,

    @ColumnInfo(name = "dateFormat")
    var dateFormat: String? = null,

    @ColumnInfo(name = "locale")
    var locale: String? = null,

    @ColumnInfo(name = "transactionDate")
    var transactionDate: String? = null,

    @ColumnInfo(name = "transactionAmount")
    var transactionAmount: String? = null,

    @ColumnInfo(name = "paymentTypeId")
    var paymentTypeId: String? = null,

    @ColumnInfo(name = "note")
    var note: String? = null,

    @ColumnInfo(name = "accountNumber")
    var accountNumber: String? = null,

    @ColumnInfo(name = "checkNumber")
    var checkNumber: String? = null,

    @ColumnInfo(name = "routingCode")
    var routingCode: String? = null,

    @ColumnInfo(name = "receiptNumber")
    var receiptNumber: String? = null,

    @ColumnInfo(name = "bankNumber")
    var bankNumber: String? = null,

    @ColumnInfo(name = "errorMessage")
    @Transient
    var errorMessage: String? = null,
)
