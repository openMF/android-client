/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.response

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.mifos.core.objects.Changes

class SaveResponse {
    @SerializedName("groupId")
    var groupId: Int? = null

    @SerializedName("resourceId")
    var resourceId: Int? = null

    @SerializedName("officeId")
    var officeId: Int? = null
    var changes: Changes? = null
    override fun toString(): String {
        return Gson().toJson(this)
    }
}