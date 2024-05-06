/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.model

import com.google.gson.Gson

data class Changes(
    val locale: String,
    val dateFormat: String
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}