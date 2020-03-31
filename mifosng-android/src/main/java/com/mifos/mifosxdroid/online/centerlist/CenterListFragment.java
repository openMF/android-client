/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.centerlist;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.therajanmaurya.sweeterror.SweetUIErrorHandler;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.CentersListAdapter;
import com.mifos.mifosxdroid.core.EndlessRecyclerViewScrollListener;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener.OnItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.dialogfragments.synccenterdialog.SyncCentersDialogFragment;
import com.mifos.mifosxdroid.online.CentersActivity;
import com.mifos.mifosxdroid.online.collectionsheet.CollectionSheetFragment;
import com.mifos.mifosxdroid.online.createnewcenter.CreateNewCenterFragment;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

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
    RecyclerView rvCenters;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.progressbar_center)
    ProgressBar pbCenter;

    @BindView(R.id.layout_error)
    View layoutError;

    @Inject
    CenterListPresenter mCenterListPresenter;

    @Inject
    CentersListAdapter centersListAdapter;

    private View rootView;
    private List<Center> centers;
    private List<Center> selectedCenters;
    private LinearLayoutManager layoutManager;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private SweetUIErrorHandler sweetUIErrorHandler;

    @Override
    public void onItemClick(View childView, int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            Intent centerIntent = new Intent(getActivity(), CentersActivity.class);
            centerIntent.putExtra(Constants.CENTER_ID, centers.get(position).getId());
            startActivity(centerIntent);
        }
    }

    @Override
    public void onItemLongPress(View childView, int position) {
        if (actionMode == null) {
            actionMode = ((MifosBaseActivity) getActivity()).startSupportActionMode
                    (actionModeCallback);
        }
        toggleSelection(position);
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
        selectedCenters = new ArrayList<>();
        actionModeCallback = new ActionModeCallback();
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

        /**
         * This is the LoadMore of the RecyclerView. It called When Last Element of RecyclerView
         * will shown on the Screen.
         */
        rvCenters.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mCenterListPresenter.loadCenters(true, totalItemsCount);
            }
        });

        mCenterListPresenter.loadCenters(false, 0);
        mCenterListPresenter.loadDatabaseCenters();

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
        rvCenters.setLayoutManager(layoutManager);
        rvCenters.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        rvCenters.setHasFixedSize(true);
        centersListAdapter.setContext(getActivity());
        rvCenters.setAdapter(centersListAdapter);
        swipeRefreshLayout.setColorSchemeColors(getActivity()
                .getResources().getIntArray(R.array.swipeRefreshColors));
        swipeRefreshLayout.setOnRefreshListener(this);
        sweetUIErrorHandler = new SweetUIErrorHandler(getActivity(), rootView);
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
        mCenterListPresenter.loadDatabaseCenters();
        if (actionMode != null) actionMode.finish();
    }

    /**
     * OnClick Error Image icon, reload the centers
     */
    @OnClick(R.id.btn_try_again)
    public void reloadOnError() {
        sweetUIErrorHandler.hideSweetErrorLayoutUI(rvCenters, layoutError);
        mCenterListPresenter.loadCenters(false, 0);
        mCenterListPresenter.loadDatabaseCenters();
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
        centersListAdapter.notifyDataSetChanged();
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
        sweetUIErrorHandler.showSweetEmptyUI(getString(R.string.center), getString(message),
                R.drawable.ic_error_black_24dp, rvCenters, layoutError);
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
        String errorMessage = getStringMessage(R.string.failed_to_fetch_centers);
        sweetUIErrorHandler.showSweetErrorUI(errorMessage,
                R.drawable.ic_error_black_24dp, rvCenters, layoutError);
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
            pbCenter.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            pbCenter.setVisibility(View.GONE);
        }
    }

    /**
     * This Method unregister the RecyclerView OnScrollListener and SwipeRefreshLayout
     * and NoClientIcon click event.
     */
    @Override
    public void unregisterSwipeAndScrollListener() {
        rvCenters.clearOnScrollListeners();
        swipeRefreshLayout.setEnabled(false);
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

    /**
     * Toggle the selection state of an item.
     * <p>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        centersListAdapter.toggleSelection(position);
        int count = centersListAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    /**
     * This ActionModeCallBack Class handling the User Event after the Selection of Clients. Like
     * Click of Menu Sync Button and finish the ActionMode
     */
    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String LOG_TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_sync, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_sync:

                    selectedCenters.clear();
                    for (Integer position : centersListAdapter.getSelectedItems()) {
                        selectedCenters.add(centers.get(position));
                    }

                    SyncCentersDialogFragment syncCentersDialogFragment =
                            SyncCentersDialogFragment.newInstance(selectedCenters);
                    FragmentTransaction fragmentTransaction = getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_SYNC);
                    syncCentersDialogFragment.setCancelable(false);
                    syncCentersDialogFragment.show(fragmentTransaction,
                            getResources().getString(R.string.sync_centers));
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            centersListAdapter.clearSelection();
            actionMode = null;
        }
    }
}