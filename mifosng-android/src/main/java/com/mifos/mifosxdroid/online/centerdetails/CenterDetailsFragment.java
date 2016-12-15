package com.mifos.mifosxdroid.online.centerdetails;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.utils.Constants;
import com.mifos.utils.Utils;

import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ilya on 12/13/16.
 */

public class CenterDetailsFragment extends MifosBaseFragment implements CenterDetailsMvpView {

    @BindView(R.id.tv_center_activation_date)
    TextView tv_activationDate;

    @BindView(R.id.tv_staff_name)
    TextView tv_staffName;

    @BindView(R.id.tv_meeting_date)
    TextView tv_meetingDate;

    @BindView(R.id.tv_meeting_frequency)
    TextView tv_meetingFrequency;

    @BindView(R.id.tv_active_clients)
    TextView tv_activeClients;

    @BindView(R.id.tv_active_group_loans)
    TextView tv_activeGroupLoans;

    @BindView(R.id.tv_active_client_loans)
    TextView tv_activeClientLoans;

    @BindView(R.id.tv_active_group_borrowers)
    TextView tv_activeGroupBorrowers;

    @BindView(R.id.tv_active_client_borrowers)
    TextView tv_activeClientBorrowers;

    @BindView(R.id.tv_active_overdue_group_loans)
    TextView tv_activeOverdueGroupLoans;

    @BindView(R.id.tv_active_overdue_client_loans)
    TextView tv_activeOverdueClientLoans;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.container)
    ScrollView scrollView;

    @Inject
    CenterDetailsPresenter mCenterDetailsPresenter;

    private int centerId;

    private OnFragmentInteractionListener mListener;

    public static CenterDetailsFragment newInstance(int centerId) {
        CenterDetailsFragment fragment = new CenterDetailsFragment();

        Bundle args = new Bundle();
        args.putInt(Constants.CENTER_ID, centerId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        this.centerId = getArguments().getInt(Constants.CENTER_ID);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_center_details, container, false);

        ButterKnife.bind(this, rootView);
        mCenterDetailsPresenter.attachView(this);

        mCenterDetailsPresenter.loadCentersGroupAndMeeting(centerId);
        mCenterDetailsPresenter.loadSummaryInfo(centerId);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_center, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();
        switch (menuItemId) {
            case R.id.add_savings_account:
                mListener.addCenterSavingAccount(centerId);
                break;
            case R.id.view_group_list:
                mListener.loadGroupsOfCenter(centerId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void showProgressbar(boolean flag) {
        if (flag) {
            progressBar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showSummaryInfo(Map<String, Integer> summaryInfo) {
        tv_activeClients.setText(String.format
                (Locale.getDefault(), "%d", summaryInfo.get("activeClients")));
        tv_activeGroupLoans.setText(String.format
                (Locale.getDefault(), "%d", summaryInfo.get("activeGroupLoans")));
        tv_activeClientLoans.setText(String.format
                (Locale.getDefault(), "%d", summaryInfo.get("activeClientLoans")));
        tv_activeClientBorrowers.setText(String.format
                (Locale.getDefault(), "%d", summaryInfo.get("activeClientBorrowers")));
        tv_activeGroupBorrowers.setText(String.format
                (Locale.getDefault(), "%d", summaryInfo.get("activeGroupBorrowers")));
        tv_activeOverdueClientLoans.setText(String.format
                (Locale.getDefault(), "%d", summaryInfo.get("overdueClientLoans")));
        tv_activeOverdueGroupLoans.setText(String.format
                (Locale.getDefault(), "%d", summaryInfo.get("overdueGroupLoans")));
    }

    @Override
    public void showCenterDetails(CenterWithAssociations centerWithAssociations) {
        setToolbarTitle(getString(R.string.center) + "-" + centerWithAssociations.getName());
        tv_staffName.setText(centerWithAssociations.getStaffName());
        tv_activationDate.setText(Utils.getStringOfDate(getContext(),
                               centerWithAssociations.getActivationDate()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (CenterDetailsFragment.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " +
                    "OnFragmentInteractionListener");
        }
    }

    @Override
    public void showMeetingDetails(CenterWithAssociations centerWithAssociations) {
        if (centerWithAssociations.getCollectionMeetingCalendar().getCalendarInstanceId() == null) {
            tv_meetingDate.setText("Unassigned");
            if (getView() != null) {
                getView().findViewById(R.id.row_meeting_frequency).setVisibility(View.GONE);
            }
        } else {
            tv_meetingDate.setText(Utils.getStringOfDate(getContext(),
                    centerWithAssociations.getCollectionMeetingCalendar().getNextTenRecurringDates()
                            .get(0)));
            if (getView() != null) {
                getView().findViewById(R.id.row_meeting_frequency).setVisibility(View.VISIBLE);

                tv_meetingFrequency.setText(centerWithAssociations
                        .getCollectionMeetingCalendar().getHumanReadable());
            }
        }
    }

    public interface OnFragmentInteractionListener {

        void addCenterSavingAccount(int centerId);

        void loadGroupsOfCenter(int centerId);
    }
}
