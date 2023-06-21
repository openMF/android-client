package com.mifos.mifosxdroid.online.surveylist

import com.mifos.api.datamanager.DataManagerSurveys
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.survey.QuestionDatas
import com.mifos.objects.survey.ResponseDatas
import com.mifos.objects.survey.Survey
import com.mifos.utils.PrefManager
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 08/06/16.
 */
class SurveyListPresenter @Inject constructor(private val mDataManagerSurveys: DataManagerSurveys) : BasePresenter<SurveyListMvpView?>() {
    private val mSubscriptions: CompositeSubscription?
    private var mDbSurveyList: List<Survey>? = null
    private var mSyncSurveyList: List<Survey?>? = null
    override fun attachView(mvpView: SurveyListMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscriptions?.clear()
    }

    fun loadSurveyList() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscriptions!!.add(mDataManagerSurveys.allSurvey
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Survey?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showFetchingError(R.string.failed_to_fetch_surveys_list)
                    }

                    override fun onNext(surveys: List<Survey?>?) {
                        mSyncSurveyList = surveys
                        mvpView!!.showProgressbar(false)
                        loadDatabaseSurveys()
                    }
                })
        )
    }

    fun loadDatabaseSurveys() {
        checkViewAttached()
        mSubscriptions!!.add(mDataManagerSurveys.databaseSurveys
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Survey?>?>() {
                    override fun onCompleted() {
                        setAlreadySurveySyncStatus(mSyncSurveyList)
                        mvpView!!.showAllSurvey(mSyncSurveyList)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showFetchingError(R.string.failed_to_load_db_surveys)
                    }

                    override fun onNext(surveyList: List<Survey?>?) {
                        mDbSurveyList = surveyList as List<Survey>?
                        if (PrefManager.userStatus == 1) {
                            for (survey in mSyncSurveyList!!) {
                                loadDatabaseQuestionDatas(survey!!.id, survey)
                            }
                        }
                    }
                })
        )
    }

    fun loadDatabaseQuestionDatas(surveyId: Int, survey: Survey?) {
        checkViewAttached()
        mSubscriptions!!.add(mDataManagerSurveys.getDatabaseQuestionDatas(surveyId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<QuestionDatas?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showFetchingError(R.string.failed_to_load_db_question_datas)
                    }

                    override fun onNext(questionDatasList: List<QuestionDatas?>?) {
                        for (questionDatas in questionDatasList!!) {
                            questionDatas?.id?.let { loadDatabaseResponseDatas(it, questionDatas) }
                        }
                        survey!!.questionDatas = questionDatasList
                    }
                })
        )
    }

    fun loadDatabaseResponseDatas(questionId: Int, questionDatas: QuestionDatas) {
        checkViewAttached()
        mSubscriptions!!.add(mDataManagerSurveys.getDatabaseResponseDatas(questionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<ResponseDatas?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showFetchingError(R.string.failed_to_load_db_response_datas)
                    }

                    override fun onNext(responseDatas: List<ResponseDatas?>?) {
                        questionDatas.responseDatas = responseDatas
                    }
                })
        )
    }

    fun setAlreadySurveySyncStatus(surveys: List<Survey?>?) {
        checkSurveyAlreadySyncedOrNot(surveys)
    }

    fun checkSurveyAlreadySyncedOrNot(surveys: List<Survey?>?) {
        if (mDbSurveyList!!.size != 0) {
            for (dbSurvey in mDbSurveyList!!) {
                for (syncSurvey in surveys!!) {
                    if (dbSurvey.id == syncSurvey!!.id) {
                        syncSurvey.isSync = true
                        break
                    }
                }
            }
        }
    }

    init {
        mSubscriptions = CompositeSubscription()
    }
}