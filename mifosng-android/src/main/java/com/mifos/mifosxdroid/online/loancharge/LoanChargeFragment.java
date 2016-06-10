/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.loancharge;
/**
 * Created by nellyk on 1/22/2016.
 */

import android.content.Context;
import android.content.Intent;
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

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ChargeNameListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.dialogfragments.loanchargedialog.LoanChargeDialogFragment;
import com.mifos.mifosxdroid.login.LoginActivity;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Page;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.BindView;
import retrofit.client.Response;


public class LoanChargeFragment extends MifosBaseFragment implements LoanChargeMvpView {

    public static final int MENU_ITEM_ADD_NEW_LOAN_CHARGES = 3000;

    @BindView(R.id.lv_charges)
    ListView lv_charges;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    LoanChargePresenter mLoanChargePresenter;
    List<Charges> chargesList = new ArrayList<Charges>();
    private ChargeNameListAdapter mChargesNameListAdapter;
    private View rootView;
    private Context context;
    private SharedPreferences sharedPreferences;
    private int loanAccountNumber;
    private int index = 0;
    private int top = 0;
    private boolean isInfiniteScrollEnabled = false;

    public LoanChargeFragment() {

    }


    public static LoanChargeFragment newInstance(int loanAccountNumber, List<Charges> chargesList) {
        LoanChargeFragment fragment = new LoanChargeFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber);
        fragment.setArguments(args);
        if (chargesList != null)
            fragment.setChargesList(chargesList);
        return fragment;
    }

    public static LoanChargeFragment newInstance(int loanAccountNumber, List<Charges>
            chargesList, boolean isParentFragmentAGroupFragment) {
        LoanChargeFragment fragment = new LoanChargeFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber);
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
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_charge_list, container, false);
        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();

        ButterKnife.bind(this, rootView);
        mLoanChargePresenter.attachView(this);

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

        mChargesNameListAdapter = new ChargeNameListAdapter(context,
                chargesList, loanAccountNumber);
        lv_charges.setAdapter(mChargesNameListAdapter);

        if (isInfiniteScrollEnabled) {
            setInfiniteScrollListener(mChargesNameListAdapter);
        }


    }


    @SuppressWarnings("deprecation")
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuItem menuItemAddNewLoanCharge = menu.add(Menu.NONE, MENU_ITEM_ADD_NEW_LOAN_CHARGES,
                Menu.NONE, getString(R.string.add_new));
        menuItemAddNewLoanCharge.setIcon(getResources().getDrawable(R.drawable
                .ic_action_content_new));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            menuItemAddNewLoanCharge.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == MENU_ITEM_ADD_NEW_LOAN_CHARGES) {
            LoanChargeDialogFragment loanChargeDialogFragment = LoanChargeDialogFragment
                    .newInstance(loanAccountNumber);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CHARGE_LIST);
            loanChargeDialogFragment.show(fragmentTransaction, "Loan Charge Dialog Fragment");
        }

        return super.onOptionsItemSelected(item);
    }

    public void fetchChargesList() {

        //Check if ClientListFragment has a clientList
        if (chargesList.size() > 0) {
            inflateChargeList();
        } else {
            mLoanChargePresenter.loadLoanChargesList(loanAccountNumber);
        }


    }

    public void setInfiniteScrollListener(final ChargeNameListAdapter chargesNameListAdapter) {

        lv_charges.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int
                    visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    mLoanChargePresenter.loadChargesList(loanAccountNumber);
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
    public void showLoanChargesList(Page<Charges> chargesPage) {
        chargesList = chargesPage.getPageItems();
        inflateChargeList();
    }

    @Override
    public void showChargesList(Page<Charges> chargesPage) {
        chargesList.addAll(chargesPage.getPageItems());
        mChargesNameListAdapter.notifyDataSetChanged();
        index = lv_charges.getFirstVisiblePosition();
        View v = lv_charges.getChildAt(0);
        top = (v == null) ? 0 : v.getTop();
        lv_charges.setSelectionFromTop(index, top);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showFetchingError(Response response) {
        if (getActivity() != null) {
            try {
                Log.i("Error", "" + response.getStatus());
                if (response.getStatus() == 401) {
                    Toast.makeText(getActivity(), "Authorization Expired - Please " +
                            "Login Again", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();

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
    public void showProgressbar(boolean b) {
        swipeRefreshLayout.setRefreshing(b);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoanChargePresenter.detachView();
    }
}
