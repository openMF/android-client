package com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.survey.QuestionDatas
import com.mifos.core.objects.survey.ResponseDatas
import com.mifos.core.objects.survey.Survey
import com.mifos.utils.NetworkUtilsWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.adapter.rxjava.HttpException
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

    private val _syncSurveysDialogUiState = MutableLiveData<SyncSurveysDialogUiState>()

    val syncSurveysDialogUiState: LiveData<SyncSurveysDialogUiState>
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
        updateTotalSyncProgressBarAndCount()
        if (mSurveySyncIndex != mSurveyList.size) {
            updateSurveyName()
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
            mSurveySyncIndex += 1
            _syncSurveysDialogUiState.value =
                SyncSurveysDialogUiState.UpdateSingleSyncSurveyProgressBar(mQuestionDataSyncIndex)
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
            mQuestionDatasList[mQuestionDataSyncIndex].id.let {
                syncResponseData(
                    it,
                    mResponseDatasList[mResponseDataSyncIndex]
                )
            }
        } else {
            mQuestionDataSyncIndex += 1
            _syncSurveysDialogUiState.value =
                SyncSurveysDialogUiState.UpdateQuestionSyncProgressBar(mQuestionDataSyncIndex)
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
                _syncSurveysDialogUiState.value =
                    SyncSurveysDialogUiState.UpdateSingleSyncSurveyProgressBar(singleSyncSurveyMax)
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
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.ShowError(e.message.toString())
                    onAccountSyncFailed(e)
                }

                override fun onNext(survey: Survey) {
                    mQuestionDatasList = survey.questionDatas
                    maxSingleSyncSurveyProgressBar = mQuestionDatasList.size
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.SetQuestionSyncProgressBarMax(mQuestionDatasList.size)
                    checkSurveySyncStatus()
                }
            })
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
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.ShowError(e.message.toString())
                    onAccountSyncFailed(e)
                }

                override fun onNext(questionDatas: QuestionDatas) {
                    mResponseDatasList = questionDatas.responseDatas
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.SetResponseSyncProgressBarMax(mResponseDatasList.size)
                    checkQuestionDataSyncStatusAndSync()
                }
            })

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
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.UpdateResponseSyncProgressBar(
                            mResponseDataSyncIndex
                        )
                }

                override fun onError(e: Throwable) {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.ShowError(e.message.toString())
                    onAccountSyncFailed(e)
                }

                override fun onNext(responseDatas: ResponseDatas) {
                    mResponseDataSyncIndex += 1
                    checkNetworkConnectionAndSyncResponseData()
                }
            })

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
                    _syncSurveysDialogUiState.value = SyncSurveysDialogUiState.ShowUI
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.UpdateTotalSyncSurveyProgressBarAndCount(0)
                    startSyncingSurveys()
                }

                override fun onError(e: Throwable) {
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.ShowError(e.message.toString())
                }

                override fun onNext(surveys: List<Survey>) {
                    mSurveyList = surveys
                    _syncSurveysDialogUiState.value =
                        SyncSurveysDialogUiState.UpdateSurveyList(surveys)
                }
            })

    }

    private fun updateTotalSyncProgressBarAndCount() {
        _syncSurveysDialogUiState.value =
            SyncSurveysDialogUiState.UpdateTotalSyncSurveyProgressBarAndCount(mSurveySyncIndex)
    }

    private fun updateSurveyName() {
        val surveyName = mSurveyList[mSurveySyncIndex].name
        _syncSurveysDialogUiState.value = surveyName?.let {
            SyncSurveysDialogUiState.ShowSyncingSurvey(
                it
            )
        }
    }
}