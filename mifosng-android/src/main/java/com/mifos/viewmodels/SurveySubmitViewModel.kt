package com.mifos.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.objects.survey.Scorecard
import com.mifos.repositories.SurveySubmitRepository
import com.mifos.states.SurveySubmitUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SurveySubmitViewModel @Inject constructor(private val repository: SurveySubmitRepository) :
    ViewModel() {

    private val _surveySubmitUiState = MutableLiveData<SurveySubmitUiState>()

    val surveySubmitUiState: LiveData<SurveySubmitUiState>
        get() = _surveySubmitUiState

    fun submitSurvey(survey: Int, scorecardPayload: Scorecard?) {
//        checkViewAttached()
//        mvpView!!.showProgressbar(true)
        _surveySubmitUiState.value = SurveySubmitUiState.ShowProgressbar
//        if (mSubscription != null) mSubscription!!.unsubscribe()
        repository.submitScore(survey, scorecardPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Scorecard>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
//                    mvpView!!.showProgressbar(false)
//                    mvpView!!.showError(R.string.failed_to_create_survey_scorecard)
                    _surveySubmitUiState.value = SurveySubmitUiState.ShowError(e.message.toString())
                }

                override fun onNext(scorecard: Scorecard) {
//                    mvpView!!.showProgressbar(false)
//                    mvpView!!.showSurveySubmittedSuccessfully(scorecard)
                    _surveySubmitUiState.value =
                        SurveySubmitUiState.ShowSurveySubmittedSuccessfully(scorecard)
                }
            })
    }
}