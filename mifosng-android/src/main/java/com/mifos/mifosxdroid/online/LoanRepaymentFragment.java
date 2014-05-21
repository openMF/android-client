package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.accounts.loan.Loan;
import com.mifos.utils.Constants;
import com.mifos.utils.SafeUIBlockingUtility;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class LoanRepaymentFragment extends Fragment {

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;


    String clientName;
    String loanAccountNumber;
    String loanProductName;
    Double amountInArrears;
    Double amountDue;
    Double fees;

    @InjectView(R.id.tv_clientName) TextView tv_clientName;
    @InjectView(R.id.tv_loan_product_short_name) TextView tv_loanProductShortName;
    @InjectView(R.id.tv_loanAccountNumber) TextView tv_loanAccountNumber;
    @InjectView(R.id.tv_in_arrears) TextView tv_inArrears;
    @InjectView(R.id.tv_amount_due) TextView tv_amountDue;
    @InjectView(R.id.et_repayment_date) EditText et_repaymentDate;
    @InjectView(R.id.et_amount) EditText et_amount;
    @InjectView(R.id.et_additional_payment) EditText et_additionalPayment;
    @InjectView(R.id.et_fees) EditText et_fees;
    @InjectView(R.id.tv_total) TextView tv_total;


    private OnFragmentInteractionListener mListener;

    public LoanRepaymentFragment() {
        // Required empty public constructor
    }

    public static LoanRepaymentFragment newInstance(Loan loan) {
        LoanRepaymentFragment fragment = new LoanRepaymentFragment();
        Bundle args = new Bundle();
        if(loan != null)
        {
            args.putString(Constants.CLIENT_NAME, loan.getClientName());
            args.putString(Constants.LOAN_PRODUCT_NAME, loan.getLoanProductName());
            args.putString(Constants.LOAN_ACCOUNT_NUMBER, loan.getAccountNo());
            args.putDouble(Constants.AMOUNT_IN_ARREARS, loan.getSummary().getTotalOverdue());
            args.putDouble(Constants.AMOUNT_DUE, loan.getSummary().getPrincipalDisbursed());
            args.putDouble(Constants.FEES_DUE, loan.getSummary().getFeeChargesOutstanding());
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            clientName = getArguments().getString(Constants.CLIENT_NAME);
            loanAccountNumber = getArguments().getString(Constants.LOAN_ACCOUNT_NUMBER);
            loanProductName = getArguments().getString(Constants.LOAN_PRODUCT_NAME);
            amountInArrears = getArguments().getDouble(Constants.AMOUNT_IN_ARREARS);
            amountDue = getArguments().getDouble(Constants.AMOUNT_DUE);
            fees = getArguments().getDouble(Constants.FEES_DUE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_loan_repayment, container, false);
        activity = (ActionBarActivity) getActivity();
        safeUIBlockingUtility = new SafeUIBlockingUtility(LoanRepaymentFragment.this.getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        actionBar = activity.getSupportActionBar();
        ButterKnife.inject(this, rootView);

        inflateUI();

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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

    public void inflateUI(){

        tv_clientName.setText(clientName);
        tv_loanProductShortName.setText(loanProductName);
        tv_loanAccountNumber.setText(loanAccountNumber);
        tv_inArrears.setText(String.valueOf(amountInArrears));
        tv_amountDue.setText(String.valueOf(amountDue));

        //Setup Form with Default Values
        Double amount = amountInArrears + amountDue;
        et_amount.setText(String.valueOf(amount));
        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try{
                    tv_total.setText(String.valueOf(calculateTotal()));
                }catch (NumberFormatException nfe){
                    et_amount.setText("0");
                }finally {
                    tv_total.setText(String.valueOf(calculateTotal()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_additionalPayment.setText("0.0");
        et_additionalPayment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try{
                    tv_total.setText(String.valueOf(calculateTotal()));
                }catch (NumberFormatException nfe){
                    et_additionalPayment.setText("0");
                }finally {
                    tv_total.setText(String.valueOf(calculateTotal()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_fees.setText(String.valueOf(fees));
        et_fees.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try{
                    tv_total.setText(String.valueOf(calculateTotal()));
                }catch (NumberFormatException nfe){
                    et_fees.setText("0");
                }finally {
                    tv_total.setText(String.valueOf(calculateTotal()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Double total = amountInArrears + amountDue + fees;

        tv_total.setText(String.valueOf(calculateTotal()));
    }

    public Double calculateTotal(){

        return Double.parseDouble(et_amount.getText().toString())
                +Double.parseDouble(et_additionalPayment.getText().toString())
                +Double.parseDouble(et_fees.getText().toString());

    }

    public interface OnFragmentInteractionListener {

    }

}
