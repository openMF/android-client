package com.mifos.feature.dataTable.dataTableList

import kotlinx.serialization.Serializable

const val SCHEMA_KEY_CODEVALUE =  "CODEVALUE"

data class SpinnerModel(
    val propertyName: String,
    val returnType: String = SCHEMA_KEY_CODEVALUE,
    val options: List<Pair<String, Int>>,
    var selectedLabel: String = ""
) {
    fun getSelectedId(): Int {
        return options.find { it.first == selectedLabel }?.second ?: 0
    }
}

data class FormWidgetModel(
    val propertyName: String,
    val returnType: String,
    var value: String = ""
)



