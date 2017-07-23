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
    private List<LoanAndClientName> loans;
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

    public void setLoans(List<LoanAndClientName> loans) {
        this.loans = loans;
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

            LoanAndClientName item = loans.get(position);
            final LoanCollectionSheet loanItem = item.getLoan();
            holder.tvClientName.setText(item.getClientName());

            holder.tvProductCode.setText(concatProductWithAccount(loanItem.getProductShortName()
                    , loanItem.getAccountId()));

            if (loanItem.getChargesDue() != null) {
                holder.etCharges.setText(
                        String.format(Locale.getDefault(), "%f", loanItem.getChargesDue()));
            }

            if (loanItem.getTotalDue() != null) {
                holder.etTotalDues.setText(
                        String.format(Locale.getDefault(), "%f", loanItem.getTotalDue()));
            }


            //Add default value of transaction irrespective of they are 'saved' or 'cancelled'
            // manually by the user.
            BulkRepaymentTransactions singleTransaction = new BulkRepaymentTransactions();
            singleTransaction.setLoanId(loanItem.getLoanId());
            singleTransaction.setTransactionAmount(loanItem.getChargesDue() != null ?
                    loanItem.getChargesDue() + loanItem.getTotalDue() :
                    loanItem.getTotalDue());

            sheetItemClickListener.onShowSheetMandatoryItem(singleTransaction, position);
        }
    }

    private String concatProductWithAccount(String productCode, String accountNo) {
        return productCode + " (#" + accountNo + ")";
    }

    @Override
    public int getItemCount() {
        return loans.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            Spinner.OnItemSelectedListener {
        int position;
        BulkRepaymentTransactions transaction = new BulkRepaymentTransactions();

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            btnAdditional.setOnClickListener(this);
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            if (i != paymentTypeList.size() - 1) {
                transaction.setPaymentTypeId(paymentTypeOptionsList.get(i).getId());
            } else {
                transaction.setPaymentTypeId(null);
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

            transaction.setLoanId(loans.get(position).getLoan().getLoanId());
            transaction.setTransactionAmount((!etCharges.getText().toString().isEmpty() ?
                    Double.parseDouble(etCharges.getText().toString()) : 0)
                    + (!etTotalDues.getText().toString().isEmpty() ?
                    Double.parseDouble(etTotalDues.getText().toString()) : 0));

            if (!etAccountNumber.getText().toString().isEmpty()) {
                transaction.setAccountNumber(etAccountNumber.getText().toString());
            }

            if (!etChequeNumber.getText().toString().isEmpty()) {
                transaction.setCheckNumber(etChequeNumber.getText().toString());
            }

            if (!etRoutingCode.getText().toString().isEmpty()) {
                transaction.setRoutingCode(etRoutingCode.getText().toString());
            }

            if (!etReceiptNumber.getText().toString().isEmpty()) {
                transaction.setReceiptNumber(etReceiptNumber.getText().toString());
            }

            if (!etBankNumber.getText().toString().isEmpty()) {
                transaction.setBankNumber(etBankNumber.getText().toString());
            }

            sheetItemClickListener.onSaveAdditionalItem(transaction, position);
            tableAdditional.setVisibility(View.GONE);
        }

        /**
         * This method sets the 'loanId' and 'transactionAmount' to the values entered in
         * the EditTexts and sets the remaining fields to null.
         */
        private void cancelAdditional() {
            transaction.setLoanId(loans.get(position).getLoan().getLoanId());
            transaction.setTransactionAmount((!etCharges.getText().toString().isEmpty() ?
                    Double.parseDouble(etCharges.getText().toString()) : 0)
                    + (!etTotalDues.getText().toString().isEmpty() ?
                    Double.parseDouble(etTotalDues.getText().toString()) : 0));

            tableAdditional.setVisibility(View.GONE);
            transaction.setPaymentTypeId(null);
            transaction.setAccountNumber(null);
            transaction.setCheckNumber(null);
            transaction.setRoutingCode(null);
            transaction.setReceiptNumber(null);
            transaction.setBankNumber(null);
            sheetItemClickListener.onSaveAdditionalItem(transaction, position);
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
