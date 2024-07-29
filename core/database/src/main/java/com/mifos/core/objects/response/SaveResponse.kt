/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
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
