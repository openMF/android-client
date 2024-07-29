/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.noncore

import android.os.Parcelable
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelContainer
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/**
 * Created by Tarun on 1/28/2017.
 */
@Parcelize
@Table(database = MifosDatabase::class)
@ModelContainer
data class DataTablePayload(
    @PrimaryKey(autoincrement = true)
    @Transient
    var id: Int? = null,

    @Column
    @Transient
    var clientCreationTime: Long? = null,

    // this field belongs to database table only for saving the
    // data table string;
    @Column
    @Transient
    var dataTableString: String? = null,

    @Column
    var registeredTableName: String? = null,

    var data: HashMap<String, @RawValue Any>? = null,
) : MifosBaseModel(), Parcelable
