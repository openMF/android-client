/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.exceptions

import android.content.Context
import android.widget.Toast

class ShortOfLengthException(private val inputField: String, private val minimumCharacters: Int) :
    Exception() {
    override fun toString(): String {
        return (inputField + " Field Expects at least " + minimumCharacters
                + " characters")
    }

    fun notifyUserWithToast(context: Context?) {
        Toast.makeText(context, toString(), Toast.LENGTH_SHORT).show()
    }
}