package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.LoanTransactionAdapter;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.services.API;
import com.mifos.utils.Constants;
import com.mifos.utils.SafeUIBlockingUtility;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoanTransactionsFragment extends Fragment {

    @InjectView(R.id.elv_loan_transactions)
    ExpandableListView elv_loanTransactions;

    private int loanAccountNumber;

    private OnFragmentInteractionListener mListener;

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;

    public static LoanTransactionsFragment newInstance(int loanAccountNumber) {
        LoanTransactionsFragment fragment = new LoanTransactionsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public LoanTransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_loan_transactions, container, false);
        activity = (ActionBarActivity) getActivity();
        safeUIBlockingUtility = new SafeUIBlockingUtility(LoanTransactionsFragment.this.getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        actionBar = activity.getSupportActionBar();
        ButterKnife.inject(this, rootView);

        inflateLoanTransactions();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

     public interface OnFragmentInteractionListener {

    }

    public void inflateLoanTransactions() {

        API.loanService.getLoanWithTransactions(loanAccountNumber, new Callback<LoanWithAssociations>() {
            @Override
            public void success(LoanWithAssociations loanWithAssociations, Response response) {

                if(loanWithAssociations != null) {

                    Log.i("Transaction List Size", "" + loanWithAssociations.getTransactions().size());

                    LoanTransactionAdapter loanTransactionAdapter =
                            new LoanTransactionAdapter(getActivity(),loanWithAssociations.getTransactions());
                    elv_loanTransactions.setAdapter(loanTransactionAdapter);
                    elv_loanTransactions.setGroupIndicator(null);

                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });

    }



}
