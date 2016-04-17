/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;
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

import com.mifos.App;
import com.mifos.mifosxdroid.LoginActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ChargeNameListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.dialogfragments.LoanChargeDialogFragment;
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



public class LoanChargeFragment extends MifosBaseFragment {

    public static final int MENU_ITEM_ADD_NEW_LOAN_CHARGES = 3000;
    @InjectView(R.id.lv_charges)
    ListView lv_charges;
    List<Charges> chargesList = new ArrayList<Charges>();
    private View rootView;
    private Context context;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences sharedPreferences;
    private int loanAccountNumber;
    private int index = 0;
    private int top = 0;
    private boolean isInfiniteScrollEnabled = false;

    public LoanChargeFragment(){

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
    public static LoanChargeFragment newInstance(int loanAccountNumber,List<Charges> chargesList, boolean isParentFragmentAGroupFragment) {
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
        if (getArguments() != null)
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_charge_list, container, false);
        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();
        ButterKnife.inject(this, rootView);
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

        final ChargeNameListAdapter chargesNameListAdapter = new ChargeNameListAdapter(context, chargesList,loanAccountNumber);
        lv_charges.setAdapter(chargesNameListAdapter);

        if (isInfiniteScrollEnabled) {
            setInfiniteScrollListener(chargesNameListAdapter);
        }


    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuItem menuItemAddNewLoanCharge = menu.add(Menu.NONE, MENU_ITEM_ADD_NEW_LOAN_CHARGES, Menu.NONE, getString(R.string.add_new));
        menuItemAddNewLoanCharge.setIcon(getResources().getDrawable(R.drawable.ic_action_content_new));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            menuItemAddNewLoanCharge.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == MENU_ITEM_ADD_NEW_LOAN_CHARGES) {
            LoanChargeDialogFragment loanChargeDialogFragment = LoanChargeDialogFragment.newInstance(loanAccountNumber);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
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

            swipeRefreshLayout.setRefreshing(true);
            //Get a Client List
            App.apiManager.getListOfLoanCharges(loanAccountNumber, new Callback<Page<Charges>>() {
                @Override
                public void success(Page<Charges> page, Response response) {
                    chargesList = page.getPageItems();
                    inflateChargeList();
                    swipeRefreshLayout.setRefreshing(false);

                }
                @Override
                public void failure(RetrofitError retrofitError) {

                    swipeRefreshLayout.setRefreshing(false);

                    if (getActivity() != null) {
                        try {
                            Log.i("Error", "" + retrofitError.getResponse().getStatus());
                            if (retrofitError.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                                Toast.makeText(getActivity(), "Authorization Expired - Please Login Again", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();

                            } else {
                                Toast.makeText(getActivity(), "There was some error fetching list.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException npe) {
                            Toast.makeText(getActivity(), "There is some problem with your internet connection.", Toast.LENGTH_SHORT).show();

                        }


                    }
                }
            });

        }


    }

    public void setInfiniteScrollListener(final ChargeNameListAdapter chargesNameListAdapter) {

        lv_charges.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount >= totalItemCount) {

                    swipeRefreshLayout.setRefreshing(true);

                    App.apiManager.getListOfCharges(loanAccountNumber, new Callback<Page<Charges>>() {

                        @Override
                        public void success(Page<Charges> chargesPage, Response response) {


                            chargesList.addAll(chargesPage.getPageItems());
                            chargesNameListAdapter.notifyDataSetChanged();
                            index = lv_charges.getFirstVisiblePosition();
                            View v = lv_charges.getChildAt(0);
                            top = (v == null) ? 0 : v.getTop();
                            lv_charges.setSelectionFromTop(index, top);
                            swipeRefreshLayout.setRefreshing(false);

                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {

                            swipeRefreshLayout.setRefreshing(false);

                            if (getActivity() != null) {
                                try {
                                    Log.i("Error", "" + retrofitError.getResponse().getStatus());
                                    if (retrofitError.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                                        Toast.makeText(getActivity(), "Authorization Expired - Please Login Again", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), LoginActivity.class));
                                        getActivity().finish();

                                    } else {
                                        Toast.makeText(getActivity(), "There was some error fetching list.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NullPointerException npe) {
                                    Toast.makeText(getActivity(), "There is some problem with your internet connection.", Toast.LENGTH_SHORT).show();

                                }


                            }

                        }

                    });

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
}
