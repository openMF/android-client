/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.dataTable.dataTableList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.icon.MifosIcons

/**
 * Created by ishankhanna on 01/08/14.
 */

class FormSpinnerCompose(
    propertyName: String,
    private val columnValues: List<String>,
    private val columnValueIds: List<Int>,
    selectedInitialValue: String = columnValues.firstOrNull() ?: "",
    visible: Boolean = true,
    priority: Int = 0,
) : BaseFormWidget(
    propertyName,
    returnType = SCHEMA_KEY_CODEVALUE,
    value = selectedInitialValue,
    visible = visible,
    priority = priority,
) {

    val spinnerValueIdMap: Map<String, Int> = columnValues.zip(columnValueIds).toMap()

    @Composable
    override fun Render() {
        if (!visible) return

        var expanded by rememberSaveable { mutableStateOf(false) }
        var selectedValue by rememberSaveable { mutableStateOf(value) }

        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(text = displayText, style = MaterialTheme.typography.labelLarge)

            Box {
                OutlinedTextField(
                    value = selectedValue,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true },
                    trailingIcon = {
                        Icon(
                            imageVector = MifosIcons.ArrowDropDown,
                            contentDescription = "Dropdown",
                        )
                    },
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    columnValues.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selectedValue = item
                                value = item
                                expanded = false
                            },
                        )
                    }
                }
            }
        }
    }

    fun getIdOfSelectedItem(): Int? = spinnerValueIdMap[value]
}

// class FormSpinner(
//    context: Context?,
//    name: String?,
//    columnValues: List<String>,
//    columnValueIds: List<Int>,
// ) : FormWidget(context, name) {
//    private val label: TextView
//    private val spinner: Spinner
//    val spinnerValueIdMap: MutableMap<String, Int>
//
//    init {
//        label = TextView(context)
//        label.text = displayText
//        spinner = Spinner(context)
//        spinner.adapter =
//            ArrayAdapter(context!!, R.layout.simple_spinner_item, columnValues)
//        layout.addView(label)
//        layout.addView(spinner)
//        spinnerValueIdMap = HashMap()
//        for (i in columnValues.indices) {
//            spinnerValueIdMap[columnValues[i]] = columnValueIds[i]
//        }
//    }
//
//    override var value: String
//        get() = spinner.selectedItem.toString()
//        set(value) {}
//
//    fun getIdOfSelectedItem(key: String): Int {
//        return spinnerValueIdMap[key]!!
//    }
// }
