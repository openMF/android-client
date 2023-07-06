/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.databinding.RowAccountItemBinding
import com.mifos.mifosxdroid.databinding.RowSavingsTransactionItemBinding
import com.mifos.objects.accounts.savings.Transaction
import com.mifos.utils.DateHelper.getDateAsString

/**
 * Created by ishankhanna on 30/05/14.
 */
class SavingsAccountTransactionsListAdapter(
    context: Context,
    listOfTransactions: List<Transaction>
) : BaseAdapter() {
    private val listOfTransactions: List<Transaction>
    private val layoutInflater: LayoutInflater
    private val mContext: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.listOfTransactions = listOfTransactions
        mContext = context
    }

    override fun getCount(): Int {
        return listOfTransactions.size
    }

    override fun getItem(i: Int): Transaction {
        return listOfTransactions[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: RowSavingsTransactionItemBinding
        val convertView: View
        if (view == null) {

            binding = RowSavingsTransactionItemBinding.inflate(layoutInflater, viewGroup, false)
            convertView = binding.root
            convertView.tag = binding
        } else {
            binding = view.tag as RowSavingsTransactionItemBinding
            convertView = view
        }
        binding.tvTransactionDate.text =
            getDateAsString(listOfTransactions[i].date)
        binding.tvTransactionType.text = listOfTransactions[i]
            .transactionType.value
        val transactionAmount = listOfTransactions[i].currency.displaySymbol +
                mContext.resources.getString(R.string.space) +
                listOfTransactions[i].amount
        binding.tvTransactionAmount.text = transactionAmount
        if (listOfTransactions[i].transactionType.deposit) {
            binding.tvTransactionAmount.setTextColor(
                ContextCompat.getColor(mContext, R.color.savings_account_status_active)
            )
        } else if (listOfTransactions[i].transactionType.withdrawal) {
            binding.tvTransactionAmount.setTextColor(Color.RED)
        } else {
            binding.tvTransactionAmount.setTextColor(Color.BLACK)
        }
        return convertView
    }
}