/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.survey

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.database.MifosDatabase
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/**
 * Created by Nasim Banu on 27,January,2016.
 */
@Parcelize
@Entity("Survey")
data class Survey(
    @PrimaryKey
    var id: Int = 0,

    @ColumnInfo("key")
    var key: String? = null,

    @ColumnInfo("name")
    var name: String? = null,

    @ColumnInfo("description")
    var description: String? = null,

    @ColumnInfo("isSync")
    @Transient
    var isSync: Boolean = false,

    @ColumnInfo("countryCode")
    var countryCode: String? = null,

    var questionDatas: List<QuestionDatas> = ArrayList(),

    var componentDatas: List<ComponentDatas> = ArrayList(),
) : MifosBaseModel(), Parcelable
