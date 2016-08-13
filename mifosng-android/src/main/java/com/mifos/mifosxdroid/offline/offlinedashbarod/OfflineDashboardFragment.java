package com.mifos.mifosxdroid.offline.offlinedashbarod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.OfflineDashboardAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadActivity;
import com.mifos.mifosxdroid.offline.syncgrouppayloads.SyncGroupPayloadsActivity;
import com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition.SyncLoanRepaymentTransactionActivity;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.group.GroupPayload;
import com.mifos.utils.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This Fragment is the Dashboard of the Offline sync of Clients, Groups, LoanRepayment etc.
 * In which presenter request the DataManager to get the offline created clients or groups or
 * LoanRepayment from DatabaseHelper and get the List of Groups, clients and LoanRepayment etc.
 *
 * if DataManager Response List is not equal to zero then add the Card Name and List Count in
 * OfflineDashboardAdapter.showCard(String cardName, String cardCount) and add the Class in
 * List<Class> mPayloadClasses, which will open onclick the card.
 *
 * mPayloadIndex, this is the counter value of the number of request we are making to the
 * DataManager to load the Clients, Groups, LoanRepayment etc list from DatabaseHelper
 *
 * SYNC_CARD_UI_NAMES is array of the card UI names, which contains the all possible name of cards.
 * due to this array we have maintain the single method in
 * OfflineDashboardAdapter.showCard(String cardName, String cardCount) and update the list.
 *
 * if DatabaseHelper returns the List size zero for all Clients, Groups, LoanRepayment etc
 * then showNoPayloadToShow() and Nothing to Sync.
 *
 * Created by Rajan Maurya on 20/07/16.
 */
