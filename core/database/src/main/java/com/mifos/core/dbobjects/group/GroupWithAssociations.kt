/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.dbobjects.group

import android.os.Parcelable
import com.mifos.core.dbobjects.Timeline
import com.mifos.core.dbobjects.client.Client
import com.mifos.core.dbobjects.client.Status
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 29/06/14.
 */
@Parcelize
data class GroupWithAssociations(
    var id: Int? = null,

    var accountNo: String? = null,

    var name: String? = null,

    var status: Status? = null,

    var active: Boolean? = null,

    var activationDate: List<Int?> = ArrayList(),

    var officeId: Int? = null,

    var officeName: String? = null,

    var staffId: Int? = null,

    var staffName: String? = null,

    var hierarchy: String? = null,

    var groupLevel: Int? = null,

    var clientMembers: List<Client> = ArrayList(),

    var timeline: Timeline? = null,
) : Parcelable
