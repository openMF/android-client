/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.typeconverters

import com.mifos.room.entities.survey.ComponentDatasEntity
import com.mifos.room.entities.survey.QuestionDatasEntity
import com.mifos.room.entities.survey.ResponseDatasEntity
import com.mifos.room.utils.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Created by Pronay Sarker on 13/02/2025 (2:53 AM)
 */
class SurveyTypeConverters {

    @TypeConverter
    fun fromQuestionDatasList(questionDatas: List<QuestionDatasEntity>?): String? {
        return questionDatas?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toQuestionDatasList(json: String?): List<QuestionDatasEntity>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromComponentDatasList(componentDatas: List<ComponentDatasEntity>?): String? {
        return componentDatas?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toComponentDatasList(json: String?): List<ComponentDatasEntity>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromResponseDatasList(responseDatas: List<ResponseDatasEntity>?): String? {
        return responseDatas?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toResponseDatasList(json: String?): List<ResponseDatasEntity>? {
        return json?.let { Json.decodeFromString(it) }
    }
}
