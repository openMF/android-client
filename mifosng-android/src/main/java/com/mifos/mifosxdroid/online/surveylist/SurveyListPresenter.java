package com.mifos.mifosxdroid.online.surveylist;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.survey.Survey;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class SurveyListPresenter extends BasePresenter<SurveyListMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    public SurveyListPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(SurveyListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadSurveyList(){
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription  = mDataManager.getAllSurvey()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Survey>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Couldn't Fetch List of Surveys");
                    }

                    @Override
                    public void onNext(List<Survey> surveys) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showAllSurvey(surveys);
                    }
                });
    }
}
