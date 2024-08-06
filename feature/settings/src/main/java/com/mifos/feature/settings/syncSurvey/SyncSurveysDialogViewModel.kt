package com.mifos.feature.settings.syncSurvey

import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.NetworkUtilsWrapper
import com.mifos.core.objects.survey.QuestionDatas
import com.mifos.core.objects.survey.ResponseDatas
import com.mifos.core.objects.survey.Survey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class SyncSurveysDialogViewModel @Inject constructor(
    private val repository: SyncSurveysDialogRepository
) :
    ViewModel() {

    @Inject
    lateinit var networkUtilsWrapper: NetworkUtilsWrapper

    private val _syncSurveysDialogUiState =
        MutableStateFlow<SyncSurveysDialogUiState>(SyncSurveysDialogUiState.Initial)

    val syncSurveysDialogUiState: StateFlow<SyncSurveysDialogUiState>
        get() = _syncSurveysDialogUiState

    private var mSurveyList: List<Survey> = ArrayList()
    private val mFailedSyncSurvey: MutableList<Survey> = ArrayList()
    private var mQuestionDatasList: List<QuestionDatas> = ArrayList()
    private var mResponseDatasList: List<ResponseDatas> = ArrayList()
    private var mSurveySyncIndex = 0
    private var mQuestionDataSyncIndex = 0
    private var mResponseDataSyncIndex = 0
    private var maxSingleSyncSurveyProgressBar = 0


    private fun checkNetworkConnection(): Boolean {
        return networkUtilsWrapper.isNetworkConnected()
    }

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
        if (checkNetworkConnection()) {
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
        if (checkNetworkConnection()) {
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
        if (checkNetworkConnection()) {
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
                mQuestionDatasList[mQuestionDataSyncIndex]
            )
        } else {
            _syncSurveysDialogUiState.value =
                mSurveyList[mSurveySyncIndex].name?.let {
                    SyncSurveysDialogUiState.UpdateSingleSyncSurvey(
                        mSurveySyncIndex, it, mQuestionDatasList.size
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
                mResponseDatasList[mResponseDataSyncIndex]
            )
        } else {
            _syncSurveysDialogUiState.value =
                SyncSurveysDialogUiState.UpdateQuestionSync(
                    mQuestionDataSyncIndex,
                    mQuestionDatasList[mQuestionDataSyncIndex].questionId,
                    mResponseDatasList.size
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
            if (e is HttpException) {
                val singleSyncSurveyMax = maxSingleSyncSurveyProgressBar
                mFailedSyncSurvey.add(mSurveyList[mSurveySyncIndex])
                mSurveySyncIndex += 1
                _syncSurveysDialogUiState.value =
                    SyncSurveysDialogUiState.ShowSyncedFailedSurveys(mFailedSyncSurvey.size)
                checkNetworkConnectionAndSyncSurvey()
            }
        } catch (throwable: Throwable) {
            RxJavaPlugins.getInstance().errorHandler.handleError(throwable)
        }
    }

    /**
     * This Method Saving the Surveys to Database.
     *
     * @param survey
     */
    private fun syncSurvey(survey: Survey) {
        survey.isSync = true
        repository.syncSurveyInDatabase(survey)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Survey>() {
                override fun onStart() {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.ShowUI(mSurveyList.size)
                }

                override fun onCompleted() {
                    _syncSurveysDialogUiState.value =
                        mSurveyList[mSurveySyncIndex].name?.let {
                            SyncSurveysDialogUiState.UpdateSingleSyncSurvey(
                                mSurveySyncIndex + 1, it, mQuestionDatasList.size
                            )
                        }!!
                }

                override fun onError(e: Throwable) {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.ShowError(e.message.toString())
                    onAccountSyncFailed(e)
                }

                override fun onNext(survey: Survey) {
                    mQuestionDatasList = survey.questionDatas
                    checkSurveySyncStatus()
                }
            }
        )
    }

    /**
     * This Method Saving the QuestionDatas to Database.
     *
     * @param surveyId int, questionDatas QuestionDatas
     */
    private fun syncQuestionData(surveyId: Int, questionDatas: QuestionDatas) {
        repository.syncQuestionDataInDatabase(surveyId, questionDatas)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<QuestionDatas>() {
                override fun onCompleted() {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.UpdateQuestionSync(
                            mQuestionDataSyncIndex + 1,
                            mQuestionDatasList[mQuestionDataSyncIndex].questionId,
                            mResponseDatasList.size
                        )
                }

                override fun onError(e: Throwable) {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.ShowError(e.message.toString())
                    onAccountSyncFailed(e)
                }

                override fun onNext(questionDatas: QuestionDatas) {
                    mResponseDatasList = questionDatas.responseDatas
                    checkQuestionDataSyncStatusAndSync()
                }
            }
        )
    }

    /**
     * This Method Saving the ResponseDatas to Database.
     *
     * @param questionId int, responseDatas ResponseDatas
     */
    private fun syncResponseData(questionId: Int, responseDatas: ResponseDatas) {
        repository.syncResponseDataInDatabase(questionId, responseDatas)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseDatas>() {
                override fun onCompleted() {
                    SyncSurveysDialogUiState.UpdateResponseSync(
                        mResponseDataSyncIndex,
                        mResponseDatasList[mResponseDataSyncIndex].value
                    )
                }

                override fun onError(e: Throwable) {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.ShowError(e.message.toString())
                    onAccountSyncFailed(e)
                }

                override fun onNext(responseDatas: ResponseDatas) {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.UpdateResponseSync(
                            mResponseDataSyncIndex,
                            mResponseDatasList[mResponseDataSyncIndex].value
                        )
                    mResponseDataSyncIndex += 1
                    checkNetworkConnectionAndSyncResponseData()
                }
            }
        )
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
        _syncSurveysDialogUiState.value = SyncSurveysDialogUiState.ShowProgressbar
        repository.allSurvey()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Survey>>() {
                override fun onCompleted() {
                    startSyncingSurveys()
                }

                override fun onError(e: Throwable) {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.ShowError(e.message.toString())
                }

                override fun onNext(surveys: List<Survey>) {
                    mSurveyList = surveys
                }
            }
        )
    }
}