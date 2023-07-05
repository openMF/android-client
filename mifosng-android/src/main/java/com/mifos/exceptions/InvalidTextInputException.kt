/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.exceptions

import android.content.Context
import android.widget.Toast

class InvalidTextInputException(
    private val fieldInput: String,
    private val localisedErrorMessage: String,
    private val inputType: String
) : Exception() {
    override fun toString(): String {
        return "$fieldInput $localisedErrorMessage $inputType"
    }

    fun notifyUserWithToast(context: Context?) {
        Toast.makeText(context, toString(), Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TYPE_ALPHABETS = "Alphabets"
    }
}