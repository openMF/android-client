/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.exceptions

import android.content.Context
import android.widget.Toast

/**
 * Created by ishankhanna on 04/07/14.
 */
class RequiredFieldException(
    private val fieldName: String,
    private val localisedErrorMessage: String
) : Exception() {
    override fun toString(): String {
        return "$fieldName $localisedErrorMessage"
    }

    fun notifyUserWithToast(context: Context?) {
        Toast.makeText(context, toString(), Toast.LENGTH_SHORT).show()
    }
}