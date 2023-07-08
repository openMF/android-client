/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.formwidgets

import android.R
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView

/**
 * Created by ishankhanna on 01/08/14.
 */
class FormSpinner(
    context: Context?,
    name: String?,
    columnValues: List<String>,
    columnValueIds: List<Int>
) : FormWidget(context, name) {
    private val label: TextView
    private val spinner: Spinner
    private val spinnerValueIdMap: MutableMap<String, Int>

    init {
        label = TextView(context)
        label.text = displayText
        spinner = Spinner(context)
        spinner.adapter =
            ArrayAdapter(context!!, R.layout.simple_spinner_item, columnValues)
        layout.addView(label)
        layout.addView(spinner)
        spinnerValueIdMap = HashMap()
        for (i in columnValues.indices) {
            spinnerValueIdMap[columnValues[i]] = columnValueIds[i]
        }
    }

    override var value: String
        get() = spinner.selectedItem.toString()
        set(value) {}

    fun getIdOfSelectedItem(key: String): Int {
        return spinnerValueIdMap[key]!!
    }
}