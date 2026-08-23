/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.loans

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Flattened Room row for a single loan-ledger transaction.
 *
 * The Fineract network payload nests transaction fields inside
 * `LoanWithAssociationsDto.transactions[]` (`LoanTransactionDto`) with sub-objects
 * (`type: LoanTransactionTypeDto`, `currency: LoanCurrencyDto`, `date: List<Int>`).
 * Following this fork's house style (see `LoanAccountSummaryEntity` / the savings
 * `*Entity` family) the nested shape is flattened into simple scalar columns so the
 * table is Room-friendly and directly queryable by `loanId`.
 *
 * `id` is the globally-unique Fineract transaction id and is used as the primary key
 * (Fineract transaction ids are unique per loan already, so a `(loanId, id)` composite
 * PK is unnecessary — the single `id` uniquely identifies a row). `loanId` is a plain
 * indexed column carrying the scoping foreign relationship, so the ledger can be read
 * back with `WHERE loanId = :loanId` and appended incrementally via a MAX(id) watermark.
 */
@Entity(
    tableName = "loan_transactions",
    indices = [Index(value = ["loanId"])],
)
@Serializable
data class LoanTransactionEntity(
    @PrimaryKey
    val id: Long,

    val loanId: Int,

    val officeName: String? = null,

    val typeValue: String? = null,

    val amount: Double,

    val dateEpochDay: Long? = null,

    val currencyCode: String? = null,

    val manuallyReversed: Boolean = false,
)
