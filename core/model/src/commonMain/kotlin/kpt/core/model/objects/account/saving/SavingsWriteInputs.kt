/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.model.objects.account.saving

/**
 * Typed inputs for the three savings writes an officer performs in the field.
 *
 * ## Why these exist
 *
 * Each replaces something that could not express a correct request:
 *
 *  * **Activation** took a `HashMap<String, String>` — the officer's date, locale and format
 *    assembled by string key at the call site, with nothing to check that all three were present
 *    or spelled right.
 *  * **Approval** took `SavingsApproval?`, whose `locale` and `dateFormat` were DEFAULTED, so a
 *    caller could set a date without ever deciding how it would be parsed.
 *  * **A transaction** took `SavingsAccountTransactionRequestEntity?` — a nullable ROOM ENTITY,
 *    with every field nullable and an `errorMessage` column, used as an outbound request body.
 *
 * ## [locale] and [dateFormat] are REQUIRED on all three
 *
 * This is the structural fix for a defect that has now recurred **seven** times across this
 * migration, and the savings writes are its worst variant: the original sent
 * `approvalDate.toString()` where `approvalDate` was **epoch milliseconds**, so the payload carried
 * `"1761000000000"` as `approvedOnDate` while declaring `dateFormat = "dd MMMM yyyy"`. The screen
 * displayed the date correctly via `DateHelper.getDateAsStringFromLong`, so the bug was invisible
 * on screen — only the request was wrong. The activation screen did the same thing.
 *
 * Fineract parses with the format you declare, so the date string and the format are ONE decision.
 * Making both required means a caller cannot make half of it. Build them with
 * [kpt.core.model.utils.ApiDateFormatter].
 */
data class SavingsApprovalInput(
    val approvedOnDate: String,
    val locale: String,
    val dateFormat: String,
    val note: String? = null,
)

/** Activate an approved savings account. See [SavingsApprovalInput] for the date-pairing rule. */
data class SavingsActivationInput(
    val activatedOnDate: String,
    val locale: String,
    val dateFormat: String,
)

/**
 * A deposit or a withdrawal.
 *
 * [transactionAmount] is a String because Fineract's savings transaction endpoint takes it as one
 * (unlike the loan endpoints, which take a number) — the caller validates and formats it, and
 * keeping the wire type here stops a Double round-trip from changing the value the officer typed.
 */
data class SavingsTransactionInput(
    val transactionDate: String,
    val transactionAmount: String,
    val locale: String,
    val dateFormat: String,
    /** Null when the tenant configures no payment types, never `0` — `0` names no row. */
    val paymentTypeId: Int? = null,
    val note: String? = null,
    val accountNumber: String? = null,
    val checkNumber: String? = null,
    val routingCode: String? = null,
    val receiptNumber: String? = null,
    val bankNumber: String? = null,
)
