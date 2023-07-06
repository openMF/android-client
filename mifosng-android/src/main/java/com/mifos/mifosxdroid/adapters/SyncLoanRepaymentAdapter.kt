package com.mifos.mifosxdroid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mifos.mifosxdroid.databinding.ItemSyncLoanRepaymentBinding
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.utils.Utils.getPaymentTypeName
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 30/07/16.
 */
class SyncLoanRepaymentAdapter @Inject constructor() :
    RecyclerView.Adapter<SyncLoanRepaymentAdapter.ViewHolder>() {
    private var loanRepaymentRequests: List<LoanRepaymentRequest>
    private var mPaymentTypeOptions: List<PaymentTypeOption>

    init {
        loanRepaymentRequests = ArrayList()
        mPaymentTypeOptions = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSyncLoanRepaymentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loanRepaymentRequest = loanRepaymentRequests[position]
        val paymentTypeName = getPaymentTypeName(
            loanRepaymentRequest.paymentTypeId.toInt(), mPaymentTypeOptions
        )
        holder.binding.tvLoanId.text = loanRepaymentRequest.loanId.toString()
        holder.binding.tvAccountNumber.text = loanRepaymentRequest.accountNumber
        if (mPaymentTypeOptions.isNotEmpty()) {
            holder.binding.tvPaymentType.text = paymentTypeName
        }
        holder.binding.tvTransactionAmount.text = loanRepaymentRequest.transactionAmount
        holder.binding.tvTransactionDate.text = loanRepaymentRequest.transactionDate
        if (loanRepaymentRequest.errorMessage != null) {
            holder.binding.tvErrorMessage.text = loanRepaymentRequest.errorMessage
            holder.binding.tvErrorMessage.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return loanRepaymentRequests.size
    }

    fun setLoanRepaymentRequests(repaymentRequests: List<LoanRepaymentRequest>) {
        loanRepaymentRequests = repaymentRequests
        notifyDataSetChanged()
    }

    fun setPaymentTypeOptions(paymentTypeOptions: List<PaymentTypeOption>) {
        mPaymentTypeOptions = paymentTypeOptions
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemSyncLoanRepaymentBinding) :
        RecyclerView.ViewHolder(binding.root)
}