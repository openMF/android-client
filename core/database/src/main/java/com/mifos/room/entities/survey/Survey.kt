/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.survey

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "Survey")
data class Survey(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "key")
    var key: String? = null,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "isSync")
    @Transient
    var isSync: Boolean = false,

    @ColumnInfo(name = "countryCode")
    var countryCode: String? = null,

    @ColumnInfo(name = "questionDatas")
    var questionDatas: List<QuestionDatas> = ArrayList(),

    @ColumnInfo(name = "componentDatas")
    var componentDatas: List<ComponentDatas> = ArrayList(),
) : Parcelable
