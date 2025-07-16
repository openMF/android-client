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

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_datatable
import androidclient.feature.client.generated.resources.feature_client_failed_to_fetch_surveys_list
import androidclient.feature.client.generated.resources.feature_client_failed_to_load_db_question_data
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.SurveyListRepository
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.room.entities.survey.QuestionDatasEntity
import com.mifos.room.entities.survey.SurveyEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class SurveyListViewModel(
    private val repository: SurveyListRepository,
    private val prefManager: UserPreferencesRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = -1)

    private val _surveyListUiState =
        MutableStateFlow<SurveyListUiState>(SurveyListUiState.ShowProgressbar)
    val surveyListUiState: StateFlow<SurveyListUiState> get() = _surveyListUiState

    private var mDbSurveyList: List<SurveyEntity>? = null
    private lateinit var mSyncSurveyList: List<SurveyEntity>

    fun loadSurveyList() {
        viewModelScope.launch {
            repository.allSurvey().collect { result ->
                when (result) {
                    is DataState.Error -> {
                        _surveyListUiState.value =
                            SurveyListUiState.ShowFetchingError(Res.string.feature_client_failed_to_fetch_surveys_list)
                    }
                    DataState.Loading -> {
                        _surveyListUiState.value = SurveyListUiState.ShowProgressbar
                    }
                    is DataState.Success -> {
                        mSyncSurveyList = result.data
                        loadDatabaseSurveys()
                    }
                }
            }
        }
    }

    private fun loadDatabaseSurveys() {
        viewModelScope.launch {
            repository.databaseSurveys().collect { result ->
                when (result) {
                    is DataState.Error -> {
                        _surveyListUiState.value =
                            SurveyListUiState.ShowFetchingError(Res.string.feature_client_failed_to_fetch_datatable)
                    }
                    DataState.Loading -> {
                        _surveyListUiState.value = SurveyListUiState.ShowProgressbar
                    }
                    is DataState.Success -> {
                        mDbSurveyList = result.data
                        if (prefManager.userInfo.first().userStatus) {
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
        }
    }

    private fun loadDatabaseQuestionData(surveyId: Int, survey: SurveyEntity?) {
        viewModelScope.launch {
            repository.getDatabaseQuestionData(surveyId).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        _surveyListUiState.value =
                            SurveyListUiState.ShowFetchingError(Res.string.feature_client_failed_to_load_db_question_data)
                    }
                    DataState.Loading -> {
                        _surveyListUiState.value = SurveyListUiState.ShowProgressbar
                    }
                    is DataState.Success -> {
                        for (questionDatas in result.data) {
                            loadDatabaseResponseDatas(questionDatas.id, questionDatas)
                        }
                        val updatedSurvey = survey!!.copy(questionDatas = result.data)
                        mSyncSurveyList = mSyncSurveyList.map {
                            if (it.id == survey.id) updatedSurvey else it
                        }
                        _surveyListUiState.value = SurveyListUiState.ShowAllSurvey(mSyncSurveyList)
                    }
                }
            }
        }
    }

    private fun loadDatabaseResponseDatas(questionId: Int, questionDatas: QuestionDatasEntity) {
        viewModelScope.launch {
            repository.getDatabaseResponseDatas(questionId).collect { result ->
                when (result) {
                    is DataState.Error -> {
                        _surveyListUiState.value =
                            SurveyListUiState.ShowFetchingError(Res.string.feature_client_failed_to_load_db_question_data)
                    }
                    DataState.Loading -> {
                        _surveyListUiState.value = SurveyListUiState.ShowProgressbar
                    }
                    is DataState.Success -> {
                        val updatedQuestionDatas = questionDatas.copy(responseDatas = result.data)

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
        }
    }

    private fun setAlreadySurveySyncStatus(surveys: List<SurveyEntity>) {
        checkSurveyAlreadySyncedOrNot(surveys)
    }

    private fun checkSurveyAlreadySyncedOrNot(surveys: List<SurveyEntity>) {
        val localDbSurveyList = mDbSurveyList ?: return

        mSyncSurveyList = surveys.map { syncSurvey ->
            if (localDbSurveyList.any { it.id == syncSurvey.id }) {
                syncSurvey.copy(isSync = true)
            } else {
                syncSurvey
            }
        }
    }
}
