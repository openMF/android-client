package com.mifos.feature.client.clientSurveyQuestion

import androidx.lifecycle.ViewModel
import com.mifos.core.data.repository.SurveyQuestionRepository
import com.mifos.core.objects.survey.Survey
import com.mifos.feature.client.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SurveyQuestionViewModel @Inject constructor(
    private val surveyQuestionRepository: SurveyQuestionRepository,
) : ViewModel() {

    private val _surveyQuestionUiState =
        MutableStateFlow<SurveyQuestionUiState>(SurveyQuestionUiState.ShowProgressbar)
    val surveyQuestionUiState: StateFlow<SurveyQuestionUiState> get() = _surveyQuestionUiState

    private lateinit var mSyncSurvey: Survey

    fun loadSurvey(surveyId: Int) {
        _surveyQuestionUiState.value = SurveyQuestionUiState.ShowProgressbar

        surveyQuestionRepository.getSurvey(surveyId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Survey>() {
                override fun onCompleted() {
                    _surveyQuestionUiState.value = SurveyQuestionUiState.ShowQuestions(ques = mSyncSurvey)
                }
                override fun onError(e: Throwable) {
                    _surveyQuestionUiState.value =
                        SurveyQuestionUiState.ShowFetchingError(R.string.feature_client_failed_to_fetch_survey_questions)
                }

                override fun onNext(survey: Survey) {
                    mSyncSurvey = survey
                }
            })
    }
}