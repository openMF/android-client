package com.mifos.mifosxdroid.online.surveysubmit

import com.mifos.api.datamanager.DataManagerSurveys
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.objects.survey.Scorecard
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 08/06/16.
 */
class SurveySubmitPresenter @Inject constructor(private val mDataManagerSurveys: DataManagerSurveys) : BasePresenter<SurveySubmitMvpView?>() {
    private var mSubscription: Subscription? = null
    override fun attachView(mvpView: SurveySubmitMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        if (mSubscription != null) mSubscription!!.unsubscribe()
    }

    fun submitSurvey(survey: Int, scorecardPayload: Scorecard?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        if (mSubscription != null) mSubscription!!.unsubscribe()
        mSubscription = mDataManagerSurveys.submitScore(survey, scorecardPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Scorecard?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(R.string.failed_to_create_survey_scorecard)
                    }

                    override fun onNext(scorecard: Scorecard?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showSurveySubmittedSuccessfully(scorecard)
                    }
                })
    }

}