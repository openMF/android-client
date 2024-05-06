/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.db

import com.google.gson.Gson

data class CollectionSheet(
    var dueDate: IntArray,
    var groups: List<MifosGroup>
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}