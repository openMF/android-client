package com.mifos.mifosxdroid.online.surveylist;

import com.mifos.api.datamanager.DataManagerSurveys;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.survey.QuestionDatas;
import com.mifos.objects.survey.ResponseDatas;
import com.mifos.objects.survey.Survey;
import com.mifos.utils.PrefManager;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class SurveyListPresenter extends BasePresenter<SurveyListMvpView> {

    private final DataManagerSurveys mDataManagerSurveys;
    private CompositeDisposable compositeDisposable;
    private List<Survey> mDbSurveyList;
    private List<Survey> mSyncSurveyList;

    @Inject
    public SurveyListPresenter(DataManagerSurveys dataManagerSurveys) {
        mDataManagerSurveys = dataManagerSurveys;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(SurveyListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeDisposable != null) compositeDisposable.clear();
    }

    public void loadSurveyList() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManagerSurveys.getAllSurvey()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<Survey>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_fetch_surveys_list);
                    }

                    @Override
                    public void onNext(List<Survey> surveys) {
                        mSyncSurveyList = surveys;
                        getMvpView().showProgressbar(false);
                        loadDatabaseSurveys();
                    }
                })
        );
    }

    public void loadDatabaseSurveys() {
        checkViewAttached();
        compositeDisposable.add(mDataManagerSurveys.getDatabaseSurveys()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<Survey>>() {
                    @Override
                    public void onComplete() {
                        setAlreadySurveySyncStatus(mSyncSurveyList);
                        getMvpView().showAllSurvey(mSyncSurveyList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError(R.string.failed_to_load_db_surveys);
                    }

                    @Override
                    public void onNext(List<Survey> surveyList) {
                        mDbSurveyList = surveyList;
                        if (PrefManager.getUserStatus() == 1) {
                            for (Survey survey : mSyncSurveyList) {
                                loadDatabaseQuestionDatas(survey.getId(), survey);
                            }
                        }
                    }
                })
        );
    }

    public void loadDatabaseQuestionDatas(int surveyId, final Survey survey) {
        checkViewAttached();
        compositeDisposable.add(mDataManagerSurveys.getDatabaseQuestionDatas(surveyId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<QuestionDatas>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError(R.string.failed_to_load_db_question_datas);
                    }

                    @Override
                    public void onNext(List<QuestionDatas> questionDatasList) {
                        for (QuestionDatas questionDatas : questionDatasList) {
                            loadDatabaseResponseDatas(questionDatas.getId(), questionDatas);
                        }
                        survey.setQuestionDatas(questionDatasList);
                    }
                })
        );
    }

    public void loadDatabaseResponseDatas(int questionId, final QuestionDatas questionDatas) {
        checkViewAttached();
        compositeDisposable.add(mDataManagerSurveys.getDatabaseResponseDatas(questionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<ResponseDatas>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError(R.string.failed_to_load_db_response_datas);
                    }

                    @Override
                    public void onNext(List<ResponseDatas> responseDatas) {
                        questionDatas.setResponseDatas(responseDatas);
                    }
                })
        );
    }

    public void setAlreadySurveySyncStatus(List<Survey> surveys) {
        checkSurveyAlreadySyncedOrNot(surveys);
    }

    public void checkSurveyAlreadySyncedOrNot(List<Survey> surveys) {
        if (mDbSurveyList.size() != 0) {
            for (Survey dbSurvey : mDbSurveyList) {
                for (Survey syncSurvey : surveys) {
                    if (dbSurvey.getId() == syncSurvey.getId()) {
                        syncSurvey.setSync(true);
                        break;
                    }
                }
            }
        }
    }
}
