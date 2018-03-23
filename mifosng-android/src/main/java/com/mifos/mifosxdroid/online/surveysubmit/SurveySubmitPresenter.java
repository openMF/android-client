package com.mifos.mifosxdroid.online.surveysubmit;

import com.mifos.api.datamanager.DataManagerSurveys;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.survey.Scorecard;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class SurveySubmitPresenter extends BasePresenter<SurveySubmitMvpView> {

    private final DataManagerSurveys mDataManagerSurveys;
    private DisposableObserver<Scorecard> disposableObserver;

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
        if (disposableObserver != null) disposableObserver.dispose();
    }

    public void submitSurvey(int survey, Scorecard scorecardPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (disposableObserver != null) disposableObserver.dispose();
        disposableObserver = mDataManagerSurveys.submitScore(survey, scorecardPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Scorecard>() {
                    @Override
                    public void onComplete() {
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
