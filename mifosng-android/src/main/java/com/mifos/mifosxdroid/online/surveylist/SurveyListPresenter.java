package com.mifos.mifosxdroid.online.surveylist;

import com.mifos.api.datamanager.DataManagerSurveys;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.survey.Survey;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class SurveyListPresenter extends BasePresenter<SurveyListMvpView> {

    private final DataManagerSurveys mDataManagerSurveys;
    private Subscription mSubscription;

    @Inject
    public SurveyListPresenter(DataManagerSurveys dataManagerSurveys) {
        mDataManagerSurveys = dataManagerSurveys;
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

    public void loadSurveyList() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManagerSurveys.getAllSurvey()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Survey>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_fetch_surveys_list);
                    }

                    @Override
                    public void onNext(List<Survey> surveys) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showAllSurvey(surveys);
                    }
                });
    }
}
