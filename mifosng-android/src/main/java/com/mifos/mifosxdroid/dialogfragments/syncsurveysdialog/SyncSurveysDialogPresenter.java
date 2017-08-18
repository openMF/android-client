package com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog;

import com.mifos.api.datamanager.DataManagerSurveys;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.survey.QuestionDatas;
import com.mifos.objects.survey.ResponseDatas;
import com.mifos.objects.survey.Survey;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by mayankjindal on 19/07/17.
 */

public class SyncSurveysDialogPresenter extends BasePresenter<SyncSurveysDialogMvpView> {

    private final DataManagerSurveys mDataManagerSurvey;
    private CompositeSubscription mSubscriptions;
    private List<Survey> mSurveyList, mFailedSyncSurvey;
    private List<QuestionDatas> mQuestionDatasList;
    private List<ResponseDatas> mResponseDatasList;
    private int mSurveySyncIndex, mQuestionDataSyncIndex, mResponseDataSyncIndex;

    @Inject
    public SyncSurveysDialogPresenter(DataManagerSurveys dataManagerSurvey) {
        mDataManagerSurvey = dataManagerSurvey;
        mSubscriptions = new CompositeSubscription();
        mSurveyList = new ArrayList<>();
        mQuestionDatasList = new ArrayList<>();
        mResponseDatasList = new ArrayList<>();
        mFailedSyncSurvey = new ArrayList<>();
    }

    @Override
    public void attachView(SyncSurveysDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    /**
     * This Method Start Syncing Surveys. Start Syncing the Survey Accounts.
     *
     *
     */
    private void startSyncingSurveys() {
        checkNetworkConnectionAndSyncSurvey();
    }

    /**
     * This Method checking network connection before starting survey synchronization
     */
    private void checkNetworkConnectionAndSyncSurvey() {
        if (getMvpView().isOnline()) {
            syncSurveyAndUpdateUI();
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    /**
     * This Method checking network connection before starting questiondata synchronization
     */
    private void checkNetworkConnectionAndSyncQuestionData() {
        if (getMvpView().isOnline()) {
            syncQuestionDataAndUpdateUI();
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    /**
     * This Method checking network connection before starting responsedata synchronization
     */
    private void checkNetworkConnectionAndSyncResponseData() {
        if (getMvpView().isOnline()) {
            syncResponseDataAndUpdateUI();
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    /**
     * This Method checking that mSurveySyncIndex and mSurveyList Size are equal or not. If they
     * are equal, It means all surveys have been synced otherwise continue syncing surveys.
     */
    private void syncSurveyAndUpdateUI() {
        updateTotalSyncProgressBarAndCount();
        if (mSurveySyncIndex != mSurveyList.size()) {
            updateSurveyName();
            syncSurvey(mSurveyList.get(mSurveySyncIndex));
        } else {
            getMvpView().showSurveysSyncSuccessfully();
        }
    }

    /**
     * This Method checking that mQuestionDataSyncIndex and mQuestionDatasList Size
     * are equal or not. If they are equal, It means all questiondata have been
     * synced otherwise continue syncing questiondata.
     */
    private void syncQuestionDataAndUpdateUI() {
        if (mQuestionDataSyncIndex != mQuestionDatasList.size()) {
            syncQuestionData(mSurveyList.get(mSurveySyncIndex).getId(),
                    mQuestionDatasList.get(mQuestionDataSyncIndex));
        } else {
            mSurveySyncIndex = mSurveySyncIndex + 1;
            getMvpView().updateSingleSyncSurveyProgressBar(mQuestionDataSyncIndex);
            mQuestionDataSyncIndex = 0;
            checkNetworkConnectionAndSyncSurvey();
        }
    }

    /**
     * This Method checking that mResponseDataSyncIndex and mResponseDatasList Size
     * are equal or not. If they are equal, It means all responsedata have been
     * synced otherwise continue syncing responsedata.
     */
    private void syncResponseDataAndUpdateUI() {
        if (mResponseDataSyncIndex != mResponseDatasList.size()) {
            syncResponseData(mQuestionDatasList.get(mQuestionDataSyncIndex).getId(),
                    mResponseDatasList.get(mResponseDataSyncIndex));
        } else {
            mQuestionDataSyncIndex = mQuestionDataSyncIndex + 1;
            getMvpView().updateQuestionSyncProgressBar(mQuestionDataSyncIndex);
            mResponseDataSyncIndex = 0;
            checkNetworkConnectionAndSyncQuestionData();
        }
    }

    /**
     * This Method will be called when ever any request will be failed synced.
     *
     * @param e Throwable
     */
    private void onAccountSyncFailed(Throwable e) {
        try {
            if (e instanceof HttpException) {
                int singleSyncSurveyMax = getMvpView().getMaxSingleSyncSurveyProgressBar();
                getMvpView().updateSingleSyncSurveyProgressBar(singleSyncSurveyMax);

                mFailedSyncSurvey.add(mSurveyList.get(mSurveySyncIndex));
                mSurveySyncIndex = mSurveySyncIndex + 1;

                getMvpView().showSyncedFailedSurveys(mFailedSyncSurvey.size());
                checkNetworkConnectionAndSyncSurvey();
            }
        } catch (Throwable throwable) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(throwable);
        }
    }

    /**
     * This Method Saving the Surveys to Database.
     *
     * @param survey
     */
    private void syncSurvey(Survey survey) {
        checkViewAttached();
        survey.setSync(true);
        mSubscriptions.add(mDataManagerSurvey.syncSurveyInDatabase(survey)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Survey>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_to_sync_survey);
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(Survey survey) {
                        mQuestionDatasList = survey.getQuestionDatas();
                        getMvpView().setMaxSingleSyncSurveyProgressBar(mQuestionDatasList.size());
                        getMvpView().setQuestionSyncProgressBarMax(mQuestionDatasList.size());
                        checkSurveySyncStatus();
                    }
                })
        );
    }

    /**
     * This Method Saving the QuestionDatas to Database.
     *
     * @param surveyId int, questionDatas QuestionDatas
     */
    private void syncQuestionData(int surveyId, QuestionDatas questionDatas) {
        checkViewAttached();
        mSubscriptions.add(mDataManagerSurvey.syncQuestionDataInDatabase(surveyId, questionDatas)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<QuestionDatas>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_to_sync_survey);
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(QuestionDatas questionDatas) {
                        mResponseDatasList = questionDatas.getResponseDatas();
                        getMvpView().setResponseSyncProgressBarMax(mResponseDatasList.size());
                        checkQuestionDataSyncStatusAndSync();
                    }
                })
        );
    }

