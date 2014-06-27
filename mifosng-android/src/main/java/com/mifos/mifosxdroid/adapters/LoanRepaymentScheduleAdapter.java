package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.accounts.loan.Period;
import com.mifos.utils.DateHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ishankhanna on 19/06/14.
 */
public class LoanRepaymentScheduleAdapter extends BaseAdapter{

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

        if(view == null) {
            view = layoutInflater.inflate(R.layout.row_loan_repayment_schedule,null);
            reusableViewHolder = new ReusableViewHolder(view);
            view.setTag(reusableViewHolder);
        } else {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
        }

        reusableViewHolder.tv_repaymentDueDate.setText(DateHelper.getDateAsString(periodList.get(i).getDueDate()));
        reusableViewHolder.tv_repaymentAmountDue.setText(String.valueOf(periodList.get(i).getTotalDueForPeriod()));
        reusableViewHolder.tv_repaymentAmountPaid.setText(String.valueOf(periodList.get(i).getTotalPaidForPeriod()));

        if(periodList.get(i).getComplete()) {
            reusableViewHolder.view_status_indicator.setBackgroundColor(context.getResources().getColor(R.color.light_green));
        } else if (!periodList.get(i).getComplete()
                && (periodList.get(i).getTotalOverdue()!=null && periodList.get(i).getTotalOverdue()>0)) {
            reusableViewHolder.view_status_indicator.setBackgroundColor(context.getResources().getColor(R.color.light_red));
        } else {
            reusableViewHolder.view_status_indicator.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
        }







        return view;
    }

    public static class ReusableViewHolder {


        @InjectView(R.id.view_status_indicator) View view_status_indicator;
        @InjectView(R.id.tv_repayment_due_date) TextView tv_repaymentDueDate;
        @InjectView(R.id.tv_repayment_amount_due) TextView tv_repaymentAmountDue;
        @InjectView(R.id.tv_repayment_amount_paid) TextView tv_repaymentAmountPaid;

        public ReusableViewHolder(View view) { ButterKnife.inject(this, view); }

    }

}
