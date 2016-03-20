package com.mifos.mifosxdroid.online.surveylastfragment;

import com.mifos.api.DataManager;
import com.mifos.api.model.ScorecardPayload;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.survey.Scorecard;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public class SurveyLastPresenter implements Presenter<SurveyLastMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    public SurveyLastMvpView mSurveyLastMvpView;

    public SurveyLastPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }


    @Override
    public void attachView(SurveyLastMvpView mvpView) {
        mSurveyLastMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mSurveyLastMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void submitScorecard(int survey, ScorecardPayload scorecardPayload){

        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.submitScore(survey,scorecardPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Scorecard>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mSurveyLastMvpView.submittingScoreCardError("Try again");
                    }

                    @Override
                    public void onNext(Scorecard scorecard) {
                        mSurveyLastMvpView.submitScoreResultCallBack(scorecard);
                    }
                });

    }
}
