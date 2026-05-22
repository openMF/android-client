/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.newLoanAccount.pages

/**
 * Type-safe value for a single editable cell of a datatable form step.
 *
 * The inline stepper widgets store one of these per `(tableIndex, columnName)`.
 * `Any` was the previous shape — it lost the per-widget type, forced unchecked
 * `as?` casts on read, and made the storage/submit boundary error-prone.
 *
 * Mapping to Fineract column display types:
 *   - [Text] → STRING, TEXT, INTEGER, DECIMAL, FLOAT, DATE
 *             (all of these reach the UI as a typed/picked String; numeric
 *              parsing happens at submit time, not on every keystroke)
 *   - [Bool] → BOOLEAN
 *   - [Code] → CODELOOKUP, CODEVALUE
 *             (carries the picked code id, not the display string — Fineract
 *              expects ids in `datatables[].data` on POST)
 *
 * [asPayloadValue] is the one boundary where the type-safe form values cross
 * into the `Map<String, Any>` shape that `DataTablePayload.data` requires.
 */
sealed interface DatatableFieldValue {
    fun asPayloadValue(): Any

    data class Text(val text: String) : DatatableFieldValue {
        override fun asPayloadValue(): Any = text
    }

    data class Bool(val checked: Boolean) : DatatableFieldValue {
        override fun asPayloadValue(): Any = checked
    }

    data class Code(val id: Int) : DatatableFieldValue {
        override fun asPayloadValue(): Any = id
    }
}
