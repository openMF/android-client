package com.mifos.mifosxdroid.online;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.accounts.loan.Loan;
import com.mifos.utils.Constants;
import com.mifos.utils.SafeUIBlockingUtility;
import com.mifos.services.API;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 09/05/14.
 */
public class LoanAccountSummaryFragment extends Fragment {


    int loanAccountNumber;

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;

    @InjectView(R.id.tv_clientName)
    TextView tv_clientName;
    @InjectView(R.id.quickContactBadge_client)
    QuickContactBadge quickContactBadge;
    @InjectView(R.id.tv_loan_product_short_name)
    TextView tv_loan_product_short_name;
    @InjectView(R.id.tv_loanAccountNumber)
    TextView tv_loanAccountNumber;
    @InjectView(R.id.tv_loan_total_due)
    TextView tv_loan_total_due;
    @InjectView(R.id.tv_loan_account_status)
    TextView tv_loan_account_status;
    @InjectView(R.id.tv_in_arrears)
    TextView tv_in_arrears;
    @InjectView(R.id.tv_loan_officer)
    TextView tv_loan_officer;
    @InjectView(R.id.tv_principal)
    TextView tv_principal;
    @InjectView(R.id.tv_loan_principal_due)
    TextView tv_loan_principal_due;
    @InjectView(R.id.tv_loan_principal_paid)
    TextView tv_loan_principal_paid;
    @InjectView(R.id.tv_interest)
    TextView tv_interest;
    @InjectView(R.id.tv_loan_interest_due)
    TextView tv_loan_interest_due;
    @InjectView(R.id.tv_loan_interest_paid)
    TextView tv_loan_interest_paid;
    @InjectView(R.id.tv_fees)
    TextView tv_fees;
    @InjectView(R.id.tv_loan_fees_due)
    TextView tv_loan_fees_due;
    @InjectView(R.id.tv_loan_fees_paid)
    TextView tv_loan_fees_paid;
    @InjectView(R.id.tv_penalty)
    TextView tv_penalty;
    @InjectView(R.id.tv_loan_penalty_due)
    TextView tv_loan_penalty_due;
    @InjectView(R.id.tv_loan_penalty_paid)
    TextView tv_loan_penalty_paid;
    @InjectView(R.id.tv_total)
    TextView tv_total;
    @InjectView(R.id.tv_total_due)
    TextView tv_total_due;
    @InjectView(R.id.tv_total_paid)
    TextView tv_total_paid;

    public static LoanAccountSummaryFragment newInstance(int loanAccountNumber) {
        LoanAccountSummaryFragment fragment = new LoanAccountSummaryFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber);
        fragment.setArguments(args);
        return fragment;
    }
    public LoanAccountSummaryFragment() {
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

        rootView = inflater.inflate(R.layout.fragment_loan_account_summary, container, false);
        activity = (ActionBarActivity) getActivity();
        safeUIBlockingUtility = new SafeUIBlockingUtility(LoanAccountSummaryFragment.this.getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        actionBar = activity.getSupportActionBar();
        ButterKnife.inject(this, rootView);

        inflateLoanAccountSummary();

        return rootView;
    }

    private void inflateLoanAccountSummary(){

        safeUIBlockingUtility.safelyBlockUI();

        actionBar.setTitle("Loan Account Summary");

        API.loanService.getLoanById(loanAccountNumber, new Callback<Loan>() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void success(Loan loan, Response response) {
                tv_clientName.setText(loan.getClientName());
                tv_loan_product_short_name.setText(loan.getLoanProductName());
                tv_loanAccountNumber.setText("#"+loan.getAccountNo());
                tv_loan_total_due.setText("-"+loan.getSummary().getPrincipalDisbursed());
                tv_loan_account_status.setText(loan.getStatus().getValue());
                tv_in_arrears.setText(String.valueOf(loan.getSummary().getTotalOverdue()));
                tv_loan_officer.setText(loan.getLoanOfficerName());
                //TODO Implement QuickContactBadge
                //quickContactBadge.setImageToDefault();

                tv_principal.setText(String.valueOf(loan.getSummary().getPrincipalDisbursed()));
                tv_loan_principal_due.setText(String.valueOf(loan.getSummary().getPrincipalOutstanding()));
                tv_loan_principal_paid.setText(String.valueOf(loan.getSummary().getPrincipalPaid()));

                tv_interest.setText(String.valueOf(loan.getSummary().getInterestCharged()));
                tv_loan_interest_due.setText(String.valueOf(loan.getSummary().getInterestOutstanding()));
                tv_loan_interest_paid.setText(String.valueOf(loan.getSummary().getInterestPaid()));

                tv_fees.setText(String.valueOf(loan.getSummary().getFeeChargesCharged()));
                tv_loan_fees_due.setText(String.valueOf(loan.getSummary().getFeeChargesOutstanding()));
                tv_loan_fees_paid.setText(String.valueOf(loan.getSummary().getFeeChargesPaid()));

                tv_penalty.setText(String.valueOf(loan.getSummary().getPenaltyChargesCharged()));
                tv_loan_penalty_due.setText(String.valueOf(loan.getSummary().getPenaltyChargesOutstanding()));
                tv_loan_penalty_paid.setText(String.valueOf(loan.getSummary().getPenaltyChargesPaid()));

                tv_total.setText(String.valueOf(loan.getSummary().getTotalExpectedRepayment()));
                tv_total_due.setText(String.valueOf(loan.getSummary().getTotalOutstanding()));
                tv_total_paid.setText(String.valueOf(loan.getSummary().getTotalRepayment()));

                safeUIBlockingUtility.safelyUnBlockUI();
            }

            @Override
            public void failure(RetrofitError retrofitError) {

                Toast.makeText(activity, "Loan Account not found.", Toast.LENGTH_SHORT).show();
                safeUIBlockingUtility.safelyUnBlockUI();
            }
        });


    }



    public interface OnFragmentInteractionListener {

    }

}
