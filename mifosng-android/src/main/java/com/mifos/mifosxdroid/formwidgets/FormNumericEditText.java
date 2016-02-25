/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.formwidgets;

import android.content.Context;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ishankhanna on 01/08/14.
 */
public class FormNumericEditText extends FormWidget {

    protected TextView label;
    protected EditText input;
    protected int priority;

    public FormNumericEditText(Context context, String property )
    {
        super( context, property );

        label = new TextView( context );
        label.setText( getDisplayText() );

        input = new EditText( context );
        input.setInputType( InputType.TYPE_CLASS_PHONE );
        input.setImeOptions( EditorInfo.IME_ACTION_DONE );
        input.setLayoutParams( FormWidget.defaultLayoutParams );

        layout.addView( label );
        layout.addView( input );
    }

    public String getValue() {
        return input.getText().toString();
    }

    public void setValue(String value) {
        input.setText( value );
    }

    @Override
    public void setHint( String value ){
        input.setHint( value );
    }
}
