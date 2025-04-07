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
    tableName = "Survey",
)
data class SurveyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val key: String? = null,

    val name: String? = null,

    val description: String? = null,

    val isSync: Boolean = false,

    val countryCode: String? = null,

    val questionDatas: List<QuestionDatasEntity> = emptyList(),

    val componentDatas: List<ComponentDatasEntity> = emptyList(),
) : Parcelable
