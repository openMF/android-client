package com.mifos.mifosxdroid.online.collectionsheetindividualdetails;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.mifos.api.model.BulkRepaymentTransactions;
import com.mifos.api.model.IndividualCollectionSheetPayload;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.objects.accounts.loan.PaymentTypeOptions;
import com.mifos.objects.collectionsheet.LoanAndClientName;
import com.mifos.objects.collectionsheet.LoanCollectionSheet;
import com.mifos.utils.Constants;
import com.mifos.utils.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aksh on 21/6/18.
 */

public class PaymentDetailsFragment extends MifosBaseFragment
        implements View.OnClickListener, Spinner.OnItemSelectedListener {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_product)
    TextView tvProduct;

    @BindView(R.id.et_total_due)
    TextView etDue;

    @BindView(R.id.tv_total_charges)
    TextView tvCharges;

    @BindView(R.id.no_payment)
    TextView tvNoPayment;

    @BindView(R.id.table_additional_details)
    TableLayout tableAdditional;

    @BindView(R.id.sp_payment_type_options)
    Spinner spPaymentOption;

    @BindView(R.id.et_account_number)
    EditText etAccountNumber;

    @BindView(R.id.et_cheque_number)
    EditText etChequeNumber;

    @BindView(R.id.et_routing_code)
    EditText etRoutingCode;

    @BindView(R.id.et_receipt_number)
    EditText etReceiptNumber;

    @BindView(R.id.et_bank_number)
    EditText etBankNumber;

    @BindView(R.id.btn_cancel_additional)
    Button btnCancelAdditional;

    @BindView(R.id.btn_save_additional)
    Button btnSaveAdditional;

    @BindView(R.id.btn_add_payment)
    Button btnAddPayment;

    @BindView(R.id.iv_user_picture)
    ImageView ivUserPicture;


    List<String> paymentTypeList;
    List<PaymentTypeOptions> paymentTypeOptionsList;

    IndividualCollectionSheetPayload payload;
    OnPayloadSelectedListener mCallback;
    private BulkRepaymentTransactions bulkRepaymentTransaction;
    private int position;
    private int clientId;
    private View rootView;
    private LoanAndClientName loanAndClientNameItem;

    public PaymentDetailsFragment() {
    }

    public static PaymentDetailsFragment newInstance(int position,
                                                     IndividualCollectionSheetPayload payload,
                                                     ArrayList<String> paymentTypeList,
                                                     LoanAndClientName loanAndClientNameItem,
                                                     ArrayList<PaymentTypeOptions>
                                                             paymentTypeOptions
            , int clientId) {
        Bundle args = new Bundle();
        args.putInt(Constants.ADAPTER_POSITION, position);
        args.putParcelable(Constants.PAYLOAD, payload);
        args.putStringArrayList(Constants.PAYMENT_LIST, paymentTypeList);
        args.putParcelable(Constants.LOAN_AND_CLIENT, loanAndClientNameItem);
        args.putParcelableArrayList(Constants.PAYMENT_OPTIONS, paymentTypeOptions);
        args.putInt(Constants.CLIENT_ID, clientId);
        PaymentDetailsFragment fragment = new PaymentDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnPayloadSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPayloadSelectedListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        position = getArguments().getInt(Constants.ADAPTER_POSITION);
        loanAndClientNameItem = getArguments().getParcelable(Constants.LOAN_AND_CLIENT);
        paymentTypeList = getArguments().getStringArrayList(Constants.PAYMENT_LIST);
        payload = getArguments().getParcelable(Constants.PAYLOAD);
        paymentTypeOptionsList = getArguments().getParcelableArrayList(Constants.PAYMENT_OPTIONS);
        clientId = getArguments().getInt(Constants.CLIENT_ID);
        bulkRepaymentTransaction = new BulkRepaymentTransactions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.add_payment_detail, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    public void showUI() {
        final LoanCollectionSheet loanCollectionSheetItem = loanAndClientNameItem.getLoan();
        tvName.setText(loanAndClientNameItem.getClientName());

        tvProduct.setText(concatProductWithAccount(loanCollectionSheetItem
                .getProductShortName(), loanCollectionSheetItem.getAccountId()));

        if (loanCollectionSheetItem.getChargesDue() != null) {
            tvCharges.setText(
                    String.format(Locale.getDefault(), "%f",
                            loanCollectionSheetItem.getChargesDue()));
        }

        if (loanCollectionSheetItem.getTotalDue() != null) {
            etDue.setText(
                    String.format(Locale.getDefault(), "%f",
                            loanCollectionSheetItem.getTotalDue()));
        }
        ImageLoaderUtils.loadImage(getContext(), clientId,
                ivUserPicture);
        BulkRepaymentTransactions defaultBulkRepaymentTransaction = new
                BulkRepaymentTransactions();
        defaultBulkRepaymentTransaction.setLoanId(loanCollectionSheetItem.getLoanId());
        defaultBulkRepaymentTransaction.setTransactionAmount(
                loanCollectionSheetItem.getChargesDue() != null ?
                        loanCollectionSheetItem.getChargesDue() +
                                loanCollectionSheetItem.getTotalDue() :
                        loanCollectionSheetItem.getTotalDue());

        onShowSheetMandatoryItem(defaultBulkRepaymentTransaction,
                position);
        btnAddPayment.setOnClickListener(this);
    }

    private String concatProductWithAccount(String productCode, String accountNo) {
        return productCode + " (#" + accountNo + ")";
    }

    private void showAdditional() {
        tableAdditional.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, paymentTypeList);
        spPaymentOption.setAdapter(adapter);
        spPaymentOption.setSelection(paymentTypeList.size() - 1);
        spPaymentOption.setOnItemSelectedListener(this);
        btnSaveAdditional.setOnClickListener(this);
        btnCancelAdditional.setOnClickListener(this);
    }

    private void cancelAdditional() {
        bulkRepaymentTransaction.setLoanId(loanAndClientNameItem
                .getLoan().getLoanId());
        bulkRepaymentTransaction.setTransactionAmount((!tvCharges.getText()
                .toString().isEmpty() ?
                Double.parseDouble(tvCharges.getText().toString()) : 0)
                + (!etDue.getText().toString().isEmpty() ?
                Double.parseDouble(etDue.getText().toString()) : 0));

        tableAdditional.setVisibility(View.GONE);
        bulkRepaymentTransaction.setPaymentTypeId(null);
        bulkRepaymentTransaction.setAccountNumber(null);
        bulkRepaymentTransaction.setCheckNumber(null);
        bulkRepaymentTransaction.setRoutingCode(null);
        bulkRepaymentTransaction.setReceiptNumber(null);
        bulkRepaymentTransaction.setBankNumber(null);
        onSaveAdditionalItem(bulkRepaymentTransaction, position);
    }

    private void saveAdditional() {
        tvNoPayment.setVisibility(View.GONE);
        bulkRepaymentTransaction.setLoanId(loanAndClientNameItem
                .getLoan().getLoanId());
        bulkRepaymentTransaction.setTransactionAmount((!tvCharges
                .getText().toString().isEmpty() ?
                Double.parseDouble(tvCharges.getText().toString()) : 0)
                + (!etDue.getText().toString().isEmpty() ?
                Double.parseDouble(etDue.getText().toString()) : 0));

        if (!etAccountNumber.getText().toString().isEmpty()) {
            bulkRepaymentTransaction.setAccountNumber(etAccountNumber.getText().toString());
        }

        if (!etChequeNumber.getText().toString().isEmpty()) {
            bulkRepaymentTransaction.setCheckNumber(etChequeNumber.getText().toString());
        }

        if (!etRoutingCode.getText().toString().isEmpty()) {
            bulkRepaymentTransaction.setRoutingCode(etRoutingCode.getText().toString());
        }

        if (!etReceiptNumber.getText().toString().isEmpty()) {
            bulkRepaymentTransaction.setReceiptNumber(etReceiptNumber.getText().toString());
        }

        if (!etBankNumber.getText().toString().isEmpty()) {
            bulkRepaymentTransaction.setBankNumber(etBankNumber.getText().toString());
        }

        onSaveAdditionalItem(bulkRepaymentTransaction, position);
        tableAdditional.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showUI();
    }

    public void onShowSheetMandatoryItem(BulkRepaymentTransactions transaction, int position) {
        payload.getBulkRepaymentTransactions().set(position, transaction);
    }

    public void onSaveAdditionalItem(BulkRepaymentTransactions transaction, int position) {
        payload.getBulkRepaymentTransactions().set(position, transaction);
        mCallback.onPayloadSelected(payload);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_payment:
                if (tableAdditional.getVisibility() == View.VISIBLE) {
                    tableAdditional.setVisibility(View.GONE);
                } else if (tableAdditional.getVisibility() == View.GONE) {
                    showAdditional();
                }
                break;

            case R.id.btn_cancel_additional:
                cancelAdditional();
                break;

            case R.id.btn_save_additional:
                saveAdditional();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i != paymentTypeList.size() - 1) {
            bulkRepaymentTransaction.setPaymentTypeId(paymentTypeOptionsList.get(i).getId());
        } else {
            bulkRepaymentTransaction.setPaymentTypeId(null);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface OnPayloadSelectedListener {
        void onPayloadSelected(IndividualCollectionSheetPayload payload);
    }

}
