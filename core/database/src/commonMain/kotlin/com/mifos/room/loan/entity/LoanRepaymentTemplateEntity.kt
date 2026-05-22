/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.loan.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.mifos.room.savings.entity.PaymentTypeOptionEntity
import com.mifos.room.savings.entity.SavingAccountCurrencyEntity
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "LoanRepaymentTemplate",
)
data class LoanRepaymentTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val loanId: Int = 0,

    val type: LoanTypeEntity? = null,

    val date: List<Int>? = null,

    val currency: SavingAccountCurrencyEntity? = null,

    val amount: Double? = null,

    val principalPortion: Double? = null,

    val interestPortion: Double? = null,

    val feeChargesPortion: Double? = null,

    val penaltyChargesPortion: Double? = null,

    val paymentTypeOptions: List<PaymentTypeOptionEntity>? = null,
)
