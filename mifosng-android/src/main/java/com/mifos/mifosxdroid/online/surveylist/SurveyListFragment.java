/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.surveylist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.SurveyListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.survey.Survey;
import com.mifos.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This Class shows the List of Surveys after fetching the surveys list from the REST API :
 * https://demo.openmf.org/fineract-provider/api/v1/surveys
 * <p/>
 * Created by Nasim Banu on 27,January,2016.
 */
public class SurveyListFragment extends ProgressableFragment implements SurveyListMvpView {

    @BindView(R.id.lv_surveys_list)
    ListView lv_surveys_list;

    @BindView(R.id.tv_survey_name)
    TextView surveySelectText;

    @Inject
    SurveyListPresenter mSurveyListPresenter;

    private OnFragmentInteractionListener mListener;
    private View rootView;
    private int clientId;

    public static SurveyListFragment newInstance(int clientId) {
        SurveyListFragment fragment = new SurveyListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CLIENT_ID, clientId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            clientId = getArguments().getInt(Constants.CLIENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_survey_list, container, false);

        ButterKnife.bind(this, rootView);
        mSurveyListPresenter.attachView(this);

        mSurveyListPresenter.loadSurveyList();

        return rootView;
    }

    @Override
    public void showAllSurvey(final List<Survey> surveys) {
        if (surveys.size() == 0) {
            surveySelectText.setText(getResources().
                    getString(R.string.no_survey_available_for_client));
        } else {
            SurveyListAdapter surveyListAdapter = new SurveyListAdapter(getActivity(), surveys);
            lv_surveys_list.setAdapter(surveyListAdapter);
            lv_surveys_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                        long l) {
                    mListener.loadSurveyQuestion(surveys.get(position), clientId);
                }
            });
        }
    }

    @Override
    public void showFetchingError(int errorMessage) {
        Toaster.show(rootView, getResources().getString(errorMessage));
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mItem_search)
            getActivity().finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = context instanceof Activity ? (Activity) context : null;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " +
                    "OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSurveyListPresenter.detachView();
    }

    public interface OnFragmentInteractionListener {

        void loadSurveyQuestion(Survey survey, int clientId);
    }

}
