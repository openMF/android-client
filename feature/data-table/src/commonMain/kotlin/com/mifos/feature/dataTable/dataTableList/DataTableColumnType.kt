/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTableList

/**
 * Column display-type constants used by the data-table form renderer.
 *
 * The values match `ResultsetColumnHeaderData.ColumnDisplayType` enum names
 * (uppercase strings produced by `GetDataTablesResponseMapper`).
 *
 * Replaces the legacy `BaseFormWidget.SCHEMA_KEY_*` constants which were
 * never ported to the KMP codebase.
 */
internal object DataTableColumnType {
    const val STRING = "STRING"
    const val TEXT = "TEXT"
    const val INTEGER = "INTEGER"
    const val FLOAT = "FLOAT"
    const val DECIMAL = "DECIMAL"
    const val DATE = "DATE"
    const val DATETIME = "DATETIME"
    const val BOOLEAN = "BOOLEAN"
    const val CODELOOKUP = "CODELOOKUP"
    const val CODEVALUE = "CODEVALUE"
}
