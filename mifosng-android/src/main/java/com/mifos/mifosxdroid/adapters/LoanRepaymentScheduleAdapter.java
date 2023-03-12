/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.databinding.RowLoanRepaymentScheduleBinding;
import com.mifos.objects.accounts.loan.Period;
import com.mifos.utils.DateHelper;

import java.util.List;



/**
 * Created by ishankhanna on 19/06/14.
 */
public class LoanRepaymentScheduleAdapter extends BaseAdapter {

    List<Period> periodList;
    LayoutInflater layoutInflater;
    Context context;

    public LoanRepaymentScheduleAdapter(Context context, List<Period> periodList) {

        layoutInflater = LayoutInflater.from(context);
        this.periodList = periodList;
        this.context = context;

    }

    @Override
    public int getCount() {
        return periodList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ReusableViewHolder reusableViewHolder;
        RowLoanRepaymentScheduleBinding binding;

        if (view == null) {
            binding = RowLoanRepaymentScheduleBinding
                    .inflate(layoutInflater, viewGroup, false);
            reusableViewHolder = new ReusableViewHolder(binding);
            reusableViewHolder.view = binding.getRoot();
            reusableViewHolder.view.setTag(reusableViewHolder);
        } else {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
            reusableViewHolder.view = view;
        }

        reusableViewHolder.tv_repaymentDueDate.setText(DateHelper.getDateAsString(periodList.get
                (i).getDueDate()));
        reusableViewHolder.tv_repaymentAmountDue.setText(String.valueOf(periodList.get(i)
                .getTotalDueForPeriod()));
        reusableViewHolder.tv_repaymentAmountPaid.setText(String.valueOf(periodList.get(i)
                .getTotalPaidForPeriod()));

        if (periodList.get(i).getComplete()) {
            reusableViewHolder.view_status_indicator.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.light_green));

        } else if (!periodList.get(i).getComplete()
                && (periodList.get(i).getTotalOverdue() != null && periodList.get(i)
                .getTotalOverdue() > 0)) {
            reusableViewHolder.view_status_indicator.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.light_red));
        } else {
            reusableViewHolder.view_status_indicator.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.light_blue));

        }


        return reusableViewHolder.view;
    }

    public static class ReusableViewHolder {


        private View view;
        View view_status_indicator;
        TextView tv_repaymentDueDate;
        TextView tv_repaymentAmountDue;
        TextView tv_repaymentAmountPaid;

        public ReusableViewHolder(RowLoanRepaymentScheduleBinding binding) {
            view_status_indicator = binding.viewStatusIndicator;
            tv_repaymentDueDate = binding.tvRepaymentDueDate;
            tv_repaymentAmountDue = binding.tvRepaymentAmountDue;
            tv_repaymentAmountPaid = binding.tvRepaymentAmountPaid;
        }

    }

}
