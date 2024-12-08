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
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 12/06/14.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class SavingsAccountTransactionRequest(
    @PrimaryKey
    @Transient
    var savingAccountId: Int? = null,

    @Column
    @Transient
    var savingsAccountType: String? = null,

    @Column
    @Transient
    var transactionType: String? = null,

    @Column
    var dateFormat: String? = null,

    @Column
    var locale: String? = null,

    @Column
    var transactionDate: String? = null,

    @Column
    var transactionAmount: String? = null,

    @Column
    var paymentTypeId: String? = null,

    @Column
    var note: String? = null,

    @Column
    var accountNumber: String? = null,

    @Column
    var checkNumber: String? = null,

    @Column
    var routingCode: String? = null,

    @Column
    var receiptNumber: String? = null,

    @Column
    var bankNumber: String? = null,

    @Column
    @Transient
    var errorMessage: String? = null,
) : MifosBaseModel(), Parcelable
