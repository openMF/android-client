/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.formwidgets;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ishankhanna on 01/08/14.
 */
public class FormSpinner extends FormWidget {

    private TextView label;
    private Spinner spinner;

    public FormSpinner(Context context, String name, List<String> columnValues) {
        super(context, name);

        label = new TextView(context);
        label.setText(getDisplayText());

        spinner = new Spinner(context);
        spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, columnValues));

        layout.addView(label);
        layout.addView(spinner);

    }

    @Override
    public String getValue() {
        return spinner.getSelectedItem().toString()==null?"":spinner.getSelectedItem().toString();
    }

}
