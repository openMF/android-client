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
import com.mifos.room.entities.Timeline
import com.mifos.room.entities.client.Client
import com.mifos.room.entities.client.Status
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 29/06/14.
 */
@Parcelize
data class GroupWithAssociations(
    val id: Int? = null,

    val accountNo: String? = null,

    val name: String? = null,

    val status: Status? = null,

    val active: Boolean? = null,

    val activationDate: List<Int?> = emptyList(),

    val officeId: Int? = null,

    val officeName: String? = null,

    val staffId: Int? = null,

    val staffName: String? = null,

    val hierarchy: String? = null,

    val groupLevel: Int? = null,

    val clientMembers: List<Client> = emptyList(),

    val timeline: Timeline? = null,
) : Parcelable
