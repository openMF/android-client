package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.mifos.api.model.BulkRepaymentTransactions;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.injection.ActivityContext;
import com.mifos.mifosxdroid.online.collectionsheetindividual.OnRetrieveSheetItemData;
import com.mifos.objects.accounts.loan.PaymentTypeOptions;
import com.mifos.objects.collectionsheet.LoanAndClientName;
import com.mifos.objects.collectionsheet.LoanCollectionSheet;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tarun on 17-07-2017.
 */

public class IndividualCollectionSheetAdapter extends
        RecyclerView.Adapter<IndividualCollectionSheetAdapter.ViewHolder> {

    private List<String> paymentTypeList;
    private List<LoanAndClientName> loanAndClientNames;
    private List<PaymentTypeOptions> paymentTypeOptionsList;
    private Context c;

    private OnRetrieveSheetItemData sheetItemClickListener;

    @Inject
    public IndividualCollectionSheetAdapter(@ActivityContext Context context) {
        c = context;
    }

    public void setSheetItemClickListener(OnRetrieveSheetItemData sheetItemClickListener) {
        this.sheetItemClickListener = sheetItemClickListener;
    }

    public void setPaymentTypeOptionsList(List<PaymentTypeOptions> paymentTypeOptionsList) {
        this.paymentTypeOptionsList = paymentTypeOptionsList;
    }

    public void setPaymentTypeList(List<String> paymentTypeList) {
        this.paymentTypeList = paymentTypeList;
        this.paymentTypeList.add(c.getString(R.string.payment_type));
    }

    public void setLoans(List<LoanAndClientName> loanAndClientNameList) {
        this.loanAndClientNames = loanAndClientNameList;
    }


    @Override
    public IndividualCollectionSheetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_individual_collection, parent, false);

        return new IndividualCollectionSheetAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final IndividualCollectionSheetAdapter.ViewHolder holder,
                                 int position) {
        if (holder != null) {

            LoanAndClientName loanAndClientNameItem = loanAndClientNames.get(position);
            final LoanCollectionSheet loanCollectionSheetItem = loanAndClientNameItem.getLoan();
            holder.tvClientName.setText(loanAndClientNameItem.getClientName());

            holder.tvProductCode.setText(concatProductWithAccount(loanCollectionSheetItem
                    .getProductShortName(), loanCollectionSheetItem.getAccountId()));

            if (loanCollectionSheetItem.getChargesDue() != null) {
                holder.etCharges.setText(
                        String.format(Locale.getDefault(), "%f",
                                loanCollectionSheetItem.getChargesDue()));
            }

            if (loanCollectionSheetItem.getTotalDue() != null) {
                holder.etTotalDues.setText(
                        String.format(Locale.getDefault(), "%f",
                                loanCollectionSheetItem.getTotalDue()));
            }


            //Add default value of transaction irrespective of they are 'saved' or 'cancelled'
            // manually by the user.
            BulkRepaymentTransactions defaultBulkRepaymentTransaction = new
                    BulkRepaymentTransactions();
            defaultBulkRepaymentTransaction.setLoanId(loanCollectionSheetItem.getLoanId());
            defaultBulkRepaymentTransaction.setTransactionAmount(
                    loanCollectionSheetItem.getChargesDue() != null ?
                            loanCollectionSheetItem.getChargesDue() +
                                    loanCollectionSheetItem.getTotalDue() :
                            loanCollectionSheetItem.getTotalDue());

            sheetItemClickListener.onShowSheetMandatoryItem(defaultBulkRepaymentTransaction,
                    position);
        }
    }

    private String concatProductWithAccount(String productCode, String accountNo) {
        return productCode + " (#" + accountNo + ")";
    }

    @Override
    public int getItemCount() {
        return loanAndClientNames.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            Spinner.OnItemSelectedListener {
        int position;
        BulkRepaymentTransactions bulkRepaymentTransaction = new BulkRepaymentTransactions();

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            btnAdditional.setOnClickListener(this);
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

        @Override
        public void onClick(View view) {
            position = getAdapterPosition();
            switch (view.getId()) {
                case R.id.btn_additional_details:
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

        /**
         * This method saves the values extracted from the EditTexts in the
         * Additional Info layout - 'tableAdditional'.
         */
        private void saveAdditional() {

            bulkRepaymentTransaction.setLoanId(loanAndClientNames.get(position)
                    .getLoan().getLoanId());
            bulkRepaymentTransaction.setTransactionAmount((!etCharges
                    .getText().toString().isEmpty() ?
                    Double.parseDouble(etCharges.getText().toString()) : 0)
                    + (!etTotalDues.getText().toString().isEmpty() ?
                    Double.parseDouble(etTotalDues.getText().toString()) : 0));

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

            sheetItemClickListener.onSaveAdditionalItem(bulkRepaymentTransaction, position);
            tableAdditional.setVisibility(View.GONE);
        }

        /**
         * This method sets the 'loanId' and 'transactionAmount' to the values entered in
         * the EditTexts and sets the remaining fields to null.
         */
        private void cancelAdditional() {
            bulkRepaymentTransaction.setLoanId(loanAndClientNames.get(position)
                    .getLoan().getLoanId());
            bulkRepaymentTransaction.setTransactionAmount((!etCharges.getText()
                    .toString().isEmpty() ?
                    Double.parseDouble(etCharges.getText().toString()) : 0)
                    + (!etTotalDues.getText().toString().isEmpty() ?
                    Double.parseDouble(etTotalDues.getText().toString()) : 0));

            tableAdditional.setVisibility(View.GONE);
            bulkRepaymentTransaction.setPaymentTypeId(null);
            bulkRepaymentTransaction.setAccountNumber(null);
            bulkRepaymentTransaction.setCheckNumber(null);
            bulkRepaymentTransaction.setRoutingCode(null);
            bulkRepaymentTransaction.setReceiptNumber(null);
            bulkRepaymentTransaction.setBankNumber(null);
            sheetItemClickListener.onSaveAdditionalItem(bulkRepaymentTransaction, position);
        }

        private void showAdditional() {
            tableAdditional.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(c,
                    android.R.layout.simple_spinner_item, paymentTypeList);
            spPaymentOption.setAdapter(adapter);
            spPaymentOption.setSelection(paymentTypeList.size() - 1);
            spPaymentOption.setOnItemSelectedListener(this);
            btnSaveAdditional.setOnClickListener(this);
            btnCancelAdditional.setOnClickListener(this);

        }

        @BindView(R.id.tv_client_name)
        TextView tvClientName;

        @BindView(R.id.tv_product_code)
        TextView tvProductCode;

        @BindView(R.id.et_charges)
        EditText etCharges;

        @BindView(R.id.et_total_due)
        EditText etTotalDues;

        @BindView(R.id.btn_additional_details)
        Button btnAdditional;

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

    }
}
