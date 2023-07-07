/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.formwidgets

import android.content.Context
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView

/**
 * Created by ishankhanna on 01/08/14.
 */
class FormNumericEditText(context: Context?, property: String?) : FormWidget(context, property) {
    private var label: TextView
    private var input: EditText
//    private var priority = 0

    init {
        label = TextView(context)
        label.text = displayText
        input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_PHONE
        input.imeOptions = EditorInfo.IME_ACTION_NEXT
        input.layoutParams = defaultLayoutParams
        layout.addView(label)
        layout.addView(input)
    }

    override var value: String
        get() = input.text.toString()
        set(value) {
            input.setText(value)
        }

    override fun setHint(value: String?) {
        input.hint = value
    }
}