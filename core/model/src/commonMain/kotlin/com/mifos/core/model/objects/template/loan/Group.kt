/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.template.loan

import com.mifos.core.common.utils.Parcelable
import com.mifos.core.common.utils.Parcelize

/**
 * Created by mayankjindal on 02/10/16.
 */
@Parcelize
data class Group(
    var id: Int? = null,

    var accountNo: Int? = null,

    var name: String? = null,

    var externalId: Int? = null,

    var status: Status? = null,

    var active: Boolean? = null,

    var activationDate: List<Int>? = null,

    var officeId: Int? = null,

    var officeName: String? = null,

    var hierarchy: String? = null,

    var groupLevel: Int? = null,

    var timeline: GroupTimeline? = null,
) : Parcelable
