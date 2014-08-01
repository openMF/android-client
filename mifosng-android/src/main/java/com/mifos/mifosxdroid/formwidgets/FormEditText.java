/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.formwidgets;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ishankhanna on 01/08/14.
 */
public class FormEditText extends FormWidget{

    protected TextView label;
    protected EditText input;

    public FormEditText(Context context, String name) {

        super(context, name);

        label = new TextView( context );
        label.setText( getDisplayText() );
        label.setLayoutParams( FormWidget.defaultLayoutParams );

        input = new EditText( context );
        input.setLayoutParams( FormWidget.defaultLayoutParams );
        input.setImeOptions( EditorInfo.IME_ACTION_DONE );

        layout.addView( label );
        layout.addView( input );


    }

    @Override
    public String getValue(){
        return input.getText().toString();
    }

    @Override
    public void setValue( String value ) {
        input.setText( value );
    }

    @Override
    public void setHint( String value ){
        input.setHint( value );
    }
}
