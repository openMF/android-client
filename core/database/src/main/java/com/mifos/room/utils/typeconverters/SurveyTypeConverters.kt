/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.utils.typeconverters

import androidx.room.TypeConverter
import com.mifos.room.entities.survey.ComponentDatas
import com.mifos.room.entities.survey.QuestionDatas
import com.mifos.room.entities.survey.ResponseDatas
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Created by Pronay Sarker on 13/02/2025 (2:53 AM)
 */
class SurveyTypeConverters {

    @TypeConverter
    fun fromQuestionDatasList(questionDatas: List<QuestionDatas>?): String? {
        return questionDatas?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toQuestionDatasList(json: String?): List<QuestionDatas>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromComponentDatasList(componentDatas: List<ComponentDatas>?): String? {
        return componentDatas?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toComponentDatasList(json: String?): List<ComponentDatas>? {
        return json?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromResponseDatasList(responseDatas: List<ResponseDatas>?): String? {
        return responseDatas?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toResponseDatasList(json: String?): List<ResponseDatas>? {
        return json?.let { Json.decodeFromString(it) }
    }
}
