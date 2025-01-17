/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.client

import com.mifos.core.entity.client.Status
import com.mifos.room.entities.Timeline

/**
 * Created by ishankhanna on 09/02/14.
 */
data class PageItem(
    var id: Int = 0,

    var accountNo: String? = null,

    var status: Status? = null,

    var isActive: Boolean = false,

    var activationDate: List<Int> = ArrayList(),

    var firstname: String? = null,

    var middlename: String? = null,

    var lastname: String? = null,

    var displayName: String? = null,

    var officeId: Int = 0,

    var officeName: String? = null,

    var staffId: Int = 0,

    var staffName: String? = null,

    var timeline: Timeline? = null,

    var fullname: String? = null,

    var imageId: Int = 0,

    var isImagePresent: Boolean = false,

    var externalId: String? = null,
)
