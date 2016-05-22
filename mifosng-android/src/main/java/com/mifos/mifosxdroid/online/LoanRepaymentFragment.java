/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

import com.google.gson.Gson;
import com.jakewharton.fliptables.FlipTable;
import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoanRepaymentFragment extends ProgressableFragment implements MFDatePicker
        .OnDatePickListener {

    @InjectView(R.id.tv_clientName)
    TextView tv_clientName;
    @InjectView(R.id.tv_loan_product_short_name)
    TextView tv_loanProductShortName;
    @InjectView(R.id.tv_loanAccountNumber)
    TextView tv_loanAccountNumber;
    @InjectView(R.id.tv_in_arrears)
    TextView tv_inArrears;
    @InjectView(R.id.tv_amount_due)
    TextView tv_amountDue;
    @InjectView(R.id.tv_repayment_date)
    TextView tv_repaymentDate;
    @InjectView(R.id.et_amount)
    EditText et_amount;
    @InjectView(R.id.et_additional_payment)
    EditText et_additionalPayment;
    @InjectView(R.id.et_fees)
    EditText et_fees;
    @InjectView(R.id.tv_total)
    TextView tv_total;
    @InjectView(R.id.sp_payment_type)
    Spinner sp_paymentType;
    @InjectView(R.id.bt_paynow)
    Button bt_paynow;
    private View rootView;
    // Arguments Passed From the Loan Account Summary Fragment
    private String clientName;
    private String loanAccountNumber;
    private String loanProductName;
    private Double amountInArrears;
    // Values fetched from Loan Repayment Template
    private List<PaymentTypeOption> paymentTypeOptionList;
    private HashMap<String, Integer> paymentTypeHashMap = new HashMap<>();
    private DialogFragment mfDatePicker;

    public static LoanRepaymentFragment newInstance(LoanWithAssociations loanWithAssociations) {
        LoanRepaymentFragment fragment = new LoanRepaymentFragment();
        Bundle args = new Bundle();
        if (loanWithAssociations != null) {
            args.putString(Constants.CLIENT_NAME, loanWithAssociations.getClientName());
            args.putString(Constants.LOAN_PRODUCT_NAME, loanWithAssociations.getLoanProductName());
            args.putString(Constants.LOAN_ACCOUNT_NUMBER, loanWithAssociations.getAccountNo());
            args.putDouble(Constants.AMOUNT_IN_ARREARS, loanWithAssociations.getSummary()
                    .getTotalOverdue());
            //args.putDouble(Constants.AMOUNT_DUE, loanWithAssociations.getSummary()
            // .getPrincipalDisbursed());
            //args.putDouble(Constants.FEES_DUE, loanWithAssociations.getSummary()
            // .getFeeChargesOutstanding());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_loan_repayment, container, false);
        setToolbarTitle("Loan Repayment");
        ButterKnife.inject(this, rootView);
        inflateUI();
        return rootView;
    }


    public void inflateUI() {
        showProgress(false);
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
                try {
                    tv_total.setText(String.valueOf(calculateTotal()));
                } catch (NumberFormatException nfe) {
                    et_additionalPayment.setText("0");
                } finally {
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
                try {
                    tv_total.setText(String.valueOf(calculateTotal()));
                } catch (NumberFormatException nfe) {
                    et_fees.setText("0");
                } finally {
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

    public Double calculateTotal() {
        return Double.parseDouble(et_amount.getText().toString())
                + Double.parseDouble(et_additionalPayment.getText().toString())
                + Double.parseDouble(et_fees.getText().toString());
    }


    public void inflatePaymentOptions() {
        App.apiManager.getLoanRepayTemplate(Integer.parseInt(loanAccountNumber), new
                Callback<LoanRepaymentTemplate>() {

                    @Override
                    public void success(LoanRepaymentTemplate loanRepaymentTemplate, Response
                            response) {
                /* Activity is null - Fragment has been detached; no need to do anything. */
                        if (getActivity() == null) return;

                        if (loanRepaymentTemplate != null) {
                            tv_amountDue.setText(String.valueOf(loanRepaymentTemplate.getAmount()));
                            inflateRepaymentDate();
                            List<String> listOfPaymentTypes = new ArrayList<String>();
                            paymentTypeOptionList = loanRepaymentTemplate.getPaymentTypeOptions();
                            // Sorting has to be done on the basis of
                            // PaymentTypeOption.position because it is specified
                            // by the users on Mifos X Platform.
                            Collections.sort(paymentTypeOptionList);

                            for (PaymentTypeOption paymentTypeOption : paymentTypeOptionList) {
                                listOfPaymentTypes.add(paymentTypeOption.getName());
                                paymentTypeHashMap.put(paymentTypeOption.getName(),
                                        paymentTypeOption
                                        .getId());
                            }

                            ArrayAdapter<String> paymentTypeAdapter = new ArrayAdapter<String>
                                    (getActivity(), android.R.layout.simple_spinner_item,
                                            listOfPaymentTypes);
                            paymentTypeAdapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            sp_paymentType.setAdapter(paymentTypeAdapter);

                            et_amount.setText(String.valueOf(loanRepaymentTemplate
                                    .getPrincipalPortion()
                                    + loanRepaymentTemplate.getInterestPortion()));
                            et_additionalPayment.setText("0.0");
                            et_fees.setText(String.valueOf(loanRepaymentTemplate
                                    .getFeeChargesPortion()));

                        }
                        showProgress(false);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        showProgress(false);
                    }
                });

    }

    public void inflateRepaymentDate() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tv_repaymentDate.setText(MFDatePicker.getDatePickedAsString());
        /*
            TODO Add Validation to make sure :
            1. Date Is in Correct Format
            2. Date Entered is not greater than Date Today i.e Date is not in future
         */
        tv_repaymentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
            }
        });

    }

    @Override
    public void onDatePicked(String date) {
        tv_repaymentDate.setText(date);
    }

    @OnClick(R.id.bt_paynow)
    public void onPayNowButtonClicked() {
        try {
            String[] headers = {"Field", "Value"};
            String[][] data = {
                    {"Repayment Date", tv_repaymentDate.getText().toString()},
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

            new AlertDialog.Builder(getActivity())
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
        } catch (NullPointerException npe) {
            Toaster.show(rootView, "Please make sure every field has a value, before submitting " +
                    "repayment!");
        }
    }

    @OnClick(R.id.bt_cancelPayment)
    public void onCancelPaymentButtonClicked() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    public void submitPayment() {
        //TODO Implement a proper builder method here
        String dateString = tv_repaymentDate.getText().toString().replace("-", " ");
        final LoanRepaymentRequest request = new LoanRepaymentRequest();
        request.setPaymentTypeId(String.valueOf(paymentTypeHashMap.get(sp_paymentType
                .getSelectedItem().toString())));
        request.setLocale("en");
        request.setTransactionAmount(String.valueOf(calculateTotal()));
        request.setDateFormat("dd MM yyyy");
        request.setTransactionDate(dateString);
        String builtRequest = new Gson().toJson(request);
        Log.i("TAG", builtRequest);

        showProgress(true);

        App.apiManager.submitPayment(Integer.parseInt(loanAccountNumber), request, new
                Callback<LoanRepaymentResponse>() {
                    @Override
                    public void success(LoanRepaymentResponse resp, Response response) {
                        if (resp != null)
                            Toaster.show(rootView, "Payment Successful, Transaction ID = " + resp
                                    .getResourceId());
                        showProgress(false);
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Toaster.show(rootView, "Payment Failed");
                        showProgress(false);
                    }
                });
    }

    public interface OnFragmentInteractionListener {

    }
}
