/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientSurveyList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.SurveyListRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.feature.client.R
import com.mifos.room.entities.survey.QuestionDatas
import com.mifos.room.entities.survey.Survey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class SurveyListViewModel @Inject constructor(
    private val repository: SurveyListRepository,
    private val prefManager: PrefManager,
) : ViewModel() {

    private val _surveyListUiState =
        MutableStateFlow<SurveyListUiState>(SurveyListUiState.ShowProgressbar)
    val surveyListUiState: StateFlow<SurveyListUiState> get() = _surveyListUiState

    private var mDbSurveyList: List<Survey>? = null
    private lateinit var mSyncSurveyList: List<Survey>

    fun loadSurveyList() {
        viewModelScope.launch {
            _surveyListUiState.value = SurveyListUiState.ShowProgressbar

            repository.allSurvey()
                .catch {
                    _surveyListUiState.value =
                        SurveyListUiState.ShowFetchingError(R.string.feature_client_failed_to_fetch_surveys_list)
                }
                .collect { surveys ->
                    mSyncSurveyList = surveys
                    loadDatabaseSurveys()
                }
        }
    }

    private fun loadDatabaseSurveys() {
        viewModelScope.launch {
            _surveyListUiState.value = SurveyListUiState.ShowProgressbar

            repository.databaseSurveys()
                .catch {
                    _surveyListUiState.value =
                        SurveyListUiState.ShowFetchingError(R.string.feature_client_failed_to_fetch_datatable)
                }
                .collect { surveyList ->
                    mDbSurveyList = surveyList
                    if (prefManager.userStatus) {
                        for (survey in mSyncSurveyList) {
                            loadDatabaseQuestionData(survey.id, survey)
                        }
                    }
                    // OnCompleted
                    setAlreadySurveySyncStatus(mSyncSurveyList)
                    _surveyListUiState.value = SurveyListUiState.ShowAllSurvey(mSyncSurveyList)
                }
        }
    }

    private fun loadDatabaseQuestionData(surveyId: Int, survey: Survey?) {
        viewModelScope.launch {
            _surveyListUiState.value = SurveyListUiState.ShowProgressbar

            repository.getDatabaseQuestionData(surveyId)
                .catch {
                    _surveyListUiState.value =
                        SurveyListUiState.ShowFetchingError(R.string.feature_client_failed_to_load_db_question_data)
                }.collect { questionDatasList ->
                    for (questionDatas in questionDatasList) {
                        loadDatabaseResponseDatas(questionDatas.id, questionDatas)
                    }
                    val updatedSurvey = survey!!.copy(questionDatas = questionDatasList)
                    mSyncSurveyList = mSyncSurveyList.map {
                        if (it.id == survey.id) updatedSurvey else it
                    }
                    _surveyListUiState.value = SurveyListUiState.ShowAllSurvey(mSyncSurveyList)
                }
        }
    }

    private fun loadDatabaseResponseDatas(questionId: Int, questionDatas: QuestionDatas) {
        viewModelScope.launch {
            _surveyListUiState.value = SurveyListUiState.ShowProgressbar

            repository.getDatabaseResponseDatas(questionId)
                .catch {
                    _surveyListUiState.value =
                        SurveyListUiState.ShowFetchingError(R.string.feature_client_failed_to_load_db_question_data)
                }
                .collect { responseDatas ->
                    val updatedQuestionDatas = questionDatas.copy(responseDatas = responseDatas)

                    mSyncSurveyList = mSyncSurveyList.map { survey ->
                        if (survey.id == questionDatas.surveyId) {
                            survey.copy(
                                questionDatas = survey.questionDatas.map {
                                    if (it.id == questionDatas.id) updatedQuestionDatas else it
                                },
                            )
                        } else {
                            survey
                        }
                    }
                    _surveyListUiState.value = SurveyListUiState.ShowAllSurvey(mSyncSurveyList)
                }
        }
    }

    private fun setAlreadySurveySyncStatus(surveys: List<Survey>) {
        checkSurveyAlreadySyncedOrNot(surveys)
    }

    private fun checkSurveyAlreadySyncedOrNot(surveys: List<Survey>) {
        if (mDbSurveyList.isNullOrEmpty()) return

        mSyncSurveyList = surveys.map { syncSurvey ->
            if (mDbSurveyList!!.any { it.id == syncSurvey.id }) {
                syncSurvey.copy(isSync = true)
            } else {
                syncSurvey
            }
        }
    }
}
