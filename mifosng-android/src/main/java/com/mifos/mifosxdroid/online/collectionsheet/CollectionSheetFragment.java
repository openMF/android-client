/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.collectionsheet;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.mifos.api.model.BulkRepaymentTransactions;
import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.Payload;
import com.mifos.objects.response.SaveResponse;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.CollectionListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.db.MifosGroup;
import com.mifos.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.adapter.rxjava.HttpException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CollectionSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CollectionSheetFragment extends MifosBaseFragment implements CollectionSheetMvpView {


    public static final String COLLECTION_SHEET_ONLINE = "Collection Sheet Online";
    private static final int MENU_ITEM_SEARCH = 2000;
    private static final int MENU_ITEM_REFRESH = 2001;
    private static final int MENU_ITEM_SAVE = 2002;
    public final String LOG_TAG = getClass().getSimpleName();
    @BindView(R.id.exlv_collection_sheet)
    ExpandableListView expandableListView;

    @Inject
    CollectionSheetPresenter mCollectionSheetPresenter;
    CollectionListAdapter collectionListAdapter;
    private int centerId; // Center for which collection sheet is being generated
    private String dateOfCollection; // Date of Meeting on which collection has to be done.
    private int calendarInstanceId;
    private View rootView;

    public CollectionSheetFragment() {
        // Required empty public constructor
    }

    public static CollectionSheetFragment newInstance(int centerId, String dateOfCollection, int
            calendarInstanceId) {
        CollectionSheetFragment fragment = new CollectionSheetFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CENTER_ID, centerId);
        args.putString(Constants.DATE_OF_COLLECTION, dateOfCollection);
        args.putInt(Constants.CALENDAR_INSTANCE_ID, calendarInstanceId);
        fragment.setArguments(args);
        return fragment;
    }

    //Called from within the Adapters to show changes when payment amounts are updated
    public void refreshFragment() {

        collectionListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            centerId = getArguments().getInt(Constants.CENTER_ID);
            dateOfCollection = getArguments().getString(Constants.DATE_OF_COLLECTION);
            calendarInstanceId = getArguments().getInt(Constants.CALENDAR_INSTANCE_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_collection_sheet, container, false);

        ButterKnife.bind(this, rootView);
        mCollectionSheetPresenter.attachView(this);

        fetchCollectionSheet();

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        MenuItem mItemSearch = menu.add(Menu.NONE, MENU_ITEM_SEARCH, Menu.NONE, getString(R
                .string.search));
//        mItemSearch.setIcon(new IconDrawable(getActivity(), MaterialIcons.md_search)
//                .colorRes(Color.WHITE)
//                .actionBarSize());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mItemSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }


        MenuItem mItemRefresh = menu.add(Menu.NONE, MENU_ITEM_REFRESH, Menu.NONE, getString(R
                .string.refresh));
        mItemRefresh.setIcon(new IconDrawable(getActivity(), MaterialIcons.md_refresh)
                .colorRes(R.color.white)
                .actionBarSize());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mItemRefresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }

        MenuItem mItemSave = menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, getString(R.string
                .save));
        mItemSave.setIcon(new IconDrawable(getActivity(), MaterialIcons.md_save)
                .colorRes(R.color.white)
                .actionBarSize());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mItemSave.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }


        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case MENU_ITEM_REFRESH:
                refreshFragment();
                break;

            case MENU_ITEM_SAVE:
                saveCollectionSheet();
                break;

            case MENU_ITEM_SEARCH:
                break;


        }


        return super.onOptionsItemSelected(item);
    }

    public void fetchCollectionSheet() {

        Payload payload = new Payload();
        payload.setCalendarId(calendarInstanceId);
        payload.setTransactionDate(dateOfCollection);
        payload.setDateFormat("dd-MM-YYYY");

        mCollectionSheetPresenter.loadCollectionSheet(centerId, payload);

    }

    public synchronized void saveCollectionSheet() {

        CollectionSheetPayload collectionSheetPayload = new CollectionSheetPayload();

        List<BulkRepaymentTransactions> bulkRepaymentTransactions = new
                ArrayList<BulkRepaymentTransactions>();

        Iterator iterator = CollectionListAdapter.sRepaymentTransactions.entrySet().iterator();

        while (iterator.hasNext()) {
            HashMap.Entry repaymentTransaction = (HashMap.Entry) iterator.next();
            bulkRepaymentTransactions.add(new BulkRepaymentTransactions((Integer)
                    repaymentTransaction.getKey(), (Double) repaymentTransaction.getValue()));
            iterator.remove();
        }

        collectionSheetPayload.bulkRepaymentTransactions = new
                BulkRepaymentTransactions[bulkRepaymentTransactions.size()];
        bulkRepaymentTransactions.toArray(collectionSheetPayload.bulkRepaymentTransactions);

        collectionSheetPayload.setCalendarId(calendarInstanceId);
        collectionSheetPayload.setTransactionDate(dateOfCollection);
        collectionSheetPayload.setDateFormat("dd-MM-YYYY");

        //Saving Collection Sheet
        mCollectionSheetPresenter.saveCollectionSheet(centerId, collectionSheetPayload);

    }


    @Override
    public void showCollectionSheet(CollectionSheet collectionSheet) {
        Log.i(COLLECTION_SHEET_ONLINE, "Received");
        List<MifosGroup> mifosGroups = collectionSheet.groups;
        collectionListAdapter = new CollectionListAdapter(getActivity(), mifosGroups);
        expandableListView.setAdapter(collectionListAdapter);
    }

    @Override
    public void showCollectionSheetSuccessfullySaved(SaveResponse saveResponse) {
        if (saveResponse != null) {
            Toast.makeText(getActivity(), "Collection Sheet Saved Successfully",
                    Toast
                            .LENGTH_SHORT).show();

        }
    }

    @Override
    public void showFailedToSaveCollectionSheet(HttpException response) {
        if (response != null) {
            if ((response.code() == 400) || (response.code() == 403)) {
                //TODO for now, It is commented
                //MFErrorParser.parseError(response.response().body());
            }
            Toast.makeText(getActivity(), "Collection Sheet could not be saved.",
                    Toast
                            .LENGTH_SHORT).show();
        }
    }

    @Override
    public void showFetchingError(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            showMifosProgressDialog();
        } else {
            hideMifosProgressDialog();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCollectionSheetPresenter.detachView();
    }
}
