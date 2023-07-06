package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mifos.mifosxdroid.databinding.ItemSyncSavingsAccountTransactionBinding
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.utils.Utils.getPaymentTypeName
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 19/08/16.
 */
class SyncSavingsAccountTransactionAdapter @Inject constructor() :
    RecyclerView.Adapter<SyncSavingsAccountTransactionAdapter.ViewHolder>() {
    private var mSavingsAccountTransactionRequests: List<SavingsAccountTransactionRequest>
    private var mPaymentTypeOptions: List<PaymentTypeOption>

    init {
        mSavingsAccountTransactionRequests = ArrayList()
        mPaymentTypeOptions = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSyncSavingsAccountTransactionBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    fun setSavingsAccountTransactions(
        savingsAccountTransactions: List<SavingsAccountTransactionRequest>
    ) {
        mSavingsAccountTransactionRequests = savingsAccountTransactions
        notifyDataSetChanged()
    }

    fun setPaymentTypeOptions(paymentTypeOptions: List<PaymentTypeOption>) {
        mPaymentTypeOptions = paymentTypeOptions
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = mSavingsAccountTransactionRequests[position]
        val paymentTypeName = getPaymentTypeName(
            transaction.paymentTypeId.toInt(),
            mPaymentTypeOptions
        )
        holder.binding.tvSavingsAccountId.text = transaction.savingAccountId.toString()
        holder.binding.tvPaymentType.text = paymentTypeName
        holder.binding.tvTransactionType.text = transaction.transactionType
        holder.binding.tvTransactionAmount.text = transaction.transactionAmount
        holder.binding.tvTransactionDate.text = transaction.transactionDate
        if (transaction.errorMessage != null) {
            holder.binding.tvErrorMessage.text = transaction.errorMessage
            holder.binding.tvErrorMessage.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mSavingsAccountTransactionRequests.size
    }

    class ViewHolder(val binding: ItemSyncSavingsAccountTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)
}