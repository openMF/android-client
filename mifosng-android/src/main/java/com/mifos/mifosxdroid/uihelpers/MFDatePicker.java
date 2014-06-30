package com.mifos.mifosxdroid.uihelpers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by ishankhanna on 30/06/14.
 */
public class MFDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    static String dateSet;
    static Calendar calendar;
    static {

        calendar = Calendar.getInstance();
        dateSet = calendar.get(Calendar.DAY_OF_MONTH)+"-"
                +(calendar.get(Calendar.MONTH)+1)+"-"
                +calendar.get(Calendar.YEAR);
    }

    OnDatePickListener mListener;

    public MFDatePicker(){

    }

    public static MFDatePicker newInsance(Fragment fragment) {
        MFDatePicker mfDatePicker = new MFDatePicker();
        mfDatePicker.mListener = (OnDatePickListener) fragment;
        return mfDatePicker;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new DatePickerDialog(getActivity(),
                this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        //TODO Fix Single digit problem that fails with the locale
        mListener.onDatePicked(day+"-"+(month+1)+"-"+year);

    }

    public static String getDatePickedAsString() {
        return dateSet;
    }

    public interface OnDatePickListener {
        public void onDatePicked(String date);
    }

}
