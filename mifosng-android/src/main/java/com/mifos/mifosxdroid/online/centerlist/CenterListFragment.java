/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.centerlist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.CentersListAdapter;
import com.mifos.mifosxdroid.core.EndlessRecyclerOnScrollListener;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener.OnItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ishankhanna on 11/03/14.
 * <p/>
 * CenterListFragment Fetching Showing CenterList in RecyclerView from
 * </>demo.openmf.org/fineract-provider/api/v1/centers?paged=true&offset=0&limit=100</>
 */
public class CenterListFragment extends MifosBaseFragment
        implements CenterListMvpView, OnItemClickListener, OnRefreshListener {

    @BindView(R.id.rv_center_list)
    RecyclerView rv_centers;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.progressbar_center)
    ProgressBar pb_center;

    @BindView(R.id.noCenterText)
    TextView mNoCenterText;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    @Inject
    CenterListPresenter mCenterListPresenter;


    CentersListAdapter centersListAdapter;;

    private View rootView;
    private OnFragmentInteractionListener mListener;
    private List<Center> centers;
    private LinearLayoutManager layoutManager;

    @Override
    public void onItemClick(View childView, int position) {
        mListener.loadGroupsOfCenter(centers.get(position).getId());
    }

    @Override
    public void onItemLongPress(View childView, int position) {
        mCenterListPresenter.loadCentersGroupAndMeeting(centers.get(position).getId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        centers = new ArrayList<>();
        centersListAdapter = new CentersListAdapter();
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_centers_list, container, false);

        setToolbarTitle(getResources().getString(R.string.title_activity_centers));

        ButterKnife.bind(this, rootView);
        mCenterListPresenter.attachView(this);

        //Showing User Interface.
        showUserInterface();

        mCenterListPresenter.loadCenters(false, 0);
        mCenterListPresenter.loadDatabaseCenters();

        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * is shown on the Screen.
         * Increase the mApiRestCounter by 1 and Send Api Request to Server with Paged(True)
         * and offset(mCenterList.size()) and limit(100).
         */
        rv_centers.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                mCenterListPresenter.loadCenters(true, centers.size());
            }
        });

        return rootView;
    }

    @Override
    public void showUserInterface() {
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_centers.setLayoutManager(layoutManager);
        rv_centers.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        rv_centers.setHasFixedSize(true);
        centersListAdapter.setContext(getActivity());
        rv_centers.setAdapter(centersListAdapter);
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        mCenterListPresenter.loadCenters(false, 0);
        mCenterListPresenter.loadDatabaseCenters();
    }

    /**
     * Shows When mApiRestValue is 1 and Server Response is Null.
     * Onclick Send Fresh Request for Center list.
     */
    @OnClick(R.id.noCentersIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        rv_centers.setVisibility(View.VISIBLE);
        mCenterListPresenter.loadCenters(false, 0);
        mCenterListPresenter.loadDatabaseCenters();
    }

    @Override
    public void showCenters(List<Center> centers) {
        this.centers = centers;
        centersListAdapter.setCenters(centers);
    }


    @Override
    public void showMoreCenters(List<Center> centers) {
        this.centers.addAll(centers);
        centersListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyCenters(int message) {
        rv_centers.setVisibility(View.GONE);
        ll_error.setVisibility(View.VISIBLE);
        mNoCenterText.setText(getStringMessage(message));
    }

    @Override
    public void showMessage(int message) {
        Toaster.show(rootView, getStringMessage(message));
    }

    @Override
    public void showCentersGroupAndMeeting(
            final CenterWithAssociations centerWithAssociations, final int id) {

        MFDatePicker mfDatePicker = new MFDatePicker();
        mfDatePicker.setOnDatePickListener(new MFDatePicker
                .OnDatePickListener() {
            @Override
            public void onDatePicked(String date) {
                mListener.loadCollectionSheetForCenter(id, date,
                        centerWithAssociations
                                .getCollectionMeetingCalendar()
                                .getId());
            }
        });
        mfDatePicker.show(getActivity().getSupportFragmentManager(),
                MFDatePicker.TAG);

    }

    @Override
    public void showFetchingError() {
        rv_centers.setVisibility(View.GONE);
        ll_error.setVisibility(View.VISIBLE);
        String errorMessage = getStringMessage(R.string.failed_to_fetch_groups)
                + getStringMessage(R.string.new_line) + getStringMessage(R.string.click_to_refresh);
        mNoCenterText.setText(errorMessage);
    }

    /**
     * Check mApiRestCounter value, if the value is 1 then
     * show MifosBaseActivity ProgressBar and if it is greater than 1,
     * It means this Request is the second and so on than show SwipeRefreshLayout
     * Check the the b is true or false
     *
     * @param show is the status of the progressbar
     */
    @Override
    public void showProgressbar(boolean show) {
        swipeRefreshLayout.setRefreshing(show);
        if (show && centersListAdapter.getItemCount() == 0) {
            pb_center.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            pb_center.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mItem_search)
            getActivity().finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        mCenterListPresenter.detachView();
    }


    public interface OnFragmentInteractionListener {
        void loadGroupsOfCenter(int centerId);

        void loadCollectionSheetForCenter(int centerId, String collectionDate, int
                calenderInstanceId);
    }
}