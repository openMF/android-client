package com.mifos.mifosxdroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mifos.mifosxdroid.databinding.ItemSyncSavingsAccountTransactionBinding;
import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;



/**
 * Created by Rajan Maurya on 19/08/16.
 */
public class SyncSavingsAccountTransactionAdapter extends
        RecyclerView.Adapter<SyncSavingsAccountTransactionAdapter.ViewHolder> {

    private List<SavingsAccountTransactionRequest> mSavingsAccountTransactionRequests;
    private List<PaymentTypeOption> mPaymentTypeOptions;

    @Inject
    public SyncSavingsAccountTransactionAdapter() {
        mSavingsAccountTransactionRequests = new ArrayList<>();
        mPaymentTypeOptions = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemSyncSavingsAccountTransactionBinding binding = ItemSyncSavingsAccountTransactionBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }


    public void setSavingsAccountTransactions(
            List<SavingsAccountTransactionRequest> savingsAccountTransactions) {
        mSavingsAccountTransactionRequests = savingsAccountTransactions;
        notifyDataSetChanged();
    }

    public void setPaymentTypeOptions(List<PaymentTypeOption> paymentTypeOptions) {
        mPaymentTypeOptions = paymentTypeOptions;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SavingsAccountTransactionRequest transaction =
                mSavingsAccountTransactionRequests.get(position);

        String paymentTypeName =
                Utils.getPaymentTypeName(Integer.parseInt(transaction.getPaymentTypeId()),
                        mPaymentTypeOptions);

        holder.tv_savings_account_id.setText(String.valueOf(transaction.getSavingAccountId()));
        holder.tv_payment_type.setText(paymentTypeName);
        holder.tv_transaction_type.setText(transaction.getTransactionType());
        holder.tv_transaction_amount.setText(transaction.getTransactionAmount());
        holder.tv_transaction_date.setText(transaction.getTransactionDate());

        if (transaction.getErrorMessage() != null) {
            holder.tv_error_message.setText(transaction.getErrorMessage());
            holder.tv_error_message.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mSavingsAccountTransactionRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_savings_account_id;

        TextView tv_payment_type;

        TextView tv_transaction_type;

        TextView tv_transaction_amount;

        TextView tv_transaction_date;

        TextView tv_error_message;

        public ViewHolder(ItemSyncSavingsAccountTransactionBinding binding) {
            super(binding.getRoot());

            tv_savings_account_id = binding.tvDbSavingsAccountId;
            tv_payment_type = binding.tvDbPaymentType;
            tv_transaction_type = binding.tvDbTransactionType;
            tv_transaction_amount = binding.tvDbTransactionAmount;
            tv_transaction_date = binding.tvDbTransactionDate;
            tv_error_message = binding.tvErrorMessage;
        }
    }
}
