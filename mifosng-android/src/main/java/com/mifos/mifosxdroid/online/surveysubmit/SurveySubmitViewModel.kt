package com.mifos.mifosxdroid.online.surveysubmit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.survey.Scorecard
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
@HiltViewModel
class SurveySubmitViewModel @Inject constructor(private val repository: SurveySubmitRepository) :
    ViewModel() {

    private val _surveySubmitUiState = MutableLiveData<SurveySubmitUiState>()

    val surveySubmitUiState: LiveData<SurveySubmitUiState>
        get() = _surveySubmitUiState

    fun submitSurvey(survey: Int, scorecardPayload: Scorecard?) {
        _surveySubmitUiState.value = SurveySubmitUiState.ShowProgressbar
        repository.submitScore(survey, scorecardPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Scorecard>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _surveySubmitUiState.value = SurveySubmitUiState.ShowError(e.message.toString())
                }

                override fun onNext(scorecard: Scorecard) {
                    _surveySubmitUiState.value =
                        SurveySubmitUiState.ShowSurveySubmittedSuccessfully(scorecard)
                }
            })
    }
}