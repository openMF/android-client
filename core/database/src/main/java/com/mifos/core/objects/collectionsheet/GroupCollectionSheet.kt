/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 25-07-2017.
 */

@Parcelize
data class GroupCollectionSheet(
    var clients: MutableList<ClientCollectionSheet> = ArrayList(),

    var groupId: Int = 0,

    var groupName: String? = null,

    var levelId: Int = 0,

    var levelName: String? = null,

    var staffId: Int = 0,

    var staffName: String? = null,
) : Parcelable