public class OfflineDashboardFragment extends MifosBaseFragment implements
        OfflineDashboardMvpView, RecyclerItemClickListener.OnItemClickListener {

    public final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.rv_offline_dashboard)
    RecyclerView rv_offline_dashboard;

    @BindView(R.id.pb_offline_dashboard)
    ProgressBar pb_offline_dashboard;

    @BindView(R.id.noPayloadText)
    TextView mNoPayloadText;

    @BindView(R.id.noPayloadIcon)
    ImageView mNoPayloadIcon;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    View rootView;

    @Inject
    OfflineDashboardPresenter mOfflineDashboardPresenter;

    @Inject
    OfflineDashboardAdapter mOfflineDashboardAdapter;

    // update mPayloadIndex to number of request is going to fetch data in Presenter;
    private int mPayloadIndex = 3;

    private static final int GRID_COUNT = 2;

    private List<Class> mPayloadClasses;

    public static final int[] SYNC_CARD_UI_NAMES = {R.string.sync_clients,
            R.string.sync_groups, R.string.sync_loanrepayments};


    @Override
    public void onItemClick(View childView, int position) {
        startPayloadActivity(mPayloadClasses.get(position));
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

    public static OfflineDashboardFragment newInstance() {
        OfflineDashboardFragment fragment = new OfflineDashboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPayloadClasses = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_offline_dashboard, container, false);

        setToolbarTitle(getActivity().getResources().getString(R.string.offline));

        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        mOfflineDashboardPresenter.attachView(this);
        ButterKnife.bind(this, rootView);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), GRID_COUNT);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_offline_dashboard.setLayoutManager(mLayoutManager);
        rv_offline_dashboard.setHasFixedSize(true);
        rv_offline_dashboard.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                this));
        rv_offline_dashboard.setItemAnimator(new DefaultItemAnimator());
        rv_offline_dashboard.addItemDecoration(new ItemOffsetDecoration(getActivity(),
                R.dimen.item_offset));
        rv_offline_dashboard.setAdapter(mOfflineDashboardAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mOfflineDashboardAdapter.removeAllCards();
        mPayloadClasses.clear();
        mPayloadIndex = 3;

        mOfflineDashboardPresenter.loadDatabaseClientPayload();
        mOfflineDashboardPresenter.loadDatabaseGroupPayload();
        mOfflineDashboardPresenter.loadDatabaseLoanRepaymentTransactions();
    }

    /**
     * This method set the response of DataManager from DatabaseHelper that if List<ClientPayload>
     * Size is zero the decrease the value of mPayloadIndex by 1 and if size is not equal to zero
     * the update the adapter and add the Card UI name and size() of the List to sync.
     *
     * @param clientPayloads List<ClientPayload> from DatabaseHelperClient
     */
    @Override
    public void showClients(List<ClientPayload> clientPayloads) {
        if (clientPayloads.size() != 0) {
            mOfflineDashboardAdapter.showCard(getActivity()
                    .getResources().getString(R.string.payloads_count) +
                    clientPayloads.size(), SYNC_CARD_UI_NAMES[0]);
            mPayloadClasses.add(SyncClientPayloadActivity.class);
        } else {
            mPayloadIndex = mPayloadIndex - 1;
            showNoPayloadToShow();
        }
    }

    /**
     * This method set the response of DataManager from DatabaseHelper that if List<GroupsPayload>
     * Size is zero, then decrease the value of mPayloadIndex by 1 and if size is not equal to zero
     * the update the adapter and add the Card UI name and size() of the List to sync.
     *
     * @param groupPayloads List<GroupPayload> from DatabaseHelperGroup
     */
    @Override
    public void showGroups(List<GroupPayload> groupPayloads) {
        if (groupPayloads.size() != 0) {
            mOfflineDashboardAdapter.showCard(getActivity()
                    .getResources().getString(R.string.payloads_count) +
                    groupPayloads.size(), SYNC_CARD_UI_NAMES[1]);
            mPayloadClasses.add(SyncGroupPayloadsActivity.class);
        } else {
            mPayloadIndex = mPayloadIndex - 1;
            showNoPayloadToShow();
        }
    }


    /**
     * This method set the response of DataManager from DatabaseHelper that if
     * List<LoanRepaymentRequest> Size is zero, then decrease the value of mPayloadIndex by 1 and
     * if size is not equal to zero the update the adapter and add the Card UI name and size() of
     * the List to sync.
     *
     * @param loanRepaymentRequests List<LoanRepaymentRequest> from DatabaseHelperLoan
     */
    @Override
    public void showLoanRepaymentTransactions(List<LoanRepaymentRequest> loanRepaymentRequests) {
        if (loanRepaymentRequests.size() != 0) {
            mOfflineDashboardAdapter.showCard(getActivity().getResources()
                    .getString(R.string.transactions_count) +
                    loanRepaymentRequests.size(), SYNC_CARD_UI_NAMES[2]);
            mPayloadClasses.add(SyncLoanRepaymentTransactionActivity.class);
        } else {
            mPayloadIndex = mPayloadIndex - 1;
            showNoPayloadToShow();
        }
    }


    /**
     * This Method setting the main UI RecyclerView.setVisibility(View.GONE) and Set text TO
     * TextView that Nothing to Sync when mPayloadIndex = 0. It means there is nothing in the
     * Database to sync to the Server.
     */
    @Override
    public void showNoPayloadToShow() {
        if (mPayloadIndex == 0) {
            rv_offline_dashboard.setVisibility(View.GONE);
            ll_error.setVisibility(View.VISIBLE);
            mNoPayloadText.setText(getActivity()
                    .getResources().getString(R.string.nothing_to_sync));
            mNoPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        }
    }

    @Override
    public void showError(int stringId) {
        Toaster.show(rootView, getResources().getString(stringId));
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            pb_offline_dashboard.setVisibility(View.VISIBLE);
        } else {
            pb_offline_dashboard.setVisibility(View.GONE);
        }
    }


    /**
     * This is the Generic Type method for starting the activity.
     * @param t Activity Class that is user wants to start
     * @param <T>
     */
    public <T> void startPayloadActivity(Class<T> t) {
        Intent intent = new Intent(getActivity(), t);
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mOfflineDashboardPresenter.detachView();
    }

}
