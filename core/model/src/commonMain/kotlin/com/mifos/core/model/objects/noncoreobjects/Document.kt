/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.noncoreobjects

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import kotlinx.serialization.Serializable

/**
 * Created by ishankhanna on 02/07/14.
 */
@Parcelize
@Serializable
data class Document(
    var id: Int = 0,

    var parentEntityType: String? = null,

    var parentEntityId: Int = 0,

    var name: String? = null,

    var fileName: String? = null,

    var size: Long = 0,

    var type: String? = null,

    var description: String? = null,
) : Parcelable
