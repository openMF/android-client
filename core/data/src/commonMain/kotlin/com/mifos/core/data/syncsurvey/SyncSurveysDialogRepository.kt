/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.syncsurvey

import com.mifos.room.survey.entity.QuestionDatasEntity
import com.mifos.room.survey.entity.ResponseDatasEntity
import com.mifos.room.survey.entity.SurveyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Per-feature sync-surveys repository (Phase C Wave 10 of store5-adoption).
 *
 * Drives the Sync Surveys dialog inside the Settings feature. The dialog walks
 * a 3-tier graph (survey → question → response) and writes each tier to local
 * persistence via the underlying data manager. There is no remote-list fetch
 * step here — the surveys themselves come from the Survey List screen which
 * the user has already pulled.
 *
 * The contract intentionally keeps the `Flow<...>` shape from the legacy
 * feature-local interface: question + response saves stream their per-tier
 * progress back to the dialog VM. Survey-tier writes are pure suspend.
 *
 * Located in `core/data/syncsurvey/` per the Wave-7/8/9 convention
 * (`core/data/note/`, `core/data/document/`, `core/data/pathtracking/`).
 */
interface SyncSurveysDialogRepository {

    /** Persist [survey] to the local database. Throws on transport error. */
    suspend fun syncSurveyInDatabase(survey: SurveyEntity)

    /**
     * Persist [questionDatas] for [surveyId]. Emits the saved entity once on
     * success.
     */
    fun syncQuestionDataInDatabase(
        surveyId: Int,
        questionDatas: QuestionDatasEntity,
    ): Flow<QuestionDatasEntity>

    /**
     * Persist [responseDatas] for [questionId]. Emits the saved entity once on
     * success.
     */
    fun syncResponseDataInDatabase(
        questionId: Int,
        responseDatas: ResponseDatasEntity,
    ): Flow<ResponseDatasEntity>

    /** All locally-known surveys, streamed from Room. */
    fun allSurvey(): Flow<List<SurveyEntity>>
}
