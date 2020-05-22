package com.mifos.mifosxdroid.online.centerdetails;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.activate.ActivateFragment;
import com.mifos.objects.group.CenterInfo;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Rajan Maurya on 05/02/17.
 */
public class CenterDetailsFragment extends MifosBaseFragment implements CenterDetailsMvpView {

    @BindView(R.id.tv_center_activation_date)
    TextView tvActivationDate;

    @BindView(R.id.tv_staff_name)
    TextView tvStaffName;

    @BindView(R.id.tv_meeting_date)
    TextView tvMeetingDate;

    @BindView(R.id.tv_meeting_frequency)
    TextView tvMeetingFrequency;

    @BindView(R.id.tv_active_clients)
    TextView tvActiveClients;

    @BindView(R.id.tv_active_group_loans)
    TextView tvActiveGroupLoans;

    @BindView(R.id.tv_active_client_loans)
    TextView tvActiveClientLoans;

    @BindView(R.id.tv_active_group_borrowers)
    TextView tvActiveGroupBorrowers;

    @BindView(R.id.tv_active_client_borrowers)
    TextView tvActiveClientBorrowers;

    @BindView(R.id.tv_active_overdue_group_loans)
    TextView tvActiveOverdueGroupLoans;

    @BindView(R.id.tv_active_overdue_client_loans)
    TextView tvActiveOverdueClientLoans;

    @BindView(R.id.rl_center)
    RelativeLayout rlCenter;

    @BindView(R.id.ll_bottom_panel)
    LinearLayout llBottomPanel;

    @Inject
    CenterDetailsPresenter centerDetailsPresenter;

    View rootView;

    private int centerId;

    private OnFragmentInteractionListener listener;

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
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            this.centerId = getArguments().getInt(Constants.CENTER_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_center_details, container, false);

        ButterKnife.bind(this, rootView);
        centerDetailsPresenter.attachView(this);

        centerDetailsPresenter.loadCentersGroupAndMeeting(centerId);
        centerDetailsPresenter.loadSummaryInfo(centerId);

        return rootView;
    }

    @OnClick(R.id.btn_activate_center)
    void onClickActivateCenter() {
        ActivateFragment activateFragment =
                ActivateFragment.newInstance(centerId, Constants.ACTIVATE_CENTER);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CENTER_DETAIL);
        fragmentTransaction.replace(R.id.container, activateFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void showProgressbar(boolean show) {
        if (show) {
            rlCenter.setVisibility(View.GONE);
            showMifosProgressBar();
        } else {
            rlCenter.setVisibility(View.VISIBLE);
            hideMifosProgressBar();
        }
    }

    @Override
    public void showCenterDetails(CenterWithAssociations centerWithAssociations) {
        setToolbarTitle(centerWithAssociations.getName());
        if (!centerWithAssociations.getActivationDate().isEmpty()) {
            if (centerWithAssociations.getStaffName() != null) {
                tvStaffName.setText(centerWithAssociations.getStaffName());
            } else {
                tvStaffName.setText(R.string.no_staff);
            }
            tvActivationDate.setText(Utils.getStringOfDate
                    (centerWithAssociations.getActivationDate()));
        }
    }

    @Override
    public void showMeetingDetails(CenterWithAssociations centerWithAssociations) {
        if (!centerWithAssociations.getActive()) {
            llBottomPanel.setVisibility(View.VISIBLE);
            showErrorMessage(R.string.error_center_inactive);
        }
        if (centerWithAssociations.getCollectionMeetingCalendar().getCalendarInstanceId() == null) {
            tvMeetingDate.setText(getString(R.string.unassigned));
            if (getView() != null) {
                getView().findViewById(R.id.row_meeting_frequency).setVisibility(View.GONE);
            }
        } else {
            tvMeetingDate.setText(Utils.getStringOfDate(centerWithAssociations
                            .getCollectionMeetingCalendar().getNextTenRecurringDates().get(0)));
            if (getView() != null) {
                getView().findViewById(R.id.row_meeting_frequency).setVisibility(View.VISIBLE);
                tvMeetingFrequency.setText(centerWithAssociations.getCollectionMeetingCalendar()
                        .getHumanReadable());
            }
        }
    }

    @Override
    public void showSummaryInfo(List<CenterInfo> centerInfos) {
        CenterInfo centerInfo = centerInfos.get(0);
        tvActiveClients.setText(String.valueOf(centerInfo.getActiveClients()));
        tvActiveGroupLoans.setText(String.valueOf(centerInfo.getActiveGroupLoans()));
        tvActiveClientLoans.setText(String.valueOf(centerInfo.getActiveClientLoans()));
        tvActiveClientBorrowers.setText(String.valueOf(centerInfo.getActiveClientBorrowers()));
        tvActiveGroupBorrowers.setText(String.valueOf(centerInfo.getActiveGroupBorrowers()));
        tvActiveOverdueClientLoans.setText(String.valueOf(centerInfo.getOverdueClientLoans()));
        tvActiveOverdueGroupLoans.setText(String.valueOf(centerInfo.getOverdueGroupLoans()));
    }

    @Override
    public void showErrorMessage(int message) {
        Toaster.show(rootView, message);
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
                listener.addCenterSavingAccount(centerId);
                break;
            case R.id.view_group_list:
                listener.loadGroupsOfCenter(centerId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (CenterDetailsFragment.OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " +
                    "OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void addCenterSavingAccount(int centerId);
        void loadGroupsOfCenter(int centerId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        centerDetailsPresenter.detachView();
    }
}
