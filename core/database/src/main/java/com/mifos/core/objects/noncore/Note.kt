/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.noncore

import android.os.Parcelable
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by rahul on 4/3/17.
 */
@Parcelize
data class Note(
    var id: Int? = null,

    var clientId: Int? = null,

    var noteContent: String? = null,

    var createdById: Int? = null,

    var createdByUsername: String? = null,

    var createdOn: Long = 0,

    var updatedById: Int? = null,

    var updatedByUsername: String? = null,

    var updatedOn: Long = 0,
) : MifosBaseModel(), Parcelable
