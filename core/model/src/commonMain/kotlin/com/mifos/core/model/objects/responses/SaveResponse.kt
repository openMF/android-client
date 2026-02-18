/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.responses

import com.mifos.core.model.objects.Changes
import com.mifos.core.model.utils.IgnoredOnParcel
import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
@Parcelize
class SaveResponse(
    var groupId: Int? = null,

    var resourceId: Int? = null,

    var officeId: Int? = null,

    @IgnoredOnParcel
    var changes: Changes? = null,
) : Parcelable {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}
