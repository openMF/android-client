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
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 18/09/16.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class GroupDate(
    @PrimaryKey
    var groupId: Long = 0,

    @PrimaryKey
    var chargeId: Long = 0,

    @Column
    var day: Int = 0,

    @Column
    var month: Int = 0,

    @Column
    var year: Int = 0,
) : MifosBaseModel(), Parcelable