    /**
     * This Method Saving the ResponseDatas to Database.
     *
     * @param questionId int, responseDatas ResponseDatas
     */
    private void syncResponseData(int questionId, ResponseDatas responseDatas) {
        checkViewAttached();
        mSubscriptions.add(mDataManagerSurvey.syncResponseDataInDatabase(questionId, responseDatas)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseDatas>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().updateResponseSyncProgressBar(mResponseDataSyncIndex);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_to_sync_survey);
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(ResponseDatas responseDatas) {
                        mResponseDataSyncIndex = mResponseDataSyncIndex + 1;
                        checkNetworkConnectionAndSyncResponseData();
                    }
                })
        );
    }

    private void checkSurveySyncStatus() {
        if (!mQuestionDatasList.isEmpty()) {
            checkNetworkConnectionAndSyncQuestionData();
        } else {
            checkNetworkConnectionAndSyncSurvey();
        }
    }

    private void checkQuestionDataSyncStatusAndSync() {
        if (!mResponseDatasList.isEmpty()) {
            checkNetworkConnectionAndSyncResponseData();
        } else {
            checkNetworkConnectionAndSyncQuestionData();
        }
    }

    public void loadSurveyList() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerSurvey.getAllSurvey()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Survey>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showUI();
                        getMvpView().updateTotalSyncSurveyProgressBarAndCount(0);
                        startSyncingSurveys();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_to_fetch_surveys_list);
                    }

                    @Override
                    public void onNext(List<Survey> surveys) {
                        mSurveyList = surveys;
                        getMvpView().showProgressbar(false);
                        getMvpView().updateSurveyList(surveys);
                    }
                })
        );
    }


    private void updateTotalSyncProgressBarAndCount() {
        getMvpView().updateTotalSyncSurveyProgressBarAndCount(mSurveySyncIndex);
    }

    private void updateSurveyName() {
        String surveyName = mSurveyList.get(mSurveySyncIndex).getName();
        getMvpView().showSyncingSurvey(surveyName);
    }
}
