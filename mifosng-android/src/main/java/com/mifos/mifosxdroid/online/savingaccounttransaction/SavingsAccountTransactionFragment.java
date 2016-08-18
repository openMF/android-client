/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.savingaccounttransaction;

import android.R.layout;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.jakewharton.fliptables.FlipTable;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.R.string;
import com.mifos.mifosxdroid.core.MaterialDialog;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.accounts.savings.DepositType;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;
import com.mifos.utils.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SavingsAccountTransactionFragment extends ProgressableFragment implements
        MFDatePicker.OnDatePickListener, SavingsAccountTransactionMvpView {

    public final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.view_flipper)
    ViewFlipper viewFlipper;

    @BindView(R.id.tv_clientName)
    TextView tv_clientName;

    @BindView(R.id.tv_savingsAccountNumber)
    TextView tv_accountNumber;

    @BindView(R.id.tv_transaction_date)
    TextView tv_transactionDate;

    @BindView(R.id.et_transaction_amount)
    EditText et_transactionAmount;

    @BindView(R.id.sp_payment_type)
    Spinner sp_paymentType;

    @Inject
    SavingsAccountTransactionPresenter mSavingAccountTransactionPresenter;

    private View rootView;
    private String savingsAccountNumber;
    private int savingsAccountId;
    private DepositType savingsAccountType;
    private String transactionType;     //Defines if the Transaction is a Deposit to an Account
    // or a Withdrawal from an Account
    private String clientName;
    // Values to be fetched from Savings Account Template
    private int paymentTypeOptionId;
    private DialogFragment mfDatePicker;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param savingsAccountWithAssociations Savings Account of the Client with some additional
     *                                       association details
     * @param transactionType                Type of Transaction (Deposit or Withdrawal)
     * @return A new instance of fragment SavingsAccountTransactionDialogFragment.
     */
    public static SavingsAccountTransactionFragment newInstance(
            SavingsAccountWithAssociations savingsAccountWithAssociations,
            String transactionType, DepositType accountType) {
        SavingsAccountTransactionFragment fragment = new SavingsAccountTransactionFragment();
        Bundle args = new Bundle();
        args.putString(Constants.SAVINGS_ACCOUNT_NUMBER, savingsAccountWithAssociations
                .getAccountNo());
        args.putInt(Constants.SAVINGS_ACCOUNT_ID, savingsAccountWithAssociations.getId());
        args.putString(Constants.SAVINGS_ACCOUNT_TRANSACTION_TYPE, transactionType);
        args.putString(Constants.CLIENT_NAME, savingsAccountWithAssociations.getClientName());
        args.putParcelable(Constants.SAVINGS_ACCOUNT_TYPE, accountType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            savingsAccountNumber = getArguments().getString(Constants.SAVINGS_ACCOUNT_NUMBER);
            savingsAccountId = getArguments().getInt(Constants.SAVINGS_ACCOUNT_ID);
            transactionType = getArguments().getString(Constants.SAVINGS_ACCOUNT_TRANSACTION_TYPE);
            clientName = getArguments().getString(Constants.CLIENT_NAME);
            savingsAccountType = getArguments().getParcelable(Constants.SAVINGS_ACCOUNT_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_savings_account_transaction, container,
                false);
        if (transactionType.equals(Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT)) {
            setToolbarTitle(getResources().getString(string.savingsAccount) + getResources()
                    .getString(string.space) + getResources().getString(R.string.deposit));
        } else {
            setToolbarTitle(getResources().getString(R.string.savingsAccount) + getResources()
                    .getString(string.space) + getResources().getString(R.string.withdrawal));
        }

        ButterKnife.bind(this, rootView);
        mSavingAccountTransactionPresenter.attachView(this);

        //This Method Checking SavingAccountTransaction made before in Offline mode or not.
        //If yes then User have to sync that first then he will be able to make transaction.
        //If not then User able to make SavingAccountTransaction in Online or Offline.
        checkSavingAccountTransactionStatusInDatabase();

        return rootView;
    }

    @Override
    public void checkSavingAccountTransactionStatusInDatabase() {
        // Checking SavingAccountTransaction Already made in Offline mode or Not.
        mSavingAccountTransactionPresenter
                .checkInDatabaseSavingAccountTransaction(savingsAccountId);
    }

    @Override
    public void showSavingAccountTransactionExistInDatabase() {
        //Visibility of ParentLayout GONE, If SavingAccountTransaction Already made in Offline Mode
        viewFlipper.setVisibility(View.GONE);

        new MaterialDialog.Builder().init(getActivity())
                .setTitle(R.string.sync_previous_transaction)
                .setMessage(string.dialog_message_sync_savingaccounttransaction)
                .setPositiveButton(R.string.dialog_action_ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                            }
                        })
                .setCancelable(false)
                .createMaterialDialog()
                .show();
    }

    @Override
    public void showSavingAccountTransactionDoesNotExistInDatabase() {
        // This Method Inflating UI and Initializing the Loading SavingAccountTransaction
        // Template for transaction.
        inflateUI();
    }


    public void inflateUI() {
        tv_clientName.setText(clientName);
        tv_accountNumber.setText(savingsAccountNumber);
        //TODO Implement QuickContactBadge here
        inflateRepaymentDate();
        inflateSavingsAccountTemplate();
    }

    public void inflateSavingsAccountTemplate() {
        mSavingAccountTransactionPresenter.loadSavingAccountTemplate(
                savingsAccountType.getEndpoint(), savingsAccountId, transactionType);
    }

    @OnClick(R.id.bt_reviewTransaction)
    public void onReviewTransactionButtonClicked() {
        // Notify user if Amount field is blank and Review
        // Transaction button is pressed.
        if (et_transactionAmount.getEditableText().toString().isEmpty()) {
            new RequiredFieldException(getString(R.string.amount), getString(R.string
                    .message_field_required)).notifyUserWithToast(getActivity());
            return;
        }
        String[] headers = {getResources().getString(string.field),
                getResources().getString(string.value)};
        String[][] data = {
                {getResources().getString(string.transaction_date),
                        tv_transactionDate.getText().toString()},
                {getResources().getString(string.payment_type),
                        sp_paymentType.getSelectedItem().toString()},
                {getResources().getString(string.amount),
                        et_transactionAmount.getEditableText().toString()}
        };
        Log.d(LOG_TAG, FlipTable.of(headers, data));
        StringBuilder formReviewStringBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                formReviewStringBuilder.append(data[i][j]);
                if (j == 0) formReviewStringBuilder.append(" : ");
            }
            formReviewStringBuilder.append('\n');
        }

        new MaterialDialog.Builder().init(getActivity())
                .setTitle(getResources().getString(string.review_transaction_details))
                .setMessage(formReviewStringBuilder.toString())
                .setPositiveButton(getResources().getString(string.process_transaction),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                processTransaction();
                            }
                        })
                .setNegativeButton(getResources().getString(string.dialog_action_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                .createMaterialDialog()
                .show();
    }

    public void processTransaction() {
        String dateString = tv_transactionDate.getText().toString().replace("-", " ");
        final SavingsAccountTransactionRequest savingsAccountTransactionRequest = new
                SavingsAccountTransactionRequest();
        savingsAccountTransactionRequest.setLocale("en");
        savingsAccountTransactionRequest.setDateFormat("dd MM yyyy");
        savingsAccountTransactionRequest.setTransactionDate(dateString);
        savingsAccountTransactionRequest.setTransactionAmount(et_transactionAmount
                .getEditableText().toString());
        savingsAccountTransactionRequest.setPaymentTypeId(String.valueOf(paymentTypeOptionId));

        String builtTransactionRequestAsJson = new Gson().toJson(savingsAccountTransactionRequest);
        Log.i(getResources().getString(string.transaction_body), builtTransactionRequestAsJson);

        if (!Network.isOnline(getActivity())) PrefManager.setUserStatus(Constants.USER_OFFLINE);

        mSavingAccountTransactionPresenter.processTransaction(savingsAccountType.getEndpoint(),
                savingsAccountId, transactionType, savingsAccountTransactionRequest);
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
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
            }
        });
    }

    @Override
    public void onDatePicked(String date) {
        tv_transactionDate.setText(date);
    }


    @Override
    public void showSavingAccountTemplate(final SavingsAccountTransactionTemplate
                                                  savingsAccountTransactionTemplate) {
        if (savingsAccountTransactionTemplate != null) {
            List<String> listOfPaymentTypes = Utils.getPaymentTypeOptions(
                    savingsAccountTransactionTemplate.getPaymentTypeOptions());
            ArrayAdapter<String> paymentTypeAdapter = new ArrayAdapter<>(getActivity(),
                    layout.simple_spinner_item, listOfPaymentTypes);

            paymentTypeAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item);
            sp_paymentType.setAdapter(paymentTypeAdapter);
            sp_paymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long
                        id) {
                    paymentTypeOptionId = savingsAccountTransactionTemplate
                            .getPaymentTypeOptions().get(position).getId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    public void showTransactionSuccessfullyDone(SavingsAccountTransactionResponse
                                                        savingsAccountTransactionResponse) {
        if (savingsAccountTransactionResponse.getResourceId() == null) {
            Toaster.show(rootView, getResources().getString(string.transaction_saved_in_db));
        } else {
            if (transactionType.equals(Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT)) {
                Toaster.show(rootView, "Deposit Successful, Transaction ID = " +
                        savingsAccountTransactionResponse.getResourceId());
            } else if (transactionType.equals(Constants.SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL)) {
                Toaster.show(rootView, "Withdrawal Successful, Transaction ID = "
                        + savingsAccountTransactionResponse.getResourceId());
            }
        }
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }


    @Override
    public void showError(int errorMessage) {
        Toaster.show(rootView, errorMessage);
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSavingAccountTransactionPresenter.detachView();
    }
}
