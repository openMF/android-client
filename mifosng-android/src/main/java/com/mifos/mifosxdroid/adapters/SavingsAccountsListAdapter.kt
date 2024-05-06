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
import androidx.core.content.ContextCompat
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.databinding.RowAccountItemBinding
import java.text.DecimalFormat

/**
 * Created by ishankhanna on 23/05/14.
 */
class SavingsAccountsListAdapter(context: Context, savingsAccountList: List<SavingsAccount>) :
    BaseAdapter() {
    var context: Context
    private val savingsAccountList: List<SavingsAccount>
    private val layoutInflater: LayoutInflater

    init {
        layoutInflater = LayoutInflater.from(context)
        this.savingsAccountList = savingsAccountList
        this.context = context
    }

    override fun getCount(): Int {
        return savingsAccountList.size
    }

    override fun getItem(i: Int): SavingsAccount {
        return savingsAccountList[i]
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
        if (savingsAccountList[i].status?.active == true) {
            binding.viewStatusIndicator.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.savings_account_status_active
                )
            )
        } else if (savingsAccountList[i].status?.approved == true) {
            binding.viewStatusIndicator.setBackgroundColor(
                ContextCompat.getColor(context, R.color.status_approved)
            )
        } else if (savingsAccountList[i].status?.submittedAndPendingApproval == true) {
            binding.viewStatusIndicator.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.status_submitted_and_pending_approval
                )
            )
        } else {
            binding.viewStatusIndicator.setBackgroundColor(
                ContextCompat.getColor(context, R.color.status_closed)
            )
        }
        val accountBalance = savingsAccountList[i].accountBalance
        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.maximumFractionDigits = 2
        decimalFormat.maximumIntegerDigits = 10
        binding.tvAmount.text =
            (if (accountBalance == null) "0.00" else decimalFormat.format(accountBalance)).toString()
        binding.tvAccountNumber.text = savingsAccountList[i].accountNo
        return convertView
    }
}