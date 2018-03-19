/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.formwidgets;

import android.content.Context;
import android.support.annotation.IntDef;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.mifos.mifosxdroid.formwidgets.FormNumericEditText.FormInputTypes.TYPE_DECIMAL;
import static com.mifos.mifosxdroid.formwidgets.FormNumericEditText.FormInputTypes.TYPE_INTEGER;

/**
 * Created by ishankhanna on 01/08/14.
 */
public class FormNumericEditText extends FormWidget {

    protected TextView label;
    protected EditText input;
    protected int priority;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_INTEGER, TYPE_DECIMAL})
    public @interface FormInputTypes {
    int TYPE_INTEGER = 0;
    int TYPE_DECIMAL = 1;
    }

    public FormNumericEditText(Context context, String property, @FormInputTypes int inputType) {
        super(context, property);

        int formInputType = inputType == FormInputTypes.TYPE_INTEGER ?
                InputType.TYPE_CLASS_NUMBER : InputType.TYPE_CLASS_PHONE;

        label = new TextView(context);
        label.setText(getDisplayText());

        input = new EditText(context);
        input.setInputType(formInputType);
        input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input.setLayoutParams(FormWidget.defaultLayoutParams);

        layout.addView(label);
        layout.addView(input);
    }

    public String getValue() {
        return input.getText().toString();
    }

    public void setValue(String value) {
        input.setText(value);
    }

    @Override
    public void setHint(String value) {
        input.setHint(value);
    }
}
