/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientcharge;
/**
 * Created by nellyk on 1/22/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
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

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ChargeNameListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.dialogfragments.ChargeDialogFragment;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.client.Response;

//TODO Replace ListView To RecyclerView and Implement offset in Service Class
public class ClientChargeFragment extends MifosBaseFragment implements ClientChargeMvpView {

    public static final int MENU_ITEM_ADD_NEW_CHARGES = 2000;
    @InjectView(R.id.lv_charges)
    ListView lv_charges;
    List<Charges> chargesList = new ArrayList<Charges>();
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private Context context;
    private SharedPreferences sharedPreferences;
    private int clientId;
    private int index = 0;
    private int top = 0;
    private boolean isInfiniteScrollEnabled = false;

    @Inject
    ClientChargePresenter mClientChargePresenter;

    ChargeNameListAdapter chargesNameListAdapter;

    public ClientChargeFragment() {

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

    public static ClientChargeFragment newInstance(int clientId, List<Charges> chargesList,
                                                   boolean isParentFragmentAGroupFragment) {
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
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null)
            clientId = getArguments().getInt(Constants.CLIENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_charge_list, container, false);
        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();
        ButterKnife.inject(this, rootView);
        mClientChargePresenter.attachView(this);

        setToolbarTitle(getString(R.string.charges));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Do Nothing For Now
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        fetchChargesList();

        return rootView;


    }

    public void inflateChargeList() {

        chargesNameListAdapter = new ChargeNameListAdapter(context, chargesList, clientId);
        lv_charges.setAdapter(chargesNameListAdapter);

        if (isInfiniteScrollEnabled) {
            setInfiniteScrollListener(chargesNameListAdapter);
        }


    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuItem menuItemAddNewDocument = menu.add(Menu.NONE, MENU_ITEM_ADD_NEW_CHARGES, Menu
                .NONE, getString(R.string.add_new));
        menuItemAddNewDocument.setIcon(
                ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_action_content_new, null));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            menuItemAddNewDocument.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == MENU_ITEM_ADD_NEW_CHARGES) {
            ChargeDialogFragment chargeDialogFragment = ChargeDialogFragment.newInstance(clientId);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CHARGE_LIST);
            chargeDialogFragment.show(fragmentTransaction, "Charge Dialog Fragment");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    public void fetchChargesList() {

        //Check if ClientListFragment has a clientList
        if (chargesList.size() > 0) {
            inflateChargeList();
        } else {

            //Get a Client Charges List
            mClientChargePresenter.loadCharges(clientId);

        }


    }

    @SuppressWarnings("deprecation")
    public void setInfiniteScrollListener(final ChargeNameListAdapter chargesNameListAdapter) {

        lv_charges.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int
                    visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount >= totalItemCount) {

                    swipeRefreshLayout.setRefreshing(true);

                    mClientChargePresenter.loadMoreClientCharges(clientId);

                }


            }
        });

    }


    public List<Charges> getChargesList() {
        return chargesList;
    }

    public void setChargesList(List<Charges> chargesList) {
        this.chargesList = chargesList;
    }

    public void setInfiniteScrollEnabled(boolean isInfiniteScrollEnabled) {
        this.isInfiniteScrollEnabled = isInfiniteScrollEnabled;
    }

    @Override
    public void showChargesList(Page<Charges> chargesPage) {
        chargesList = chargesPage.getPageItems();
        inflateChargeList();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showFetchingErrorCharges(Response response) {

        if (getActivity() != null) {
            try {
                Log.i("Error", "" + response.getStatus());
                if (response.getStatus() == 401) {
                    Toast.makeText(getActivity(), "Authorization Expired - Please " +
                            "Login Again", Toast.LENGTH_SHORT).show();
                    logout();

                } else {
                    Toast.makeText(getActivity(), "There was some error fetching list" +
                            ".", Toast.LENGTH_SHORT).show();
                }
            } catch (NullPointerException npe) {
                Toast.makeText(getActivity(), "There is some problem with your " +
                        "internet connection.", Toast.LENGTH_SHORT).show();

            }


        }
    }

    @Override
    public void showMoreClientCharges(Page<Charges> chargesPage) {

        chargesList.addAll(chargesPage.getPageItems());
        chargesNameListAdapter.notifyDataSetChanged();
        index = lv_charges.getFirstVisiblePosition();
        View v = lv_charges.getChildAt(0);
        top = (v == null) ? 0 : v.getTop();
        lv_charges.setSelectionFromTop(index, top);
    }

    @Override
    public void showProgressBar(boolean b) {
        if (b) {
            showProgress("Loading Charges");
        } else {
            hideProgress();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mClientChargePresenter.detachView();
    }
}
