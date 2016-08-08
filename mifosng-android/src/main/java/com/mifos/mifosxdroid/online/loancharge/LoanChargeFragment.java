/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.loancharge;
/**
 * Created by nellyk on 1/22/2016.
 */

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.ChargeNameListAdapter;
import com.mifos.mifosxdroid.core.EndlessRecyclerOnScrollListener;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.RecyclerItemClickListener;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.dialogfragments.loanchargedialog.LoanChargeDialogFragment;
import com.mifos.objects.client.Charges;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoanChargeFragment extends MifosBaseFragment implements LoanChargeMvpView,
        RecyclerItemClickListener.OnItemClickListener {

    public static final int MENU_ITEM_ADD_NEW_LOAN_CHARGES = 3000;

    @BindView(R.id.rv_charge)
    RecyclerView rv_charges;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.noChargesText)
    TextView mNoChargesText;

    @BindView(R.id.noChargesIcon)
    ImageView mNoChargesIcon;

    @BindView(R.id.ll_error)
    LinearLayout ll_error;

    @Inject
    LoanChargePresenter mLoanChargePresenter;

    List<Charges> chargesList = new ArrayList<>();

    private ChargeNameListAdapter mChargesNameListAdapter;
    private View rootView;
    private Context context;
    private int loanAccountNumber;


    public LoanChargeFragment() {

    }

    public static LoanChargeFragment newInstance(int loanAccountNumber,
                                                 List<Charges> chargesList) {
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_charges.setLayoutManager(layoutManager);
        rv_charges.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), this));
        rv_charges.setHasFixedSize(true);


        //Loading LoanChargesList
        mLoanChargePresenter.loadLoanChargesList(loanAccountNumber);

        setToolbarTitle(getString(R.string.charges));

        /**
         * Setting mApiRestCounter to 1 and send Refresh Request to Server
         */
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_light, R.color.green_light, R
                .color.orange_light, R.color.red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mLoanChargePresenter.loadLoanChargesList(loanAccountNumber);

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

                //Future Implementation
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
        mLoanChargePresenter.loadLoanChargesList(loanAccountNumber);
    }


    public void setChargesList(List<Charges> chargesList) {
        this.chargesList = chargesList;
    }

    @Override
    public void showLoanChargesList(List<Charges> charges) {

        if (charges.size() == 0) {
            ll_error.setVisibility(View.VISIBLE);
            mNoChargesText.setText("There is No Charges to Show");
            mNoChargesIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp);
        } else {
            chargesList = charges;
            mChargesNameListAdapter = new ChargeNameListAdapter(context,
                    chargesList, loanAccountNumber);
            rv_charges.setAdapter(mChargesNameListAdapter);
        }
    }

    @Override
    public void showFetchingError(String s) {
        ll_error.setVisibility(View.VISIBLE);
        mNoChargesText.setText(s + "\n Click to Refresh ");
        mLoanChargePresenter.loadLoanChargesList(loanAccountNumber);
        Toaster.show(rootView, s);
    }


    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            showMifosProgressBar();
        } else {
            hideMifosProgressBar();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoanChargePresenter.detachView();
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

}
