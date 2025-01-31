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

import com.mifos.core.common.utils.Parcelable
import com.mifos.core.common.utils.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class MifosGroup(
    var staffId: Int = 0,

    var staffName: String? = null,

    var levelId: Int = 0,

    var levelName: String? = null,

    var groupId: Long = 0,

    var groupName: String? = null,

    var centerId: Long = 0,

    var clients: List<Client> = ArrayList(),
) : Parcelable
