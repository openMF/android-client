/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.group

import android.os.Parcelable
import com.mifos.core.entity.client.Status
import com.mifos.core.model.objects.collectionsheets.CollectionMeetingCalendar
import com.mifos.core.objects.collectionsheets.CollectionMeetingCalendar
import com.mifos.room.entities.Timeline
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 28/06/14.
 */
@Parcelize
data class CenterWithAssociations(
    var id: Int? = null,

    var accountNo: String? = null,

    var name: String? = null,

    var externalId: String? = null,

    var officeId: Int? = null,

    var officeName: String? = null,

    var staffId: Int? = null,

    var staffName: String? = null,

    var hierarchy: String? = null,

    var status: Status? = null,

    var active: Boolean? = null,

    var activationDate: List<Int> = ArrayList(),

    var timeline: Timeline? = null,

    var groupMembers: List<Group> = ArrayList(),

    var collectionMeetingCalendar: CollectionMeetingCalendar = CollectionMeetingCalendar(),
) : Parcelable
