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

import com.mifos.mifosxdroid.R;
import com.mifos.objects.accounts.savings.SavingsAccount;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

/**
 * Created by ishankhanna on 23/05/14.
 */
public class SavingsAccountsListAdapter extends BaseAdapter {

    private List<SavingsAccount> savingsAccountList;
    private LayoutInflater layoutInflater;
    Context context;

    public SavingsAccountsListAdapter(Context context, List<SavingsAccount> savingsAccountList) {

        this.layoutInflater = LayoutInflater.from(context);
        this.savingsAccountList = savingsAccountList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return savingsAccountList.size();
    }

    @Override
    public SavingsAccount getItem(int i) {
        return savingsAccountList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ReusableViewHolder reusableViewHolder;
        if(view==null) {

            view = layoutInflater.inflate(R.layout.row_account_item,null);
            reusableViewHolder = new ReusableViewHolder(view);
            view.setTag(reusableViewHolder);

        }else {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
        }

        if(savingsAccountList.get(i).getStatus().getActive()) {

            reusableViewHolder.view_status_indicator.setBackgroundColor(context.getResources().getColor(R.color.savings_account_status_active));

        } else if (savingsAccountList.get(i).getStatus().getApproved()) {

            reusableViewHolder.view_status_indicator.setBackgroundColor(context.getResources().getColor(R.color.status_approved));

        } else if (savingsAccountList.get(i).getStatus().getSubmittedAndPendingApproval()) {

            reusableViewHolder.view_status_indicator.setBackgroundColor(context.getResources().getColor(R.color.status_submitted_and_pending_approval));

        } else {
            reusableViewHolder.view_status_indicator.setBackgroundColor(context.getResources().getColor(R.color.status_closed));
        }

        Double accountBalance = savingsAccountList.get(i).getAccountBalance();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMaximumIntegerDigits(10);
        reusableViewHolder.tv_amount.setText(String.valueOf(accountBalance==null?"0.00": decimalFormat.format(accountBalance)));
        reusableViewHolder.tv_accountNumber.setText(savingsAccountList.get(i).getAccountNo());

        return view;
    }


    public static class ReusableViewHolder{

        @Bind(R.id.tv_amount) TextView tv_amount;
        @Bind(R.id.tv_accountNumber) TextView tv_accountNumber;
        @Bind(R.id.view_status_indicator) View view_status_indicator;

        public ReusableViewHolder(View view) {
            ButterKnife.bind(this, view);
        }


    }
}
