/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.formwidgets;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.mifos.mifosxdroid.uihelpers.MFDatePicker;

/**
 * Created by ishankhanna on 01/08/14.
 */
public class FormEditText extends FormWidget {

    protected TextView label;
    protected EditText input;

    private Boolean isDateAvailable;

    public FormEditText(Context context, String name) {

        super(context, name);
        label = new TextView(context);
        label.setText(getDisplayText());
        label.setLayoutParams(FormWidget.defaultLayoutParams);

        input = new EditText(context);
        input.setLayoutParams(FormWidget.defaultLayoutParams);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        isDateAvailable = false;
        layout.addView(label);
        layout.addView(input);


    }

    @Override
    public String getValue() {
        return input.getText().toString();
    }

    @Override
    public void setValue(String value) {
        input.setText(value);
    }

    @Override
    public void setHint(String value) {
        input.setHint(value);
    }

    public Boolean isDateField() {
        return isDateAvailable;
    }

    public void setIsDateField(Boolean isDateField, final FragmentManager fragmentManager) {

        this.isDateAvailable = isDateField;

        if (isDateField()) {

            input.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (MotionEvent.ACTION_UP == event.getAction()) {

                        final MFDatePicker mfDatePicker = new MFDatePicker();
                        mfDatePicker.setOnDatePickListener(new MFDatePicker.OnDatePickListener() {
                            @Override
                            public void onDatePicked(String date) {
                                setValue(date);
                                mfDatePicker.dismiss();
                            }
                        });
                        mfDatePicker.show(fragmentManager, MFDatePicker.TAG);
                    }

                    return true;
                }
            });


        } else {
            throw new RuntimeException("This EditText must be a Date Field! Please check if " +
                    "you've set isDateAvailable = true or not");
        }

    }


}
