/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.mifos.core.objects.db.Loan
import com.mifos.mifosxdroid.databinding.RowLoanListItemBinding

class LoanListAdapter(context: Context?, listLoans: List<Loan>) : BaseAdapter() {
    private val layoutInflater: LayoutInflater
    private val listLoans: List<Loan>

    init {
        layoutInflater = LayoutInflater.from(context)
        this.listLoans = listLoans
    }

    override fun getCount(): Int {
        return listLoans.size
    }

    override fun getItem(i: Int): Loan {
        return listLoans[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: RowLoanListItemBinding
        val convertView: View
        if (view == null) {
            binding = RowLoanListItemBinding.inflate(layoutInflater, viewGroup, false)
            convertView = binding.root
            convertView.tag = binding
        } else {
            binding = view.tag as RowLoanListItemBinding
            convertView = view
        }
        val loan = listLoans[i]
        binding.tvLoanAccountId.text = loan.loanId.toString()
        return convertView
    }
}