/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.databinding.RowAccountItemBinding

/**
 * Created by ishankhanna on 01/03/14.
 */
class LoanAccountsListAdapter(
    private val context: Context,
    private val loanAccountList: List<LoanAccount>
) : BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return loanAccountList.size
    }

    override fun getItem(i: Int): LoanAccount {
        return loanAccountList[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: RowAccountItemBinding
        val convertView: View
        if (view == null) {
            binding = RowAccountItemBinding.inflate(layoutInflater, viewGroup, false)
            convertView = binding.root
            convertView.tag = binding
        } else {
            binding = view.tag as RowAccountItemBinding
            convertView = view
        }

        val loanAccount = loanAccountList[i]

        if (loanAccount.status?.active == true) {
            binding.viewStatusIndicator.setBackgroundColor(
                ContextCompat.getColor(context, R.color.loan_status_disbursed)
            )
        } else if (loanAccount.status?.waitingForDisbursal == true) {
            binding.viewStatusIndicator.setBackgroundColor(
                ContextCompat.getColor(context, R.color.status_approved)
            )
        } else if (loanAccount.status?.pendingApproval == true) {
            binding.viewStatusIndicator.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.status_submitted_and_pending_approval
                )
            )
        } else if (loanAccount.status?.active == true && loanAccount.inArrears == true) {
            binding.viewStatusIndicator.setBackgroundColor(
                ContextCompat.getColor(context, R.color.red)
            )
        } else {
            binding.viewStatusIndicator.setBackgroundColor(
                ContextCompat.getColor(context, R.color.status_closed)
            )
        }

        // TODO: Change getProductName to Loan Amount Due
        binding.tvAmount.text = loanAccount.productName
        binding.tvAmount.ellipsize = TextUtils.TruncateAt.END
        binding.tvAccountNumber.text = loanAccount.accountNo

        return convertView
    }
}