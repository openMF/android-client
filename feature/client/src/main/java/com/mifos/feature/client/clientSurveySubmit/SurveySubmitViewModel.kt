package com.mifos.feature.client.clientSurveySubmit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.Constants
import com.mifos.core.data.repository.SurveySubmitRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.objects.survey.Scorecard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 13/08/23.
 */
@HiltViewModel
class SurveySubmitViewModel @Inject constructor(
    private val repository: SurveySubmitRepository,
    private val prefManager: PrefManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = -1)

    private val _surveySubmitUiState =
        MutableStateFlow<SurveySubmitUiState>(SurveySubmitUiState.Initial)

    val surveySubmitUiState: StateFlow<SurveySubmitUiState>
        get() = _surveySubmitUiState

    val userId = MutableStateFlow(prefManager.getUserId())

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