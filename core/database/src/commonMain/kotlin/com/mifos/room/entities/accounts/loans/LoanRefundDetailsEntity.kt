/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.loans

import kotlinx.serialization.Serializable
import template.core.base.database.CollationSequence.UNSPECIFIED
import template.core.base.database.ColumnInfo
import template.core.base.database.ColumnInfoTypeAffinity.INHERIT_FIELD_NAME
import template.core.base.database.ColumnInfoTypeAffinity.UNDEFINED
import template.core.base.database.ColumnInfoTypeAffinity.VALUE_UNSPECIFIED
import template.core.base.database.Entity
import template.core.base.database.PrimaryKey

@Serializable
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "LoanRefundDetails",
)
data class LoanRefundDetailsEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(index = true, name = INHERIT_FIELD_NAME, typeAffinity = UNDEFINED, collate = UNSPECIFIED, defaultValue = VALUE_UNSPECIFIED)
    val loanId: Int,
    val accountNo: String,
    val clientName: String?,
    val totalOverpaid: Double,
    val currencyCode: String?,
    val decimalPlaces: Int?,
    val defaultTransactionDate: String,
)

/**
 * Maps API loan entity to Room entity for offline caching.
 * Used in DataManagerLoan when fetching data online.
 */
fun LoanWithAssociationsEntity.toLoanRefundDetailsEntity(
    defaultTransactionDate: String,
): LoanRefundDetailsEntity = LoanRefundDetailsEntity(
    loanId = id,
    accountNo = accountNo,
    clientName = clientName,
    totalOverpaid = totalOverpaid,
    currencyCode = currency.code,
    decimalPlaces = currency.decimalPlaces,
    defaultTransactionDate = defaultTransactionDate,
)
