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

import com.mifos.core.model.utils.Parcelable
import com.mifos.core.model.utils.Parcelize
import com.mifos.room.utils.Entity
import com.mifos.room.utils.PrimaryKey
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Entity(
    indices = [],
    inheritSuperIndices = false,
    primaryKeys = [],
    foreignKeys = [],
    ignoredColumns = [],
    tableName = "QuestionDatas",
)
data class QuestionDatasEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    val surveyId: Int = 0,

    val componentKey: String? = null,

    val key: String? = null,

    val text: String? = null,

    val description: String? = null,

    val sequenceNo: Int = 0,

    val responseDatas: List<ResponseDatasEntity> = emptyList(),
) : Parcelable {

    var questionId: Int
        get() = id
        set(id) {
            this.id = id
        }
}
