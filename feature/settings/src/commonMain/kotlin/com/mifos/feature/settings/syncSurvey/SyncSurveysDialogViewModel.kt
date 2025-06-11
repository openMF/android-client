/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.syncSurvey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.room.entities.survey.QuestionDatasEntity
import com.mifos.room.entities.survey.ResponseDatasEntity
import com.mifos.room.entities.survey.SurveyEntity
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SyncSurveysDialogViewModel(
    private val repository: SyncSurveysDialogRepository,
    networkMonitor: NetworkMonitor,
) :
    ViewModel() {

    private val _syncSurveysDialogUiState =
        MutableStateFlow<SyncSurveysDialogUiState>(SyncSurveysDialogUiState.Initial)

    val syncSurveysDialogUiState: StateFlow<SyncSurveysDialogUiState>
        get() = _syncSurveysDialogUiState

    private var mSurveyList: List<SurveyEntity> = ArrayList()

    private val mFailedSyncSurvey: MutableList<SurveyEntity> = ArrayList()
    private var mQuestionDatasList: List<QuestionDatasEntity> = ArrayList()
    private var mResponseDatasList: List<ResponseDatasEntity> = ArrayList()
    private var mSurveySyncIndex = 0
    private var mQuestionDataSyncIndex = 0
    private var mResponseDataSyncIndex = 0
    private var maxSingleSyncSurveyProgressBar = 0

    val isNetworkAvailable: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /**
     * This Method Start Syncing Surveys. Start Syncing the Survey Accounts.
     *
     *
     */
    private fun startSyncingSurveys() {
        checkNetworkConnectionAndSyncSurvey()
    }

    /**
     * This Method checking network connection before starting survey synchronization
     */
    private fun checkNetworkConnectionAndSyncSurvey() {
        if (isNetworkAvailable.value) {
            syncSurveyAndUpdateUI()
        } else {
            _syncSurveysDialogUiState.value = SyncSurveysDialogUiState.ShowNetworkIsNotAvailable
            _syncSurveysDialogUiState.value = SyncSurveysDialogUiState.DismissDialog
        }
    }

    /**
     * This Method checking network connection before starting questiondata synchronization
     */
    private fun checkNetworkConnectionAndSyncQuestionData() {
        if (isNetworkAvailable.value) {
            syncQuestionDataAndUpdateUI()
        } else {
            _syncSurveysDialogUiState.value = SyncSurveysDialogUiState.ShowNetworkIsNotAvailable
            _syncSurveysDialogUiState.value = SyncSurveysDialogUiState.DismissDialog
        }
    }

    /**
     * This Method checking network connection before starting responsedata synchronization
     */
    private fun checkNetworkConnectionAndSyncResponseData() {
        if (isNetworkAvailable.value) {
            syncResponseDataAndUpdateUI()
        } else {
            _syncSurveysDialogUiState.value = SyncSurveysDialogUiState.ShowNetworkIsNotAvailable
            _syncSurveysDialogUiState.value = SyncSurveysDialogUiState.DismissDialog
        }
    }

    /**
     * This Method checking that mSurveySyncIndex and mSurveyList Size are equal or not. If they
     * are equal, It means all surveys have been synced otherwise continue syncing surveys.
     */
    private fun syncSurveyAndUpdateUI() {
        if (mSurveySyncIndex != mSurveyList.size) {
            syncSurvey(mSurveyList[mSurveySyncIndex])
        } else {
            _syncSurveysDialogUiState.value = SyncSurveysDialogUiState.ShowSurveysSyncSuccessfully
        }
    }

    /**
     * This Method checking that mQuestionDataSyncIndex and mQuestionDatasList Size
     * are equal or not. If they are equal, It means all questiondata have been
     * synced otherwise continue syncing questiondata.
     */
    private fun syncQuestionDataAndUpdateUI() {
        if (mQuestionDataSyncIndex != mQuestionDatasList.size) {
            syncQuestionData(
                mSurveyList[mSurveySyncIndex].id,
                mQuestionDatasList[mQuestionDataSyncIndex],
            )
        } else {
            _syncSurveysDialogUiState.value =
                mSurveyList[mSurveySyncIndex].name?.let {
                    SyncSurveysDialogUiState.UpdateSingleSyncSurvey(
                        mSurveySyncIndex, it, mQuestionDatasList.size,
                    )
                }!!
            mSurveySyncIndex += 1
            maxSingleSyncSurveyProgressBar = mQuestionDatasList.size
            mQuestionDataSyncIndex = 0
            checkNetworkConnectionAndSyncSurvey()
        }
    }

    /**
     * This Method checking that mResponseDataSyncIndex and mResponseDatasList Size
     * are equal or not. If they are equal, It means all responsedata have been
     * synced otherwise continue syncing responsedata.
     */
    private fun syncResponseDataAndUpdateUI() {
        if (mResponseDataSyncIndex != mResponseDatasList.size) {
            syncResponseData(
                mQuestionDatasList[mQuestionDataSyncIndex].id,
                mResponseDatasList[mResponseDataSyncIndex],
            )
        } else {
            _syncSurveysDialogUiState.value =
                SyncSurveysDialogUiState.UpdateQuestionSync(
                    mQuestionDataSyncIndex,
                    mQuestionDatasList[mQuestionDataSyncIndex].questionId,
                    mResponseDatasList.size,
                )
            mQuestionDataSyncIndex += 1
            mResponseDataSyncIndex = 0
            checkNetworkConnectionAndSyncQuestionData()
        }
    }

    /**
     * This Method will be called when ever any request will be failed synced.
     *
     * @param e Throwable
     */
    private fun onAccountSyncFailed(e: Throwable) {
        try {
            if (e is ClientRequestException || e is ServerResponseException) {
                maxSingleSyncSurveyProgressBar
                mFailedSyncSurvey.add(mSurveyList[mSurveySyncIndex])
                mSurveySyncIndex += 1
                _syncSurveysDialogUiState.value =
                    SyncSurveysDialogUiState.ShowSyncedFailedSurveys(mFailedSyncSurvey.size)
                checkNetworkConnectionAndSyncSurvey()
            }
        } catch (throwable: Throwable) {
            Logger.e("onAccountSyncFailed", throwable)
//            val errorObservable = Observable.error<Throwable>(RuntimeException("Custom error"))
//            errorObservable.subscribe { println("Error: ${throwable.message}") }
        }
    }

    /**
     * This Method Saving the Surveys to Database.
     *
     * @param survey
     */
    private fun syncSurvey(survey: SurveyEntity) {
        val updatedSurvey = survey.copy(isSync = true)
        viewModelScope.launch {
            try {
                _syncSurveysDialogUiState.value =
                    SyncSurveysDialogUiState.ShowUI(mSurveyList.size)

                repository.syncSurveyInDatabase(updatedSurvey)

                // onnext
                mQuestionDatasList = updatedSurvey.questionDatas
                checkSurveySyncStatus()

                // oncompleted
                _syncSurveysDialogUiState.value =
                    mSurveyList[mSurveySyncIndex].name?.let {
                        SyncSurveysDialogUiState.UpdateSingleSyncSurvey(
                            mSurveySyncIndex + 1, it, mQuestionDatasList.size,
                        )
                    }!!
            } catch (e: Exception) {
                _syncSurveysDialogUiState.value =
                    SyncSurveysDialogUiState.ShowError(e.message.toString())
                onAccountSyncFailed(e)
            }
        }
    }

    /**
     * This Method Saving the QuestionDatas to Database.
     *
     * @param surveyId int, questionDatas QuestionDatas
     */
    private fun syncQuestionData(surveyId: Int, questionDatas: QuestionDatasEntity) {
        viewModelScope.launch {
            repository.syncQuestionDataInDatabase(surveyId, questionDatas)
                .catch { e ->
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.ShowError(e.message.toString())
                    onAccountSyncFailed(e)
                }.collect { questionDatas ->
                    mResponseDatasList = questionDatas.responseDatas
                    checkQuestionDataSyncStatusAndSync()

                    // oncompleted
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.UpdateQuestionSync(
                            mQuestionDataSyncIndex + 1,
                            mQuestionDatasList[mQuestionDataSyncIndex].questionId,
                            mResponseDatasList.size,
                        )
                }
        }
    }

    /**
     * This Method Saving the ResponseDatas to Database.
     *
     * @param questionId int, responseDatas ResponseDatas
     */
    private fun syncResponseData(questionId: Int, responseDatas: ResponseDatasEntity) {
        viewModelScope.launch {
            repository.syncResponseDataInDatabase(questionId, responseDatas)
                .catch { e ->
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.ShowError(e.message.toString())
                    onAccountSyncFailed(e)
                }
                .collect {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.UpdateResponseSync(
                            mResponseDataSyncIndex,
                            mResponseDatasList[mResponseDataSyncIndex].value,
                        )
                    mResponseDataSyncIndex += 1
                    checkNetworkConnectionAndSyncResponseData()

                    // oncompleted
                    SyncSurveysDialogUiState.UpdateResponseSync(
                        mResponseDataSyncIndex,
                        mResponseDatasList[mResponseDataSyncIndex].value,
                    )
                }
        }
    }

    private fun checkSurveySyncStatus() {
        if (mQuestionDatasList.isNotEmpty()) {
            checkNetworkConnectionAndSyncQuestionData()
        } else {
            checkNetworkConnectionAndSyncSurvey()
        }
    }

    private fun checkQuestionDataSyncStatusAndSync() {
        if (mResponseDatasList.isNotEmpty()) {
            checkNetworkConnectionAndSyncResponseData()
        } else {
            checkNetworkConnectionAndSyncQuestionData()
        }
    }

    fun loadSurveyList() {
        viewModelScope.launch {
            _syncSurveysDialogUiState.value = SyncSurveysDialogUiState.ShowProgressbar

            repository.allSurvey()
                .catch {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.ShowError(it.message.toString())
                }.collect { surveys ->
                    mSurveyList = surveys
                    // onCompleted
                    startSyncingSurveys()
                }
        }
    }
}
