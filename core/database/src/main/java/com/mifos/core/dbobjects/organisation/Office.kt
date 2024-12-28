/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.dbobjects.organisation

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ForeignKey
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 14/07/14.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class Office(
    @PrimaryKey
    var id: Int? = null,

    @Column
    var externalId: String? = null,

    @Column
    var name: String? = null,

    @Column
    var nameDecorated: String? = null,

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    var officeOpeningDate: OfficeOpeningDate? = null,

    var openingDate: List<Int?> = ArrayList(),
) : MifosBaseModel(), Parcelable
