package com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.survey.Survey;

import java.util.List;

/**
 * Created by mayankjindal on 19/07/17.
 */

public interface SyncSurveysDialogMvpView extends MvpView {

    void showUI();

    void updateSurveyList(List<Survey> mSurveyList);

    void showSyncingSurvey(String surveyName);

    void showSyncedFailedSurveys(int failedCount);

    void setMaxSingleSyncSurveyProgressBar(int total);

    void setQuestionSyncProgressBarMax(int total);

    void setResponseSyncProgressBarMax(int total);

    void updateSingleSyncSurveyProgressBar(int i);

    void updateQuestionSyncProgressBar(int i);

    void updateResponseSyncProgressBar(int i);

    void updateTotalSyncSurveyProgressBarAndCount(int i);

    int getMaxSingleSyncSurveyProgressBar();

    void showNetworkIsNotAvailable();

    void showSurveysSyncSuccessfully();

    Boolean isOnline();

    void dismissDialog();

    void showDialog();

    void hideDialog();

    void showError(int s);
}
