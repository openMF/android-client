package com.mifos.mifosxdroid.online.surveysubmit;

import com.mifos.api.DataManager;
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

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public SurveySubmitPresenter(DataManager dataManager) {
        mDataManager = dataManager;
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
        mSubscription = mDataManager.submitScore(survey, scorecardPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Scorecard>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError("Try Again");
                    }

                    @Override
                    public void onNext(Scorecard scorecard) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSurveySubmittedSuccessfully(scorecard);
                    }
                });
    }

}
