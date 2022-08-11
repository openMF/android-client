/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.uihelpers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;

import com.mifos.mifosxdroid.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ishankhanna on 30/06/14.
 */
public class MFDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = "MFDatePicker";
    static String dateSet;
    static Calendar calendar;

    static {
        calendar = Calendar.getInstance();
        dateSet = DateFormat.format("dd-MM-yyyy", calendar.getTime()).toString();
    }

    OnDatePickListener onDatePickListener;

    public MFDatePicker() {

    }

    public static MFDatePicker newInsance(Fragment fragment) {
        MFDatePicker mfDatePicker = new MFDatePicker();
        mfDatePicker.onDatePickListener = (OnDatePickListener) fragment;
        return mfDatePicker;
    }

    public static String getDatePickedAsString() {
        return dateSet;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                R.style.Mifos_DesignSystem_Components_Dialog,
                this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMaxDate(new Date().getTime());
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date date = calendar.getTime();
        onDatePickListener.onDatePicked(DateFormat.format("dd-MM-yyyy", date).toString());
    }

    public void setOnDatePickListener(OnDatePickListener onDatePickListener) {
        this.onDatePickListener = onDatePickListener;
    }

    public interface OnDatePickListener {
        void onDatePicked(String date);
    }
}
