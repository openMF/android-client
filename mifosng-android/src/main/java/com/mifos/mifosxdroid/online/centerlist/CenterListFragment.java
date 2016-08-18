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
import com.mifos.objects.client.Page;
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
        implements CenterListMvpView, OnItemClickListener {


    @BindView(R.id.rv_center_list)
    RecyclerView rv_centers;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noCenterText)
    TextView mNoCenterText;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    @Inject
    CenterListPresenter mCenterListPresenter;

    private View rootView;
    private CentersListAdapter centersListAdapter;
    private OnFragmentInteractionListener mListener;
    private List<Center> mCentersList;
    private LinearLayoutManager layoutManager;
    private int mApiRestCounter;
    private int limit = 100;

    @Override
    public void onItemClick(View childView, int position) {
        mListener.loadGroupsOfCenter(mCentersList.get(position).getId());
    }

    @Override
    public void onItemLongPress(View childView, int position) {
        mCenterListPresenter.loadCentersGroupAndMeeting(
                mCentersList.get(position).getId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCentersList = new ArrayList<>();
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_centers_list, container, false);

        setToolbarTitle(getResources().getString(R.string.title_activity_centers));

        ButterKnife.bind(this, rootView);
        mCenterListPresenter.attachView(this);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_centers.setLayoutManager(layoutManager);
        rv_centers.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        rv_centers.setHasFixedSize(true);

        mApiRestCounter = 1;
        mCenterListPresenter.loadCenters(true, 0, limit);


        /**
         * Setting mApiRestCounter to 1 and send Fresh Request to Server
         */
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_light, R.color.green_light, R
                .color.orange_light, R.color.red_light);
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                mApiRestCounter = 1;

                mCenterListPresenter.loadCenters(true, 0, limit);

                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });


        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * is shown on the Screen.
         * Increase the mApiRestCounter by 1 and Send Api Request to Server with Paged(True)
         * and offset(mCenterList.size()) and limit(100).
         */
        rv_centers.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                mApiRestCounter = mApiRestCounter + 1;
                mCenterListPresenter.loadCenters(true, mCentersList.size(), limit);
            }
        });

        return rootView;
    }


    /**
     * Shows When mApiRestValue is 1 and Server Response is Null.
     * Onclick Send Fresh Request for Center list.
     */
    @OnClick(R.id.noCentersIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        mCenterListPresenter.loadCenters(true, 0, limit);
    }


    /**
     * Setting Data in RecyclerView of the CenterListFragment if the mApiRestCounter value is 1,
     * otherwise adding value in ArrayList and updating the CenterListAdapter.
     * If the Response is have null then show Toast to User There is No Center Available.
     *
     * @param centerPage is the List<Center> and
     *                   TotalValue of center API Response by Server
     */
    @Override
    public void showCenters(Page<Center> centerPage) {

        /**
         * if mApiRestCounter is 1, So this is the first Api Request.
         * else if mApiRestCounter is greater than 1, SO this is for loadmore request.
         */
        if (mApiRestCounter == 1) {
            mCentersList = centerPage.getPageItems();
            centersListAdapter = new CentersListAdapter(getActivity(), mCentersList);
            rv_centers.setAdapter(centersListAdapter);

            ll_error.setVisibility(View.GONE);
        } else {

            mCentersList.addAll(centerPage.getPageItems());
            centersListAdapter.notifyDataSetChanged();

            //checking the response size if size is zero then show toast No More
            // Clients Available for fetch
            if (centerPage.getPageItems().size() == 0 &&
                    (centerPage.getTotalFilteredRecords() == mCentersList.size()))
                Toaster.show(rootView, "No more Center Available");
        }


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

    /**
     * Check the mApiRestCounter value is the value is 1,
     * So there no data to show and setVisibility VISIBLE
     * of Error ImageView and TextView layout and otherwise simple
     * show the Toast Message of Error Message.
     *
     * @param s is the Error Message given by CenterListPresenter
     */
    @Override
    public void showFetchingError(String s) {

        if (mApiRestCounter == 1) {
            ll_error.setVisibility(View.VISIBLE);
            mNoCenterText.setText(s + "\n Click to Refresh ");
        }

        Toaster.show(rootView, s);
    }


    /**
     * Check mApiRestCounter value, if the value is 1 then
     * show MifosBaseActivity ProgressBar and if it is greater than 1,
     * It means this Request is the second and so on than show SwipeRefreshLayout
     * Check the the b is true or false
     *
     * @param b is the status of the progressbar
     */
    @Override
    public void showProgressbar(boolean b) {

        if (mApiRestCounter == 1) {
            if (b) {
                showMifosProgressBar();
            } else {
                hideMifosProgressBar();
            }
        } else {
            swipeRefreshLayout.setRefreshing(b);
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