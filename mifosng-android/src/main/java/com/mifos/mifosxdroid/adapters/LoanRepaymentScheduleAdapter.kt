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
import com.mifos.core.objects.accounts.loan.Period
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.databinding.RowLoanRepaymentScheduleBinding
import com.mifos.utils.DateHelper.getDateAsString

/**
 * Created by ishankhanna on 19/06/14.
 */
class LoanRepaymentScheduleAdapter(
    private val context: Context,
    private val periodList: List<Period>
) : BaseAdapter() {

    override fun getCount(): Int {
        return periodList.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        val viewHolder: ReusableViewHolder
        val binding: RowLoanRepaymentScheduleBinding

        if (view == null) {
            binding = RowLoanRepaymentScheduleBinding.inflate(
                LayoutInflater.from(context),
                viewGroup,
                false
            )
            viewHolder = ReusableViewHolder(binding)
            binding.root.tag = viewHolder
        } else {
            binding = RowLoanRepaymentScheduleBinding.bind(view)
            viewHolder = view.tag as ReusableViewHolder
        }

        val period = periodList[i]
        viewHolder.binding.tvRepaymentDueDate.text = period.dueDate?.let { getDateAsString(it) }
        viewHolder.binding.tvRepaymentAmountDue.text = period.totalDueForPeriod.toString()
        viewHolder.binding.tvRepaymentAmountPaid.text = period.totalPaidForPeriod.toString()

        when {
            period.complete != null && period.complete!! -> {
                viewHolder.binding.viewStatusIndicator.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.light_green
                    )
                )
            }

            period.totalOverdue != null && period.totalOverdue!! > 0 -> {
                viewHolder.binding.viewStatusIndicator.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.light_red
                    )
                )
            }

            else -> {
                viewHolder.binding.viewStatusIndicator.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.light_blue
                    )
                )
            }
        }

        return binding.root
    }

    class ReusableViewHolder(val binding: RowLoanRepaymentScheduleBinding)
}