package com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog

import com.mifos.api.datamanager.DataManagerSurveys
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.survey.QuestionDatas
import com.mifos.objects.survey.ResponseDatas
import com.mifos.objects.survey.Survey
import retrofit2.adapter.rxjava.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by mayankjindal on 19/07/17.
 */
class SyncSurveysDialogPresenter @Inject constructor(private val mDataManagerSurvey: DataManagerSurveys) :
    BasePresenter<SyncSurveysDialogMvpView>() {
    private val mSubscriptions: CompositeSubscription = CompositeSubscription()
    private var mSurveyList: List<Survey>
    private val mFailedSyncSurvey: MutableList<Survey>
    private var mQuestionDatasList: List<QuestionDatas>
    private var mResponseDatasList: List<ResponseDatas>
    private var mSurveySyncIndex = 0
    private var mQuestionDataSyncIndex = 0
    private var mResponseDataSyncIndex = 0

    init {
        mSurveyList = ArrayList()
        mQuestionDatasList = ArrayList()
        mResponseDatasList = ArrayList()
        mFailedSyncSurvey = ArrayList()
    }

    override fun attachView(mvpView: SyncSurveysDialogMvpView) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions.unsubscribe()
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
        if (mvpView?.isOnline!!) {
            syncSurveyAndUpdateUI()
        } else {
            mvpView?.showNetworkIsNotAvailable()
            mvpView?.dismissDialog()
        }
    }

    /**
     * This Method checking network connection before starting questiondata synchronization
     */
    private fun checkNetworkConnectionAndSyncQuestionData() {
        if (mvpView?.isOnline!!) {
            syncQuestionDataAndUpdateUI()
        } else {
            mvpView?.showNetworkIsNotAvailable()
            mvpView?.dismissDialog()
        }
    }

    /**
     * This Method checking network connection before starting responsedata synchronization
     */
    private fun checkNetworkConnectionAndSyncResponseData() {
        if (mvpView?.isOnline!!) {
            syncResponseDataAndUpdateUI()
        } else {
            mvpView?.showNetworkIsNotAvailable()
            mvpView?.dismissDialog()
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
            mvpView?.showSurveysSyncSuccessfully()
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
            mvpView?.updateSingleSyncSurveyProgressBar(mQuestionDataSyncIndex)
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
            mQuestionDatasList[mQuestionDataSyncIndex].id?.let {
                syncResponseData(
                    it,
                    mResponseDatasList[mResponseDataSyncIndex]
                )
            }
        } else {
            mQuestionDataSyncIndex += 1
            mvpView?.updateQuestionSyncProgressBar(mQuestionDataSyncIndex)
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
                val singleSyncSurveyMax = mvpView!!.maxSingleSyncSurveyProgressBar
                mvpView?.updateSingleSyncSurveyProgressBar(singleSyncSurveyMax)
                mFailedSyncSurvey.add(mSurveyList[mSurveySyncIndex])
                mSurveySyncIndex += 1
                mvpView?.showSyncedFailedSurveys(mFailedSyncSurvey.size)
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
        checkViewAttached()
        survey.isSync = true
        mSubscriptions.add(
            mDataManagerSurvey.syncSurveyInDatabase(survey)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Survey>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showError(R.string.failed_to_sync_survey)
                        onAccountSyncFailed(e)
                    }

                    override fun onNext(survey: Survey) {
                        mQuestionDatasList = survey.questionDatas
                        mvpView?.maxSingleSyncSurveyProgressBar = mQuestionDatasList.size
                        mvpView?.setQuestionSyncProgressBarMax(mQuestionDatasList.size)
                        checkSurveySyncStatus()
                    }
                })
        )
    }

    /**
     * This Method Saving the QuestionDatas to Database.
     *
     * @param surveyId int, questionDatas QuestionDatas
     */
    private fun syncQuestionData(surveyId: Int, questionDatas: QuestionDatas) {
        checkViewAttached()
        mSubscriptions.add(
            mDataManagerSurvey.syncQuestionDataInDatabase(surveyId, questionDatas)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<QuestionDatas>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView?.showError(R.string.failed_to_sync_survey)
                        onAccountSyncFailed(e)
                    }

                    override fun onNext(questionDatas: QuestionDatas) {
                        mResponseDatasList = questionDatas.responseDatas
                        mvpView?.setResponseSyncProgressBarMax(mResponseDatasList.size)
                        checkQuestionDataSyncStatusAndSync()
                    }
                })
        )
    }

    /**
     * This Method Saving the ResponseDatas to Database.
     *
     * @param questionId int, responseDatas ResponseDatas
     */
    private fun syncResponseData(questionId: Int, responseDatas: ResponseDatas) {
        checkViewAttached()
        mSubscriptions.add(
            mDataManagerSurvey.syncResponseDataInDatabase(questionId, responseDatas)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ResponseDatas>() {
                    override fun onCompleted() {
                        mvpView?.updateResponseSyncProgressBar(mResponseDataSyncIndex)
                    }

                    override fun onError(e: Throwable) {
                        mvpView?.showError(R.string.failed_to_sync_survey)
                        onAccountSyncFailed(e)
                    }

                    override fun onNext(responseDatas: ResponseDatas) {
                        mResponseDataSyncIndex += 1
                        checkNetworkConnectionAndSyncResponseData()
                    }
                })
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
        checkViewAttached()
        mvpView?.showProgressbar(true)
        mSubscriptions.add(
            mDataManagerSurvey.allSurvey
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Survey>>() {
                    override fun onCompleted() {
                        mvpView?.showUI()
                        mvpView?.updateTotalSyncSurveyProgressBarAndCount(0)
                        startSyncingSurveys()
                    }

                    override fun onError(e: Throwable) {
                        mvpView?.showError(R.string.failed_to_fetch_surveys_list)
                    }

                    override fun onNext(surveys: List<Survey>) {
                        mSurveyList = surveys
                        mvpView?.showProgressbar(false)
                        mvpView?.updateSurveyList(surveys)
                    }
                })
        )
    }

    private fun updateTotalSyncProgressBarAndCount() {
        mvpView?.updateTotalSyncSurveyProgressBarAndCount(mSurveySyncIndex)
    }

    private fun updateSurveyName() {
        val surveyName = mSurveyList[mSurveySyncIndex].name
        mvpView?.showSyncingSurvey(surveyName)
    }
}