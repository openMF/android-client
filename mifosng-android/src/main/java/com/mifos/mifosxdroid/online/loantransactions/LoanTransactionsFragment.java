/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.loantransactions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.LoanTransactionAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.utils.Constants;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoanTransactionsFragment extends MifosBaseFragment
        implements LoanTransactionsMvpView {

    @BindView(R.id.elv_loan_transactions)
    ExpandableListView elv_loanTransactions;

    @Inject
    LoanTransactionsPresenter mLoanTransactionsPresenter;

    private LoanTransactionAdapter adapter;

    private int loanAccountNumber;
    private View rootView;

    public static LoanTransactionsFragment newInstance(int loanAccountNumber) {
        LoanTransactionsFragment fragment = new LoanTransactionsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null)
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_loan_transactions, container, false);

        ButterKnife.bind(this, rootView);
        mLoanTransactionsPresenter.attachView(this);

        inflateLoanTransactions();

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        super.onPrepareOptionsMenu(menu);
    }

    public void inflateLoanTransactions() {
        mLoanTransactionsPresenter.loadLoanTransaction(loanAccountNumber);
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
    public void showLoanTransaction(LoanWithAssociations loanWithAssociations) {
        Log.i("Transaction List Size", "" + loanWithAssociations.getTransactions().size());
        adapter = new LoanTransactionAdapter(getActivity(),
                loanWithAssociations.getTransactions());
        elv_loanTransactions.setAdapter(adapter);
        elv_loanTransactions.setGroupIndicator(null);
    }

    @Override
    public void showFetchingError(String s) {
        Toaster.show(rootView, s);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoanTransactionsPresenter.detachView();
    }
}
