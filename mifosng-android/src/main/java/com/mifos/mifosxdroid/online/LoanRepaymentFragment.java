package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jakewharton.fliptables.FlipTable;
import com.mifos.mifosxdroid.R;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.Loan;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.services.API;
import com.mifos.utils.Constants;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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

    // Values fetched from Loan Repayment Template
    List<PaymentTypeOption> paymentTypeOptionList;
    HashMap<String, Integer> paymentTypeHashMap = new HashMap<String, Integer>();

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
    @InjectView(R.id.bt_paynow) Button bt_paynow;

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
            //args.putDouble(Constants.AMOUNT_DUE, loan.getSummary().getPrincipalDisbursed());
            //args.putDouble(Constants.FEES_DUE, loan.getSummary().getFeeChargesOutstanding());
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
            //amountDue = getArguments().getDouble(Constants.AMOUNT_DUE);
            //fees = getArguments().getDouble(Constants.FEES_DUE);
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

        //Setup Form with Default Values
        et_amount.setText("0.0");

        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                try {
                    tv_total.setText(String.valueOf(calculateTotal()));
                } catch (NumberFormatException nfe) {
                    et_amount.setText("0");
                } finally {
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

        et_fees.setText("0.0");

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

        inflateRepaymentDate();

        inflatePaymentOptions();

        tv_total.setText(String.valueOf(calculateTotal()));

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
                    tv_amountDue.setText(String.valueOf(loanRepaymentTemplate.getAmount()));

                    inflateRepaymentDate();

                    List<String> listOfPaymentTypes = new ArrayList<String>();

                    //Currently this method assumes that Positions are Unique for each paymentType
                    //TODO Implement a Duplication check on positions and sort them and add into listOfPaymentTypes
                    paymentTypeOptionList = loanRepaymentTemplate.getPaymentTypeOptions();
                    Iterator<PaymentTypeOption> paymentTypeOptionIterator = paymentTypeOptionList.iterator();
                    while(paymentTypeOptionIterator.hasNext())
                    {
                        PaymentTypeOption paymentTypeOption = paymentTypeOptionIterator.next();
                        listOfPaymentTypes.add(paymentTypeOption.getPosition(),paymentTypeOption.getName());
                        paymentTypeHashMap.put(paymentTypeOption.getName(),paymentTypeOption.getId());
                    }

                    ArrayAdapter<String> paymentTypeAdapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_item, listOfPaymentTypes);

                    paymentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_paymentType.setAdapter(paymentTypeAdapter);

                    et_amount.setText(String.valueOf(loanRepaymentTemplate.getPrincipalPortion()+loanRepaymentTemplate.getInterestPortion()));
                    et_additionalPayment.setText("0.0");
                    et_fees.setText(String.valueOf(loanRepaymentTemplate.getFeeChargesPortion()));

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
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        et_repaymentDate.setText(new StringBuilder().append(day)
                .append(" - ").append(month + 1).append(" - ").append(year));

        /*
            TODO Add Validation to make sure :
            1. Date Is in Correct Format
            2. Date Entered is not greater than Date Today i.e Date is not in future
         */

    }

    @OnClick(R.id.bt_paynow)
    public void onPayNowButtonClicked(){

        try {
            String[] headers = {"Field", "Value"};


            String[][] data = {
                    {"Repayment Date", et_repaymentDate.getText().toString()},
                    {"Payment Type", sp_paymentType.getSelectedItem().toString()},
                    {"Amount", et_amount.getText().toString()},
                    {"Addition Payment", et_additionalPayment.getText().toString()},
                    {"Fees", et_fees.getText().toString()},
                    {"Total", String.valueOf(calculateTotal())}
            };

            System.out.println(FlipTable.of(headers, data));

            String formReviewString = new StringBuilder().append(data[0][0] + " : " + data[0][1])
                    .append("\n")
                    .append(data[1][0] + " : " + data[1][1])
                    .append("\n")
                    .append(data[2][0] + " : " + data[2][1])
                    .append("\n")
                    .append(data[3][0] + " : " + data[3][1])
                    .append("\n")
                    .append(data[4][0] + " : " + data[4][1])
                    .append("\n")
                    .append(data[5][0] + " : " + data[5][1]).toString();


            AlertDialog confirmPaymentDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Review Payment Details")
                    .setMessage(formReviewString)
                    .setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            submitPayment();
                        }
                    })
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }catch (NullPointerException npe) {
            Toast.makeText(getActivity(),"Please make sure every field has a value, before submitting repayment!",Toast.LENGTH_LONG).show();
        }


    }

    @OnClick(R.id.bt_cancelPayment)
    public void onCancelPaymentButtonClicked(){
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    public void submitPayment(){
        //TODO Implement a proper builder method here

        String dateString = et_repaymentDate.getEditableText().toString().replace(" - ", " ");

        final LoanRepaymentRequest loanRepaymentRequest = new LoanRepaymentRequest();
        loanRepaymentRequest.setPaymentTypeId(String.valueOf(paymentTypeHashMap.get(sp_paymentType.getSelectedItem().toString())));
        loanRepaymentRequest.setLocale("en");
        loanRepaymentRequest.setTransactionAmount(String.valueOf(calculateTotal()));
        loanRepaymentRequest.setDateFormat("dd MM yyyy");
        loanRepaymentRequest.setTransactionDate(dateString);
        String builtRequest = new Gson().toJson(loanRepaymentRequest);
        Log.i("TAG", builtRequest);

        safeUIBlockingUtility.safelyBlockUI();
        API.loanService.submitPayment(Integer.parseInt(loanAccountNumber),
                loanRepaymentRequest,
                new Callback<LoanRepaymentResponse>() {
                    @Override
                    public void success(LoanRepaymentResponse loanRepaymentResponse, Response response) {

                        if (loanRepaymentResponse != null)
                        {
                            Toast.makeText(getActivity(),"Payment Successful, Transaction ID = "+loanRepaymentResponse.getResourceId(),
                                    Toast.LENGTH_LONG).show();
                        }
                        safeUIBlockingUtility.safelyUnBlockUI();
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Toast.makeText(getActivity(),"Payment Failed",Toast.LENGTH_SHORT).show();
                        safeUIBlockingUtility.safelyUnBlockUI();
                    }
                });
    }
}
