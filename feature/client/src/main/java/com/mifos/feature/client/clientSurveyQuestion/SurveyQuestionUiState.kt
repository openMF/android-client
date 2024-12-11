/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientSurveyQuestion

import com.mifos.core.objects.survey.Survey

sealed class SurveyQuestionUiState {

    data object ShowProgressbar : SurveyQuestionUiState()

    data class ShowFetchingError(val message: Int) : SurveyQuestionUiState()

    data class ShowQuestions(val ques: Survey) : SurveyQuestionUiState()
}
