package com.mifos.mifosxdroid.online.surveylistfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.survey.Survey;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public class SurveyListPresenter implements Presenter<SurveyListMvpView> {


    private final DataManager mDataManager;
    public Subscription mSubscription;
    private SurveyListMvpView mSurveyListMvpView;

    public SurveyListPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SurveyListMvpView mvpView) {
        mSurveyListMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mSurveyListMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadAllSurvey(){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllSurvey()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Survey>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mSurveyListMvpView.ResponseError("Couldn't Fetch List of Surveys");
                    }

                    @Override
                    public void onNext(List<Survey> surveys) {
                        mSurveyListMvpView.showAllSurvey(surveys);
                    }
                });
    }
}
