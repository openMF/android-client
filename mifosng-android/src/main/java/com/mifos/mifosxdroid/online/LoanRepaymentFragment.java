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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.accounts.loan.Loan;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.templates.loans.PaymentTypeOption;
import com.mifos.utils.Constants;
import com.mifos.utils.SafeUIBlockingUtility;
import com.mifos.utils.services.API;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoanRepaymentFragment extends Fragment {

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;


    // Arguments Passed From the Loan Account Summary Fragment
    String clientName;
    String loanAccountNumber;
    String loanProductName;
    Double amountInArrears;
    Double amountDue;
    Double fees;

    // Values fetched from Loan Repayment Template
    List<PaymentTypeOption> paymentTypeOptionList;

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
    @InjectView(R.id.sp_payment_type) Spinner sp_paymentType;


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
        actionBar.setTitle("Loan Repayment");
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

        safeUIBlockingUtility.safelyBlockUI();

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

        inflateRepaymentDate();

        inflatePaymentOptions();

    }

    public Double calculateTotal(){

        return Double.parseDouble(et_amount.getText().toString())
                +Double.parseDouble(et_additionalPayment.getText().toString())
                +Double.parseDouble(et_fees.getText().toString());

    }

    public interface OnFragmentInteractionListener {

    }

    public void inflatePaymentOptions(){

        API.loanService.getLoanRepaymentTemplate(Integer.parseInt(loanAccountNumber), new Callback<LoanRepaymentTemplate>() {

            @Override
            public void success(LoanRepaymentTemplate loanRepaymentTemplate, Response response) {

                if(loanRepaymentTemplate != null)
                {
                    List<String> listOfPaymentTypes = new ArrayList<String>();

                    //Currently this method assumes that Positions are Unique for each paymentType
                    //TODO Implement a Duplication check on positions and sort them and add into listOfPaymentTypes
                    paymentTypeOptionList = loanRepaymentTemplate.getPaymentTypeOptions();
                    Iterator<PaymentTypeOption> paymentTypeOptionIterator = paymentTypeOptionList.iterator();
                    while(paymentTypeOptionIterator.hasNext())
                    {
                        PaymentTypeOption paymentTypeOption = paymentTypeOptionIterator.next();
                        listOfPaymentTypes.add(paymentTypeOption.getPosition(),paymentTypeOption.getName());
                    }

                    ArrayAdapter<String> paymentTypeAdapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_item, listOfPaymentTypes);

                    paymentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_paymentType.setAdapter(paymentTypeAdapter);

                }

                safeUIBlockingUtility.safelyUnBlockUI();

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                safeUIBlockingUtility.safelyUnBlockUI();

            }
        });

    }

    public void inflateRepaymentDate(){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        et_repaymentDate.setText(new StringBuilder().append(month + 1)
        .append(" - ").append(day).append(" - ").append(year));



    }
}
