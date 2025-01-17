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
@Entity(tableName = "ResponseDatas")
data class ResponseDatas(
    @PrimaryKey
    @ColumnInfo(name = "responseId")
    var responseId: Int = 0,

    @ColumnInfo(name = "questionId")
    @Transient
    var questionId: Int = 0,

    @ColumnInfo(name = "text")
    var text: String? = null,

    @ColumnInfo(name = "sequenceNo")
    var sequenceNo: Int = 0,

    @ColumnInfo(name = "value")
    var value: Int = 0,
) : Parcelable
