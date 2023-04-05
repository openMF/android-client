package com.mifos.mifosxdroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mifos.mifosxdroid.databinding.ItemSyncLoanRepaymentBinding;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by Rajan Maurya on 30/07/16.
 */
public class SyncLoanRepaymentAdapter extends
        RecyclerView.Adapter<SyncLoanRepaymentAdapter.ViewHolder> {

    private List<LoanRepaymentRequest> loanRepaymentRequests;
    private List<PaymentTypeOption> mPaymentTypeOptions;

    @Inject
    public SyncLoanRepaymentAdapter() {
        loanRepaymentRequests = new ArrayList<>();
        mPaymentTypeOptions = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemSyncLoanRepaymentBinding binding = ItemSyncLoanRepaymentBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        LoanRepaymentRequest loanRepaymentRequest = loanRepaymentRequests.get(position);
        String paymentTypeName =
                Utils.getPaymentTypeName(Integer.parseInt(
                        loanRepaymentRequest.getPaymentTypeId()), mPaymentTypeOptions);

        holder.tv_loan_id.setText(String.valueOf(loanRepaymentRequest.getLoanId()));
        holder.tv_account_number.setText(loanRepaymentRequest.getAccountNumber());

        if (mPaymentTypeOptions.size() != 0) {
            holder.tv_payment_type.setText(paymentTypeName);
        }

        holder.tv_transaction_amount.setText(loanRepaymentRequest.getTransactionAmount());
        holder.tv_transaction_date.setText(loanRepaymentRequest.getTransactionDate());

        if (loanRepaymentRequest.getErrorMessage() != null) {
            holder.tv_error_message.setText(loanRepaymentRequest.getErrorMessage());
            holder.tv_error_message.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return loanRepaymentRequests.size();
    }

    public void setLoanRepaymentRequests(List<LoanRepaymentRequest> repaymentRequests) {
        loanRepaymentRequests = repaymentRequests;
        notifyDataSetChanged();
    }

    public void setPaymentTypeOptions(List<PaymentTypeOption> paymentTypeOptions) {
        mPaymentTypeOptions = paymentTypeOptions;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_loan_id;

        TextView tv_account_number;

        TextView tv_payment_type;

        TextView tv_transaction_amount;

        TextView tv_transaction_date;

        TextView tv_error_message;

        public ViewHolder(ItemSyncLoanRepaymentBinding binding) {
            super(binding.getRoot());

            tv_loan_id = binding.tvDbLoanId;
            tv_account_number = binding.tvDbAccountNumber;
            tv_payment_type = binding.tvDbPaymentType;
            tv_transaction_amount = binding.tvDbTransactionAmount;
            tv_transaction_date = binding.tvDbTransactionDate;
            tv_error_message = binding.tvErrorMessage;
        }
    }
}
