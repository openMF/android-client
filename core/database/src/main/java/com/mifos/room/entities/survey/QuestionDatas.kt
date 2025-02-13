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
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "QuestionDatas")
data class QuestionDatas(
    @PrimaryKey
    var id: Int = 0,

    @Transient
    val surveyId: Int = 0,

    val componentKey: String? = null,

    val key: String? = null,

    val text: String? = null,

    val description: String? = null,

    val sequenceNo: Int = 0,

    val responseDatas: List<ResponseDatas> = emptyList(),
) : Parcelable {

    var questionId: Int
        get() = id
        set(id) {
            this.id = id
        }
}
