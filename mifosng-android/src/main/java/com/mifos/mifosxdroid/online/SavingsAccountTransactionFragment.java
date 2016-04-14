/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.fliptables.FlipTable;
import com.mifos.App;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.savings.DepositType;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SavingsAccountTransactionFragment extends ProgressableFragment implements MFDatePicker.OnDatePickListener {

    @InjectView(R.id.tv_clientName)
    TextView tv_clientName;
    @InjectView(R.id.tv_savingsAccountNumber)
    TextView tv_accountNumber;
    @InjectView(R.id.tv_transaction_date)
    TextView tv_transactionDate;
    @InjectView(R.id.et_transaction_amount)
    EditText et_transactionAmount;
    @InjectView(R.id.sp_payment_type)
    Spinner sp_paymentType;

    private View rootView;
    private String savingsAccountNumber;
    private DepositType savingsAccountType;
    private String transactionType;     //Defines if the Transaction is a Deposit to an Account or a Withdrawal from an Account
    private String clientName;
    // Values to be fetched from Savings Account Template
    private List<PaymentTypeOption> paymentTypeOptionList;
    private HashMap<String, Integer> paymentTypeHashMap = new HashMap<String, Integer>();
    private DialogFragment mfDatePicker;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param savingsAccountWithAssociations Savings Account of the Client with some additional association details
     * @param transactionType                Type of Transaction (Deposit or Withdrawal)
     * @return A new instance of fragment SavingsAccountTransactionDialogFragment.
     */
    public static SavingsAccountTransactionFragment newInstance(SavingsAccountWithAssociations savingsAccountWithAssociations, String transactionType, DepositType accountType) {
        SavingsAccountTransactionFragment fragment = new SavingsAccountTransactionFragment();
        Bundle args = new Bundle();
        args.putString(Constants.SAVINGS_ACCOUNT_NUMBER, savingsAccountWithAssociations.getAccountNo());
        args.putString(Constants.SAVINGS_ACCOUNT_TRANSACTION_TYPE, transactionType);
        args.putString(Constants.CLIENT_NAME, savingsAccountWithAssociations.getClientName());
        args.putParcelable(Constants.SAVINGS_ACCOUNT_TYPE, accountType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            savingsAccountNumber = getArguments().getString(Constants.SAVINGS_ACCOUNT_NUMBER);
            transactionType = getArguments().getString(Constants.SAVINGS_ACCOUNT_TRANSACTION_TYPE);
            clientName = getArguments().getString(Constants.CLIENT_NAME);
            savingsAccountType = getArguments().getParcelable(Constants.SAVINGS_ACCOUNT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_savings_account_transaction, container, false);
        if (transactionType.equals(Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT))
            setToolbarTitle(getResources().getString(R.string.savingsAccount) + " " + getResources().getString(R.string.deposit));
        else
            setToolbarTitle(getResources().getString(R.string.savingsAccount) + " " + getResources().getString(R.string.withdrawal));
        ButterKnife.inject(this, rootView);
        inflateUI();
        return rootView;
    }

    public void inflateUI() {
        showProgress(true);
        tv_clientName.setText(clientName);
        tv_accountNumber.setText(savingsAccountNumber);
        //TODO Implement QuickContactBadge here
        inflateRepaymentDate();
        inflatePaymentOptions();
    }

    public void inflatePaymentOptions() {
        App.apiManager.getSavingsAccountTemplate(savingsAccountType.getEndpoint(), Integer.parseInt(savingsAccountNumber), transactionType, new Callback<SavingsAccountTransactionTemplate>() {
            @Override
            public void success(SavingsAccountTransactionTemplate savingsAccountTransactionTemplate, Response response) {
                /* Activity is null - Fragment has been detached; no need to do anything. */
                if (getActivity() == null) return;

                if (savingsAccountTransactionTemplate != null) {
                    List<String> listOfPaymentTypes = new ArrayList<>();
                    paymentTypeOptionList = savingsAccountTransactionTemplate.getPaymentTypeOptions();
                    // Sorting has to be done on the basis of
                    // PaymentTypeOption.position because it is specified
                    // by the users on Mifos X Platform.
                    Collections.sort(paymentTypeOptionList);
                    Iterator<PaymentTypeOption> paymentTypeOptionIterator = paymentTypeOptionList.iterator();
                    while (paymentTypeOptionIterator.hasNext()) {
                        PaymentTypeOption paymentTypeOption = paymentTypeOptionIterator.next();
                        listOfPaymentTypes.add(paymentTypeOption.getName());
                        paymentTypeHashMap.put(paymentTypeOption.getName(), paymentTypeOption.getId());
                    }
                    ArrayAdapter<String> paymentTypeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, listOfPaymentTypes);
                    paymentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_paymentType.setAdapter(paymentTypeAdapter);
                }
                showProgress(false);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                showProgress(false);
            }
        });
    }

    @OnClick(R.id.bt_reviewTransaction)
    public void onReviewTransactionButtonClicked() {
        // Notify user if Amount field is blank and Review
        // Transaction button is pressed.
        if (et_transactionAmount.getEditableText().toString().isEmpty()) {
            new RequiredFieldException(getString(R.string.amount), getString(R.string.message_field_required)).notifyUserWithToast(getActivity());
            return;
        }
        String[] headers = {"Field", "Value"};
        String[][] data = {
                {"Transaction Date", tv_transactionDate.getText().toString()},
                {"Payment Type", sp_paymentType.getSelectedItem().toString()},
                {"Amount", et_transactionAmount.getEditableText().toString()}
        };
        System.out.println(FlipTable.of(headers, data));
        StringBuilder formReviewStringBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                formReviewStringBuilder.append(data[i][j]);
                if (j == 0)
                    formReviewStringBuilder.append(" : ");
            }
            formReviewStringBuilder.append("\n");
        }
        new AlertDialog.Builder(getActivity())
                .setTitle("Review Payment Details")
                .setMessage(formReviewStringBuilder.toString())
                .setPositiveButton("Process Transaction", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        processTransaction();
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public void processTransaction() {
        String dateString = tv_transactionDate.getText().toString().replace("-", " ");
        final SavingsAccountTransactionRequest savingsAccountTransactionRequest = new SavingsAccountTransactionRequest();
        savingsAccountTransactionRequest.setLocale("en");
        savingsAccountTransactionRequest.setDateFormat("dd MM yyyy");
        savingsAccountTransactionRequest.setTransactionDate(dateString);
        savingsAccountTransactionRequest.setTransactionAmount(et_transactionAmount.getEditableText().toString());
        savingsAccountTransactionRequest.setPaymentTypeId(String.valueOf(paymentTypeHashMap.get(sp_paymentType.getSelectedItem().toString())));

        String builtTransactionRequestAsJson = new Gson().toJson(savingsAccountTransactionRequest);
        Log.i("Transaction Body", builtTransactionRequestAsJson);
        showProgress();
        App.apiManager.processTransaction(savingsAccountType.getEndpoint(), Integer.parseInt(savingsAccountNumber), transactionType, savingsAccountTransactionRequest, new Callback<SavingsAccountTransactionResponse>() {
                    @Override
                    public void success(SavingsAccountTransactionResponse savingsAccountTransactionResponse, Response response) {
                        /* Activity is null - Fragment has been detached; no need to do anything. */
                        if (getActivity() == null) return;

                        if (savingsAccountTransactionResponse != null) {
                            if (transactionType.equals(Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT)) {
                                Toaster.show(rootView, "Deposit Successful, Transaction ID = " + savingsAccountTransactionResponse.getResourceId());
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                            } else if (transactionType.equals(Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL)) {
                                Toaster.show(rootView, "Withdrawal Successful, Transaction ID = " + savingsAccountTransactionResponse.getResourceId());
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                            }
                        }
                        hideProgress();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Toaster.show(rootView, "Transaction Failed");
                        hideProgress();
                    }
                }
        );
    }

    @OnClick(R.id.bt_cancelTransaction)
    public void onCancelTransactionButtonClicked() {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    public void inflateRepaymentDate() {
        mfDatePicker = MFDatePicker.newInsance(this);
        tv_transactionDate.setText(MFDatePicker.getDatePickedAsString());
        // TODO Add Validation to make sure :
        // 1. Date Is in Correct Format
        // 2. Date Entered is not greater than Date Today i.e Date is not in future
        tv_transactionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
            }
        });
    }

    @Override
    public void onDatePicked(String date) {
        tv_transactionDate.setText(date);
    }
}
