/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.databinding.RowAccountItemBinding;
import com.mifos.objects.accounts.loan.LoanAccount;

import java.util.List;

/**
 * Created by ishankhanna on 01/03/14.
 */
public class LoanAccountsListAdapter extends BaseAdapter {

    Context context;
    private List<LoanAccount> loanAccountList;
    private LayoutInflater layoutInflater;

    public LoanAccountsListAdapter(Context context, List<LoanAccount> loanAccountList) {

        this.layoutInflater = LayoutInflater.from(context);
        this.loanAccountList = loanAccountList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return loanAccountList.size();
    }

    @Override
    public LoanAccount getItem(int i) {
        return loanAccountList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ReusableViewHolder reusableViewHolder;
        RowAccountItemBinding binding;

        if (view == null) {
            binding = RowAccountItemBinding
                    .inflate(layoutInflater, viewGroup, false);
            reusableViewHolder = new ReusableViewHolder(binding);
            reusableViewHolder.view = binding.getRoot();
            reusableViewHolder.view.setTag(reusableViewHolder);
        } else {
            reusableViewHolder = (ReusableViewHolder) view.getTag();
            reusableViewHolder.view = view;
        }

        if (loanAccountList.get(i).getStatus().getActive()) {

            reusableViewHolder.view_status_indicator.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.loan_status_disbursed));

        } else if (loanAccountList.get(i).getStatus().getWaitingForDisbursal()) {

            reusableViewHolder.view_status_indicator.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.status_approved));

        } else if (loanAccountList.get(i).getStatus().getPendingApproval()) {

            reusableViewHolder.view_status_indicator.setBackgroundColor(
                    ContextCompat.getColor(context,
                            R.color.status_submitted_and_pending_approval));

        } else if (loanAccountList.get(i).getStatus().getActive() && loanAccountList.get(i)
                .getInArrears()) {

            reusableViewHolder.view_status_indicator.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.red));

        } else {

            reusableViewHolder.view_status_indicator.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.status_closed));

        }

        //TODO : Change getProductName to Loan Amount Due
        reusableViewHolder.tv_amount.setText(loanAccountList.get(i).getProductName());
        reusableViewHolder.tv_amount.setEllipsize(TextUtils.TruncateAt.END);
        reusableViewHolder.tv_accountNumber.setText(loanAccountList.get(i).getAccountNo());

        return reusableViewHolder.view;
    }


    public static class ReusableViewHolder {

        private View view;
        TextView tv_amount;
        TextView tv_accountNumber;
        View view_status_indicator;

        public ReusableViewHolder(RowAccountItemBinding binding) {

            tv_amount = binding.tvAmount;
            tv_accountNumber = binding.tvAccountNumber;
            view_status_indicator = binding.viewStatusIndicator;

        }

    }
}
