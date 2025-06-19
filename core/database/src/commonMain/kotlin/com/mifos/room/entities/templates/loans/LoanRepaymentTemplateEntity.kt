/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.templates.loans

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.savings.SavingAccountCurrencyEntity
import com.mifos.room.utils.ColumnInfo
import com.mifos.room.utils.Entity
import com.mifos.room.utils.INHERIT_FIELD_NAME
import com.mifos.room.utils.PrimaryKey
import com.mifos.room.utils.UNDEFINED
import com.mifos.room.utils.UNSPECIFIED
import com.mifos.room.utils.VALUE_UNSPECIFIED
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
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
    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val loanId: Int = 0,

    val type: LoanType? = null,

    val date: List<Int>? = null,

    val currency: SavingAccountCurrencyEntity? = null,

    val amount: Double? = null,

    val principalPortion: Double? = null,

    val interestPortion: Double? = null,

    val feeChargesPortion: Double? = null,

    val penaltyChargesPortion: Double? = null,

    val paymentTypeOptions: List<PaymentTypeOptionEntity>? = null,
) : Parcelable
