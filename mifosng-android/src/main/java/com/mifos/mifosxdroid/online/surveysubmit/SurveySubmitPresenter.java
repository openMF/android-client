package com.mifos.mifosxdroid.online.surveysubmit;

import com.mifos.api.datamanager.DataManagerSurveys;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.survey.Scorecard;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class SurveySubmitPresenter extends BasePresenter<SurveySubmitMvpView> {

    private final DataManagerSurveys mDataManagerSurveys;
    private Subscription mSubscription;

    @Inject
    public SurveySubmitPresenter(DataManagerSurveys dataManagerSurveys) {
        mDataManagerSurveys = dataManagerSurveys;
    }

    @Override
    public void attachView(SurveySubmitMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void submitSurvey(int survey, Scorecard scorecardPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManagerSurveys.submitScore(survey, scorecardPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Scorecard>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_create_survey_scorecard);
                    }

                    @Override
                    public void onNext(Scorecard scorecard) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSurveySubmittedSuccessfully(scorecard);
                    }
                });
    }

}
