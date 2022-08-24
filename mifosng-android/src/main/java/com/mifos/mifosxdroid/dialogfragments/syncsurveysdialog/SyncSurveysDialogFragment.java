package com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.survey.Survey;
import com.mifos.utils.Constants;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SyncSurveysDialogFragment extends DialogFragment implements SyncSurveysDialogMvpView {

    public static final String LOG_TAG = SyncSurveysDialogFragment.class.getSimpleName();

    @BindView(R.id.tv_sync_title)
    TextView tvSyncTitle;

    @BindView(R.id.tv_survey_name)
    TextView tvSyncingSurveyName;

    @BindView(R.id.tv_total_surveys)
    TextView tvTotalSurveys;

    @BindView(R.id.tv_syncing_survey)
    TextView tvSyncingSurvey;

    @BindView(R.id.pb_sync_survey)
    ProgressBar pbSyncingSurvey;

    @BindView(R.id.tv_total_progress)
    TextView tvTotalProgress;

    @BindView(R.id.pb_total_sync_survey)
    ProgressBar pbTotalSyncSurvey;

    @BindView(R.id.tv_syncing_question)
    TextView tvSyncingQuestion;

    @BindView(R.id.pb_sync_question)
    ProgressBar pbSyncingQuestion;

    @BindView(R.id.tv_syncing_response)
    TextView tvSyncingResponse;

    @BindView(R.id.pb_sync_response)
    ProgressBar pbSyncingResponse;

    @BindView(R.id.tv_sync_failed)
    TextView tvSyncFailed;

    @BindView(R.id.btn_hide)
    Button btnHide;

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @Inject
    SyncSurveysDialogPresenter mSyncSurveysDialogPresenter;

    View rootView;

    private List<Survey> mSurveyList;


    public static SyncSurveysDialogFragment newInstance() {
        SyncSurveysDialogFragment syncSurveysDialogFragment = new SyncSurveysDialogFragment();
        Bundle args = new Bundle();
        syncSurveysDialogFragment.setArguments(args);
        return syncSurveysDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        mSurveyList = new ArrayList<Survey>();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_fragment_sync_surveys, container, false);
        ButterKnife.bind(this, rootView);
        mSyncSurveysDialogPresenter.attachView(this);
        //Start Syncing Surveys
        if (isOnline() && (PrefManager.getUserStatus() == Constants.USER_ONLINE)) {
            mSyncSurveysDialogPresenter.loadSurveyList();
        } else {
            showNetworkIsNotAvailable();
            getFragmentManager().popBackStack();
        }

        return rootView;
    }

    @OnClick(R.id.btn_cancel)
    void onClickCancelButton() {
        dismissDialog();
    }

    @OnClick(R.id.btn_hide)
    void onClickHideButton() {
        if (btnHide.getText().equals(getResources().getString(R.string.dialog_action_ok))) {
            dismissDialog();
        } else {
            hideDialog();
        }
    }

    @Override
    public void updateSurveyList(List<Survey> surveyList) {
        mSurveyList = surveyList;
    }

    @Override
    public void showUI() {
        pbTotalSyncSurvey.setMax(mSurveyList.size());
        String total_surveys = mSurveyList.size() + getResources().getString(R.string.space) +
                getResources().getString(R.string.surveys);
        tvTotalSurveys.setText(total_surveys);
        tvSyncFailed.setText(String.valueOf(0));
    }

    @Override
    public void showSyncingSurvey(String surveyName) {
        tvSyncingSurvey.setText(surveyName);
        tvSyncingSurveyName.setText(surveyName);
    }

    @Override
    public void showSyncedFailedSurveys(int failedCount) {
        tvSyncFailed.setText(String.valueOf(failedCount));
    }

    @Override
    public void setMaxSingleSyncSurveyProgressBar(int total) {
        pbSyncingSurvey.setMax(total);
    }

    @Override
    public void setQuestionSyncProgressBarMax(int count) {
        pbSyncingQuestion.setMax(count);
    }

    @Override
    public void setResponseSyncProgressBarMax(int count) {
        pbSyncingResponse.setMax(count);
    }

    @Override
    public void updateSingleSyncSurveyProgressBar(int count) {
        pbSyncingSurvey.setProgress(count);
    }

    @Override
    public void updateQuestionSyncProgressBar(int i) {
        pbSyncingQuestion.setProgress(i);
    }

    @Override
    public void updateResponseSyncProgressBar(int i) {
        pbSyncingResponse.setProgress(i);
    }

    @Override
    public void updateTotalSyncSurveyProgressBarAndCount(int count) {
        pbTotalSyncSurvey.setProgress(count);
        String total_sync_count = getResources()
                .getString(R.string.space) + count + getResources()
                .getString(R.string.slash) + mSurveyList.size();
        tvTotalProgress.setText(total_sync_count);
    }

    @Override
    public int getMaxSingleSyncSurveyProgressBar() {
        return pbSyncingSurvey.getMax();
    }

    @Override
    public void showNetworkIsNotAvailable() {
        Toast.makeText(getActivity(), getResources().getString(R.string
                .error_network_not_available), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSurveysSyncSuccessfully() {
        btnCancel.setVisibility(View.INVISIBLE);
        dismissDialog();
        Toast.makeText(getActivity(), R.string.sync_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Boolean isOnline() {
        return Network.isOnline(getActivity());
    }

    @Override
    public void dismissDialog() {
        getDialog().dismiss();
    }

    @Override
    public void showDialog() {
        getDialog().show();
    }

    @Override
    public void hideDialog() {
        getDialog().hide();
    }

    @Override
    public void showError(int s) {
        Toaster.show(rootView, s);
    }

    @Override
    public void showProgressbar(boolean b) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSyncSurveysDialogPresenter.detachView();
    }
}