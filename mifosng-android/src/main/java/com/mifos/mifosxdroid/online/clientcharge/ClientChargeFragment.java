/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientcharge;
/**
 * Created by nellyk on 1/22/2016.
 */

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ChargeNameListAdapter;
import com.mifos.mifosxdroid.core.EndlessRecyclerOnScrollListener;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.dialogfragments.chargedialog.ChargeDialogFragment;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClientChargeFragment extends MifosBaseFragment implements ClientChargeMvpView,
        RecyclerItemClickListener.OnItemClickListener {

    public static final int MENU_ITEM_ADD_NEW_CHARGES = 2000;

    @BindView(R.id.rv_charge)
    RecyclerView rv_charges;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noChargesText)
    TextView mNoChargesText;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    List<Charges> chargesList = new ArrayList<>();

    @Inject
    ClientChargePresenter mClientChargePresenter;

    ChargeNameListAdapter mChargesNameListAdapter;

    private View rootView;
    private int clientId;
    private int mApiRestCounter;
    private int limit = 10;

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
        return fragment;
    }

    @Override
    public void onItemClick(View childView, int position) {

    }

    @Override
    public void onItemLongPress(View childView, int position) {

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


        ButterKnife.bind(this, rootView);
        mClientChargePresenter.attachView(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_charges.setLayoutManager(layoutManager);
        rv_charges.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        rv_charges.setHasFixedSize(true);

        setToolbarTitle(getString(R.string.charges));

        mApiRestCounter = 1;
        mClientChargePresenter.loadCharges(clientId, 0, limit);

        /**
         * Setting mApiRestCounter to 1 and send Refresh Request to Server
         */
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_light, R.color.green_light, R
                .color.orange_light, R.color.red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mApiRestCounter = 1;

                mClientChargePresenter.loadCharges(clientId, 0, limit);

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
        rv_charges.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {

                mApiRestCounter = mApiRestCounter + 1;
                mClientChargePresenter.loadCharges(clientId, chargesList.size(), limit);
            }
        });

        return rootView;
    }

    /**
     * Shows When mApiRestValue is 1 and Server Response is Null.
     * Onclick Send Fresh Request for Center list.
     */
    @OnClick(R.id.noChargesIcon)
    public void reloadOnError() {
        ll_error.setVisibility(View.GONE);
        mClientChargePresenter.loadCharges(clientId, 0, limit);
    }


    public void setChargesList(List<Charges> chargesList) {
        this.chargesList = chargesList;
    }


    @Override
    public void showChargesList(Page<Charges> chargesPage) {

        /**
         * if mApiRestCounter is 1, So this is the first Api Request.
         * else if mApiRestCounter is greater than 1, SO this is for loadmore request.
         */
        if (mApiRestCounter == 1) {
            chargesList = chargesPage.getPageItems();
            mChargesNameListAdapter = new ChargeNameListAdapter(getActivity(),
                    chargesList, clientId);
            rv_charges.setAdapter(mChargesNameListAdapter);

            ll_error.setVisibility(View.GONE);
        } else {

            chargesList.addAll(chargesPage.getPageItems());
            mChargesNameListAdapter.notifyDataSetChanged();

            //checking the response size if size is zero then show toast No More
            // Clients Available for fetch
            if (chargesPage.getPageItems().size() == 0 &&
                    (chargesPage.getTotalFilteredRecords() == chargesList.size()))
                Toaster.show(rootView, "No more Charges Available");
        }

    }

    @Override
    public void showFetchingErrorCharges(String s) {

        if (mApiRestCounter == 1) {
            ll_error.setVisibility(View.VISIBLE);
            mNoChargesText.setText(s + "\n Click to Refresh ");
        }

        Toaster.show(rootView, s);

    }


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
    public void onDestroyView() {
        super.onDestroyView();
        mClientChargePresenter.detachView();
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

}
