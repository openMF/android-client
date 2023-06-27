/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.uihelpers

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.mifos.mifosxdroid.R
import java.util.Calendar
import java.util.Date

/**
 * Created by ishankhanna on 30/06/14.
 */
class MFDatePicker : DialogFragment(), OnDateSetListener {
    var onDatePickListener: OnDatePickListener? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = DatePickerDialog(
            requireActivity(),
            R.style.MaterialDatePickerTheme,
            this, calendar!![Calendar.YEAR],
            calendar!![Calendar.MONTH],
            calendar!![Calendar.DAY_OF_MONTH]
        )
        dialog.datePicker.minDate = Date().time
        return dialog
    }

    override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar[year, month] = day
        val date = calendar.time
        onDatePickListener!!.onDatePicked(DateFormat.format("dd-MM-yyyy", date).toString())
    }

    fun setCustomOnDatePickListener(onDatePickListener: OnDatePickListener?) {
        this.onDatePickListener = onDatePickListener
    }

    interface OnDatePickListener {
        fun onDatePicked(date: String?)
    }

    companion object {
        const val TAG = "MFDatePicker"
        var datePickedAsString: String? = null
        var calendar: Calendar? = null

        init {
            calendar = Calendar.getInstance()
            datePickedAsString = DateFormat.format("dd-MM-yyyy", calendar?.time).toString()
        }

        @JvmStatic
        fun newInsance(fragment: Fragment?): MFDatePicker {
            val mfDatePicker = MFDatePicker()
            mfDatePicker.onDatePickListener = fragment as OnDatePickListener?
            return mfDatePicker
        }
    }
}