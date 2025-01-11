/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.entity.center

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by nellyk on 1/22/2016.
 */
@Parcelize
@Table(database = MifosDatabase::class, useBooleanGetterSetters = false)
@ModelContainer
data class CenterPayload(
    @PrimaryKey(autoincrement = true)
    @Transient
    var id: Int = 0,

    @Column
    @Transient
    var errorMessage: String? = null,

    @Column
    var dateFormat: String? = null,

    @Column
    var locale: String? = null,

    @Column
    var name: String? = null,

    @Column
    var officeId: Int? = null,

    @Column
    var active: Boolean = false,

    @Column
    var activationDate: String? = null,
) : MifosBaseModel(), Parcelable
