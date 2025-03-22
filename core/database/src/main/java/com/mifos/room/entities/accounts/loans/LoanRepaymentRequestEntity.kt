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

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LoanRepaymentRequestEntity(

// TODO(check this out)
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,

    @PrimaryKey
    val timeStamp: Long = 0,

    val loanId: Int? = null,

    val errorMessage: String? = null,

    val dateFormat: String? = null,

    val locale: String? = null,

    val transactionDate: String? = null,

    val transactionAmount: String? = null,

    val paymentTypeId: String? = null,

    val note: String? = null,

    val accountNumber: String? = null,

    val checkNumber: String? = null,

    val routingCode: String? = null,

    val receiptNumber: String? = null,

    val bankNumber: String? = null,
)
