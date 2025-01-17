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
import com.mifos.core.entity.survey.ResponseDatas
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "QuestionDatas")
data class QuestionDatas(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "surveyId")
    @Transient
    var surveyId: Int = 0,

    @ColumnInfo(name = "componentKey")
    var componentKey: String? = null,

    @ColumnInfo(name = "key")
    var key: String? = null,

    @ColumnInfo(name = "text")
    var text: String? = null,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "sequenceNo")
    var sequenceNo: Int = 0,

    @ColumnInfo(name = "responseDatas")
    var responseDatas: List<ResponseDatas> = ArrayList(),
) : Parcelable {

    var questionId: Int
        get() = id
        set(id) {
            this.id = id
        }
}
