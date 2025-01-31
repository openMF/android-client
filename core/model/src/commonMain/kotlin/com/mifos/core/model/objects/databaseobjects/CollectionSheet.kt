/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.databaseobjects

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CollectionSheet(
    var dueDate: IntArray,
    var groups: List<MifosGroup>,
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}
