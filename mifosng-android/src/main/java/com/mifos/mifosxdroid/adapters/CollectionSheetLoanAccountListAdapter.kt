/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.mifos.core.objects.db.Loan
import com.mifos.mifosxdroid.databinding.RowCollectionSheetLoanBinding

/**
 * Created by ishankhanna on 21/07/14.
 */
class CollectionSheetLoanAccountListAdapter(
    private val context: Context,
    private val loans: List<Loan>,
    private val groupPosition: Int,
    private val childPosition: Int
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return loans.size
    }

    override fun getItem(position: Int): Loan {
        return loans[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: RowCollectionSheetLoanBinding
        val view: View

        if (convertView == null) {
            binding = RowCollectionSheetLoanBinding.inflate(inflater, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as RowCollectionSheetLoanBinding
            view = convertView
        }

        val transactionAmount =
            CollectionListAdapter.sRepaymentTransactions[loans[position].loanId]
        binding.tvAmountDue.text = loans[position].totalDue.toString()
        binding.tvLoanShortname.text = loans[position].productShortName
        binding.etAmountPaid.setText(transactionAmount.toString())
        binding.etAmountPaid.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                try {
                    CollectionListAdapter.sRepaymentTransactions[loans[position]
                        .loanId] = if (s.toString() == "") 0.00 else s.toString().toDouble()
                } catch (e: NumberFormatException) {
                    CollectionListAdapter.sRepaymentTransactions[loans[position]
                        .loanId] = 0.00
                }
                /* TODO Fix Live update of Amounts
                CollectionSheetFragment.refreshFragment();
                binding.etAmountPaid.requestFocus();
                */
            }
        })
        return view
    }
}