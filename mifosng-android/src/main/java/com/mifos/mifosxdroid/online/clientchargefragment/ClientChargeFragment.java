/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientchargefragment;
/**
 * Created by nellyk on 1/22/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.mifos.App;
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ChargeNameListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.dialogfragments.ChargeDialogFragment;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;



public class ClientChargeFragment extends MifosBaseFragment implements ClientChargeMvpView{

    public static final int MENU_ITEM_ADD_NEW_CHARGES = 2000;
    @InjectView(R.id.lv_charges)
    ListView lv_charges;
    List<Charges> chargesList = new ArrayList<Charges>();
    private View rootView;
    private Context context;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private int clientId;
    private int index = 0;
    private int top = 0;
    private boolean isInfiniteScrollEnabled = false;
    public DataManager mDatamanager;
    public ClientChargePresenter mClientChargePresenter;
    public ChargeNameListAdapter chargesNameListAdapter;

    public ClientChargeFragment(){

    }


    public static ClientChargeFragment newInstance(int clientId, List<Charges> chargesList) {
        ClientChargeFragment fragment = new ClientChargeFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CLIENT_ID, clientId);
        fragment.setArguments(args);
        if (chargesList != null)
            fragment.setChargesList(chargesList);
        return fragment;
    }
    public static ClientChargeFragment newInstance(int clientId,List<Charges> chargesList, boolean isParentFragmentAGroupFragment) {
        ClientChargeFragment fragment = new ClientChargeFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CLIENT_ID, clientId);
        fragment.setArguments(args);
        fragment.setChargesList(chargesList);
        if (isParentFragmentAGroupFragment) {
            fragment.setInfiniteScrollEnabled(false);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            clientId = getArguments().getInt(Constants.CLIENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_charge_list, container, false);
        setHasOptionsMenu(true);
        mDatamanager = new DataManager();
        mClientChargePresenter = new ClientChargePresenter(mDatamanager);
        mClientChargePresenter.attachView(this);
        context = getActivity().getApplicationContext();
        ButterKnife.inject(this, rootView);
        setToolbarTitle(getString(R.string.charges));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(true);
                mClientChargePresenter.loadclientchargeslist(clientId);
                hideProgress();
                if(chargesList != null)
                    chargesList.clear();
            }
        });

        fetchChargesList();

        return rootView;


    }

    public void inflateChargeList() {

        chargesNameListAdapter = new ChargeNameListAdapter(context, chargesList,clientId);
        lv_charges.setAdapter(chargesNameListAdapter);

        if (isInfiniteScrollEnabled) {
            setInfiniteScrollListener();
        }


    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuItem menuItemAddNewDocument = menu.add(Menu.NONE, MENU_ITEM_ADD_NEW_CHARGES, Menu.NONE, getString(R.string.add_new));
        menuItemAddNewDocument.setIcon(getResources().getDrawable(R.drawable.ic_action_content_new));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            menuItemAddNewDocument.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == MENU_ITEM_ADD_NEW_CHARGES) {
            ChargeDialogFragment chargeDialogFragment = ChargeDialogFragment.newInstance(clientId);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CHARGE_LIST);
            chargeDialogFragment.show(fragmentTransaction, "Charge Dialog Fragment");
        }

        return super.onOptionsItemSelected(item);
    }

    public void fetchChargesList() {
        //Check if ClientListFragment has a clientList
        if (chargesList.size() > 0) {
            inflateChargeList();
        } else {
            if(!swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(true);
            mClientChargePresenter.loadclientchargeslist(clientId);
        }
    }

    public void setInfiniteScrollListener() {

        lv_charges.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    swipeRefreshLayout.setRefreshing(true);
                    mClientChargePresenter.loadmoreclientchargeslist(clientId);

                }


            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mClientChargePresenter.detachView();
    }

    public void setChargesList(List<Charges> chargesList) {
        this.chargesList = chargesList;
    }
    public void setInfiniteScrollEnabled(boolean isInfiniteScrollEnabled) {
        this.isInfiniteScrollEnabled = isInfiniteScrollEnabled;
    }

    @Override
    public void showClientChargesProgressBar(boolean status) {
        if(status){
            showProgress();
        }else {
            hideProgress();
        }
    }

    @Override
    public void showClientChargesList(Page<Charges> chargesPage) {

        chargesList = chargesPage.getPageItems();
        inflateChargeList();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showClientChargesListFetchError() {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), "There was some error fetching list .", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showMoreClientChargesList(Page<Charges> chargesPage) {
        chargesList.addAll(chargesPage.getPageItems());
        chargesNameListAdapter.notifyDataSetChanged();
        index = lv_charges.getFirstVisiblePosition();
        View v = lv_charges.getChildAt(0);
        top = (v == null) ? 0 : v.getTop();
        lv_charges.setSelectionFromTop(index, top);
        swipeRefreshLayout.setRefreshing(false);
    }
}
