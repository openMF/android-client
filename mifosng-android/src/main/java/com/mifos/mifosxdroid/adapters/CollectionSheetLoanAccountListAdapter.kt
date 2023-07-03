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
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.objects.db.Loan

/**
 * Created by ishankhanna on 21/07/14.
 */
class CollectionSheetLoanAccountListAdapter(
    context: Context?,
    loans: List<Loan>,
    groupPosition: Int,
    childPosition: Int
) : BaseAdapter() {
    var layoutInflater: LayoutInflater
    var loans: List<Loan> = ArrayList()
    var groupPosition: Int
    var childPosition: Int
    var positionBeingEdited = -1

    init {
        layoutInflater = LayoutInflater.from(context)
        this.loans = loans
        this.groupPosition = groupPosition
        this.childPosition = childPosition
    }

    override fun getCount(): Int {
        return loans.size
    }

    override fun getItem(position: Int): Loan {
        return loans[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var convertView = convertView
        val reusableViewHolder: ReusableViewHolder
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_collection_sheet_loan, null)
            reusableViewHolder = ReusableViewHolder(convertView)
            convertView.tag = reusableViewHolder
        } else {
            reusableViewHolder = convertView.tag as ReusableViewHolder
        }
        val transactionAmount =
            CollectionListAdapter.sRepaymentTransactions[loans[position].getLoanId()]
        reusableViewHolder.tv_amountDue!!.text = loans[position].getTotalDue().toString()
        reusableViewHolder.tv_loanShortName!!.text = loans[position].getProductShortName()
        reusableViewHolder.et_amountPaid!!.setText(transactionAmount.toString())
        reusableViewHolder.et_amountPaid!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                try {
                    CollectionListAdapter.sRepaymentTransactions[loans[position]
                        .getLoanId()] = if (s.toString() == "") 0.00 else s
                        .toString().toDouble()
                } catch (e: NumberFormatException) {
                    CollectionListAdapter.sRepaymentTransactions[loans[position]
                        .getLoanId()] = 0.00
                }
                /* TODO Fix Live update of Amounts
                CollectionSheetFragment.refreshFragment();
                reusableViewHolder.et_amountPaid.requestFocus();
                */
            }
        })
        return convertView
    }

    class ReusableViewHolder(view: View?) {
        @JvmField
        @BindView(R.id.tv_loan_shortname)
        var tv_loanShortName: TextView? = null

        @JvmField
        @BindView(R.id.tv_amountDue)
        var tv_amountDue: TextView? = null

        @JvmField
        @BindView(R.id.et_amountPaid)
        var et_amountPaid: EditText? = null

        init {
            ButterKnife.bind(this, view!!)
        }
    }

    companion object {
        private const val TAG = "LoanAccountListAdapter"
    }
}