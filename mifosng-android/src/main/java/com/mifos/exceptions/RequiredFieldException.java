/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.exceptions;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ishankhanna on 04/07/14.
 */
public class RequiredFieldException extends Exception {

    private String fieldName;
    private String localisedErrorMessage;


    public RequiredFieldException(String fieldName, String localisedErrorMessage) {

        this.fieldName = fieldName;
        this.localisedErrorMessage = localisedErrorMessage;
    }

    @Override
    public String toString() {
        return fieldName + " " + localisedErrorMessage ;
    }

    public void notifyUserWithToast(Context context) {
        Toast.makeText(context, toString(), Toast.LENGTH_SHORT).show();
    }
}
