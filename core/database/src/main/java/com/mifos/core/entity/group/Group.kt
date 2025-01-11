/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.entity.group

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.entity.Timeline
import com.mifos.core.entity.client.Status
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * This is the Groups Model Table
 * Created by ishankhanna on 28/06/14.
 */
@Parcelize
@Table(database = MifosDatabase::class, useBooleanGetterSetters = false)
@ModelContainer
data class Group(
    @PrimaryKey
    var id: Int? = null,

    @Column
    var accountNo: String? = null,

    @Column
    @Transient
    var sync: Boolean = false,

    @Column
    var name: String? = null,

    var status: Status? = null,

    @Column
    var active: Boolean? = null,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    @Transient
    var groupDate: GroupDate? = null,

    var activationDate: List<Int> = ArrayList(),

    @Column
    var officeId: Int? = null,

    @Column
    var officeName: String? = null,

    @Column
    var centerId: Int? = 0,

    @Column
    var centerName: String? = null,

    @Column
    var staffId: Int? = null,

    @Column
    var staffName: String? = null,

    @Column
    var hierarchy: String? = null,

    @Column
    var groupLevel: Int = 0,

    var timeline: Timeline? = null,

    var externalId: String? = null,
) : MifosBaseModel(), Parcelable
