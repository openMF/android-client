package com.mifos.feature.client.clientSurveyList

import androidx.lifecycle.ViewModel
import com.mifos.core.data.repository.SurveyListRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.objects.survey.QuestionDatas
import com.mifos.core.objects.survey.ResponseDatas
import com.mifos.core.objects.survey.Survey
import com.mifos.feature.client.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class SurveyListViewModel @Inject constructor(
    private val repository: SurveyListRepository,
    private val prefManager: PrefManager
) : ViewModel() {

    private val _surveyListUiState =
        MutableStateFlow<SurveyListUiState>(SurveyListUiState.ShowProgressbar)
    val surveyListUiState: StateFlow<SurveyListUiState> get() = _surveyListUiState


    private var mDbSurveyList: List<Survey>? = null
    private lateinit var mSyncSurveyList: List<Survey>


    fun loadSurveyList() {
        _surveyListUiState.value = SurveyListUiState.ShowProgressbar

        repository.allSurvey()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Survey>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _surveyListUiState.value =
                        SurveyListUiState.ShowFetchingError(R.string.feature_client_failed_to_fetch_surveys_list)
                }

                override fun onNext(surveys: List<Survey>) {
                    mSyncSurveyList = surveys
                    loadDatabaseSurveys()
                }
            })
    }

    fun loadDatabaseSurveys() {
        _surveyListUiState.value = SurveyListUiState.ShowProgressbar
        repository.databaseSurveys()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Survey>>() {
                override fun onCompleted() {
                    setAlreadySurveySyncStatus(mSyncSurveyList)
                    _surveyListUiState.value = SurveyListUiState.ShowAllSurvey(mSyncSurveyList)
                }

                override fun onError(e: Throwable) {
                    _surveyListUiState.value =
                        SurveyListUiState.ShowFetchingError(R.string.feature_client_failed_to_fetch_datatable)
                }

                override fun onNext(surveyList: List<Survey>) {
                    mDbSurveyList = surveyList
                    if (prefManager.userStatus) {
                        for (survey in mSyncSurveyList) {
                            loadDatabaseQuestionData(survey.id, survey)
                        }
                    }
                }
            })

    }

    fun loadDatabaseQuestionData(surveyId: Int, survey: Survey?) {
        _surveyListUiState.value = SurveyListUiState.ShowProgressbar
        repository.getDatabaseQuestionData(surveyId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<QuestionDatas>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _surveyListUiState.value =
                        SurveyListUiState.ShowFetchingError(R.string.feature_client_failed_to_load_db_question_data)
                }

                override fun onNext(questionDatasList: List<QuestionDatas>) {
                    for (questionDatas in questionDatasList) {
                        questionDatas.id.let { loadDatabaseResponseDatas(it, questionDatas) }
                    }
                    survey!!.questionDatas = questionDatasList
                }
            })

    }

    fun loadDatabaseResponseDatas(questionId: Int, questionDatas: QuestionDatas) {
        _surveyListUiState.value = SurveyListUiState.ShowProgressbar
        repository.getDatabaseResponseDatas(questionId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<ResponseDatas>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _surveyListUiState.value =
                        SurveyListUiState.ShowFetchingError(R.string.feature_client_failed_to_load_db_question_data)
                }

                override fun onNext(responseDatas: List<ResponseDatas>) {
                    questionDatas.responseDatas = responseDatas
                }
            })

    }

    fun setAlreadySurveySyncStatus(surveys: List<Survey>) {
        checkSurveyAlreadySyncedOrNot(surveys)
    }

    private fun checkSurveyAlreadySyncedOrNot(surveys: List<Survey>) {
        if (mDbSurveyList!!.isNotEmpty()) {
            for (dbSurvey in mDbSurveyList!!) {
                for (syncSurvey in surveys) {
                    if (dbSurvey.id == syncSurvey.id) {
                        syncSurvey.isSync = true
                        break
                    }
                }
            }
        }
    }
}