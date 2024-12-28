/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.`object`

import android.os.Parcelable
import com.mifos.core.`object`.commonfiles.InterestType
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 14/02/14.
 */
@Parcelize
data class SearchedEntity(

    var entityId: Int = 0,

    var entityAccountNo: String? = null,

    var entityName: String? = null,

    var entityType: String? = null,

    var parentId: Int = 0,

    var parentName: String? = null,

    var entityStatus: InterestType? = null,

) : Parcelable {
    val description: String
        get() = "#$entityId - $entityName"
}
