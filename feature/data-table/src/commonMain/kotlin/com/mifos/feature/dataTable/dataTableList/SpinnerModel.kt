/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTableList

const val SCHEMA_KEY_CODEVALUE = "CODEVALUE"

data class SpinnerModel(
    val propertyName: String,
    val returnType: String = SCHEMA_KEY_CODEVALUE,
    val options: List<Pair<String, Int>>,
    var selectedLabel: String = "",
) {
    fun getSelectedId(): Int {
        return options.find { it.first == selectedLabel }?.second ?: 0
    }
}

data class FormWidgetModel(
    val propertyName: String,
    val returnType: String,
    var value: String = "",
)
