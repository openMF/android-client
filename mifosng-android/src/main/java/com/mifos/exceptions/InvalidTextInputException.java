/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.exceptions;

import android.content.Context;
import android.widget.Toast;

public class InvalidTextInputException extends Exception {
    private String fieldInput;
    private String localisedErrorMessage;
    private String inputType;
    public static final String TYPE_ALPHABETS= "ALPHABETS";

    public InvalidTextInputException(String fieldInput,String localisedErrorMessage,String inputType){
        this.fieldInput=fieldInput;
        this.localisedErrorMessage=localisedErrorMessage;
        this.inputType=inputType;
    }

    @Override
    public String toString(){
        return fieldInput + "" + localisedErrorMessage + inputType;
    }


    public void notifyUserWithToast(Context context) {
        Toast.makeText(context, toString(), Toast.LENGTH_SHORT).show();
    }
}
