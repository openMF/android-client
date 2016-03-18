/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.loantransactionsfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.mifos.App;
import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.LoanTransactionAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.utils.Constants;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoanTransactionsFragment extends MifosBaseFragment implements LoanTransactionsMvpView{

    @InjectView(R.id.elv_loan_transactions)
    ExpandableListView elv_loanTransactions;

    private int loanAccountNumber;
    private View rootView;
    private DataManager dataManager;
    private LoanTransactionsPresenter mLoanTransactionsPresenter;

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
        if (getArguments() != null)
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_loan_transactions, container, false);
        ButterKnife.inject(this, rootView);
        dataManager = new DataManager();
        mLoanTransactionsPresenter = new LoanTransactionsPresenter(dataManager);
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
        showProgress();
        mLoanTransactionsPresenter.loanLoanTransactions(loanAccountNumber);
    }

    @Override
    public void showLoanTransactions(LoanWithAssociations loanWithAssociations) {
        hideProgress();
        if (loanWithAssociations != null) {
            Log.i("Transaction List Size", "" + loanWithAssociations.getTransactions().size());
            LoanTransactionAdapter adapter = new LoanTransactionAdapter(getActivity(), loanWithAssociations.getTransactions());
            elv_loanTransactions.setAdapter(adapter);
            elv_loanTransactions.setGroupIndicator(null);
        }
    }

    @Override
    public void ResponseError(String s) {
        hideProgress();
        Toaster.show(rootView,s);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoanTransactionsPresenter.detachView();
    }
}
