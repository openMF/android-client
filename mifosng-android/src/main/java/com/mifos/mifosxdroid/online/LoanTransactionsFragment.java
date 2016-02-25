/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.LoanTransactionAdapter;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.utils.Constants;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoanTransactionsFragment extends MifosBaseFragment {

    @InjectView(R.id.elv_loan_transactions)
    ExpandableListView elv_loanTransactions;

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
        if (getArguments() != null)
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_loan_transactions, container, false);
        ButterKnife.inject(this, rootView);
        inflateLoanTransactions();
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        super.onPrepareOptionsMenu(menu);
    }

    public void inflateLoanTransactions() {
        App.apiManager.getLoanTransactions(loanAccountNumber, new Callback<LoanWithAssociations>() {
            @Override
            public void success(LoanWithAssociations loan, Response response) {
                if (loan != null) {
                    Log.i("Transaction List Size", "" + loan.getTransactions().size());
                    LoanTransactionAdapter adapter = new LoanTransactionAdapter(getActivity(), loan.getTransactions());
                    elv_loanTransactions.setAdapter(adapter);
                    elv_loanTransactions.setGroupIndicator(null);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }
}
