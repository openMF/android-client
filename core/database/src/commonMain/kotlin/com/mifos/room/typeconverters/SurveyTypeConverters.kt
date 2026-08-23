/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.typeconverters

import com.mifos.room.entities.survey.ComponentDatasEntity
import com.mifos.room.entities.survey.QuestionDatasEntity
import com.mifos.room.entities.survey.ResponseDatasEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.room3.ColumnTypeConverter

/**
 * Created by Pronay Sarker on 13/02/2025 (2:53 AM)
 */
class SurveyTypeConverters {

    @ColumnTypeConverter
    fun fromQuestionDatasList(questionDatas: List<QuestionDatasEntity>?): String? {
        return questionDatas?.let { Json.encodeToString(it) }
    }

    @ColumnTypeConverter
    fun toQuestionDatasList(json: String?): List<QuestionDatasEntity>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @ColumnTypeConverter
    fun fromComponentDatasList(componentDatas: List<ComponentDatasEntity>?): String? {
        return componentDatas?.let { Json.encodeToString(it) }
    }

    @ColumnTypeConverter
    fun toComponentDatasList(json: String?): List<ComponentDatasEntity>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @ColumnTypeConverter
    fun fromResponseDatasList(responseDatas: List<ResponseDatasEntity>?): String? {
        return responseDatas?.let { Json.encodeToString(it) }
    }

    @ColumnTypeConverter
    fun toResponseDatasList(json: String?): List<ResponseDatasEntity>? {
        return json?.let { Json.decodeFromString(it) }
    }
}
