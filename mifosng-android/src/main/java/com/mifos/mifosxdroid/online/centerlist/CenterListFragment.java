/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.centerlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.CentersListAdapter;
import com.mifos.mifosxdroid.core.EndlessRecyclerViewScrollListener;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener.OnItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.CentersActivity;
import com.mifos.mifosxdroid.online.collectionsheet.CollectionSheetFragment;
import com.mifos.mifosxdroid.online.createnewcenter.CreateNewCenterFragment;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ishankhanna on 11/03/14.
 * <p/>
 * CenterListFragment Fetching and Showing CenterList in RecyclerView from
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

    @BindView(R.id.rl_error)
    RelativeLayout rlError;

    @Inject
    CenterListPresenter mCenterListPresenter;

    @Inject
    CentersListAdapter centersListAdapter;

    private View rootView;
    private List<Center> centers;
    private LinearLayoutManager layoutManager;

    @Override
    public void onItemClick(View childView, int position) {
        Intent centerIntent = new Intent(getActivity(), CentersActivity.class);
        centerIntent.putExtra(Constants.CENTER_ID, centers.get(position).getId());
        startActivity(centerIntent);
    }

    @Override
    public void onItemLongPress(View childView, int position) {
        mCenterListPresenter.loadCentersGroupAndMeeting(centers.get(position).getId());
    }

    public static CenterListFragment newInstance() {
        CenterListFragment centerListFragment = new CenterListFragment();
        Bundle args = new Bundle();
        centerListFragment.setArguments(args);
        return centerListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        centers = new ArrayList<>();
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_centers_list, container, false);

        ButterKnife.bind(this, rootView);
        mCenterListPresenter.attachView(this);

        //Showing User Interface.
        showUserInterface();

        //Fetching Centers
        mCenterListPresenter.loadCenters(false, 0);

        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * will shown on the Screen.
         */
        rv_centers.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mCenterListPresenter.loadCenters(true, totalItemsCount);
            }
        });

        return rootView;
    }

    /**
     * This Method is setting the UI
     */
    @Override
    public void showUserInterface() {
        setToolbarTitle(getResources().getString(R.string.title_activity_centers));
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

    @OnClick(R.id.fab_create_center)
    void onClickCreateNewCenter() {
        ((MifosBaseActivity) getActivity()).replaceFragment(CreateNewCenterFragment.newInstance(),
                true, R.id.container);
    }

    /**
     * This Method will be called, whenever user will pull down to RefreshLayout.
     */
    @Override
    public void onRefresh() {
        mCenterListPresenter.loadCenters(false, 0);
    }

    /**
     * OnClick Error Image icon, reload the centers
     */
    @OnClick(R.id.noCentersIcon)
    public void reloadOnError() {
        rlError.setVisibility(View.GONE);
        rv_centers.setVisibility(View.VISIBLE);
        mCenterListPresenter.loadCenters(false, 0);
    }

    /**
     * Attaching the this.centers to the CentersListAdapter
     *
     * @param centers List<Center>
     */
    @Override
    public void showCenters(List<Center> centers) {
        this.centers = centers;
        centersListAdapter.setCenters(centers);
    }

    /**
     * Updating the CenterListAdapter
     *
     * @param centers List<Center>
     */
    @Override
    public void showMoreCenters(List<Center> centers) {
        this.centers.addAll(centers);
        centersListAdapter.notifyDataSetChanged();
    }

    /**
     * Showing that Server response is Empty
     *
     * @param message
     */
    @Override
    public void showEmptyCenters(int message) {
        rv_centers.setVisibility(View.GONE);
        rlError.setVisibility(View.VISIBLE);
        mNoCenterText.setText(getStringMessage(message));
    }

    /**
     * This Method for showing simple SeekBar
     *
     * @param message
     */
    @Override
    public void showMessage(int message) {
        Toaster.show(rootView, getStringMessage(message));
    }

    /**
     * This Method for showing the CollectionSheet of Center
     *
     * @param centerWithAssociations
     * @param id
     */
    @Override
    public void showCentersGroupAndMeeting(final CenterWithAssociations centerWithAssociations,
                                           final int id) {
        MFDatePicker mfDatePicker = new MFDatePicker();
        mfDatePicker.setOnDatePickListener(new MFDatePicker.OnDatePickListener() {
            @Override
            public void onDatePicked(String date) {
                if (centerWithAssociations.getCollectionMeetingCalendar().getId() != null) {
                    ((MifosBaseActivity) getActivity())
                            .replaceFragment(CollectionSheetFragment.newInstance(id, date,
                                    centerWithAssociations.getCollectionMeetingCalendar().getId()),
                                    true, R.id.container);

                } else {
                    showMessage(R.string.no_meeting_found);
                }
            }
        });
        mfDatePicker.show(getActivity().getSupportFragmentManager(), MFDatePicker.TAG);
    }

    /**
     * If Loading Centers is failed on first request then show to user a message that center failed
     * to load.
     */
    @Override
    public void showFetchingError() {
        rv_centers.setVisibility(View.GONE);
        rlError.setVisibility(View.VISIBLE);
        String errorMessage = getStringMessage(R.string.failed_to_fetch_centers)
                + getStringMessage(R.string.new_line) + getStringMessage(R.string.click_to_refresh);
        mNoCenterText.setText(errorMessage);
    }

    /**
     * This Method for showing Progress bar if the Center count is zero otherwise
     * shows swipeRefreshLayout
     *
     * @param show Boolean
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
    public void onDestroyView() {
        super.onDestroyView();
        mCenterListPresenter.detachView();
    }
}