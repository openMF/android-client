/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.formwidgets

import android.content.Context
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.mifos.mifosxdroid.uihelpers.MFDatePicker
import com.mifos.mifosxdroid.uihelpers.MFDatePicker.OnDatePickListener

/**
 * Created by ishankhanna on 01/08/14.
 */
class FormEditText(context: Context?, name: String?) : FormWidget(context, name) {
    private var label: TextView
    private var input: EditText
    var isDateField: Boolean

    init {
        label = TextView(context)
        label.text = displayText
        label.layoutParams = defaultLayoutParams
        input = EditText(context)
        input.layoutParams = defaultLayoutParams
        input.imeOptions = EditorInfo.IME_ACTION_DONE
        isDateField = false
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

    fun setIsDateField(isDateField: Boolean, fragmentManager: FragmentManager?) {
        this.isDateField = isDateField
        if (this.isDateField) {
            input.setOnTouchListener { v, event ->
                if (MotionEvent.ACTION_UP == event.action) {
                    val mfDatePicker = MFDatePicker()
                    mfDatePicker.onDatePickListener = object : OnDatePickListener {
                        override fun onDatePicked(date: String?) {
                            if (date != null) {
                                value = date
                            }
                            mfDatePicker.dismiss()
                        }
                    }
                    if (fragmentManager != null) {
                        mfDatePicker.show(fragmentManager, MFDatePicker.TAG)
                    }
                }
                true
            }
        } else {
            throw RuntimeException(
                "This EditText must be a Date Field! Please check if " +
                        "you've set isDateAvailable = true or not"
            )
        }
    }
}