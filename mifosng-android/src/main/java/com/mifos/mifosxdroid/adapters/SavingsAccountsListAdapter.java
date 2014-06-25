package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.accounts.savings.SavingsAccount;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

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
            reusableViewHolder.view_status_indicator.setBackgroundColor(context.getResources().getColor(R.color.light_green));
        } else if (savingsAccountList.get(i).getStatus().getApproved()) {
            reusableViewHolder.view_status_indicator.setBackgroundColor(context.getResources().getColor(R.color.light_yellow));
        } else if (savingsAccountList.get(i).getStatus().getSubmittedAndPendingApproval()) {
            reusableViewHolder.view_status_indicator.setBackgroundColor(context.getResources().getColor(R.color.blue));
        } else {
            reusableViewHolder.view_status_indicator.setBackgroundColor(context.getResources().getColor(R.color.black));
        }

        reusableViewHolder.tv_amount.setText(String.valueOf(savingsAccountList.get(i).getAccountBalance()));
        reusableViewHolder.tv_accountNumber.setText(savingsAccountList.get(i).getAccountNo());

        return view;
    }


    public static class ReusableViewHolder{

        @InjectView(R.id.tv_amount) TextView tv_amount;
        @InjectView(R.id.tv_accountNumber) TextView tv_accountNumber;
        @InjectView(R.id.view_status_indicator) View view_status_indicator;

        public ReusableViewHolder(View view) {
            ButterKnife.inject(this, view);
        }


    }
}
